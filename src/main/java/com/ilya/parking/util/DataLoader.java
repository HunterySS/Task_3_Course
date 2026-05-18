package com.ilya.parking.util;

import java.util.List;
import com.ilya.parking.exception.ParkingException;
import com.ilya.parking.model.impl.Car;

public interface DataLoader {
    List<Car> loadCars() throws ParkingException;
    int loadTotalSlots() throws ParkingException;
}