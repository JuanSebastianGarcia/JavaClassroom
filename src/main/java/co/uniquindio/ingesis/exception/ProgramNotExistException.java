package co.uniquindio.ingesis.exception;

/**
 * Custom exception thrown when attempting to access a program that does not
 * exist.
 */
public class ProgramNotExistException extends Exception {

    /**
     * Constructs a new ProgramNotExistException with a default error message.
     */
    public ProgramNotExistException() {
        super("The program does not exist.");
    }
}
