package ru.sqbt.plsqltests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@RequiredArgsConstructor
@Configuration
public class AppConfig {
    @Bean("YmlCaseMapper")
    public ObjectMapper objectMapper() {
        YAMLMapper mapper = new YAMLMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        return mapper;
    }
}
