package se325.flights.service.test;

import org.junit.Ignore;
import org.junit.Test;
import se325.flights.dto.BookingInfoDTO;
import se325.flights.dto.FlightDTO;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests functionality related to retrieving flight information.
 */
public class FlightIT extends BaseIntegrationTests {

    /**
     * Tests that the correct flight info is returned when we search for flights by airport name. The
     * response should be a 200 OK with a list of valid {@link se325.flights.dto.FlightDTO} objects. The list should
     * contain one flight.
     */
    @Test
    public void testFlightSearch_AirportsByName() {
        try (Response response = clientRequest("/flights?origin=Singapore&destination=Tokyo").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            assertEquals(1, flights.size());
            FlightDTO flight = flights.get(0);

            assertEquals(SINGAPORE_TO_TOKYO, flight);
        }
    }

    /**
     * Tests that the correct flight info is returned when we search for flights by airport code. The response should
     * be a 200 OK with a list of valid {@link se325.flights.dto.FlightDTO} objects. The list should contain one flight.
     */
    @Test
    public void testFlightSearch_AirportsByCode() {
        try (Response response = clientRequest("/flights?origin=SIN&destination=NRT").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            assertEquals(1, flights.size());
            FlightDTO flight = flights.get(0);

            assertEquals(SINGAPORE_TO_TOKYO, flight);
        }
    }

    /**
     * Tests that the flight origin and destination search (by name) is case-insensitive.
     */
    @Test
    public void testFlightSearch_AirportsByName_CaseInsensitive() {
        try (Response response = clientRequest("/flights?origin=sinGApOrE&destination=ToKYo").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            assertEquals(1, flights.size());
            FlightDTO flight = flights.get(0);

            assertEquals(SINGAPORE_TO_TOKYO, flight);
        }
    }

    /**
     * Tests that the flight origin and destination search (by code) is case-insensitive.
     */
    @Test
    public void testFlightSearch_AirportsByCode_CaseInsensitive() {
        try (Response response = clientRequest("/flights?origin=sin&destination=Nrt").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            assertEquals(1, flights.size());
            FlightDTO flight = flights.get(0);

            assertEquals(SINGAPORE_TO_TOKYO, flight);
        }
    }

    /**
     * Tests that a flight search can get multiple results, in the correct order (sorted by departure time)
     */
    @Test
    public void testFlightSearch_MultipleResults() {
        try (Response response = clientRequest("/flights?origin=Auckland&destination=SYD").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            assertEquals(3, flights.size());
            assertEquals(AUCKLAND_TO_SYDNEY_1, flights.get(0));
            assertEquals(AUCKLAND_TO_SYDNEY_2, flights.get(1));
            assertEquals(AUCKLAND_TO_SYDNEY_3, flights.get(2));
        }
    }

    /**
     * Tests that a flight search can get results from multile matching origins, in the correct order.
     * "n" as an origin matches both AucklaNd and SiNgapore.
     * "n" as a destination matches both SydNey and Tokyo (Nrt).
     */
    @Test
    public void testFlightSearch_MultipleMatchingOrigins() {
        try (Response response = clientRequest("/flights?origin=n&destination=n").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            assertEquals(4, flights.size());
            assertEquals(AUCKLAND_TO_SYDNEY_1, flights.get(0));
            assertEquals(AUCKLAND_TO_SYDNEY_2, flights.get(1));
            assertEquals(AUCKLAND_TO_SYDNEY_3, flights.get(2));
            assertEquals(SINGAPORE_TO_TOKYO, flights.get(3));
        }
    }

    /**
     * Tests that a flight search with no results still returns a 200 OK response, just with an empty list.
     */
    @Test
    public void testFlightSearch_NoResults() {
        try (Response response = clientRequest("/flights?origin=Auckland&destination=Singapore").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            assertEquals(0, flights.size());
        }
    }

    /**
     * Tests that a flight search with a missing origin will return a 400 response
     */
    @Test
    public void testFlightSearchFail_MissingOrigin() {
        try (Response response = clientRequest("/flights?destination=Tokyo").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a flight search with a missing destination will return a 400 response
     */
    @Test
    public void testFlightSearchWithMissingDestination() {
        try (Response response = clientRequest("/flights?origin=AKL").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a flight search with a departure time query will return results on that day in the origin's timezone
     */
    @Test
    public void testFlightSearchWithDepartureTime_SingleResult() {
        try (Response response = clientRequest(
                "/flights?origin=AKL&destination=SYD&departureDate=2021-08-21").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            for(FlightDTO f : flights) {
                System.out.println(f.getDepartureTime());
            }
            assertEquals(1, flights.size());
            assertEquals(AUCKLAND_TO_SYDNEY_1, flights.get(0));
        }
    }

    /**
     * Tests that a flight search with a departure time query will return results on that day in the origin's timezone
     */
    @Test
    public void testFlightSearchWithDepartureTime_MultipleResults() {
        try (Response response = clientRequest(
                "/flights?origin=AKL&destination=SYD&departureDate=2021-08-22").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            assertEquals(2, flights.size());
            assertEquals(AUCKLAND_TO_SYDNEY_2, flights.get(0));
            assertEquals(AUCKLAND_TO_SYDNEY_3, flights.get(1));
        }
    }

    /**
     * Tests that a flight search with an invalid departure time query will return a 400 Bad Request error.
     */
    @Test
    public void testFlightSearchWithDepartureTimeFail_InvalidDate() {
        try (Response response = clientRequest(
                "/flights?origin=AKL&destination=SYD&departureDate=invalid").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that we can get booking info for a flight which exists
     */
    @Ignore
    @Test
    public void testRetrieveBookingInfo() {
        try (Response response = clientRequest("/flights/1/booking-info").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            BookingInfoDTO info = response.readEntity(BookingInfoDTO.class);

            assertEquals("787-9 Dreamliner", info.getAircraftType().getName());
            assertEquals(0, info.getBookedSeats().size());
        }
    }

    /**
     * Tests that we get a 404 error for requesting booking info for a nonexistent flight
     */
    @Test
    public void testRetrieveBookingInfoFail_NotFound() {
        try (Response response = clientRequest("/flights/999/booking-info").get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }
}
