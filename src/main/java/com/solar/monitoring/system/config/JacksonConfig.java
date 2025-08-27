package com.solar.monitoring.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {
    
    /**
     * Creates and returns the application's primary Jackson JSON ObjectMapper.
     *
     * The returned mapper has the JavaTimeModule registered so Java 8+ date/time types
     * (e.g., LocalDate, Instant) are serialized/deserialized correctly.
     *
     * @return a configured {@link com.fasterxml.jackson.databind.ObjectMapper} used as the primary JSON mapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    
    /**
     * Creates and exposes a Spring XmlMapper bean configured to handle Java 8 date/time types.
     *
     * <p>Registers the Jackson JavaTimeModule so Java Time (JSR-310) types are serialized/deserialized correctly in XML.
     *
     * @return a configured {@link com.fasterxml.jackson.dataformat.xml.XmlMapper} instance
     */
    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}