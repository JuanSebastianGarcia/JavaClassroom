package co.uniquindio.ingesis.dto.responses;

/**
 * Data Transfer Object (DTO) representing a standardized error response.
 *
 * @param error the error message describing the failure or issue
 */
public record ErrorResponse(String error) {
}
