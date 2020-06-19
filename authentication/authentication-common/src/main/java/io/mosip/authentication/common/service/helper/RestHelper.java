package io.mosip.authentication.common.service.helper;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.ERRORS;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.RestServiceException;

/**
 * This interface is used to for send/receive HTTP
 * 
 * @author Sanjay Murali
 */
public interface RestHelper {

	/**
	 * Request to send/receive HTTP requests and return the response synchronously.
	 *
	 * @param         <T> the generic type
	 * @param request the request
	 * @return the response object or null in case of exception
	 * @throws RestServiceException the rest service exception
	 */
	<T> T requestSync(RestRequestDTO request) throws RestServiceException;

	/**
	 * Request to send/receive HTTP requests and return the response asynchronously.
	 *
	 * @param request the request
	 * @return the supplier
	 */
	Supplier<Object> requestAsync(RestRequestDTO request);
	
	@SuppressWarnings("unchecked")
	default boolean containsError(String response, ObjectMapper mapper) {
		try {
			Map<String, Object> readValue = mapper.readValue(response.getBytes(), Map.class);
			return readValue.entrySet().stream()
						.anyMatch(entry -> entry.getKey().equals(ERRORS)
											&& !Objects.isNull(entry.getValue()) 
											&& (entry.getValue() instanceof List && !((List<?>)entry.getValue()).isEmpty()));
		} catch (IOException e) {
			//Ignoring parse error
			return false;
		}
	}

}