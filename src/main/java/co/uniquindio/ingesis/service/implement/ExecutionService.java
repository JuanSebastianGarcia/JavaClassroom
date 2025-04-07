package co.uniquindio.ingesis.service.implement;


import co.uniquindio.ingesis.service.interfaces.ExecutionServiceInterface;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ExecutionService implements ExecutionServiceInterface {

    private static final String BASE_PROGRAMS_DIR = "programs";

    @Override
    public String executeProgram(String folderName) {
        try {
            // Ruta del directorio
            Path folderPath = Paths.get(BASE_PROGRAMS_DIR, folderName);
            if (!Files.exists(folderPath)) {
                return "El programa no existe en la ruta: " + folderPath.toString();
            }

            // Buscar archivo con método main
            File[] javaFiles = folderPath.toFile().listFiles((dir, name) -> name.endsWith(".java"));
            File mainFile = null;

            for (File file : javaFiles) {
                String content = Files.readString(file.toPath());
                if (content.contains("public static void main")) {
                    mainFile = file;
                    break;
                }
            }

            if (mainFile == null) {
                return "No se encontró un archivo con método 'main'";
            }

            // Compilar
            Process compileProcess = new ProcessBuilder("javac", "-encoding", "UTF-8", mainFile.getName())
            .directory(folderPath.toFile())
            .redirectErrorStream(true)
            .start();
            compileProcess.waitFor(5, TimeUnit.SECONDS);
            String compileOutput = new String(compileProcess.getInputStream().readAllBytes());

            if (compileProcess.exitValue() != 0) {
                return "Error al compilar:\n" + compileOutput;
            }

            // Ejecutar
            String className = mainFile.getName().replace(".java", "");
            Process runProcess = new ProcessBuilder("java", className)
                    .directory(folderPath.toFile())
                    .redirectErrorStream(true)
                    .start();

            runProcess.waitFor(5, TimeUnit.SECONDS);
            String runOutput = new String(runProcess.getInputStream().readAllBytes());

            return "Salida del programa:\n" + runOutput;

        } catch (Exception e) {
            return "Error al ejecutar el programa: " + e.getMessage();
        }
    }
}