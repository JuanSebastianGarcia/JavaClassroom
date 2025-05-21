package co.uniquindio.ingesis.service.interfaces;

import co.uniquindio.ingesis.dto.ExampleResource.ExampleDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ExampleServiceInterface {

    /**
     * Adds a new example along with its associated ZIP file content.
     *
     * @param exampleDto     the data transfer object containing example details
     * @param zipInputStream the input stream of the ZIP file with example files
     * @return a confirmation message or identifier of the added example
     * @throws IOException if there is an error reading the ZIP input stream
     */
    String addExample(ExampleDto exampleDto, InputStream zipInputStream) throws IOException;

    /**
     * Retrieves an example based on the provided example DTO.
     *
     * @param requestDto the DTO containing the request criteria for the example
     * @return the example DTO matching the request
     */
    ExampleDto getExample(ExampleDto requestDto);

    /**
     * Updates an existing example and optionally its associated ZIP file content.
     *
     * @param exampleDto     the DTO with updated example details
     * @param zipInputStream the input stream of the updated ZIP file, if any
     * @return a confirmation message or identifier of the updated example
     * @throws IOException if there is an error reading the ZIP input stream
     */
    String updateExample(ExampleDto exampleDto, InputStream zipInputStream) throws IOException;

    /**
     * Deletes an example by its identifier.
     *
     * @param id the unique identifier of the example to delete
     * @return a confirmation message about the deletion
     */
    String deleteExample(Integer id);

    /**
     * Lists all available examples.
     *
     * @return a list of example DTOs
     */
    List<ExampleDto> listExamples();

    /**
     * Assigns an example to a list of students identified by their IDs.
     *
     * @param exampleId          the ID of the example to assign
     * @param cedulasEstudiantes the list of student ID strings to assign the
     *                           example to
     * @return a confirmation message about the assignment
     */
    String assignExampleToStudents(Integer exampleId, List<String> cedulasEstudiantes);

    /**
     * Retrieves the list of student IDs assigned to a specific example.
     *
     * @param exampleId the ID of the example
     * @return a list of student ID strings assigned to the example
     */
    List<String> getStudentsAssignedToExample(Integer exampleId);
}
