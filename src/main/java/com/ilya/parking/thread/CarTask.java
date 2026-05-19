package com.ilya.parking.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ilya.parking.exception.ParkingException;
import com.ilya.parking.model.impl.Car;
import com.ilya.parking.service.ParkingManager;

public class CarTask implements Callable<Boolean> {
    private static final Logger logger = LogManager.getLogger(CarTask.class);

    private final Car car;
    private final ParkingManager parkingManager;

    public CarTask(Car car, ParkingManager parkingManager) {
        this.car = car;
        this.parkingManager = parkingManager;
        logger.info("CarTask created for car {}", car.getId());
    }

    @Override
    public Boolean call() {
        logger.info("Car {} started execution", car.getId());
        boolean isParked = false;

        try {
            car.enter();

            parkingManager.parkCar(car);

            int parkingDuration = car.getParkingDuration();
            logger.info("Car {} is parked for {} seconds", car.getId(), parkingDuration);
            TimeUnit.SECONDS.sleep(parkingDuration);

            parkingManager.leaveParking(car);

            logger.info("Car {} successfully completed parking", car.getId());
            isParked = true;

        } catch (ParkingException e) {
            logger.error("Car {} failed to park: {}", car.getId(), e.getMessage());
            car.leave();

        } catch (InterruptedException e) {
            logger.error("Car {} was interrupted: {}", car.getId(), e.getMessage());
            Thread.currentThread().interrupt();
        }
        return isParked;
    }
}