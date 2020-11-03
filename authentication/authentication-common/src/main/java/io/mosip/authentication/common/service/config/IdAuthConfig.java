package io.mosip.authentication.common.service.config;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_ERRORMESSAGES_DEFAULT_LANG;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import io.mosip.authentication.common.service.cache.MasterDataCache;
import io.mosip.authentication.common.service.cache.PartnerServiceCache;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
public abstract class IdAuthConfig extends HibernateDaoConfig {

	private static Logger logger = IdaLogger.getLogger(IdAuthConfig.class);

	/** The environment. */
	@Autowired
	private Environment environment;

	@Autowired
	private MasterDataCache masterDataCache;

	@Autowired
	private PartnerServiceCache partnerServiceCache;

	@Value("${ida-cache-ttl-in-days:0")
	public int cacheTTL;

	@Value("${spring.cache.type:simple}")
	public String cacheType;

	/**
	 * Initialize.
	 */
	@PostConstruct
	public void initialize() {
		IdType.initializeAliases(environment);
		ScheduleCacheEviction();
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

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		return threadPoolTaskScheduler;
	}

	private void ScheduleCacheEviction() {
		if (cacheTTL > 0 && !StringUtils.equalsIgnoreCase(cacheType, "none")) {
			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "cacheTTL",
					"Scheduling cache eviction every " + cacheTTL + " day(s)");
			threadPoolTaskScheduler().scheduleAtFixedRate(masterDataCache::clearMasterDataCache,
					Instant.now().plusSeconds(cacheTTL), Duration.ofSeconds(cacheTTL));
			threadPoolTaskScheduler().scheduleAtFixedRate(partnerServiceCache::clearPartnerServiceCache,
					Instant.now().plusSeconds(cacheTTL), Duration.ofSeconds(cacheTTL));
		}
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
