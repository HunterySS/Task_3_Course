package com.ilya.parking.state;

import com.ilya.parking.model.impl.Car;

public interface CarState {
    void enter(Car car);
    void park(Car car);
    void leave(Car car);
}