package co.uniquindio.ingesis.resource;

import java.util.Map;
import co.uniquindio.ingesis.service.interfaces.StudentProgramServiceInterface;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST resource for managing student program relationships.
 * Provides endpoints for tracking program resolution status for students.
 */
@Path("/student-program")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentProgramResource {

    /**
     * Service for student program operations.
     */
    private final StudentProgramServiceInterface studentProgramService;

    /**
     * Constructor with dependency injection for the student program service.
     *
     * @param studentProgramService Service for student program operations
     */
    public StudentProgramResource(StudentProgramServiceInterface studentProgramService) {
        this.studentProgramService = studentProgramService;
    }

    /**
     * Endpoint to mark a program as resolved or not resolved for a specific student.
     *
     * @param studentId   ID of the student
     * @param programId   ID of the program
     * @param requestBody JSON map containing the resolved status (key: "resolved", value: boolean)
     * @return Response with confirmation message
     */
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

    /**
     * Endpoint to check if a program is marked as resolved for a specific student.
     *
     * @param studentId ID of the student
     * @param programId ID of the program
     * @return Response with boolean indicating if the program is resolved
     */
    @GET
    @Path("/is-resolved/{studentId}/{programId}")
    public Response isResolved(@PathParam("studentId") Integer studentId,
                               @PathParam("programId") Integer programId) {
        boolean resolved = studentProgramService.isResolved(studentId, programId);
        return Response.ok(resolved).build();
    }

    /**
     * Endpoint to delete a program resolution record for a specific student.
     *
     * @param studentId ID of the student
     * @param programId ID of the program
     * @return Response with confirmation message
     */
    @DELETE
    @Path("/mark-resolved/{studentId}/{programId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteProgramResolution(@PathParam("studentId") Long studentId,
                                            @PathParam("programId") Long programId) {
        studentProgramService.deleteProgramResolution(studentId, programId);
        return Response.ok("Program resolution deleted successfully.").build();
    }
}