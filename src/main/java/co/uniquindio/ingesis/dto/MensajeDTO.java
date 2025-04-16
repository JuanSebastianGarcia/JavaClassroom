package co.uniquindio.ingesis.dto;

public record MensajeDTO(
    String tipo,          // Tipo de mensaje (EMAIL, SMS, etc.)
    String destinatario,  // Destinatario del mensaje
    String contenido,     // Contenido del mensaje
    String asunto         // Asunto del mensaje (si aplica)
) {}
