package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.programResource.ProgramDto;
import co.uniquindio.ingesis.exception.ProgramNotExistException;
import co.uniquindio.ingesis.model.Program;
import co.uniquindio.ingesis.repository.ProgramRepository;
import co.uniquindio.ingesis.service.interfaces.ProgramServiceInterface;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * REST resource for managing student programs.
 * This resource provides endpoints to upload, retrieve, update, delete,
 * share, and download programs for a specific student.
 */
@Path("/student/{studentId}/program")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class ProgramResource {

    private final ProgramServiceInterface programService;

    public ProgramResource(ProgramServiceInterface programService) {
        this.programService = programService;
    }

    @Inject
    ProgramRepository programRepository;

    /**
     * Uploads a new program for a student.
     *
     * @param studentId      ID of the student
     * @param zipInputStream ZIP file containing the program's code
     * @param code           Unique code of the program
     * @param name           Name of the program
     * @param description    Description of the program
     * @return A success message or an error response
     */
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadProgram(
            @PathParam("studentId") Integer studentId,
            @FormParam("file") InputStream zipInputStream,
            @FormParam("code") String code,
            @FormParam("name") String name,
            @FormParam("description") String description) {
        try {
            ProgramDto programDto = new ProgramDto(null, code, name, description, studentId, false);
            String response = programService.addProgram(programDto, zipInputStream);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Retrieves a specific program for a student by its code.
     *
     * @param studentId ID of the student
     * @param code      Code of the program
     * @return The program details or a not found response
     */
    @GET
    @Path("/{code}")
    public Response getProgram(
            @PathParam("studentId") Integer studentId,
            @PathParam("code") String code) {
        try {
            ProgramDto program = programService.getProgram(new ProgramDto(0, code, "", "", studentId, false));
            return Response.ok(program).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    /**
     * Updates an existing program for a student.
     *
     * @param studentId      ID of the student
     * @param code           Code of the program to update
     * @param zipInputStream New ZIP file containing updated program code
     * @param name           New name of the program
     * @param description    New description of the program
     * @return A success message or an error response
     */
    @PUT
    @Path("/update/{code}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateProgram(
            @PathParam("studentId") Integer studentId,
            @PathParam("code") String code,
            @FormParam("file") InputStream zipInputStream,
            @FormParam("name") String name,
            @FormParam("description") String description) {
        try {
            ProgramDto programDto = new ProgramDto(null, code, name, description, studentId, false);
            String response = programService.updateProgram(programDto, zipInputStream);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Deletes a program for a student.
     *
     * @param studentId ID of the student
     * @param code      Code of the program to delete
     * @return A success message or an error response
     */
    @DELETE
    @Path("/{code}")
    public Response deleteProgram(
            @PathParam("studentId") Integer studentId,
            @PathParam("code") String code) {
        try {
            String response = programService.deleteProgram(new ProgramDto(0, code, "", "", studentId, false));
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private static final String BASE_DIR = "/deployments/programs";

    /**
     * Downloads the program folder as a ZIP file.
     *
     * @param programCode Code of the program
     * @return The zipped folder or a not found/error response
     */
    @GET
    @Path("/{programCode}/download")
    @Produces("application/zip")
    public Response downloadProgramAsZip(@PathParam("programCode") String programCode) {
        File programFolder = new File(BASE_DIR, programCode);

        if (!programFolder.exists() || !programFolder.isDirectory()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Program folder not found").build();
        }

        try {
            File zipFile = File.createTempFile(programCode + "-", ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                    ZipOutputStream zos = new ZipOutputStream(fos)) {
                zipFolderRecursive(programFolder, programFolder, zos);
            }

            return Response.ok(zipFile)
                    .header("Content-Disposition", "attachment; filename=\"" + programCode + ".zip\"")
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generating zip file").build();
        }
    }

    /**
     * Recursively compresses a folder and its subfolders into a ZIP stream.
     *
     * @param baseFolder  Base folder to calculate relative paths
     * @param currentFile Current file or folder to process
     * @param zos         Output ZIP stream
     */
    private void zipFolderRecursive(File baseFolder, File currentFile, ZipOutputStream zos) throws IOException {
        for (File file : currentFile.listFiles()) {
            if (file.isDirectory()) {
                zipFolderRecursive(baseFolder, file, zos);
            } else {
                String relativePath = baseFolder.toURI().relativize(file.toURI()).getPath();
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(relativePath);
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    /**
     * Updates the "shared" status of a program.
     *
     * @param studentId ID of the student
     * @param code      Code of the program
     * @param shared    New shared status
     * @return A success message or a not found response
     */
    @PUT
    @Transactional
    @Path("/{code}/share")
    public Response shareProgram(
            @PathParam("studentId") Integer studentId,
            @PathParam("code") String code,
            @QueryParam("shared") boolean shared) {

        try {
            Program program = programRepository.findByCodeAndStudentId(code, studentId)
                    .orElseThrow(ProgramNotExistException::new);

            program.setShared(shared);
            programRepository.persist(program);

            return Response.ok("Program shared status updated: " + shared).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Retrieves a list of all programs marked as shared.
     *
     * @return List of shared program DTOs
     */
    @GET
    @Transactional
    @Path("/shared")
    public List<ProgramDto> getSharedPrograms() {
        return programService.listSharedPrograms();
    }
}
