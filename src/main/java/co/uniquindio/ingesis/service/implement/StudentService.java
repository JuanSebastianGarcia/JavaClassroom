package co.uniquindio.ingesis.service.implement;

import java.util.Optional;


import co.uniquindio.ingesis.dto.studentResource.StudentDto;
import co.uniquindio.ingesis.dto.studentResource.StudentUpdateDto;
import co.uniquindio.ingesis.exception.PasswordIncorrextException;
import co.uniquindio.ingesis.exception.StudentExistException;
import co.uniquindio.ingesis.exception.StudentNotExistException;
import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.repository.StudentRepository;
import co.uniquindio.ingesis.service.interfaces.StudentServiceInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

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
    @Transactional
    public String addStudent(StudentDto studentDto)throws StudentExistException {
        
        Student new_student = buildStudentFromDto(studentDto);

        //search student
        Optional<Student> student_exist = studentRepository.findByCedula(new_student.getDocument());

        // validate if the student already exist
        if(student_exist.isPresent()){
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
    @Transactional
    public StudentDto getStudent(String email) {

        
        //search student
        Optional<Student> student = studentRepository.findByEmail(email);

        //validate if student dont exist
        if (student.isEmpty()){
            throw new StudentNotExistException();
        }

        //build student dto
        StudentDto studentDto = buildDtoFromStudent(student.get());

        return studentDto;
    }

    
    
    
    /*
     * this method remove a student 
     */
    @Transactional
    @Override
    public String deleteStuddent(String email,StudentDto studentDto) throws PasswordIncorrextException, StudentNotExistException {

        //search student
        Optional<Student> student = studentRepository.findByEmail(email);

        //validate if the student existe
        if (student.isEmpty()) {
            
            throw new StudentNotExistException();
        }


        //validate student
        if (!BCrypt.checkpw(studentDto.password(), student.get().getPassword())) {

            throw new PasswordIncorrextException();
        }


        //update student
        studentRepository.delete(student.get());

        return "the student has been deleteadd ";
    }



    


    /*
     * this method update the student
     */
    @Override
    @Transactional
    public String updadateStudent(int id,StudentUpdateDto studentUpdateDto) throws PasswordIncorrextException {
        
        //search student
        Student student = studentRepository.findById((long)id);

        //validate if the student existe
        if (student == null) {
            
            throw new StudentNotExistException();
        }


        //validate studentp
        if (!BCrypt.checkpw(studentUpdateDto.password(), student.getPassword())) {
        
            throw new PasswordIncorrextException();
        }

        //update password
        if (!(studentUpdateDto.new_password().equals("") || studentUpdateDto.new_password() == null)) {
            
            String new_password = hashPassword(studentUpdateDto.new_password());

            student.setPassword(new_password);
        }

       
        //update email
        if (!(studentUpdateDto.email().equals("") || studentUpdateDto.email() == null)) {
            
            student.setEmail(studentUpdateDto.email());
        }

        //update name
        if (!(studentUpdateDto.nombre().equals("") || studentUpdateDto.nombre() == null)) {
            
            student.setName(studentUpdateDto.nombre());
        }

    
        //update student
        studentRepository.persist(student);

        return "the student has been update";
    }





    /*
     * this method build a student from student dto
     */
    private Student buildStudentFromDto(StudentDto studentDto){

        String password_hash = hashPassword(studentDto.password());

        //generate a student
        return new Student(studentDto.id(),studentDto.cedula(),studentDto.name(),studentDto.email(),password_hash);
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
