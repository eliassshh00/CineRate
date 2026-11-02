package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ExceptionDescription;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger log = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception exception) {
        log.log(Level.SEVERE, "Exception caught: " + exception.getMessage());
        var description = new ExceptionDescription(exception.getClass().getName(), exception.getMessage());
        return Response.status(statusOf(exception)).entity(description).type(MediaType.APPLICATION_JSON).build();
    }

    // group rule: 400 if the data is invalid, 404 if the element does not exist, 500 for the rest
    private int statusOf(Exception exception) {
        if (exception instanceof WebApplicationException webException) {
            return webException.getResponse().getStatus();
        }
        if (exception instanceof IllegalArgumentException || exception instanceof IllegalStateException ||
                exception instanceof ProcessingException) {
            return Response.Status.BAD_REQUEST.getStatusCode();
        }
        return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }
}
