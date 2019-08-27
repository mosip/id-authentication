package io.mosip.registration.service.config.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.id.GlobalParamId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * Class for implementing GlobalContextParam service
 * 
 * @author Sravya Surampalli
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Service
public class GlobalParamServiceImpl extends BaseService implements GlobalParamService {

	private static final Set<String> NON_REMOVABLE_PARAMS = new HashSet<>(
			Arrays.asList("mosip.registration.machinecenterchanged", "mosip.registration.initial_setup",
					"mosip.reg.db.current.version", "mosip.reg.services.version",
					RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, RegistrationConstants.SERVICES_VERSION_KEY));
	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(GlobalParamServiceImpl.class);

	/**
	 * Class to retrieve Global parameters of application
	 */
	@Autowired
	private GlobalParamDAO globalParamDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.GlobalParamService#getGlobalParams
	 */
	public Map<String, Object> getGlobalParams() {

		LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Fetching list of global params");

		return globalParamDAO.getGlobalParams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.config.GlobalParamService#synchConfigData(
	 * boolean)
	 */
	@Override
	public ResponseDTO synchConfigData(boolean isJob) {
		LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"config data sync is started");

		ResponseDTO responseDTO = new ResponseDTO();

		if (isJob && !RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
			LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"NO Internet Connection So calling off global param sync");

			return setErrorResponse(responseDTO, RegistrationConstants.NO_INTERNET, null);
		}
		String triggerPoint = isJob ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM
				: RegistrationConstants.JOB_TRIGGER_POINT_USER;

		saveGlobalParams(responseDTO, triggerPoint);

		if (!isJob) {
			/* If unable to fetch from server and no data in DB create error response */
			if (responseDTO.getSuccessResponseDTO() == null && getGlobalParams().isEmpty()) {
				setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);
			} else if (responseDTO.getSuccessResponseDTO() != null) {
				setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE,
						responseDTO.getSuccessResponseDTO().getOtherAttributes());
			}
		}

		LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"config data sync is completed");

		return responseDTO;
	}

	@SuppressWarnings("unchecked")
	private void parseToMap(HashMap<String, Object> map, HashMap<String, String> globalParamMap) {
		if (map != null) {
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();

				if (entry.getValue() instanceof HashMap) {
					parseToMap((HashMap<String, Object>) entry.getValue(), globalParamMap);
				} else {
					globalParamMap.put(key, String.valueOf(entry.getValue()));
				}
			}
		}
	}

	private void saveGlobalParams(ResponseDTO responseDTO, String triggerPoinnt) {
		
		if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
			try {
				boolean isToBeRestarted = false;
				Map<String, String> requestParamMap = new HashMap<>();
                if(validate(responseDTO, triggerPoinnt))
                {
				/* REST CALL */
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> globalParamJsonMap = (LinkedHashMap<String, Object>) serviceDelegateUtil
						.get(RegistrationConstants.GET_GLOBAL_CONFIG, requestParamMap, true, triggerPoinnt);

				// Check for response
				if (null != globalParamJsonMap.get(RegistrationConstants.RESPONSE)) {

					@SuppressWarnings("unchecked")
					HashMap<String, Object> responseMap = (HashMap<String, Object>) globalParamJsonMap
							.get(RegistrationConstants.RESPONSE);
					@SuppressWarnings("unchecked")
					HashMap<String, Object> configDetailJsonMap = (HashMap<String, Object>) responseMap
							.get("configDetail");

					HashMap<String, String> globalParamMap = new HashMap<>();

					parseToMap(configDetailJsonMap, globalParamMap);

					List<GlobalParam> globalParamList = globalParamDAO.getAllEntries();

					for (GlobalParam globalParam : globalParamList) {
						if (!NON_REMOVABLE_PARAMS.contains(globalParam.getGlobalParamId().getCode())) {
							/* Check in map, if exists, update it and remove from map */
							GlobalParamId globalParamId = globalParam.getGlobalParamId();

							if (globalParamMap.get(globalParamId.getCode()) != null) {

								/* update (Local already exists) but val change */
								if (!globalParamMap.get(globalParamId.getCode()).trim().equals(globalParam.getVal())
										|| !(globalParam.getIsActive().booleanValue())) {
									String val = globalParamMap.get(globalParamId.getCode()).trim();
									updateVal(globalParam, val);

									/* Add in application map */
									updateApplicationMap(globalParamId.getCode(), val);

									isToBeRestarted = isPropertyRequireRestart(globalParamId.getCode());
								}
							}
							/* Set is deleted true as removed from server */
							else {
								updateIsDeleted(globalParam);
								ApplicationContext.removeGlobalConfigValueOf(globalParamId.getCode());
							}
							globalParamMap.remove(globalParamId.getCode());
						}
					}

					for (Entry<String, String> key : globalParamMap.entrySet()) {
						createNew(key.getKey(), globalParamMap.get(key.getKey()), globalParamList);

						isToBeRestarted = isPropertyRequireRestart(key.getKey());
						/* Add in application map */
						updateApplicationMap(key.getKey(), key.getValue());
					}

					/* Save all Global Params */
					globalParamDAO.saveAll(globalParamList);
					if (isToBeRestarted) {
						Map<String, Object> attributes = new HashMap<>();
						attributes.put("Restart", RegistrationConstants.ENABLE);
						setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, attributes);
					} else {
						setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, null);
					}
				} else {
					setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);
				}
                }
                } catch (HttpServerErrorException | HttpClientErrorException | SocketTimeoutException
					| RegBaseCheckedException | ClassCastException | ResourceAccessException exception) {
                	if (isAuthTokenEmptyException(exception)) {
					setErrorResponse(responseDTO,
							RegistrationExceptionConstants.AUTH_TOKEN_COOKIE_NOT_FOUND.getErrorCode(), null);
                	} else {
                		setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);
                	}
				LOGGER.error("REGISTRATION_SYNC_CONFIG_DATA", APPLICATION_NAME, APPLICATION_ID,
						exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			}
		} else {
			LOGGER.error(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					" Unable to sync config data as no internet connection and no data in DB");
		}
	}

	private boolean isPropertyRequireRestart(String key) {
		return (key.contains("kernel") || key.contains("mosip.primary"));
	}

	private void updateVal(GlobalParam globalParam, String val) {
		globalParam.setVal(val);
		globalParam.setUpdBy(getUserIdFromSession());
		globalParam.setUpdDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		globalParam.setIsActive(true);
		globalParam.setIsDeleted(false);
	}

	private void updateIsDeleted(GlobalParam globalParam) {
		globalParam.setIsActive(true);
		globalParam.setIsDeleted(true);
		globalParam.setDelDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		globalParam.setUpdBy(getUserIdFromSession());
		globalParam.setUpdDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
	}

	private void createNew(String code, String value, List<GlobalParam> globalParamList) {
		GlobalParam globalParam = new GlobalParam();

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(code);
		globalParamId.setLangCode(RegistrationConstants.ENGLISH_LANG_CODE);

		/* TODO Need to Add Description not key (CODE) */
		globalParam.setName(code);
		globalParam.setTyp("CONFIGURATION");
		globalParam.setIsActive(true);
		globalParam.setCrBy(getUserIdFromSession());
		globalParam.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		globalParam.setVal(value);
		globalParam.setGlobalParamId(globalParamId);
		globalParamList.add(globalParam);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.config.GlobalParamService#
	 * updateSoftwareUpdateStatus(boolean)
	 */
	@Override
	public ResponseDTO updateSoftwareUpdateStatus(boolean isUpdateAvailable, Timestamp timestamp) {

		LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Updating the SoftwareUpdate flag started.");

		ResponseDTO responseDTO = new ResponseDTO();

		GlobalParam globalParam = globalParamDAO.updateSoftwareUpdateStatus(isUpdateAvailable, timestamp);

		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		if (globalParam.getVal().equalsIgnoreCase(RegistrationConstants.ENABLE)) {
			successResponseDTO.setMessage(RegistrationConstants.SOFTWARE_UPDATE_SUCCESS_MSG);
		} else {
			successResponseDTO.setMessage(RegistrationConstants.SOFTWARE_UPDATE_FAILURE_MSG);
		}
		responseDTO.setSuccessResponseDTO(successResponseDTO);

		LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Updating the SoftwareUpdate flag ended.");
		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.config.GlobalParamService#update(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public void update(String code, String val) {

		LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Update global param started");

		if(code!=null && val!=null) {
			// Primary Key
			GlobalParamId globalParamId = new GlobalParamId();
			globalParamId.setCode(code);
			globalParamId.setLangCode(RegistrationConstants.ENGLISH_LANG_CODE);

			// Get Current global param
			GlobalParam globalParam = globalParamDAO.get(globalParamId);

			Timestamp time = Timestamp.valueOf(DateUtils.getUTCCurrentDateTime());
			if (globalParam == null) {
				globalParam = new GlobalParam();
				globalParam.setGlobalParamId(globalParamId);
				globalParam.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				globalParam.setCrDtime(time);

			}
			globalParam.setVal(val);
			globalParam.setName(code);
			globalParam.setIsActive(true);
			globalParam.setUpdBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
			globalParam.setUpdDtimes(time);

			// Update Global Param
			globalParamDAO.update(globalParam);

			updateApplicationMap(code, val);

			LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"Update global param ended");
		}
		else {
		LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Not Update global param because of code or val is null value");
		}
		

	}

	private void updateApplicationMap(String code, String val) {
		ApplicationContext.setGlobalConfigValueOf(code, val);
		// getBaseGlobalMap().put(code, val);

	}
	
	private boolean validate(ResponseDTO responseDTO,String triggerPoint) throws RegBaseCheckedException
	{
		
		if(responseDTO!=null)
		{
			if(triggerPoint!=null)
			{
				return true;
			}
			else
			{
				throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_GLOBALPARAM_SYNC_SERVICE_IMPL_TRIGGER_POINT.getErrorCode(),RegistrationExceptionConstants.REG_POLICY_SYNC_SERVICE_IMPL_CENTERMACHINEID.getErrorMessage());
			}
		}
		else
		{
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_GLOBALPARAM_SYNC_SERVICE_IMPL.getErrorCode(),RegistrationExceptionConstants.REG_POLICY_SYNC_SERVICE_IMPL.getErrorMessage());
		}
		
	}
}
