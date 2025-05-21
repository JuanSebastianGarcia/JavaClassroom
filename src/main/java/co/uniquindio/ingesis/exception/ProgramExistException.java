package co.uniquindio.ingesis.exception;

/**
 * Custom exception thrown when attempting to create a program that already
 * exists.
 */
public class ProgramExistException extends Exception {

    /**
     * Constructs a new ProgramExistException with a default error message.
     */
    public ProgramExistException() {
        super("The program already exists.");
    }
}
