package co.uniquindio.ingesis.exception;

/**
 * Custom runtime exception thrown when attempting to create a teacher that
 * already exists.
 */
public class TeacherExistException extends RuntimeException {

    /**
     * Constructs a new TeacherExistException with a default error message.
     */
    public TeacherExistException() {
        super("Teacher already exists");
    }
}
