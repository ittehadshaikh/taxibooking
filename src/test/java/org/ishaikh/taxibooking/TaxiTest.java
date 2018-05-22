package org.ishaikh.taxibooking;

import java.awt.Point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

public class TaxiTest {
    @Test
    public void testTaxi() {
	// Create a new taxi and ensure that it is available and located at (0, 0)
	Taxi taxi = new Taxi(1, false);
	assertTrue(taxi.isAvailable());
	assertEquals(new Point(0, 0), taxi.getAvailableLocation());

	// Set it off on a ride of 3 time uints.
	// Should become available at the end of the time units.
	// Also verify its location at the end of the ride.
	taxi.ride(new Point(1, 1), new Point(1, 2));
	assertFalse(taxi.isAvailable());	
	taxi.tick();
	taxi.tick();
	assertFalse(taxi.isAvailable());	
	taxi.tick();
	assertTrue(taxi.isAvailable());	
	assertEquals(new Point(1, 2), taxi.getAvailableLocation());

	// Reset the taxi and ensure that it is available and located at (0, 0)
	taxi.reset();
	assertTrue(taxi.isAvailable());	
	assertEquals(new Point(0, 0), taxi.getAvailableLocation());
    }
}