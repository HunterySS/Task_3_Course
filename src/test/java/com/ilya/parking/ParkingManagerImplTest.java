package com.ilya.parking;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ilya.parking.exception.ParkingException;
import com.ilya.parking.model.impl.Car;
import com.ilya.parking.service.ParkingManager;
import com.ilya.parking.service.impl.ParkingManagerImpl;

class ParkingManagerImplTest {

    private ParkingManager parkingManager;

    @BeforeEach
    void setUp() {
        parkingManager = new ParkingManagerImpl(2);
    }

    @AfterEach
    void tearDown() {
        parkingManager.shutdown();
    }

    @Test
    void testInitialAvailableSlots() {
        assertEquals(2, parkingManager.getAvailableSlots());
        assertEquals(2, parkingManager.getTotalSlots());
    }

    @Test
    void testParkCar() throws InterruptedException, ParkingException {
        Car car = new Car("CAR-001", 1);

        parkingManager.parkCar(car);

        assertEquals(1, parkingManager.getAvailableSlots());
    }

    @Test
    void testParkAndLeave() throws InterruptedException, ParkingException {
        Car car = new Car("CAR-001", 1);

        parkingManager.parkCar(car);
        assertEquals(1, parkingManager.getAvailableSlots());

        parkingManager.leaveParking(car);

        assertEquals(2, parkingManager.getAvailableSlots());
    }

    @Test
    void testParkingFull() throws InterruptedException, ParkingException {
        Car car1 = new Car("CAR-001", 1);
        Car car2 = new Car("CAR-002", 1);
        Car car3 = new Car("CAR-003", 1);

        parkingManager.parkCar(car1);
        parkingManager.parkCar(car2);

        assertEquals(0, parkingManager.getAvailableSlots());

        assertThrows(ParkingException.class, () -> {
            parkingManager.parkCar(car3);
        });
    }

    @Test
    void testMultipleParkAndLeave() throws InterruptedException, ParkingException {
        Car car1 = new Car("CAR-001", 1);
        Car car2 = new Car("CAR-002", 1);

        parkingManager.parkCar(car1);
        parkingManager.parkCar(car2);

        assertEquals(0, parkingManager.getAvailableSlots());

        parkingManager.leaveParking(car1);

        assertEquals(1, parkingManager.getAvailableSlots());

        Car car3 = new Car("CAR-003", 1);
        parkingManager.parkCar(car3);

        assertEquals(0, parkingManager.getAvailableSlots());
    }
}