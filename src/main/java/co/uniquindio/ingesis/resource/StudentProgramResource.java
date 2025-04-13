package co.uniquindio.ingesis.resource;

import java.util.Map;
import co.uniquindio.ingesis.service.interfaces.StudentProgramServiceInterface;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/student-program")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentProgramResource {

    private final StudentProgramServiceInterface studentProgramService;

    public StudentProgramResource(StudentProgramServiceInterface studentProgramService) {
        this.studentProgramService = studentProgramService;
    }

    @POST
    @Path("/mark-resolved/{studentId}/{programId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response markProgramResolved(
            @PathParam("studentId") Long studentId,
            @PathParam("programId") Long programId,
            Map<String, Boolean> requestBody) {
    
        Boolean resolved = requestBody.get("resolved");
        studentProgramService.markProgramResolved(studentId, programId, resolved);
        return Response.ok("Program marked as " + (resolved ? "resolved." : "not resolved.")).build();
    }

    @GET
    @Path("/is-resolved/{studentId}/{programId}")
    public Response isResolved(@PathParam("studentId") Integer studentId,
                               @PathParam("programId") Integer programId) {
        boolean resolved = studentProgramService.isResolved(studentId, programId);
        return Response.ok(resolved).build();
    }

    @DELETE
    @Path("/mark-resolved/{studentId}/{programId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteProgramResolution(@PathParam("studentId") Long studentId,
                                            @PathParam("programId") Long programId) {
        studentProgramService.deleteProgramResolution(studentId, programId);
        return Response.ok("Program resolution deleted successfully.").build();
    }
}
