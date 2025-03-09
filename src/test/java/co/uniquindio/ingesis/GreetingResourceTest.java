package co.uniquindio.ingesis;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@QuarkusTest // Marks this class as a Quarkus test, enabling dependency injection and testing support
class GreetingResourceTest {

    @ConfigProperty(name = "jwt.secret.key") 
    String SECRET_KEY; // Injects the JWT secret key from the configuration

    /**
     * Generates a JWT token for testing purposes.
     * 
     * @return A signed JWT token with test user data.
     */
    private String generateToken() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY); // Decode the base64 secret key
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256"); // Create the signing key

        return Jwts.builder()
                .setSubject("testuser") // Set the test subject (username)
                .setIssuer("javclassroom") // Define the token issuer
                .setIssuedAt(new Date()) // Set the issue date
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // Set expiration to 1 hour
                .claim("role", "teacher") // Add a custom claim (user role)
                .signWith(secretKey) // Sign the token with the secret key
                .compact();
    }

    @Test // Marks this as a JUnit test method
    void testHelloEndpoint() {
        String token = generateToken(); // Generate a valid test token

        // Perform an HTTP GET request to the "/hello" endpoint with the token
        given()
            .header("Authorization", "Bearer " + token) // Set the Authorization header
            .when().get("/hello") // Make the GET request
            .then()
            .statusCode(200) // Verify that the response status is 200 (OK)
            .body(is("Hello from Quarkus REST")); // Check the response body
    }
}

