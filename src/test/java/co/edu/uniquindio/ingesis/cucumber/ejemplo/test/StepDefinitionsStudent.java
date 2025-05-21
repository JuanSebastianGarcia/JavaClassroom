package co.edu.uniquindio.ingesis.cucumber.ejemplo.test;

import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitionsStudent {

    private final String BASE_URL = "http://localhost:8081/student";
    private final String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicmFoaWFueHAzMjBAZ21haWwuY29tIiwiaXNzIjoiY2xhc3Nyb29tIiwiaWF0IjoxNzQ1ODE5Mzg1LCJleHAiOjE3NDg0MTEzODUsInJvbGUiOiJ0ZWFjaGVyIiwiY2VkdWxhIjoiMTIzNDU2Nzg5In0.E70tirlbwbQZh957hK5y2dmv12M5dJ76adWDs0U-hgM";

    private Response response;
    private String requestBody;

    // ---------- CREAR ESTUDIANTE ----------

    @Given("I want to register a new student with valid data")
    public void i_want_to_register_a_new_student_with_valid_data() {
        requestBody = """
                {
                    "cedula": "1007053788",
                    "name": "Test Student",
                    "email": "teststudent5@email.com",
                    "password": "Password1234"
                }
                """;
    }

    @When("I send the request to register the student")
    public void i_send_the_request_to_register_the_student() {
        response = given()
                .header("Authorization", token)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(BASE_URL);
    }

    @Then("the system should return status {int} indicating successful student creation")
    public void the_system_should_return_status_created(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    // ---------- CONSULTAR ESTUDIANTE ----------

    @When("I send a request to retrieve the student with email {string}")
    public void i_send_a_request_to_retrieve_the_student(String email) {
        response = given()
                .header("Authorization", token)
                .when()
                .get(BASE_URL + "?email=" + email);
    }

    @Then("the system should return status {int} and the student's data")
    public void the_system_should_return_student_data(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
        assertNotNull(response.getBody().asString());
    }

    // ---------- ACTUALIZAR ESTUDIANTE ----------

    @Given("I want to update the information of the student with email {string}")
    public void i_want_to_update_the_student(String email) {
        requestBody = """
                {
                    "email": "brahiandprueba2@gmail.com",
                    "password": "contraseñaSegura1234",
                    "password": "contraseñaSegura1234",
                    "name": "Updated Student Name"

                }
                """.formatted(email);
    }

    @When("I send the update request for the student")
    public void i_send_update_request_for_student() {
        response = given()
                .header("Authorization", token)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put(BASE_URL);
    }

    @Then("the system should return status {int} indicating successful student update")
    public void the_system_should_return_status_updated(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    // ---------- ELIMINAR ESTUDIANTE ----------

    @When("I send a request to delete the student with email {string} and cedula {string} and password {string}")
    public void i_send_delete_request(String email, String cedula, String password) {
        requestBody = """
                {
                    "cedula": "%s",
                    "password": "%s"
                }
                """.formatted(cedula, password);

        response = given()
                .header("Authorization", token)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .delete(BASE_URL + "?email=" + email);
    }

    @Then("the system should return status {int} indicating successful student deletion")
    public void the_system_should_return_status_deleted(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    // ---------- VALIDACIONES GENÉRICAS ----------

    @Then("the student system should return status {int}")
    public void the_system_should_return_status(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @Then("the student system should show an error message")
    public void the_system_should_show_error_message() {
        assertNotNull(response.getBody().asString());
        System.out.println("Mensaje de error: " + response.getBody().asString());
    }
}
