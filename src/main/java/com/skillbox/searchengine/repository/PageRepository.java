package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.model.PageEntity;
import com.skillbox.searchengine.model.SiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с таблицей индексов (PageEntity).
 */
@Repository
public interface PageRepository extends JpaRepository<PageEntity, Long> {

    /**
     * Поиск страницы по уникальному пути (URL).
     *
     * @param path Путь страницы (полный URL).
     * @return Сущность страницы, если найдена; null, если страница не найдена.
     */
    PageEntity findByPath(String path);

    /**
     * Найти все страницы, принадлежащие сайту с заданным идентификатором.
     *
     * @param siteId Идентификатор сайта.
     * @return Список страниц, относящихся к данному сайту.
     */
    @Query(value = """
            SELECT p.* FROM page p 
            WHERE p.site_id = :siteId""",
            nativeQuery = true)
    List<PageEntity> findBySiteId(@Param("siteId") Long siteId);

    /**
     * Подсчитывает количество страниц, принадлежащих сайту.
     *
     * @param siteEntity Объект сайта, для которого производится подсчёт.
     * @return Количество страниц для указанного сайта.
     */
    int countBySiteId(SiteEntity siteEntity);

    /**
     * Подсчитывает количество страниц, принадлежащих сайту, по идентификатору сайта.
     *
     * @param siteId Идентификатор сайта.
     * @return Количество страниц для указанного сайта.
     */
    @Query(value = """
            SELECT COUNT(*) FROM page p 
            WHERE p.site_id = :siteId""",
            nativeQuery = true)
    int countPageBySiteId(Long siteId);


}

