package de.gedoplan.showcase.springaidemo.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .requestFactory(new BufferingClientHttpRequestFactory(
                        ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS)))
                .requestInterceptors(interceptors -> interceptors.add(new HttpLoggingInterceptor()));
    }

}
