package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import ch.unil.doplab.cinerate.domain.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/users")
public class UserResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return new ArrayList<>(state.getAllUsers().values());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public User getUser(@PathParam("id") UUID id) {
        var user = state.getUser(id);
        if (user == null) {
            throw new NotFoundException("No user with ID " + id);
        }
        return user;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean setUser(@PathParam("id") UUID id, User user) {
        if (!state.setUser(id, user)) {
            throw new NotFoundException("No user with ID " + id);
        }
        return true;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User addUser(User user) {
        return state.addUser(user);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean removeUser(@PathParam("id") UUID id) {
        if (!state.removeUser(id)) {
            throw new NotFoundException("No user with ID " + id);
        }
        return true;
    }
}
