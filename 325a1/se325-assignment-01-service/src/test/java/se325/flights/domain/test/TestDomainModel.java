package se325.flights.domain.test;

import org.junit.*;
import se325.flights.domain.*;
import se325.flights.service.PersistenceManager;
import se325.flights.util.SecurityUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the flight booking domain model. These can be run directly from IntelliJ and don't require running any
 * particular Maven goal - but will be run automatically when Maven's test goal (and highter - package, verify,
 * install, ...) is executed.
 */
public class TestDomainModel {

    private static PersistenceManager PM;
    private EntityManager em;

    /**
     * Before we start, create the persistence manager to initialize the DB with test data.
     */
    @BeforeClass
    public static void initPersistenceManager() {
        PM = PersistenceManager.instance();
    }

    /**
     * Before we finish, make sure all connections to the DB are closed.
     */
    @AfterClass
    public static void closePersistenceManager() {
        PM.close();
    }

    /**
     * Before each test, reset the persistence manager to reset the dummy data in the DB. And, add some dummy
     * flight booking information. This information is added here in code, to make sure the
     * {@link Flight#makeBooking(User, String...)} method is tested.
     */
    @Before
    public void createEntityManager() throws BookingException {
        PM.reset();

        EntityManager em = PM.createEntityManager();

        // Book some flights
        em.getTransaction().begin();
        User alice = em.find(User.class, 1L);
        Flight aucklandToSydney = em.find(Flight.class, 1L);
        aucklandToSydney.makeBooking(alice, "1A", "1J");
        em.getTransaction().commit();

        em.getTransaction().begin();
        Flight singaporeToTokyo = em.find(Flight.class, 4L);
        User bob = em.find(User.class, 2L);
        singaporeToTokyo.makeBooking(alice, "11A", "11D");
        singaporeToTokyo.makeBooking(bob, "31A", "31B", "31C");
        em.getTransaction().commit();

        this.em = PM.createEntityManager();
    }

    /**
     * Close a particular JPA / Hibernate session after each test
     */
    @After
    public void resetDatabase() {
        em.close();
    }

    /**
     * Tests whether the user data added by the db-init.sql script is loaded and read successfully by our domain model
     */
    @Test
    public void testDummyUsers() {
        em.getTransaction().begin();

        List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
        assertEquals(2, users.size());

        assertEquals(1L, (long) users.get(0).getId());
        assertEquals("Alice", users.get(0).getUsername());
        assertTrue(SecurityUtils.matches("pa55word", users.get(0).getPassHash()));

        assertEquals(2L, (long) users.get(1).getId());
        assertEquals("Bob", users.get(1).getUsername());
        assertTrue(SecurityUtils.matches("12345", users.get(1).getPassHash()));

        em.getTransaction().commit();

    }

    /**
     * Tests whether the airport data added by the db-init.sql script is loaded and read successfully by our domain
     * model
     */
    @Test
    public void testDummyAirports() {
        em.getTransaction().begin();

        List<Airport> airports = em.createQuery("SELECT a FROM Airport a", Airport.class).getResultList();
        assertEquals(4, airports.size());

        assertEquals(1L, (long) airports.get(0).getId());
        assertEquals("Auckland International Airport", airports.get(0).getName());

        assertEquals(2L, (long) airports.get(1).getId());
        assertEquals("Sydney International Airport", airports.get(1).getName());

        assertEquals(3L, (long) airports.get(2).getId());
        assertEquals("Tokyo Narita International Airport", airports.get(2).getName());

        assertEquals(4L, (long) airports.get(3).getId());
        assertEquals("Singapore Changi International Airport", airports.get(3).getName());

        em.getTransaction().commit();
    }

    /**
     * Tests whether the aircraft data added by the db-init.sql script is loaded and read successfully by our domain
     * model
     */
    @Test
    public void testDummyAircraft() {
        em.getTransaction().begin();

        List<AircraftType> aircraft = em.createQuery("SELECT a FROM AircraftType a", AircraftType.class).getResultList();
        assertEquals(2, aircraft.size());

        assertEquals(1L, (long) aircraft.get(0).getId());
        assertEquals("787-9 Dreamliner", aircraft.get(0).getName());
        assertEquals(7, aircraft.get(0).getSeatingZones().size());

        assertEquals(2L, (long) aircraft.get(1).getId());
        assertEquals("777-200ER", aircraft.get(1).getName());
        assertEquals(7, aircraft.get(1).getSeatingZones().size());

        em.getTransaction().commit();
    }

    /**
     * Tests whether the flight data added by the db-init.sql script is loaded and read successfully by our domain model
     */
    @Test
    public void testDummyFlights() {

        em.getTransaction().begin();

        List<Flight> flights = em.createQuery("SELECT f FROM Flight f", Flight.class).getResultList();
        assertEquals(4, flights.size());

        assertEquals(1L, (long) flights.get(0).getId());
        assertEquals("Auckland International Airport", flights.get(0).getOrigin().getName());
        assertEquals("Sydney International Airport", flights.get(0).getDestination().getName());
        assertEquals("787-9 Dreamliner", flights.get(0).getAircraftType().getName());
        assertEquals(302, flights.get(0).getTotalNumSeats());

        assertEquals(2L, (long) flights.get(1).getId());
        assertEquals("Auckland International Airport", flights.get(1).getOrigin().getName());
        assertEquals("Sydney International Airport", flights.get(1).getDestination().getName());
        assertEquals("787-9 Dreamliner", flights.get(1).getAircraftType().getName());
        assertEquals(302, flights.get(1).getTotalNumSeats());

        assertEquals(3L, (long) flights.get(2).getId());
        assertEquals("Auckland International Airport", flights.get(2).getOrigin().getName());
        assertEquals("Sydney International Airport", flights.get(2).getDestination().getName());
        assertEquals("787-9 Dreamliner", flights.get(2).getAircraftType().getName());
        assertEquals(302, flights.get(2).getTotalNumSeats());

        assertEquals(4L, (long) flights.get(3).getId());
        assertEquals("Singapore Changi International Airport", flights.get(3).getOrigin().getName());
        assertEquals("Tokyo Narita International Airport", flights.get(3).getDestination().getName());
        assertEquals("777-200ER", flights.get(3).getAircraftType().getName());
        assertEquals(271, flights.get(3).getTotalNumSeats());

        em.getTransaction().commit();
    }

    /**
     * Tests whether our test booking data for a flight with a single booking, can be retrieved successfully
     */
    @Test
    public void testDummySingleBooking() {
        em.getTransaction().begin();

        Flight aucklandToSydney = em.find(Flight.class, 1L);
        assertEquals(1, aucklandToSydney.getBookings().size());
        FlightBooking aliceBooking = aucklandToSydney.getBookings().stream().findFirst().get();
        assertEquals("Alice", aliceBooking.getUser().getUsername());
        assertSame(aucklandToSydney, aliceBooking.getFlight());
        assertEquals(2, aliceBooking.getSeats().size());

        assertEquals(2, aucklandToSydney.getBookedSeats().size());
        assertEquals(300, aucklandToSydney.getNumSeatsRemaining());

        em.getTransaction().commit();
    }

    /**
     * Tests whether our test booking data for a flight with multiple bookings, can be retrieved successfully
     */
    @Test
    public void testDummyMultiBooking() {
        em.getTransaction().begin();

        Flight singaporeToTokyo = em.find(Flight.class, 4L);
        assertEquals(2, singaporeToTokyo.getBookings().size());

        FlightBooking aliceBooking = singaporeToTokyo.getBookings().stream()
                .filter(b -> b.getUser().getId() == 1L)
                .findFirst().get();
        assertEquals("Alice", aliceBooking.getUser().getUsername());
        assertSame(singaporeToTokyo, aliceBooking.getFlight());
        assertEquals(2, aliceBooking.getSeats().size());

        FlightBooking bobBooking = singaporeToTokyo.getBookings().stream()
                .filter(b -> b.getUser().getId() == 2L)
                .findFirst().get();
        assertEquals("Bob", bobBooking.getUser().getUsername());
        assertSame(singaporeToTokyo, bobBooking.getFlight());
        assertEquals(3, bobBooking.getSeats().size());

        assertEquals(5, singaporeToTokyo.getBookedSeats().size());
        assertEquals(266, singaporeToTokyo.getNumSeatsRemaining());

        em.getTransaction().commit();
    }

    /**
     * Tests that we can't make a booking for 0 seats
     */
    @Test
    public void testBookNoSeats() {
        em.getTransaction().begin();
        User bob = em.find(User.class, 2L);
        Flight aucklandToSydney = em.find(Flight.class, 1L);
        try {
            aucklandToSydney.makeBooking(bob);
            fail("Can't make a booking for 0 seats");

        } catch (BookingException e) {
            // Ensure state was not updated
            assertEquals(1, aucklandToSydney.getBookings().size());
            FlightBooking aliceBooking = aucklandToSydney.getBookings().stream().findFirst().get();
            assertEquals("Alice", aliceBooking.getUser().getUsername());
            assertSame(aucklandToSydney, aliceBooking.getFlight());
            assertEquals(2, aliceBooking.getSeats().size());

            assertEquals(2, aucklandToSydney.getBookedSeats().size());
            assertEquals(300, aucklandToSydney.getNumSeatsRemaining());
        } finally {
            em.getTransaction().commit();
        }
    }

    /**
     * Tests that we can't make a booking for an invalid seat (where the seat code is completely invalid and doesn't
     * even look like a real seat code)
     */
    @Test
    public void testBookInvalidSeats() {
        em.getTransaction().begin();
        User bob = em.find(User.class, 2L);
        Flight aucklandToSydney = em.find(Flight.class, 1L);
        try {
            aucklandToSydney.makeBooking(bob, "FooBar");
            fail("Shouldn't be allowed to make a booking for an invalid seat");

        } catch (BookingException e) {
            // Ensure state was not updated
            assertEquals(1, aucklandToSydney.getBookings().size());
            FlightBooking aliceBooking = aucklandToSydney.getBookings().stream().findFirst().get();
            assertEquals("Alice", aliceBooking.getUser().getUsername());
            assertSame(aucklandToSydney, aliceBooking.getFlight());
            assertEquals(2, aliceBooking.getSeats().size());

            assertEquals(2, aucklandToSydney.getBookedSeats().size());
            assertEquals(300, aucklandToSydney.getNumSeatsRemaining());
        } finally {
            em.getTransaction().commit();
        }
    }

    /**
     * Tests that we can't make a booking for an invalid seat (where the seat code is invalid but at least looks
     * legitimate)
     */
    @Test
    public void testBookInvalidButLegitLookingSeats() {
        em.getTransaction().begin();
        User bob = em.find(User.class, 2L);
        Flight aucklandToSydney = em.find(Flight.class, 1L);
        try {
            aucklandToSydney.makeBooking(bob, "500F");
            fail("Shouldn't be allowed to make a booking for an invalid seat");

        } catch (BookingException e) {
            // Ensure state was not updated
            assertEquals(1, aucklandToSydney.getBookings().size());
            FlightBooking aliceBooking = aucklandToSydney.getBookings().stream().findFirst().get();
            assertEquals("Alice", aliceBooking.getUser().getUsername());
            assertSame(aucklandToSydney, aliceBooking.getFlight());
            assertEquals(2, aliceBooking.getSeats().size());

            assertEquals(2, aucklandToSydney.getBookedSeats().size());
            assertEquals(300, aucklandToSydney.getNumSeatsRemaining());
        } finally {
            em.getTransaction().commit();
        }
    }

    /**
     * Tests that we can't make a booking with any invalid seat codes, even when others are valid.
     */
    @Test
    public void testBookValidAndInvalidSeats() {
        em.getTransaction().begin();
        User bob = em.find(User.class, 2L);
        Flight aucklandToSydney = em.find(Flight.class, 1L);
        try {
            aucklandToSydney.makeBooking(bob, "45A", "FooBar");
            fail("Shouldn't be allowed to make a booking for a mix of valid and invalid seats");

        } catch (BookingException e) {
            // Ensure state was not updated
            assertEquals(1, aucklandToSydney.getBookings().size());
            FlightBooking aliceBooking = aucklandToSydney.getBookings().stream().findFirst().get();
            assertEquals("Alice", aliceBooking.getUser().getUsername());
            assertSame(aucklandToSydney, aliceBooking.getFlight());
            assertEquals(2, aliceBooking.getSeats().size());

            assertEquals(2, aucklandToSydney.getBookedSeats().size());
            assertEquals(300, aucklandToSydney.getNumSeatsRemaining());
        } finally {
            em.getTransaction().commit();
        }
    }

    /**
     * Tests that we can't make a booking for seats which are already booked.
     */
    @Test
    public void testBookSameSeats() {
        em.getTransaction().begin();
        User bob = em.find(User.class, 2L);
        Flight aucklandToSydney = em.find(Flight.class, 1L);
        try {
            aucklandToSydney.makeBooking(bob, "1J");
            fail("Booking for the same seat should not be successful.");

        } catch (BookingException e) {
            // Ensure state was not updated
            assertEquals(1, aucklandToSydney.getBookings().size());
            FlightBooking aliceBooking = aucklandToSydney.getBookings().stream().findFirst().get();
            assertEquals("Alice", aliceBooking.getUser().getUsername());
            assertSame(aucklandToSydney, aliceBooking.getFlight());
            assertEquals(2, aliceBooking.getSeats().size());

            assertEquals(2, aucklandToSydney.getBookedSeats().size());
            assertEquals(300, aucklandToSydney.getNumSeatsRemaining());
        } finally {
            em.getTransaction().commit();
        }
    }

    /**
     * Tests that we can't make a booking which includes any seat that's already booked, even when the booking
     * request also includes unbooked seats
     */
    @Test
    public void testBookSomeSameAndSomeDifferentSeats() {
        em.getTransaction().begin();
        User bob = em.find(User.class, 2L);
        Flight aucklandToSydney = em.find(Flight.class, 1L);
        try {
            aucklandToSydney.makeBooking(bob, "45A", "1J");
            fail("Booking for the same seat should not be successful.");

        } catch (BookingException e) {
            // Ensure state was not updated
            assertEquals(1, aucklandToSydney.getBookings().size());
            FlightBooking aliceBooking = aucklandToSydney.getBookings().stream().findFirst().get();
            assertEquals("Alice", aliceBooking.getUser().getUsername());
            assertSame(aucklandToSydney, aliceBooking.getFlight());
            assertEquals(2, aliceBooking.getSeats().size());

            assertEquals(2, aucklandToSydney.getBookedSeats().size());
            assertEquals(300, aucklandToSydney.getNumSeatsRemaining());
        } finally {
            em.getTransaction().commit();
        }
    }
}
