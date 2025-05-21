package co.edu.uniquindio.ingesis.cucumber.ejemplo.test;

import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitionsProgram {

    private final String BASE_URL = "http://quarkus-app:8080/student";
    private final String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicmFoaWFueHAzMjBAZ21haWwuY29tIiwiaXNzIjoiY2xhc3Nyb29tIiwiaWF0IjoxNzQ1ODE5Mzg1LCJleHAiOjE3NDg0MTEzODUsInJvbGUiOiJ0ZWFjaGVyIiwiY2VkdWxhIjoiMTIzNDU2Nzg5In0.E70tirlbwbQZh957hK5y2dmv12M5dJ76adWDs0U-hgM";

    private Response response;
    private File zipFile;
    private String code;
    private String name;
    private String description;

    @Given("I have a zip file named {string} to upload")
    public void i_have_a_zip_file_named(String filename) {
        zipFile = new File("src/test/resources/" + filename);
        assertTrue(zipFile.exists(), "El archivo zip no existe");
    }

    @Given("I have program details with code {string}, name {string}, and description {string}")
    public void i_have_program_details(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    @When("I send a request to upload the program for student with ID {int}")
    public void i_send_a_request_to_upload_the_program(int studentId) {
        response = given()
                .header("Authorization", token)
                .multiPart("file", zipFile)
                .multiPart("code", code)
                .multiPart("name", name)
                .multiPart("description", description)
                .when()
                .post(BASE_URL + "/" + studentId + "/program/upload");
    }

    @Then("the system should return status {int} indicating successful program upload")
    public void the_system_should_return_status_upload(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @When("I send a request to retrieve the program with code {string} for student with ID {int}")
    public void i_send_a_request_to_retrieve_the_program(String code, int studentId) {
        response = given()
                .header("Authorization", token)
                .when()
                .get(BASE_URL + "/" + studentId + "/program/" + code);
    }

    @Then("the system should return status {int} and the program's data")
    public void the_system_should_return_program_data(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
        assertNotNull(response.getBody().asString());
    }

    @Given("I have an updated zip file named {string}")
    public void i_have_an_updated_zip_file_named(String filename) {
        zipFile = new File("src/test/resources/" + filename);
        assertTrue(zipFile.exists(), "El archivo zip actualizado no existe");
    }

    @Given("I have updated program details with name {string} and description {string}")
    public void i_have_updated_program_details(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @When("I send a request to update the program with code {string} for student with ID {int}")
    public void i_send_a_request_to_update_the_program(String code, int studentId) {
        response = given()
                .header("Authorization", token)
                .multiPart("file", zipFile)
                .multiPart("name", name)
                .multiPart("description", description)
                .when()
                .put(BASE_URL + "/" + studentId + "/program/update/" + code);
    }

    @Then("the system should return status {int} indicating successful program update")
    public void the_system_should_return_status_update(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @When("I send a request to delete the program with code {string} for student with ID {int}")
    public void i_send_a_request_to_delete_the_program(String code, int studentId) {
        response = given()
                .header("Authorization", token)
                .when()
                .delete(BASE_URL + "/" + studentId + "/program/" + code);
    }

    @Then("the system should return status {int} indicating successful program deletion")
    public void the_system_should_return_status_deletion(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @When("I send a request to list shared programs")
    public void i_send_a_request_to_list_shared_programs() {
        response = given()
                .header("Authorization", token)
                .when()
                .get(BASE_URL + "/1/program/shared");
    }

    @Then("the system should return status {int} and a list of shared programs")
    public void the_system_should_return_shared_programs(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
        assertNotNull(response.getBody().asString());
    }
}
