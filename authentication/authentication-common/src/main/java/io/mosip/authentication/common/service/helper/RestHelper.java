package io.mosip.authentication.common.service.helper;

import java.util.function.Supplier;

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

}