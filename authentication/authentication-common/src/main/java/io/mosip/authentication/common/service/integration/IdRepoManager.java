package io.mosip.authentication.common.service.integration;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.KER_USER_ID_NOTEXIST_ERRORCODE;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
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
import io.mosip.idrepository.core.dto.BaseRequestResponseDTO;
import io.mosip.idrepository.core.dto.DocumentsDTO;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;

/**
 * Fetch data's and manages entity info's from ID Repository
 * 
 * @author Dinesh Karuppiah.T
 * @author Manoj SP
 *
 */
@Component
public class IdRepoManager {

	private static final String ERROR_CODE = "errorCode";

	private static final String ERRORS = "errors";

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

	@Autowired
	private IdentityCacheRepository identityRepo;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Fetch data from Id Repo based on Individual's UIN / VID value and all UIN.
	 *
	 * @param id
	 *            the uin
	 * @param isBio
	 *            the is bio
	 * @return the idenity
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	public Map<String, Object> getIdentity(String id, boolean isBio) throws IdAuthenticationBusinessException {
		try {
			IdentityEntity entity = null;
			if (!identityRepo.existsById(id)) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
						"Id not found in DB");
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
								IdType.UIN.getType()));
			}
			
			if (isBio) {
				entity = identityRepo.getOne(id);
			} else {
				entity = identityRepo.findDemoDataById(id);
			}

			if (DateUtils.before(entity.getExpiryTimestamp(), DateUtils.getUTCCurrentDateTime())) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
						"Id expired");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UIN_DEACTIVATED);
			}

			ResponseWrapper<BaseRequestResponseDTO> responseWrapper = new ResponseWrapper<>();
			BaseRequestResponseDTO response = new BaseRequestResponseDTO();
			response.setIdentity(mapper.readValue(entity.getDemographicData(), Object.class));
			if (entity.getBiometricData() != null) {
				DocumentsDTO document = new DocumentsDTO("individualBiometrics",
						CryptoUtil.encodeBase64(entity.getBiometricData()));
				response.setDocuments(Collections.singletonList(document));
			}
			responseWrapper.setResponse(response);
			return mapper.convertValue(responseWrapper, new TypeReference<Map<String, Object>>() {
			});
		} catch (IOException | DataAccessException | TransactionException | JDBCConnectionException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Gets the RID by UID.
	 *
	 * @param idvId
	 *            the idv id
	 * @return the RID by UID
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
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
			if (KER_USER_ID_NOTEXIST_ERRORCODE.equalsIgnoreCase(e.getErrorCode())) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
								IdType.USER_ID.getType()));
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
	 * @param regID
	 *            the reg ID
	 * @param isBio
	 *            the is bio
	 * @return the id by RID
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
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
			if (environment.getProperty(IdRepoConstants.ACTIVE_STATUS).equalsIgnoreCase(
					(String) ((Map<String, Object>) idRepoResponse.get(IdAuthCommonConstants.RESPONSE))
							.get(IdAuthCommonConstants.STATUS))) {
				idRepoResponse.put(IdAuthCommonConstants.UIN, getUINfromIDentityResponse(idRepoResponse));
			} else {
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
									|| IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode()
											.equalsIgnoreCase((String) map.get(ERROR_CODE)))) {
						throw new IdAuthenticationBusinessException(
								IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
								String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
										IdType.USER_ID.getType()));
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

	@SuppressWarnings("unchecked")
	private String getUINfromIDentityResponse(Map<String, Object> idRepoResponse) {
		return Optional.ofNullable(idRepoResponse.get(IdAuthCommonConstants.RESPONSE)).filter(obj -> obj instanceof Map)
				.map(obj -> ((Map<String, Object>) obj).get(IdAuthCommonConstants.IDENTITY))
				.filter(obj -> obj instanceof Map)
				.map(obj -> ((Map<String, Object>) obj).get(IdAuthCommonConstants.UIN_CAPS))
				.filter(obj -> obj instanceof Number).map(obj -> String.valueOf(obj)).orElse(null);
	}

	/**
	 * Update VID dstatus.
	 *
	 * @param vid
	 *            the vid
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	public void updateVIDstatus(String vid) throws IdAuthenticationBusinessException {
		try {
			if (identityRepo.existsById(vid) && Objects.nonNull(identityRepo.getOne(vid).getTransactionLimit())) {
				identityRepo.deleteById(vid);
			}

		} catch (DataAccessException | TransactionException | JDBCConnectionException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
}
