package io.mosip.authentication.common.service.integration;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.KER_USER_ID_NOTEXIST_ERRORCODE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.logger.spi.Logger;

/*
 * Fetch data's and manages entity info's from ID Repository
 * 
 * @author Dinesh Karuppiah.T
 */

@Component
public class IdRepoManager {

	private static final String ERRORMESSAGE = "message";

	private static final String ERROR_CODE = "errorCode";

	private static final String ERRORS = "errors";

	private static final List<String> ID_REPO_ERRORS_INVALID_VID = Arrays.asList("VID is EXPIRED", "VID is USED",
			"VID is REVOKED", "VID is DEACTIVATED", "VID is INVALIDATED");

	private static final String DEACTIVATEDUIN = "DEACTIVATED UIN";

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

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(IdRepoManager.class);

	/**
	 * Gets the RID by UID.
	 *
	 * @param idvId the idv id
	 * @return the RID by UID
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@SuppressWarnings("unchecked")
	public String getRIDByUID(String idvId) throws IdAuthenticationBusinessException {
		RestRequestDTO buildRequest = null;
		String rid = null;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("appId", environment.getProperty(IdAuthConfigKeyConstants.MOSIP_IDA_AUTH_APPID));
			params.put("uid", idvId);
			buildRequest = restRequestFactory.buildRequest(RestServicesConstants.USERID_RID, null, Map.class);

			buildRequest.setPathVariables(params);
			Map<String, Object> ridMap = restHelper.requestSync(buildRequest);
			rid = (String) ((Map<String, Object>) ridMap.get(IdAuthCommonConstants.RESPONSE)).get("rid");
		} catch (RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			if(KER_USER_ID_NOTEXIST_ERRORCODE.equalsIgnoreCase(e.getErrorCode())) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
	                    String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),IdType.USER_ID.getType()));
			}
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}

		catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
		return rid;
	}

	/**
	 * Gets the id by RID.
	 *
	 * @param regID the reg ID
	 * @param isBio the is bio
	 * @return the id by RID
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getIdByRID(String regID, boolean isBio) throws IdAuthenticationBusinessException {
		RestRequestDTO buildRequest = null;
		Map<String, Object> idRepoResponse = null;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("rid", regID);
			if (isBio) {
				buildRequest = restRequestFactory.buildRequest(RestServicesConstants.RID_UIN, null, Map.class);
				params.put("type", "bio");
			} else {
				buildRequest = restRequestFactory.buildRequest(RestServicesConstants.RID_UIN_WITHOUT_TYPE, null,
						Map.class);
			}
			buildRequest.setPathVariables(params);
			idRepoResponse = restHelper.requestSync(buildRequest);
			if (!environment.getProperty(IdRepoConstants.ACTIVE_STATUS).equalsIgnoreCase(
					(String) ((Map<String, Object>) idRepoResponse.get(IdAuthCommonConstants.RESPONSE)).get(IdAuthCommonConstants.STATUS))) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UIN_DEACTIVATED);
			}
		} catch (RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> idrepoMap = (Map<String, Object>) responseBody.get();
				if (idrepoMap.containsKey(ERRORS)) {
					List<Map<String, Object>> idRepoerrorList = (List<Map<String, Object>>) idrepoMap.get(ERRORS);

					if (!idRepoerrorList.isEmpty() && idRepoerrorList.stream()
							.anyMatch(map -> map.containsKey(ERROR_CODE)
									&& (IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode()
											.equalsIgnoreCase((String) map.get(ERROR_CODE)))
									|| IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode().equalsIgnoreCase((String)map.get(ERROR_CODE)))) {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
								                    String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),IdType.USER_ID.getType()));
					} else {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,
								e);
					}
				}
			}
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
		return idRepoResponse;
	}

}
