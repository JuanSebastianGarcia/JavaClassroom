package co.uniquindio.ingesis.service.implement;

import co.uniquindio.ingesis.service.interfaces.ExecutionServiceInterface;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ExecutionService implements ExecutionServiceInterface {

    private static final String BASE_PROGRAMS_DIR = "programs";
    private static final long TIMEOUT_SECONDS = 10;

    @Override
    public String executeProgram(String folderName) {
        try {
            Path folderPath = validateAndGetFolderPath(folderName);

            File mainFile = findMainJavaFile(folderPath);
            if (mainFile == null) {
                return buildNoMainFileError(folderPath);
            }

            String compileOutput = compileJavaFiles(folderPath);
            if (!compileOutput.isEmpty()) {
                return compileOutput;
            }

            return executeCompiledProgram(folderPath, mainFile);

        } catch (Exception e) {
            return "Error al ejecutar el programa: " + e.getMessage();
        }
    }

    private Path validateAndGetFolderPath(String folderName) throws Exception {
        Path folderPath = Paths.get(BASE_PROGRAMS_DIR, folderName);
        if (!Files.exists(folderPath)) {
            throw new FileNotFoundException("El directorio del programa no existe: " + folderPath);
        }
        if (!Files.isDirectory(folderPath)) {
            throw new IOException("La ruta especificada no es un directorio: " + folderPath);
        }
        return folderPath;
    }

    private File findMainJavaFile(Path folderPath) throws IOException {
        try (Stream<Path> paths = Files.walk(folderPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .filter(this::hasMainMethod)
                    .findFirst()
                    .orElse(null);
        }
    }

    private boolean hasMainMethod(File javaFile) {
        try {
            String content = Files.readString(javaFile.toPath());
            return content.contains("public static void main(String[] args)") ||
                    content.contains("public static void main( String[] args )") ||
                    content.contains("public static void main(String args[])");
        } catch (IOException e) {
            return false;
        }
    }

    private String buildNoMainFileError(Path folderPath) throws IOException {
        StringBuilder error = new StringBuilder();
        error.append("No se encontró un archivo .java con método 'main' válido en: ")
                .append(folderPath.toString())
                .append("\n\nArchivos encontrados:\n");

        try (Stream<Path> paths = Files.walk(folderPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> error.append("- ").append(p.toString()).append("\n"));
        }

        return error.toString();
    }

    private String compileJavaFiles(Path folderPath) throws Exception {
        List<String> javaFiles;
        try (Stream<Path> paths = Files.walk(folderPath)) {
            javaFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(p -> folderPath.relativize(p).toString())
                    .collect(Collectors.toList());
        }

        if (javaFiles.isEmpty()) {
            return "No se encontraron archivos .java para compilar.";
        }

        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("-encoding");
        command.add("UTF-8");
        command.addAll(javaFiles);

        Process compileProcess = new ProcessBuilder(command)
                .directory(folderPath.toFile())
                .redirectErrorStream(true)
                .start();

        boolean finished = compileProcess.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        String output = new String(compileProcess.getInputStream().readAllBytes());

        if (!finished || compileProcess.exitValue() != 0) {
            return "Error al compilar los archivos:\n" + output;
        }

        return "";
    }

    private String executeCompiledProgram(Path folderPath, File javaFile) throws Exception {
        String className = extractFullClassName(javaFile);

        Process runProcess = new ProcessBuilder("java", className)
                .directory(folderPath.toFile())
                .redirectErrorStream(true)
                .start();

        boolean finished = runProcess.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        String output = new String(runProcess.getInputStream().readAllBytes());

        if (!finished) {
            return "El programa excedió el tiempo límite de ejecución (" + TIMEOUT_SECONDS + "s)\n" +
                    "Salida parcial:\n" + output;
        }

        return "Salida del programa:\n" + output;
    }

    private String extractFullClassName(File javaFile) throws IOException {
        String packageName = null;
        List<String> lines = Files.readAllLines(javaFile.toPath());

        for (String line : lines) {
            line = line.strip();
            if (line.startsWith("package ")) {
                packageName = line.replace("package", "").replace(";", "").trim();
                break;
            }
        }

        String className = javaFile.getName().replace(".java", "");
        return (packageName != null ? packageName + "." : "") + className;
    }
}
