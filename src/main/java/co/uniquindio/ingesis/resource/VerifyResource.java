package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.service.interfaces.VerificationServiceInterface;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

/**
 * Resource for handling user account verification requests.
 */
@Path("/verify")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VerifyResource {

    private final VerificationServiceInterface verifyService;

    @Inject
    public VerifyResource(VerificationServiceInterface verifyService) {
        this.verifyService = verifyService;
    }

    /**
     * Verifies a student's account using the provided token.
     *
     * @param token The verification token.
     * @param email The student's email address.
     * @return HTTP response indicating the verification result.
     */
    @POST
    public Response verifyStudent(@QueryParam("token") String token, @QueryParam("email") String email) {
        try {
            verifyService.verifyAccount(email, token); // Se corrigió el parámetro incorrecto
            return Response.ok("The account has been verified").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
