package io.mosip.authentication.common.service.helper;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.WEBSUB_PUBLISH_URL;

import java.util.function.Supplier;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.WebSubEventSubcriber;
import io.mosip.authentication.common.service.websub.WebSubEventTopicRegistrar;
import io.mosip.authentication.common.service.websub.dto.EventModel;
import io.mosip.authentication.common.service.websub.impl.BaseWebSubEventsInitializer;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.retry.WithRetry;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.websub.spi.PublisherClient;

/**
 * The Class WebSubHelper.
 * 
 * @author Loganathan Sekar
 */
@Component
public class WebSubHelper {
	
	private static final Logger logger = IdaLogger.getLogger(WebSubHelper.class);
	
	/** The Constant PUBLISHER_IDA. */
	private static final String PUBLISHER_IDA = "IDA";
	
	/** The publisher. */
	@Autowired
	private PublisherClient<String, Object, HttpHeaders> publisher;
	
	/** The websub publish url. */
	@Value("${" + WEBSUB_PUBLISH_URL + "}")
	private String websubPublishUrl;

	/**
	 * Inits the subscriber.
	 *
	 * @param subscriber the subscriber
	 */
	@Async
	public void initSubscriber(WebSubEventSubcriber subscriber) {
		initSubscriber(subscriber, null);
	}
	
	/**
	 * Inits the subscriber.
	 *
	 * @param subscriber the subscriber
	 * @param enableTester the enable tester
	 */
	@Async
	public void initSubscriber(WebSubEventSubcriber subscriber, Supplier<Boolean> enableTester) {
		try {
			subscriber.subscribe(enableTester);
		} catch (Exception e) {
			//Just logging the exception to avoid other further subscriptions failure
			logger.error(IdAuthCommonConstants.SESSION_ID, "initSubscriber",  this.getClass().getSimpleName(), "FATAL: Subscription failed for:" + subscriber.getClass().getCanonicalName());
		}
	}
	
	/**
	 * Inits the registrar.
	 *
	 * @param registrar the registrar
	 */
	public void initRegistrar(WebSubEventTopicRegistrar registrar) {
		initRegistrar(registrar, null);
	}
	
	/**
	 * Inits the registrar.
	 *
	 * @param registrar the registrar
	 * @param enableTester the enable tester
	 */
	@WithRetry
	public void initRegistrar(WebSubEventTopicRegistrar registrar, Supplier<Boolean> enableTester) {
		registrar.register(enableTester);
	}
	
	/**
	 * Publish event.
	 *
	 * @param <U> the generic type
	 * @param eventTopic the event topic
	 * @param eventModel the event model
	 */
	@WithRetry
	public <U> void publishEvent(String eventTopic, U eventModel) {
		publisher.publishUpdate(eventTopic, eventModel, MediaType.APPLICATION_JSON_VALUE, null, websubPublishUrl);
	}
	
	/**
	 * Creates the event model.
	 *
	 * @param <T> the generic type
	 * @param <S> the generic type
	 * @param topic the topic
	 * @param event the event
	 * @return the event model
	 */
	public <T,S> EventModel<T> createEventModel(String topic, T event) {
		EventModel<T> eventModel = new EventModel<>();
		eventModel.setEvent(event);
		eventModel.setPublisher(PUBLISHER_IDA);
		eventModel.setPublishedOn(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
		eventModel.setTopic(topic);
		return eventModel;
	}
}
