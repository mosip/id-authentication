package io.mosip.authentication.common.service.websub;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * Subscribes to the topics on the application startup. (some configured delay is applied for the application to be ready for the intent verification by the websub hub to work)
 *
 * Also if configured, this listener schedules re-subscription of topics which is done as a
 * work-around for the bug: MOSIP-9496. By default the
 * ida-websub-resubscription-delay-secs value is set to 0 that disables this
 * workaround. To enable this, that property should be assigned with a positive
 * number like 1 * 60 * 60 = 3600 for one hour.
 * 
 * @author Loganathan Sekar
 * @author Manoj SP
 *
 */
 
@Component
public abstract class BaseIDAWebSubInitializer implements ApplicationListener<ApplicationReadyEvent>{
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(BaseIDAWebSubInitializer.class);

	/**
	 * Default is Zero which will disable the scheduling.
	 */
	@Value("${ida-websub-resubscription-delay-secs:0}")
	private int reSubscriptionDelaySecs;

	/** The web sub helper. */
	@Autowired
	protected WebSubHelper webSubHelper;
	
	/** The task scheduler. */
	@Autowired
	protected ThreadPoolTaskScheduler taskScheduler;
	
	/**
	 * On application event.
	 *
	 * @param event the event
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (reSubscriptionDelaySecs > 0) {
			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "onApplicationEvent",
					"Work around for web-sub notification issue after some time.");
			scheduleRetrySubscriptions();
		} else {
			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "scheduleRetrySubscriptions",
					"Scheduling for re-subscription is Disabled as the re-subsctription delay value is: "
							+ reSubscriptionDelaySecs);
		}
		
	}
	
	/**
	 * Schedule retry subscriptions.
	 */
	private void scheduleRetrySubscriptions() {
		logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "scheduleRetrySubscriptions",
				"Scheduling re-subscription every " + reSubscriptionDelaySecs + " seconds");
		taskScheduler.scheduleAtFixedRate(this::initSubsriptions, Instant.now().plusSeconds(reSubscriptionDelaySecs),
				Duration.ofSeconds(reSubscriptionDelaySecs));
	}

	/**
	 * Inits the subsriptions.
	 *
	 * @return true, if successful
	 */
	public int initSubsriptions() {
		try {
			logger.info(IdAuthCommonConstants.SESSION_ID, "initSubsriptions", "", "Initializing subscribptions..");
			doInitSubscriptions();
			logger.info(IdAuthCommonConstants.SESSION_ID, "initSubsriptions", "", "Initialized subscribptions.");
			return HttpStatus.OK.value();
		} catch (ResourceAccessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "FATAL: initSubsriptions", "",
					"Initializing subscriptions failed: " + e.getMessage());
			return HttpStatus.SERVICE_UNAVAILABLE.value();
		} catch (Exception e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "FATAL: initSubsriptions", "",
					"Initializing subscriptions failed: " + e.getMessage());
			return HttpStatus.INTERNAL_SERVER_ERROR.value();
		}
	}
	
	/**
	 * Register topics.
	 *
	 * @return true, if successful
	 */
	public boolean registerTopics() {
		try {
			logger.info(IdAuthCommonConstants.SESSION_ID, "registerTopics", "", "Registering Topics..");
			doRegisterTopics();
			logger.info(IdAuthCommonConstants.SESSION_ID, "registerTopics", "", "Registered subscribptions.");
			return true;
		} catch (Exception e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "registerTopics", "",
					"Topics registration failed: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Do init subscriptions.
	 */
	protected abstract void doInitSubscriptions();
	
	/**
	 * Do register topics.
	 */
	protected abstract void doRegisterTopics();

}
