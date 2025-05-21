package co.uniquindio.ingesis.exception;

/**
 * Custom exception thrown when the provided password is incorrect.
 */
public class PasswordIncorrectException extends Exception {

    /**
     * Constructs a new PasswordIncorrectException with a default error message.
     */
    public PasswordIncorrectException() {
        super("The password is incorrect");
    }
}
