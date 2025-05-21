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

/**
 * Service responsible for compiling and executing Java programs located in
 * specific folders.
 * 
 * Programs are expected to be located under the base directory
 * {@code programs/}, with each
 * program in its own subfolder. The service validates the existence of a main
 * Java file with a
 * valid {@code public static void main(String[] args)} method, compiles all
 * Java files in the folder,
 * and executes the compiled program with a timeout to prevent long-running
 * processes.
 * 
 * Compilation and execution errors are returned as part of the output messages.
 * 
 * @author
 * @version 1.0
 */
@ApplicationScoped
public class ExecutionService implements ExecutionServiceInterface {

    private static final String BASE_PROGRAMS_DIR = "programs";
    private static final long TIMEOUT_SECONDS = 10;

    /**
     * Compiles and executes a Java program located in a specified folder.
     * 
     * The method performs the following steps:
     * Validates the existence and correctness of the folder.
     * Searches for a Java file containing the {@code main} method.
     * Compiles all Java files within the folder.
     * Executes the compiled program and returns its output or error
     * messages.
     * 
     * @param folderName the name of the folder inside the base directory containing
     *                   the Java program
     * @return the output from the program execution or error messages if
     *         compilation/execution fails
     */
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
            return "Error executing program: " + e.getMessage();
        }
    }

    /**
     * Validates the existence and type of the specified folder and returns its
     * path.
     * 
     * @param folderName the folder name to validate under the base directory
     * @return the absolute path of the folder if it exists and is a directory
     * @throws FileNotFoundException if the folder does not exist
     * @throws IOException           if the path exists but is not a directory
     */
    private Path validateAndGetFolderPath(String folderName) throws Exception {
        Path folderPath = Paths.get(BASE_PROGRAMS_DIR, folderName);
        if (!Files.exists(folderPath)) {
            throw new FileNotFoundException("Program directory does not exist: " + folderPath);
        }
        if (!Files.isDirectory(folderPath)) {
            throw new IOException("Specified path is not a directory: " + folderPath);
        }
        return folderPath;
    }

    /**
     * Searches for a Java source file containing a valid main method inside the
     * folder.
     * 
     * @param folderPath the folder path to search for Java files
     * @return the first Java file containing a main method, or {@code null} if none
     *         is found
     * @throws IOException if an I/O error occurs during file traversal
     */
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

    /**
     * Checks if a given Java file contains a valid main method declaration.
     * 
     * @param javaFile the Java source file to inspect
     * @return {@code true} if the file contains a main method, {@code false}
     *         otherwise
     */
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

    /**
     * Constructs an error message listing all Java files when no valid main file is
     * found.
     * 
     * @param folderPath the folder where the search was performed
     * @return a detailed error message with the list of Java files found
     * @throws IOException if an I/O error occurs during file traversal
     */
    private String buildNoMainFileError(Path folderPath) throws IOException {
        StringBuilder error = new StringBuilder();
        error.append("No .java file with a valid 'main' method found in: ")
                .append(folderPath.toString())
                .append("\n\nFiles found:\n");

        try (Stream<Path> paths = Files.walk(folderPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> error.append("- ").append(p.toString()).append("\n"));
        }

        return error.toString();
    }

    /**
     * Compiles all Java source files in the given folder.
     * 
     * @param folderPath the folder containing Java files to compile
     * @return an empty string if compilation is successful, or an error message
     *         otherwise
     * @throws Exception if an error occurs during compilation
     */
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
            return "No .java files found to compile.";
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
            return "Compilation errors:\n" + output;
        }

        return "";
    }

    /**
     * Executes the compiled Java program and captures its output.
     * 
     * @param folderPath the folder where the compiled classes are located
     * @param javaFile   the Java source file containing the main class
     * @return the program's output or a timeout message if execution exceeds the
     *         time limit
     * @throws Exception if an error occurs during execution
     */
    private String executeCompiledProgram(Path folderPath, File javaFile) throws Exception {
        String className = extractFullClassName(javaFile);

        Process runProcess = new ProcessBuilder("java", className)
                .directory(folderPath.toFile())
                .redirectErrorStream(true)
                .start();

        boolean finished = runProcess.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        String output = new String(runProcess.getInputStream().readAllBytes());

        if (!finished) {
            return "Program exceeded execution time limit (" + TIMEOUT_SECONDS + "s)\n" +
                    "Partial output:\n" + output;
        }

        return "Program output:\n" + output;
    }

    /**
     * Extracts the fully qualified class name from a Java source file.
     * 
     * @param javaFile the Java source file to inspect
     * @return the full class name including package if present
     * @throws IOException if an error occurs reading the file
     */
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
