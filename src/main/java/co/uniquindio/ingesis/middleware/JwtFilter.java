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

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod(); //  Obtener el método HTTP de la solicitud
        System.out.println("🔍 Request Intercepted Path: '" + path + "'"); //  Imprimir ruta

        if (path.equals("/verify") || path.startsWith("/verify?") || 
        path.equals("/auth") || path.startsWith("/auth/") || 
        (path.equals("/teacher") && ("POST".equals(method) || "GET".equals(method)))) { 
        System.out.println("✅ Skipping JWT check for: " + method + " " + path);
        return;
    }
        //  Si llega aquí, significa que está pidiendo autenticación
        System.out.println(" Unauthorized request to: " + path);
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Unauthorized request to: " + path);
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }
    
        String token = authHeader.substring(7);

        try {
            Claims claims = validateToken(token);
            requestContext.setProperty("userEmail", claims.getSubject());
            requestContext.setProperty("userRole", claims.get("role", String.class));

        } catch (ExpiredJwtException e) {
            abortWithUnauthorized(requestContext, "Token has expired");
        } catch (MalformedJwtException | SecurityException | UnsupportedJwtException e) {
            abortWithUnauthorized(requestContext, "Invalid token");
        }
    }

    private Claims validateToken(String token) {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }
}
