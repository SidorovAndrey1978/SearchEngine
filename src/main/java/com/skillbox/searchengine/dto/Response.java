package com.skillbox.searchengine.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Response {
    private boolean result;

    public Response(boolean result) {
        this.result = result;
    }
}
