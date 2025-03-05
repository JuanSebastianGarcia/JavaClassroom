package co.uniquindio.ingesis.exception;

public class StudentExistException extends RuntimeException {

    public StudentExistException(){
        super("the student already exist");
    }
}
