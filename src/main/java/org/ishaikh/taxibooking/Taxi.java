package org.ishaikh.taxibooking;

import java.awt.Point;
import java.util.Objects;

/**
 * Class representing a taxi.
 */
public class Taxi {
    /**
     * Constructor.
     *
     * @param id                  the id assigned for this taxi.
     * @param moveTimeInternally  time movement to be managed by the class by
     *                            associating 1 time unit with 1 sec. Otherwise, time movement is
     *                            is managed externally using the tick methods.
     */
    public Taxi(int id, boolean moveTimeInternally) {
	this.id = id;
	this.moveTimeInternally = moveTimeInternally;
	this.availableLocation = new Point();
	this.destinationLocation = new Point();
    }

    /**
     * Reset ride & availability details.
     */
    public void reset() {
	available = true;
	availableLocation.setLocation(0, 0);
	destinationLocation.setLocation(0, 0);
	rideStartTime = -1;
	rideEndTime = -1;
	rideDuration = -1;
    }
	
    /**
     * Reset ride & availability details. Used to initialize taxi at the end of a ride.
     *
     * @param destLocation  this is the new value of the available location
     */
    public void reset(Point destLocation) {
	available = true;
	availableLocation.setLocation(destLocation);
	destinationLocation.setLocation(0, 0);
	rideStartTime = -1;
	rideEndTime = -1;
	rideDuration = -1;
    }
	
    /**
     * Move the clock one time unit. Also free the taxi if the ride duration has elapsed.
     * This has no impact when the taxi is available.
     * And is ignored when time movement is managed internally.
     */
    public void tick() {
	if (!available && !moveTimeInternally) {
	    rideDuration++;
	    if (rideStartTime + rideDuration >= rideEndTime) {
		reset(destinationLocation);
	    }
	}
    }

    /**
     * Is the taxi avaialable for a customer?
     * <p>
     * If movement of time is managed internally (using the system clock),
     * this method checks if the clock has moved past the required ride time and
     * hence the taxi is available.
     * Else, simply checks if the available flag is set or not.
     */
    public boolean isAvailable() {
	if (available) {
	    return true;
	} else {
	    if (moveTimeInternally) {
		if (rideEndTime <= System.currentTimeMillis()/1000) {
		    reset(destinationLocation);
		    return true;
		} else {
		    return false;
		}
	    } else {
		return false;
	    }
	}
    }

    /**
     * Sets this taxi off on a ride starting at its current location (or available location),
     * to the provide source location (or customer's pick-up location) and
     * then onto the provided destination location.
     * <p>
     * The ride start and end times are also set.
     *
     * @param sourceLocation        the pick-up location for the customer.
     * @param destLocation          the drop-off location for the customer.
     * @return                      the time units to reach the destination.
     *                              -1 in case of errors (e.g. taxi is not available)
     */
    public long ride(Point sourceLocation, Point destLocation) {
	if (isAvailable()) {
	    destinationLocation.setLocation(destLocation);
	    rideStartTime = System.currentTimeMillis()/1000;
	    rideDuration = 0;
	    rideEndTime =
		rideStartTime +
		distance(availableLocation, sourceLocation) +
		distance(sourceLocation, destinationLocation);
	    available = false;
	    return rideEndTime - rideStartTime;
	} else {
	    return -1;
	}
    }

    /**
     * Return the id of this taxi.
     *
     * @return the id of this taxi.
     */
    public int getId() {
	return id;
    }

    /**
     * Return the location of this taxi.
     *
     * @return the current location (also the available location)
     */
    public Point getAvailableLocation() {
	return availableLocation;
    }

    /**
     * Return String representation of object.
     *
     * @return String representation.
     */
    @Override
    public String toString() {
	return
	    "{taxi: {id: " + id + ", available: " + available +
	    ", availableLocation: [" + availableLocation.x + ", " + availableLocation.y +
	    "], destinationLocation: [" + destinationLocation.x + ", " + destinationLocation.y +
	    "], rideStartTime: " + rideStartTime + ", rideEndTime: " + rideEndTime +
	    ", rideDuration: " + rideDuration + ", moveTimeInternally: " + moveTimeInternally + "}}";
    }

    /**
     * Test for equality with another taxi.
     *
     * @param object The other object to test for equality.
     * @result true is equal false otherwise.
     */
    @Override
    public boolean equals(Object object) {
	if (this == object) return true;
	if (object == null || this.getClass() != object.getClass()) return false;
	Taxi other = (Taxi)object;
	return
	    this.id == other.id &&
	    this.available == other.available &&
	    Objects.equals(this.availableLocation, other.availableLocation) &&
	    Objects.equals(this.destinationLocation, other.destinationLocation) &&
	    this.rideStartTime == other.rideStartTime &&
	    this.rideEndTime == other.rideEndTime &&
	    this.moveTimeInternally == other.moveTimeInternally &&
	    this.rideDuration == other.rideDuration;
    }

    /**
     * Return the  hash code.
     *
     * @return hash code.
     */
    @Override
    public int hashCode() {
	return Objects.hash(id, available, availableLocation, destinationLocation,
			    rideStartTime, rideEndTime, moveTimeInternally, rideDuration);
    }

    /**
     * Helper function to determine the distance between the 2 given points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return   the distance between the 2 points.
     */
    public static int distance(Point p1, Point p2) {
	return (Math.abs(p1.x-p2.x) + Math.abs(p1.y-p2.y));
    }

    // Identifier for this taxi.
    private int id;

    // Is the taxi available for a customer or booked?
    private boolean available = true;

    // Location when available.
    private Point availableLocation;

    // Destination location when booked. Available location is the start location
    private Point destinationLocation;

    // Start & End times for a ride. Values are the system's time in seconds.
    // Used whether time is moved internally or externally.
    private long rideStartTime;
    private long rideEndTime;

    // Manage movement of time internally. A time unit is mapped to 1 second and the system
    // time is used to free the taxi whenever its availability is checked/queried.
    // If managed externally, then the tick method will move time and free the taxi appropriately.
    boolean moveTimeInternally = false;

    // How long has the current ride lasted? Initialized at the start of every ride.
    // Used when time is moved externally. Every tick() increments this value.
    // If system time is used, this is ignored.
    private long rideDuration;
}