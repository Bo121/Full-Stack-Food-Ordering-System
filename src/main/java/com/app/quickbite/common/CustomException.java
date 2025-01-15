package com.app.quickbite.common;

/**
 * Custom business exception class
 */
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
