package co.uniquindio.ingesis.exception;

public class PasswordIncorrectException extends Exception{
    
    public PasswordIncorrectException(){
        super("the password is incorrect");
    }
}
