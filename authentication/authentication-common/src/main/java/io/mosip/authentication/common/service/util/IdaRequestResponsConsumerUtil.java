package io.mosip.authentication.common.service.util;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.METADATA;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERSION;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.dto.ObjectWithIdVersion;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.function.AnonymousProfileStoreFunction;
import io.mosip.authentication.core.function.AuthTransactionStoreFunction;
import io.mosip.authentication.core.spi.profile.AuthAnonymousProfileService;

/**
 * The Class IdaRequestResponsConsumerUtil.
 * 
 * @author Loganathan S
 */
@Component
public class IdaRequestResponsConsumerUtil implements AuthTransactionStoreFunction, AnonymousProfileStoreFunction {
	
	@Autowired
	private ObjectMapper mapper;
	
	@Lazy
	@Autowired
	private AuthAnonymousProfileService authAnonymousProfileService;
	
	private AuthTransactionStatusEventPublisher authTransactionStatusEventPublisher;
	
	@Override
	public void storeAnonymousProfile(Map<String, Object> requestBody, Map<String, Object> responseBody,
			Map<String, Object> requestMetadata, Map<String, Object> responseMetadata) {
		if(authAnonymousProfileService != null) {
			authAnonymousProfileService.storeAnonymousProfile(requestBody, responseBody, requestMetadata, responseMetadata);
		}
	}

	@Override
	public void storeAuthTransaction(Map<String, Object> metadata, String requestSignature,
			String responseSignature) throws IdAuthenticationAppException {
		if(metadata != null) {
			Object authTxnObj = metadata.get(AutnTxn.class.getSimpleName());
			if(authTxnObj != null) {
				AutnTxn autnTxn = mapper.convertValue(authTxnObj, AutnTxn.class);
				autnTxn.setRequestSignature(requestSignature);
				autnTxn.setResponseSignature(responseSignature);
//				try {
//					idService.saveAutnTxn(autnTxn);
//					authTransactionStatusEventPublisher.publishEvent(AuthTxnServiceImpl.fetchAuthResponseDTO(autnTxn), autnTxn.getId(), autnTxn.getCrDTimes());
//				} catch (IdAuthenticationBusinessException e) {
//					mosipLogger.error("sessionId", BASE_IDA_FILTER, "storeAuthTransaction", "\n" + ExceptionUtils.getStackTrace(e));
//					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
//				}
				
			}
		}
	}
	
	/**
	 * setResponseParams method is set the transaction ID and response time based on
	 * the request time zone
	 *
	 * @param requestBody  the request body
	 * @param responseBody the response body
	 * @return the map
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected Map<String, Object> setResponseParams(Map<String, Object> requestBody, Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		if (Objects.nonNull(requestBody) && Objects.nonNull(requestBody.get(IdAuthCommonConstants.TRANSACTION_ID))) {
			responseBody.replace(IdAuthCommonConstants.TRANSACTION_ID,
					requestBody.get(IdAuthCommonConstants.TRANSACTION_ID));
		}

//		if (Objects.nonNull(requestBody) && Objects.nonNull(requestBody.get(IdAuthCommonConstants.REQ_TIME))
//				&& isDate((String) requestBody.get(IdAuthCommonConstants.REQ_TIME))) {
//			ZoneId zone = ZonedDateTime.parse((CharSequence) requestBody.get(IdAuthCommonConstants.REQ_TIME)).getZone();
//
//			String responseTime = Objects.nonNull(responseBody.get(RES_TIME)) ? (String) responseBody.get(RES_TIME)
//					: DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime());
//			responseBody.remove("responsetime");// Handled for forbidden error scenario
//
//			responseBody.put(RES_TIME,
//					DateUtils.formatDate(DateUtils.parseToDate(responseTime,
//							env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN), TimeZone.getTimeZone(zone)),
//							env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN), TimeZone.getTimeZone(zone)));
//		}
		responseBody.remove(METADATA);// Handled for forbidden error scenario, also to remove additional metadata
		return responseBody;
	}
	
	public static void setIdVersionToResponse(ObjectWithMetadata requestWithMetadata, ObjectWithIdVersion responseWithIdVersion) {
		requestWithMetadata.getMetadata(VERSION, String.class).ifPresent(responseWithIdVersion::setVersion);
		requestWithMetadata.getMetadata(ID, String.class).ifPresent(responseWithIdVersion::setId);
	}
	
	public static void setIdVersionToObjectWithMetadata(ObjectWithMetadata requestWithMetadata, ObjectWithMetadata objectWithMetadata) {
		requestWithMetadata.getMetadata(VERSION, String.class).ifPresent(version -> objectWithMetadata.putMetadata(VERSION, version));
		requestWithMetadata.getMetadata(ID, String.class).ifPresent(id -> objectWithMetadata.putMetadata(ID, id));
	}
	
}
