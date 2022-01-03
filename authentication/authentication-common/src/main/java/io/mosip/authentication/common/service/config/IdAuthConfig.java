package io.mosip.authentication.common.service.config;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_ERRORMESSAGES_DEFAULT_LANG;

import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
public abstract class IdAuthConfig extends HibernateDaoConfig {

	/** The environment. */
	@Autowired
	private EnvUtil environment;
	
	/**
	 * Initialize.
	 */
	@PostConstruct
	public void initialize() {
		IdType.initializeAliases(environment);
	}

	/**
	 * Locale resolver.
	 *
	 * @return the locale resolver
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		Locale locale = new Locale(EnvUtil.getErrorMsgDefaultLang());
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
	
	@Bean
	public AfterburnerModule afterburnerModule() {
	  return new AfterburnerModule();
	}
	
	private ArrayList<String> serviceNames() {
		ArrayList<String> list = new ArrayList<String>();
		for(RestServicesConstants service: RestServicesConstants.values()) {
			list.add(service.getServiceName());
		}
		return list;
	}

	@Bean
	public RestRequestBuilder getRestRequestBuilder() {
		return new RestRequestBuilder(serviceNames());
	}

}
