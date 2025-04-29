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

    @Override
    @Transactional
    public String addExample(ExampleDto exampleDto, InputStream zipInputStream) throws IOException {
        Example example = buildExampleFromDto(exampleDto);

        // Persistir en la base de datos
        exampleRepository.persist(example);

        // Crear la carpeta donde se va a descomprimir el zip
        String exampleDirectory = "ejemplos/" + example.getId(); // O si quieres usar otro campo, cámbialo
        File directory = new File(exampleDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Descomprimir el zip
        unzip(zipInputStream, directory.getAbsolutePath());

        return "Example created successfully!";
    }

    /**
     * Método para descomprimir un archivo zip en un directorio específico
     */
    private void unzip(InputStream zipInputStream, String outputDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // Crear los directorios padre si no existen
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

    @Transactional
    public ExampleDto getExample(ExampleDto requestDto) {
        Example example = exampleRepository.findByIdOptional(Long.valueOf(requestDto.id()))
                .orElseThrow(() -> new NotFoundException("Example not found"));
        return buildDtoFromExample(example);
    }

    @Override
    @Transactional
    public String updateExample(ExampleDto exampleDto, InputStream zipInputStream) throws IOException {
        Example example = exampleRepository.findByIdOptional(exampleDto.id().longValue())
                .orElseThrow(() -> new NotFoundException("Example not found"));

        // Actualizar datos del ejemplo
        example.setTitle(exampleDto.title());
        example.setContent(exampleDto.content());
        example.setCategory(exampleDto.category());
        example.setDifficulty(exampleDto.difficulty());
        exampleRepository.persist(example);

        // Si se proporciona un archivo ZIP, actualizar los archivos del ejemplo
        if (zipInputStream != null) {
            String exampleDirectory = "ejemplos/" + example.getId();
            File directory = new File(exampleDirectory);

            // Eliminar el directorio existente
            if (directory.exists()) {
                deleteDirectory(directory);
            }

            // Crear nuevamente el directorio
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Extraer el nuevo archivo ZIP
            unzip(zipInputStream, directory.getAbsolutePath());
        }

        return "Example and files updated successfully.";
    }

    @Override
    @Transactional
    public String deleteExample(Integer id) {
        Example example = exampleRepository.findByIdOptional(id.longValue())
                .orElseThrow(() -> new NotFoundException("Example not found"));

        // Eliminar de la base de datos
        exampleRepository.delete(example);

        // Ruta a la carpeta del ejemplo
        String examplePath = "C:\\Users\\brahi\\Documents\\ProyectoApi\\ejemplos\\" + example.getId();
        Path exampleDirectory = Paths.get(examplePath);

        try {
            if (Files.exists(exampleDirectory)) {
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
     * Recursively deletes a directory and all its contents.
     *
     * @param directory The directory to delete
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

    @Override
    public List<ExampleDto> listExamples() {
        return exampleRepository.listAll()
                .stream()
                .map(this::buildDtoFromExample)
                .collect(Collectors.toList());
    }

    private Example buildExampleFromDto(ExampleDto dto) {
        return new Example(dto.id(), dto.title(), dto.content(), dto.category(), dto.difficulty(),
                dto.cedulaProfesor());
    }

    private ExampleDto buildDtoFromExample(Example example) {
        return new ExampleDto(example.getId(), example.getTitle(), example.getContent(), example.getCategory(),
                example.getDifficulty(), example.getCedulaProfesor());
    }

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

    @Override
    public List<String> getStudentsAssignedToExample(Integer exampleId) {
        return exampleAssignmentRepository.findCedulasByExampleId(exampleId);
    }
}
