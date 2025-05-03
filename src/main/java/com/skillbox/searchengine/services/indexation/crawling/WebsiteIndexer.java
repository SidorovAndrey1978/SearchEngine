package com.skillbox.searchengine.services.indexation.crawling;

import com.skillbox.searchengine.config.Site;
import com.skillbox.searchengine.dto.indexing.DtoIndex;
import com.skillbox.searchengine.dto.indexing.DtoLemma;
import com.skillbox.searchengine.dto.indexing.DtoPage;
import com.skillbox.searchengine.model.*;
import com.skillbox.searchengine.repository.IndexRepository;
import com.skillbox.searchengine.repository.LemmaRepository;
import com.skillbox.searchengine.repository.PageRepository;
import com.skillbox.searchengine.repository.SiteRepository;
import com.skillbox.searchengine.services.indexation.indexing.IndexBuilder;
import com.skillbox.searchengine.services.indexation.lemmatization.LemmasCollector;
import com.skillbox.searchengine.utils.MessageLogs;
import com.skillbox.searchengine.utils.UrlHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

/**
 * Класс, ответственный за индексацию сайта.
 * <p>
 * Содержит методы для построения индекса, сбора лемм и сохранения данных.
 * <p>
 * Работа начинается с этапа подготовки сайта, затем переходит к
 * этапу индексации страниц и заканчивается созданием индекса.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class WebsiteIndexer implements Runnable {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final LemmasCollector lemmasCollector;
    private final IndexBuilder indexBuilder;
    private final Site site;
    private final UrlHelper urlHelper;

    /**
     * Основной метод выполнения задачи индексации сайта.
     * Производит следующие шаги:
     * - удаление старых данных (если сайт уже индексировался раньше),
     * - сохранение сайта в репозиторий,
     * - индексация страниц сайта,
     * - сбор лемм и сохранение их в репозиторий,
     * - построение и сохранение индекса.
     */
    @Override
    public void run() {

        if (siteRepository.findByUrl(site.getUrl()) != null) {
            deleteData(site);
        }

        try {
            saveSiteToRepository();

            savePagesToTheRepository();

            saveLemmasToRepository();

            saveIndexesToRepository();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(MessageLogs.THE_FLOW_WAS_INTERRUPTED);
        }
    }

    /**
     * Сохраняет сущность сайта в репозиторий.
     * Устанавливает статус индексации и фиксирует текущее время.
     */
    private void saveSiteToRepository() throws InterruptedException {
        checkForInterruption();
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setStatus(SiteStatus.INDEXING);
        siteEntity.setStatusTime(LocalDateTime.now());
        siteEntity.setUrl(site.getUrl());
        siteEntity.setName(site.getName());
        siteRepository.save(siteEntity);
    }

    /**
     * Индексация страниц сайта.
     * Собирает все страницы сайта, сохраняет их в репозиторий.
     */
    private void savePagesToTheRepository() throws InterruptedException {
        checkForInterruption();

        List<PageEntity> pageEntities = new ArrayList<>();
        Set<String> visitedLinks = new HashSet<>();
        List<DtoPage> pages = new ArrayList<>();

        String startUrl = site.getUrl();
        SiteEntity siteEntity = siteRepository.findByUrl(startUrl);

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        WebCrawlerTask task = new WebCrawlerTask(visitedLinks, pages, urlHelper, startUrl);
        forkJoinPool.invoke(task);

        for (DtoPage dtoPage : pages) {
            String pageUrl = dtoPage.getPath();
            String path = urlHelper.getPathToPage(pageUrl);

            PageEntity pageEntity = new PageEntity();
            pageEntity.setSiteId(siteEntity);
            pageEntity.setPath(path);
            pageEntity.setCode(dtoPage.getCode());
            pageEntity.setContent(dtoPage.getContent());
            pageEntities.add(pageEntity);

        }
        pageRepository.saveAll(pageEntities);
        forkJoinPool.shutdown();
    }

    /**
     * Сбор и сохранение лемм для текущего сайта.
     * Проходит по каждой странице сайта, собирает леммы и сохраняет их в репозиторий.
     */
    private void saveLemmasToRepository() throws InterruptedException {
        checkForInterruption();

        SiteEntity siteEntity = siteRepository.findByUrl(site.getUrl());
        List<DtoLemma> dtoLemmas = lemmasCollector.extractLemmasForSite(siteEntity);

        List<LemmaEntity> lemmaEntities = new ArrayList<>();
        for (DtoLemma dtoLemma : dtoLemmas) {
            LemmaEntity lemmaEntity = new LemmaEntity();
            lemmaEntity.setSiteId(siteEntity);
            lemmaEntity.setLemma(dtoLemma.getLemma());
            lemmaEntity.setFrequency(dtoLemma.getFrequency());
            lemmaEntities.add(lemmaEntity);
        }
        lemmaRepository.saveAll(lemmaEntities);

    }

    /**
     * Построение и сохранение индекса.
     * Строит индекс по каждой странице сайта и сохраняет его в репозиторий.
     */
    private void saveIndexesToRepository() throws InterruptedException {
        checkForInterruption();

        SiteEntity siteEntity = siteRepository.findByUrl(site.getUrl());

        List<DtoIndex> dtoIndices = indexBuilder.buildIndexesForSite(siteEntity);

        List<IndexEntity> indexEntities = new ArrayList<>();
        for (DtoIndex dtoIndex : dtoIndices) {
            PageEntity pageEntity = pageRepository.getReferenceById(dtoIndex.getPageId());
            LemmaEntity lemmaEntity = lemmaRepository.getReferenceById(dtoIndex.getLemmaId());

            IndexEntity indexEntity = new IndexEntity();
            indexEntity.setPageId(pageEntity);
            indexEntity.setLemmaId(lemmaEntity);
            indexEntity.setRank(dtoIndex.getRank());
            indexEntities.add(indexEntity);
        }
        indexRepository.saveAll(indexEntities);

        siteEntity.setStatus(SiteStatus.INDEXED);
        siteEntity.setStatusTime(LocalDateTime.now());
        siteRepository.save(siteEntity);
    }

    /**
     * Удаляет старые данные о сайте, если он уже индексировался ранее.
     *
     * @param site сайт, данные которого подлежат удалению.
     */
    private void deleteData(Site site) {
        SiteEntity siteEntity = siteRepository.findByUrl(site.getUrl());
        if (siteEntity != null) {
            siteRepository.delete(siteEntity);
        }
    }

    /**
     * Проверяет, не был ли прерван текущий поток.
     *
     * @throws InterruptedException если поток был прерван.
     */
    private static void checkForInterruption() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException(MessageLogs.THE_FLOW_WAS_INTERRUPTED);
        }
    }
}
