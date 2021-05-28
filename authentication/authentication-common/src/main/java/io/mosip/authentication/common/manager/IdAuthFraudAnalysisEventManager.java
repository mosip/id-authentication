package io.mosip.authentication.common.manager;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.IDV_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.KYC;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.OTP;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUESTEDAUTH;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQ_TIME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TRANSACTION_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.UTF_8;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.IdAuthFraudAnalysisEventDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.kernel.core.util.DateUtils;

/**
 * @author Manoj SP
 *
 */
@Component
public class IdAuthFraudAnalysisEventManager {

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
	public void analyseDigitalSignatureFailure(String uri, String authRequest)
			throws JsonParseException, JsonMappingException, IOException {
		List<String> pathSegments = URLEncodedUtils.parsePathSegments(uri, Charset.forName(UTF_8));
		String authType = null;
		if (pathSegments.size() > 4) {
			Map<String, Object> request = mapper.readValue(authRequest, new TypeReference<Map<String, Object>>() {
			});
			String idvIdHash = IdAuthSecurityManager.generateHashAndDigestAsPlainText(((String) request.get(IDV_ID)).getBytes());
			String txnId = (String) request.get(TRANSACTION_ID);
			String partnerId = pathSegments.get(4);
			LocalDateTime requestTime = DateUtils.parseUTCToLocalDateTime((String) request.get(REQ_TIME));
			authType = getAuthType(pathSegments, authType, request);
			IdAuthFraudAnalysisEventDTO eventData = createEventData(idvIdHash, txnId, partnerId, authType, requestTime, "N",
					IdAuthenticationErrorConstants.DSIGN_FALIED.getErrorMessage());
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
		List<AutnTxn> requests = authtxnRepo.findByRefIdAndRequestDTtimesBefore(eventData.getIndividualIdHash(),
				eventData.getRequestTime().minusSeconds(requestFloodingTimeDiff));
		if (requests.size() >= requestCountForFlooding) {
			eventData.setComment(String.format("Multple Request received with count : %s within seconds : %s", requests.size(),
					requestFloodingTimeDiff));
			publisher.publishEvent(eventData);
		}
	}

	private void requestFloodingBasedOnPartnerId(IdAuthFraudAnalysisEventDTO eventData) {
		List<AutnTxn> requests = authtxnRepo.findByEntityIdAndRequestDTtimesBefore(eventData.getPartnerId(),
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
		eventData.setComment(comment);
		return eventData;
	}

	private String getAuthType(List<String> pathSegments, String authType, Map<String, Object> request) {
		if (pathSegments.get(2).contentEquals(OTP)) {
			authType = RequestType.OTP_REQUEST.getRequestType();
		} else if (pathSegments.get(2).contentEquals(KYC)) {
			authType = RequestType.KYC_AUTH_REQUEST.getRequestType();
		} else if (pathSegments.get(2).contentEquals("auth")) {
			AuthTypeDTO authTypeDTO = mapper.convertValue(request.get(REQUESTEDAUTH), AuthTypeDTO.class);
			if (authTypeDTO.isBio()) {
				authType = "BIO-AUTH";
			} else if (authTypeDTO.isDemo()) {
				authType = RequestType.DEMO_AUTH.getRequestType();
			} else if (authTypeDTO.isOtp()) {
				authType = RequestType.OTP_AUTH.getRequestType();
			} else if (authTypeDTO.isPin()) {
				authType = RequestType.STATIC_PIN_AUTH.getRequestType();
			}
		}
		return authType;
	}
}
