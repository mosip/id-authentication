package io.mosip.registration.service.config.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.hamcrest.core.IsInstanceOf;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.impl.LoginServiceImpl;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.GlobalParamService#getGlobalParams
	 */
	public Map<String, Object> getGlobalParams() {

		LOGGER.debug("REGISTRATION - GLOBALPARAMS - GLOBALPARAMSSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching list of global params");

		auditFactory.audit(AuditEvent.LOGIN_MODES_FETCH, Components.LOGIN_MODES, "Fetching list of global params", "refId",
				"refIdType");
		
		return globalParamDAO.getGlobalParams();
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.config.GlobalParamService#getGlobalParamsFromServer()
	 */
	@Override
	public ResponseDTO getGlobalParamsFromServer() {
		
		//TODO Should be removed 
		String registrationCenterID = "1234";
		
		
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.REGISTRATION_CENTER_ID, registrationCenterID);

		try {
			HashMap<String,Object>  map = (HashMap<String, Object>) serviceDelegateUtil.get(RegistrationConstants.GET_GLOBAL_CONFIG, requestParamMap,true);
		
			HashMap<String,String> globalParam =new HashMap<>();
			
			
			parseToMap(map, globalParam);
			
			/*for (Entry<String, String> entry : globalParam.entrySet()) {
				 System.out.println("Key = " + entry.getKey() + 
	                    ", Value = " + entry.getValue());
			}

	           
			System.out.println(globalParam.size());*/
			
		} catch (HttpClientErrorException | SocketTimeoutException | RegBaseCheckedException | ClassCastException |ResourceAccessException exception) {
			exception.printStackTrace();
		}
		
		return null;
	}
	
	private void parseToMap(HashMap<String,Object> map,HashMap<String, String> globalParamMap) {
		for (Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
			
			 if(entry.getValue() instanceof HashMap) {
				 parseToMap((HashMap<String, Object>) entry.getValue(), globalParamMap);
			 } else {
				 globalParamMap.put(key, entry.getValue().toString());
			 }
			 
			
		}
	}
}
