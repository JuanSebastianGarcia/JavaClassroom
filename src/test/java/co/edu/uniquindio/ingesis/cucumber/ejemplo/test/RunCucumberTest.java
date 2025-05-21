package co.edu.uniquindio.ingesis.cucumber.ejemplo.test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", glue = "co.edu.uniquindio.ingesis.cucumber.ejemplo.test", plugin = {
        "pretty" })
public class RunCucumberTest {
}