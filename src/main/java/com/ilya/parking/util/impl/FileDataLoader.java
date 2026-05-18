package com.ilya.parking.util.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ilya.parking.exception.ParkingException;
import com.ilya.parking.model.impl.Car;
import com.ilya.parking.util.DataLoader;

public class FileDataLoader implements DataLoader {
    private static final Logger logger = LogManager.getLogger(FileDataLoader.class);

    private final String filePath;

    public FileDataLoader(String filePath) {
        this.filePath = filePath;
        logger.info("FileDataLoader created with file path: {}", filePath);
    }

    @Override
    public List<Car> loadCars() throws ParkingException {
        logger.info("Loading cars from file: {}", filePath);
        List<Car> cars = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line == null || line.strip().isBlank()) {
                throw new ParkingException("File is empty");
            }

            int totalSlots = Integer.parseInt(line.strip());
            logger.debug("Total slots from file: {}", totalSlots);

            int lineNumber = 2;
            while ((line = reader.readLine()) != null) {
                if (line.strip().isBlank()) {
                    lineNumber++;
                    continue;
                }

                String[] parts = line.strip().split(",");
                if (parts.length != 2) {
                    logger.warn("Invalid line format at line {}: {}", lineNumber, line);
                    lineNumber++;
                    continue;
                }

                try {
                    String carId = parts[0].strip();
                    int parkingDuration = Integer.parseInt(parts[1].strip());

                    if (parkingDuration <= 0) {
                        logger.warn("Invalid parking duration for car {}: {}, skipping", carId, parkingDuration);
                        lineNumber++;
                        continue;
                    }

                    Car car = new Car(carId, parkingDuration);
                    cars.add(car);
                    logger.debug("Loaded car: id={}, duration={}s", carId, parkingDuration);

                } catch (NumberFormatException e) {
                    logger.warn("Invalid number format at line {}: {}", lineNumber, line);
                }

                lineNumber++;
            }

            if (cars.isEmpty()) {
                throw new ParkingException("No valid cars found in file");
            }

            logger.info("Successfully loaded {} cars from file", cars.size());
            return cars;

        } catch (IOException e) {
            logger.error("Failed to read file: {}", filePath, e);
            throw new ParkingException("Failed to read data file: " + filePath, e);
        } catch (NumberFormatException e) {
            logger.error("Invalid total slots format in first line", e);
            throw new ParkingException("Invalid total slots format in file", e);
        }
    }

    @Override
    public int loadTotalSlots() throws ParkingException {
        logger.info("Loading total slots from file: {}", filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line == null || line.strip().isBlank()) {
                throw new ParkingException("File is empty");
            }

            int totalSlots = Integer.parseInt(line.strip());

            if (totalSlots <= 0) {
                throw new ParkingException("Total slots must be positive: " + totalSlots);
            }

            logger.info("Total slots loaded: {}", totalSlots);
            return totalSlots;

        } catch (IOException e) {
            logger.error("Failed to read file: {}", filePath, e);
            throw new ParkingException("Failed to read data file: " + filePath, e);
        } catch (NumberFormatException e) {
            logger.error("Invalid total slots format in first line", e);
            throw new ParkingException("Invalid total slots format in file", e);
        }
    }
}