package com.tutoringplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String[] allowedOrigins = {};
    private final Api api = new Api();

    public static class Api {
        private String filesBaseUrl = "/api/files/";

        public String getFilesBaseUrl() {
            return filesBaseUrl;
        }

        public void setFilesBaseUrl(String filesBaseUrl) {
            this.filesBaseUrl = filesBaseUrl;
        }
    }

    public String[] getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public Api getApi() {
        return api;
    }
}