package co.uniquindio.ingesis.dto.ExcecutionResource;

/**
 * Data Transfer Object (DTO) representing the result of code execution.
 *
 * @param output the standard output produced by the executed code
 * @param errors any error messages generated during execution
 */
public record ExecutionResultDto(String output, String errors) {
}