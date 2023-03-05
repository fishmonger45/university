package se325.flights.service.test;

import org.junit.Test;
import se325.flights.dto.BookingInfoDTO;
import se325.flights.dto.BookingRequestDTO;
import se325.flights.dto.FlightBookingDTO;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests functionality related to flight bookings.
 */
public class BookingIT extends BaseIntegrationTests {

    /**
     * Tests that an authenticated user can make a booking on a flight.
     */
    @Test
    public void testMakeBooking() {
        logInAsAlice();
        makeBooking(1, "1A", "1J");
    }

    /**
     * Tests that an unauthenticated user can't make a booking. A 401 Unauthorized response should be returned.
     */
    @Test
    public void testMakeBookingFail_NotAuthenticated() {
        BookingRequestDTO request = new BookingRequestDTO(1, "1A", "1J");
        try (Response response = clientRequest("/bookings").post(Entity.json(request))) {

            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that an authenticated user can't make a booking for invalid seats. A 404 or 409 should be returned
     * (either is ok).
     */
    @Test
    public void testMakeBookingFail_InvalidSeats() {
        logInAsAlice();
        BookingRequestDTO request = new BookingRequestDTO(1, "250A");
        try (Response response = clientRequest("/bookings").post(Entity.json(request))) {

            assertTrue(Response.Status.NOT_FOUND.getStatusCode() == response.getStatus() ||
                    Response.Status.CONFLICT.getStatusCode() == response.getStatus());
        }
    }

    /**
     * Tests that an authenticated user can't make a booking for seats which have already been booked. A 404 or 409
     * should be returned (either is ok).
     */
    @Test
    public void testMakeBookingFail_AlreadyBooked() {
        logInAsAlice();
        makeBooking(1, "1A", "1J");

        BookingRequestDTO request = new BookingRequestDTO(1, "1J", "1K");
        try (Response response = clientRequest("/bookings").post(Entity.json(request))) {

            assertTrue(Response.Status.NOT_FOUND.getStatusCode() == response.getStatus() ||
                    Response.Status.CONFLICT.getStatusCode() == response.getStatus());
        }
    }

    /**
     * Tests that a booking made by an authenticated user can be retrieved. The retrieved booking should contain the
     * booked seat code info, ordered by row then by letter.
     */
    @Test
    public void testRetrieveBooking() {
        logInAsAlice();
        URI bookingLink
                = makeBooking(1, "1A", "1J");

        try (Response response = client.target(bookingLink
        ).request().get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            FlightBookingDTO booking = response.readEntity(FlightBookingDTO.class);

            assertEquals(2, booking.getBookedSeats().size());
            assertEquals("1A", booking.getBookedSeats().get(0));
            assertEquals("1J", booking.getBookedSeats().get(1));
            assertEquals(AUCKLAND_TO_SYDNEY_1, booking.getFlight());
            assertEquals(2000, booking.getTotalCost());
            assertNotNull(booking.getId());
        }
    }

    /**
     * Tests that a request by an authenticated user for a booking with a nonexistent id will return a 404 response.
     */
    @Test
    public void testRetrieveBookingFail_InvalidBooking() {
        logInAsAlice();

        try (Response response = clientRequest("/bookings/1").get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a request by an unauthenticated user to retrieve a booking will return a 401 response.
     */
    @Test
    public void testRetrieveBookingFail_NotAuthenticated() {
        logInAsAlice();
        URI bookingLink
                = makeBooking(1, "1A", "1J");

        // Clear the auth cookie
        client.close();
        client = ClientBuilder.newClient();

        try (Response response = client.target(bookingLink
        ).request().get()) {
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a request by an authenticated user for a booking other than their own booking will return a
     * 404 response.
     */
    @Test
    public void testRetrieveBookingFail_OtherUsersBooking() {
        logInAsAlice();
        URI bookingLink
                = makeBooking(1, "1A", "1J");

        logInAsBob();

        try (Response response = client.target(bookingLink
        ).request().get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that we can get the bookings for an authenticated user with no current bookings. In this case, a 200 OK
     * response should be returned, with an empty booking list.
     */
    @Test
    public void testRetrieveAllBookings_EmptyList() {
        logInAsAlice();
        try (Response response = clientRequest("/bookings").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightBookingDTO> bookings = response.readEntity(new GenericType<>() {
            });
            assertEquals(0, bookings.size());
        }
    }

    /**
     * Tests that an authenticated user correctly gets a list with a single booking if they've only made one booking
     */
    @Test
    public void testRetrieveAllBookings_SingleBooking() {
        logInAsAlice();
        makeBooking(1, "1A", "1J");

        try (Response response = clientRequest("/bookings").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightBookingDTO> bookings = response.readEntity(new GenericType<>() {
            });
            assertEquals(1, bookings.size());

            FlightBookingDTO booking = bookings.get(0);

            assertEquals(2, booking.getBookedSeats().size());
            assertEquals("1A", booking.getBookedSeats().get(0));
            assertEquals("1J", booking.getBookedSeats().get(1));
            assertEquals(AUCKLAND_TO_SYDNEY_1, booking.getFlight());
            assertEquals(2000, booking.getTotalCost());
            assertNotNull(booking.getId());
        }
    }

    /**
     * Tests that an authenticated user correctly gets a list with multiple bookings, in the correct order (ordered
     * by departure time, ascending).
     */
    @Test
    public void testRetrieveAllBookings_MultiBooking() {
        logInAsAlice();
        makeBooking(2, "1A", "1J");
        makeBooking(1, "2A", "2J", "2K");

        try (Response response = clientRequest("/bookings").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightBookingDTO> bookings = response.readEntity(new GenericType<>() {
            });
            assertEquals(2, bookings.size());

            FlightBookingDTO booking = bookings.get(0);
            assertEquals(3, booking.getBookedSeats().size());
            assertEquals("2A", booking.getBookedSeats().get(0));
            assertEquals("2J", booking.getBookedSeats().get(1));
            assertEquals("2K", booking.getBookedSeats().get(2));
            assertEquals(AUCKLAND_TO_SYDNEY_1, booking.getFlight());
            assertEquals(3000, booking.getTotalCost());
            assertNotNull(booking.getId());

            booking = bookings.get(1);
            assertEquals(2, booking.getBookedSeats().size());
            assertEquals("1A", booking.getBookedSeats().get(0));
            assertEquals("1J", booking.getBookedSeats().get(1));
            assertEquals(AUCKLAND_TO_SYDNEY_2, booking.getFlight());
            assertEquals(2000, booking.getTotalCost());
            assertNotNull(booking.getId());
        }
    }

    /**
     * Tests that only the currently authenticated user's bookings will be returned in the booking list.
     */
    @Test
    public void testRetrieveAllBookings_OnlyOwnBookings() {

        logInAsAlice();
        makeBooking(2, "1A", "1J");

        logInAsBob();
        makeBooking(1, "2A", "2J", "2K");

        logInAsAlice();

        try (Response response = clientRequest("/bookings").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightBookingDTO> bookings = response.readEntity(new GenericType<>() {
            });
            assertEquals(1, bookings.size());

            FlightBookingDTO booking = bookings.get(0);
            assertEquals(2, booking.getBookedSeats().size());
            assertEquals("1A", booking.getBookedSeats().get(0));
            assertEquals("1J", booking.getBookedSeats().get(1));
            assertEquals(AUCKLAND_TO_SYDNEY_2, booking.getFlight());
            assertEquals(2000, booking.getTotalCost());
            assertNotNull(booking.getId());
        }
    }

    /**
     * Tests that we can't get bookings when a user is not authenticated. In this case, a 401 Unauthorized response
     * should be returned.
     */
    @Test
    public void testRetrieveAllBookingsFail_NotAuthenticated() {
        try (Response response = clientRequest("/bookings").get()) {
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a booking can be cancelled by the user who booked it. A 204 response should be returned. Also makes
     * sure that the booking is actually deleted from the system.
     */
    @Test
    public void testCancelBooking() {
        logInAsAlice();
        URI bookingLink = makeBooking(1, "1A", "1J");

        try (Response response = client.target(bookingLink).request().delete()) {
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }

        try (Response response = client.target(bookingLink).request().get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that getting a flight's booking info after a booking is made, will show that booking's seats as taken.
     */
    @Test
    public void testGetBookingInfoAfterBookingMade() {
        logInAsAlice();
        makeBooking(1, "1A", "1J");

        try (Response response = clientRequest("/flights/1/booking-info").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            BookingInfoDTO info = response.readEntity(BookingInfoDTO.class);

            assertEquals(2, info.getBookedSeats().size());
            assertTrue(info.getBookedSeats().contains("1A"));
            assertTrue(info.getBookedSeats().contains("1J"));
        }
    }
}
