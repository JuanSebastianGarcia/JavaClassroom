package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackDto;
import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackResponseDto;
import co.uniquindio.ingesis.service.interfaces.FeedbackServiceInterface;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    @Inject
    FeedbackServiceInterface feedbackService;

    @POST
    @Path("/programa/{programId}/profesor/{teacherId}")
    @Transactional
    public Response agregarFeedback(@PathParam("programId") Long programId,
                                     @PathParam("teacherId") Integer teacherId,
                                     @Valid FeedbackDto dto) {
    
        // Creamos un nuevo DTO con los IDs de la URL
        FeedbackDto nuevoDto = new FeedbackDto(
                null,
                dto.comment(),
                programId,
                teacherId
        );
    
        FeedbackResponseDto feedback = feedbackService.agregarFeedback(nuevoDto);
        return Response.status(Response.Status.CREATED).entity(feedback).build();
    }

    @GET
    @Path("/programa/{programId}")
    public Response obtenerFeedbackPorPrograma(@PathParam("programId") Long programId) {
        List<FeedbackResponseDto> feedbacks = feedbackService.obtenerFeedbackPorPrograma(programId);
        return Response.ok(feedbacks).build();
    }
    

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
                teacherId
        );
    
        FeedbackResponseDto response = feedbackService.actualizarFeedback(feedbackId, nuevoDto);
        return Response.ok(response).build();
    }
    

    @DELETE
    @Path("/{feedbackId}")
    @Transactional
    public Response eliminarFeedback(@PathParam("feedbackId") Long feedbackId) {
        feedbackService.eliminarFeedback(feedbackId);
        return Response.ok("Comentario eliminado exitosamente").build();
    }

    
}
