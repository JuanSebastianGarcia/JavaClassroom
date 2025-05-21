package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.ExampleResource.ExampleDto;
import co.uniquindio.ingesis.service.interfaces.ExampleServiceInterface;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * REST resource for managing code examples.
 * Provides endpoints for uploading, retrieving, updating, deleting,
 * assigning, and downloading programming examples.
 */
@Path("/example")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll // Public access without authentication
public class ExampleResource {

    private final ExampleServiceInterface exampleService;

    public ExampleResource(ExampleServiceInterface exampleService) {
        this.exampleService = exampleService;
    }

    /**
     * Creates a new example along with its ZIP file.
     *
     * @param id             Example ID
     * @param zipInputStream Input stream of the ZIP file
     * @param title          Example title
     * @param content        Example content/description
     * @param category       Example category (e.g., Loops, Arrays)
     * @param difficulty     Difficulty level of the example
     * @param requestContext Request context to extract user identity
     * @return Response indicating the result of the operation
     */
    @POST
    @Path("/update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createExample(
            @FormParam("id") Integer id,
            @FormParam("file") InputStream zipInputStream,
            @FormParam("title") String title,
            @FormParam("content") String content,
            @FormParam("category") String category,
            @FormParam("difficulty") Integer difficulty,
            @Context ContainerRequestContext requestContext) {
        try {
            String cedulaProfesor = (String) requestContext.getProperty("userCedula");
            ExampleDto exampleDto = new ExampleDto(id, title, content, category, difficulty, cedulaProfesor);
            String response = exampleService.addExample(exampleDto, zipInputStream);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Retrieves an example by its ID.
     *
     * @param id Example ID
     * @return Example DTO or 404 if not found
     */
    @GET
    @Path("/{id}")
    public Response getExample(@PathParam("id") Integer id) {
        try {
            ExampleDto example = exampleService.getExample(new ExampleDto(id, "", "", "", 0, ""));
            return Response.ok(example).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    /**
     * Updates an existing example and its associated ZIP file.
     *
     * @param id             Example ID
     * @param zipInputStream Updated ZIP file input stream
     * @param title          New title
     * @param content        New content/description
     * @param category       New category
     * @param difficulty     New difficulty level
     * @param requestContext Request context to extract user identity
     * @return Response with update result
     */
    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateExample(
            @PathParam("id") Integer id,
            @FormParam("file") InputStream zipInputStream,
            @FormParam("title") String title,
            @FormParam("content") String content,
            @FormParam("category") String category,
            @FormParam("difficulty") Integer difficulty,
            @Context ContainerRequestContext requestContext) {
        try {
            String cedulaProfesor = (String) requestContext.getProperty("userCedula");
            ExampleDto exampleDto = new ExampleDto(id, title, content, category, difficulty, cedulaProfesor);
            String response = exampleService.updateExample(exampleDto, zipInputStream);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Deletes an example by ID.
     *
     * @param id Example ID
     * @return Response with deletion result
     */
    @DELETE
    @Path("/{id}")
    public Response deleteExample(@PathParam("id") Integer id) {
        try {
            String response = exampleService.deleteExample(id);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Retrieves a list of all examples.
     *
     * @return List of examples
     */
    @GET
    @Path("/list")
    public Response listExamples() {
        try {
            return Response.ok(exampleService.listExamples()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Assigns an example to multiple students.
     *
     * @param exampleId          Example ID
     * @param cedulasEstudiantes List of student IDs
     * @return Response with assignment result
     */
    @POST
    @Path("/{id}/assign")
    public Response assignExample(
            @PathParam("id") Integer exampleId,
            List<String> cedulasEstudiantes) {
        try {
            String response = exampleService.assignExampleToStudents(exampleId, cedulasEstudiantes);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Retrieves the list of students assigned to a given example.
     *
     * @param exampleId Example ID
     * @return List of student IDs
     */
    @GET
    @Path("/{id}/students")
    public List<String> getStudentsByExample(@PathParam("id") Integer exampleId) {
        return exampleService.getStudentsAssignedToExample(exampleId);
    }

    private static final String EXAMPLES_BASE_DIR = "/deployments/ejemplos";

    /**
     * Downloads an example's files as a ZIP archive.
     *
     * @param exampleId Example ID
     * @return ZIP file containing the example's content
     */
    @GET
    @Path("/ejemplo/{exampleId}/download")
    @Produces("application/zip")
    public Response downloadExampleAsZip(@PathParam("exampleId") String exampleId) {
        File exampleFolder = new File(EXAMPLES_BASE_DIR, exampleId);

        if (!exampleFolder.exists() || !exampleFolder.isDirectory()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Example folder not found").build();
        }

        try {
            File zipFile = File.createTempFile("example-" + exampleId + "-", ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                    ZipOutputStream zos = new ZipOutputStream(fos)) {
                zipFolderRecursive(exampleFolder, exampleFolder, zos);
            }

            return Response.ok(zipFile)
                    .header("Content-Disposition", "attachment; filename=\"example-" + exampleId + ".zip\"")
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating ZIP file for the example").build();
        }
    }

    /**
     * Recursively compresses a folder and its subdirectories into a ZIP output
     * stream.
     *
     * @param baseFolder  Base folder to calculate relative paths
     * @param currentFile Current file or folder being processed
     * @param zos         Output stream to write ZIP entries
     * @throws IOException if an I/O error occurs
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
}
