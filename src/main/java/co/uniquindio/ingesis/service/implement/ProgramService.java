package co.uniquindio.ingesis.service.implement;

import co.uniquindio.ingesis.dto.programResource.ProgramDto;
import co.uniquindio.ingesis.exception.ProgramExistException;
import co.uniquindio.ingesis.exception.ProgramNotExistException;
import co.uniquindio.ingesis.model.Program;
import co.uniquindio.ingesis.repository.ProgramRepository;
import co.uniquindio.ingesis.service.interfaces.ProgramServiceInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.*;

/**
 * Implementation of the Program Service Interface that handles program management operations.
 * This service manages the programs' data and their associated files in the file system.
 */
@ApplicationScoped
public class ProgramService implements ProgramServiceInterface {

    /**
     * Repository for program entity operations.
     */
    private final ProgramRepository programRepository;

    /**
     * Constructor with dependency injection for the program repository.
     *
     * @param programRepository Repository for program operations
     */
    @Inject
    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    /**
     * Adds a new program to the system and extracts its ZIP file content to the appropriate directory.
     *
     * @param programDto      DTO containing program information
     * @param zipInputStream  Input stream of the ZIP file with program content
     * @return                Success message confirming program creation
     * @throws ProgramExistException If a program with the same code already exists
     * @throws IOException           If there's an error processing the ZIP file
     */
    @Override
    @Transactional
    public String addProgram(ProgramDto programDto, InputStream zipInputStream) throws ProgramExistException, IOException {
       
        Program newProgram = buildProgramFromDto(programDto);

        // Check if program already exists
        if (programRepository.findByCode(newProgram.getCode()).isPresent()) {
            throw new ProgramExistException();
        }

        // Define the program directory
        String programDirectory = "programs/" + newProgram.getCode();
        File directory = new File(programDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Extract the zip file
        extractZipFile(zipInputStream, directory);

        Program managedProgram = programRepository.getEntityManager().merge(newProgram);
        programRepository.persistAndFlush(managedProgram);

        return "Program created successfully with source code.";
    }

    /**
     * Retrieves program information by its code.
     *
     * @param programDto  DTO containing the code of the program to retrieve
     * @return            DTO with complete program information
     * @throws ProgramNotExistException If no program with the specified code exists
     */
    @Override
    public ProgramDto getProgram(ProgramDto programDto) throws ProgramNotExistException {
        Program program = programRepository.findByCode(programDto.code())
                .orElseThrow(ProgramNotExistException::new);
        return buildDtoFromProgram(program);
    }

    /**
     * Updates an existing program's information and optionally its associated files.
     *
     * @param programDto      DTO containing updated program information
     * @param zipInputStream  Input stream of the updated ZIP file (can be null if no file update)
     * @return                Success message confirming program update
     * @throws ProgramNotExistException If no program with the specified code exists
     * @throws IOException              If there's an error processing the ZIP file
     */
    @Override
    @Transactional
    public String updateProgram(ProgramDto programDto, InputStream zipInputStream) throws ProgramNotExistException, IOException {
        Program program = programRepository.findByCode(programDto.code())
                .orElseThrow(ProgramNotExistException::new);

        // Update program data
        program.setName(programDto.name());
        program.setDescription(programDto.description());
        programRepository.persist(program);

        // If a ZIP file is provided, update the program files
        if (zipInputStream != null) {
            String programDirectory = "programs/" + program.getCode();
            File directory = new File(programDirectory);

            // Delete existing directory before extracting new files
            if (directory.exists()) {
                deleteDirectory(directory);
            }
            
            // Create the directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Extract the new ZIP file
            extractZipFile(zipInputStream, directory);
        }

        return "Program and file updated successfully.";
    }

    /**
     * Deletes a program from the database and its associated files from the file system.
     *
     * @param programDto  DTO containing the code of the program to delete
     * @return            Success message confirming program deletion
     * @throws ProgramNotExistException If no program with the specified code exists
     */
    @Override
    @Transactional
    public String deleteProgram(ProgramDto programDto) throws ProgramNotExistException {
        // Find the program in the database
        Program program = programRepository.findByCode(programDto.code())
                .orElseThrow(ProgramNotExistException::new);

        // Delete the program from the database
        programRepository.delete(program);

        // Path to the program directory
        String programPath = "C:\\Users\\brahi\\Documents\\ProyectoApi\\programs\\" + programDto.code();
        Path programDirectory = Paths.get(programPath);

        try {
            if (Files.exists(programDirectory)) {
                Files.walkFileTree(programDirectory, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }

            return "Program deleted successfully.";
        } catch (IOException e) {
            return "Error deleting program directory: " + e.getMessage();
        }
    }

    /**
     * Recursively deletes a directory and all its contents.
     *
     * @param directory  The directory to delete
     * @throws IOException If an I/O error occurs
     */
    private void deleteDirectory(File directory) throws IOException {
        Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Converts a ProgramDto to a Program entity.
     *
     * @param programDto  DTO containing program information
     * @return            Program entity
     */
    private Program buildProgramFromDto(ProgramDto programDto) {
        return new Program(programDto.id(), programDto.code(), programDto.name(), programDto.description(), "");
    }

    /**
     * Converts a Program entity to a ProgramDto.
     *
     * @param program  Program entity
     * @return         DTO with program information
     */
    private ProgramDto buildDtoFromProgram(Program program) {
        return new ProgramDto(program.getId(), program.getCode(), program.getName(), program.getDescription());
    }

    /**
     * Extracts a ZIP file to the specified directory.
     *
     * @param zipInputStream  Input stream of the ZIP file
     * @param directory       Directory where files will be extracted
     * @throws IOException    If an I/O error occurs during extraction
     */
    private void extractZipFile(InputStream zipInputStream, File directory) throws IOException {
        System.out.println(" Descomprimiendo ZIP en: " + directory.getAbsolutePath());
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File entryFile = new File(directory, entry.getName());
                System.out.println(" Extrayendo: " + entryFile.getAbsolutePath());
                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(entryFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}