package co.uniquindio.ingesis.service.interfaces;

import co.uniquindio.ingesis.dto.ExampleResource.ExampleDto;
//import co.uniquindio.ingesis.model.Student;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ExampleServiceInterface {
    String addExample(ExampleDto exampleDto, InputStream zipInputStream) throws IOException;

    ExampleDto getExample(ExampleDto requestDto);

    String updateExample(ExampleDto exampleDto, InputStream zipInputStream) throws IOException;

    String deleteExample(Integer id);

    List<ExampleDto> listExamples();

    String assignExampleToStudents(Integer exampleId, List<String> cedulasEstudiantes);

    List<String> getStudentsAssignedToExample(Integer exampleId);
}
