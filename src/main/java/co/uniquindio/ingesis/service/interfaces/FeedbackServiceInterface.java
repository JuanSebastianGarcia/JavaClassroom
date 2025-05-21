package co.uniquindio.ingesis.service.interfaces;

import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackDto;
import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackResponseDto;
import java.util.List;

/**
 * Interface for managing feedback operations.
 */
public interface FeedbackServiceInterface {

    /**
     * Adds new feedback.
     *
     * @param dto the data transfer object containing feedback details
     * @return the response DTO with the saved feedback information
     */
    FeedbackResponseDto agregarFeedback(FeedbackDto dto);

    /**
     * Retrieves all feedback for a specific program.
     *
     * @param programId the ID of the program
     * @return a list of feedback response DTOs associated with the program
     */
    List<FeedbackResponseDto> obtenerFeedbackPorPrograma(Long programId);

    /**
     * Updates an existing feedback entry.
     *
     * @param feedbackId the ID of the feedback to update
     * @param dto        the DTO containing updated feedback data
     * @return the response DTO with the updated feedback information
     */
    FeedbackResponseDto actualizarFeedback(Long feedbackId, FeedbackDto dto);

    /**
     * Deletes a feedback entry by its ID.
     *
     * @param feedbackId the ID of the feedback to delete
     */
    void eliminarFeedback(Long feedbackId);
}
