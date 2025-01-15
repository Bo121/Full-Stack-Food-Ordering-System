package com.app.quickbite.config;

import com.app.quickbite.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * Configure static resource mapping
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Set up static resource mappings
        log.info("Starting static resource mapping...");
        long startTime = System.currentTimeMillis();
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        log.info("Static resource mapping completed [Elapsed time: " + (System.currentTimeMillis() - startTime) + "ms]");
    }

    /**
     * Extend MVC message converters
     * <p>The default message converters cannot handle certain issues with frontend JS data, 
     * such as 19-digit IDs being truncated to 18 digits. To resolve this, a custom message converter 
     * is needed. This custom converter is already defined in the `common` package under `JacksonConfig`. 
     * It converts `long` type data to `String` type data to prevent data loss on the frontend.
     *
     * @param converters List of message converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Create a custom message converter object
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // Set the object mapper, using Jackson to convert objects to JSON strings
        converter.setObjectMapper(new JacksonObjectMapper());
        // Add the custom message converter to the list of converters
        converters.add(0, converter); // Add the custom converter to the first position

        log.info("Custom message converter has been added to the list of message converters");
    }
}

