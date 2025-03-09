package co.uniquindio.ingesis.dto.login;

/**
 * DTO (Data Transfer Object) for returning an authentication token.
 * 
 * <p>This record is used to send a token after a successful login.</p>
 *
 * @param token The authentication token generated for the user.
 */
public record TokenResponseDto(

    String token

) {
    // No additional methods needed as records automatically generate getters, equals, hashCode, and toString.
}
