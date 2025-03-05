package co.uniquindio.ingesis.exception;

public class StudentNotExistException extends RuntimeException{

    public StudentNotExistException(){
        super("the student don't exist");
    }

}
