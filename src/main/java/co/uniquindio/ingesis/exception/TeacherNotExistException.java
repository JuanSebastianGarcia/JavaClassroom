package co.uniquindio.ingesis.exception;

/**
 * Custom runtime exception thrown when the specified teacher does not exist.
 */
public class TeacherNotExistException extends RuntimeException {

    /**
     * Constructs a new TeacherNotExistException with a default error message.
     */
    public TeacherNotExistException() {
        super("Teacher does not exist");
    }
}
