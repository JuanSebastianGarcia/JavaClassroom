package co.uniquindio.ingesis.service.interfaces;

import co.uniquindio.ingesis.dto.programResource.ProgramDto;
import co.uniquindio.ingesis.exception.ProgramExistException;
import co.uniquindio.ingesis.exception.ProgramNotExistException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Interface that defines the services for program management.
 * Provides CRUD operations (Create, Read, Update, Delete) for programs.
 */
public interface ProgramServiceInterface {

    /**
     * Adds a new program to the system.
     *
     * @param programDto     DTO object with the program information to create
     * @param zipInputStream Stream of the ZIP file with the program content
     * @return Confirmation message of the creation
     * @throws ProgramExistException If a program with the same code already exists
     * @throws IOException           If an error occurs during file reading or
     *                               processing
     */
    String addProgram(ProgramDto programDto, InputStream zipInputStream) throws ProgramExistException, IOException;

    /**
     * Gets the information of a specific program.
     *
     * @param programDto DTO object with the code of the program to query
     * @return DTO object with the complete program information
     * @throws ProgramNotExistException If no program exists with the specified code
     */
    ProgramDto getProgram(ProgramDto programDto) throws ProgramNotExistException;

    /**
     * Updates the information of an existing program, including its associated
     * file.
     *
     * @param programDto     DTO object with the updated program information
     * @param zipInputStream Stream of the ZIP file with the updated program content
     * @return Confirmation message of the update
     * @throws ProgramNotExistException If no program exists with the specified code
     * @throws IOException              If an error occurs during file reading or
     *                                  processing
     */
    String updateProgram(ProgramDto programDto, InputStream zipInputStream)
            throws ProgramNotExistException, IOException;

    /**
     * Deletes a program from the system.
     *
     * @param programDto DTO object with the code of the program to delete
     * @return Confirmation message of the deletion
     * @throws ProgramNotExistException If no program exists with the specified code
     */
    String deleteProgram(ProgramDto programDto) throws ProgramNotExistException, IOException;

    List<ProgramDto> listPrograms();

    // Nuevo método para listar programas compartidos
    List<ProgramDto> listSharedPrograms();

}