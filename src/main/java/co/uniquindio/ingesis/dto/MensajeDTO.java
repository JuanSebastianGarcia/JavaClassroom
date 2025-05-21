package co.uniquindio.ingesis.dto;

/**
 * Data Transfer Object (DTO) for messaging information.
 *
 * @param tipo         the type of message (e.g., EMAIL, SMS)
 * @param destinatario the recipient of the message
 * @param contenido    the content/body of the message
 * @param asunto       the subject of the message (if applicable)
 */
public record MensajeDTO(
        String tipo,
        String destinatario,
        String contenido,
        String asunto) {
}
