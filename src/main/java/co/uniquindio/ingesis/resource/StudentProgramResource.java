package co.uniquindio.ingesis.resource;

import java.util.Map;

import co.uniquindio.ingesis.dto.ReportResource.ResolvedProgramReportDto;
import co.uniquindio.ingesis.service.interfaces.StudentProgramServiceInterface;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * REST resource for managing student program relationships.
 * Provides endpoints for tracking and reporting program resolution status for
 * students.
 */
@Path("/student-program")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentProgramResource {

    /**
     * Service for handling student-program interactions.
     */
    private final StudentProgramServiceInterface studentProgramService;

    /**
     * Constructor for injecting the student program service.
     *
     * @param studentProgramService Service for student program operations
     */
    public StudentProgramResource(StudentProgramServiceInterface studentProgramService) {
        this.studentProgramService = studentProgramService;
    }

    /**
     * Marks a specific program as resolved or not resolved for a student by a
     * teacher.
     *
     * @param studentId   ID of the student
     * @param programId   ID of the program
     * @param teacherId   ID of the teacher performing the operation
     * @param requestBody JSON map containing the "resolved" boolean value
     * @return Response with confirmation message
     */
    @POST
    @Path("/mark-resolved/{studentId}/{programId}/{teacherId}")
    public Response markProgramResolved(
            @PathParam("studentId") Long studentId,
            @PathParam("programId") Long programId,
            @PathParam("teacherId") Long teacherId,
            Map<String, Boolean> requestBody) {

        Boolean resolved = requestBody.get("resolved");
        studentProgramService.markProgramResolved(studentId, programId, resolved, teacherId);
        return Response.ok("Program marked as " + (resolved ? "resolved." : "not resolved.")).build();
    }

    /**
     * Checks if a specific program has been marked as resolved for a student.
     *
     * @param studentId ID of the student
     * @param programId ID of the program
     * @return Response containing true if resolved, false otherwise
     */
    @GET
    @Path("/is-resolved/{studentId}/{programId}")
    public Response isResolved(
            @PathParam("studentId") Integer studentId,
            @PathParam("programId") Integer programId) {
        boolean resolved = studentProgramService.isResolved(studentId, programId);
        return Response.ok(resolved).build();
    }

    /**
     * Deletes the program resolution record for a student.
     *
     * @param studentId ID of the student
     * @param programId ID of the program
     * @return Response with a confirmation message
     */
    @DELETE
    @Path("/mark-resolved/{studentId}/{programId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteProgramResolution(
            @PathParam("studentId") Long studentId,
            @PathParam("programId") Long programId) {
        studentProgramService.deleteProgramResolution(studentId, programId);
        return Response.ok("Program resolution deleted successfully.").build();
    }

    /**
     * Retrieves a summary report of resolved programs grouped by student.
     *
     * @return Response containing a list of {@link ResolvedProgramReportDto}
     */
    @GET
    @Path("/report/resolved-summary")
    public Response getResolvedProgramReport() {
        return Response.ok(studentProgramService.getResolvedProgramReport()).build();
    }

    /**
     * Exports the resolved program summary report to a PDF file.
     *
     * @return Response with a downloadable PDF containing the report,
     *         or error if generation fails
     */
    @GET
    @Path("/report/resolved-summary/pdf")
    @Produces("application/pdf")
    public Response exportResolvedProgramReportPdf() {
        try {
            List<ResolvedProgramReportDto> report = studentProgramService.getResolvedProgramReport();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Resolved Program Report"));
            document.add(new Paragraph(" ")); // Empty line

            PdfPTable table = new PdfPTable(3);
            table.addCell("Student ID");
            table.addCell("Student Name");
            table.addCell("Resolved Count");

            for (ResolvedProgramReportDto dto : report) {
                table.addCell(dto.studentId().toString());
                table.addCell(dto.studentName());
                table.addCell(dto.resolvedCount().toString());
            }

            document.add(table);
            document.close();

            return Response.ok(baos.toByteArray())
                    .header("Content-Disposition", "attachment; filename=resolved_program_report.pdf")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error generating PDF report").build();
        }
    }

    /**
     * Retrieves a list of all available programs in the system.
     *
     * @return Response containing the list of programs
     */
    @GET
    @Path("/programs")
    public Response getAllPrograms() {
        return Response.ok(studentProgramService.getAllPrograms()).build();
    }

}
