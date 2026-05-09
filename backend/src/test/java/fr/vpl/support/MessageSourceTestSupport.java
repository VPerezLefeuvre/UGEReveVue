package fr.vpl.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Shared helper for tests that assert localized messages resolved by Spring.
 */
public abstract class MessageSourceTestSupport {

    @Autowired
    private MessageSource messageSource;

    protected String message(String code) {
        return messageSource.getMessage(code, null, Locale.ENGLISH);
    }
}
