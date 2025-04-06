package com.example.securityjwt.auth;

import lombok.Getter;

@Getter
public enum SecurityUrls {

    PERMIT_ALL(new String[]{
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/auth/**",
            "/error"
    });

    private final String[] urls;

    SecurityUrls(String[] urls) {
        this.urls = urls;
    }

}
