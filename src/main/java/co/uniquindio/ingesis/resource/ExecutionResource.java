package co.uniquindio.ingesis.resource;


import co.uniquindio.ingesis.service.interfaces.ExecutionServiceInterface;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso REST para la ejecución de programas Java.
 */
@Path("/api/program-execution")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExecutionResource {

    @Inject
    ExecutionServiceInterface executionService;

    /**
     * Ejecuta un programa Java almacenado en una carpeta específica.
     *
     * @param folderName Nombre del directorio dentro de "programs/" donde está el archivo Java.
     * @return Salida de la ejecución (stdout o errores).
     */
    @POST
    @Path("/execute/{folderName}")
    @RolesAllowed({"student"})
    public Response executeProgram(@PathParam("folderName") String folderName) {
        String result = executionService.executeProgram(folderName);
        return Response.ok().entity(result).build();
    }
}
