package com.skillbox.searchengine.dto.indexing;

import com.skillbox.searchengine.dto.Response;
import lombok.Getter;

@Getter
public class OkResponse extends Response {

    public OkResponse() {
        super(true);

    }
}
