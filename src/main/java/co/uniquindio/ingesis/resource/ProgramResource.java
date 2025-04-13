package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.programResource.ProgramDto;
import co.uniquindio.ingesis.service.interfaces.ProgramServiceInterface;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;

/**
 * Recurso REST para la gestión de programas.
 * Proporciona endpoints para crear, consultar, actualizar y eliminar programas.
 */
@Path("/program")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll  // Permite acceso sin autenticación
public class ProgramResource {

    /**
     * Servicio que implementa la lógica de negocio para los programas.
     */
    private final ProgramServiceInterface programService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param programService Servicio de programas
     */
    public ProgramResource(ProgramServiceInterface programService) {
        this.programService = programService;
    }

    /**
     * Endpoint para cargar un nuevo programa.
     *
     * @param zipInputStream Stream del archivo ZIP con el contenido del programa
     * @param code Código único del programa
     * @param name Nombre del programa
     * @param description Descripción del programa
     * @return Respuesta con el resultado de la operación
     */
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadProgram(
            @FormParam("file") InputStream zipInputStream,
            @FormParam("code") String code,
            @FormParam("name") String name,
            @FormParam("description") String description) {
        try {
            // Crea un DTO con los parámetros recibidos
            ProgramDto programDto = new ProgramDto(null, code, name, description);
            
            // Procesa el archivo ZIP y los datos del programa
            String response = programService.addProgram(programDto, zipInputStream);
            
            // Devuelve respuesta exitosa con estado 201 (CREATED)
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            // Manejo de errores centralizado
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint para obtener información de un programa por su código.
     *
     * @param code Código único del programa a consultar
     * @return Respuesta con el programa encontrado o error si no existe
     */
    @GET
    @Path("/{code}")
    public Response getProgram(@PathParam("code") String code) {
        try {
            // Crea un DTO con el código recibido para buscar el programa
            ProgramDto program = programService.getProgram(new ProgramDto(0, code, "", ""));
            
            // Devuelve el programa encontrado
            return Response.ok(program).build();
        } catch (Exception e) {
            // Si no se encuentra el programa, devuelve error 404
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint para actualizar un programa existente.
     *
     * @param code Código único del programa a actualizar
     * @param zipInputStream Stream del archivo ZIP con el contenido actualizado (opcional)
     * @param name Nuevo nombre del programa
     * @param description Nueva descripción del programa
     * @return Respuesta con el resultado de la operación
     */
    @PUT
    @Path("/update/{code}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateProgram(
            @PathParam("code") String code,
            @FormParam("file") InputStream zipInputStream,
            @FormParam("name") String name,
            @FormParam("description") String description) {
        try {
            // Crea un DTO con los parámetros actualizados
            ProgramDto programDto = new ProgramDto(null, code, name, description);
    
            // Actualiza el programa y su archivo asociado
            String response = programService.updateProgram(programDto, zipInputStream);
            
            // Devuelve respuesta exitosa
            return Response.ok(response).build();
        } catch (Exception e) {
            // Manejo de errores centralizado
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint para eliminar un programa por su código.
     *
     * @param code Código único del programa a eliminar
     * @return Respuesta con el resultado de la operación
     */
    @DELETE
    @Path("/{code}")
    public Response deleteProgram(@PathParam("code") String code) {
        try {
            // Crea un DTO con el código recibido para eliminar el programa
            String response = programService.deleteProgram(new ProgramDto(0, code, "", ""));
            
            // Devuelve respuesta exitosa
            return Response.ok(response).build();
        } catch (Exception e) {
            // Manejo de errores centralizado
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}