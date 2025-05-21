package co.uniquindio.ingesis.dto;

/**
 * Generic response DTO to wrap responses with an error flag.
 *
 * @param <T>      the type of the response payload
 * @param error    indicates if there was an error (true = error, false =
 *                 success)
 * @param response the actual response data of type T
 */
public record ResponseDto<T>(boolean error, T response) {
}
