package io.mosip.authentication.common.manager;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.IDV_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.KYC;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.OTP;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQ_TIME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TRANSACTION_ID;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.util.StringEscapeUtils;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.IdAuthFraudAnalysisEventDTO;
import io.mosip.kernel.core.util.DateUtils;

/**
 * @author Manoj SP
 *
 */
@Component
public class IdAuthFraudAnalysisEventManager {

	private static final String AUTH = "AUTH";

	@Value("${ida.fraud-analysis.request-flooding.time-diff-in-sec:1}")
	private int requestFloodingTimeDiff;

	@Value("${ida.fraud-analysis.request-flooding.request-count:3}")
	private int requestCountForFlooding;

	@Autowired
	private IdAuthFraudAnalysisEventPublisher publisher;

	@Autowired
	private AutnTxnRepository authtxnRepo;
	
	@Autowired
	private ObjectMapper mapper;

	@Async
	public void analyseDigitalSignatureFailure(String uri, Map<String, Object> request, String errorMessage) {
		List<String> pathSegments = Arrays.asList(uri.split("/"));
		String authType = null;
		if (pathSegments.size() > 4) {
			String idvIdHash = IdAuthSecurityManager.generateHashAndDigestAsPlainText(((String) request.get(IDV_ID)).getBytes());
			String txnId = (String) request.get(TRANSACTION_ID);
			String partnerId = pathSegments.get(4);
			LocalDateTime requestTime = DateUtils.parseUTCToLocalDateTime((String) request.get(REQ_TIME));
			authType = getAuthType(pathSegments, authType, request);
			IdAuthFraudAnalysisEventDTO eventData = createEventData(idvIdHash, txnId, partnerId, authType, requestTime, "N",
					errorMessage);
			publisher.publishEvent(eventData);
			this.analyseRequestFlooding(eventData);
		}
	}

	@Async
	public void analyseEvent(AutnTxn txn) {
		IdAuthFraudAnalysisEventDTO eventData = createEventData(txn.getRefId(), txn.getRequestTrnId(), txn.getEntityId(),
				txn.getAuthTypeCode(), txn.getRequestDTtimes(), txn.getStatusCode(), txn.getStatusComment());
		publisher.publishEvent(eventData);
		this.analyseRequestFlooding(eventData);
	}

	private void analyseRequestFlooding(IdAuthFraudAnalysisEventDTO eventData) {
		requestFloodingBasedOnIdvId(eventData);
		requestFloodingBasedOnPartnerId(eventData);
	}

	private void requestFloodingBasedOnIdvId(IdAuthFraudAnalysisEventDTO eventData) {
		List<AutnTxn> requests = authtxnRepo.findByRefIdAndRequestDTtimesAfter(eventData.getIndividualIdHash(),
				eventData.getRequestTime().minusSeconds(requestFloodingTimeDiff));
		if (requests.size() >= requestCountForFlooding) {
			eventData.setComment(String.format("Multple Request received with count : %s within seconds : %s", requests.size(),
					requestFloodingTimeDiff));
			publisher.publishEvent(eventData);
		}
	}

	private void requestFloodingBasedOnPartnerId(IdAuthFraudAnalysisEventDTO eventData) {
		List<AutnTxn> requests = authtxnRepo.findByEntityIdAndRequestDTtimesAfter(eventData.getPartnerId(),
				eventData.getRequestTime().minusSeconds(requestFloodingTimeDiff));
		if (requests.size() >= requestCountForFlooding) {
			eventData.setComment(String.format("Multple Request received with count : %s within seconds : %s", requests.size(),
					requestFloodingTimeDiff));
			publisher.publishEvent(eventData);
		}
	}

	private IdAuthFraudAnalysisEventDTO createEventData(String hash, String txnId, String partnerId, String authType,
			LocalDateTime requestTime, String authStatus, String comment) {
		IdAuthFraudAnalysisEventDTO eventData = new IdAuthFraudAnalysisEventDTO();
		eventData.setIndividualIdHash(hash);
		eventData.setTransactionId(txnId);
		eventData.setPartnerId(partnerId);
		eventData.setAuthType(authType);
		eventData.setRequestTime(requestTime);
		eventData.setAuthStatus(authStatus);
		eventData.setComment(formatAsJson(comment));
		return eventData;
	}

	private String formatAsJson(String comment) {
		try {
			mapper.readValue(comment, Object.class);
			return StringEscapeUtils.escapeJson(comment);
		} catch (IOException e) {
			return comment;
		}
	}

	private String getAuthType(List<String> pathSegments, String authType, Map<String, Object> request) {
		String contextSuffix = pathSegments.get(3);
		if (contextSuffix.contentEquals(OTP)) {
			authType = RequestType.OTP_REQUEST.getRequestType();
		} else if (contextSuffix.contentEquals(KYC)) {
			authType = RequestType.KYC_AUTH_REQUEST.getRequestType();
		} else if (contextSuffix.contentEquals("auth")) {
			authType = AUTH;
		}
		return authType;
	}
}
