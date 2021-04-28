package gradle.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


/**
 * Class that runs Cucumber tests
 */

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"},
        glue = {"gradle.cucumber.steps"},
        features = {"src/test/resources/"},
        publish = true
)
public class CucumberTest {
}
