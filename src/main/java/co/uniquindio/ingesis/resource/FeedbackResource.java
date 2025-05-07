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
 * REST resource for managing program feedback.
 * Provides endpoints for creating, reading, updating, and deleting feedback
 * submitted by teachers for specific programs.
 */
@Path("/api/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    /**
     * Service for feedback operations.
     */
    @Inject
    FeedbackServiceInterface feedbackService;

    @Inject
    ProgramServiceInterface programService;

    /**
     * Endpoint to add new feedback for a specific program by a teacher.
     *
     * @param programId ID of the program receiving feedback
     * @param teacherId ID of the teacher providing feedback
     * @param dto       DTO containing the feedback comment
     * @return Response with created feedback information
     */
    @POST
    @Path("/programa/{programId}/profesor/{teacherId}")
    @Transactional
    public Response agregarFeedback(@PathParam("programId") Long programId,
            @PathParam("teacherId") Integer teacherId,
            @Valid FeedbackDto dto) {

        // Create a new DTO with IDs from the URL
        FeedbackDto nuevoDto = new FeedbackDto(
                null,
                dto.comment(),
                programId,
                teacherId);

        FeedbackResponseDto feedback = feedbackService.agregarFeedback(nuevoDto);
        return Response.status(Response.Status.CREATED).entity(feedback).build();
    }

    /**
     * Endpoint to retrieve all feedback for a specific program.
     *
     * @param programId ID of the program
     * @return Response with list of feedback for the program
     */
    @GET
    @Path("/programa/{programId}")
    public Response obtenerFeedbackPorPrograma(@PathParam("programId") Long programId) {
        List<FeedbackResponseDto> feedbacks = feedbackService.obtenerFeedbackPorPrograma(programId);
        return Response.ok(feedbacks).build();
    }

    /**
     * Endpoint to update existing feedback for a program.
     *
     * @param feedbackId ID of the feedback to update
     * @param programId  ID of the program
     * @param teacherId  ID of the teacher
     * @param dto        DTO containing the updated comment
     * @return Response with updated feedback information
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
     * Endpoint to delete existing feedback.
     *
     * @param feedbackId ID of the feedback to delete
     * @return Response with confirmation message
     */
    @DELETE
    @Path("/{feedbackId}")
    @Transactional
    public Response eliminarFeedback(@PathParam("feedbackId") Long feedbackId) {
        feedbackService.eliminarFeedback(feedbackId);
        return Response.ok("Comentario eliminado exitosamente").build();
    }

    @GET
    @Path("/programas")
    public Response obtenerTodosLosProgramas() {
        List<ProgramDto> programas = programService.listPrograms();
        return Response.ok(programas).build();
    }

}