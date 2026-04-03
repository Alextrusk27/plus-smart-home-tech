package ru.practicum.interaction.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.interaction.api.exception.ExceptionHandler;

@Configuration
public class ExceptionHandlerAutoConfiguration {

    @Bean
    public ExceptionHandler exceptionHandler() {
        return new ExceptionHandler();
    }
}
