package com.hatio.taskStream.exception;

public class UnauthorizedAccessException extends  RuntimeException{
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
