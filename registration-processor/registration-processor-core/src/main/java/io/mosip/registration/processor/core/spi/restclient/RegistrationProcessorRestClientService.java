package io.mosip.registration.processor.core.spi.restclient;

import java.util.List;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
	
/**
 * The Interface RegistrationProcessorRestClientService.
 *
 * @author Rishabh Keshari
 * @param <T> the generic type
 */
public interface RegistrationProcessorRestClientService<T> {

	/**
	 * Gets the api.
	 *
	 * @param apiName the api name
	 * @param pathsegments pathsegments of the uri
	 * @param queryParam the query param
	 * @param queryParamValue the query param value
	 * @param responseType the response type
	 * @return the api
	 * @throws ApisResourceAccessException the apis resource access exception
	 */
	public T getApi(ApiName apiName,List<String> pathsegments, String queryParam, String queryParamValue, Class<?> responseType)throws ApisResourceAccessException;
	
	
	/**
	 * Post api.
	 *
	 * @param apiName the api name
	 * @param queryParam the query param
	 * @param queryParamValue the query param value
	 * @param requestedData the requested data
	 * @param responseType the response type
	 * @return the t
	 * @throws ApisResourceAccessException the apis resource access exception
	 */
	public T postApi(ApiName apiName, String queryParam, String queryParamValue,T requestedData, Class<?> responseType)throws ApisResourceAccessException;


}
