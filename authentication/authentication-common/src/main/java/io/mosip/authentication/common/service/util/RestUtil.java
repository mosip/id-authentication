package io.mosip.authentication.common.service.util;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.ERRORS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.THROWING_REST_SERVICE_EXCEPTION;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class RestUtil.
 */
public final class RestUtil {
	
	private static Logger mosipLogger = IdaLogger.getLogger(RestHelper.class);
	
	/**
	 * Instantiates a new rest util.
	 */
	private RestUtil() {
	}
	
	/**
	 * Gets the error.
	 *
	 * @param response the response
	 * @param mapper the mapper
	 * @return the error
	 */
	public static Optional<Entry<String, Object>> getError(String response, ObjectMapper mapper) {
		try {
			Map<String, Object> readValue = mapper.readValue(response.getBytes(), Map.class);
			return readValue.entrySet().stream()
						.filter(entry -> entry.getKey().equals(ERRORS)
											&& !Objects.isNull(entry.getValue()) 
											&& (entry.getValue() instanceof List && !((List<?>)entry.getValue()).isEmpty()))
						.findAny();
		} catch (IOException e) {
			//Ignoring parse error
			return Optional.empty();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<ServiceError> getErrorList(String responseBodyAsString, ObjectMapper mapper) {
		try {
			Map<String, Object> responseMap = mapper.readValue(responseBodyAsString.getBytes(), Map.class);
			Object errors = responseMap.get("errors");
			if(errors instanceof Map) {
				Map<String, Object> errorMap = (Map<String, Object>) errors;
				return List.of(new ServiceError((String)errorMap.get("errorCode"), (String)errorMap.get("message")));
			}
		} catch (IOException e) {
			mosipLogger.warn(IdAuthCommonConstants.SESSION_ID, RestUtil.class.getCanonicalName(), "checkErrorResponse",
					THROWING_REST_SERVICE_EXCEPTION + "- UNKNOWN_ERROR - " + ExceptionUtils.getStackTrace(e));
			return Collections.emptyList();
		}
		
		return ExceptionUtils.getServiceErrorList(responseBodyAsString);
	}

}
