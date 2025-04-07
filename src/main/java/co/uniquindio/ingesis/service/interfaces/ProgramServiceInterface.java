package co.uniquindio.ingesis.service.interfaces;

import co.uniquindio.ingesis.dto.programResource.ProgramDto;
import co.uniquindio.ingesis.exception.ProgramExistException;
import co.uniquindio.ingesis.exception.ProgramNotExistException;

import java.io.IOException;
import java.io.InputStream;

public interface ProgramServiceInterface {

    String addProgram(ProgramDto programDto, InputStream zipInputStream) throws ProgramExistException, IOException;

    ProgramDto getProgram(ProgramDto programDto) throws ProgramNotExistException;

    // Nueva sobrecarga del método updateProgram que incluye un InputStream para actualizar el archivo
    String updateProgram(ProgramDto programDto, InputStream zipInputStream) throws ProgramNotExistException, IOException;

    String deleteProgram(ProgramDto programDto) throws ProgramNotExistException;
}