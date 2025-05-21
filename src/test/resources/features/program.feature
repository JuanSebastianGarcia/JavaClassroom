Feature: Gestión de programas

  Scenario: Subir un nuevo programa
    Given I have a zip file named "program.zip" to upload
    And I have program details with code "P050", name "Programa de Prueba", and description "Descripción de prueba"
    When I send a request to upload the program for student with ID 123
    Then the system should return status 201 indicating successful program upload

  Scenario: Obtener un programa existente
    When I send a request to retrieve the program with code "P050" for student with ID 123
    Then the system should return status 200 and the program's data

  Scenario: Actualizar un programa existente
    Given I have an updated zip file named "program_updated.zip"
    And I have updated program details with name "Programa Actualizado" and description "Descripción actualizada"
    When I send a request to update the program with code "P050" for student with ID 123
    Then the system should return status 200 indicating successful program update

  Scenario: Eliminar un programa existente
    When I send a request to delete the program with code "P050" for student with ID 123
    Then the system should return status 200 indicating successful program deletion

  Scenario: Listar programas compartidos
    When I send a request to list shared programs
    Then the system should return status 200 and a list of shared programs
