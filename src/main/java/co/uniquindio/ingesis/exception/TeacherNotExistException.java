package co.uniquindio.ingesis.exception;


public class TeacherNotExistException extends RuntimeException {
    public TeacherNotExistException() {
        super("Teacher does not exist");
    }
}