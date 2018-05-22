package org.ishaikh.taxibooking;

import java.awt.Point;

/**
 * Contains the source and destination co-ordinates for a ride
 */
public class RideCoordinates {
    /**
     * Constructor.
     *
     * @param source         co-ordinates of the source (from) of the ride
     * @param destination    co-ordinates of the destination (to) of the ride
     */
    public RideCoordinates(Point source, Point destination) {
	this.source = source;
	this.destination = destination;
    }

    /**
     * Getter for source.
     *
     * @return source co-ordinates.
     */
    public Point getSource() {
	return source;
    }

    /**
     * Getter for destination.
     *
     * @return destination co-ordinates.
     */
    public Point getDestination() {
	return destination;
    }

    @Override
    public String toString() {
	return
	    "{ride-coordinates: {source: {x: " + source.x + ", y: " + source.y +
	    "}}, {destination: {x: " + destination.x + ", y: " + destination.y + "}}}";
    }

    // Source co-ordinates.
    private Point source;

    // Destination co-ordinates
    private Point destination;
}