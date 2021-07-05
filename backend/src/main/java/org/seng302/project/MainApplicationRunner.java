/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package org.seng302.project;

import net.minidev.json.parser.ParseException;
import org.seng302.project.serviceLayer.util.DGAAChecker;
import org.seng302.project.serviceLayer.util.SpringEnvironment;
import org.seng302.project.serviceLayer.util.TestDataRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;

/**
 * This spring component runs at application startup to do some initialisation
 * work.
 */
@Component
public class MainApplicationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MainApplicationRunner.class.getName());
    public final DGAAChecker dgaaChecker;
    private final TestDataRunner testDataRunner;
    private final SpringEnvironment springEnvironment;

    /**
     * This constructor is implicitly called by Spring (purpose of the @Autowired
     * annotation). Injected constructors can be supplied with instances of other
     * classes (i.e. dependency injection)
     */
    @Autowired
    public MainApplicationRunner(TestDataRunner testDataRunner,
                                 SpringEnvironment springEnvironment,
                                 DGAAChecker dgaaChecker) {
        this.testDataRunner = testDataRunner;
        this.springEnvironment = springEnvironment;
        this.dgaaChecker = dgaaChecker;
    }

    /**
     * By overriding the run method, we tell Spring to run this code at startup. See
     * https://dzone.com/articles/spring-boot-applicationrunner-and-commandlinerunne
     */
    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        logger.info("Startup application with {}", args);
        logger.info("Setting spring constant with environment variables");
        springEnvironment.setEnvironment();

        if (springEnvironment.TEST_DATA) {
            try {
                testDataRunner.insertTestData();
            } catch (FileNotFoundException exception) {
                logger.error("Error: test data file cannot be found");
            } catch (ParseException exception) {
                logger.error("Error: test data cannot be parsed");
            }
        }

        dgaaChecker.dgaaCheck();
    }

}
