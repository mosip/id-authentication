package io.mosip.authentication.common.service.config;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.builder.RestRequestBuilder;
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
	
	private static final Logger logger = IdaLogger.getLogger(IdAuthConfig.class);

	/** The environment. */
	@Autowired
	private EnvUtil environment;
	
	/**
	 * Initialize.
	 */
	@PostConstruct
	public void initialize() {
		IdType.initializeAliases(environment.getEnvironment());
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
	
	@Bean
	public RestRequestBuilder getRestRequestBuilder() {
		return new RestRequestBuilder(Arrays.stream(RestServicesConstants.values())
				.map(RestServicesConstants::getServiceName).collect(Collectors.toList()));
	}
	
	@Bean
	@Primary
	public Executor executor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(Math.floorDiv(EnvUtil.getActiveAsyncThreadCount(), 3));
	    executor.setMaxPoolSize(EnvUtil.getActiveAsyncThreadCount());
	    executor.setThreadNamePrefix("idauth-");
	    executor.setWaitForTasksToCompleteOnShutdown(true);
	    executor.initialize();
	    return executor;
	}

	@Bean
	@Qualifier("webSubHelperExecutor")
	public Executor webSubHelperExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(Math.floorDiv(EnvUtil.getActiveAsyncThreadCount(), 3));
	    executor.setMaxPoolSize(EnvUtil.getActiveAsyncThreadCount());
	    executor.setThreadNamePrefix("idauth-websub-");
	    executor.setWaitForTasksToCompleteOnShutdown(true);
	    executor.initialize();
	    return executor;
	}

	@Bean
	@Qualifier("fraudAnalysisExecutor")
	public Executor fraudAnalysisExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(Math.floorDiv(EnvUtil.getActiveAsyncThreadCount(), 3));
	    executor.setMaxPoolSize(EnvUtil.getActiveAsyncThreadCount());
	    executor.setThreadNamePrefix("idauth-fraud-analysis-");
	    executor.setWaitForTasksToCompleteOnShutdown(true);
	    executor.initialize();
	    return executor;
	}
	
	@Scheduled(fixedRateString = "${" + "mosip.ida.monitor-thread-queue-in-ms" + ":10000}")
	public void monitorThreadQueueLimit() {
		if (StringUtils.isNotBlank(EnvUtil.getMonitorAsyncThreadQueue())) {
			ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) executor();
			ThreadPoolTaskExecutor webSubHelperExecutor = (ThreadPoolTaskExecutor) webSubHelperExecutor();
			ThreadPoolTaskExecutor fraudAnalysisExecutor = (ThreadPoolTaskExecutor) fraudAnalysisExecutor();
			String monitoringLog = "Thread Name : {} Thread Active Count: {} Thread Task count: {} Thread queue count: {}";
			logThreadQueueDetails(threadPoolTaskExecutor, threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().size(), monitoringLog);
			logThreadQueueDetails(webSubHelperExecutor, webSubHelperExecutor.getThreadPoolExecutor().getQueue().size(), monitoringLog);
			logThreadQueueDetails(fraudAnalysisExecutor, fraudAnalysisExecutor.getThreadPoolExecutor().getQueue().size(), monitoringLog);
		}
	}

	private void logThreadQueueDetails(ThreadPoolTaskExecutor threadPoolTaskExecutor, int threadPoolQueueSize,
			String monitoringLog) {
		if (threadPoolQueueSize > EnvUtil.getAsyncThreadQueueThreshold())
			logger.info(monitoringLog, threadPoolTaskExecutor.getThreadNamePrefix(),
					threadPoolTaskExecutor.getActiveCount(),
					threadPoolTaskExecutor.getThreadPoolExecutor().getTaskCount(), threadPoolQueueSize);
	}

}
