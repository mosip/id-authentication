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

import io.mosip.authentication.common.service.helper.WebSubSubscriptionHelper;
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
	
	private static Logger logger = IdaLogger.getLogger(BaseIDAWebSubInitializer.class);

	@Value("${ida-websub-resubscription-retry-count:3}")
	private int retryCount;

	/**
	 * Default is Zero which will disable the scheduling.
	 */
	@Value("${ida-websub-resubscription-delay-secs:0}")
	private int reSubscriptionDelaySecs;

	@Autowired
	protected WebSubSubscriptionHelper webSubSubscriptionHelper;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;
	
	
	@Value("${" + SUBSCRIPTIONS_DELAY_ON_STARTUP + ":60000}")
	private int taskSubsctiptionDelay;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info(IdAuthCommonConstants.SESSION_ID, "onApplicationEvent",  this.getClass().getSimpleName(), "Scheduling event subscriptions after (milliseconds): " + taskSubsctiptionDelay);
		taskScheduler.schedule(
				  this::initSubsriptions,
				  new Date(System.currentTimeMillis() + taskSubsctiptionDelay)
				);
		
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
	
	private void scheduleRetrySubscriptions() {
		logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "scheduleRetrySubscriptions",
				"Scheduling re-subscription every " + reSubscriptionDelaySecs + " seconds");
		taskScheduler.scheduleAtFixedRate(this::retrySubscriptions, Instant.now().plusSeconds(reSubscriptionDelaySecs),
				Duration.ofSeconds(reSubscriptionDelaySecs));
	}

	private void retrySubscriptions() {
		// Call Init Subscriptions for the count until no error in the subscription.
		// This will execute once first for sure if retry count is 0 or more. If the
		// subscription fails it will retry subscriptions up to given retry count.
		for (int i = 0; i <= retryCount; i++) {
			if (initSubsriptions()) {
				return;
			}
		}
	}

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

	protected abstract void doInitSubscriptions();

}
