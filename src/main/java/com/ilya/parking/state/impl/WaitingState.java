package com.ilya.parking.state.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ilya.parking.model.impl.Car;
import com.ilya.parking.state.CarState;

public class WaitingState implements CarState {
    private static final Logger logger = LogManager.getLogger(WaitingState.class);

    @Override
    public void enter(Car car) {
        logger.info("Car {} is waiting to enter the parking", car.getId());
    }

    @Override
    public void park(Car car) {
        car.setState(new ParkedState());
        logger.info("Car {} successfully parked", car.getId());
    }

    @Override
    public void leave(Car car) {
        car.setState(new LeavingState());
        logger.info("Car {} left without parking", car.getId());
    }
}