package gradle.cucumber.steps;


import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.Before;
import org.seng302.project.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CucumberSpringContextConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CucumberSpringContextConfiguration.class.getName());

    /**
     * Need this method so the cucumber will recognize this class as glue and load spring context configuration
     */
    @Before
    public void setUp() {
        logger.info("-------------- Spring Context Initialized For Executing Cucumber Tests --------------");
    }
}