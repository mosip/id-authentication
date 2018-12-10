package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_MASTER_SYNC;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.LinkedList;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.mastersync.MasterSyncDto;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.MasterSyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.RestClientUtil;

/**
 * Service class to sync master data from server to client
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Service
public class MasterSyncServiceImpl implements MasterSyncService {

	/** Object for masterSyncDao class. */
	@Autowired
	private MasterSyncDao masterSyncDao;

	/** Object for restClientUtil class. */
	@Autowired
	private RestClientUtil restClientUtil;

	/** Object for urlPath. */
	@Value("${MASTER_SYNC_URL}")
	private String urlPath;

	/** Object for readTimeout. */
	@Value("${UPLOAD_API_READ_TIMEOUT}")
	private int readTimeout;

	/** Object for connectTimeout. */
	@Value("${UPLOAD_API_WRITE_TIMEOUT}")
	private int connectTimeout;

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(MasterSyncServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.MasterSyncService#getMasterSync()
	 */
	@Override
	public ResponseDTO getMasterSync(String masterSyncDtls) throws RegBaseCheckedException {

		ResponseDTO responseDTO = null;

		SuccessResponseDTO sucessResponse = new SuccessResponseDTO();

		ObjectMapper mapper = new ObjectMapper();

		SyncControl masterSyncDetails;

		if (!RegistrationAppHealthCheckUtil.isNetworkAvailable()) {

			responseDTO = buildErrorRespone(RegistrationConstants.MASTER_SYNC_OFFLINE_FAILURE_MSG_CODE,
					RegistrationConstants.MASTER_SYNC_OFFLINE_FAILURE_MSG);
			return responseDTO;

		}

		LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
				"Fetching the last sync details from databse ended");

		masterSyncDetails = masterSyncDao.getMasterSyncStatus(masterSyncDtls);

		String registrationCenterId = SessionContext.getInstance().getUserContext().getRegistrationCenterDetailDTO()
				.getRegistrationCenterId();

		Timestamp lastSyncTime = masterSyncDetails.getLastSyncDtimes();

		LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, "registrationCenterId" + "===>" + registrationCenterId,
				"lastSyncTime" + "===>" + lastSyncTime);

		String masterJson = "{\"languages\":[{\"language\":[{\"languageCode\":\"1\",\"languageName\":\"eng\",\"languageFamily\":\"Engish\",\"nativeName\":\"Engilsh\"},{\"languageCode\":\"2\",\"languageName\":\"arb\",\"languageFamily\":\"Arab\",\"nativeName\":\"Arab\"}]}],\"biometricattributes\":[{\"biometricattribute\":[{\"code\":\"1\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"2\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"3\",\"name\":\"asfesr\",\"description\":\"testing2\",\"biometricTypeCode\":\"2\",\"langCode\":\"eng\"}]}],\"blacklistedwords\":[{\"word\":\"1\",\"description\":\"asdf1\",\"langCode\":\"eng\"},{\"word\":\"2\",\"description\":\"asdf2\",\"langCode\":\"eng\"},{\"word\":\"3\",\"description\":\"asdf3\",\"langCode\":\"eng\"}],\"biometrictypes\":[{\"biometrictype\":[{\"code\":\"1\",\"name\":\"fingerprints\",\"description\":\"fingerprint\",\"langCode\":\"eng\"}]},{\"biometrictype\":[{\"code\":\"2\",\"name\":\"iries\",\"description\":\"iriescapture\",\"langCode\":\"eng\"}]}],\"idtypes\":[{\"code\":\"1\",\"name\":\"PAN\",\"description\":\"pan card\",\"langCode\":\"eng\"},{\"code\":\"1\",\"name\":\"VID\",\"description\":\"voter id\",\"langCode\":\"eng\"}],\"documentcategories\":[{\"code\":\"1\",\"name\":\"POA\",\"description\":\"poaaa\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"POI\",\"description\":\"porrr\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"POR\",\"description\":\"pobbb\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"POB\",\"description\":\"poiii\",\"langCode\":\"eng\"}],\"documenttypes\":[{\"code\":\"1\",\"name\":\"passport\",\"description\":\"passportid\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"passport\",\"description\":\"passportid's\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"voterid\",\"description\":\"votercards\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"passport\",\"description\":\"passports\",\"langCode\":\"eng\"}],\"locations\":[{\"code\":\"1\",\"name\":\"chennai\",\"hierarchyLevel\":\"1\",\"hierarchyName\":\"Tamil Nadu\",\"parentLocCode\":\"1\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"hyderabad\",\"hierarchyLevel\":\"2\",\"hierarchyName\":\"Telengana\",\"parentLocCode\":\"2\",\"langCode\":\"eng\"}],\"titles\":[{\"title\":[{\"titleCode\":\"1\",\"titleName\":\"admin\",\"titleDescription\":\"ahsasa\",\"langCode\":\"eng\"}]},{\"title\":[{\"titleCode\":\"2\",\"titleDescription\":\"asas\",\"titleName\":\"superadmin\",\"langCode\":\"eng\"}]}],\"genders\":[{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"male\",\"langCode\":\"eng\"},{\"genderCode\":\"2\",\"genderName\":\"female\",\"langCode\":\"eng\"}]},{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"female\",\"langCode\":\"eng\"}]}],\"reasonCategory\":{\"code\":\"1\",\"name\":\"rejected\",\"description\":\"rejectedfile\",\"langCode\":\"eng\",\"reasonLists\":[{\"code\":\"1\",\"name\":\"document\",\"description\":\"inavliddoc\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"1\"},{\"code\":\"2\",\"name\":\"document\",\"description\":\"inavlidtype\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"2\"}]}}";

		try {

			LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "MASTER-SYNC-RESTFUL_SERVICE-BEGINE");

			/*
			 * Object masterSyncJson = getMasterSyncJson(registrationCenterId,
			 * lastSyncTime);
			 * 
			 * LOGGER.debug(RegistrationConstants.MASTER_SYNC, APPLICATION_NAME,
			 * "MASTER-SYNC-RESTFUL_SERVICE-ENDS", "master sync json ======>" +
			 * masterSyncJson.toString());
			 */

			MasterSyncDto masterSyncDto = mapper.readValue(masterJson, MasterSyncDto.class);

			masterSyncDao.insertMasterSyncData(masterSyncDto);

			LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "MASTER-SYNC-RESTFUL_SERVICE-ENDS");

			sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
			sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
			sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);
			responseDTO = new ResponseDTO();
			responseDTO.setSuccessResponseDTO(sucessResponse);

		} catch (RuntimeException | IOException runtimeException) {

			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + "Runtime error while parsing the master sync json");

			throw new RegBaseCheckedException(RegistrationConstants.MASTER_SYNC_EXCEPTION,
					runtimeException.getMessage());
		}

		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.PacketUploadService#pushPacket(java.io.File)
	 */

	/*private Object getMasterSyncJson(String centerId, Timestamp lastSyncTime)
			throws URISyntaxException, RegBaseCheckedException {

		LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "getting json object from server");

		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("centerId", centerId);
		map.add("lastSyncTime", lastSyncTime);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
		requestHTTPDTO.setHttpEntity(requestEntity);
		requestHTTPDTO.setClazz(Object.class);
		requestHTTPDTO.setUri(new URI(urlPath));
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		Object response = null;

		try {

			response = restClientUtil.invoke(setTimeout(requestHTTPDTO));

		} catch (HttpClientErrorException e) {
			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					e.getRawStatusCode() + "Http error while pulling json from server");
			throw new RegBaseCheckedException(Integer.toString(e.getRawStatusCode()), e.getStatusText());
		} catch (RuntimeException e) {
			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					e.getMessage() + "Runtime error while pulling json from server");
			throw new RegBaseUncheckedException(RegistrationExceptionConstants.REG_PACKET_UPLOAD_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_UPLOAD_ERROR.getErrorMessage());
		} catch (SocketTimeoutException e) {
			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					e.getMessage() + "Error in pulling json from server");
			throw new RegBaseCheckedException((e.getMessage()), e.getLocalizedMessage());
		}
		return response;
	}

	private RequestHTTPDTO setTimeout(RequestHTTPDTO requestHTTPDTO) { // Timeout in milli second
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setReadTimeout(readTimeout);
		requestFactory.setConnectTimeout(connectTimeout);
		requestHTTPDTO.setSimpleClientHttpRequestFactory(requestFactory);
		return requestHTTPDTO;
	}*/

	private ResponseDTO buildErrorRespone(final String errorCode, final String message) {

		ResponseDTO response = new ResponseDTO();

		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		/* Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(errorCode);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(message);

		errorResponses.add(errorResponse);

		/* Adding list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);

		return response;
	}

}
