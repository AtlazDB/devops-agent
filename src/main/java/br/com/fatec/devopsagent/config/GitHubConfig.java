package br.com.fatec.devopsagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github")
public record GitHubConfig(
        String token,
        String organization,
        String repository
) {
}