package io.mosip.authentication.common.service.websub;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.SUBSCRIPTIONS_DELAY_ON_STARTUP;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

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
	private ThreadPoolTaskScheduler taskScheduler;
	
	
	/** The task subsctiption delay. */
	@Value("${" + SUBSCRIPTIONS_DELAY_ON_STARTUP + ":60000}")
	private int taskSubsctiptionDelay;
	
	/**
	 * On application event.
	 *
	 * @param event the event
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info(IdAuthCommonConstants.SESSION_ID, "onApplicationEvent",  this.getClass().getSimpleName(), "Scheduling event subscriptions after (milliseconds): " + taskSubsctiptionDelay);
		taskScheduler.schedule(() -> {
			//Invoke topic registrations. This is done only once.
			//Note: With authenticated websub, only register topics which are only published by IDA
			registerTopics();
			//Init topic subscriptions
			initSubsriptions();
		}, new Date(System.currentTimeMillis() + taskSubsctiptionDelay));
		
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
	private boolean initSubsriptions() {
		try {
			logger.info(IdAuthCommonConstants.SESSION_ID, "initSubsriptions", "", "Initializing subscribptions..");
			doInitSubscriptions();
			logger.info(IdAuthCommonConstants.SESSION_ID, "initSubsriptions", "", "Initialized subscribptions.");
			return true;
		} catch (Exception e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "initSubsriptions", "",
					"Initializing subscribptions failed: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Register topics.
	 *
	 * @return true, if successful
	 */
	private boolean registerTopics() {
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
	protected abstract int doInitSubscriptions();
	
	/**
	 * Do register topics.
	 */
	protected abstract int doRegisterTopics();

}
