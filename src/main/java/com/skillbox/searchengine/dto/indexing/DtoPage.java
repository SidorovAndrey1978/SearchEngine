package com.skillbox.searchengine.dto.indexing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DtoPage {
    private int code;
    private String path;
    private String content;
}
