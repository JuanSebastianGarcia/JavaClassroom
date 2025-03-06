package co.uniquindio.ingesis.resource;


import co.uniquindio.ingesis.dto.studentResource.StudentDto;
import co.uniquindio.ingesis.dto.studentResource.StudentUpdateDto;
import co.uniquindio.ingesis.exception.PasswordIncorrextException;
import co.uniquindio.ingesis.exception.StudentExistException;
import co.uniquindio.ingesis.exception.StudentNotExistException;
import co.uniquindio.ingesis.service.interf.StudentServiceInterface;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/student")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {


    /*
     * service to access student
     */
    //private final StudenService studentService;
    private final StudentServiceInterface studentService;




    /*
     * Constructor with dependency injectionm
     */
    public StudentResource(StudentServiceInterface studentService){
        this.studentService = studentService;
    }




    /*
     * this method recibe a request to add student 
     */
    @POST
    public Response addStudent(@Valid StudentDto studentRegisterDto){
        /*
         * this controller manage the creation of a new student
        */
        try{

            String response = this.studentService.addStudent(studentRegisterDto);

            return Response.status(Response.Status.CREATED).entity(response).build();
        }
        catch(StudentExistException e){
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    
    }



    /*
     * this method search a student
     */
    @GET
    public Response getStudent(StudentDto studentDto){

        try{
            StudentDto student = studentService.getStudent(studentDto);

            return Response.status(Response.Status.OK).entity(student).build();
        }
        catch(StudentExistException e){
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }



    /*
     * this method update a student
     */
    @PUT
    public Response updateStudent(StudentUpdateDto studentUpdateDto){

        try{
            String student = studentService.updadateStudent(studentUpdateDto);

            return Response.status(Response.Status.CREATED).entity(student).build();
        }
        catch(PasswordIncorrextException e){
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
        catch(StudentNotExistException e){
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

    }



    /*
     * this method delete a student
     */
    @DELETE
    public Response deleteStudent(StudentDto studentDto){

        try{

            String respuesta = studentService.deleteStuddent(studentDto);

            return Response.status(Response.Status.OK).entity(respuesta).build();

        }catch(PasswordIncorrextException e){
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
        catch(StudentNotExistException e){
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }

    }




}
