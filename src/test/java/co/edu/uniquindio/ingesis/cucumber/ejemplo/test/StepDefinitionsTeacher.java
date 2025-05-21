package co.edu.uniquindio.ingesis.cucumber.ejemplo.test;

import io.cucumber.java.en.*; // Cambiado de .es a .en
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitionsTeacher {

    private final String BASE_URL = "http://localhost:8081/teacher";
    private final String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicmFoaWFuZDMyMDUxOUBnbWFpbC5jb20iLCJpc3MiOiJjbGFzc3Jvb20iLCJpYXQiOjE3NDc3NjE4NDcsImV4cCI6MTc1MDM1Mzg0Nywicm9sZSI6InRlYWNoZXIiLCJjZWR1bGEiOiIxMjM0NTY3ODkzOSIsImlkIjoxOX0.sFf-Yh5wF_CEa6I9FwWTxZoCZZc0WnlEDHPwZRnVIlI"; // Reemplaza
                                                                                                                                                                                                                                                                                                // con
                                                                                                                                                                                                                                                                                                // tu
                                                                                                                                                                                                                                                                                                // token
                                                                                                                                                                                                                                                                                                // real

    private Response response;
    private String requestBody;

    // ---------- CREAR PROFESOR ----------

    @Given("I want to register a new professor with valid data")
    public void i_want_to_register_a_new_professor_with_valid_data() {
        requestBody = """
                {
                    "cedula": "9876543221",
                    "name": "Brahian Garcia",
                    "email": "noexistente@gmail.com",
                    "password": "contraseñaSegura1234"
                }
                """;
    }

    @When("I send the request to register the professor")
    public void i_send_the_request_to_register_the_professor() {
        response = given()
                .header("Authorization", token)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(BASE_URL);
    }

    @Then("the system should return status {int} indicating successful teacher creation")
    public void the_system_should_return_status_created(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    // ---------- CONSULTAR PROFESOR ----------

    @When("I send a request to retrieve the professor with ID {string}")
    public void i_send_a_request_to_retrieve_the_professor(String cedula) {
        response = given()
                .header("Authorization", token)
                .when()
                .get(BASE_URL + "/" + cedula);
    }

    @Then("the system should return status {int} and the professor's data")
    public void the_system_should_return_professor_data(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
        assertNotNull(response.getBody().asString());
    }

    // ---------- ACTUALIZAR PROFESOR ----------

    @Given("I want to update the information of the professor with ID {string}")
    public void i_want_to_update_the_professor(String cedula) {
        requestBody = """
                {
                    "cedula": "%s",
                    "name": "Brahian Garcia Actualizado",
                    "email": "brahianactualizado@gmail.com",
                    "password": "claveNueva456"
                }
                """.formatted(cedula);
    }

    @When("I send the update request for the professor")
    public void i_send_update_request_for_professor() {
        response = given()
                .header("Authorization", token)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put(BASE_URL);
    }

    @Then("the system should return status {int} indicating successful update")
    public void the_system_should_return_status_updated(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    // ---------- ELIMINAR PROFESOR ----------

    @When("I send a request to delete the professor with ID {string}")
    public void i_send_delete_request(String cedula) {
        response = given()
                .header("Authorization", token)
                .when()
                .delete(BASE_URL + "/" + cedula);
    }

    @Then("the system should return status {int} indicating successful teacher deletion")
    public void the_system_should_return_status_deleted(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    // ---------- VALIDACIONES GENÉRICAS ----------

    @Then("the teacher system should return status {int}")
    public void the_system_should_return_status(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @Then("the teacher system should show an error message")
    public void the_system_should_show_error_message() {
        assertNotNull(response.getBody().asString());
        System.out.println("Mensaje de error: " + response.getBody().asString());
    }
}
