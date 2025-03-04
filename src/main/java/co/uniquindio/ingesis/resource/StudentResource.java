package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.studentResource.StudentRegisterDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
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
    private final StudenService studentService;


    /*
     * Constructor with dependency injectionm
     */
    public StudentResource(StudenService studentService){
        this.studentService = studentService;
    }



    public Response addStudent(@Valid StudentRegisterDto studentRegisterDto){
        /*
         * this controller manage the creation of a new student
        */
        try{

            String respone = StudenService.

            return Response.status(Response.Status.CREATED).entity(respone).build();
        }
        catch(){

        }
        

    }





}
