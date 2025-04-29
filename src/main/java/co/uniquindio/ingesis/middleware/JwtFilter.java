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
        String method = requestContext.getMethod(); // Obtener el método HTTP de la solicitud
        System.out.println("Request Intercepted Path: '" + path + "'");

        // Excluir todos los endpoints bajo "/program"
        if (path.startsWith("program") || path.startsWith("/program")) {
            System.out.println("Skipping JWT check for path: " + path);
            return; // No hacer nada y continuar con la ejecución normal
        }

        // Excluir rutas como /verify, /auth y las de los profesores
        if (path.equals("/verify") || path.startsWith("/verify?") ||
                path.equals("/auth") || path.startsWith("/auth/") ||
                (path.equals("/teacher") && ("POST".equals(method) || "GET".equals(method)))) {
            System.out.println("Skipping JWT check for: " + method + " " + path);
            return;
        }

        // Si llega aquí, significa que está pidiendo autenticación
        System.out.println("Unauthorized request to: " + path);
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Unauthorized request to: " + path);
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7); // Eliminar "Bearer " del encabezado
        System.out.println("Received token: " + token); // Imprimir el token para verificar si es correcto

        try {
            Claims claims = validateToken(token); // Validar el token
            System.out.println("Token validated. Claims: " + claims); // Imprimir todas las claims del token

            // Asignar los valores de claims al contexto de la solicitud
            requestContext.setProperty("userEmail", claims.getSubject());
            requestContext.setProperty("userRole", claims.get("role", String.class));
            requestContext.setProperty("userCedula", claims.get("cedula", String.class));

        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            abortWithUnauthorized(requestContext, "Token has expired");
        } catch (MalformedJwtException | SecurityException | UnsupportedJwtException e) {
            System.out.println("Invalid token: " + e.getMessage());
            abortWithUnauthorized(requestContext, "Invalid token");
        }
    }

    // Método para validar el token JWT
    private Claims validateToken(String token) {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY); // Obtener la clave secreta
        System.out.println("Secret key used for validation: " + SECRET_KEY); // Verificar la clave secreta
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Método para abortar la solicitud con respuesta UNAUTHORIZED
    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }
}
