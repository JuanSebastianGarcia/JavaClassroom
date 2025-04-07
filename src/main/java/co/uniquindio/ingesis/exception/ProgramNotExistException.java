package co.uniquindio.ingesis.exception;

public class ProgramNotExistException extends Exception {
    public ProgramNotExistException() {
        super("The program does not exist.");
    }
}

