package co.uniquindio.ingesis.exception;

public class RoleMismatchException extends RuntimeException {
    public RoleMismatchException() {
        super("El rol proporcionado no coincide con el rol registrado del usuario.");
    }
}