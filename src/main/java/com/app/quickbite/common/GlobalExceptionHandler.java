package com.app.quickbite.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * <p>Declared using the @ControllerAdvice annotation as a controller advice, 
 * allowing some common operations for methods in controllers annotated with @RestController and @Controller.
 * <p>Uses the @ResponseBody annotation to convert returned data into JSON format.
 * <p>Uses the @Slf4j annotation to declare a logger object.
 */

 @ControllerAdvice(annotations = {RestController.class, Controller.class})
 @ResponseBody
 @Slf4j
 public class GlobalExceptionHandler {
 
     /**
      * Handles exceptions of type {@link SQLIntegrityConstraintViolationException}. 
      * In this project, it primarily handles exceptions caused by duplicate data during registration.
      * <p>Slices the error message from the SQL exception and returns specific error messages, 
      * such as indicating that a particular account already exists.
      * <p>Declared using the @ExceptionHandler annotation, which specifies that this method is 
      * an exception handler for exceptions of type {@link SQLIntegrityConstraintViolationException}.
      *
      * @param exception The exception, for example: 
      *                  com.mysql.cj.jdbc.exceptions.SQLIntegrityConstraintViolationException: Duplicate entry 'riverify' for key 'username'.
      * @return A common result object.
      */
     @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
     public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
         log.error("SQLIntegrityConstraintViolationException: {}", exception.getMessage());
         // If the exception message contains "Duplicate entry", it indicates a primary key conflict exception.
         if (exception.getMessage().contains("Duplicate entry")) {
             // Extract specific error details and return them to the frontend.
             String[] split = exception.getMessage().split("'"); // Example: Duplicate entry 'riverify' for key 'employee.idx_username', split by single quotes.
             return R.error(split[1] + " already exists"); // Return the duplicate data, e.g., "riverify already exists".
         }
         // For other exceptions, return a generic error message.
         return R.error("Operation failed, unknown error");
     }
 
     /**
      * Handles custom exceptions of type {@link CustomException}. 
      * These exceptions can be thrown in the program using, for example: throw new CustomException("xxx"), 
      * and will be caught and returned to the frontend here (wrapped in an R object).
      * <p>Declared using the @ExceptionHandler annotation, which specifies that this method is 
      * an exception handler for exceptions of type {@link CustomException}.
      *
      * @param exception The custom exception
      * @return A common result object for displaying error messages on the frontend.
      */
     @ExceptionHandler(CustomException.class)
     public R<String> exceptionHandler(CustomException exception) {
         log.error("Custom exception occurred: {}", exception.getMessage());
         return R.error(exception.getMessage());
     }
 }
 
