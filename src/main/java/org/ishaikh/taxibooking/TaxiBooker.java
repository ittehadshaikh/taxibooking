package org.ishaikh.taxibooking;

import java.util.List;
import java.util.ArrayList;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Implements the taxi booking system.
 * Is also a REST Resource Controller for Spring.
 * A Singleton of this object is used by Spring.
 */
@RestController
@RequestMapping("/api")
public class TaxiBooker {
    /**
     * Constants.
     */
    public static final int NUMBER_OF_TAXIS = 3;

    /**
     * Constructor.
     */
    public TaxiBooker() {
	createTaxis(NUMBER_OF_TAXIS);
    }

    /**
     * Book a taxi.
     * Handler for a POST HTTP request for "/api/book".
     *
     * @param rideCoordinates  JSON request body is mapped to this class
     * @result the resulting BookingStatus is translated into JSON and sent in body of HTTP response.
     *         A NULL return traslates to a empty response body.
     */
    @PostMapping("/book")
    public BookingStatus book(@RequestBody RideCoordinates rideCoordinates) {
	BookingStatus result = null;
	
	synchronized(taxis) {
	    // Find a free taxi that is closest to the source location.
	    // If multiple taxis are equally close to the source location pick the smallest id.
	    Taxi closestTaxi = null;
	    int closestTaxiDistance = -1;
	    for (Taxi taxi : taxis) {
		if (taxi.isAvailable()) {
		    int taxiDistance = Taxi.distance(taxi.getAvailableLocation(),
						     rideCoordinates.getSource());
		    if (closestTaxiDistance == -1 || taxiDistance < closestTaxiDistance) {
			closestTaxiDistance = taxiDistance;
			closestTaxi = taxi;
		    }
		}
	    }

	    // If we found a free taxi, start its ride...
	    if (closestTaxi != null) {
		long totalTime = closestTaxi.ride(rideCoordinates.getSource(),
						  rideCoordinates.getDestination());
		result = new BookingStatus(closestTaxi.getId(), totalTime);
	    }
	}

	return result;
    }

    /**
     * Advance the clock by one time unit. Delegates to the Taxi.tick().
     * Handler for a PUT HTTP request for "/api/tick".
     */
    @PutMapping("/tick")
    public void tick() {
	synchronized(taxis) {
	    for (Taxi taxi : taxis) {
		taxi.tick();
	    }
	}
    }

    /**
     * Reset all taxis.. Delegates to the Taxi.reset().
     * Handler for a PUT HTTP request for "/api/reset".
     */
    @PutMapping("/reset")
    public void reset() {
	synchronized(taxis) {
	    for (Taxi taxi : taxis) {
		taxi.reset();
	    }
	}
    }

    /**
     * Provides details of a taxi. Useful for debugging from the client side.
     * Handler for a GET HTTP request for "/api/status/id".
     *
     * @param id the id of the taxi extracted from the URI and passed in.
     * @result a string describing the details of the taxi at this moment.
     */
    /**
     * Not meant for production use...
    @GetMapping("/status/{id}")
    public String status(@PathVariable int id) {
	for (Taxi taxi : taxis) {
	    if (taxi.getId() == id) {
		return taxi.toString();
	    }
	}
	return "Invalid id: " + id;
    }
    */

    /**
     * Create the requested number of taxis and populate the collection.
     *
     * @param numberOfTaxis    how many taxis to create
     */
    private void createTaxis(int numberOfTaxis) {
	for (int i = 0; i < numberOfTaxis; i++) {
	    taxis.add(new Taxi(i+1, false));
	}
    }

    // Collection of taxi's managed by this booker.
    List<Taxi> taxis = new ArrayList<Taxi>();    
}