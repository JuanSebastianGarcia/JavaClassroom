package co.uniquindio.ingesis.resource;

import co.uniquindio.ingesis.dto.studentResource.StudentDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
//import jakarta.ws.rs.GET;
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


    /*
     * Constructor with dependency injectionm
     */



    public Response addStudent(@Valid StudentDto studentRegisterDto){
        /*
         * this controller manage the creation of a new student
        */
        try{

            String response = "";

            return Response.status(Response.Status.CREATED).entity(response).build();
        }
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        

    }





}
