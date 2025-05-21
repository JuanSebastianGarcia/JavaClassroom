package co.uniquindio.ingesis.exception;

/**
 * Custom exception thrown when an unknown role is encountered.
 */
public class RoleUnknownException extends Exception {

    /**
     * Constructs a new RoleUnknownException with a default error message.
     */
    public RoleUnknownException() {
        super("The role was unknown");
    }
}
