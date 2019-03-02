package io.mosip.authentication.service.impl.id.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * @author Dinesh Karuppiah.T
 * @author Rakesh Roshan
 */

@Service
public class IdRepoServiceImpl implements IdRepoService {

	private static final List<String> ID_REPO_ERRORS_INVALID_UIN = Arrays.asList(
														IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(),
				                                        IdRepoErrorConstants.INVALID_UIN.getErrorCode()
			                                        );


	private static final String STATUS_KEY = "status";


	private static Logger logger = IdaLogger.getLogger(OTPManager.class);

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The Constant AUTH_FACADE. */
	private static final String ID_REPO_SERVICE = "IDA - IdRepoService";

	@Autowired
	private RestHelper restHelper;

	@Autowired
	private RestRequestFactory restRequestFactory;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private ObjectMapper mapper;

	/**
	 * Fetch data from Id Repo based on Individual's UIN / VID value
	 */
	public Map<String, Object> getIdenity(String uin, boolean isBio) throws IdAuthenticationBusinessException {

		RestRequestDTO buildRequest = null;
		Map<String, Object> response = null;

		try {
			buildRequest = restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, null, Map.class);
			Map<String, String> params = new HashMap<>();
			params.put("uin", uin);
			if (isBio) {
				params.put("type", "bio");
			} else {
				params.put("type", "demo");
			}
			buildRequest.setPathVariables(params);
			response = restHelper.requestSync(buildRequest);
			if(environment.getProperty("mosip.kernel.idrepo.status.registered")
					.equalsIgnoreCase((String)response.get(STATUS_KEY))){		
			  response.put("uin", uin);
			}
			else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UIN_DEACTIVATED);
			}
			
		} catch (RestServiceException e) {
		  Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> idrepoMap = (Map<String, Object>) responseBody.get();
				if (idrepoMap.containsKey("errors")) {
					List<Map<String, Object>> idRepoerrorList = (List<Map<String, Object>>) idrepoMap.get("errors");
					if (!idRepoerrorList.isEmpty() && idRepoerrorList.stream().anyMatch(
							map -> map.containsKey("errCode") && ID_REPO_ERRORS_INVALID_UIN.contains(map.get("errCode")))) {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN);
					} else {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_PROCESS);
					}
				}
			}
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_PROCESS);
		} catch (IDDataValidationException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}
		return response;
	}

}
