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

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.seng302.project.model.Business;
import org.seng302.project.model.BusinessRepository;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileReader;

/**
 * This spring component runs at application startup to do some initialisation
 * work.
 */
@Component
public class MainApplicationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MainApplicationRunner.class.getName());
    private static final JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * This constructor is implicitly called by Spring (purpose of the @Autowired
     * annotation). Injected constructors can be supplied with instances of other
     * classes (i.e. dependency injection)
     *
     * @param userRepository the user repository to persist example data.
     */
    @Autowired
    public MainApplicationRunner(UserRepository userRepository, BusinessRepository businessRepository,
                                 BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * By overriding the run method, we tell Spring to run this code at startup. See
     * https://dzone.com/articles/spring-boot-applicationrunner-and-commandlinerunne
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Startup application with {}", args);
        if (Constants.DEV_MODE) {
            if (userRepository.count() == 0) {
                logger.info("Adding sample to data to user repository");
                JSONObject data = (JSONObject) parser.parse(new FileReader("./src/main/resources/user_data.json"));
                JSONArray dataArray = (JSONArray) data.get("data");
                for (Object object : dataArray) {
                    JSONObject jsonUser = (JSONObject) object;
                    User newUser = new User(
                            jsonUser.getAsString("firstName"),
                            jsonUser.getAsString("lastName"),
                            jsonUser.getAsString("middleName"),
                            jsonUser.getAsString("nickname"),
                            jsonUser.getAsString("bio"),
                            jsonUser.getAsString("email"),
                            jsonUser.getAsString("dateOfBirth"),
                            jsonUser.getAsString("phoneNumber"),
                            jsonUser.getAsString("homeAddress"),
                            (passwordEncoder.encode(jsonUser.getAsString("password")))
                    );
                    userRepository.save(newUser);
                }
                logger.info("Finished adding sample data to user repository");
                logger.info(String.format("Added %d entries to repository", userRepository.count()));
            }
        }

        //Creating a test business to be retrieved
        if (businessRepository.count() == 0) {
            logger.info("Adding sample to data to business repository");
            Business newBusiness = new Business("Myrtle's Motel", "Accommodation by Myrtle", "6121 Autumn Leaf Trail", "Accommodation and Food Services", 1);
            businessRepository.save(newBusiness);

            logger.info(String.format("Added new business with id %d", businessRepository.findByName("Myrtle's Motel").get(0).getId()));


            //TODO: The below caused infinite business->user->business->user... recursion
            //TODO: need to save primary admin as an admin too
            //Testing for linking to admin pages
//            User businessAdmin2 = userRepository.getOne(2);
//            newBusiness.addAdministrator(businessAdmin2);
//            businessRepository.save(newBusiness);
//            User businessAdmin3 = userRepository.getOne(3);
//            newBusiness.addAdministrator(businessAdmin3);
//            businessRepository.save(newBusiness);

        }

    }

}
