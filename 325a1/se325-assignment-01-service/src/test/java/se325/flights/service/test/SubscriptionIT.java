package se325.flights.service.test;

import org.junit.Ignore;
import org.junit.Test;
import se325.flights.CabinClass;
import se325.flights.dto.AvailableSeatsSubscriptionDTO;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests functionality related to subscriptions (async HTTP requests).
 */
@Ignore
public class SubscriptionIT extends BaseIntegrationTests {

    /**
     * Tests that we can subscribe to be notified when seats are available on a flight in a particular cabin class, and
     * that if those seats are already available at the time we make the request, we'll be immediately notified.
     */
    @Ignore
    @Test
    public void testSubscription_SeatsAlreadyAvailable() {
        logInAsAlice();
        AvailableSeatsSubscriptionDTO request = new AvailableSeatsSubscriptionDTO(1, CabinClass.Business, 5);
        try (Response response = clientRequest("/flights/subscribe").post(Entity.json(request))) {
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that we can subscribe to be notified when seats are available on a flight, and that if those seats are
     * already available at the time we make the request, we'll be immediately notified.
     */
    @Ignore
    @Test
    public void testSubscription_SeatsAlreadyAvailable_AnyCabinClass() {
        logInAsAlice();
        AvailableSeatsSubscriptionDTO request = new AvailableSeatsSubscriptionDTO(1, null, 5);
        try (Response response = clientRequest("/flights/subscribe").post(Entity.json(request))) {
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that we will get a 401 Unauthorized error when we try to subscribe to seat notifications without
     * logging in.
     */
    @Ignore
    @Test
    public void testSubscriptionFail_NotAuthenticated() {
        AvailableSeatsSubscriptionDTO request = new AvailableSeatsSubscriptionDTO(1, CabinClass.Business, 5);
        try (Response response = clientRequest("/flights/subscribe").post(Entity.json(request))) {
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that we will get a 404 Not Found error when we try to subscribe to seat notifications from a flight which
     * doesn't exist
     */
    @Ignore
    @Test
    public void testSubscriptionFail_InvalidFlight() {
        logInAsAlice();
        AvailableSeatsSubscriptionDTO request = new AvailableSeatsSubscriptionDTO(999, CabinClass.Business, 5);
        try (Response response = clientRequest("/flights/subscribe").post(Entity.json(request))) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that, if one user requests to subscribe to available seat notifications when there are insufficient seats
     * available, they'll be notified once those seats are available.
     */
    @Ignore
    @Test
    public void testSubscription_NotifiedWhenSeatsAvailable() throws ExecutionException, InterruptedException, TimeoutException {

        // First, have Alice book all the seats
        logInAsAlice();
        URI bookingUri = bookAllSeatsInBusinessClass();

        // Now, have Bob subscribe to seat notifications
        Client bobClient = ClientBuilder.newClient();
        logInAs("Bob", "12345", bobClient);
        AvailableSeatsSubscriptionDTO request = new AvailableSeatsSubscriptionDTO(1, CabinClass.Business, 5);
        Future<Response> future = clientRequest(bobClient, "/flights/subscribe").async().post(Entity.json(request));

        // Bob's subscription shouldn't be fulfilled just yet. Wait for a couple of seconds to verify this.
        try {
            future.get(2, TimeUnit.SECONDS);
            fail("future.get() should have timed out, not succeeded.");
        } catch (TimeoutException e) {

            // Now, have Alice cancel her booking
            try (Response response = client.target(bookingUri).request().delete()) {
                assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
            }

            // Now, wait for Bob's subscription notification response. It should come through. If it hasn't come through
            // within five seconds, we'll assume it's not going to, and fail the test.
            Response subResponse = future.get(5, TimeUnit.SECONDS);
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), subResponse.getStatus());

        }
    }

    /**
     * Tests that, if one user requests to subscribe to available seat notifications when there are insufficient seats
     * available in any cabin class, they'll be notified once those seats are available.
     */
    @Ignore
    @Test
    public void testSubscription_NotifiedWhenSeatsAvailable_AnyCabinClass() throws ExecutionException, InterruptedException, TimeoutException {

        // First, have Alice book all the seats
        logInAsAlice();
        URI bookingUri = bookAllSeatsInBusinessClass();

        // Now, have Bob subscribe to seat notifications
        Client bobClient = ClientBuilder.newClient();
        logInAs("Bob", "12345", bobClient);
        AvailableSeatsSubscriptionDTO request = new AvailableSeatsSubscriptionDTO(1, null, 300);
        Future<Response> future = clientRequest(bobClient, "/flights/subscribe").async().post(Entity.json(request));

        // Bob's subscription shouldn't be fulfilled just yet. Wait for a couple of seconds to verify this.
        try {
            future.get(2, TimeUnit.SECONDS);
            fail("future.get() should have timed out, not succeeded.");
        } catch (TimeoutException e) {

            // Now, have Alice cancel her booking
            try (Response response = client.target(bookingUri).request().delete()) {
                assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
            }

            // Now, wait for Bob's subscription notification response. It should come through. If it hasn't come through
            // within five seconds, we'll assume it's not going to, and fail the test.
            Response subResponse = future.get(5, TimeUnit.SECONDS);
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), subResponse.getStatus());

        }
    }

    /**
     * Books all seats in business class on flight 1.
     *
     * @return the URI pointing to the booking
     */
    private URI bookAllSeatsInBusinessClass() {
        return makeBooking(1, "1A", "1J", "1K", "2A", "2J", "2K",
                "3A", "3J", "3K", "4A", "4J", "4K", "5A", "5J", "5K", "6A", "6J", "6K");
    }
}
