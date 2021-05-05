package io.mosip.authentication.common.service.helper;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.WEBSUB_PUBLISH_URL;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.WebSubEventSubcriber;
import io.mosip.authentication.common.service.websub.WebSubEventTopicRegistrar;
import io.mosip.authentication.common.service.websub.dto.EventModel;
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
	public void initSubscriber(WebSubEventSubcriber subscriber) {
		initSubscriber(subscriber, null);
	}
	
	/**
	 * Inits the subscriber.
	 *
	 * @param subscriber the subscriber
	 * @param enableTester the enable tester
	 */
	@WithRetry
	public void initSubscriber(WebSubEventSubcriber subscriber, Supplier<Boolean> enableTester) {
		subscriber.subscribe(enableTester);
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
