package com.ilya.parking;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ilya.parking.config.ParkingConfiguration;

class ParkingConfigurationTest {

    @Test
    void testSingletonCreation() {
        ParkingConfiguration config1 = ParkingConfiguration.getInstance(5, 10, "data/test.txt");
        ParkingConfiguration config2 = ParkingConfiguration.getInstance();

        assertSame(config1, config2);
        assertTrue(ParkingConfiguration.isCreated());
    }

    @Test
    void testSingletonSameInstance() {
        ParkingConfiguration config1 = ParkingConfiguration.getInstance(5, 10, "data/test.txt");
        ParkingConfiguration config2 = ParkingConfiguration.getInstance(10, 20, "data/other.txt");

        assertSame(config1, config2);
        assertEquals(5, config1.getTotalSlots());
        assertEquals(10, config1.getParkingTimeoutSeconds());
        assertEquals("data/test.txt", config1.getDataFilePath());
    }

    @Test
    void testGetValues() {
        ParkingConfiguration config = ParkingConfiguration.getInstance(3, 15, "data/parking.txt");

        assertEquals(3, config.getTotalSlots());
        assertEquals(15, config.getParkingTimeoutSeconds());
        assertEquals("data/parking.txt", config.getDataFilePath());
    }

    @Test
    void testGetInstanceWithoutInitThrowsException() {
        ParkingConfiguration instance = ParkingConfiguration.getInstance();
        assertNotNull(instance);
    }
}