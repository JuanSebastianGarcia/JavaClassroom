package co.uniquindio.ingesis.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import co.uniquindio.ingesis.dto.login.LoginDto;
import co.uniquindio.ingesis.dto.login.TokenResponseDto;
import co.uniquindio.ingesis.dto.responses.ErrorResponse;
import co.uniquindio.ingesis.exception.AccountNotVerifiedException;
import co.uniquindio.ingesis.exception.PasswordIncorrectException;
import co.uniquindio.ingesis.exception.RoleUnknownException;
import co.uniquindio.ingesis.exception.StudentNotExistException;
import co.uniquindio.ingesis.exception.TeacherNotExistException;
import co.uniquindio.ingesis.service.implement.AuthService;

/**
 * REST resource for handling authentication requests.
 * Provides endpoints for user login and token generation.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private AuthService authService;

    /**
     * Authenticates a user and returns a JWT token if credentials are valid.
     *
     * @param loginDto The login credentials (email, password, role).
     * @return HTTP response with a JWT token or an error message.
     */
    @POST
    public Response login(LoginDto loginDto) {
        try {
            TokenResponseDto tokenResponse = authService.loginUser(loginDto);
            return Response.ok(tokenResponse).build();
        } catch (StudentNotExistException | TeacherNotExistException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (PasswordIncorrectException e) {
            return buildErrorResponse(Response.Status.UNAUTHORIZED, e.getMessage());
        } catch (RoleUnknownException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }catch (AccountNotVerifiedException e) {
            return buildErrorResponse(Response.Status.UNAUTHORIZED, e.getMessage());
        } 
        catch (Exception e) { // Catch-all for unexpected errors
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    /**
     * Utility method to build standardized error responses.
     *
     * @param status The HTTP response status.
     * @param message The error message.
     * @return A Response object containing the error message.
     */
    private Response buildErrorResponse(Response.Status status, String message) {
        return Response.status(status)
                .entity(new ErrorResponse(message))
                .build();
    }
}
