package se325.flights.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.flights.domain.User;
import se325.flights.domain.mappers.UserMapper;
import se325.flights.dto.UserDTO;
import se325.flights.util.SecurityUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * A JAX-RS resource class which handles requests to create user accounts, log in, and log out.
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @POST
    public Response create(UserDTO dtoUser) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Response response;
        try {
            em.getTransaction().begin();
            User domainUser = UserMapper.toDomain(dtoUser);
            TypedQuery<User> userExistsQuery = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", domainUser.getUsername());
            if (userExistsQuery.getResultList().isEmpty()) {
                String uuid = SecurityUtils.generateRandomUUIDString();
                // XXX: use uuid somehow here?
                em.persist(domainUser);
                em.getTransaction().commit();
                response = Response.created(URI.create("/users/" + uuid)).build();
            } else {
                response = Response.status(Response.Status.CONFLICT).build();
            }
            return response;
        } finally {
            em.close();
        }
    }

    @POST
    @Path("/login")
    public Response login(UserDTO dtoUser) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Response response;
        try {
            em.getTransaction().begin();
            User domainParamUser = UserMapper.toDomain(dtoUser);
            TypedQuery<User> userLoginQuery = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username AND u.passHash = :passhash", User.class)
                    .setParameter("username", domainParamUser.getUsername())
                    .setParameter("passhash", domainParamUser.getPassHash());
            User domainUser = userLoginQuery.getSingleResult();
            NewCookie cookie = SecurityUtils.generateAuthCookie();
            String authToken = cookie.getValue();
            domainUser.setAuthToken(authToken);
            em.getTransaction().commit();
            response = Response.status(204).cookie(cookie).build();
        } catch (NoResultException e) {
            LOGGER.debug(e.toString());
            response = Response.status(401).build();
        } finally {
            em.close();
        }
        return response;
    }

    @GET
    @Path("/logout")
    public Response logout(@CookieParam("authToken") String authToken) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<User> userLoginQuery = em.createQuery(
                    "SELECT u FROM User u WHERE u.authToken = :authToken", User.class)
                    .setParameter("authToken", authToken);
            User domainUser = userLoginQuery.getSingleResult();
            domainUser.setAuthToken(null);
            em.getTransaction().commit();
        } catch (NoResultException e) {
            LOGGER.debug(e.toString());
        } finally {
            em.close();
        }
        return Response.noContent().cookie(SecurityUtils.generateDeleteAuthCookie()).build();
    }
}
