package com.skillbox.searchengine.services.indexation;

import com.skillbox.searchengine.config.Site;
import com.skillbox.searchengine.config.SitesList;
import com.skillbox.searchengine.exception.PageOutsideConfigured;
import com.skillbox.searchengine.model.SiteEntity;
import com.skillbox.searchengine.model.SiteStatus;
import com.skillbox.searchengine.repository.IndexRepository;
import com.skillbox.searchengine.repository.LemmaRepository;
import com.skillbox.searchengine.repository.PageRepository;
import com.skillbox.searchengine.repository.SiteRepository;
import com.skillbox.searchengine.services.indexation.crawling.PageIndexer;
import com.skillbox.searchengine.services.indexation.crawling.WebsiteIndexer;
import com.skillbox.searchengine.services.indexation.indexing.IndexBuilder;
import com.skillbox.searchengine.services.indexation.lemmatization.LemmasCollector;
import com.skillbox.searchengine.utils.MessageLogs;
import com.skillbox.searchengine.utils.UrlHelper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final LemmasCollector lemmasCollector;
    private final IndexBuilder indexBuilder;
    private final PageIndexer pageIndexer;
    private final UrlHelper urlHelper;
    private final SitesList sitesList;
    private ExecutorService executorService;

    @Transactional
    @Override
    public void startIndexing() {
        if (isIndexing()) {
            throw new IllegalStateException(MessageLogs.INDEXING_IS_ALREADY_RUNNING);
        }
        List<Site> siteList = sitesList.getSites();
        executorService = Executors.newFixedThreadPool(siteList.size());

        for (Site site : siteList) {
            executorService.submit(new WebsiteIndexer(siteRepository,
                    pageRepository, lemmaRepository, indexRepository,
                    lemmasCollector, indexBuilder, site, urlHelper));
        }
    }

    @Transactional
    @Override
    public void startPageIndexing(String page) {
        if (!isPageAvailable(page)) {
            throw new PageOutsideConfigured();
        }
        pageIndexer.start(page);
    }

    @Transactional
    @Override
    public void stopIndexing() {
        if (!isIndexing()) {
            throw new IllegalStateException(MessageLogs.INDEXING_IS_NOT_RUNNING);
        }
        executorService.shutdownNow();
        List<SiteEntity> indexingSite = siteRepository.findByStatus(SiteStatus.INDEXING);
        for (SiteEntity site : indexingSite) {
            site.setStatusTime(LocalDateTime.now());
            site.setStatus(SiteStatus.FAILED);
            site.setLastError(MessageLogs.INDEXATION_STOPPED_BY_USER);
            siteRepository.save(site);
        }
    }

    private boolean isPageAvailable(String page) {
        String pageHost = urlHelper.getHostFromPage(page);
        for (Site site : sitesList.getSites()) {
            if (site.getUrl().contains(pageHost)) {
                return true;
            }
        }
        return false;
    }

    private boolean isIndexing() {
        return siteRepository.existsByStatus(SiteStatus.INDEXING);
    }


}
