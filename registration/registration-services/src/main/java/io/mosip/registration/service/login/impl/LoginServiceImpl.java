package io.mosip.registration.service.login.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_LOGIN_SERVICE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.mapper.CustomObjectMapper.MAPPER_FACADE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.AppAuthenticationDAO;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.ScreenAuthorizationDAO;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.dto.UserMachineMappingDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.operator.UserDetailService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.operator.UserSaltDetailsService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.service.sync.PublicKeySync;
import io.mosip.registration.service.sync.TPMPublicKeySyncService;

/**
 * Implementation for {@link LoginService}
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Service
public class LoginServiceImpl extends BaseService implements LoginService {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(LoginServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditManagerService auditFactory;

	/**
	 * Class to retrieve the Login Details from DB
	 */
	@Autowired
	private AppAuthenticationDAO appAuthenticationDAO;

	/**
	 * Class to retrieve the Officer Details from DB
	 */
	@Autowired
	private UserDetailDAO userDetailDAO;

	/**
	 * Class to retrieve the Registration Center details from DB
	 */
	@Autowired
	private RegistrationCenterDAO registrationCenterDAO;

	/**
	 * Class to retrieve the Screen authorization from DB
	 */
	@Autowired
	private ScreenAuthorizationDAO screenAuthorizationDAO;

	@Autowired
	private PublicKeySync publicKeySyncImpl;

	@Autowired
	private GlobalParamService globalParamService;

	@Autowired
	private MasterSyncService masterSyncService;

	@Autowired
	private UserDetailService userDetailService;

	@Autowired
	private UserOnboardService userOnboardService;

	@Autowired
	private UserSaltDetailsService userSaltDetailsService;
	@Autowired
	private TPMPublicKeySyncService tpmPublicKeySyncService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.login.LoginService#getModesOfLogin(java.lang.
	 * String, java.util.Set)
	 */
	@Override
	public List<String> getModesOfLogin(String authType, Set<String> roleList) {
		// Retrieve Login information

		LOGGER.info(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID, "Fetching list of login modes");

		List<String> loginModes = new ArrayList<>();

		try {
			getModesOfLoginValidation(authType, roleList);

			auditFactory.audit(AuditEvent.LOGIN_MODES_FETCH, Components.LOGIN_MODES,
					RegistrationConstants.APPLICATION_NAME, AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

			loginModes = appAuthenticationDAO.getModesOfLogin(authType, roleList);
			
			LOGGER.info(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					"Completed fetching list of login modes");
			
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));

		}
		return loginModes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.login.LoginService#getUserDetail(java.lang.
	 * String)
	 */
	@Override
	public UserDTO getUserDetail(String userId) {
		// Retrieving Officer details
		LOGGER.info(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID, "Fetching User details");

		UserDTO userDTO = null;

		try {
			getUserDetailValidation(userId);

			auditFactory.audit(AuditEvent.FETCH_USR_DET, Components.USER_DETAIL, RegistrationConstants.APPLICATION_NAME,
					AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

			userDTO = MAPPER_FACADE.map(userDetailDAO.getUserDetail(userId), UserDTO.class);

			LOGGER.info(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID, "Completed fetching User details");
			
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
		}
		return userDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.login.LoginService#getRegistrationCenterDetails
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId, String langCode) {
		// Retrieving Registration Center details

		LOGGER.info(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID, "Fetching Center details");

		RegistrationCenterDetailDTO registrationCenterDetailDTO = null;

		try {
			getRegistrationCenterDetailsValidation(centerId, langCode);
			
			auditFactory.audit(AuditEvent.FETCH_CNTR_DET, Components.CENTER_DETAIL,
					RegistrationConstants.APPLICATION_NAME, AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

			registrationCenterDetailDTO = registrationCenterDAO.getRegistrationCenterDetails(centerId, langCode);			

			LOGGER.info(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					"Completed fetching of Center details");

		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
		}
		return registrationCenterDetailDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.login.LoginService#
	 * getScreenAuthorizationDetails(java.util.List)
	 */
	@Override
	public AuthorizationDTO getScreenAuthorizationDetails(List<String> roleCode) {
		// Fetching screen authorization details

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
				"Fetching list of Screens to be Authorized");
		
		AuthorizationDTO authorizationDTO = null;
		
		try {
			getScreenAuthorizationDetailsValidation(roleCode);
			
			auditFactory.audit(AuditEvent.FETCH_SCR_AUTH, Components.SCREEN_AUTH, RegistrationConstants.APPLICATION_NAME,
					AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

			LOGGER.info(LoggerConstants.LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					"Completed fetching list of Screens to be Authorized");
			
			authorizationDTO = screenAuthorizationDAO.getScreenAuthorizationDetails(roleCode);

		} catch(RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
		}
		return authorizationDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.login.LoginService#updateLoginParams(io.mosip.
	 * registration.dto.UserDTO)
	 */
	public void updateLoginParams(UserDTO userDTO) {

		LOGGER.info("REGISTRATION - UPDATELOGINPARAMS - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Updating Login Params");
		
		try {
			userDTOValidation(userDTO);
			
			UserDetail userDetail = userDetailDAO.getUserDetail(userDTO.getId());

			userDetail.setLastLoginDtimes(userDTO.getLastLoginDtimes());
			userDetail.setLastLoginMethod(userDTO.getLastLoginMethod());
			userDetail.setUnsuccessfulLoginCount(userDTO.getUnsuccessfulLoginCount());
			userDetail.setUserlockTillDtimes(userDTO.getUserlockTillDtimes());

			userDetailDAO.updateLoginParams(userDetail);

			LOGGER.info("REGISTRATION - UPDATELOGINPARAMS - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
					"Updated Login Params");

		} catch(RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.login.LoginService#initialSync()
	 */
	@Override
	public List<String> initialSync() {
		LOGGER.info("REGISTRATION  - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID, "Started Initial sync");

		List<String> val = new LinkedList<>();

		// Sync the TPM Public with Server, if it is initial set-up and TPM is available
		String keyIndex = null;
		final boolean isInitialSetUp = RegistrationConstants.ENABLE
				.equalsIgnoreCase(getGlobalConfigValueOf(RegistrationConstants.INITIAL_SETUP));

		if (isInitialSetUp && RegistrationConstants.ENABLE
				.equals(getGlobalConfigValueOf(RegistrationConstants.TPM_AVAILABILITY))) {
			try {
				keyIndex = tpmPublicKeySyncService.syncTPMPublicKey();
			} catch (RegBaseCheckedException regBaseCheckedException) {
				LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
						"Exception while syncing the TPM public key to server");
				val.add(RegistrationConstants.FAILURE);
				return val;
			}
		}

		performingAllSyncOperations(val, keyIndex, isInitialSetUp);

		LOGGER.info("REGISTRATION  - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID, "completed Initial sync");

		return val;

	}

	/**
	 * Performing all sync operations.
	 *
	 * @param val            the List of values
	 * @param keyIndex       the key index
	 * @param isInitialSetUp the isInitialSetUp flag
	 */
	private void performingAllSyncOperations(List<String> val, String keyIndex, final boolean isInitialSetUp) {
		ResponseDTO publicKeySyncResponse=null;
		try {
			publicKeySyncResponse = publicKeySyncImpl
					.getPublicKey(RegistrationConstants.JOB_TRIGGER_POINT_USER);
			ResponseDTO responseDTO = globalParamService.synchConfigData(false);
			ResponseDTO userResponseDTO = new ResponseDTO();
			ResponseDTO userSaltResponse = new ResponseDTO();
			SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
			if (successResponseDTO != null && successResponseDTO.getOtherAttributes() != null) {
				val.add(RegistrationConstants.RESTART);
			}

			ResponseDTO masterResponseDTO = null;
			if (isInitialSetUp) {
				masterResponseDTO = masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001,
						RegistrationConstants.JOB_TRIGGER_POINT_USER, keyIndex);

			} else {
				masterResponseDTO = masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001,
						RegistrationConstants.JOB_TRIGGER_POINT_USER);

			}
			if (null != masterResponseDTO && null != masterResponseDTO.getSuccessResponseDTO()) {
				userResponseDTO = userDetailService.save(RegistrationConstants.JOB_TRIGGER_POINT_USER);

				if (null != userResponseDTO.getSuccessResponseDTO()) {
					userSaltResponse = userSaltDetailsService
							.getUserSaltDetails(RegistrationConstants.JOB_TRIGGER_POINT_USER);

				}
			}
			if ((((null != masterResponseDTO && masterResponseDTO.getErrorResponseDTOs() != null)
					|| userResponseDTO.getErrorResponseDTOs() != null
					|| userSaltResponse.getErrorResponseDTOs() != null) || responseDTO.getErrorResponseDTOs() != null
					|| publicKeySyncResponse.getErrorResponseDTOs() != null)) {
				val.add(RegistrationConstants.FAILURE);
			} else {
				val.add(RegistrationConstants.SUCCESS);
			}
		} catch (RegBaseCheckedException exRegBaseCheckedException) {
			val.add(RegistrationConstants.FAILURE);
			LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(exRegBaseCheckedException));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.login.LoginService#validateInvalidLogin(io.
	 * mosip.registration.dto.UserDTO, java.lang.String, int, int)
	 */
	public String validateInvalidLogin(UserDTO userDTO, String errorMessage, int invalidLoginCount,
			int invalidLoginTime) {

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "validating invalid login params");

		try {
			userDTOValidation(userDTO);

			int loginCount = userDTO.getUnsuccessfulLoginCount() != null
					? userDTO.getUnsuccessfulLoginCount().intValue()
					: RegistrationConstants.PARAM_ZERO;

			Timestamp loginTime = userDTO.getUserlockTillDtimes();

			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					"Comparing timestamps in case of invalid login attempts");

			if (loginCount >= invalidLoginCount
					&& TimeUnit.MILLISECONDS.toMinutes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()).getTime()
							- loginTime.getTime()) >= invalidLoginTime) {

				loginCount = RegistrationConstants.PARAM_ZERO;
				userDTO.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ZERO);

				updateLoginParams(userDTO);

			}

			if (loginCount >= invalidLoginCount) {

				LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
						"validating login count and time ");

				if (TimeUnit.MILLISECONDS
						.toMinutes(loginTime.getTime() - System.currentTimeMillis()) >= invalidLoginTime) {

					userDTO.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ONE);

					updateLoginParams(userDTO);

				} else {
					return RegistrationConstants.ERROR;
				}
				return "false";

			} else {
				if (!errorMessage.isEmpty()) {

					LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							"updating login count and time for invalid login attempts");
					loginCount = loginCount + RegistrationConstants.PARAM_ONE;
					userDTO.setUserlockTillDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
					userDTO.setUnsuccessfulLoginCount(loginCount);

					updateLoginParams(userDTO);

					if (loginCount >= invalidLoginCount) {
						return RegistrationConstants.ERROR;
					} else {
						return errorMessage;
					}
				}
				return "true";
			}

		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
		}

		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.login.LoginService#validateUser(java.lang.
	 * String)
	 */
	public ResponseDTO validateUser(String userId) {
		ResponseDTO responseDTO = new ResponseDTO();
		
		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Validating User");
		
		try {
			getUserDetailValidation(userId);
			
			UserDTO userDTO = getUserDetail(userId);
			if (userDTO == null) {
				setErrorResponse(responseDTO, RegistrationConstants.USER_NAME_VALIDATION, null);
			} else {
				Map<String, String> centerAndMachineId = userOnboardService.getMachineCenterId();

				String centerId = centerAndMachineId.get(RegistrationConstants.USER_CENTER_ID);

				if (userDTO.getRegCenterUser().getRegcntrId().equals(centerId)) {
					ApplicationContext.map().put(RegistrationConstants.USER_CENTER_ID, centerId);
					if (userDTO.getStatusCode().equalsIgnoreCase(RegistrationConstants.BLOCKED)) {
						setErrorResponse(responseDTO, RegistrationConstants.BLOCKED_USER_ERROR, null);
					} else {
						for (UserMachineMappingDTO userMachineMapping : userDTO.getUserMachineMapping()) {
							ApplicationContext.map().put(RegistrationConstants.DONGLE_SERIAL_NUMBER,
									userMachineMapping.getMachineMaster().getSerialNum());
						}

						Set<String> roleList = new LinkedHashSet<>();

						userDTO.getUserRole().forEach(roleCode -> {
							if (roleCode.isActive()) {
								roleList.add(String.valueOf(roleCode.getRoleCode()));
							}
						});

						LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Validating roles");
						// Checking roles
						if (roleList.isEmpty() || !(roleList.contains(RegistrationConstants.OFFICER)
								|| roleList.contains(RegistrationConstants.SUPERVISOR)
								|| roleList.contains(RegistrationConstants.ADMIN_ROLE))) {
							setErrorResponse(responseDTO, RegistrationConstants.ROLES_EMPTY_ERROR, null);
						} else {
							ApplicationContext.map().put(RegistrationConstants.USER_STATION_ID,
									centerAndMachineId.get(RegistrationConstants.USER_STATION_ID));

							Map<String, Object> params = new LinkedHashMap<>();

							params.put(RegistrationConstants.ROLES_LIST, roleList);
							params.put(RegistrationConstants.USER_DTO, userDTO);
							setSuccessResponse(responseDTO, RegistrationConstants.SUCCESS, params);
						}
					}
				} else {
					setErrorResponse(responseDTO, RegistrationConstants.USER_MACHINE_VALIDATION_MSG, null);
				}
			}

			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "completed validating user");
			
		} catch(RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error(LOG_REG_LOGIN_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
			
			setErrorResponse(responseDTO, RegistrationConstants.USER_NAME_VALIDATION, null);
		}
		return responseDTO;
	}
	
	private void getModesOfLoginValidation(String authType, Set<String> roleList) throws RegBaseCheckedException {
		
		if(isStringEmpty(authType)) {			
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_LOGIN_AUTH_TYPE_EXCEPTION);
		} else if(isSetEmpty(roleList)) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_LOGIN_ROLES_EXCEPTION);			
		}
	}
	
	private void getUserDetailValidation(String userId) throws RegBaseCheckedException {
		
		if(isStringEmpty(userId)) {					
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_LOGIN_USER_ID_EXCEPTION);
		}
	}
	
	private void getRegistrationCenterDetailsValidation(String centerId, String langCode) throws RegBaseCheckedException {
		
		if(isStringEmpty(centerId)) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_LOGIN_CENTER_ID_EXCEPTION);
		} else if(isStringEmpty(langCode)) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_LOGIN_LANG_CODE_EXCEPTION);
		}
	}
	
	private void getScreenAuthorizationDetailsValidation(List<String> roleCode) throws RegBaseCheckedException {
		if(isListEmpty(roleCode)) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_LOGIN_ROLES_EXCEPTION);
		}
	}
	
	private void userDTOValidation(UserDTO userDTO) throws RegBaseCheckedException {
		
		if(null == userDTO) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_LOGIN_USER_DTO_EXCEPTION);
		} 
	}

}
