package co.uniquindio.ingesis.service.interfaces;

/**
 * Service interface responsible for executing programs.
 */
public interface ExecutionServiceInterface {

    /**
     * Executes a program located in the specified folder.
     *
     * @param folderName the name of the folder where the program files are located
     * @return the output or result of the program execution as a String
     */
    String executeProgram(String folderName);
}
