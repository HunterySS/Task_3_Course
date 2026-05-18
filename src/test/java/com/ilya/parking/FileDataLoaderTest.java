package com.ilya.parking;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ilya.parking.exception.ParkingException;
import com.ilya.parking.model.impl.Car;
import com.ilya.parking.util.impl.FileDataLoader;

class FileDataLoaderTest {

    private static final String TEST_FILE = "test_parking_data.txt";
    private FileDataLoader dataLoader;

    @BeforeEach
    void setUp() {
        dataLoader = new FileDataLoader(TEST_FILE);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    void testLoadTotalSlots() throws IOException, ParkingException {
        String content = "5\nCAR001,3\nCAR002,4\n";
        Files.write(Paths.get(TEST_FILE), content.getBytes());

        int totalSlots = dataLoader.loadTotalSlots();

        assertEquals(5, totalSlots);
    }

    @Test
    void testLoadCars() throws IOException, ParkingException {
        String content = "3\nCAR001,5\nCAR002,3\nCAR003,7\n";
        Files.write(Paths.get(TEST_FILE), content.getBytes());

        List<Car> cars = dataLoader.loadCars();

        assertEquals(3, cars.size());
        assertEquals("CAR001", cars.get(0).getId());
        assertEquals(5, cars.get(0).getParkingDuration());
        assertEquals("CAR002", cars.get(1).getId());
        assertEquals(3, cars.get(1).getParkingDuration());
        assertEquals("CAR003", cars.get(2).getId());
        assertEquals(7, cars.get(2).getParkingDuration());
    }

    @Test
    void testLoadCarsWithEmptyLines() throws IOException, ParkingException {
        String content = "3\n\nCAR001,5\n\nCAR002,3\n\nCAR003,7\n\n";
        Files.write(Paths.get(TEST_FILE), content.getBytes());

        List<Car> cars = dataLoader.loadCars();

        assertEquals(3, cars.size());
    }

    @Test
    void testLoadCarsWithInvalidLine() throws IOException, ParkingException {
        String content = "3\nCAR001,5\nINVALID_LINE\nCAR003,7\n";
        Files.write(Paths.get(TEST_FILE), content.getBytes());

        List<Car> cars = dataLoader.loadCars();

        assertEquals(2, cars.size());
        assertEquals("CAR001", cars.get(0).getId());
        assertEquals("CAR003", cars.get(1).getId());
    }

    @Test
    void testFileNotFound() {
        FileDataLoader badLoader = new FileDataLoader("non_existent_file.txt");

        assertThrows(ParkingException.class, () -> {
            badLoader.loadTotalSlots();
        });
    }

    @Test
    void testEmptyFile() throws IOException {
        Files.write(Paths.get(TEST_FILE), "".getBytes());

        assertThrows(ParkingException.class, () -> {
            dataLoader.loadTotalSlots();
        });
    }

    @Test
    void testInvalidTotalSlotsFormat() throws IOException {
        Files.write(Paths.get(TEST_FILE), "invalid\nCAR001,5".getBytes());

        assertThrows(ParkingException.class, () -> {
            dataLoader.loadTotalSlots();
        });
    }
}