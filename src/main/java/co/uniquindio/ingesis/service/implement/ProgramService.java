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

@ApplicationScoped
public class ProgramService implements ProgramServiceInterface {

    private final ProgramRepository programRepository;

    @Inject
    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

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

    @Override
    public ProgramDto getProgram(ProgramDto programDto) throws ProgramNotExistException {
        Program program = programRepository.findByCode(programDto.code())
                .orElseThrow(ProgramNotExistException::new);
        return buildDtoFromProgram(program);
    }

@Override
@Transactional
public String updateProgram(ProgramDto programDto, InputStream zipInputStream) throws ProgramNotExistException, IOException {
    Program program = programRepository.findByCode(programDto.code())
            .orElseThrow(ProgramNotExistException::new);

    // Actualizar los datos del programa
    program.setName(programDto.name());
    program.setDescription(programDto.description());
    programRepository.persist(program);

    // Si se proporciona un archivo ZIP, actualizamos el archivo en el directorio
    if (zipInputStream != null) {
        String programDirectory = "programs/" + program.getCode();
        File directory = new File(programDirectory);

        // Si ya existe un directorio, lo eliminamos antes de extraer el nuevo archivo
        if (directory.exists()) {
            deleteDirectory(directory);
        }
        
        // Crear el directorio si no existe
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Extraer el nuevo archivo ZIP
        extractZipFile(zipInputStream, directory);
    }

    return "Program and file updated successfully.";
}


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
    @Override
    @Transactional
public String deleteProgram(ProgramDto programDto) throws ProgramNotExistException {
    // Buscar el programa en la base de datos
    Program program = programRepository.findByCode(programDto.code())
            .orElseThrow(ProgramNotExistException::new);

    // Eliminar el programa de la base de datos
    programRepository.delete(program);

    // Ruta de la carpeta del programa
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
    private Program buildProgramFromDto(ProgramDto programDto) {
        return new Program(programDto.id(), programDto.code(), programDto.name(), programDto.description(), "");
    }

    private ProgramDto buildDtoFromProgram(Program program) {
        return new ProgramDto(program.getId(), program.getCode(), program.getName(), program.getDescription());
    }

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