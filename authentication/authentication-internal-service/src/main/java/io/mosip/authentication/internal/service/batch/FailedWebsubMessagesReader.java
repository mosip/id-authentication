package io.mosip.authentication.internal.service.batch;
import static io.mosip.authentication.common.service.websub.impl.BaseWebSubEventsInitializer.EVENT_TYPE_PLACEHOLDER;
import static io.mosip.authentication.common.service.websub.impl.IdChangeEventsInitializer.PARTNER_ID_PLACEHOLDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_FETCH_FAILED_WEBSUB_MESSAGES_CHUNK_SIZE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_MAX_WEBSUB_MSG_PULL_WINDOW_DAYS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_TOPIC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HOTLIST_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HOTLIST_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HOTLIST_TOPIC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TEMPLATES_TOPIC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TITLES_TOPIC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_URL;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.FailedMessageEntity;
import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.helper.WebSubHelper.FailedMessage;
import io.mosip.authentication.common.service.impl.idevent.MessageStoreStatus;
import io.mosip.authentication.common.service.repository.FailedMessagesRepo;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.PartnerEventTypes;
import io.mosip.authentication.core.exception.IdAuthRetryException;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Class FailedWebsubMessagesReader.
 * 
 * @author Loganathan Sekar
 */
@Component
public class FailedWebsubMessagesReader implements ItemReader<FailedMessage> {
	
	/**
	 * To string.
	 *
	 * @return the java.lang. string
	 */
	
	/**
	 * To string.
	 *
	 * @return the java.lang. string
	 */
	@Data
	
	/**
	 * Instantiates a new topic info.
	 *
	 * @param topic the topic
	 * @param callbackUrl the callback url
	 * @param secret the secret
	 * @param failedMessageConsumer the failed message consumer
	 */
	
	/**
	 * Instantiates a new topic info.
	 *
	 * @param topic the topic
	 * @param callbackUrl the callback url
	 * @param secret the secret
	 */
	@AllArgsConstructor
	public class TopicInfo {
		
		/** The topic. */
		private String topic;
		
		/** The callback url. */
		private String callbackUrl;
		
		/** The secret. */
		private String secret;
		
	}


	/** The Constant DEFAULT_MAX_WEBSUB_MSG_PULL_WINDOW_DAYS. */
	private static final int DEFAULT_MAX_WEBSUB_MSG_PULL_WINDOW_DAYS = 2;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(FailedWebsubMessagesReader.class);
	
	/** The total count. */
	private AtomicInteger totalCount;
	
	/** The start. */
	private AtomicBoolean start = new AtomicBoolean(true);
	
	/** The current page index. */
	private AtomicInteger currentPageIndex = new AtomicInteger(0);
	
	/** The current effectivedtimes. */
	private String effectivedtimes;

	/** The request ids iterator. */
	private Iterator<FailedMessage> messagesIterator;

	/** The max websub messages pull window days. */
	@Value("${" + IDA_MAX_WEBSUB_MSG_PULL_WINDOW_DAYS + ":" + DEFAULT_MAX_WEBSUB_MSG_PULL_WINDOW_DAYS + "}" )
	private int maxWebsubMessagesPullWindowDays;
	
	/** The websub helper. */
	@Autowired
	private WebSubHelper websubHelper;
	
	/** The auth parther id. */
	@Value("${"+ IDA_AUTH_PARTNER_ID  +"}")
	private String authPartherId;
	
	/** The hotlist event topic. */
	@Value("${" + IDA_WEBSUB_HOTLIST_TOPIC + "}")
	private String hotlistEventTopic;
	
	/** The masterdata templates event topic. */
	@Value("${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_TOPIC + "}")
	private String masterdataTemplatesEventTopic;
	
	/** The masterdata titles event topic. */
	@Value("${" + IDA_WEBSUB_MASTERDATA_TITLES_TOPIC + "}")
	private String masterdataTitlesEventTopic;
	
	/** The partner cert event topic. */
	@Value("${" + IDA_WEBSUB_CA_CERT_TOPIC + "}")
	private String partnerCertEventTopic;
	
	/** The credential issue callback URL. */
	@Value("${"+ IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL +"}")
	private String credentialIssueCallbackURL;
	
	/** The cred issue callbacksecret. */
	@Value("${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}")
	private String credIssueCallbacksecret;
	
	/** The auth type callback URL. */
	@Value("${"+ IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL +"}")
	private String authTypeCallbackURL;
	
	/** The autype callback secret. */
	@Value("${"+ IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET +"}")
	private String autypeCallbackSecret;
	
	/** The hotlist callback URL. */
	@Value("${" + IDA_WEBSUB_HOTLIST_CALLBACK_URL + "}")
	private String hotlistCallbackURL;

	/** The partner service callback secret. */
	@Value("${" + IDA_WEBSUB_HOTLIST_CALLBACK_SECRET + "}")
	private String hotlistCallbackSecret;
	
	/** The masterdata templates callback URL. */
	@Value("${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_URL + "}")
	private String masterdataTemplatesCallbackURL;

	/** The masterdata templates callback secret. */
	@Value("${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_SECRET + "}")
	private String masterdataTemplatesCallbackSecret;
	
	/** The masterdata titles callback URL. */
	@Value("${" + IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_URL + "}")
	private String masterdataTitlesCallbackURL;

	/** The masterdata titles callback secret. */
	@Value("${" + IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_SECRET + "}")
	private String masterdataTitlesCallbackSecret;
	
	/** The partner cert callback URL. */
	@Value("${" + IDA_WEBSUB_CA_CERT_CALLBACK_URL + "}")
	private String partnerCertCallbackURL;

	/** The partner service callback secret. */
	@Value("${" + IDA_WEBSUB_CA_CERT_CALLBACK_SECRET + "}")
	private String partnerCertCallbackSecret;
	
	/** The partner service callback URL. */
	@Value("${"+ IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_URL +"}")
	private String partnerServiceCallbackURL;
	
	/** The partner service callback secret. */
	@Value("${"+ IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET +"}")
	private String partnerServiceCallbackSecret;
	
	/** The env. */
	@Autowired
	protected Environment env;
	
	/** The chunk size. */
	@Value("${" + IDA_FETCH_FAILED_WEBSUB_MESSAGES_CHUNK_SIZE + ":10}")
	private int chunkSize;
	
	/** The topics to fetch failed messages. */
	private final List<TopicInfo> topicsToFetchFailedMessages = new ArrayList<>();

	/** The topics to fetch failed messages iterator. */
	private Iterator<TopicInfo> topicsToFetchFailedMessagesIterator;

	/** The current topic info. */
	private TopicInfo currentTopicInfo;
	
	/** The Constant ID_CHANGE_EVENTS. */
	public static final IDAEventType[] ID_CHANGE_EVENTS = {IDAEventType.CREDENTIAL_ISSUED, IDAEventType.REMOVE_ID, IDAEventType.DEACTIVATE_ID, IDAEventType.ACTIVATE_ID};
	
	/** The failed messages repo. */
	@Autowired
	private FailedMessagesRepo failedMessagesRepo;
	/**
	 * Post construct.
	 */
	@PostConstruct
	public void postConstruct() {
		initializeTopicInfosToPullFailedMessages();
	}

	/**
	 * Initialize topic infos to pull failed messages.
	 */
	private void initializeTopicInfosToPullFailedMessages() {
		//ID Change event topics
		String topicPrefix = authPartherId + "/";
		Arrays.stream(ID_CHANGE_EVENTS).forEach(eventType -> {
			String topic = topicPrefix + eventType.toString();
			
			String callbackURL = credentialIssueCallbackURL.replace(PARTNER_ID_PLACEHOLDER, authPartherId)
					.replace(EVENT_TYPE_PLACEHOLDER, eventType.toString().toLowerCase());
			
			topicsToFetchFailedMessages.add(new TopicInfo(topic, callbackURL,credIssueCallbacksecret));
		});
		
		String authTypeStatusTopic = topicPrefix + IDAEventType.AUTH_TYPE_STATUS_UPDATE.name();
		topicsToFetchFailedMessages.add(new TopicInfo(authTypeStatusTopic, authTypeCallbackURL.replace(PARTNER_ID_PLACEHOLDER, authPartherId), autypeCallbackSecret));
		
		topicsToFetchFailedMessages.add(new TopicInfo(hotlistEventTopic, hotlistCallbackURL, hotlistCallbackSecret));
		
		topicsToFetchFailedMessages.add(new TopicInfo(masterdataTemplatesEventTopic, masterdataTemplatesCallbackURL, masterdataTemplatesCallbackSecret));
		
		topicsToFetchFailedMessages.add(new TopicInfo(masterdataTitlesEventTopic, masterdataTitlesCallbackURL, masterdataTitlesCallbackSecret));
		
		topicsToFetchFailedMessages.add(new TopicInfo(partnerCertEventTopic, partnerCertCallbackURL, partnerCertCallbackSecret));
		
		//Partner Event topics
		Stream.of(PartnerEventTypes.values()).forEach(partnerEventType -> {
			String topic = env.getProperty(partnerEventType.getTopicPropertyName());
			String callbackURL = partnerServiceCallbackURL.replace(EVENT_TYPE_PLACEHOLDER, partnerEventType.getName());
			topicsToFetchFailedMessages.add(new TopicInfo(topic, callbackURL, partnerServiceCallbackSecret));
		});
	}
	
	/**
	 * Initialize.
	 */
	private void initialize() {
		totalCount = new AtomicInteger(0);
		topicsToFetchFailedMessagesIterator = topicsToFetchFailedMessages.iterator();
		messagesIterator = getFailedMessagesIterator();
	}

	/**
	 * Gets the request ids iterator.
	 *
	 * @return the request ids iterator
	 */
	private Iterator<FailedMessage> getFailedMessagesIterator() {
		Stream<FailedMessage> requestIdStream = Stream
				.<List<FailedMessage>>iterate(
						this.getNextPageItems(), 
						list -> list != null && !list.isEmpty(),
						list -> this.getNextPageItems())
				.flatMap(List::stream);
		return requestIdStream.iterator();
	}

	/**
	 * Gets the next page items.
	 *
	 * @return the next page items
	 */
	private synchronized List<FailedMessage> getNextPageItems() {
		if(start.get()) {
			start.set(false);
			if(topicsToFetchFailedMessagesIterator.hasNext()) {
				currentTopicInfo = getNextTopic();
			} else {
				return List.of();
			}
		}
		
		if(currentTopicInfo == null) {
			return List.of();
		}
		
		List<FailedMessage> failedMessages = getNextFailedMessagesForCurrentTopic();
		if(!failedMessages.isEmpty()) {
			return failedMessages;
		}
		
		while(failedMessages.isEmpty()) {
			if(topicsToFetchFailedMessagesIterator.hasNext()) {
				currentTopicInfo = getNextTopic();
				//Reset page index to 0 for next topic
				currentPageIndex.set(0);
				failedMessages = getNextFailedMessagesForCurrentTopic();
				if(!failedMessages.isEmpty()) {
					return failedMessages;
				}
			} else {
				return List.of();
			}
		}
		
		return List.of();
	}

	/**
	 * Gets the next topic.
	 *
	 * @return the next topic
	 */
	private TopicInfo getNextTopic() {
		TopicInfo nextTopicInfo = topicsToFetchFailedMessagesIterator.next();
		updateEffectiveTimeForTopic(nextTopicInfo.getTopic());
		return nextTopicInfo;
	}

	/**
	 * Update effective time for topic.
	 *
	 * @param topic the topic
	 */
	private void updateEffectiveTimeForTopic(String topic) {
		LocalDateTime minDTime = getMinEffectiveDTimes();
		Optional<FailedMessageEntity> failedMessage = failedMessagesRepo.findFirstByTopicAndCrDTimesGreaterThanAndStatusCodeOrderByPublishedOnDtimesDesc(topic, minDTime, MessageStoreStatus.PROCESSED.toString());
		LocalDateTime effDTime;
		if (failedMessage.isPresent() && failedMessage.get().getFailedDTimes().isAfter(minDTime)) {
			effDTime = failedMessage.get().getFailedDTimes();
		} else {
			effDTime = minDTime;
		}
		effectivedtimes = DateUtils.formatToISOString(effDTime);
	}

	/**
	 * Gets the next failed messages for current topic.
	 *
	 * @return the next failed messages for current topic
	 */
	private List<FailedMessage> getNextFailedMessagesForCurrentTopic() {
		List<FailedMessage> failedMessages = doGetNextFailedMessages();
		if(!failedMessages.isEmpty()) {
			//Increment page index
			currentPageIndex.incrementAndGet();
			return failedMessages;
		}
		return List.of();
	}

	/**
	 * Do get next failed messages.
	 *
	 * @return the list
	 */
	private List<FailedMessage> doGetNextFailedMessages() {
		try {
			return websubHelper.getFailedMessages(currentTopicInfo.getTopic(), currentTopicInfo.getCallbackUrl(),
					chunkSize, currentTopicInfo.getSecret(), effectivedtimes, currentPageIndex.get());
		} catch (Exception e) {
			mosipLogger.error("Error in Fetched failed messages for topic {} \n {}", currentTopicInfo.getTopic(),
					ExceptionUtils.getStackTrace(e));
		}
		return List.of();
	}
	
	/**
	 * Read.
	 *
	 * @return the credential request ids dto
	 * @throws Exception the exception
	 * @throws UnexpectedInputException the unexpected input exception
	 * @throws ParseException the parse exception
	 * @throws NonTransientResourceException the non transient resource exception
	 */
	@Override
	public FailedMessage read() throws Exception {
		try {
			if(messagesIterator == null) {
				initialize();
			}
			
			if (messagesIterator.hasNext()) {
				totalCount.incrementAndGet();
				return messagesIterator.next();
			} else {
				mosipLogger.info("Fetched failed messages. Total count: {}", totalCount.get());
				messagesIterator = null;
				return null;
			}
		} catch (IdAuthUncheckedException e) {
			// Throwing retry exception to perform job level retry
			throw new IdAuthRetryException(e);
		} catch (Exception e) {
			// Throwing retry exception to perform job level retry
			throw new IdAuthRetryException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), 
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), e);
		}
	}


	/**
	 * Gets the effective D times.
	 *
	 * @return the effective D times
	 */
	private LocalDateTime getMinEffectiveDTimes() {
		// Fetch credentials since last credential stored event date time.
		return LocalDateTime.now().minus(maxWebsubMessagesPullWindowDays, ChronoUnit.DAYS);
		
	}

}
