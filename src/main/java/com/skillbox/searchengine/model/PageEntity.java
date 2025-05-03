package com.skillbox.searchengine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "page")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    private SiteEntity siteId;

    @Column(name = "path", columnDefinition = "TEXT", nullable = false)
    private String path;

    @Column(name = "code", nullable = false)
    private int code;

    @Lob
    @Column(name = "content", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "pageId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<IndexEntity> index = new ArrayList<>();

}
