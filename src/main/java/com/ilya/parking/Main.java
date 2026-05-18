package com.ilya.parking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.ilya.parking.exception.ParkingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ilya.parking.config.ParkingConfiguration;
import com.ilya.parking.model.impl.Car;
import com.ilya.parking.service.ParkingManager;
import com.ilya.parking.service.impl.ParkingManagerImpl;
import com.ilya.parking.thread.CarTask;
import com.ilya.parking.util.DataLoader;
import com.ilya.parking.util.impl.FileDataLoader;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== Parking Application Started ===");

        try {
            ParkingConfiguration config = initializeConfiguration();

            DataLoader dataLoader = new FileDataLoader(config.getDataFilePath());

            int totalSlots = dataLoader.loadTotalSlots();
            List<Car> cars = dataLoader.loadCars();

            logger.info("Total parking slots: {}", totalSlots);
            logger.info("Total cars to process: {}", cars.size());

            ParkingManager parkingManager = new ParkingManagerImpl(totalSlots);

            ExecutorService executorService = Executors.newFixedThreadPool(cars.size());
            List<Future<Boolean>> futures = new ArrayList<>();

            for (Car car : cars) {
                CarTask carTask = new CarTask(car, parkingManager);
                Future<Boolean> future = executorService.submit(carTask);
                futures.add(future);
            }

            executorService.shutdown();

            boolean allCompleted = executorService.awaitTermination(60, TimeUnit.SECONDS);
            if (!allCompleted) {
                logger.warn("Timeout waiting for tasks to complete, forcing shutdown");
                executorService.shutdownNow();
            }

            long successCount = 0;
            long failureCount = 0;

            for (Future<Boolean> future : futures) {
                try {
                    Boolean result = future.get();
                    if (Boolean.TRUE.equals(result)) {
                        successCount++;
                    } else {
                        failureCount++;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error getting task result", e);
                    failureCount++;
                }
            }

            logger.info("=== Parking Application Finished ===");
            logger.info("Statistics: Success={}, Failure={}, Total={}",
                    successCount, failureCount, cars.size());

            if (failureCount > 0) {
                logger.warn("Some cars were not served successfully");
            } else {
                logger.info("All cars were served successfully");
            }

            parkingManager.shutdown();

        } catch (ParkingException e) {
            logger.error("Fatal error in parking application: {}", e.getMessage(), e);
            System.exit(1);
        } catch (InterruptedException e) {
            logger.error("Application interrupted", e);
            Thread.currentThread().interrupt();
            System.exit(1);
        }
    }

    private static ParkingConfiguration initializeConfiguration() {
        String dataFilePath = System.getProperty("parking.data.file", "data/parking_data.txt");
        int totalSlots = Integer.parseInt(System.getProperty("parking.total.slots", "0"));
        int timeoutSeconds = Integer.parseInt(System.getProperty("parking.timeout.seconds", "5"));

        if (totalSlots <= 0) {
            logger.warn("Total slots not specified via system property, will read from file");
        }

        ParkingConfiguration config = ParkingConfiguration.getInstance(totalSlots, timeoutSeconds, dataFilePath);
        logger.info("Configuration initialized with timeout: {} seconds", timeoutSeconds);

        return config;
    }
}