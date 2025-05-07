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
 * Recurso REST para la gestión de programas.
 * Proporciona endpoints para crear, consultar, actualizar y eliminar programas.
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

    @GET
    @Path("/{code}")
    public Response getProgram(
            @PathParam("studentId") Integer studentId, // Añadido el parámetro
            @PathParam("code") String code) {
        try {
            ProgramDto program = programService.getProgram(new ProgramDto(0, code, "", "", studentId, false));
            return Response.ok(program).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/update/{code}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateProgram(
            @PathParam("studentId") Integer studentId, // Añadido el parámetro
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

    @DELETE
    @Path("/{code}")
    public Response deleteProgram(
            @PathParam("studentId") Integer studentId, // Añadido el parámetro
            @PathParam("code") String code) {
        try {
            String response = programService.deleteProgram(new ProgramDto(0, code, "", "", studentId, false));
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private static final String BASE_DIR = "/deployments/programs";

    @GET
    @Path("/{programCode}/download")
    @Produces("application/zip")
    public Response downloadProgramAsZip(@PathParam("programCode") String programCode) {
        File programFolder = new File(BASE_DIR, programCode);

        if (!programFolder.exists() || !programFolder.isDirectory()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró la carpeta del programa").build();
        }

        try {
            File zipFile = File.createTempFile(programCode + "-", ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                    ZipOutputStream zos = new ZipOutputStream(fos)) {

                zipFolderRecursive(programFolder, programFolder, zos); // 👈 Recursivo
            }

            return Response.ok(zipFile)
                    .header("Content-Disposition", "attachment; filename=\"" + programCode + ".zip\"")
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al crear el archivo zip").build();
        }
    }

    // 🔁 Método auxiliar para comprimir con subdirectorios
    private void zipFolderRecursive(File baseFolder, File currentFile, ZipOutputStream zos) throws IOException {
        for (File file : currentFile.listFiles()) {
            if (file.isDirectory()) {
                zipFolderRecursive(baseFolder, file, zos);
            } else {
                String relativePath = baseFolder.toURI().relativize(file.toURI()).getPath(); // Estructura de carpetas
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

    @GET
    @Transactional
    @Path("/shared")
    public List<ProgramDto> getSharedPrograms() {
        return programService.listSharedPrograms();
    }

}