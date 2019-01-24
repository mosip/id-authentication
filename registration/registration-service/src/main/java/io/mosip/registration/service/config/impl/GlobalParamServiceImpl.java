package io.mosip.registration.service.config.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.GlobalParamId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.impl.LoginServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

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
	private static final Logger LOGGER = AppConfig.getLogger(LoginServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/**
	 * Class to retrieve Global parameters of application
	 */
	@Autowired
	private GlobalParamDAO globalParamDAO;

	@Autowired
	private UserOnboardDAO userOnboardDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.GlobalParamService#getGlobalParams
	 */
	public Map<String, Object> getGlobalParams() {

		LOGGER.debug(RegistrationConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Fetching list of global params");

		auditFactory.audit(AuditEvent.LOGIN_MODES_FETCH, Components.LOGIN_MODES, "Fetching list of global params",
				"refId", "refIdType");

		return globalParamDAO.getGlobalParams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.config.GlobalParamService#synchConfigData()
	 */
	@Override
	public ResponseDTO synchConfigData() {
		LOGGER.debug(RegistrationConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"config data synch is started");

		ResponseDTO responseDTO = new ResponseDTO();

		if (!RegistrationAppHealthCheckUtil.isNetworkAvailable() && getGlobalParams().isEmpty()) {
			LOGGER.debug(RegistrationConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					" Unable to synch config data");
			return setErrorResponse(responseDTO, RegistrationConstants.GLOBAL_CONFIG_ERROR_MSG, null);
		}

		String centerID = null;
		try {
			String macId = RegistrationSystemPropertiesChecker.getMachineId();

			// get stationID
			String stationID = userOnboardDAO.getStationID(macId);
			// get CenterID
			centerID = userOnboardDAO.getCenterID(stationID);

			Map<String, String> requestParamMap = new HashMap<String, String>();
			requestParamMap.put(RegistrationConstants.REGISTRATION_CENTER_ID, centerID);

			/* REST CALL */
			@SuppressWarnings("unchecked")
			HashMap<String, Object> globalParamJsonMap = (HashMap<String, Object>) serviceDelegateUtil
					.get(RegistrationConstants.GET_GLOBAL_CONFIG, requestParamMap, true);
			HashMap<String, String> globalParamMap = new HashMap<>();
			parseToMap(globalParamJsonMap, globalParamMap);

			List<GlobalParam> list = new ArrayList<>();

			for (Entry<String, String> key : globalParamMap.entrySet()) {

				GlobalParam globalParam = globalParamDAO.get(key.getKey());

		 		if (globalParam != null) {
					globalParam.setVal(globalParamMap.get(key.getKey()));

					globalParam.setUpdBy(getUserIdFromSession());
					globalParam.setUpdDtimes(Timestamp.valueOf(LocalDateTime.now()));

				} else {
					globalParam = new GlobalParam();
					GlobalParamId globalParamId = new GlobalParamId();
					globalParamId.setCode(UUID.randomUUID().toString());
					globalParamId.setLangCode("ENG");
					globalParam.setGlobalParamId(globalParamId);
					globalParam.setName(key.getKey());
					globalParam.setTyp("CONFIGURATION");
					globalParam.setIsActive(true);
					globalParam.setCrBy("brahma");
					globalParam.setCrDtime(Timestamp.valueOf(LocalDateTime.now()));
					globalParam.setVal(globalParamMap.get(key.getKey()));
				}

				list.add(globalParam);
			}

			/* Save all Global Params */
			globalParamDAO.saveAll(list);

			setSuccessResponse(responseDTO, RegistrationConstants.POLICY_SYNC_SUCCESS_MESSAGE, null);

		} catch (HttpServerErrorException | HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException
				| ClassCastException | ResourceAccessException exception) {

			setErrorResponse(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE, null);
			LOGGER.error("REGISTRATION_SYNCH_CONFIG_DATA", APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
		}
		LOGGER.debug(RegistrationConstants.GLOBAL_PARAM_SERVICE_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
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
}
