package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.programResource.ProgramDto;
import co.uniquindio.ingesis.service.interfaces.ProgramServiceInterface;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;

@Path("/program")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll  // Permite acceso sin autenticación
public class ProgramResource {

    private final ProgramServiceInterface programService;

    public ProgramResource(ProgramServiceInterface programService) {
        this.programService = programService;
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadProgram(
            @FormParam("file") InputStream zipInputStream,
            @FormParam("code") String code,
            @FormParam("name") String name,
            @FormParam("description") String description) {
        try {

            // Crea un ProgramDto con los parámetros recibidos
        ProgramDto programDto = new ProgramDto(null, code, name, description);
            // Procesar el archivo ZIP y los datos del programa
            String response = programService.addProgram(programDto, zipInputStream);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{code}")
    public Response getProgram(@PathParam("code") String code) {
        try {
            ProgramDto program = programService.getProgram(new ProgramDto(0, code, "", ""));
            return Response.ok(program).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/update/{code}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateProgram(
            @PathParam("code") String code,
            @FormParam("file") InputStream zipInputStream,
            @FormParam("name") String name,
            @FormParam("description") String description) {
        try {
            // Crea un ProgramDto con los parámetros recibidos
            ProgramDto programDto = new ProgramDto(null, code, name, description);
    
            // Llama al servicio para actualizar el programa y el archivo
            String response = programService.updateProgram(programDto, zipInputStream);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{code}")
    public Response deleteProgram(@PathParam("code") String code) {
        try {
            String response = programService.deleteProgram(new ProgramDto(0, code, "", ""));
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}