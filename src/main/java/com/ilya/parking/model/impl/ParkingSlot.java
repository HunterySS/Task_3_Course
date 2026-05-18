package com.ilya.parking.model.impl;

import com.ilya.parking.model.impl.Car;

public class ParkingSlot {
    private final int slotNumber;
    private Car currentCar;
    private boolean isOccupied;

    public ParkingSlot(int slotNumber) {
        this.slotNumber = slotNumber;
        this.isOccupied = false;
        this.currentCar = null;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public boolean occupy(Car car) {
        if (!isOccupied) {
            this.currentCar = car;
            this.isOccupied = true;
            return true;
        }
        return false;
    }

    public Car release() {
        if (isOccupied) {
            Car releasedCar = this.currentCar;
            this.currentCar = null;
            this.isOccupied = false;
            return releasedCar;
        }
        return null;
    }

    public Car getCurrentCar() {
        return currentCar;
    }
}