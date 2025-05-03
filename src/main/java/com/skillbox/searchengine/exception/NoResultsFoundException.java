package com.skillbox.searchengine.exception;

import com.skillbox.searchengine.utils.MessageLogs;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class NoResultsFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = MessageLogs.NOT_FOUND_ON_REQUEST;
    private static final HttpStatus STATUS_CODE = HttpStatus.NOT_FOUND;

    public NoResultsFoundException() {
        super(ERROR_MESSAGE);
 
    }
}
