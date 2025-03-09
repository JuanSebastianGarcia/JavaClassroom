package co.uniquindio.ingesis.service.interfaces;

public interface VerificationServiceInterface {

    /**
     * Sends an email to the user to verify their account.
     * 
     * @param email The email to send the verification email to.
     * @return The verification code sent to the user.
     */
    String sendVerificationEmail(String email);


    /**
     * Verifies the user's account.
     * 
     * @param email The email of the user to verify.
     * @param verificationCode The verification code sent to the user.
     * @return A message indicating the result of the verification.
     */
    void verifyAccount(String email, String verificationCode);
    
}
