package co.uniquindio.ingesis.middleware;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.UnsupportedJwtException;

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

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "jwt.secret.key")
    private String SECRET_KEY;

    /**
     * Intercepts incoming HTTP requests to perform JWT authentication.
     * Allows specific endpoints to bypass authentication.
     *
     * @param requestContext the context of the HTTP request
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();
        System.out.println("Request Intercepted Path: '" + path + "'");

        // Skip JWT validation for all endpoints under "/program"
        if (path.startsWith("program") || path.startsWith("/program")) {
            System.out.println("Skipping JWT check for path: " + path);
            return;
        }

        // Skip JWT validation for specific public routes: /verify, /auth, and some
        // teacher routes
        if (path.equals("/verify") || path.startsWith("/verify?") ||
                path.equals("/auth") || path.startsWith("/auth/") ||
                (path.equals("/teacher") && ("POST".equals(method) || "GET".equals(method)))) {
            System.out.println("Skipping JWT check for: " + method + " " + path);
            return;
        }

        // For other requests, perform JWT validation
        System.out.println("Unauthorized request to: " + path);
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the Authorization header is present and correctly formatted
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Unauthorized request to: " + path);
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }

        // Extract JWT token from the Authorization header
        String token = authHeader.substring(7);
        System.out.println("Received token: " + token);

        try {
            // Validate the JWT token and extract claims
            Claims claims = validateToken(token);
            System.out.println("Token validated. Claims: " + claims);

            // Set extracted claims as request properties for downstream use
            requestContext.setProperty("userEmail", claims.getSubject());
            requestContext.setProperty("userRole", claims.get("role", String.class));
            requestContext.setProperty("userCedula", claims.get("cedula", String.class));
            Integer userId = claims.get("id", Integer.class);
            requestContext.setProperty("userId", userId);

        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            abortWithUnauthorized(requestContext, "Token has expired");
        } catch (MalformedJwtException | SecurityException | UnsupportedJwtException e) {
            System.out.println("Invalid token: " + e.getMessage());
            abortWithUnauthorized(requestContext, "Invalid token");
        }
    }

    /**
     * Validates the JWT token using the configured secret key.
     *
     * @param token the JWT token string to validate
     * @return Claims extracted from the token if valid
     */
    private Claims validateToken(String token) {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        System.out.println("Secret key used for validation: " + SECRET_KEY);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Aborts the HTTP request by responding with an UNAUTHORIZED status and an
     * error message.
     *
     * @param requestContext the context of the HTTP request
     * @param message        the error message to include in the response body
     */
    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }
}
