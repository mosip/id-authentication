package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_MASTER_SYNC;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.MasterSyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

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
	public ResponseDTO getMasterSync(String masterSyncDtls) {

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
		try {

			// getting Last Sync date from Data from sync table
			masterSyncDetails = masterSyncDao.getMasterSyncStatus(masterSyncDtls);

			String registrationCenterId = SessionContext.getInstance().getUserContext().getRegistrationCenterDetailDTO()
					.getRegistrationCenterId();

			Timestamp lastSyncTime = masterSyncDetails.getLastSyncDtimes();

			LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, "registrationCenterId" + "===>" + registrationCenterId,
					"lastSyncTime" + "===>" + lastSyncTime);

			String masterJson = "{\"languages\":[{\"language\":[{\"languageCode\":\"1\",\"languageName\":\"eng\",\"languageFamily\":\"Engish\",\"nativeName\":\"Engilsh\"},{\"languageCode\":\"2\",\"languageName\":\"arb\",\"languageFamily\":\"Arab\",\"nativeName\":\"Arab\"}]}],\"biometricattributes\":[{\"biometricattribute\":[{\"code\":\"1\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"2\",\"name\":\"asdf\",\"description\":\"testing\",\"biometricTypeCode\":\"1\",\"langCode\":\"eng\"}]},{\"biometricattribute\":[{\"code\":\"3\",\"name\":\"asfesr\",\"description\":\"testing2\",\"biometricTypeCode\":\"2\",\"langCode\":\"eng\"}]}],\"blacklistedwords\":[{\"word\":\"1\",\"description\":\"asdf1\",\"langCode\":\"eng\"},{\"word\":\"2\",\"description\":\"asdf2\",\"langCode\":\"eng\"},{\"word\":\"3\",\"description\":\"asdf3\",\"langCode\":\"eng\"}],\"biometrictypes\":[{\"biometrictype\":[{\"code\":\"1\",\"name\":\"fingerprints\",\"description\":\"fingerprint\",\"langCode\":\"eng\"}]},{\"biometrictype\":[{\"code\":\"2\",\"name\":\"iries\",\"description\":\"iriescapture\",\"langCode\":\"eng\"}]}],\"idtypes\":[{\"code\":\"1\",\"name\":\"PAN\",\"description\":\"pan card\",\"langCode\":\"eng\"},{\"code\":\"1\",\"name\":\"VID\",\"description\":\"voter id\",\"langCode\":\"eng\"}],\"documentcategories\":[{\"code\":\"1\",\"name\":\"POA\",\"description\":\"poaaa\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"POI\",\"description\":\"porrr\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"POR\",\"description\":\"pobbb\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"POB\",\"description\":\"poiii\",\"langCode\":\"eng\"}],\"documenttypes\":[{\"code\":\"1\",\"name\":\"passport\",\"description\":\"passportid\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"passport\",\"description\":\"passportid's\",\"langCode\":\"eng\"},{\"code\":\"3\",\"name\":\"voterid\",\"description\":\"votercards\",\"langCode\":\"eng\"},{\"code\":\"4\",\"name\":\"passport\",\"description\":\"passports\",\"langCode\":\"eng\"}],\"locations\":[{\"code\":\"1\",\"name\":\"chennai\",\"hierarchyLevel\":\"1\",\"hierarchyName\":\"Tamil Nadu\",\"parentLocCode\":\"1\",\"langCode\":\"eng\"},{\"code\":\"2\",\"name\":\"hyderabad\",\"hierarchyLevel\":\"2\",\"hierarchyName\":\"Telengana\",\"parentLocCode\":\"2\",\"langCode\":\"eng\"}],\"titles\":[{\"title\":[{\"titleCode\":\"1\",\"titleName\":\"admin\",\"titleDescription\":\"ahsasa\",\"langCode\":\"eng\"}]},{\"title\":[{\"titleCode\":\"2\",\"titleDescription\":\"asas\",\"titleName\":\"superadmin\",\"langCode\":\"eng\"}]}],\"genders\":[{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"male\",\"langCode\":\"eng\"},{\"genderCode\":\"2\",\"genderName\":\"female\",\"langCode\":\"eng\"}]},{\"gender\":[{\"genderCode\":\"1\",\"genderName\":\"female\",\"langCode\":\"eng\"}]}],\"reasonCategory\":{\"code\":\"1\",\"name\":\"rejected\",\"description\":\"rejectedfile\",\"langCode\":\"eng\",\"reasonLists\":[{\"code\":\"1\",\"name\":\"document\",\"description\":\"inavliddoc\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"1\"},{\"code\":\"2\",\"name\":\"document\",\"description\":\"inavlidtype\",\"langCode\":\"eng\",\"reasonCategoryCode\":\"2\"}]}}";

			LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "MASTER-SYNC-RESTFUL_SERVICE-BEGINE");

			// Mapping json object to respective dto's
			MasterSyncDto masterSyncDto = mapper.readValue(masterJson, MasterSyncDto.class);

			masterSyncDao.insertMasterSyncData(masterSyncDto);

			LOGGER.debug(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "MASTER-SYNC-RESTFUL_SERVICE-ENDS");

			sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
			sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
			sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);
			responseDTO = new ResponseDTO();
			responseDTO.setSuccessResponseDTO(sucessResponse);

		} catch (JsonParseException jsonParseException) {

			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, jsonParseException.getMessage());

			responseDTO = buildErrorRespone(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE,
					RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		} catch (JsonMappingException jsonMappingException) {

			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, jsonMappingException.getMessage());

			responseDTO = buildErrorRespone(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE,
					RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		} catch (IOException ioException) {

			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());

			responseDTO = buildErrorRespone(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE,
					RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

		} catch (RegBaseUncheckedException regBaseUncheckedException) {

			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, regBaseUncheckedException.getMessage());

			responseDTO = buildErrorRespone(RegistrationConstants.MASTER_SYNC_FAILURE_MSG_CODE,
					RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);
		}

		return responseDTO;
	}

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
