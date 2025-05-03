package com.skillbox.searchengine.services.search;

import com.skillbox.searchengine.dto.search.SearchData;
import com.skillbox.searchengine.dto.search.SearchResponse;
import com.skillbox.searchengine.exception.EmptyQueryException;
import com.skillbox.searchengine.exception.NoResultsFoundException;
import com.skillbox.searchengine.exception.PageOutsideConfigured;
import com.skillbox.searchengine.model.LemmaEntity;
import com.skillbox.searchengine.model.PageEntity;
import com.skillbox.searchengine.model.SiteEntity;
import com.skillbox.searchengine.repository.SiteRepository;
import com.skillbox.searchengine.services.search.searchhelpers.PageFinder;
import com.skillbox.searchengine.services.search.searchhelpers.RelevanceCalculator;
import com.skillbox.searchengine.services.search.searchhelpers.SnippetGeneration;
import com.skillbox.searchengine.services.search.searchhelpers.SortingLemmas;
import com.skillbox.searchengine.utils.LemmaExtractor;
import com.skillbox.searchengine.utils.MessageLogs;
import com.skillbox.searchengine.utils.UrlHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация службы поиска, выполняющая поиск по сайтам и выдачу результатов.
 * Процесс включает несколько этапов: обработка запроса, поиск страниц,
 * расчёт релевантности и подготовка ответа.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final RelevanceCalculator relevanceCalculator;
    private final SnippetGeneration snippetGeneration;
    private final PageFinder pageFinder;

    private final SiteRepository siteRepository;
    private final LemmaExtractor lemmaExtractor;
    private final UrlHelper urlHelper;
    private final SortingLemmas sortingLemmas;


    @Override
    public ResponseEntity<Object> search(String query, String siteUrl, int offset, int limit) {
        if (query.isEmpty()) {
            throw new EmptyQueryException();

        } else {
            List<SearchData> searchData;
            if (siteUrl != null) {
                if (siteRepository.findByUrl(siteUrl) == null) {
                    throw new PageOutsideConfigured();

                } else {
                    searchData = oneSiteSearch(query, siteUrl);
                }
            } else {
                searchData = searchAllSites(query);
            }
            if (searchData.isEmpty()) {
                throw new NoResultsFoundException();
            }
            int count = searchData.size();

            if (searchData.size() > offset) {

                int endIndex = Math.min(offset + limit, searchData.size());
                searchData = searchData.subList(offset, endIndex);

            }
            return new ResponseEntity<>(new SearchResponse(true, count, searchData),
                    HttpStatus.OK);
        }
    }

    @Override
    public List<SearchData> searchAllSites(String query) {

        log.info(MessageLogs.LOG_START_AllSITES_SEARCH, query);

        List<String> lemmasFromQuery = sortingLemmas.processQuery(query);

        List<LemmaEntity> findLemma = sortingLemmas
                .findLemmasInRepository(lemmasFromQuery, null);

        Map<Long, List<LemmaEntity>> mapLemmaOnsite = findLemma.stream()
                .collect(Collectors.groupingBy(
                        lemmaEntity -> lemmaEntity.getSiteId().getId()));

        List<PageEntity> pagesOfAllSites = new ArrayList<>();

        for (Map.Entry<Long, List<LemmaEntity>> entry : mapLemmaOnsite.entrySet()) {

            Long siteId = entry.getKey();
            List<LemmaEntity> lemmaOnSite = entry.getValue();

            List<LemmaEntity> filteredLemmas = sortingLemmas
                    .filterAndSortLemmas(lemmaOnSite, siteId);

            List<PageEntity> pageOnSite = pageFinder
                    .findPagesContainingAllLemmas(filteredLemmas);

            pagesOfAllSites.addAll(pageOnSite);
        }

        LinkedHashMap<PageEntity, Float> sortedPages = relevanceCalculator
                .calculateRelevance(pagesOfAllSites);

        List<SearchData> searchData = getSearchData(sortedPages, lemmasFromQuery);

        List<SearchData> searchDataList = new ArrayList<>(searchData);

        log.info(MessageLogs.LOG_FINISH_AllSITES_SEARCH);
        return searchDataList;
    }


    @Override
    public List<SearchData> oneSiteSearch(String query, String url) {
        log.info(MessageLogs.LOG_START_OneSITE_SEARCH, url, query);

        SiteEntity siteEntity = siteRepository.findByUrl(url);

        long siteId = siteEntity.getId();

        List<String> lemmasFromQuery = sortingLemmas.processQuery(query);

        List<LemmaEntity> findLemma = sortingLemmas
                .findLemmasInRepository(lemmasFromQuery, siteId);

        List<LemmaEntity> sortedLemmas = sortingLemmas
                .filterAndSortLemmas(findLemma, siteId);

        List<PageEntity> matchingPages = pageFinder
                .findPagesContainingAllLemmas(sortedLemmas);

        LinkedHashMap<PageEntity, Float> sortedPages = relevanceCalculator
                .calculateRelevance(matchingPages);

        List<SearchData> searchData = getSearchData(sortedPages, lemmasFromQuery);

        log.info(MessageLogs.LOG_FINISH_OneSITES_SEARCH);
        return searchData;
    }

    /**
     * Формирует финальный список результатов поиска.
     *
     * @param sortedPages     Отсортированный список страниц с их релевантностью.
     * @param lemmasFromQuery Леммы, полученные из поискового запроса.
     * @return Список объектов SearchData, готовых к выводу пользователю.
     */
    private List<SearchData> getSearchData(LinkedHashMap<PageEntity, Float> sortedPages,
                                           List<String> lemmasFromQuery) {
        List<SearchData> searchData = new ArrayList<>();

        for (PageEntity pageEntity : sortedPages.keySet()) {
            String uri = pageEntity.getPath();
            String content = pageEntity.getContent();
            String title = urlHelper.getTitleFromHtml(content);
            SiteEntity siteEntity = pageEntity.getSiteId();
            String site = siteEntity.getUrl();
            String siteName = siteEntity.getName();
            Float absRelevance = sortedPages.get(pageEntity);

            String clearContent = lemmaExtractor.cleanHtml(content);
            String snippet = snippetGeneration.getSnippet(clearContent, lemmasFromQuery);

            searchData.add(new SearchData(site, siteName, uri, title, snippet, absRelevance));
        }
        return searchData;
    }
}
