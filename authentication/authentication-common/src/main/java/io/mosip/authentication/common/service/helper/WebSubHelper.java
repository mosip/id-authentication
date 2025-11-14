package io.mosip.authentication.common.service.helper;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HUB_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PUBLISHER_URL;

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.authentication.common.service.websub.WebSubEventSubcriber;
import io.mosip.authentication.common.service.websub.WebSubEventTopicRegistrar;
import io.mosip.authentication.common.service.websub.dto.EventInterface;
import io.mosip.authentication.common.service.websub.dto.EventModel;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.retry.WithRetry;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.core.websub.spi.SubscriptionExtendedClient;
import io.mosip.kernel.websub.api.model.FailedContentRequest;
import io.mosip.kernel.websub.api.model.FailedContentResponse;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;

/**
 * The Class WebSubHelper.
 * 
 * @author Loganathan Sekar
 */
@Component
public class WebSubHelper {
	
	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(WebSubHelper.class);
	
	/** The Constant PUBLISHER_IDA. */
	private static final String PUBLISHER_IDA = "IDA";
	
	/** The publisher. */
	@Autowired
	private PublisherClient<String, Object, HttpHeaders> publisher;
	
	/** The hub URL. */
	@Value("${"+ IDA_WEBSUB_HUB_URL +"}")
	private String hubURL;
	
	/** The publisher url. */
	@Value("${"+ IDA_WEBSUB_PUBLISHER_URL +"}")
	private String publisherUrl;
	
	/** The subscription client. */
	@Autowired
	protected SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscriptionClient;
	
	/** The subscription extended client. */
	@Autowired
	protected SubscriptionExtendedClient<FailedContentResponse, FailedContentRequest> subscriptionExtendedClient;
	
	/**
	 * Inits the subscriber.
	 *
	 * @param subscriber the subscriber
	 * @return 
	 */
	public int initSubscriber(WebSubEventSubcriber subscriber) {
		return initSubscriber(subscriber, null);
	}
	
	/**
	 * Inits the subscriber.
	 *
	 * @param subscriber the subscriber
	 * @param enableTester the enable tester
	 * @return 
	 */
	public int initSubscriber(WebSubEventSubcriber subscriber, Supplier<Boolean> enableTester) {
		try {
			subscriber.subscribe(enableTester);
			return HttpStatus.SC_OK;
		} catch (ResourceAccessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "initSubscriber",  this.getClass().getSimpleName(), "FATAL: Subscription failed for:" + subscriber.getClass().getCanonicalName());
			return HttpStatus.SC_SERVICE_UNAVAILABLE;
		} catch (Exception e) {
			//Just logging the exception to avoid other further subscriptions failure
			logger.error(IdAuthCommonConstants.SESSION_ID, "initSubscriber",  this.getClass().getSimpleName(), "FATAL: Subscription failed for:" + subscriber.getClass().getCanonicalName());
			return HttpStatus.SC_INTERNAL_SERVER_ERROR;
		}
	}
	
	/**
	 * Inits the registrar.
	 *
	 * @param registrar the registrar
	 * @return 
	 */
	public int initRegistrar(WebSubEventTopicRegistrar registrar) {
		try {
			initRegistrar(registrar, null);
			return HttpStatus.SC_OK;
		} catch (ResourceAccessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "initSubscriber",  this.getClass().getSimpleName(), "registration failed for:" + registrar.getClass().getCanonicalName());
			return HttpStatus.SC_SERVICE_UNAVAILABLE;
		} catch (Exception e) {
			//Just logging the exception to avoid other further subscriptions failure
			logger.error(IdAuthCommonConstants.SESSION_ID, "initSubscriber",  this.getClass().getSimpleName(), "Subscription failed for:" + registrar.getClass().getCanonicalName());
			return HttpStatus.SC_INTERNAL_SERVER_ERROR;
		}
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
	@Async("webSubHelperExecutor")
	public <U> void publishEvent(String eventTopic, U eventModel) {
		publisher.publishUpdate(eventTopic, eventModel, MediaType.APPLICATION_JSON_VALUE, null, publisherUrl);
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
	public <T extends EventInterface,S> EventModel<T> createEventModel(String topic, T event) {
		String eventId = UUID.randomUUID().toString();
		event.setId(eventId);
		EventModel<T> eventModel = new EventModel<>();
		eventModel.setEvent(event);
		logger.debug("Event- "+event);
		eventModel.setPublisher(PUBLISHER_IDA);
		eventModel.setPublishedOn(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
		eventModel.setTopic(topic);
		logger.debug("EventModel- "+eventModel);
		return eventModel;
	}
	
	/**
	 * Creates the event model.
	 *
	 * @param topic the topic
	 * @return the io.mosip.kernel.core.websub.model. event model
	 */
	public  io.mosip.kernel.core.websub.model.EventModel createEventModel(String topic) {
		io.mosip.kernel.core.websub.model.Event event = new io.mosip.kernel.core.websub.model.Event();
		String dateTime = DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime());
		event.setTimestamp(dateTime);
		String eventId = UUID.randomUUID().toString();
		event.setId(eventId);
		
		io.mosip.kernel.core.websub.model.EventModel eventModel = new io.mosip.kernel.core.websub.model.EventModel();
		eventModel.setEvent(event);
		eventModel.setPublisher(PUBLISHER_IDA);
		eventModel.setPublishedOn(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
		eventModel.setTopic(topic);
		return eventModel;
	}
	
	/**
	 * Register topic.
	 *
	 * @param eventTopic the event topic
	 */
	public void registerTopic(String eventTopic) {
		publisher.registerTopic(eventTopic, publisherUrl);
	}
	
	/**
	 * Subscribe.
	 *
	 * @param subscriptionRequest the subscription request
	 * @return the subscription change response
	 */
	public SubscriptionChangeResponse subscribe(SubscriptionChangeRequest subscriptionRequest) {
		subscriptionRequest.setHubURL(hubURL);
		return subscriptionClient.subscribe(subscriptionRequest);
	}
	
}
