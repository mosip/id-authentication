package io.mosip.authentication.service.config;

import java.util.Locale;

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

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
@Configuration
public class IdAuthConfig implements WebMvcConfigurer {

	@Autowired
	private Environment environment;

	   @Bean
	    public LocaleResolver localeResolver() {
	        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
	        Locale locale = new Locale(environment.getProperty("mosip.errormessages.default-lang"));
	        LocaleContextHolder.setLocale(locale);
	        sessionLocaleResolver.setDefaultLocale(locale);
	        return sessionLocaleResolver;
	    }
	   
	   @Bean
	   public MessageSource messageSource() {
		   ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		   source.setBasename("errormessages");
		   return source;
	   }
}
