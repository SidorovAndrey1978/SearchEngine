package com.skillbox.searchengine.exception;

import com.skillbox.searchengine.utils.MessageLogs;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PageOutsideConfigured extends RuntimeException{

    private static final String ERROR_MESSAGE = MessageLogs.PAGE_OUTSIDE_CONFIGURED_SITES;
    private static final HttpStatus STATUS_CODE = HttpStatus.BAD_REQUEST;

    public PageOutsideConfigured() {
        super(ERROR_MESSAGE);
    }

}
