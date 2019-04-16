package io.mosip.authentication.common.integration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.factory.RestRequestFactory;
import io.mosip.authentication.common.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;

/**
 * 
 * @author Dinesh Karuppiah.T
 * @author Rakesh Roshan
 */

@Component
public class IdRepoManager {

	/**
	 * The Constant Id Repo Errors
	 */
	private static final List<String> ID_REPO_ERRORS_INVALID_UIN = Arrays.asList(
			IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(), IdRepoErrorConstants.INVALID_UIN.getErrorCode());
	/**
	 * The Constant status
	 */
	private static final String STATUS_KEY = "status";
	/**
	 * The Rest Helper
	 */
	@Autowired
	private RestHelper restHelper;

	/**
	 * The Restrequest Factory
	 */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/**
	 * The Environment
	 */
	@Autowired
	private Environment environment;

	/**
	 * Fetch data from Id Repo based on Individual's UIN / VID value and all UIN
	 * 
	 * @param uin
	 * @param isBio
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	@SuppressWarnings("unchecked")
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
			if (environment.getProperty(IdAuthenticationProperties.MOSIP_KERNEL_IDREPO_STATUS_REGISTERED.getkey())
					.equalsIgnoreCase((String) ((Map<String, Object>)response.get("response")).get(STATUS_KEY))) {
				response.put("uin", uin);
			} else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UIN_DEACTIVATED);
			}

		} catch (RestServiceException e) {
			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> idrepoMap = (Map<String, Object>) responseBody.get();
				if (idrepoMap.containsKey("errors")) {
					List<Map<String, Object>> idRepoerrorList = (List<Map<String, Object>>) idrepoMap.get("errors");
					if (!idRepoerrorList.isEmpty()
							&& idRepoerrorList.stream().anyMatch(map -> map.containsKey("errCode")
									&& ID_REPO_ERRORS_INVALID_UIN.contains(map.get("errCode")))) {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
					} else {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,
								e);
					}
				}
			}
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (IDDataValidationException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
		return response;
	}

}
