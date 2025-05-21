Feature: Gestión de estudiantes

  Scenario: Registrar un nuevo estudiante exitosamente
    Given I want to register a new student with valid data
    When I send the request to register the student
    Then the system should return status 201 indicating successful student creation

  Scenario: Consultar un estudiante existente por email
    When I send a request to retrieve the student with email "pruebaestudianteapi@gmail.com"
    Then the system should return status 200 and the student's data

  Scenario: Actualizar los datos de un estudiante existente
    Given I want to update the information of the student with email "pruebaestudianteapi@gmail.com"
    When I send the update request for the student
    Then the system should return status 200 indicating successful student update

  Scenario: Eliminar un estudiante existente por email
    When I send a request to delete the student with email "teststudent5@email.com" and cedula "1007053788" and password "Password1234"
    Then the system should return status 200 indicating successful student deletion
