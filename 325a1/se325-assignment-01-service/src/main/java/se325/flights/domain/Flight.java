package se325.flights.domain;

import se325.flights.CabinClass;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a flight. Flights have a name - usually a code provided by the airline, e.g. "NZ-103" for a particular
 * flight from A            EntityManager em = PersistenceManager.instance().createEntityManager();uckland to Sydney. Flights also have an origin and destination {@link Airport}, a departure and arrival
 * time in UTC for the purposes of coordinating between international timezones ({@link ZonedDateTime}), and take place
 * on a particular {@link AircraftType}.
 * <p>
 * Multiple {@link User}s can book a flight using its {@link #makeBooking(User, String...)} method. Valid booking
 * requests result in {@link FlightBooking} instances being created. The price of a particular booking depends on the
 * {@link CabinClass} of the seats being booked (determined by the {@link AircraftType} of a flight), and the
 * price-per-cabin-class for a particular cabin class on a flight.
 */
@Entity
public class Flight {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;
    @OneToMany(mappedBy = "flight", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<FlightBooking> bookings = new HashSet<>();
    @ManyToOne
    private AircraftType aircraftType;
    @ManyToOne
    private Airport origin;
    @ManyToOne
    private Airport destination;
    @ElementCollection
    private Set<SeatPricing> seatPricings = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(ZonedDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public ZonedDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(ZonedDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Airport getOrigin() {
        return origin;
    }

    public Airport getDestination() {
        return destination;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public Set<FlightBooking> getBookings() {
        return Collections.unmodifiableSet(bookings);
    }

    /**
     * Gets a list of all {@link Seat}s which have already been booked on this flight. Accumulates all the booked seats
     * on all of this flight's {@link FlightBooking}s.
     *
     * @return a list of {@link Seat} instances
     */
    public List<Seat> getBookedSeats() {

        ArrayList<Seat> seats = new ArrayList<>();
        for (FlightBooking booking : bookings) {
            seats.addAll(booking.getSeats());
        }
        return Collections.unmodifiableList(seats);
    }

    /**
     * Gets the total number of seats on this flight. Obtained directly from the {@link AircraftType#getTotalNumSeats()}
     * method.
     *
     * @return the total number of seats on this flight
     */
    public int getTotalNumSeats() {
        return this.aircraftType.getTotalNumSeats();
    }

    /**
     * Gets the number of seats remaining (i.e. unbooked seats) on this flight.
     *
     * @return the number of unbooked seats on this flight
     */
    public int getNumSeatsRemaining() {
        return getTotalNumSeats() - getBookedSeats().size();
    }

    /**
     * Gets the price of seats of the given cabin class on this flight. If the given cabin class isn't found for this
     * flight, then the seat price for those seats will be $0
     *
     * @param cabinClass the cabin class to check
     * @return the price for seats of that cabin class on this flight
     */
    private int getPriceFor(CabinClass cabinClass) {
        return seatPricings.stream()
                .filter(p -> p.getCabinClass().equals(cabinClass))
                .findFirst()
                .map(SeatPricing::getPrice)
                .orElse(0);
    }

    /**
     * Gets all seat pricings for this flight. If there's not an entry in this map for a particular {@link CabinClass},
     * then it is assumed that there are no seats of that class on this flight.
     *
     * @return a map, mapping cabin classes to their prices per seat on this flight.
     */
    public Map<CabinClass, Integer> getSeatPricings() {
        Map<CabinClass, Integer> pricings = new HashMap<>();
        for (SeatPricing p : this.seatPricings) {
            pricings.put(p.getCabinClass(), p.getPrice());
        }
        return pricings;
    }

    /**
     * Attempts to make a booking for the given seats for the given user on this flight.
     *
     * @param user      the user who's making the booking
     * @param seatCodes the seat codes to book
     * @throws BookingException if the booking is for 0 seats, or any of the requested seats are already booked, or
     *                          if any of the requested seats are invalid.
     */
    public FlightBooking makeBooking(User user, List<String> seatCodes) throws BookingException {
        // empty booking
        ensureNotEmptyBooking(seatCodes);

        // requested seats already booked
        if (seatCodes.stream().anyMatch(sc -> this.getBookedSeats().stream().anyMatch(s -> s.getSeatCode().equals(sc)))) {
            throw new BookingException("Seat is already booked for flight");
        }

        // no seating zones on plane
        if (this.aircraftType.getSeatingZones().size() == 0) {
            throw new BookingException("Empty seating zones");
        }

        // requested seats are invalid
        if (!seatCodes.stream().allMatch(c -> this.aircraftType.getSeatingZones().stream().anyMatch(z -> z.isValidSeatCode(c)))) {
            throw new BookingException("Seat code is invalid");
        }

        // book the seat
        FlightBooking fb = new FlightBooking(user, this);
        for(String c: seatCodes) {
            fb.getSeats().add(new Seat(c, this.getPriceFor(this.aircraftType.getCabinClass(c))));
        }

        this.bookings.add(fb);
        return fb;
    }

    public FlightBooking makeBooking(User user, String... seatCodes) throws BookingException {
        return makeBooking(user, List.of(seatCodes));
    }

    /**
     * Ensures that an incoming booking request isn't for 0 / null seats.
     *
     * @param seatCodes the booking request
     * @throws BookingException if the booking request is null or empty
     */
    private void ensureNotEmptyBooking(List<String> seatCodes) throws BookingException {
        if (seatCodes == null || seatCodes.size() == 0) {
            throw new BookingException("Cannot make a booking for 0 seats");
        }
    }

    /**
     * Ensures that an incoming booking request isn't for seats which are already booked.
     *
     * @param seatCodes the booking request
     * @throws BookingException if the booking request is for seats which are already booked
     */
    private void ensureUnbooked(List<String> seatCodes) throws BookingException {
        List<String> bookedSeatCodes = getBookedSeats().stream().map(Seat::getSeatCode).collect(Collectors.toList());
        boolean alreadyBooked = seatCodes.stream().anyMatch(bookedSeatCodes::contains);
        if (alreadyBooked) {
            throw new BookingException("One ore more seats are already booked");
        }
    }

    public void removeBooking(FlightBooking booking) {
        this.bookings.remove(booking);
        booking.setFlight(null);
    }
}