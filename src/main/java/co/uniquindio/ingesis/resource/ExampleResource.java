package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.ExampleResource.ExampleDto;
//import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.service.interfaces.ExampleServiceInterface;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.PermitAll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Path("/example")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll // Permite acceso sin autenticación
public class ExampleResource {

    private final ExampleServiceInterface exampleService;

    public ExampleResource(ExampleServiceInterface exampleService) {
        this.exampleService = exampleService;
    }

    /**
     * Endpoint para agregar un ejemplo, incluyendo un archivo ZIP.
     *
     * @param zipInputStream Stream del archivo ZIP con el contenido del ejemplo
     * @param exampleDto     Datos del ejemplo (título, contenido, etc.)
     * @return Respuesta con el resultado de la operación
     */
    @POST
    @Path("/update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response CreateExample(
            @FormParam("id") Integer id, // Extraemos el ID del formulario
            @FormParam("file") InputStream zipInputStream, // Extraemos el archivo ZIP
            @FormParam("title") String title, // Extraemos el título
            @FormParam("content") String content, // Extraemos el contenido
            @FormParam("category") String category, // Extraemos la categoría
            @FormParam("difficulty") Integer difficulty, // Extraemos el nivel de dificultad
            @Context ContainerRequestContext requestContext) {
        try {
            // Obtener la cédula del usuario autenticado desde el contexto
            String cedulaProfesor = (String) requestContext.getProperty("userCedula");

            // Crea un DTO con los parámetros recibidos y la cédula del profesor
            ExampleDto exampleDto = new ExampleDto(id, title, content, category, difficulty, cedulaProfesor);

            // Procesa el archivo ZIP y los datos del ejemplo
            String response = exampleService.addExample(exampleDto, zipInputStream);

            // Devuelve respuesta exitosa con estado 200 (OK)
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception e) {
            // Manejo de errores centralizado
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint para obtener un ejemplo por su ID.
     *
     * @param id ID único del ejemplo
     * @return Respuesta con el ejemplo encontrado o error si no existe
     */
    @GET
    @Path("/{id}")
    public Response getExample(@PathParam("id") Integer id) {
        try {
            // Obtiene el ejemplo desde el servicio
            ExampleDto example = exampleService.getExample(new ExampleDto(id, "", "", "", 0, ""));
            return Response.ok(example).build();
        } catch (Exception e) {
            // Si no se encuentra el ejemplo, devuelve error 404
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint para actualizar un ejemplo, incluyendo su archivo ZIP.
     *
     * @param id             ID único del ejemplo a actualizar
     * @param zipInputStream Stream del archivo ZIP con el contenido actualizado
     *                       (opcional)
     * @param exampleDto     Nuevos datos del ejemplo
     * @return Respuesta con el resultado de la operación
     */
    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateExample(
            @PathParam("id") Integer id, // Extraemos el ID de la URL
            @FormParam("file") InputStream zipInputStream, // Extraemos el archivo ZIP
            @FormParam("title") String title, // Extraemos el título
            @FormParam("content") String content, // Extraemos el contenido
            @FormParam("category") String category, // Extraemos la categoría
            @FormParam("difficulty") Integer difficulty, // Extraemos el nivel de dificultad
            @Context ContainerRequestContext requestContext) {
        try {
            // Obtener la cédula del usuario autenticado desde el contexto
            String cedulaProfesor = (String) requestContext.getProperty("userCedula");

            // Crea un DTO con los parámetros recibidos y la cédula del profesor
            ExampleDto exampleDto = new ExampleDto(id, title, content, category, difficulty, cedulaProfesor);

            // Actualiza el ejemplo y su archivo asociado
            String response = exampleService.updateExample(exampleDto, zipInputStream);

            // Devuelve respuesta exitosa
            return Response.ok(response).build();
        } catch (Exception e) {
            // Manejo de errores centralizado
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint para eliminar un ejemplo por su ID.
     *
     * @param id ID único del ejemplo a eliminar
     * @return Respuesta con el resultado de la operación
     */
    @DELETE
    @Path("/{id}")
    public Response deleteExample(@PathParam("id") Integer id) {
        try {
            // Elimina el ejemplo
            String response = exampleService.deleteExample(id);
            return Response.ok(response).build();
        } catch (Exception e) {
            // Manejo de errores centralizado
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint para listar todos los ejemplos.
     *
     * @return Respuesta con la lista de ejemplos
     */
    @GET
    @Path("/list")
    public Response listExamples() {
        try {
            // Devuelve la lista de ejemplos
            return Response.ok(exampleService.listExamples()).build();
        } catch (Exception e) {
            // Manejo de errores centralizado
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

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

    @GET
    @Path("/{id}/students")
    public List<String> getStudentsByExample(@PathParam("id") Integer exampleId) {
        return exampleService.getStudentsAssignedToExample(exampleId);
    }

    private static final String EXAMPLES_BASE_DIR = "/deployments/ejemplos";

    @GET
    @Path("/ejemplo/{exampleId}/download")
    @Produces("application/zip")
    public Response downloadExampleAsZip(@PathParam("exampleId") String exampleId) {
        File exampleFolder = new File(EXAMPLES_BASE_DIR, exampleId);

        if (!exampleFolder.exists() || !exampleFolder.isDirectory()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró la carpeta del ejemplo").build();
        }

        try {
            File zipFile = File.createTempFile("example-" + exampleId + "-", ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                    ZipOutputStream zos = new ZipOutputStream(fos)) {

                zipFolderRecursive(exampleFolder, exampleFolder, zos); // Recursivo
            }

            return Response.ok(zipFile)
                    .header("Content-Disposition", "attachment; filename=\"ejemplo-" + exampleId + ".zip\"")
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al crear el archivo zip del ejemplo").build();
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

}
