package io.mosip.authentication.common.service.websub;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.SUBSCRIPTIONS_DELAY_ON_STARTUP;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.common.service.websub.impl.MasterDataUpdateEventInitializer;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Websub Initializer for External facing IDA services such as Auth, EKYC and
 * OTP services.
 *
 * @author Loganathan Sekar
 * @author Manoj SP
 */

@Component
public final class IdAuthWebSubInitializer extends CacheUpdatingWebsubInitializer {
	
	private static Logger logger = IdaLogger.getLogger(IdAuthWebSubInitializer.class);
	
	@Autowired
	private MasterDataUpdateEventInitializer masterDataUpdateEventInitializer;
	
	@Autowired
	private IdAuthFraudAnalysisEventPublisher fraudEventPublisher;
	
	@Autowired
	private AuthTransactionStatusEventPublisher authTransactionStatusEventPublisher;
	
	/** The task subsctiption delay. */
	@Value("${" + SUBSCRIPTIONS_DELAY_ON_STARTUP + ":60000}")
	private int taskSubsctiptionDelay;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info(IdAuthCommonConstants.SESSION_ID, "onApplicationEvent",  this.getClass().getSimpleName(), "Scheduling event subscriptions after (milliseconds): " + taskSubsctiptionDelay);
		taskScheduler.schedule(() -> {
			//Invoke topic registrations. This is done only once.
			registerTopics();
			//Init topic subscriptions
			initSubsriptions();
		}, new Date(System.currentTimeMillis() + taskSubsctiptionDelay));
		super.onApplicationEvent(event);
	}

	/**
	 * Do init subscriptions.
	 */
	@Override
	protected void doInitSubscriptions() {
		webSubHelper.initSubscriber(masterDataUpdateEventInitializer, this::isCacheEnabled);
	}

	/**
	 * Do register topics.
	 */
	@Override
	protected void doRegisterTopics() {
		webSubHelper.initRegistrar(masterDataUpdateEventInitializer, this::isCacheEnabled);
		webSubHelper.initRegistrar(fraudEventPublisher);
		webSubHelper.initRegistrar(authTransactionStatusEventPublisher);

	}

}
