package com.skillbox.searchengine.dto.indexing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DtoLemma {
    private String lemma;
    private int frequency;
}
