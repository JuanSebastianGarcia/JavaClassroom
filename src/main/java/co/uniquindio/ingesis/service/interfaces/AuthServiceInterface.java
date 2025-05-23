package co.uniquindio.ingesis.service.interfaces;

import co.uniquindio.ingesis.dto.login.LoginDto;
import co.uniquindio.ingesis.dto.login.TokenResponseDto;
import co.uniquindio.ingesis.exception.AccountNotVerifiedException;
import co.uniquindio.ingesis.exception.PasswordIncorrectException;
import co.uniquindio.ingesis.exception.RoleUnknownException;

/**
 * Service interface for handling user authentication.
 * 
 * This interface defines methods related to user login.
 */
public interface AuthServiceInterface {

    /**
     * Authenticates a user based on the provided login details.
     * 
     * @param loginDto The user's login information (email and password).
     * @return A string containing a token or authentication message.
     * @throws RoleUnknownException
     * @throws PasswordIncorrectException
     * @throws AccountNotVerifiedException
     */
    TokenResponseDto loginUser(LoginDto loginDto)
            throws RoleUnknownException, PasswordIncorrectException, AccountNotVerifiedException;
}
