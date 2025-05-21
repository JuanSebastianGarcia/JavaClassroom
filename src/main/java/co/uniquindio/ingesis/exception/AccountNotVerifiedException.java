package co.uniquindio.ingesis.exception;

/**
 * Custom exception thrown when an account has not been verified.
 */
public class AccountNotVerifiedException extends Exception {

    /**
     * Constructs a new AccountNotVerifiedException with a default error message.
     */
    public AccountNotVerifiedException() {
        super("The account is not verified");
    }

}
