package io.mosip.registration.service.sync.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_MASTER_SYNC;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dto.IndividualTypeDto;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.mastersync.BiometricAttributeDto;
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.dto.mastersync.GenderDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.dto.mastersync.MasterDataResponseDto;
import io.mosip.registration.dto.mastersync.ReasonListDto;
import io.mosip.registration.entity.BiometricAttribute;
import io.mosip.registration.entity.BlacklistedWords;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.entity.IndividualType;
import io.mosip.registration.entity.Location;
import io.mosip.registration.entity.ReasonCategory;
import io.mosip.registration.entity.ReasonList;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.ValidDocument;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.remap.CenterMachineReMapService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

/**
 * It makes call to the external 'MASTER Sync' services to download the master
 * data which are relevant to center specific by passing the center id or mac
 * address or machine id. Once download the data, it stores the information into
 * the DB for further processing. If center remapping found from the sync
 * response object, it invokes this 'CenterMachineReMapService' object to
 * initiate the center remapping related activities. During the process, the
 * required informations are updated into the audit table for further tracking.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Service
public class MasterSyncServiceImpl extends BaseService implements MasterSyncService {

	/**
	 * The SncTransactionManagerImpl, which Have the functionalities to get the job
	 * and to create sync transaction
	 */
	@Autowired
	protected SyncManager syncManager;

	/** Object for masterSyncDao class. */
	@Autowired
	private MasterSyncDao masterSyncDao;

	/** The machine mapping DAO. */
	@Autowired
	private MachineMappingDAO machineMappingDAO;

	/** The global param service. */
	@Autowired
	private GlobalParamService globalParamService;

	/** The audit factory. */
	@Autowired
	private AuditManagerService auditFactory;

	/** The center machine re map service. */
	@Autowired
	private CenterMachineReMapService centerMachineReMapService;

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(MasterSyncServiceImpl.class);

	/**
	 * It invokes the Master Sync service to download the required information from
	 * external services if the system is online. Once download, the data would be
	 * updated into the DB for further process.
	 *
	 * @param masterSyncDtls the master sync details
	 * @param triggerPoint   from where the call has been initiated [Either : user
	 *                       or system]
	 * @return success or failure status as Response DTO.
	 * @throws RegBaseCheckedException
	 */
	@Override
	public ResponseDTO getMasterSync(String masterSyncDtls, String triggerPoint) throws RegBaseCheckedException {
		LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "Initiating the Master Sync");
		if (masterSyncFieldsValidate(masterSyncDtls, triggerPoint)) {
			return syncMasterData(masterSyncDtls, triggerPoint, getRequestParams(masterSyncDtls, null));
		} else {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"masterSyncDtls/triggerPoint is mandatory...");
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL.getErrorCode(),
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL.getErrorMessage());
		}

	}

	/**
	 * It invokes the external 'Master Sync' service to download the required center
	 * specific information from MOSIP server if the system is online. Once
	 * download, the data would be updated into the DB for further process.
	 *
	 * @param masterSyncDtls the master sync details
	 * @param triggerPoint   from where the call has been initiated [Either : user
	 *                       or system]
	 * @param keyIndex       This is the key index provided by the MOSIP server post
	 *                       submission of local TPM public key. Based on this key
	 *                       the MOSIP server would identify the client and send the
	 *                       sync response accordingly.
	 * @return the master sync Success or failure status is wrapped in ResponseDTO.
	 * @throws RegBaseCheckedException
	 */
	@Override
	public ResponseDTO getMasterSync(String masterSyncDtls, String triggerPoint, String keyIndex)
			throws RegBaseCheckedException {
		LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
				"Initiating the Master Sync for initial setup");
		if (masterSyncFieldsValidateWithIndex(masterSyncDtls, triggerPoint, keyIndex)) {
			return syncMasterData(masterSyncDtls, triggerPoint, getRequestParams(masterSyncDtls, keyIndex));
		} else {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"masterSyncDtls/triggerPoint/keyIndex is mandatory...");
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL.getErrorCode(),
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL.getErrorMessage());
		}

	}

	private synchronized ResponseDTO syncMasterData(String masterSyncDtls, String triggerPoint,
			Map<String, String> requestParam) {
		ResponseDTO responseDTO = new ResponseDTO();
		String resoponse = RegistrationConstants.EMPTY;

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
				"Fetching the last sync  and machine Id details from database Starts");
		try {

			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"Fetching the last sync and machine Id details from database Ends");

			LinkedHashMap<String, Object> masterSyncResponse = getMasterSyncJson(triggerPoint, requestParam);

			if (null != masterSyncResponse && !masterSyncResponse.isEmpty()
					&& null != masterSyncResponse.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE)
					&& null == masterSyncResponse.get(RegistrationConstants.ERRORS)) {

				LOGGER.info(RegistrationConstants.MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
						"master sync json ======>" + masterSyncResponse.toString());

				String jsonString = new ObjectMapper().writeValueAsString(
						masterSyncResponse.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE));

				// Mapping json object to respective dto's
				MasterDataResponseDto masterSyncDto = objectMapper.readValue(jsonString, MasterDataResponseDto.class);

				resoponse = masterSyncDao.save(masterSyncDto);

				if (resoponse.equals(RegistrationConstants.SUCCESS)) {

					LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
							RegistrationConstants.MASTER_SYNC_SUCCESS);
					setSuccessResponse(responseDTO, RegistrationConstants.MASTER_SYNC_SUCCESS, null);
					syncManager.createSyncControlTransaction(
							syncManager.createSyncTransaction(RegistrationConstants.JOB_EXECUTION_SUCCESS,
									RegistrationConstants.JOB_EXECUTION_SUCCESS, triggerPoint, masterSyncDtls));

				} else {

					LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
							RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);
					setErrorResponse(responseDTO, RegistrationConstants.MASTER_SYNC_FAILURE_MSG, null);
				}

			} else {

				if (centerReMapFlag(masterSyncResponse)) {

					LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "Auditing center remapping");

					auditFactory.audit(AuditEvent.MACHINE_REMAPPED, Components.CENTER_MACHINE_REMAP,
							RegistrationConstants.APPLICATION_NAME, AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

					LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
							"updating center remapping flag in global param");

					globalParamService.update(RegistrationConstants.MACHINE_CENTER_REMAP_FLAG,
							RegistrationConstants.TRUE);

					LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
							"updating center remapping process");

					centerMachineReMapService.startRemapProcess();

					setSuccessResponse(responseDTO,
							(String) globalParamService.getGlobalParams().get(RegistrationConstants.INITIAL_SETUP),
							masterSyncResponse);

				} else {

					LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
							RegistrationConstants.MASTER_SYNC_FAILURE_MSG_INFO);

					setErrorResponse(responseDTO, errorMsg(masterSyncResponse), null);
				}

			}

		} catch (RegBaseUncheckedException | RegBaseCheckedException regBaseUncheckedException) {
			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, regBaseUncheckedException.getMessage()
					+ resoponse + ExceptionUtils.getStackTrace(regBaseUncheckedException));

			if (isAuthTokenEmptyException(regBaseUncheckedException)) {
				setErrorResponse(responseDTO, RegistrationExceptionConstants.AUTH_TOKEN_COOKIE_NOT_FOUND.getErrorCode(),
						null);
			} else {
				setErrorResponse(responseDTO, RegistrationConstants.MASTER_SYNC_FAILURE_MSG, null);
			}

		} catch (RuntimeException | IOException runtimeException) {
			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + resoponse + ExceptionUtils.getStackTrace(runtimeException));

			setErrorResponse(responseDTO, RegistrationConstants.MASTER_SYNC_FAILURE_MSG, null);
		}

		return responseDTO;
	}

	/**
	 * It makes the call to external Master sync service [master_sync/
	 * center_remap_sync] with respect to the current center
	 * 
	 * @param triggerPoint    from where the call has been initiated. Applicable
	 *                        values are {user or system}.
	 * @param requestParamMap it holds the center id, last sync datetime, mac
	 *                        address and machine id.
	 * @return Map Response object, which contains the master sync data.
	 * @throws RegBaseCheckedException
	 */
	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Object> getMasterSyncJson(String triggerPoint, Map<String, String> requestParamMap)
			throws RegBaseCheckedException {

		ResponseDTO responseDTO = new ResponseDTO();
		LinkedHashMap<String, Object> masterSyncResponse = null;
		String serviceName;

		LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "Master Sync Restful service starts.....");

		try {

			// Setting uri Variables
			// If initial setup, then invoke the 'Master Sync' without center id. That will
			// be returned based on the mac id/ machine name.
			if (RegistrationConstants.ENABLE.equalsIgnoreCase(
					(String) globalParamService.getGlobalParams().get(RegistrationConstants.INITIAL_SETUP))) {

				serviceName = RegistrationConstants.MASTER_VALIDATOR_SERVICE_NAME;

			} else {
				// If Center id available in db, then invoke the 'Master Sync' with center id.
				requestParamMap.put(RegistrationConstants.MASTER_CENTER_PARAM, getCenterId());
				serviceName = RegistrationConstants.MASTER_CENTER_REMAP_SERVICE_NAME;
			}

			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.MAC_ADDRESS + "===> " + requestParamMap.get(RegistrationConstants.MAC_ADDRESS)
							+ " " + RegistrationConstants.MASTER_DATA_LASTUPDTAE + "==> "
							+ requestParamMap.get(RegistrationConstants.MASTER_DATA_LASTUPDTAE));

			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				masterSyncResponse = (LinkedHashMap<String, Object>) serviceDelegateUtil.get(serviceName,
						requestParamMap, true, triggerPoint);

				if (null != masterSyncResponse.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE)) {
					LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, RegistrationConstants.SUCCESS);
					setSuccessResponse(responseDTO, RegistrationConstants.SUCCESS, null);
				} else {
					LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, errorMsg(masterSyncResponse));
					setErrorResponse(responseDTO, errorMsg(masterSyncResponse), null);
				}
			} else {
				LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, RegistrationConstants.NO_INTERNET);
				setErrorResponse(responseDTO, RegistrationConstants.NO_INTERNET, null);
			}
		} catch (HttpClientErrorException httpClientErrorException) {
			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					httpClientErrorException.getRawStatusCode() + "Http error while pulling json from server"
							+ ExceptionUtils.getStackTrace(httpClientErrorException));
			throw new RegBaseCheckedException(Integer.toString(httpClientErrorException.getRawStatusCode()),
					httpClientErrorException.getStatusText());
		} catch (SocketTimeoutException socketTimeoutException) {
			LOGGER.error(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					socketTimeoutException.getMessage() + "Http error while pulling json from server"
							+ ExceptionUtils.getStackTrace(socketTimeoutException));
			throw new RegBaseCheckedException(socketTimeoutException.getMessage(),
					socketTimeoutException.getLocalizedMessage());
		}

		LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
				"Master Sync Restful service ends successful.....");

		return masterSyncResponse;
	}

	private Map<String, String> getRequestParams(String masterSyncDtls, String keyIndex) {
		Map<String, String> requestParamMap = new HashMap<>();

		// Add Mac Address
		String macId = RegistrationSystemPropertiesChecker.getMachineId();
		requestParamMap.put(RegistrationConstants.MAC_ADDRESS, macId);

		// Get KeyIndex
		if (!RegistrationConstants.ENABLE
				.equalsIgnoreCase(String.valueOf(ApplicationContext.map().get(RegistrationConstants.INITIAL_SETUP)))) {
			keyIndex = machineMappingDAO.getKeyIndexByMacId(macId);
		}

		// Add the Key Index
		if (null != keyIndex) {
			requestParamMap.put(RegistrationConstants.KEY_INDEX.toLowerCase(), keyIndex);
		}

		// getting Last Sync date from Data from sync table
		SyncControl masterSyncDetails = masterSyncDao.syncJobDetails(masterSyncDtls);

		// Add the Last Updated Date
		if (masterSyncDetails != null) {
			requestParamMap.put(RegistrationConstants.MASTER_DATA_LASTUPDTAE, DateUtils.formatToISOString(
					LocalDateTime.ofInstant(masterSyncDetails.getLastSyncDtimes().toInstant(), ZoneOffset.ofHours(0))));
		}

		return requestParamMap;
	}

	/**
	 * Find location or region by hierarchy code.
	 *
	 * @param hierarchyCode the hierarchy code
	 * @param langCode      the lang code
	 * @return the list holds the Location data to be displayed in the UI.
	 * @throws RegBaseCheckedException
	 */
	@Override
	public List<LocationDto> findLocationByHierarchyCode(String hierarchyCode, String langCode)
			throws RegBaseCheckedException {

		List<LocationDto> locationDto = new ArrayList<>();
		if (codeAndlangCodeNullCheck(hierarchyCode, langCode)) {
			List<Location> masterLocation = masterSyncDao.findLocationByLangCode(hierarchyCode, langCode);

			for (Location masLocation : masterLocation) {
				LocationDto location = new LocationDto();
				location.setCode(masLocation.getCode());
				location.setHierarchyName(masLocation.getHierarchyName());
				location.setName(masLocation.getName());
				location.setLangCode(masLocation.getLangCode());
				locationDto.add(location);
			}
		} else {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.CODE_AND_LANG_CODE_MANDATORY);
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_CODE_AND_LANGCODE.getErrorCode(),
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_CODE_AND_LANGCODE.getErrorMessage());
		}
		return locationDto;
	}

	/**
	 * Find proviance by hierarchy code.
	 *
	 * @param code     the code
	 * @param langCode the lang code
	 * @return the list holds the Province data to be displayed in the UI.
	 * @throws RegBaseCheckedException
	 */
	@Override
	public List<LocationDto> findProvianceByHierarchyCode(String code, String langCode) throws RegBaseCheckedException {

		List<LocationDto> locationDto = new ArrayList<>();
		if (codeAndlangCodeNullCheck(code, langCode)) {
			List<Location> masterLocation = masterSyncDao.findLocationByParentLocCode(code, langCode);

			for (Location masLocation : masterLocation) {
				LocationDto location = new LocationDto();
				location.setCode(masLocation.getCode());
				location.setHierarchyName(masLocation.getHierarchyName());
				location.setName(masLocation.getName());
				location.setLangCode(masLocation.getLangCode());
				locationDto.add(location);
			}
		} else {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.CODE_AND_LANG_CODE_MANDATORY);
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_CODE_AND_LANGCODE.getErrorCode(),
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_CODE_AND_LANGCODE.getErrorMessage());
		}
		return locationDto;
	}

	/**
	 * Gets all the reasons for rejection that to be selected during EOD approval
	 * process.
	 *
	 * @param langCode the lang code
	 * @return the all reasons
	 * @throws RegBaseCheckedException
	 */
	@Override
	public List<ReasonListDto> getAllReasonsList(String langCode) throws RegBaseCheckedException {

		List<ReasonListDto> reasonListResponse = new ArrayList<>();
		if (langCodeNullCheck(langCode)) {
			List<String> resonCantCode = new ArrayList<>();
			// Fetting Reason Category
			List<ReasonCategory> masterReasonCatogery = masterSyncDao.getAllReasonCatogery(langCode);
			if (masterReasonCatogery != null && !masterReasonCatogery.isEmpty()) {
				masterReasonCatogery.forEach(reason -> {
					resonCantCode.add(reason.getCode());
				});
			}
			// Fetching reason list based on lang_Code and rsncat_code
			List<ReasonList> masterReasonList = masterSyncDao.getReasonList(langCode, resonCantCode);
			masterReasonList.forEach(reasonList -> {
				ReasonListDto reasonListDto = new ReasonListDto();
				reasonListDto.setCode(reasonList.getCode());
				reasonListDto.setName(reasonList.getName());
				reasonListDto.setRsnCatCode(reasonList.getRsnCatCode());
				reasonListDto.setLangCode(reasonList.getLangCode());
				reasonListResponse.add(reasonListDto);
			});
		} else {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.LANG_CODE_MANDATORY);
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_LANGCODE.getErrorCode(),
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_LANGCODE.getErrorMessage());
		}
		return reasonListResponse;

	}

	/**
	 * Gets all the black listed words that shouldn't be allowed while capturing
	 * demographic information from user.
	 *
	 * @param langCode the lang code
	 * @return the all black listed words
	 * @throws RegBaseCheckedException
	 */
	@Override
	public List<BlacklistedWordsDto> getAllBlackListedWords(String langCode) throws RegBaseCheckedException {

		List<BlacklistedWordsDto> blackWords = new ArrayList<>();
		if (langCodeNullCheck(langCode)) {
			List<BlacklistedWords> blackListedWords = masterSyncDao.getBlackListedWords(langCode);

			blackListedWords.forEach(blackList -> {

				BlacklistedWordsDto words = new BlacklistedWordsDto();
				words.setDescription(blackList.getDescription());
				words.setLangCode(blackList.getLangCode());
				words.setWord(blackList.getWord());
				blackWords.add(words);

			});
		} else {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.LANG_CODE_MANDATORY);
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_LANGCODE.getErrorCode(),
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_LANGCODE.getErrorMessage());
		}
		return blackWords;
	}

	/**
	 * Gets the gender details.
	 *
	 * @param langCode the lang code
	 * @return the gender dtls
	 * @throws RegBaseCheckedException
	 */
	@Override
	public List<GenderDto> getGenderDtls(String langCode) throws RegBaseCheckedException {
		List<GenderDto> gendetDtoList = new ArrayList<>();
		if (langCodeNullCheck(langCode)) {
			List<Gender> masterDocuments = masterSyncDao.getGenderDtls(langCode);
			masterDocuments.forEach(gender -> {
				GenderDto genders = new GenderDto();
				genders.setCode(gender.getCode());
				genders.setGenderName(gender.getGenderName());
				genders.setIsActive(gender.getIsActive());
				genders.setLangCode(gender.getLangCode());
				gendetDtoList.add(genders);
			});
		} else {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.LANG_CODE_MANDATORY);
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_LANGCODE.getErrorCode(),
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_LANGCODE.getErrorMessage());
		}
		return gendetDtoList;
	}

	/**
	 * Gets all the document categories from db that to be displayed in the UI
	 * dropdown.
	 *
	 * @param docCode  the doc code
	 * @param langCode the lang code
	 * @return all the document categories
	 * @throws RegBaseCheckedException
	 */
	@Override
	public List<DocumentCategoryDto> getDocumentCategories(String docCode, String langCode)
			throws RegBaseCheckedException {
		List<String> validDocuments = new ArrayList<>();
		List<DocumentCategoryDto> documentsDTO = new ArrayList<>();
		if (codeAndlangCodeNullCheck(docCode, langCode)) {
			List<ValidDocument> masterValidDocuments = masterSyncDao.getValidDocumets(docCode);
			masterValidDocuments.forEach(docs -> {
				validDocuments.add(docs.getDocTypeCode());
			});

			List<DocumentType> masterDocuments = masterSyncDao.getDocumentTypes(validDocuments, langCode);

			masterDocuments.forEach(document -> {

				DocumentCategoryDto documents = new DocumentCategoryDto();
				documents.setDescription(document.getDescription());
				documents.setLangCode(document.getLangCode());
				documents.setName(document.getName());
				documentsDTO.add(documents);

			});
		} else {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.CODE_AND_LANG_CODE_MANDATORY);
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_CODE_AND_LANGCODE.getErrorCode(),
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_CODE_AND_LANGCODE.getErrorMessage());
		}
		return documentsDTO;
	}

	/**
	 * Gets the individual type.
	 *
	 * @param code     the code
	 * @param langCode the lang code
	 * @return the individual type
	 * @throws RegBaseCheckedException
	 */
	@Override
	public List<IndividualTypeDto> getIndividualType(String code, String langCode) throws RegBaseCheckedException {
		List<IndividualTypeDto> listOfIndividualDTO = new ArrayList<>();

		if (codeAndlangCodeNullCheck(code, langCode)) {
			List<IndividualType> masterDocuments = masterSyncDao.getIndividulType(code, langCode);

			masterDocuments.forEach(individual -> {
				IndividualTypeDto individualDto = new IndividualTypeDto();
				individualDto.setName(individual.getName());
				individualDto.setCode(individual.getIndividualTypeId().getCode());
				individualDto.setLangCode(individual.getIndividualTypeId().getLangCode());
				listOfIndividualDTO.add(individualDto);
			});
		} else {

			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.CODE_AND_LANG_CODE_MANDATORY);
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_CODE_AND_LANGCODE.getErrorCode(),
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_CODE_AND_LANGCODE.getErrorMessage());
		}
		return listOfIndividualDTO;
	}

	/**
	 * Gets the biometric type.
	 *
	 * @param langCode the lang code
	 * @return the biometric type
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	public List<BiometricAttributeDto> getBiometricType(String langCode) throws RegBaseCheckedException {
		List<BiometricAttributeDto> listOfbiometricAttributeDTO = new ArrayList<>();
		if (langCodeNullCheck(langCode)) {
			List<String> biometricType = new LinkedList<>(
					Arrays.asList(RegistrationConstants.FNR, RegistrationConstants.IRS));

			if (RegistrationConstants.DISABLE.equalsIgnoreCase(
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.FINGERPRINT_DISABLE_FLAG)))) {
				biometricType.remove(RegistrationConstants.FNR);
			} else if (RegistrationConstants.DISABLE.equalsIgnoreCase(
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.IRIS_DISABLE_FLAG)))) {
				biometricType.remove(RegistrationConstants.IRS);
			}

			List<BiometricAttribute> masterBiometrics = masterSyncDao.getBiometricType(langCode, biometricType);

			masterBiometrics.forEach(biometrics -> {
				BiometricAttributeDto biometricsDto = new BiometricAttributeDto();
				biometricsDto.setName(biometrics.getName());
				biometricsDto.setCode(biometrics.getCode());
				biometricsDto.setBiometricTypeCode(biometrics.getBiometricTypeCode());
				biometricsDto.setLangCode(biometrics.getLangCode());
				listOfbiometricAttributeDTO.add(biometricsDto);
			});
		} else {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.LANG_CODE_MANDATORY);
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_LANGCODE.getErrorCode(),
					RegistrationExceptionConstants.REG_MASTER_SYNC_SERVICE_IMPL_LANGCODE.getErrorMessage());
		}
		return listOfbiometricAttributeDTO;

	}

	/**
	 * Center re map flag.
	 *
	 * @param centerReMap the center re map
	 * @return the boolean
	 */
	@SuppressWarnings("unchecked")
	private Boolean centerReMapFlag(LinkedHashMap<String, Object> centerReMap) {

		LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "Logging center remap flag....");
		Boolean reMapFlag = false;

		if (null != centerReMap && null != centerReMap.get(RegistrationConstants.ERRORS)) {
			List<LinkedHashMap<String, Object>> errorMap = (List<LinkedHashMap<String, Object>>) centerReMap
					.get(RegistrationConstants.ERRORS);
			String errorMsg = (String) errorMap.get(0).get(RegistrationConstants.ERROR_MSG);
			if ("Registration Center has been updated for the received Machine ID".equalsIgnoreCase(errorMsg)) {
				reMapFlag = true;
			}
		}
		LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, Boolean.toString(reMapFlag));
		return reMapFlag;

	}

	/**
	 * Error msg.
	 *
	 * @param centerReMap the center re map
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	private String errorMsg(LinkedHashMap<String, Object> centerReMap) {
		LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, "Logging error message....");
		String errorMsg = RegistrationConstants.MASTER_SYNC + "-" + RegistrationConstants.MASTER_SYNC_FAILURE_MSG;
		if (null != centerReMap && centerReMap.size() > 0) {
			List<LinkedHashMap<String, Object>> errorMap = (List<LinkedHashMap<String, Object>>) centerReMap
					.get(RegistrationConstants.ERRORS);
			if (null != errorMap.get(0).get(RegistrationConstants.ERROR_MSG)) {
				errorMsg = (String) errorMap.get(0).get(RegistrationConstants.ERROR_MSG);
			}
		}
		LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID, errorMsg);
		return errorMsg;

	}

	/**
	 * Master sync fields validate with index.
	 *
	 * @param masterSyncDtls the master sync dtls
	 * @param triggerPoint   the trigger point
	 * @param keyIndex       the key index
	 * @return true, if successful
	 */
	private boolean masterSyncFieldsValidateWithIndex(String masterSyncDtls, String triggerPoint, String keyIndex) {

		if (StringUtils.isEmpty(masterSyncDtls)) {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"masterSyncDtls is missing it is a mandatory field.");
			return false;
		} else if (StringUtils.isEmpty(triggerPoint)) {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"triggerPoint is missing it is a mandatory field.");
			return false;
		} else if (RegistrationConstants.ENABLE
				.equals(String.valueOf(ApplicationContext.map().get(RegistrationConstants.TPM_AVAILABILITY)))
				&& StringUtils.isEmpty(keyIndex)) {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"keyIndex is missing it is a mandatory field.");
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Master sync fields validate.
	 *
	 * @param masterSyncDtls the master sync dtls
	 * @param triggerPoint   the trigger point
	 * @return true, if successful
	 */
	private boolean masterSyncFieldsValidate(String masterSyncDtls, String triggerPoint) {

		if (StringUtils.isEmpty(masterSyncDtls)) {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"masterSyncDtls is missing it is a mandatory field.");
			return false;
		} else if (StringUtils.isEmpty(triggerPoint)) {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"triggerPoint is missing it is a mandatory field.");
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Lang code null check.
	 *
	 * @param langCode the language code
	 * @return true, if successful
	 */
	private boolean langCodeNullCheck(String langCode) {
		if (StringUtils.isEmpty(langCode)) {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"language code is missing it is a mandatory field.");
			return false;
		} else {
			return true;
		}

	}

	private boolean codeAndlangCodeNullCheck(String code, String langCode) {

		if (StringUtils.isEmpty(code)) {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"code is missing it is a mandatory field.");
			return false;
		} else if (StringUtils.isEmpty(langCode)) {
			LOGGER.info(LOG_REG_MASTER_SYNC, APPLICATION_NAME, APPLICATION_ID,
					"language code is missing it is a mandatory field.");
			return false;
		} else {
			return true;
		}

	}
}
