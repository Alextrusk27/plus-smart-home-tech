package ru.practicum.interaction.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.interaction.api.exception.ExceptionController;

@Configuration
public class ExceptionHandlerAutoConfiguration {

    @Bean
    public ExceptionController exceptionController() {
        return new ExceptionController();
    }
}
