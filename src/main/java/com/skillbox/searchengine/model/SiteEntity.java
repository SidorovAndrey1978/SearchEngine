package com.skillbox.searchengine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "site")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiteEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status_time", nullable = false)
    private LocalDateTime statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SiteStatus status;

    @Column(name = "url", nullable = false, columnDefinition = "VARCHAR(255)")
    private String url;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

    @OneToMany(mappedBy = "siteId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PageEntity> pages = new ArrayList<>();

    @OneToMany(mappedBy = "siteId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LemmaEntity> lemma = new ArrayList<>();
    
}
