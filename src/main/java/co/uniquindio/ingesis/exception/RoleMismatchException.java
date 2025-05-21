package co.uniquindio.ingesis.exception;

/**
 * Custom runtime exception thrown when the provided role does not match
 * the user's registered role.
 */
public class RoleMismatchException extends RuntimeException {

    /**
     * Constructs a new RoleMismatchException with a default error message.
     */
    public RoleMismatchException() {
        super("The provided role does not match the user's registered role.");
    }
}
