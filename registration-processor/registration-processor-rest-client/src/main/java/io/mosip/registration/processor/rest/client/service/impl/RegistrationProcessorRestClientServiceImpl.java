package io.mosip.registration.processor.rest.client.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.rest.client.utils.RestApiClient;

/**
 * The Class RegistrationProcessorRestClientServiceImpl.
 * 
 * @author Rishabh Keshari
 */
@Service
public class RegistrationProcessorRestClientServiceImpl implements RegistrationProcessorRestClientService<Object> {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(RegistrationProcessorRestClientServiceImpl.class);

	/** The rest api client. */
	@Autowired
	private RestApiClient restApiClient;

	/** The env. */
	@Autowired
	private Environment env;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.restclient.
	 * RegistrationProcessorRestClientService#getApi(io.mosip.registration.processor
	 * .core.code.ApiName,
	 * io.mosip.registration.processor.core.code.RestUriConstant, java.lang.String,
	 * java.lang.String, java.lang.Class)
	 */
	@Override
	public Object getApi(ApiName apiName,List<String> pathsegments, String queryParamName, String queryParamValue,
			Class<?> responseType)throws ApisResourceAccessException {
		Object obj =null;
		String apiHostIpPort = env.getProperty(apiName.name());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiHostIpPort );
		
		
		if (!((queryParamName == null) || (("").equals(queryParamName)))) {

			String[] queryParamNameArr = queryParamName.split(",");
			String[] queryParamValueArr = queryParamValue.split(",");
			for (int i = 0; i < queryParamNameArr.length; i++) {
				builder.queryParam(queryParamNameArr[i], queryParamValueArr[i]);
			}

		}
		if(!((pathsegments == null) || (pathsegments.isEmpty()))) {
			for(String segment:pathsegments) {
				if(!((segment == null) || (("").equals(segment))))
				{
					builder.pathSegment(segment);
				}
			}
			
		}
		try{
			obj=restApiClient.getApi(builder.toUriString(), responseType);

		}catch(ResourceAccessException e) {

			throw new ApisResourceAccessException(PlatformErrorMessages.RPR_RCT_UNKNOWN_RESOURCE_EXCEPTION.getCode());

		}

		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.restclient.
	 * RegistrationProcessorRestClientService#postApi(io.mosip.registration.
	 * processor.core.code.ApiName,
	 * io.mosip.registration.processor.core.code.RestUriConstant, java.lang.String,
	 * java.lang.String, java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object postApi(ApiName apiName, String queryParamName, String queryParamValue,
			Object requestedData, Class<?> responseType)throws ApisResourceAccessException {

		Object obj =null;
		String apiHostIpPort = env.getProperty(apiName.name());
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiHostIpPort);

		if (!((queryParamName == null) || (("").equals(queryParamName)))) {
			String[] queryParamNameArr = queryParamName.split(",");
			String[] queryParamValueArr = queryParamValue.split(",");

			for (int i = 0; i < queryParamNameArr.length; i++) {
				builder.queryParam(queryParamNameArr[i], queryParamValueArr[i]);
			}
		}

		try{
			obj=restApiClient.postApi(builder.toUriString(), requestedData, responseType);

		}catch(ResourceAccessException e) {

			throw new ApisResourceAccessException(PlatformErrorMessages.RPR_RCT_UNKNOWN_RESOURCE_EXCEPTION.getMessage(),
					e);

		}

		return obj;
	}

}