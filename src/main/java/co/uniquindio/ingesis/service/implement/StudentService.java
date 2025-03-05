package co.uniquindio.ingesis.service.implement;

import java.util.Optional;

import com.google.inject.Inject;

import co.uniquindio.ingesis.dto.studentResource.StudentDto;
import co.uniquindio.ingesis.exception.StudentExistException;
import co.uniquindio.ingesis.exception.StudentNotExistException;
import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.repository.StudentRepository;
import co.uniquindio.ingesis.service.interf.StudentServiceInterface;
import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

/*
 * this service is responsible for the process to manage students
 */
@ApplicationScoped 
public class StudentService implements StudentServiceInterface{


    /*
     * student's repository
     */
    @Inject
    private StudentRepository studentRepository;



    /*
     * this method add a new student and validate
     */
    @Override
    public String addStudent(StudentDto studentDto)throws StudentExistException {
        
        Student new_student = buildStudentFromDto(studentDto);

        //search student
        Optional<Student> student_exist = studentRepository.findByCedula(new_student.getDocument());

        // validate if the student already exist
        if(student_exist.get() != null){
            throw new StudentExistException();
        }

        //add student
        studentRepository.persist(new_student);


        return "the student has been created";
    }

    
    
    /*
     * this method search a student by document
     */
    @Override   
    public StudentDto getStudent(StudentDto studentDto) {

        
        //search student
        Optional<Student> student = studentRepository.findByCedula(studentDto.cedula());

        //validate if student dont exist
        if (student.get() ==null){
            throw new StudentNotExistException();
        }

        //build student dto
        studentDto = buildDtoFromStudent(student.get());

        return studentDto;
    }

    
    
    
    @Override
    public String deleteStuddent(StudentDto studentDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteStuddent'");
    }




    @Override
    public String updadateStudent(StudentDto studentDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updadateStudent'");
    }





    /*
     * this method build a student from student dto
     */
    private Student buildStudentFromDto(StudentDto studentDto){

        String password_hash = hashPassword(studentDto.password());

        //generate a student
        return new Student(0,studentDto.cedula(),studentDto.name(),studentDto.email(),password_hash);
    }


    /*
     * this method apply a hash to the password
     */
    private String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }


    /*
     * this method build a studentDto from student
     */
    private StudentDto buildDtoFromStudent(Student student){
        return new StudentDto(student.getId(), student.getDocument(), student.getName(), student.getEmail(), "");
    } 


}
