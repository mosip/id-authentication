package io.mosip.authentication.service.filter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class InternalAuthFilter.
 * 
 * @author Sanjay Murali
 */
public class InternalAuthFilter extends BaseAuthFilter {
	
	private static Logger mosipLogger = IdaLogger.getLogger(InternalAuthFilter.class);

	/** The Constant STATUS. */
	private static final String STATUS = "status";

	/** The Constant Y. */
	private static final String Y = "Y";

	/** The Constant TXN_ID. */
	private static final String TXN_ID = "txnID";

	/** The Constant INFO. */
	private static final String INFO = "info";

	/** The Constant MATCH_INFOS. */
	private static final String MATCH_INFOS = "matchInfos";

	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "authType";

	/** The Constant FULL_ADDRESS. */
	private static final String FULL_ADDRESS = "fullAddress";

	/** The Constant PERSONAL_IDENTITY. */
	private static final String PERSONAL_IDENTITY = "personalIdentity";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant REQ_TIME. */
	private static final String REQ_TIME = "reqTime";

	/** The Constant RES_TIME. */
	private static final String RES_TIME = "resTime";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#decodedRequest(java.
	 * util.Map)
	 */
	@Override
	protected Map<String, Object> decodedRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		try {
			requestBody.replace(REQUEST, decode((String) requestBody.get(REQUEST)));
			if (null != requestBody.get(REQUEST)) {
				Map<String, Object> request = keyManager.requestData(requestBody, mapper);
				requestBody.replace(REQUEST, request);
			}
			return requestBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#encodedResponse(java.
	 * util.Map)
	 */
	@Override
	protected Map<String, Object> encodedResponse(Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		return responseBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#setTxnId(java.util.Map,
	 * java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> setResponseParam(Map<String, Object> requestBody, Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		try {
			if (Y.equals(responseBody.get(STATUS))) {
				Map<String, Object> authType = (Map<String, Object>) requestBody.get(AUTH_TYPE);
				if ((authType.get(PERSONAL_IDENTITY) instanceof Boolean)
						&& (authType.get(FULL_ADDRESS) instanceof Boolean) && !(boolean) authType.get(PERSONAL_IDENTITY)
						&& !(boolean) authType.get(FULL_ADDRESS)) {
					Map<String, Object> info = (Map<String, Object>) responseBody.get(INFO);
					info.remove(MATCH_INFOS);
					responseBody.replace(INFO, info);
				}
			}

			if (Objects.nonNull(requestBody.get(TXN_ID))) {
				responseBody.replace(TXN_ID, requestBody.get(TXN_ID));
			}

			if (Objects.nonNull(requestBody.get(REQ_TIME)) && isDate((String) requestBody.get(REQ_TIME))) {
				ZoneId zone = ZonedDateTime.parse((CharSequence) requestBody.get(REQ_TIME)).getZone();
				responseBody.replace(RES_TIME,
						DateUtils.formatDate(
								DateUtils.parseToDate((String) responseBody.get(RES_TIME),
										env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)),
								env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)));

				return responseBody;
			} else {
				return responseBody;
			}
		} catch (DateTimeParseException e) {
			mosipLogger.error("sessionId", "IdAuthFilter", "setResponseParam", "\n" + ExceptionUtils.getStackTrace(e));
			return responseBody;
		}
	}

	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}

}
