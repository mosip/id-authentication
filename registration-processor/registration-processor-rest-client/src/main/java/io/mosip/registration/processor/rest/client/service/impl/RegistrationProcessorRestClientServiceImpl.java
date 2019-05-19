package io.mosip.registration.processor.rest.client.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
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
	private static Logger regProcLogger = RegProcessorLogger
			.getLogger(RegistrationProcessorRestClientServiceImpl.class);

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
	 * RegistrationProcessorRestClientService#getApi(io.mosip.registration.
	 * processor .core.code.ApiName,
	 * io.mosip.registration.processor.core.code.RestUriConstant,
	 * java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Object getApi(ApiName apiName, List<String> pathsegments, String queryParamName, String queryParamValue,
			Class<?> responseType) throws ApisResourceAccessException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"RegistrationProcessorRestClientServiceImpl::getApi()::entry");
		Object obj = null;
		String apiHostIpPort = env.getProperty(apiName.name());

		UriComponentsBuilder builder = null;
		UriComponents uriComponents = null;
		if (apiHostIpPort != null) {

			builder = UriComponentsBuilder.fromUriString(apiHostIpPort);
			if (!((pathsegments == null) || (pathsegments.isEmpty()))) {
				for (String segment : pathsegments) {
					if (!((segment == null) || (("").equals(segment)))) {
						builder.pathSegment(segment);
					}
				}

			}

			if (!((queryParamName == null) || (("").equals(queryParamName)))) {

				String[] queryParamNameArr = queryParamName.split(",");
				String[] queryParamValueArr = queryParamValue.split(",");
				for (int i = 0; i < queryParamNameArr.length; i++) {
					builder.queryParam(queryParamNameArr[i], queryParamValueArr[i]);
				}

			}

			try {

				uriComponents = builder.build(false).encode();
				regProcLogger.info(uriComponents.toUri().toString(), " URI Of ID Repo", "", "");
				obj = restApiClient.getApi(uriComponents.toUri(), responseType);

			} catch (Exception e) {
				e.printStackTrace();
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + ExceptionUtils.getStackTrace(e));

				throw new ApisResourceAccessException(
						PlatformErrorMessages.RPR_RCT_UNKNOWN_RESOURCE_EXCEPTION.getCode(), e);

			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"RegistrationProcessorRestClientServiceImpl::getApi()::exit");
		return obj;
	}

	public Object postApi(ApiName apiName, String queryParamName, String queryParamValue, Object requestedData,
			Class<?> responseType, MediaType mediaType) throws ApisResourceAccessException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"RegistrationProcessorRestClientServiceImpl::postApi()::entry");

		Object obj = null;
		String apiHostIpPort = env.getProperty(apiName.name());
		UriComponentsBuilder builder = null;
		if (apiHostIpPort != null)
			builder = UriComponentsBuilder.fromUriString(apiHostIpPort);
		if (builder != null) {

			if (!((queryParamName == null) || (("").equals(queryParamName)))) {
				String[] queryParamNameArr = queryParamName.split(",");
				String[] queryParamValueArr = queryParamValue.split(",");

				for (int i = 0; i < queryParamNameArr.length; i++) {
					builder.queryParam(queryParamNameArr[i], queryParamValueArr[i]);
				}
			}

			try {
				obj = restApiClient.postApi(builder.toUriString(), mediaType, requestedData, responseType);

			} catch (Exception e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + ExceptionUtils.getStackTrace(e));

				throw new ApisResourceAccessException(
						PlatformErrorMessages.RPR_RCT_UNKNOWN_RESOURCE_EXCEPTION.getMessage(), e);

			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"RegistrationProcessorRestClientServiceImpl::postApi()::exit");
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.restclient.
	 * RegistrationProcessorRestClientService#postApi(io.mosip.registration.
	 * processor.core.code.ApiName,
	 * io.mosip.registration.processor.core.code.RestUriConstant,
	 * java.lang.String, java.lang.String, java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object postApi(ApiName apiName, String queryParamName, String queryParamValue, Object requestedData,
			Class<?> responseType) throws ApisResourceAccessException {
		return postApi(apiName, queryParamName, queryParamValue, requestedData, responseType, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.restclient.
	 * RegistrationProcessorRestClientService#postApi(io.mosip.registration.
	 * processor.core.code.ApiName, java.util.List, java.lang.String,
	 * java.lang.String, java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object postApi(ApiName apiName, List<String> pathsegments, String queryParamName, String queryParamValue,
			Object requestedData, Class<?> responseType) throws ApisResourceAccessException {

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"RegistrationProcessorRestClientServiceImpl::postApi()::entry");
		Object obj = null;
		String apiHostIpPort = env.getProperty(apiName.name());
		UriComponentsBuilder builder = null;
		if (apiHostIpPort != null)
			builder = UriComponentsBuilder.fromUriString(apiHostIpPort);
		if (builder != null) {

			if (!((pathsegments == null) || (pathsegments.isEmpty()))) {
				for (String segment : pathsegments) {
					if (!((segment == null) || (("").equals(segment)))) {
						builder.pathSegment(segment);
					}
				}

			}
			if (!checkNull(queryParamName)) {
				String[] queryParamNameArr = queryParamName.split(",");
				String[] queryParamValueArr = queryParamValue.split(",");

				for (int i = 0; i < queryParamNameArr.length; i++) {
					builder.queryParam(queryParamNameArr[i], queryParamValueArr[i]);
				}
			}

			try {
				obj = restApiClient.postApi(builder.toUriString(), null, requestedData, responseType);

			} catch (Exception e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + ExceptionUtils.getStackTrace(e));

				throw new ApisResourceAccessException(
						PlatformErrorMessages.RPR_RCT_UNKNOWN_RESOURCE_EXCEPTION.getMessage(), e);

			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"RegistrationProcessorRestClientServiceImpl::postApi()::exit");
		return obj;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.mosip.registration.processor.core.spi.restclient.
	 * RegistrationProcessorRestClientService#patchApi(io.mosip.registration.
	 * processor.core.code.ApiName, java.util.List, java.lang.String,
	 * java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public Object patchApi(ApiName apiName, List<String> pathsegments, String queryParamName, String queryParamValue,
			Object requestedData, Class<?> responseType) throws ApisResourceAccessException {

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"RegistrationProcessorRestClientServiceImpl::postApi()::entry");
		Object obj = null;
		String apiHostIpPort = env.getProperty(apiName.name());
		UriComponentsBuilder builder = null;
		if (apiHostIpPort != null)
			builder = UriComponentsBuilder.fromUriString(apiHostIpPort);
		if (builder != null) {

			if (!((pathsegments == null) || (pathsegments.isEmpty()))) {
				for (String segment : pathsegments) {
					if (!((segment == null) || (("").equals(segment)))) {
						builder.pathSegment(segment);
					}
				}

			}
			if (!checkNull(queryParamName)) {
				String[] queryParamNameArr = queryParamName.split(",");
				String[] queryParamValueArr = queryParamValue.split(",");

				for (int i = 0; i < queryParamNameArr.length; i++) {
					builder.queryParam(queryParamNameArr[i], queryParamValueArr[i]);
				}
			}

			try {
				obj = restApiClient.patchApi(builder.toUriString(), requestedData, responseType);

			} catch (Exception e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + ExceptionUtils.getStackTrace(e));

				throw new ApisResourceAccessException(
						PlatformErrorMessages.RPR_RCT_UNKNOWN_RESOURCE_EXCEPTION.getMessage(), e);

			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"RegistrationProcessorRestClientServiceImpl::postApi()::exit");
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.restclient.
	 * RegistrationProcessorRestClientService#putApi(io.mosip.registration.
	 * processor.core.code.ApiName, java.util.List, java.lang.String,
	 * java.lang.String, java.lang.Object, java.lang.Class)
	 */
	public Object putApi(ApiName apiName, List<String> pathsegments, String queryParamName, String queryParamValue,
			Object requestedData, Class<?> responseType,MediaType mediaType) throws ApisResourceAccessException {

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"RegistrationProcessorRestClientServiceImpl::putApi()::entry");
		Object obj = null;
		String apiHostIpPort = env.getProperty(apiName.name());
		UriComponentsBuilder builder = null;
		if (apiHostIpPort != null)
			builder = UriComponentsBuilder.fromUriString(apiHostIpPort);
		if (builder != null) {

			if (!((pathsegments == null) || (pathsegments.isEmpty()))) {
				for (String segment : pathsegments) {
					if (!((segment == null) || (("").equals(segment)))) {
						builder.pathSegment(segment);
					}
				}

			}
			if (!checkNull(queryParamName)) {
				String[] queryParamNameArr = queryParamName.split(",");
				String[] queryParamValueArr = queryParamValue.split(",");

				for (int i = 0; i < queryParamNameArr.length; i++) {
					builder.queryParam(queryParamNameArr[i], queryParamValueArr[i]);
				}
			}

			try {
				obj = restApiClient.putApi(builder.toUriString(), requestedData, responseType,mediaType);

			} catch (Exception e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + ExceptionUtils.getStackTrace(e));

				throw new ApisResourceAccessException(
						PlatformErrorMessages.RPR_RCT_UNKNOWN_RESOURCE_EXCEPTION.getMessage(), e);
			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"RegistrationProcessorRestClientServiceImpl::putApi()::exit");
		return obj;
	}

	/**
	 * Check null.
	 *
	 * @param queryParamName
	 *            the query param name
	 * @return true, if successful
	 */
	private boolean checkNull(String queryParamName) {

		return ((queryParamName == null) || (("").equals(queryParamName)));
	}

}