package com.ilya.parking.state.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ilya.parking.model.impl.Car;
import com.ilya.parking.state.CarState;

public class LeavingState implements CarState {
    private static final Logger logger = LogManager.getLogger(LeavingState.class);

    @Override
    public void enter(Car car) {
        logger.warn("Car {} has already left", car.getId());
    }

    @Override
    public void park(Car car) {
        logger.error("Cannot park, car {} has already left", car.getId());
    }

    @Override
    public void leave(Car car) {
        logger.warn("Car {} has already left", car.getId());
    }
}