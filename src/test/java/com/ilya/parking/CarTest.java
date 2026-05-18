package com.ilya.parking;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ilya.parking.model.impl.Car;
import com.ilya.parking.state.impl.LeavingState;
import com.ilya.parking.state.impl.ParkedState;
import com.ilya.parking.state.impl.WaitingState;

class CarTest {

    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car("TEST-001", 5);
    }

    @Test
    void testCarCreation() {
        assertEquals("TEST-001", car.getId());
        assertEquals(5, car.getParkingDuration());
        assertTrue(car.getState() instanceof WaitingState);
    }

    @Test
    void testCarStateChange() {
        car.park();
        assertTrue(car.getState() instanceof ParkedState);
    }

    @Test
    void testCarEnter() {
        car.enter();
        assertTrue(car.getState() instanceof WaitingState);
    }

    @Test
    void testCarLeave() {
        car.leave();
        assertTrue(car.getState() instanceof LeavingState);
    }
}