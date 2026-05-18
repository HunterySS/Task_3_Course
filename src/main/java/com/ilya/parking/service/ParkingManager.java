package com.ilya.parking.service;

import com.ilya.parking.exception.ParkingException;
import com.ilya.parking.model.impl.Car;

public interface ParkingManager {
    void parkCar(Car car) throws InterruptedException, ParkingException;
    void leaveParking(Car car);
    int getAvailableSlots();
    int getTotalSlots();
    void shutdown();
}