package com.ilya.parking.config;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParkingConfiguration {
    private static final Logger logger = LogManager.getLogger(ParkingConfiguration.class);

    private static final AtomicReference<ParkingConfiguration> instance = new AtomicReference<>(null);
    private static final AtomicBoolean isCreated = new AtomicBoolean(false);

    private final int totalSlots;
    private final int parkingTimeoutSeconds;
    private final String dataFilePath;

    private ParkingConfiguration(int totalSlots, int parkingTimeoutSeconds, String dataFilePath) {
        this.totalSlots = totalSlots;
        this.parkingTimeoutSeconds = parkingTimeoutSeconds;
        this.dataFilePath = dataFilePath;
        logger.info("ParkingConfiguration created: totalSlots={}, timeout={}s, file={}",
                totalSlots, parkingTimeoutSeconds, dataFilePath);
    }

    public static ParkingConfiguration getInstance(int totalSlots, int parkingTimeoutSeconds, String dataFilePath) {
        ParkingConfiguration existingInstance = instance.get();
        if (existingInstance == null) {
            ParkingConfiguration newInstance = new ParkingConfiguration(totalSlots, parkingTimeoutSeconds, dataFilePath);
            if (instance.compareAndSet(null, newInstance)) {
                isCreated.set(true);
                logger.info("ParkingConfiguration instance created successfully");
                return newInstance;
            } else {
                logger.warn("ParkingConfiguration already created by another thread");
                return instance.get();
            }
        }
        return existingInstance;
    }

    public static ParkingConfiguration getInstance() {
        ParkingConfiguration existingInstance = instance.get();
        if (existingInstance == null) {
            logger.error("ParkingConfiguration not initialized. Call getInstance with parameters first.");
            throw new IllegalStateException("ParkingConfiguration not initialized");
        }
        return existingInstance;
    }

    public static boolean isCreated() {
        return isCreated.get();
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public int getParkingTimeoutSeconds() {
        return parkingTimeoutSeconds;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }
}