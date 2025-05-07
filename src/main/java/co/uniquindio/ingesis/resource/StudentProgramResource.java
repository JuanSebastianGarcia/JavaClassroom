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
     * Endpoint to mark a program as resolved or not resolved for a specific
     * student.
     *
     * @param studentId   ID of the student
     * @param programId   ID of the program
     * @param requestBody JSON map containing the resolved status (key: "resolved",
     *                    value: boolean)
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

    /**
     * Endpoint to retrieve a summary report of resolved programs per student.
     *
     * @return Response with a list of ResolvedProgramReportDto
     */
    @GET
    @Path("/report/resolved-summary")
    public Response getResolvedProgramReport() {
        return Response.ok(studentProgramService.getResolvedProgramReport()).build();
    }

    /**
     * Endpoint to export the resolved program report as a PDF file.
     *
     * @return PDF file containing the resolved programs summary
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

            // Agregamos título
            document.add(new Paragraph("Resolved Program Report"));
            document.add(new Paragraph(" ")); // línea vacía

            // Crear tabla de 3 columnas
            PdfPTable table = new PdfPTable(3);
            table.addCell("Student ID");
            table.addCell("Student Name");
            table.addCell("Resolved Count");

            // Llenar la tabla
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

    @GET
    @Path("/programs")
    public Response getAllPrograms() {
        return Response.ok(studentProgramService.getAllPrograms()).build();
    }

}