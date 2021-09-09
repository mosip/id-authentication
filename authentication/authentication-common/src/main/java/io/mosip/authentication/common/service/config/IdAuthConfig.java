package io.mosip.authentication.common.service.config;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_ERRORMESSAGES_DEFAULT_LANG;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
@EnableAsync
public abstract class IdAuthConfig extends HibernateDaoConfig {

	/** The environment. */
	@Autowired
	private Environment environment;

	/**
	 * Initialize.
	 */
	@PostConstruct
	public void initialize() {
		IdType.initializeAliases(environment);
	}

	/**
	 * Rest template.
	 *
	 * @return the rest template
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	/**
	 * Locale resolver.
	 *
	 * @return the locale resolver
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		Locale locale = new Locale(environment.getProperty(MOSIP_ERRORMESSAGES_DEFAULT_LANG));
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
		source.addBasenames("errormessages", "actionmessages");
		return source;
	}

	/**
	 * Thread pool task scheduler.
	 *
	 * @return the thread pool task scheduler
	 */
	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		return threadPoolTaskScheduler;
	}

	/**
	 * Checks if is finger auth enabled.
	 *
	 * @return true, if is finger auth enabled
	 */
	protected abstract boolean isFingerAuthEnabled();

	/**
	 * Checks if is face auth enabled.
	 *
	 * @return true, if is face auth enabled
	 */
	protected abstract boolean isFaceAuthEnabled();

	/**
	 * Checks if is iris auth enabled.
	 *
	 * @return true, if is iris auth enabled
	 */
	protected abstract boolean isIrisAuthEnabled();

}
