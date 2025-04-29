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
import java.io.InputStream;
import java.util.List;

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

}
