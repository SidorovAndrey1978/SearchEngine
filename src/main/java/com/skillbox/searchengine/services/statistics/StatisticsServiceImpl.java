package com.skillbox.searchengine.services.statistics;

import com.skillbox.searchengine.dto.statistics.DetailedStatisticsItem;
import com.skillbox.searchengine.dto.statistics.StatisticsData;
import com.skillbox.searchengine.dto.statistics.StatisticsResponse;
import com.skillbox.searchengine.dto.statistics.TotalStatistics;
import com.skillbox.searchengine.model.SiteEntity;
import com.skillbox.searchengine.repository.LemmaRepository;
import com.skillbox.searchengine.repository.PageRepository;
import com.skillbox.searchengine.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;


    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics overallStatistics = getTotal();
        List<DetailedStatisticsItem> detailedStatistics = getStatisticData();
        StatisticsData statisticsData =
                new StatisticsData(overallStatistics, detailedStatistics);
        boolean result = true;
        return new StatisticsResponse(result, statisticsData);
    }


    private TotalStatistics getTotal() {
        int sites = (int) siteRepository.count();
        int pages = (int) pageRepository.count();
        int lemmas = (int) lemmaRepository.count();
        boolean indexing = true;
        return new TotalStatistics(sites, pages, lemmas, indexing);
    }

    private DetailedStatisticsItem getDetailed(SiteEntity siteEntity) {
        String url = siteEntity.getUrl();
        String name = siteEntity.getName();
        String status = siteEntity.getStatus().toString();
        LocalDateTime statusTime = siteEntity.getStatusTime();
        String error = siteEntity.getLastError();
        int pages = pageRepository.countBySiteId(siteEntity);
        int lemmas = lemmaRepository.countBySiteId(siteEntity);
        return new DetailedStatisticsItem(url, name, status, statusTime,
                error, pages, lemmas);
    }

    private List<DetailedStatisticsItem> getStatisticData() {
        List<SiteEntity> siteEntities = siteRepository.findAll();
        return siteEntities.stream()
                .map(this::getDetailed)
                .collect(Collectors.toList());
    }
}
