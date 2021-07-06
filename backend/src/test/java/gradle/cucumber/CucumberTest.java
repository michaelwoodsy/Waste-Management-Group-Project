package gradle.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.seng302.project.Main;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


/**
 * Class that runs Cucumber tests
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"},
        features = {"src/test/resources"},
        glue = {"gradle.cucumber.steps"},
        publish = true
)
@SpringBootTest
@ContextConfiguration(classes = Main.class)
@ActiveProfiles(value = {"test"})
public class CucumberTest {
}
