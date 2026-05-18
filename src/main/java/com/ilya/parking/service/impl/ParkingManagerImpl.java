package com.ilya.parking.service.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ilya.parking.exception.ParkingException;
import com.ilya.parking.model.impl.ParkingSlot;
import com.ilya.parking.model.impl.Car;
import com.ilya.parking.service.ParkingManager;

public class ParkingManagerImpl implements ParkingManager {
    private static final Logger logger = LogManager.getLogger(ParkingManagerImpl.class);

    private final ParkingSlot[] slots;
    private final int totalSlots;
    private final AtomicInteger availableSlots;
    private final ReentrantLock lock;
    private final Condition slotAvailable;
    private final AtomicBoolean isRunning;

    public ParkingManagerImpl(int totalSlots) {
        this.totalSlots = totalSlots;
        this.slots = new ParkingSlot[totalSlots];
        this.availableSlots = new AtomicInteger(totalSlots);
        this.lock = new ReentrantLock(true);
        this.slotAvailable = lock.newCondition();
        this.isRunning = new AtomicBoolean(true);

        for (int i = 0; i < totalSlots; i++) {
            slots[i] = new ParkingSlot(i + 1);
        }

        logger.info("ParkingManager initialized with {} slots", totalSlots);
    }

    @Override
    public void parkCar(Car car) throws InterruptedException, ParkingException {
        if (!isRunning.get()) {
            throw new ParkingException("Parking manager is shut down");
        }

        logger.info("Car {} is trying to park", car.getId());

        lock.lock();
        try {
            while (availableSlots.get() == 0 && isRunning.get()) {
                logger.info("No available slots. Car {} is waiting", car.getId());
                if (!slotAvailable.await(5, TimeUnit.SECONDS)) {
                    logger.error("Car {} timed out while waiting for a slot", car.getId());
                    throw new ParkingException("Timeout waiting for parking slot");
                }
            }

            if (!isRunning.get()) {
                throw new ParkingException("Parking manager is shut down during waiting");
            }

            int slotIndex = findFreeSlot();
            if (slotIndex == -1) {
                throw new ParkingException("No free slot found but availableSlots > 0");
            }

            boolean occupied = slots[slotIndex].occupy(car);
            if (occupied) {
                availableSlots.decrementAndGet();
                car.park();
                logger.info("Car {} occupied slot {}", car.getId(), slots[slotIndex].getSlotNumber());
                logger.info("Available slots: {}", availableSlots.get());
            } else {
                throw new ParkingException("Failed to occupy slot " + slots[slotIndex].getSlotNumber());
            }

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void leaveParking(Car car) {
        lock.lock();
        try {
            for (ParkingSlot slot : slots) {
                if (slot.isOccupied() && slot.getCurrentCar() != null
                        && slot.getCurrentCar().getId().equals(car.getId())) {
                    Car releasedCar = slot.release();
                    if (releasedCar != null) {
                        availableSlots.incrementAndGet();
                        car.leave();
                        logger.info("Car {} released slot {}", car.getId(), slot.getSlotNumber());
                        logger.info("Available slots: {}", availableSlots.get());
                        slotAvailable.signal();
                        break;
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getAvailableSlots() {
        return availableSlots.get();
    }

    @Override
    public int getTotalSlots() {
        return totalSlots;
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            isRunning.set(false);
            slotAvailable.signalAll();
            logger.info("ParkingManager shutdown complete");
        } finally {
            lock.unlock();
        }
    }

    private int findFreeSlot() {
        for (int i = 0; i < slots.length; i++) {
            if (!slots[i].isOccupied()) {
                return i;
            }
        }
        return -1;
    }
}