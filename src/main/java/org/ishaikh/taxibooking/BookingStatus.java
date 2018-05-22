package org.ishaikh.taxibooking;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a taxi booked for a customer.
 */
public class BookingStatus {
    /**
     * Constructor.
     *
     * @param carId       the taxi id
     * @param totalTime   time units to reach destination.
     */
    public BookingStatus(int carId, long totalTime) {
	this.carId = carId;
	this.totalTime = totalTime;
    }

    /**
     * Getter for the id.
     *
     * @return id of the taxi.
     */
    @JsonProperty("car_id")
    public int getCarId() {
	return carId;
    }

    /**
     * Getter for the total time
     *
     * @return total time for the ride.
     */
    @JsonProperty("total_time")
    public long getTotalTime() {
	return totalTime;
    }

    // Id of the taxi booked.
    private int carId;

    // Total time units needed to reach destination.
    private long totalTime;
}