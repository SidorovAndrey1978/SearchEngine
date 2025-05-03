package com.skillbox.searchengine.exception;

import com.skillbox.searchengine.utils.MessageLogs;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmptyQueryException extends RuntimeException{

    private static final String ERROR_MESSAGE = MessageLogs.EMPTY_REQUEST;
    private static final HttpStatus STATUS_CODE = HttpStatus.BAD_REQUEST;

    public EmptyQueryException() {
        super(ERROR_MESSAGE);
    }
    
}
