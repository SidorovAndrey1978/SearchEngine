package com.skillbox.searchengine.services.indexation.crawling;

import com.skillbox.searchengine.config.Site;
import com.skillbox.searchengine.config.SitesList;
import com.skillbox.searchengine.exception.PageOutsideConfigured;
import com.skillbox.searchengine.model.PageEntity;
import com.skillbox.searchengine.model.SiteEntity;
import com.skillbox.searchengine.model.SiteStatus;
import com.skillbox.searchengine.repository.PageRepository;
import com.skillbox.searchengine.repository.SiteRepository;
import com.skillbox.searchengine.utils.UrlHelper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Компонент, ответственный за индексацию страниц.
 * <p>
 * Формирует сущности SiteEntity и PageEntity, сохраняет их в репозитории.
 * Проводит предварительную проверку на соответствие URL правилам и извлекает контент страницы.
 */
@RequiredArgsConstructor
@Component
public class PageIndexer {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final SitesList sitesList;
    private final UrlHelper urlHelper;

    /**
     * Запускает процедуру индексации страницы.
     *
     * @param page URL страницы, которую нужно проиндексировать.
     * @throws PageOutsideConfigured если страница находится за пределами разрешенных сайтов.
     */
    public void start(String page) {
        String hostSite = urlHelper.getHostFromPage(page);
        String path = urlHelper.getPathToPage(page);

        SiteEntity siteEntity;

        if (siteRepository.findByUrlLike("%" + hostSite + "%") == null) {
            String name = getNameSite(page);
            String url = getUrlSite(page);
            siteEntity = new SiteEntity();
            siteEntity.setStatus(SiteStatus.INDEXING);
            siteEntity.setStatusTime(LocalDateTime.now());
            siteEntity.setUrl(url);
            siteEntity.setName(name);
            siteRepository.save(siteEntity);
        } else {
            siteEntity = siteRepository.findByUrlLike("%" + hostSite + "%");
        }
        if (pageRepository.findByPath(path) != null) {
            PageEntity pageEntity = pageRepository.findByPath(path);
            pageRepository.deleteById(pageEntity.getId());
        }

        PageEntity pageEntity = new PageEntity();
        Optional<Document> optionalDoc = urlHelper.getConnection(page);
        if (optionalDoc.isPresent()) {
            Document document = optionalDoc.get();
            Connection.Response response = document.connection().response();
            int code = response.statusCode();
            String htmlContent = document.outerHtml();
            pageEntity.setSiteId(siteEntity);
            pageEntity.setPath(path);
            pageEntity.setCode(code);
            pageEntity.setContent(htmlContent);
        } else {
            throw new PageOutsideConfigured();
        }
        pageRepository.save(pageEntity);
    }

    /**
     * Определяет URL сайта, которому принадлежит данная страница.
     *
     * @param page URL страницы.
     * @return URL сайта, если страница соответствует какому-либо известному сайту; пустая строка, если не найдено.
     */
    private String getUrlSite(String page) {
        String pageHost = urlHelper.getHostFromPage(page);
        for (Site site : sitesList.getSites()) {
            if (site.getUrl().contains(pageHost)) {
                return site.getUrl();
            }
        }
        return "";
    }

    /**
     * Определяет имя сайта, которому принадлежит данная страница.
     *
     * @param page URL страницы.
     * @return Имя сайта, если страница соответствует какому-либо известному сайту; пустая строка, если не найдено.
     */
    private String getNameSite(String page) {
        String pageHost = urlHelper.getHostFromPage(page);
        for (Site site : sitesList.getSites()) {
            if (site.getUrl().contains(pageHost)) {
                return site.getName();
            }
        }
        return "";
    }
}
