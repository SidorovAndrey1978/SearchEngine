package com.skillbox.searchengine.dto.indexing;

import com.skillbox.searchengine.dto.Response;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse extends Response {

    private String error;

    public ErrorResponse(String error) {
        super(false);
        this.error = error;
    }
}
