package fr.vpl.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Configuration for Internationalization (i18n).
 * Defines how the application loads and interprets localized messages.
 */
@Configuration
public class MessageConfig {

    /**
     * Configures the MessageSource bean to use properties files.
     *
     * @return A ResourceBundleMessageSource configured for UTF-8 and 'messages' basename.
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // Look for files named messages.properties, messages_fr.properties, etc.
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        // If a key is missing, return the code itself instead of throwing an error
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}