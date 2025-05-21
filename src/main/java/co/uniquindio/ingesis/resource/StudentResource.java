package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.studentResource.GetAllDto;
import co.uniquindio.ingesis.dto.studentResource.StudentDto;
import co.uniquindio.ingesis.dto.studentResource.StudentUpdateDto;
import co.uniquindio.ingesis.exception.PasswordIncorrectException;
import co.uniquindio.ingesis.exception.StudentExistException;
import co.uniquindio.ingesis.exception.StudentNotExistException;
import co.uniquindio.ingesis.service.interfaces.StudentServiceInterface;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST API resource for managing student operations.
 * Provides endpoints to create, retrieve, update, and delete student records.
 */
@Path("/student")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {

    /**
     * Service interface for student management.
     */
    private final StudentServiceInterface studentService;

    /**
     * Constructor for dependency injection.
     *
     * @param studentService Service interface for handling student operations.
     */
    public StudentResource(StudentServiceInterface studentService) {
        this.studentService = studentService;
    }

    /**
     * Endpoint to add a new student.
     *
     * @param studentRegisterDto DTO containing student registration data.
     * @return HTTP response with status CREATED if successful, CONFLICT if the
     *         student already exists,
     *         or INTERNAL_SERVER_ERROR if an unexpected error occurs.
     */
    @POST
    public Response addStudent(@Valid StudentDto studentRegisterDto) {
        try {
            String response = this.studentService.addStudent(studentRegisterDto);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (StudentExistException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint to retrieve a student's details by email.
     *
     * @param email Email of the student to be retrieved.
     * @return HTTP response with student data if found, NOT_FOUND if the student
     *         does not exist,
     *         or INTERNAL_SERVER_ERROR if an unexpected error occurs.
     */
    @GET
    public Response getStudent(@QueryParam("email") String email) {
        try {
            StudentDto student = studentService.getStudent(email);
            return Response.status(Response.Status.OK).entity(student).build();
        } catch (StudentNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint to update a student's details.
     *
     * @param studentUpdateDto DTO containing updated student information.
     * @return HTTP response with status OK if updated successfully, FORBIDDEN
     *         if password is incorrect,
     *         NOT_FOUND if the student does not exist, or INTERNAL_SERVER_ERROR if
     *         an unexpected error occurs.
     */
    @PUT
    public Response updateStudent(@Valid StudentUpdateDto studentUpdateDto) {
        try {
            String student = studentService.updateStudent(studentUpdateDto);
            return Response.ok(student).build();
        } catch (StudentNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (PasswordIncorrectException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint to delete a student by email.
     *
     * @param email      Email of the student to be deleted.
     * @param studentDto DTO containing student authentication data.
     * @return HTTP response with status OK if deleted successfully, FORBIDDEN if
     *         password is incorrect,
     *         NOT_FOUND if the student does not exist, or INTERNAL_SERVER_ERROR if
     *         an unexpected error occurs.
     */
    @DELETE
    public Response deleteStudent(@QueryParam("email") String email, StudentDto studentDto) {
        try {
            String respuesta = studentService.deleteStuddent(email, studentDto);
            return Response.status(Response.Status.OK).entity(respuesta).build();
        } catch (PasswordIncorrectException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (StudentNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint to retrieve all students in a paginated format.
     *
     * @param page Page number to retrieve.
     * @return HTTP response with the list of students for the given page,
     *         or INTERNAL_SERVER_ERROR if an unexpected error occurs.
     */
    @GET
    @Path("/all")
    public Response getAllStudents(@QueryParam("page") int page) {
        try {
            GetAllDto getAllDto = new GetAllDto(page);
            return Response.status(Response.Status.OK)
                    .entity(studentService.getAllStudents(getAllDto))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
