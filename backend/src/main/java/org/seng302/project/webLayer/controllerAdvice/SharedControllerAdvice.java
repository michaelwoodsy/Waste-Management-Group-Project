package org.seng302.project.webLayer.controllerAdvice;

import org.seng302.project.serviceLayer.exceptions.BadRequestException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.regex.Pattern;

/**
 * Class that all controllers use for handling exceptions.
 */
@ControllerAdvice
public class SharedControllerAdvice {


    /**
     * Handles sending a 400 response when a request DTO is invalid.
     *
     * @param ex the MethodArgumentNotValidException thrown by the DTO
     * @return a 400 response with a message about why the request was invalid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> invalidRequestDTO(MethodArgumentNotValidException ex) {

        var responseString = "";

        //Gets the useful bit of the error message e.g. "Product name is a mandatory field"
        var pattern = Pattern.compile("]]; default message \\[(.*?)]");
        var matcher = pattern.matcher(ex.getMessage());
        if (matcher.find())
        {
            responseString = matcher.group(1);
        }

        return new ResponseEntity<>(responseString, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles sending a 400 response when a request is invalid.
     *
     * @param ex the BadRequestException thrown by the controller/service
     * @return a 400 response with a message about why the request was invalid
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> badRequestDTO(BadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles sending a 406 response when a request has unacceptable path variables
     *
     * @param ex the NotAcceptableException thrown by the controller/service
     * @return a 406 response with a message about what variable was not found
     */
    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<String> notAcceptable(NotAcceptableException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
}
