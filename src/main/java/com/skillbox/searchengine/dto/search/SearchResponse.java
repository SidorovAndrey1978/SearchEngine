package com.skillbox.searchengine.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {
    private boolean result;
    private int count;
    private List<SearchData> data;
}
