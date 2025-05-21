package co.uniquindio.ingesis.service.implement;

import co.uniquindio.ingesis.dto.ExampleResource.ExampleDto;
import co.uniquindio.ingesis.model.Example;
import co.uniquindio.ingesis.model.ExampleAssignment;
import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.repository.ExampleAssignmentRepository;
import co.uniquindio.ingesis.repository.ExampleRepository;
import co.uniquindio.ingesis.repository.StudentRepository;
import co.uniquindio.ingesis.service.interfaces.ExampleServiceInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jakarta.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

@ApplicationScoped
public class ExampleService implements ExampleServiceInterface {

    private final ExampleRepository exampleRepository;

    @Inject
    ExampleAssignmentRepository exampleAssignmentRepository;

    @Inject
    StudentRepository studentRepository;

    public ExampleService(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    /**
     * Adds a new example and extracts its ZIP content to a folder.
     *
     * @param exampleDto     Data transfer object with example info
     * @param zipInputStream InputStream of the ZIP file with example files
     * @return Success message
     * @throws IOException If an I/O error occurs during unzip
     */
    @Override
    @Transactional
    public String addExample(ExampleDto exampleDto, InputStream zipInputStream) throws IOException {
        Example example = buildExampleFromDto(exampleDto);

        // Persist example in the database
        exampleRepository.persist(example);

        // Create folder to unzip files
        String exampleDirectory = "ejemplos/" + example.getId();
        File directory = new File(exampleDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Unzip the input ZIP stream into the created folder
        unzip(zipInputStream, directory.getAbsolutePath());

        return "Example created successfully!";
    }

    /**
     * Unzips a ZIP file input stream into the specified directory.
     *
     * @param zipInputStream ZIP file input stream
     * @param outputDir      Directory to extract files to
     * @throws IOException If an I/O error occurs
     */
    private void unzip(InputStream zipInputStream, String outputDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // Create parent directories if not exist
                    file.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * Retrieves an example by its ID wrapped in a DTO.
     *
     * @param requestDto DTO containing example ID
     * @return Example DTO
     */
    @Transactional
    public ExampleDto getExample(ExampleDto requestDto) {
        Example example = exampleRepository.findByIdOptional(Long.valueOf(requestDto.id()))
                .orElseThrow(() -> new NotFoundException("Example not found"));
        return buildDtoFromExample(example);
    }

    /**
     * Updates an existing example and optionally replaces its ZIP files.
     *
     * @param exampleDto     DTO with updated data
     * @param zipInputStream New ZIP input stream (optional)
     * @return Success message
     * @throws IOException If an I/O error occurs during unzip or file deletion
     */
    @Override
    @Transactional
    public String updateExample(ExampleDto exampleDto, InputStream zipInputStream) throws IOException {
        Example example = exampleRepository.findByIdOptional(exampleDto.id().longValue())
                .orElseThrow(() -> new NotFoundException("Example not found"));

        // Update example fields
        example.setTitle(exampleDto.title());
        example.setContent(exampleDto.content());
        example.setCategory(exampleDto.category());
        example.setDifficulty(exampleDto.difficulty());
        exampleRepository.persist(example);

        // If a ZIP file is provided, replace existing files
        if (zipInputStream != null) {
            String exampleDirectory = "ejemplos/" + example.getId();
            File directory = new File(exampleDirectory);

            // Delete existing directory if exists
            if (directory.exists()) {
                deleteDirectory(directory);
            }

            // Create directory again
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Extract new ZIP files
            unzip(zipInputStream, directory.getAbsolutePath());
        }

        return "Example and files updated successfully.";
    }

    /**
     * Deletes an example and its corresponding files.
     *
     * @param id The ID of the example to delete
     * @return Success or error message
     */
    @Override
    @Transactional
    public String deleteExample(Integer id) {
        Example example = exampleRepository.findByIdOptional(id.longValue())
                .orElseThrow(() -> new NotFoundException("Example not found"));

        // Delete from database
        exampleRepository.delete(example);

        // Path to example folder
        String examplePath = "ejemplos/" + example.getId();
        Path exampleDirectory = Paths.get(examplePath);

        try {
            if (Files.exists(exampleDirectory)) {
                // Walk the file tree and delete files and folders recursively
                Files.walkFileTree(exampleDirectory, new SimpleFileVisitor<Path>() {
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
            return "Example deleted successfully.";
        } catch (IOException e) {
            return "Error deleting example directory: " + e.getMessage();
        }
    }

    /**
     * Recursively deletes a directory and its contents.
     *
     * @param directory Directory to delete
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
     * Lists all examples as DTOs.
     *
     * @return List of ExampleDto objects
     */
    @Transactional
    @Override
    public List<ExampleDto> listExamples() {
        return exampleRepository.listAll()
                .stream()
                .map(this::buildDtoFromExample)
                .collect(Collectors.toList());
    }

    /**
     * Converts a DTO to an Example entity.
     */
    private Example buildExampleFromDto(ExampleDto dto) {
        return new Example(dto.id(), dto.title(), dto.content(), dto.category(), dto.difficulty(),
                dto.cedulaProfesor());
    }

    /**
     * Converts an Example entity to a DTO.
     */
    private ExampleDto buildDtoFromExample(Example example) {
        return new ExampleDto(example.getId(), example.getTitle(), example.getContent(), example.getCategory(),
                example.getDifficulty(), example.getCedulaProfesor());
    }

    /**
     * Assigns an example to a list of students by their document numbers (cedulas).
     *
     * @param exampleId          ID of the example to assign
     * @param cedulasEstudiantes List of student document numbers
     * @return Success message
     */
    @Transactional
    public String assignExampleToStudents(Integer exampleId, List<String> cedulasEstudiantes) {
        Example example = exampleRepository.findByIdOptional(exampleId.longValue())
                .orElseThrow(() -> new NotFoundException("Example not found"));

        for (String cedula : cedulasEstudiantes) {
            Student student = studentRepository.findByCedula(cedula)
                    .orElseThrow(() -> new NotFoundException("Student with cedula " + cedula + " not found"));
            ExampleAssignment assignedExample = new ExampleAssignment();
            assignedExample.setExample(example);
            assignedExample.setCedulaEstudiante(student.getDocument());
            exampleAssignmentRepository.persist(assignedExample);
        }

        return "Example assigned to students successfully.";
    }

    /**
     * Gets a list of student document numbers assigned to a specific example.
     *
     * @param exampleId ID of the example
     * @return List of student document numbers
     */
    @Override
    public List<String> getStudentsAssignedToExample(Integer exampleId) {
        return exampleAssignmentRepository.findCedulasByExampleId(exampleId);
    }
}
