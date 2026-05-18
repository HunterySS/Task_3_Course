package com.ilya.parking.state.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ilya.parking.model.impl.Car;
import com.ilya.parking.state.CarState;

public class ParkedState implements CarState {
    private static final Logger logger = LogManager.getLogger(ParkedState.class);

    @Override
    public void enter(Car car) {
        logger.warn("Car {} is already parked", car.getId());
    }

    @Override
    public void park(Car car) {
        logger.warn("Car {} is already in parked state", car.getId());
    }

    @Override
    public void leave(Car car) {
        car.setState(new LeavingState());
        logger.info("Car {} is leaving the parking", car.getId());
    }
}