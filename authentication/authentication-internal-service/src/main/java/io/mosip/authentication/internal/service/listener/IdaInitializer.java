package io.mosip.authentication.internal.service.listener;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.internal.service.integration.WebSubSubscriptionHelper;
import io.mosip.kernel.core.logger.spi.Logger;

//TODO To be removed. Added as a work-around for the bug: MOSIP-9496
@Component
public class IdaInitializer implements ApplicationListener<ApplicationReadyEvent>{
	
	private static Logger logger = IdaLogger.getLogger(IdaInitializer.class);
	
	@Value("${retry-count:3}")
	private int retryCount;

	@Value("${resubscription-delay-secs:7200}") // Default is 60 * 60 * 2 = 2 hours
	private int reSubscriptionDelaySecs;
	
	@Autowired
	private WebSubSubscriptionHelper webSubSubscriptionHelper;
	
	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "onApplicationEvent",  "Work around for web-sub notification issue after some time.");
		scheduleRetrySubscriptions();
	}

	private void scheduleRetrySubscriptions() {
		logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "scheduleRetrySubscriptions",  "Scheduling re-subscription every " + reSubscriptionDelaySecs + " seconds");
		taskScheduler.scheduleAtFixedRate(this::retrySubscriptions,
				Instant.now().plusSeconds(reSubscriptionDelaySecs), 
				Duration.ofSeconds(reSubscriptionDelaySecs));
	}

	private void retrySubscriptions() {
		//Call Init Subscriptions for the count until no error in the subscription
		for(int i = 0; i < retryCount; i++) {
			if(initSubsriptions()) {
				return;
			}
		}
	}

	private boolean initSubsriptions() {
		try {
			logger.info(IdAuthCommonConstants.SESSION_ID, "initSubsriptions",  "", "Initializing subscribptions..");
			webSubSubscriptionHelper.initSubsriptions();
			logger.info(IdAuthCommonConstants.SESSION_ID, "initSubsriptions",  "", "Initialized subscribptions.");
			return true;
		} catch (Exception e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "initSubsriptions",  "", "Initializing subscribptions failed: " + e.getMessage());
			return false;
		}
	}
	

}
