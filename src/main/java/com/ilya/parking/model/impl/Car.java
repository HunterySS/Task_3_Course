package com.ilya.parking.model.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ilya.parking.model.Parkable;
import com.ilya.parking.state.CarState;
import com.ilya.parking.state.impl.WaitingState;

public class Car implements Parkable {
    private static final Logger logger = LogManager.getLogger(Car.class);

    private final String id;
    private final int parkingDuration;
    private CarState state;

    public Car(String id, int parkingDuration) {
        this.id = id;
        this.parkingDuration = parkingDuration;
        this.state = new WaitingState();
        logger.info("Car created: id={}, parkingDuration={} sec", id, parkingDuration);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getParkingDuration() {
        return parkingDuration;
    }

    public CarState getState() {
        return state;
    }

    public void setState(CarState state) {
        this.state = state;
        logger.debug("Car {} state changed to {}", id, state.getClass().getSimpleName());
    }

    public void enter() {
        state.enter(this);
    }

    public void park() {
        state.park(this);
    }

    public void leave() {
        state.leave(this);
    }
}