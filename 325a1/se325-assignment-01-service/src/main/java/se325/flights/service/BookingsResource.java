package se325.flights.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.flights.domain.BookingException;
import se325.flights.domain.Flight;
import se325.flights.domain.FlightBooking;
import se325.flights.domain.User;
import se325.flights.domain.mappers.BookingMapper;
import se325.flights.dto.BookingRequestDTO;
import se325.flights.dto.FlightBookingDTO;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A JAX-RS Resource class intended to contain methods with making and cancelling flight bookings, in
 * addition to retrieving information about existing flight bookings.
 */

@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingsResource.class);

    @POST
    public Response book(@CookieParam("authToken") String authToken, BookingRequestDTO dtoBookingRequest) {
        Response response = null;
        if (authToken == null) {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            EntityManager em = PersistenceManager.instance().createEntityManager();
            try {
                em.getTransaction().begin();
                TypedQuery<User> userTypedQuery = em.createQuery(
                        "SELECT u FROM User u WHERE u.authToken = :authToken", User.class)
                        .setParameter("authToken", authToken);
                User domainUser = userTypedQuery.getSingleResult();
                Flight domainFlight = em.find(Flight.class, dtoBookingRequest.getFlightId(), LockModeType.PESSIMISTIC_WRITE);
                FlightBooking flightBooking = domainFlight.makeBooking(domainUser, dtoBookingRequest.getRequestedSeats());
                /// XXX: is the uri scheme that we want?
                em.getTransaction().commit();
                response = Response.created(URI.create("/bookings/" + flightBooking.getId())).build();

            } catch (NoResultException e) {
                LOGGER.debug(e.toString());
                response = Response.status(Response.Status.UNAUTHORIZED).build();
            } catch (RollbackException e) {
                LOGGER.debug(e.toString());
                response = Response.status(Response.Status.CONFLICT).build();
            } catch (BookingException e) {
                LOGGER.debug(e.toString());
                em.getTransaction().rollback();
                // is this correct?
                response = Response.status(Response.Status.CONFLICT).build();
            } finally {
                em.close();
            }
        }

        return response;
    }

    @GET
    public Response getBooking(@CookieParam("authToken") String authToken) {
        Response response;
        if (authToken == null) {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            EntityManager em = PersistenceManager.instance().createEntityManager();
            try {
                TypedQuery<User> userTypedQuery = em.createQuery(
                        "SELECT u FROM User u WHERE u.authToken = :authToken", User.class)
                        .setParameter("authToken", authToken);
                User domainUser = userTypedQuery.getSingleResult();
                // sort and convert
                List<FlightBooking> domainFlightBookings = domainUser.getBookings().stream().collect(Collectors.toList());
                domainFlightBookings.sort(Comparator.comparing((FlightBooking fb) -> fb.getFlight().getDepartureTime()));
                List<FlightBookingDTO> dtoFlightBookings = domainFlightBookings.stream().map(BookingMapper::toDTO).collect(Collectors.toList());
                response = Response.ok(dtoFlightBookings).build();
            } catch (NoResultException e) {
                LOGGER.debug(e.toString());
                response = Response.status(Response.Status.UNAUTHORIZED).build();
            } finally {
                em.close();
            }
        }
        return response;
    }

    @GET
    @Path("{id}")
    public Response getBooking(@CookieParam("authToken") String authToken, @PathParam("id") String id) {
        Response response;
        if (authToken == null) {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            EntityManager em = PersistenceManager.instance().createEntityManager();
            try {
                // XXX: product join here, mitigate later
                TypedQuery<FlightBooking> flightBookingTypedQuery = em.createQuery(
                        "SELECT b FROM User u JOIN u.bookings b WHERE u.authToken = :authToken AND b.id = :id", FlightBooking.class)
                        .setParameter("authToken", authToken)
                        .setParameter("id", Long.valueOf(id));
                FlightBooking flightBooking = flightBookingTypedQuery.getSingleResult();
                FlightBookingDTO dtoFlightBooking = BookingMapper.toDTO(flightBooking);
                response = Response.ok(dtoFlightBooking).build();
            } catch (NoResultException e) {
                LOGGER.debug(e.toString());
                response = Response.status(Response.Status.NOT_FOUND).build();
            } finally {
                em.close();
            }
        }
        return response;
    }

    @DELETE
    @Path("{id}")
    public Response deleteBooking(@CookieParam("authToken") String authToken, @PathParam("id") String id) {
        Response response;
        if (authToken == null) {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            EntityManager em = PersistenceManager.instance().createEntityManager();
            try {
                // XXX: product join here, mitigate later
                em.getTransaction().begin();
                TypedQuery<FlightBooking> flightBookingTypedQuery = em.createQuery(
                        "SELECT b FROM User u JOIN u.bookings b WHERE u.authToken = :authToken AND b.id = :id", FlightBooking.class)
                        .setParameter("authToken", authToken)
                        .setParameter("id", Long.valueOf(id));
                FlightBooking domainFlightBooking = flightBookingTypedQuery.getSingleResult();
                em.remove(domainFlightBooking);
                response = Response.status(Response.Status.NO_CONTENT).build();
                em.getTransaction().commit();

            } catch (NoResultException e) {
                LOGGER.debug(e.toString());
                response = Response.status(Response.Status.NOT_FOUND).build();
            } finally {
                em.close();
            }
        }
        return response;
    }
}
