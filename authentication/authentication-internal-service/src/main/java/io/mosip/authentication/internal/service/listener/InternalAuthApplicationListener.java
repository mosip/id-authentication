package io.mosip.authentication.internal.service.listener;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.SUBSCRIPTIONS_DELAY_ON_STARTUP;

import java.util.Date;

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

@Component
public class InternalAuthApplicationListener implements ApplicationListener<ApplicationReadyEvent>{
	
	private static Logger logger = IdaLogger.getLogger(WebSubSubscriptionHelper.class);

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;
	
	@Value("${" + SUBSCRIPTIONS_DELAY_ON_STARTUP + ":60000}")
	private int taskSubsctiptionDelay;
	
	@Autowired
	private WebSubSubscriptionHelper webSubSubscriptionHelper;

	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info(IdAuthCommonConstants.SESSION_ID, "onApplicationEvent",  "", "Scheduling event subscriptions after (milliseconds): " + taskSubsctiptionDelay);
		taskScheduler.schedule(
				  this::initSubsriptions,
				  new Date(System.currentTimeMillis() + taskSubsctiptionDelay)
				);
		
	}
	
	private void initSubsriptions() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "initSubsriptions",  "", "Initializing subscribptions..");
		webSubSubscriptionHelper.initSubsriptions();
	}

}
