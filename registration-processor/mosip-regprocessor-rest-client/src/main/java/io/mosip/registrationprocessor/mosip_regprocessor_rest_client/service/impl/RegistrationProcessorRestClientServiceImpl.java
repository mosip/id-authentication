package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.RestUriConstant;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils.RestApiClient;


/**
 * The Class RegistrationProcessorRestClientServiceImpl.
 * 
 * @author Rishabh Keshari
 */
@Service
public class RegistrationProcessorRestClientServiceImpl implements RegistrationProcessorRestClientService<Object> {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(RegistrationProcessorRestClientServiceImpl.class);

	/** The rest api client. */
	@Autowired
	private RestApiClient restApiClient;

	/** The env. */
	@Autowired
	private Environment env;

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService#getApi(io.mosip.registration.processor.core.code.ApiName, io.mosip.registration.processor.core.code.RestUriConstant, java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Object getApi(ApiName apiName, RestUriConstant uri, String queryParamName, String queryParamValue, Class<?> responseType) {

		String getUri=uri.getUri();
		String apiHostIpPort = env.getProperty(apiName.name());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiHostIpPort+getUri);

		if(! ( (queryParamName == null) || (("").equals(queryParamName))) ) {

			String [] queryParamNameArr=queryParamName.split(",");
			String [] queryParamValueArr=queryParamValue.split(",");
			for (int i = 0; i < queryParamNameArr.length; i++) {
				builder.queryParam(queryParamNameArr[i], queryParamValueArr[i]);
			}

		}
		System.out.println("URI CREATED BY BUILDER :  "+builder.toUriString());
		return restApiClient.getApi(builder.toUriString(), responseType);
	}


	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService#postApi(io.mosip.registration.processor.core.code.ApiName, io.mosip.registration.processor.core.code.RestUriConstant, java.lang.String, java.lang.String, java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object postApi(ApiName apiName, RestUriConstant uri, String queryParamName, String queryParamValue, Object requestedData,Class<?> responseType) {
		
		String postUri=uri.getUri();
		String apiHostIpPort = env.getProperty(apiName.name());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiHostIpPort+postUri);

		if(! ( (queryParamName == null) || (("").equals(queryParamName))) ) {
			String [] queryParamNameArr=queryParamName.split(",");
			String [] queryParamValueArr=queryParamValue.split(",");

			for (int i = 0; i < queryParamNameArr.length; i++) {
				builder.queryParam(queryParamNameArr[i], queryParamValueArr[i]);
			}
		}

		System.out.println("URI CREATED BY BUILDER :  "+builder.toUriString());
		return restApiClient.postApi(builder.toUriString(), requestedData,responseType);
	}

}
