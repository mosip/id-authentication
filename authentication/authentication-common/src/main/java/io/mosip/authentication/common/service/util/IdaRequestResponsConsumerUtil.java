package io.mosip.authentication.common.service.util;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TRANSACTION_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERSION;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.impl.AuthTxnServiceImpl;
import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.ObjectWithIdVersionTransactionID;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.function.AnonymousProfileStoreFunction;
import io.mosip.authentication.core.function.AuthTransactionStoreFunction;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.profile.AuthAnonymousProfileService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.exception.ParseException;

/**
 * The Class IdaRequestResponsConsumerUtil.
 * 
 * @author Loganathan S
 */
@Component
public class IdaRequestResponsConsumerUtil implements AuthTransactionStoreFunction, AnonymousProfileStoreFunction {
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdaRequestResponsConsumerUtil.class);
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	/** The auth anonymous profile service. */
	@Autowired(required = false)
	private AuthAnonymousProfileService authAnonymousProfileService;
	
	/** The auth transaction status event publisher. */
	@Autowired
	private AuthTransactionStatusEventPublisher authTransactionStatusEventPublisher;
	
	/** The id service. */
	@Autowired
	private IdService<AutnTxn> idService;

	
	/**
	 * Store anonymous profile.
	 *
	 * @param requestBody the request body
	 * @param responseBody the response body
	 * @param requestMetadata the request metadata
	 * @param responseMetadata the response metadata
	 * @param status 
	 * @param errors 
	 */
	@Override
	public void storeAnonymousProfile(Map<String, Object> requestBody,
			Map<String, Object> requestMetadata, Map<String, Object> responseMetadata, boolean status, List<AuthError> errors) {
		if(authAnonymousProfileService != null) {
			authAnonymousProfileService.storeAnonymousProfile(requestBody, requestMetadata, responseMetadata, status, errors);
		}
	}

	/**
	 * Store auth transaction.
	 *
	 * @param metadata the metadata
	 * @param requestSignature the request signature
	 * @param responseSignature the response signature
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@Override
	public void storeAuthTransaction(Map<String, Object> metadata, String requestSignature,
			String responseSignature) throws IdAuthenticationAppException {
		if(metadata != null) {
			Object authTxnObj = metadata.get(AutnTxn.class.getSimpleName());
			if(authTxnObj != null) {
				AutnTxn autnTxn = mapper.convertValue(authTxnObj, AutnTxn.class);
				autnTxn.setRequestSignature(requestSignature);
				autnTxn.setResponseSignature(responseSignature);
				try {
					idService.saveAutnTxn(autnTxn);
					authTransactionStatusEventPublisher.publishEvent(AuthTxnServiceImpl.fetchAuthResponseDTO(autnTxn), autnTxn.getId(), autnTxn.getCrDTimes());
				} catch (IdAuthenticationBusinessException e) {
					mosipLogger.error("sessionId", this.getClass().getSimpleName(), "storeAuthTransaction", "\n" + ExceptionUtils.getStackTrace(e));
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
				}
				
			}
		}
	}
	
	/**
	 * Sets the id version to response.
	 *
	 * @param sourceRequestWithMetadata the source request with metadata
	 * @param targetResponseWithIdVersion the target response with id version
	 */
	public static void setIdVersionToResponse(ObjectWithMetadata sourceRequestWithMetadata, ObjectWithIdVersionTransactionID targetResponseWithIdVersion) {
		sourceRequestWithMetadata.getMetadata(VERSION, String.class).ifPresent(targetResponseWithIdVersion::setVersion);
		sourceRequestWithMetadata.getMetadata(ID, String.class).ifPresent(targetResponseWithIdVersion::setId);
	}
	
	/**
	 * Sets the transaction id to response.
	 *
	 * @param sourceRequestWithMetadata the source request with metadata
	 * @param targetResponseWithIdVersion the target response with id version
	 */
	public static void setTransactionIdToResponse(ObjectWithMetadata sourceRequestWithMetadata, ObjectWithIdVersionTransactionID targetResponseWithIdVersion) {
		sourceRequestWithMetadata.getMetadata(TRANSACTION_ID, String.class).ifPresent(targetResponseWithIdVersion::setTransactionID);
	}
	
	/**
	 * Sets the id version to object with metadata.
	 *
	 * @param sourceRequestWithMetadata the source request with metadata
	 * @param targetObjectWithMetadata the target object with metadata
	 */
	public static void setIdVersionToObjectWithMetadata(ObjectWithMetadata sourceRequestWithMetadata, ObjectWithMetadata targetObjectWithMetadata) {
		sourceRequestWithMetadata.getMetadata(VERSION, String.class).ifPresent(version -> targetObjectWithMetadata.putMetadata(VERSION, version));
		sourceRequestWithMetadata.getMetadata(ID, String.class).ifPresent(id -> targetObjectWithMetadata.putMetadata(ID, id));
	}
	
	/**
	 * Gets the response time.
	 *
	 * @param requestTime the request time
	 * @param dateTimePattern the date time pattern
	 * @return the response time
	 */
	public static String getResponseTime(String requestTime, String dateTimePattern) {
		String resTime;
		ZoneId zone;
		if (requestTime != null) {
			DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);
			ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(requestTime, isoPattern);
			zone = zonedDateTime2.getZone();
		} else {
			zone = ZoneOffset.UTC;
		}
		resTime = DateUtils.formatDate(DateUtils.parseToDate(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()),
				dateTimePattern, TimeZone.getTimeZone(ZoneOffset.UTC)),
				dateTimePattern, TimeZone.getTimeZone(zone));
		return resTime;
	}

	public static LocalDateTime convertStringDateTimeToLDT(String stringDateTime) {
		LocalDateTime strUTCDate = DateUtils.getUTCCurrentDateTime();
		try {
			strUTCDate = DateUtils.parseToLocalDateTime(DateUtils.getUTCTimeFromDate(
					DateUtils.parseToDate(stringDateTime, EnvUtil.getDateTimePattern())));
		} catch (ParseException e) {
			mosipLogger.warn(IdAuthCommonConstants.SESSION_ID, IdaRequestResponsConsumerUtil.class.getClass().getName(), e.getMessage(),
					"Invalid  DateTime - setting to current date time");
		}
		return strUTCDate;
	}
}
