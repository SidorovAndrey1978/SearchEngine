package com.skillbox.searchengine.dto.indexing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DtoIndex {
    private long pageId;
    private long lemmaId;
    private float rank;
}
