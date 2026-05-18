package com.ilya.parking;

import static org.junit.jupiter.api.Assertions.*;

import com.ilya.parking.exception.ParkingException;
import org.junit.jupiter.api.Test;

class ParkingExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String message = "Parking is full";
        ParkingException exception = new ParkingException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testExceptionWithMessageAndCause() {
        String message = "File not found";
        Throwable cause = new RuntimeException("IO error");
        ParkingException exception = new ParkingException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionThrown() {
        assertThrows(ParkingException.class, () -> {
            throw new ParkingException("Test exception");
        });
    }
}