package co.uniquindio.ingesis.dto.FeedbackResource;

import jakarta.validation.constraints.NotBlank;

public record FeedbackDto(

    /*
     * Feedback id (autogenerado en la base de datos)
     */
    Integer id,

    /*
     * Comentario del profesor
     */
    @NotBlank
    String comment,

    /*
     * ID del programa al que pertenece el comentario
     */
    Long programId,

    /*
     * ID del profesor que dejó el comentario
     */

    Integer teacherId

) {}