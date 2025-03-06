package co.uniquindio.ingesis.exception;

public class TeacherExistException extends RuntimeException {
    public TeacherExistException() {
        super("Teacher already exists");
    }
}