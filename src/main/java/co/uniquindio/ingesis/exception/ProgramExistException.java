package co.uniquindio.ingesis.exception;

public class ProgramExistException extends Exception {
    public ProgramExistException() {
        super("The program already exists.");
    }
}