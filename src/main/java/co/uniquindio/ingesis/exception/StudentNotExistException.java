package co.uniquindio.ingesis.exception;

/**
 * Custom runtime exception thrown when the specified student does not exist.
 */
public class StudentNotExistException extends RuntimeException {

    /**
     * Constructs a new StudentNotExistException with a default error message.
     */
    public StudentNotExistException() {
        super("The student does not exist");
    }
}
