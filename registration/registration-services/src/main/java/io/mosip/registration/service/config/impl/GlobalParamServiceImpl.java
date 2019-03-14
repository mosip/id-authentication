package io.mosip.registration.service.config.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.id.GlobalParamId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * Class for implementing GlobalContextParam service
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Service
public class GlobalParamServiceImpl extends BaseService implements GlobalParamService {

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

	@Override
	public ResponseDTO synchConfigData(boolean isJob) {
		LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"config data synch is started");

		ResponseDTO responseDTO = new ResponseDTO();

		String triggerPoint = (isJob ? RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM : RegistrationConstants.JOB_TRIGGER_POINT_USER);
		/* Fetch Global Params from server */
		saveGlobalParamsFromServer(responseDTO,triggerPoint);

		if (!isJob) {
			/* If unable to fetch from server and no data in DB create error response */
			if (responseDTO.getSuccessResponseDTO() == null && getGlobalParams().isEmpty()) {
				setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);
			} else {
				setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, null);
			}
		}

		LOGGER.info(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"config data synch is completed");

		return responseDTO;
	}

	@SuppressWarnings("unchecked")
	private void parseToMap(HashMap<String, Object> map, HashMap<String, String> globalParamMap) {
		for (Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();

			if (entry.getValue() instanceof HashMap) {
				parseToMap((HashMap<String, Object>) entry.getValue(), globalParamMap);
			} else {
				globalParamMap.put(key, entry.getValue().toString());
			}
		}
	}

	private void saveGlobalParamsFromServer(ResponseDTO responseDTO,String triggerPoint) {

		if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {

			try {

				Map<String, String> requestParamMap = new HashMap<>();

				/* REST CALL */
				@SuppressWarnings("unchecked")
				HashMap<String, Object> globalParamJsonMap = (HashMap<String, Object>) serviceDelegateUtil
						.get(RegistrationConstants.GET_GLOBAL_CONFIG, requestParamMap, true,triggerPoint);
				HashMap<String, String> globalParamMap = new HashMap<>();
				parseToMap(globalParamJsonMap, globalParamMap);

				List<GlobalParam> list = new ArrayList<>();

				for (Entry<String, String> key : globalParamMap.entrySet()) {
					
					GlobalParamId globalParamId = new GlobalParamId();
					globalParamId.setCode(key.getKey());
					globalParamId.setLangCode("eng");
					GlobalParam globalParam = globalParamDAO.get(globalParamId);		
					
					if (globalParam != null) {
						globalParam.setVal(globalParamMap.get(key.getKey()));

						globalParam.setUpdBy(getUserIdFromSession());
						globalParam.setUpdDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));

					} else {
						globalParam = new GlobalParam();
						//globalParamId.setCode(key.getKey());
						globalParamId.setLangCode("eng");
						globalParam.setGlobalParamId(globalParamId);
						/* TODO Need to Add Description not key (CODE) */
						globalParam.setName(key.getKey());
						globalParam.setTyp("CONFIGURATION");
						globalParam.setIsActive(true);
						if (SessionContext.isSessionContextAvailable()) {
							globalParam.setCrBy(SessionContext.userContext().getUserId());
						} else {
							globalParam.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
						} 
						globalParam.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
						globalParam.setVal(globalParamMap.get(key.getKey()));
					}

					list.add(globalParam);
				}

				/* Save all Global Params */
				globalParamDAO.saveAll(list);

				setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, null);

			} catch (HttpServerErrorException | HttpClientErrorException | SocketTimeoutException
					| RegBaseCheckedException | ClassCastException | ResourceAccessException exception) {
				setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);
				LOGGER.error("REGISTRATION_SYNCH_CONFIG_DATA", APPLICATION_NAME, APPLICATION_ID,
						exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			}
		} else {
			LOGGER.error(LoggerConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					" Unable to synch config data as no internet connection and no data in DB");
			setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);

		}
	}
}
