package org.ishaikh.taxibooking;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.restdocs.JUnitRestDocumentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Point;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Unit testing for the TaxiBooker REST resource Controller.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class TaxiBookerTest {

    /**
     * Create the MVC tester.
     */
    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
	    .apply(documentationConfiguration(this.restDocumentation))
	    .build();
    }

    /**
     * Test the TaxiBooker.
     * Perform a bunch of bookings.
     */
    @Test
    public void book() throws Exception {
	// Reset everything...
        this.mockMvc.perform(put("/api/reset"))
	    .andExpect(status().isOk())
	    .andDo(print())
	    .andDo(document("reset"));

	// Book the first ride. Should work since all are free.
        String rideCoordinatesJson = json(new RideCoordinates(new Point(1, 2), new Point(5, 6)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.car_id", is(1)))
	    .andExpect(jsonPath("$.total_time", is(11)))
	    .andDo(print())
	    .andDo(document("book"));

	// Book the second ride. Should work since only one is booked.
	rideCoordinatesJson = json(new RideCoordinates(new Point(3, 4), new Point(2, 2)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.car_id", is(2)))
	    .andExpect(jsonPath("$.total_time", is(10)));

	// Book the third ride. Should work since 2 are booked.
	rideCoordinatesJson = json(new RideCoordinates(new Point(1, 1), new Point(4, 4)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.car_id", is(3)))
	    .andExpect(jsonPath("$.total_time", is(8)));

	// Try to book the fourth ride. Should fail since all taxis are booked.
	rideCoordinatesJson = json(new RideCoordinates(new Point(1, 1), new Point(4, 4)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(content().string(""));

	// Move ahead by 7 time units.
	moveTimeUnits(7);

	// Try to book a ride; that should not be possible.
	rideCoordinatesJson = json(new RideCoordinates(new Point(1, 1), new Point(4, 4)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(content().string(""));

	// Move ahead by 1 time units.
	moveTimeUnits(1);

	// Book the next ride. Taxi 3 should be free now.
	rideCoordinatesJson = json(new RideCoordinates(new Point(3, 4), new Point(2, 2)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.car_id", is(3)))
	    .andExpect(jsonPath("$.total_time", is(4)));

	// Move ahead by 2 time units.
	moveTimeUnits(2);

	// Book the next ride. Taxi 2 should be free now.
	rideCoordinatesJson = json(new RideCoordinates(new Point(1, 1), new Point(4, 4)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.car_id", is(2)))
	    .andExpect(jsonPath("$.total_time", is(8)));

	// Try to book a ride; that should not be possible.
	rideCoordinatesJson = json(new RideCoordinates(new Point(5, 6), new Point(7, 7)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(content().string(""));

	// Move ahead by 1 time units.
	this.mockMvc.perform(put("/api/tick"))
	    .andExpect(status().isOk())
	    .andDo(print())
	    .andDo(document("tick"));

	// Book the next ride. Taxi 1 should be free now.
	rideCoordinatesJson = json(new RideCoordinates(new Point(5, 6), new Point(7, 7)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.car_id", is(1)))
	    .andExpect(jsonPath("$.total_time", is(3)));

	// Try to book a ride; that should not be possible. All are busy.
	rideCoordinatesJson = json(new RideCoordinates(new Point(5, 6), new Point(7, 7)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(content().string(""));

	// Reset everything...
        this.mockMvc.perform(put("/api/reset"))
	    .andExpect(status().isOk());

	// Book the next ride. All taxis should be free now. We should get back 1..
	rideCoordinatesJson = json(new RideCoordinates(new Point(5, 6), new Point(7, 7)));
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content(rideCoordinatesJson))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.car_id", is(1)))
	    .andExpect(jsonPath("$.total_time", is(14)));
    }

    /**
     * Helper method to move by the given time units.
     *
     * @param numberOfTicks 
     */
    public void moveTimeUnits(int numberOfTicks) throws Exception {
	for (int i = 0; i < numberOfTicks; i++) {
	    this.mockMvc.perform(put("/api/tick"))
		.andExpect(status().isOk());
	}
    }

    /**
     * Test a failed booking request.
     */
    @Test
    public void bookWithInvalidJson() throws Exception {
	// Try to book a ride with invalid body. Should fail with 400..
        this.mockMvc.perform(post("/api/book")
			     .contentType(contentType)
			     .content("invalid json"))
	    .andExpect(status().isBadRequest());
    }

    /**
     * Test a failed request with an incorrect URI.
     */
    @Test
    public void requestWithInvalidUri() throws Exception {
	// Try a request with an invalid URI. Should fail with 404..
        this.mockMvc.perform(post("/api/trybook")
			     .contentType(contentType)
			     .content("some json, irrelevant"))
	    .andExpect(status().isNotFound());
    }

    /**
     * Test a failed booking request with valid URI but invalid method.
     */
    @Test
    public void requestWithInvalidMethod() throws Exception {
	// Try a request with invalid method. Should fail with 405..
        this.mockMvc.perform(put("/api/book")
			     .contentType(contentType)
			     .content("some json; irrelevant"))
	    .andExpect(status().isMethodNotAllowed());
    }


    /**
     * Generate JSON from given object using the Jackson JSON HTTPMessageConverter created earlier.
     *
     * @param object input object that needs to be converted into JSON.
     * @result JSON representation of object.
     */
    protected String json(Object object) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(object,
						       MediaType.APPLICATION_JSON,
						       mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    /**
     * Create the Jackson JSON HTTPMessageConverter
     */
    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);
        assertNotNull("the JSON message converter must not be null",
		      this.mappingJackson2HttpMessageConverter);
    }

    // application/json content type.
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
						  MediaType.APPLICATION_JSON.getSubtype(),
						  Charset.forName("utf8"));

    // The main MVC tester.
    private MockMvc mockMvc;

    // Jackson JSON HTTP Message Converter.
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Rule
    public JUnitRestDocumentation restDocumentation =
	new JUnitRestDocumentation("target/generated-snippets");
}