package com.skillbox.searchengine.repository;

import com.skillbox.searchengine.model.SiteEntity;
import com.skillbox.searchengine.model.SiteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с таблицей индексов (SiteEntity).
 */
@Repository
public interface SiteRepository extends JpaRepository<SiteEntity, Long> {

    /**
     * Поиск сайта по уникальному URL.
     *
     * @param url Уникальный URL сайта.
     * @return Сущность сайта, если найдена; null, если сайт не найден.
     */
    SiteEntity findByUrl(String url);

    /**
     * Поиск всех сайтов с заданным статусом.
     *
     * @param siteStatus Статус сайта (например, INDEXING, FAILED и т.д.)
     * @return Список сайтов с указанным статусом.
     */
    List<SiteEntity> findByStatus(SiteStatus siteStatus);

    /**
     * Поиск сайта по подобию URL (использует LIKE-запрос).
     *
     * @param url Часть URL сайта.
     * @return Сущность сайта, если найдена; null, если сайт не найден.
     */
    SiteEntity findByUrlLike(String url);

    /**
     * Проверяет существование сайтов с заданным статусом.
     *
     * @param siteStatus Статус сайта.
     * @return True, если такой сайт существует; False, если не существует.
     */
    boolean existsByStatus(SiteStatus siteStatus);
}

