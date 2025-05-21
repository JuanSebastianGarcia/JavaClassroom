package co.uniquindio.ingesis.dto.login;

/**
 * DTO (Data Transfer Object) for returning an authentication token.
 * 
 * This record is used to send a token after a successful login.
 *
 * @param token The authentication token generated for the user.
 */
public record TokenResponseDto(

        String token

) {
}
