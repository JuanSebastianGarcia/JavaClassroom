package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.teacherResource.TeacherDto;
import co.uniquindio.ingesis.service.interf.TeacherServiceInterface;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/teacher")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeacherResource {

    /*
     * Service to access teacher
     */
    private final TeacherServiceInterface teacherService;

    /*
     * Constructor with dependency injection
     */
    public TeacherResource(TeacherServiceInterface teacherService) {
        this.teacherService = teacherService;
    }


    /*
     * Controller to add a new teacher
     */
    @POST
    public Response addTeacher(@Valid TeacherDto teacherDto) {
        try {
            String response = teacherService.addTeacher(teacherDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }                       
    }

    /*
     * Controller to get a teacher by document
     */
    @GET
    @Path("/{cedula}")
    public Response getTeacher(@PathParam("cedula") String cedula) {
        try {
            TeacherDto teacherDto = new TeacherDto(0, cedula, "", "", "");
            TeacherDto teacher = teacherService.getTeacher(teacherDto);
            return Response.ok(teacher).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


    /*
     * Controller to update a teacher's information
     */
    @PUT
    public Response updateTeacher(@Valid TeacherDto teacherDto) {
        try {
            String response = teacherService.updateTeacher(teacherDto);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /*
     * Controller to delete a teacher
     */
    @DELETE
    @Path("/{cedula}")
    public Response deleteTeacher(@PathParam("cedula") String cedula) {
        try {
            TeacherDto teacherDto = new TeacherDto(0, cedula, "", "", "");
            String response = teacherService.deleteTeacher(teacherDto);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

}
