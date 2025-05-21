Feature: Gestión de profesores

  Scenario: Registrar un nuevo profesor exitosamente
    Given I want to register a new professor with valid data  
    When I send the request to register the professor  
    Then the system should return status 201 indicating successful teacher creation  

  Scenario: Consultar un profesor existente por cédula  
    When I send a request to retrieve the professor with ID "1007053789"  
    Then the system should return status 200 and the professor's data  

  Scenario: Actualizar los datos de un profesor existente  
    Given I want to update the information of the professor with ID "1007053789"  
    When I send the update request for the professor  
    Then the system should return status 200 indicating successful update  

  Scenario: Eliminar un profesor existente por cédula  
    When I send a request to delete the professor with ID "9876543221"  
    Then the system should return status 200 indicating successful teacher deletion  