package io.mosip.authentication.service.config;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
@Configuration
public class IdAuthConfig implements WebMvcConfigurer {

    /** The environment. */
    @Autowired
    private Environment environment;

    /** The mapper. */
    @Autowired
    private ObjectMapper mapper;

    /**
     * Set the timestamp for request and response.
     */
    @PostConstruct
    public void setup() {
	mapper.setDateFormat(new SimpleDateFormat(environment.getProperty("datetime.pattern")));
    }

    /**
     * Locale resolver.
     *
     * @return the locale resolver
     */
    @Bean
    public LocaleResolver localeResolver() {
	SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
	Locale locale = new Locale(environment.getProperty("mosip.errormessages.default-lang"));
	LocaleContextHolder.setLocale(locale);
	sessionLocaleResolver.setDefaultLocale(locale);
	return sessionLocaleResolver;
    }

    /**
     * Message source.
     *
     * @return the message source
     */
    @Bean
    public MessageSource messageSource() {
	ResourceBundleMessageSource source = new ResourceBundleMessageSource();
	source.addBasenames("errormessages", "eKycPDFTemplate");
	return source;
    }
}
