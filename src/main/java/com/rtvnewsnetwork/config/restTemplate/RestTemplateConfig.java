package com.rtvnewsnetwork.config.restTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(ObjectMapper objectMapper) {
        RestTemplate restTemplate = new RestTemplate();

        // Replace the default Jackson converter with one using Spring's ObjectMapper
        for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
            if (restTemplate.getMessageConverters().get(i) instanceof MappingJackson2HttpMessageConverter) {
                restTemplate.getMessageConverters().set(i, new MappingJackson2HttpMessageConverter(objectMapper));
            }
        }

        return restTemplate;
    }
}
