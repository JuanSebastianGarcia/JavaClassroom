package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.TeacherConsult;
import co.uniquindio.ingesis.dto.ResponseDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/teacher")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherResource {

    @GET
    @Path("/{email}")
    public Response getTeacher(@PathParam("email") String email) {
        try {
            // Simulación de búsqueda del profesor
            TeacherConsult teacher = new TeacherConsult(email);
            var response = new ResponseDto<>(false, teacher);
            return Response.ok(response).build();
        } catch (Exception e) {
            var errorResponse = new ResponseDto<>(true, "Error al obtener el profesor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

    @DELETE
    @Path("/{email}")
    public Response deleteTeacher(@PathParam("email") String email) {
        try {
            // Simulación de eliminación del profesor
            var response = new ResponseDto<>(false, "El profesor con email " + email + " ha sido eliminado.");
            return Response.ok(response).build();
        } catch (Exception e) {
            var errorResponse = new ResponseDto<>(true, "Error al eliminar el profesor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }

}
