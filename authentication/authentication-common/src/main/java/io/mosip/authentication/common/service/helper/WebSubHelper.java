package io.mosip.authentication.common.service.helper;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_FAILED_MESSAGES_SYNC_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HUB_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PUBLISHER_URL;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.websub.WebSubEventSubcriber;
import io.mosip.authentication.common.service.websub.WebSubEventTopicRegistrar;
import io.mosip.authentication.common.service.websub.dto.EventModel;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthRetryException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.function.ConsumerWithThrowable;
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
import lombok.Data;

/**
 * The Class WebSubHelper.
 * 
 * @author Loganathan Sekar
 */
@Component
public class WebSubHelper {
	
	@Data
	public static class FailedMessage{
		private String topic;
		private String message;
		private String timestamp;
		
		private ConsumerWithThrowable<FailedMessage, Exception> failedMessageConsumer;
	}
	
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
	
	@Value("${"+ IDA_WEBSUB_FAILED_MESSAGES_SYNC_URL +"}")
	private String failedMessageSyncUrl;
	
	@Autowired
	protected SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscriptionClient;
	
	@Autowired
	protected SubscriptionExtendedClient<FailedContentResponse, FailedContentRequest> subscriptionExtendedClient;
	
	@Autowired
	private ObjectMapper mapper;

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
	public <T,S> EventModel<T> createEventModel(String topic, T event) {
		EventModel<T> eventModel = new EventModel<>();
		eventModel.setEvent(event);
		eventModel.setPublisher(PUBLISHER_IDA);
		eventModel.setPublishedOn(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
		eventModel.setTopic(topic);
		return eventModel;
	}
	
	public void registerTopic(String eventTopic) {
		publisher.registerTopic(eventTopic, publisherUrl);
	}
	
	public SubscriptionChangeResponse subscribe(SubscriptionChangeRequest subscriptionRequest) {
		subscriptionRequest.setHubURL(hubURL);
		return subscriptionClient.subscribe(subscriptionRequest);
	}
	
	@WithRetry
	public List<FailedMessage> getFailedMessages(String topic, String callbackUrl, int messageCount, String secret, String timestamp, int pageIndex, ConsumerWithThrowable<FailedMessage, Exception> failedMessageConsumer) {
		try {
		FailedContentRequest failedContentRequest = new FailedContentRequest();
		failedContentRequest.setHubURL(failedMessageSyncUrl);
		failedContentRequest.setTopic(topic);
		failedContentRequest.setCallbackURL(callbackUrl);
		failedContentRequest.setMessageCount(messageCount);
		failedContentRequest.setSecret(secret);
		failedContentRequest.setTimestamp(timestamp);
		failedContentRequest.setPaginationIndex(pageIndex);
		FailedContentResponse failedContent = subscriptionExtendedClient.getFailedContent(failedContentRequest);
		List<?> messages = failedContent.getFailedcontents();
		return messages == null ? List.of() : messages.stream().map(obj -> {
			FailedMessage failedMessage = mapper.convertValue(obj, FailedMessage.class);
			failedMessage.setTopic(topic);
			failedMessage.setFailedMessageConsumer(failedMessageConsumer);
			return failedMessage;
		}).collect(Collectors.toList());
		} catch(Exception e) {
			throw new IdAuthRetryException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
	
	
}
