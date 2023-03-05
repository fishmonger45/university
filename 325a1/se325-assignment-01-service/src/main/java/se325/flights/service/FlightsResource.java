package se325.flights.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.flights.domain.Airport;
import se325.flights.domain.Flight;
import se325.flights.domain.mappers.FlightMapper;
import se325.flights.dto.BookingInfoDTO;
import se325.flights.dto.FlightDTO;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A JAX-RS Resource class for retrieving information about particular flights.
 */
@Path("/flights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FlightsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightsResource.class);

    /**
     * Parses the given departure date query. If the query matches the format "YYYY-MM-DD" (e.g. "2021-08-16"), an array
     * of two {@link ZonedDateTime} instances corresponding to 00:00:00 and 23:59:59 on the given date in the given
     * timezone is returned.
     *
     * @param departureDateQuery the date / time query to parse
     * @param timezone           the timezone to parse. Should come from {@link Airport#getTimeZone()}
     * @return an array of two {@link ZonedDateTime} instances, representing the beginning and end of the given date
     * in the given timezone
     * @throws DateTimeException if departureDateQuery or timezone are invalid
     */
    private ZonedDateTime[] parseDepartureDateQuery(String departureDateQuery, String timezone) throws DateTimeException {
        LocalDate departureDate = LocalDate.parse(departureDateQuery, DateTimeFormatter.ISO_DATE);
        return new ZonedDateTime[]{
                ZonedDateTime.of(departureDate, LocalTime.MIN, ZoneId.of(timezone)),
                ZonedDateTime.of(departureDate, LocalTime.MAX, ZoneId.of(timezone))
        };
    }


    @GET
    public Response listFlights(@QueryParam("origin") String origin, @QueryParam("destination") String destination, @QueryParam("departureDate") String departureDate) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        if (origin == null || destination == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Response response = null;
        try {
            em.getTransaction().begin();
            TypedQuery<Airport> search = em.createQuery("SELECT a FROM Airport a WHERE " +
                    "LOWER(a.name) LIKE CONCAT('%', LOWER(:search), '%') OR " +
                    "LOWER(a.code) LIKE CONCAT('%', LOWER(:search), '%')", Airport.class);

            // find airports that match the search term
            List<Airport> origins = search.setParameter("search", origin).getResultList();
            List<Airport> destinations = search.setParameter("search", destination).getResultList();

            // find matching flights
            List<Flight> flights = em.createQuery("SELECT f FROM Flight f WHERE " +
                    "f.origin IN :origins AND " +
                    "f.destination IN :destinations", Flight.class)
                    .setParameter("origins", origins)
                    .setParameter("destinations", destinations)
                    .getResultList();

            if (departureDate != null) {
                flights = flights.stream().filter(x -> {
                    String timezone = x.getOrigin().getTimeZone();
                    ZonedDateTime[] dates = parseDepartureDateQuery(departureDate, timezone);
                    // falls within day
                    if (x.getDepartureTime().compareTo(dates[0]) > -1 && x.getDepartureTime().compareTo(dates[1]) < 1) {
                        return true;
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
            }
            // sort and convert
            flights.sort((Flight f1, Flight f2) -> f1.getDepartureTime().compareTo(f1.getDepartureTime()));
            List<FlightDTO> flightsDTO = flights.stream().map(FlightMapper::toDTO).collect(Collectors.toList());
            response = Response.ok(flightsDTO).build();

        } catch (DateTimeException e) {
            LOGGER.error(e.toString());
            response = Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOGGER.error(e.toString());
            response = Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            em.close();
        }
        return response;
    }

    @GET
    @Path("{id}/booking-info")
    public Response bookingInfo(@PathParam("id") String id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Response response = null;
        try {
            Long iid = Long.valueOf(id);
            em.getTransaction().begin();
            TypedQuery<Flight> flightTypedQuery = em.createQuery("SELECT f FROM Flight f WHERE f.id = :id", Flight.class)
                    .setParameter("id", iid);
            Flight domainFlight = flightTypedQuery.getSingleResult();
            BookingInfoDTO dtoBookingInfo = FlightMapper.toBookingInfoDTO(domainFlight);
            response = Response.ok(dtoBookingInfo).build();
        } catch (NoResultException e) {
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        catch (Exception e) {
            LOGGER.error(e.toString());
        } finally {
            em.close();
        }
        return response;
    }
}

