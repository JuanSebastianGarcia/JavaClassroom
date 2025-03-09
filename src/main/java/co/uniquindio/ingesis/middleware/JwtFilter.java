package co.uniquindio.ingesis.middleware; 

// Import necessary JWT handling classes
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.UnsupportedJwtException;

// Import Jakarta RESTful Web Services (JAX-RS) and security annotations
import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

// Import MicroProfile configuration for environment variable injection
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider // Marks this class as a JAX-RS provider
@Priority(Priorities.AUTHENTICATION) // Ensures this filter is executed at the authentication stage
public class JwtFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "jwt.secret.key") // Injects the JWT secret key from configuration
    private String SECRET_KEY;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Bypass authentication for endpoints related to "auth"
        if (path.contains("auth")) {
            return;
        }

        // Exclude "POST /teacher" from authentication
        if (path.equals("teacher") || path.equals("/teacher") || path.startsWith("teacher/")) {
            return;
        }

        // Extract the Authorization header
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the presence and format of the Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }

        // Extract the JWT token from the Authorization header
        String token = authHeader.substring(7); // Remove "Bearer " prefix

        try {
            // Validate the token and extract claims
            Claims claims = validateToken(token);
            requestContext.setProperty("userEmail", claims.getSubject()); // Store user email
            requestContext.setProperty("userRole", claims.get("role", String.class)); // Store user role

        } catch (ExpiredJwtException e) {
            abortWithUnauthorized(requestContext, "Token has expired");
        } catch (MalformedJwtException | SecurityException | UnsupportedJwtException e) {
            abortWithUnauthorized(requestContext, "Invalid token");
        }
    }

    /**
     * Validates and decodes the JWT token.
     *
     * @param token The JWT token to validate
     * @return The extracted claims from the token
     */
    private Claims validateToken(String token) {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Aborts the request with an unauthorized response.
     *
     * @param requestContext The current request context
     * @param message The error message to return
     */
    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }
}