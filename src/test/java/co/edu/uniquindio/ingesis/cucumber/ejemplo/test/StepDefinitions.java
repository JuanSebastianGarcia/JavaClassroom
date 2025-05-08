package co.edu.uniquindio.ingesis.cucumber.ejemplo.test;

import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class StepDefinitions {

    private String requestBody;
    private Response response;

    @Given("soy un estudiante registrado con credenciales válidas")
    public void soy_estudiante_registrado_con_credenciales_validas() {
        requestBody = """
                {
                  "email": "brahianjajasalu2@gmail.com",
                  "password": "contraseñaSegura1234",
                  "role": "student"
                }
                """;
    }

    @Given("soy un estudiante registrado con una contraseña incorrecta")
    public void soy_estudiante_con_contraseña_incorrecta() {
        requestBody = """
                {
                  "email": "brahianjajasalu2@gmail.com",
                  "password": "claveIncorrecta123",
                  "role": "student"
                }
                """;
    }

    @Given("soy un usuario no registrado")
    public void soy_un_usuario_no_registrado() {
        requestBody = """
                {
                  "email": "noexiste@email.com",
                  "password": "cualquierClave123",
                  "role": "student"
                }
                """;
    }

    @Given("soy un estudiante registrado con credenciales válidas pero rol incorrecto")
    public void soy_estudiante_con_rol_incorrecto() {
        requestBody = """
                {
                  "email": "brahianjajasalu2@gmail.com",
                  "password": "contraseñaSegura1234",
                  "role": "teacher"
                }
                """;
    }

    @When("envío una solicitud de inicio de sesión al servicio de autenticación")
    public void envio_solicitud_login() {
        response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:8081/auth");
    }

    @Then("debería recibir un código de estado 200")
    public void deberia_recibir_codigo_200() {
        response.then().statusCode(200);
    }

    @Then("debería recibir un código de estado 401")
    public void deberia_recibir_codigo_401() {
        response.then().statusCode(401);
    }

    @Then("debería recibir un código de estado 404")
    public void deberia_recibir_codigo_404() {
        response.then().statusCode(404);
    }

    @Then("debería recibir un código de estado 400")
    public void deberia_recibir_codigo_400() {
        response.then().statusCode(400);
    }

    @And("debería recibir un token de autenticación")
    public void deberia_recibir_token() {
        response.then().body("token", notNullValue());
    }

    @And("no debería recibir un token de autenticación")
    public void no_deberia_recibir_token() {
        response.then().body("token", nullValue());
    }
}