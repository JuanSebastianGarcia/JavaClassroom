package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackDto;
import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackResponseDto;
import co.uniquindio.ingesis.dto.programResource.ProgramDto;
import co.uniquindio.ingesis.service.interfaces.FeedbackServiceInterface;
import co.uniquindio.ingesis.service.interfaces.ProgramServiceInterface;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * REST resource for managing feedback on programming assignments.
 * Provides endpoints to allow teachers to create, retrieve, update, and delete
 * feedback
 * for student-submitted programs.
 */
@Path("/api/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    /**
     * Service interface for feedback-related operations.
     */
    @Inject
    FeedbackServiceInterface feedbackService;

    /**
     * Service interface for program-related operations.
     */
    @Inject
    ProgramServiceInterface programService;

    /**
     * Creates new feedback for a specific program provided by a teacher.
     *
     * @param programId the ID of the program being reviewed
     * @param teacherId the ID of the teacher giving the feedback
     * @param dto       the feedback content to be saved
     * @return a response containing the created feedback data
     */
    @POST
    @Path("/programa/{programId}/profesor/{teacherId}")
    @Transactional
    public Response agregarFeedback(@PathParam("programId") Long programId,
            @PathParam("teacherId") Integer teacherId,
            @Valid FeedbackDto dto) {

        FeedbackDto nuevoDto = new FeedbackDto(
                null,
                dto.comment(),
                programId,
                teacherId);

        FeedbackResponseDto feedback = feedbackService.agregarFeedback(nuevoDto);
        return Response.status(Response.Status.CREATED).entity(feedback).build();
    }

    /**
     * Retrieves all feedback associated with a specific program.
     *
     * @param programId the ID of the program whose feedback is being requested
     * @return a response containing a list of feedback entries
     */
    @GET
    @Path("/programa/{programId}")
    public Response obtenerFeedbackPorPrograma(@PathParam("programId") Long programId) {
        List<FeedbackResponseDto> feedbacks = feedbackService.obtenerFeedbackPorPrograma(programId);
        return Response.ok(feedbacks).build();
    }

    /**
     * Updates an existing feedback entry for a given program and teacher.
     *
     * @param feedbackId the ID of the feedback entry to update
     * @param programId  the ID of the associated program
     * @param teacherId  the ID of the teacher who provided the feedback
     * @param dto        the updated feedback comment
     * @return a response with the updated feedback information
     */
    @PUT
    @Path("/{feedbackId}/programa/{programId}/profesor/{teacherId}")
    @Transactional
    public Response actualizarFeedback(@PathParam("feedbackId") Long feedbackId,
            @PathParam("programId") Long programId,
            @PathParam("teacherId") Integer teacherId,
            @Valid FeedbackDto dto) {

        FeedbackDto nuevoDto = new FeedbackDto(
                null,
                dto.comment(),
                programId,
                teacherId);

        FeedbackResponseDto response = feedbackService.actualizarFeedback(feedbackId, nuevoDto);
        return Response.ok(response).build();
    }

    /**
     * Deletes a feedback entry by its ID.
     *
     * @param feedbackId the ID of the feedback to be deleted
     * @return a response confirming successful deletion
     */
    @DELETE
    @Path("/{feedbackId}")
    @Transactional
    public Response eliminarFeedback(@PathParam("feedbackId") Long feedbackId) {
        feedbackService.eliminarFeedback(feedbackId);
        return Response.ok("Feedback successfully deleted").build();
    }

    /**
     * Retrieves all programs available in the system.
     *
     * @return a response containing the list of all registered programs
     */
    @GET
    @Path("/programas")
    public Response obtenerTodosLosProgramas() {
        List<ProgramDto> programas = programService.listPrograms();
        return Response.ok(programas).build();
    }
}