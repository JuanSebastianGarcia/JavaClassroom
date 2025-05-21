package co.uniquindio.ingesis.exception;

/**
 * Custom runtime exception thrown when attempting to create a student that
 * already exists.
 */
public class StudentExistException extends RuntimeException {

    /**
     * Constructs a new StudentExistException with a default error message.
     */
    public StudentExistException() {
        super("The student already exists");
    }
}
