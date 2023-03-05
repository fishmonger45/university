package se325.flights.service.test;

import org.junit.After;
import org.junit.Before;
import se325.flights.dto.AirportDTO;
import se325.flights.dto.BookingRequestDTO;
import se325.flights.dto.FlightDTO;
import se325.flights.dto.UserDTO;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

/**
 * Code that's common to all Integration Tests.
 */
public abstract class BaseIntegrationTests {

    protected static final AirportDTO AUCKLAND_AIRPORT = new AirportDTO(
            1L,
            "Auckland International Airport",
            "AKL",
            -37.0082,
            174.7850,
            "Pacific/Auckland"
    );

    protected static final AirportDTO SYDNEY_AIRPORT = new AirportDTO(
            2L,
            "Sydney International Airport",
            "SYD",
            -33.9399,
            151.1753,
            "Australia/Sydney"
    );

    protected static final AirportDTO TOKYO_AIRPORT = new AirportDTO(
            3L,
            "Tokyo Narita International Airport",
            "NRT",
            35.7720,
            140.3929,
            "Asia/Tokyo"
    );

    protected static final AirportDTO SINGAPORE_AIRPORT = new AirportDTO(
            4L,
            "Singapore Changi International Airport",
            "SIN",
            1.3644,
            103.9915,
            "Asia/Singapore"
    );

    protected static final FlightDTO AUCKLAND_TO_SYDNEY_1 = new FlightDTO(
            1L,
            "NZ-103",
            ZonedDateTime.parse("2021-08-20T21:00:00+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME),
            AUCKLAND_AIRPORT,
            ZonedDateTime.parse("2021-08-21T00:20:00+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME),
            SYDNEY_AIRPORT,
            "787-9 Dreamliner"
    );

    protected static final FlightDTO AUCKLAND_TO_SYDNEY_2 = new FlightDTO(
            2L,
            "NZ-103",
            ZonedDateTime.parse("2021-08-21T21:00:00+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME),
            AUCKLAND_AIRPORT,
            ZonedDateTime.parse("2021-08-22T00:20:00+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME),
            SYDNEY_AIRPORT,
            "787-9 Dreamliner"
    );

    protected static final FlightDTO AUCKLAND_TO_SYDNEY_3 = new FlightDTO(
            3L,
            "NZ-103",
            ZonedDateTime.parse("2021-08-21T23:00:00+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME),
            AUCKLAND_AIRPORT,
            ZonedDateTime.parse("2021-08-22T02:20:00+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME),
            SYDNEY_AIRPORT,
            "787-9 Dreamliner"
    );

    protected static final FlightDTO SINGAPORE_TO_TOKYO = new FlightDTO(
            4L,
            "SQ-12",
            ZonedDateTime.parse("2021-08-30T01:25+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME),
            SINGAPORE_AIRPORT,
            ZonedDateTime.parse("2021-08-30T08:30+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME),
            TOKYO_AIRPORT,
            "777-200ER"
    );

    protected static final String WEB_SERVICE_URI = "http://localhost:10000/services";

    protected Client client;

    /**
     * Runs before each unit test to create the web service client, and send a test request which will force-re-init
     * the database. This ensures each unit test is starting with a clean playing field.
     */
    @Before
    public void setUp() {
        client = ClientBuilder.newClient();

        Response response = clientRequest("/test/reset-db").delete();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    /**
     * After each test, close the client to clear any leftover auth cookie data
     */
    @After
    public void tearDown() {
        client.close();
        client = null;
    }

    /**
     * Authorizes as user "Alice"
     */
    protected void logInAsAlice() {
        logInAs("Alice", "pa55word");
    }

    /**
     * Authorizes as user "Bob"
     */
    protected void logInAsBob() {
        logInAs("Bob", "12345");
    }

    /**
     * Authorizes as a user with the given username and password. Asserts that the given server response is a 204
     * No Content response, with an authToken cookie in the response header.
     *
     * @param username the username to authenticate
     * @param password the password to authenticate
     */
    protected void logInAs(String username, String password) {
        logInAs(username, password, client);
    }

    /**
     * Authorizes as a user with the given username and password. Asserts that the given server response is a 204
     * No Content response, with an authToken cookie in the response header.
     *
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @param client   the client to use to send the request
     */
    protected void logInAs(String username, String password, Client client) {
        UserDTO user = new UserDTO(username, password);
        try (Response response = clientRequest(client, "/users/login").post(Entity.json(user))) {

            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
            assertNotNull(response.getCookies().get("authToken"));
        }
    }

    /**
     * A shorthand for writing client.target(WEB_SERVICE_URI + path).request()...
     *
     * @param path the path to append to {@link #WEB_SERVICE_URI}
     * @return the {@link Invocation.Builder} used to make a web request
     */
    protected Invocation.Builder clientRequest(String path) {
        return clientRequest(client, path);
    }

    /**
     * A shorthand for writing client.target(WEB_SERVICE_URI + path).request()...
     *
     * @param client the client to invoke
     * @param path   the path to append to {@link #WEB_SERVICE_URI}
     * @return the {@link Invocation.Builder} used to make a web request
     */
    protected Invocation.Builder clientRequest(Client client, String path) {
        return client.target(WEB_SERVICE_URI + path).request();
    }

    /**
     * Sends a request to the server to make a booking on the given flight, for the given seats. Checks the server
     * Response to make sure it is a 201 Created response, with a valid Location. Returns the URI pointing to the
     * booking.
     *
     * @param flightId the id of the flight to book
     * @param seats    the seats to book
     */
    protected URI makeBooking(long flightId, String... seats) {
        BookingRequestDTO request = new BookingRequestDTO(flightId, seats);
        try (Response response = clientRequest("/bookings").post(Entity.json(request))) {

            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertNotNull(response.getLocation());
            assertTrue(response.getLocation().toString().contains("/bookings/"));
            return response.getLocation();
        }
    }
}
