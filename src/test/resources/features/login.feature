Feature: Inicio de sesión de usuario

  Scenario: Inicio exitoso de sesión de un estudiante
    Given soy un estudiante registrado con credenciales válidas
    When envío una solicitud de inicio de sesión al servicio de autenticación
    Then debería recibir un código de estado 200
    And debería recibir un token de autenticación

  Scenario: Fallo de inicio de sesión por contraseña incorrecta
    Given soy un estudiante registrado con una contraseña incorrecta
    When envío una solicitud de inicio de sesión al servicio de autenticación
    Then debería recibir un código de estado 401
    And no debería recibir un token de autenticación

  Scenario: Fallo de inicio de sesión con usuario no registrado
    Given soy un usuario no registrado
    When envío una solicitud de inicio de sesión al servicio de autenticación
    Then debería recibir un código de estado 404
    And no debería recibir un token de autenticación 


  Scenario: Fallo de inicio de sesión por rol incorrecto
    Given soy un estudiante registrado con credenciales válidas pero rol incorrecto
    When envío una solicitud de inicio de sesión al servicio de autenticación
    Then debería recibir un código de estado 400
    And no debería recibir un token de autenticación

