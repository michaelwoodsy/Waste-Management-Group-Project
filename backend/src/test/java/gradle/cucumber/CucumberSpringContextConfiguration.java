package gradle.cucumber;


import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.Before;
import org.seng302.project.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = Main.class, loader = SpringBootContextLoader.class)
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