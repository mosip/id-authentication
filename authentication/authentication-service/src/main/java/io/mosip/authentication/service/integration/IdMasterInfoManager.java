package io.mosip.authentication.service.integration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Component
public class IdMasterInfoManager {

	/** Session id */
	private static final String SESSION_ID = "sessionId";

	/** Id Master data Service */
	private static final String ID_MASTERDATA_SERVICE = "IdMasterInfoManager";

	/**
	 * Rest Helper
	 */
	@Autowired
	private RestHelper restHelper;

	/**
	 * The Rest request factory
	 */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/**
	 * Object Mapper
	 */
	ObjectMapper mapper = new ObjectMapper();

	/** The logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdMasterInfoManager.class);

	private Map<String, List<Object>> getIdMasterInfo(String code) throws IdAuthenticationBusinessException {
		Map<String, List<Object>> request = null;
		RestRequestDTO requestdto;
		try {
			requestdto = restRequestFactory.buildRequest(RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE, null,
					Map.class);
			Map<String, String> params = new HashMap<>();
			params.put("id", code);
			requestdto.setPathVariables(params);
			Map<String, List<Object>> response = restHelper.requestSync(requestdto);
			if (response != null && !response.isEmpty()) {
				return response;
			}
		} catch (IDDataValidationException | RestServiceException e) {
			mosipLogger.error(SESSION_ID, ID_MASTERDATA_SERVICE, e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}

		return Collections.emptyMap();
	}

}
