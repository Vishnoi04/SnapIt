package com.snapit.config;

import java.net.URI;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
public class NeonDatabaseConfig {
    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties, Environment environment) {
        String pooledUrl = environment.getProperty("DATABASE_URL");
        if (StringUtils.hasText(pooledUrl)) {
            PostgresUrl connection = parseUrl(pooledUrl);
            properties.setUrl(connection.jdbcUrl());
            if (connection.username() != null) {
                properties.setUsername(connection.username());
            }
            if (connection.password() != null) {
                properties.setPassword(connection.password());
            }
        }
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    public FlywayConfigurationCustomizer neonFlywayConnection(Environment environment) {
        return configuration -> {
            String url = environment.getProperty("FLYWAY_URL");
            if (!StringUtils.hasText(url)) {
                url = environment.getProperty("DATABASE_URL_UNPOOLED");
            }
            if (StringUtils.hasText(url)) {
                PostgresUrl connection = parseUrl(url);
                String username = connection.username() != null
                        ? connection.username()
                        : environment.getProperty("DB_USERNAME");
                String password = connection.password() != null
                        ? connection.password()
                        : environment.getProperty("DB_PASSWORD");
                configuration.dataSource(connection.jdbcUrl(), username, password);
            }
        };
    }

    private static PostgresUrl parseUrl(String url) {
        if (url.startsWith("jdbc:")) {
            return new PostgresUrl(url, null, null);
        }
        if (url.startsWith("postgresql://")) {
            URI uri;
            try {
                uri = URI.create(url);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Invalid PostgreSQL connection URL");
            }
            String authority = uri.getRawAuthority();
            if (!"postgresql".equalsIgnoreCase(uri.getScheme())
                    || uri.getHost() == null
                    || !StringUtils.hasText(uri.getRawPath())
                    || uri.getRawFragment() != null) {
                throw new IllegalArgumentException("Invalid PostgreSQL connection URL");
            }

            String username = null;
            String password = null;
            int userInfoSeparator = uri.getUserInfo() == null ? -1 : uri.getUserInfo().indexOf(':');
            if (userInfoSeparator >= 0) {
                username = uri.getUserInfo().substring(0, userInfoSeparator);
                password = uri.getUserInfo().substring(userInfoSeparator + 1);
            }

            int authoritySeparator = authority.lastIndexOf('@');
            String hostAndPort = authoritySeparator >= 0
                    ? authority.substring(authoritySeparator + 1)
                    : authority;
            String jdbcUrl = "jdbc:postgresql://" + hostAndPort + uri.getRawPath();
            if (uri.getRawQuery() != null) {
                jdbcUrl += "?" + uri.getRawQuery();
            }
            return new PostgresUrl(jdbcUrl, username, password);
        }
        throw new IllegalArgumentException("Expected a PostgreSQL JDBC or connection URL");
    }

    private record PostgresUrl(String jdbcUrl, String username, String password) {
    }
}
