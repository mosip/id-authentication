package io.mosip.authentication.internal.service.batch;
import static io.mosip.authentication.common.service.websub.impl.BaseWebSubEventsInitializer.EVENT_TYPE_PLACEHOLDER;
import static io.mosip.authentication.common.service.websub.impl.IdChangeEventsInitializer.PARTNER_ID_PLACEHOLDER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.helper.WebSubHelper.FailedMessage;
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
 * @author Loganathan Sekar
 */
@Component
public class FailedWebsubMessagesReader implements ItemReader<FailedMessage> {
	
	@Data
	@AllArgsConstructor
	public class TopicInfo {
		private String topic;
		private String callbackUrl;
		private String secret;
	}


	private static final int DEFAULT_MAX_WEBSUB_MSG_PULL_WINDOW_DAYS = 2;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(FailedWebsubMessagesReader.class);
	
	/** The total count. */
	private AtomicInteger totalCount;
	
	private AtomicBoolean start = new AtomicBoolean(true);
	
	/** The effectivedtimes. */
	private String effectivedtimes;

	/** The request ids iterator. */
	private Iterator<FailedMessage> messagesIterator;

	@Value("${" + IDA_MAX_WEBSUB_MSG_PULL_WINDOW_DAYS + ":" + DEFAULT_MAX_WEBSUB_MSG_PULL_WINDOW_DAYS + "}" )
	private int maxWebsubMessagesPullWindowDays;
	
	@Autowired
	private WebSubHelper websubHelper;
	
	@Value("${"+ IDA_AUTH_PARTNER_ID  +"}")
	private String authPartherId;
	
	@Value("${" + IDA_WEBSUB_HOTLIST_TOPIC + "}")
	private String hotlistEventTopic;
	
	@Value("${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_TOPIC + "}")
	private String masterdataTemplatesEventTopic;
	
	@Value("${" + IDA_WEBSUB_MASTERDATA_TITLES_TOPIC + "}")
	private String masterdataTitlesEventTopic;
	
	@Value("${" + IDA_WEBSUB_CA_CERT_TOPIC + "}")
	private String partnerCertEventTopic;
	
	@Value("${"+ IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL +"}")
	private String credentialIssueCallbackURL;
	
	@Value("${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}")
	private String credIssueCallbacksecret;
	
	@Value("${"+ IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL +"}")
	private String authTypeCallbackURL;
	
	@Value("${"+ IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET +"}")
	private String autypeCallbackSecret;
	
	@Value("${" + IDA_WEBSUB_HOTLIST_CALLBACK_URL + "}")
	private String hotlistCallbackURL;

	/** The partner service callback secret. */
	@Value("${" + IDA_WEBSUB_HOTLIST_CALLBACK_SECRET + "}")
	private String hotlistCallbackSecret;
	
	@Value("${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_URL + "}")
	private String masterdataTemplatesCallbackURL;

	@Value("${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_SECRET + "}")
	private String masterdataTemplatesCallbackSecret;
	
	@Value("${" + IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_URL + "}")
	private String masterdataTitlesCallbackURL;

	@Value("${" + IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_SECRET + "}")
	private String masterdataTitlesCallbackSecret;
	
	@Value("${" + IDA_WEBSUB_CA_CERT_CALLBACK_URL + "}")
	private String partnerCertCallbackURL;

	/** The partner service callback secret. */
	@Value("${" + IDA_WEBSUB_CA_CERT_CALLBACK_SECRET + "}")
	private String partnerCertCallbackSecret;
	
	@Value("${"+ IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_URL +"}")
	private String partnerServiceCallbackURL;
	
	/** The partner service callback secret. */
	@Value("${"+ IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET +"}")
	private String partnerServiceCallbackSecret;
	
	@Autowired
	protected Environment env;
	
	private final List<TopicInfo> topicsToFetchFailedMessages = new ArrayList<>();

	private Iterator<TopicInfo> topicsToFetchFailedMessagesIterator;

	private TopicInfo currentTopicInfo;
	
	/** The Constant ID_CHANGE_EVENTS. */
	private static final IDAEventType[] ID_CHANGE_EVENTS = {IDAEventType.CREDENTIAL_ISSUED, IDAEventType.REMOVE_ID, IDAEventType.DEACTIVATE_ID, IDAEventType.ACTIVATE_ID};
	
	
	@PostConstruct
	public void postConstruct() {
		initializeTopicInfosToPullFailedMessages();
	}

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
		topicsToFetchFailedMessages.add(new TopicInfo(authTypeStatusTopic, authTypeCallbackURL, autypeCallbackSecret));
		
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
		effectivedtimes = getInitialEffectiveDTimes();
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
				currentTopicInfo = topicsToFetchFailedMessagesIterator.next();
			}
		}
		
		if(currentTopicInfo == null) {
			return List.of();
		}
		
		
		
//		try {
			return websubHelper.getFailedMessages(currentTopicInfo, effectivedtimes, DEFAULT_MAX_WEBSUB_MSG_PULL_WINDOW_DAYS, effectivedtimes);
//			return credentialRequestManager.getMissingCredentialsPageItems(currentPageIndex.getAndIncrement(), effectivedtimes);
//		} catch (RestServiceException | IDDataValidationException e) {
//			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
//		}
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
				mosipLogger.info("Fetched missing credentials. Total count: {}", totalCount.get());
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
	private String getInitialEffectiveDTimes() {
		// Fetch credentials since last credential stored event date time.
		LocalDateTime maxCredentialPullWindowTime = LocalDateTime.now().minus(maxWebsubMessagesPullWindowDays, ChronoUnit.DAYS);
		return DateUtils.formatToISOString(maxCredentialPullWindowTime);
		
	}

}
