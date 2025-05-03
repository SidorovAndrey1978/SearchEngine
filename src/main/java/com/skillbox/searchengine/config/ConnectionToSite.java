package com.skillbox.searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "connection-to-site")
public class ConnectionToSite {
    private String userAgent;
    private String referrer;
}
