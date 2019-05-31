
package io.mosip.preregistration.application.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.preregistration.application.code.RequestCodes;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.DemographicCreateResponseDTO;
import io.mosip.preregistration.application.dto.DemographicMetadataDTO;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.dto.DemographicUpdateResponseDTO;
import io.mosip.preregistration.application.dto.DemographicViewDTO;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.BookingDeletionFailedException;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.PreIdInvalidForUserIdException;
import io.mosip.preregistration.application.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordFailedToUpdateException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.RecordNotFoundForPreIdsException;
import io.mosip.preregistration.application.exception.RestCallException;
import io.mosip.preregistration.application.exception.util.DemographicExceptionCatcher;
import io.mosip.preregistration.application.repository.DemographicRepository;
import io.mosip.preregistration.application.service.util.DemographicServiceUtil;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DeleteBookingDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentDeleteResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentsMetaData;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.core.common.entity.DemographicEntity;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.EncryptionFailedException;
import io.mosip.preregistration.core.exception.HashingException;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.CryptoUtil;
import io.mosip.preregistration.core.util.HashUtill;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * This class provides the service implementation for Demographic
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Behera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 */
@Service
public class DemographicService {

	/**
	 * logger instance
	 */
	private Logger log = LoggerConfiguration.logConfig(DemographicService.class);
	/**
	 * Autowired reference for {@link #MosipPridGenerator<String>}
	 */
	@Autowired
	private PridGenerator<String> pridGenerator;

	/**
	 * Autowired reference for {@link #RegistrationRepositary}
	 */
	@Autowired
	private DemographicRepository demographicRepository;

	/**
	 * Autowired reference for {@link #DemographicServiceUtil}
	 */
	@Autowired
	private DemographicServiceUtil serviceUtil;

	/**
	 * Autowired reference for {@link #JsonValidatorImpl}
	 */
	@Autowired
	private IdObjectValidator jsonValidator;

	/**
	 * Autowired reference for {@link #RestTemplateBuilder}
	 */
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Autowired reference for {@link #AuditLogUtil}
	 */
	@Autowired
	AuditLogUtil auditLogUtil;

	/**
	 * Reference for ${document.resource.url} from property file
	 */
	@Value("${document.resource.url}")
	private String docResourceUrl;

	/**
	 * Reference for ${createId} from property file
	 */
	@Value("${mosip.preregistration.demographic.create.id}")
	private String createId;

	/**
	 * Reference for ${updateId} from property file
	 */
	@Value("${mosip.preregistration.demographic.update.id}")
	private String updateId;

	/**
	 * Reference for ${retrieveId} from property file
	 */
	@Value("${mosip.preregistration.demographic.retrieve.basic.id}")
	private String retrieveId;
	/**
	 * Reference for ${retrieveDetailsId} from property file
	 */
	@Value("${mosip.preregistration.demographic.retrieve.details.id}")
	private String retrieveDetailsId;

	/**
	 * Reference for ${retrieveStatusId} from property file
	 */
	@Value("${mosip.preregistration.demographic.retrieve.status.id}")
	private String retrieveStatusId;

	/**
	 * Reference for ${deleteId} from property file
	 */
	@Value("${mosip.preregistration.demographic.delete.id}")
	private String deleteId;

	/**
	 * Reference for ${updateStatusId} from property file
	 */
	@Value("${mosip.preregistration.demographic.update.status.id}")
	private String updateStatusId;

	/**
	 * Reference for ${dateId} from property file
	 */
	@Value("${mosip.preregistration.demographic.retrieve.date.id}")
	private String dateId;

	/**
	 * Reference for ${ver} from property file
	 */
	@Value("${version}")
	private String version;

	Map<String, String> idValidationFields = new HashMap<>();
	/**
	 * Reference for ${appointmentResourse.url} from property file
	 */
	@Value("${appointmentResourse.url}")
	private String appointmentResourseUrl;

	@Value("${booking.resource.url}")
	private String deleteAppointmentResourseUrl;

	@Value("${mosip.pregistration.pagesize}")
	private String pageSize;

	/**
	 * Reference for ${mosip.utc-datetime-pattern} from property file
	 */
	@Value("${mosip.utc-datetime-pattern}")
	private String dateFormat;

	/**
	 * Response status
	 */
	protected String trueStatus = "true";

	/**
	 * Request map to store the id and version and this is to be passed to request
	 * validator method.
	 */
	Map<String, String> requiredRequestMap = new HashMap<>();

	/**
	 * This method acts as a post constructor to initialize the required request
	 * parameters.
	 */
	@PostConstruct
	public void setup() {
		// requiredRequestMap.put("id", id);
		requiredRequestMap.put("version", version);
	}

	@Autowired
	CryptoUtil cryptoUtil;

	public AuthUserDetails authUserDetails() {
		return (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/*
	 * This method is used to create the demographic data by generating the unique
	 * PreId
	 * 
	 * @see
	 * io.mosip.registration.service.RegistrationService#addPreRegistration(java.
	 * lang.Object, java.lang.String)
	 * 
	 * @param demographicRequest pass demographic request
	 * 
	 * @return responseDTO
	 */
	public MainResponseDTO<DemographicCreateResponseDTO> addPreRegistration(
			MainRequestDTO<DemographicRequestDTO> request) {
		log.info("sessionId", "idType", "id", "In addPreRegistration method of pre-registration service ");
		log.info("sessionId", "idType", "id",
				"Pre Registration start time : " + DateUtils.getUTCCurrentDateTimeString());
		requiredRequestMap.put("id", createId);
		MainResponseDTO<DemographicCreateResponseDTO> mainResponseDTO = (MainResponseDTO<DemographicCreateResponseDTO>) serviceUtil
				.getMainResponseDto(request);
		boolean isSuccess = false;
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(request), requiredRequestMap)) {
				DemographicRequestDTO demographicRequest = request.getRequest();
				ValidationUtil.langvalidation(demographicRequest.getLangCode());
				log.info("sessionId", "idType", "id",
						"JSON validator start time : " + DateUtils.getUTCCurrentDateTimeString());
				jsonValidator.validateIdObject(demographicRequest.getDemographicDetails(),
						IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
				log.info("sessionId", "idType", "id",
						"JSON validator end time : " + DateUtils.getUTCCurrentDateTimeString());
				log.info("sessionId", "idType", "id",
						"Pre ID generation start time : " + DateUtils.getUTCCurrentDateTimeString());
				String preId = pridGenerator.generateId();
				log.info("sessionId", "idType", "id",
						"Pre ID generation end time : " + DateUtils.getUTCCurrentDateTimeString());
				DemographicEntity demographicEntity = demographicRepository
						.save(serviceUtil.prepareDemographicEntityForCreate(demographicRequest,
								StatusCodes.PENDING_APPOINTMENT.getCode(), authUserDetails().getUserId(), preId));
				DemographicCreateResponseDTO res = serviceUtil.setterForCreatePreRegistration(demographicEntity);

				mainResponseDTO.setResponse(res);

			}
			isSuccess = true;
			log.info("sessionId", "idType", "id",
					"Pre Registration end time : " + DateUtils.getUTCCurrentDateTimeString());
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In pre-registration service of addPreRegistration- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex, mainResponseDTO);

		} finally {
			mainResponseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
			if (isSuccess) {
				setAuditValues(EventId.PRE_407.toString(), EventName.PERSIST.toString(), EventType.BUSINESS.toString(),
						"Pre-Registration data is sucessfully saved in the demographic table",
						AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Failed to save the Pre-Registration data", AuditLogVariables.NO_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			}
		}
		return mainResponseDTO;

	}

	/*
	 * This method is used to update the demographic data by PreId
	 * 
	 * @see
	 * io.mosip.registration.service.RegistrationService#addPreRegistration(java.
	 * lang.Object, java.lang.String)
	 * 
	 * @param demographicRequest pass demographic request
	 * 
	 * @return responseDTO
	 */
	public MainResponseDTO<DemographicUpdateResponseDTO> updatePreRegistration(
			MainRequestDTO<DemographicRequestDTO> request, String preRegistrationId, String userId) {
		log.info("sessionId", "idType", "id", "In updatePreRegistration method of pre-registration service ");
		log.info("sessionId", "idType", "id",
				"Pre Registration start time : " + DateUtils.getUTCCurrentDateTimeString());
		MainResponseDTO<DemographicUpdateResponseDTO> mainResponseDTO = null;
		mainResponseDTO = (MainResponseDTO<DemographicUpdateResponseDTO>) serviceUtil.getMainResponseDto(request);
		requiredRequestMap.put("id", updateId);
		boolean isSuccess = false;
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(request), requiredRequestMap)) {
				ValidationUtil.langvalidation(request.getRequest().getLangCode());
				Map<String, String> requestParamMap = new HashMap<>();
				requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preRegistrationId);
				if (ValidationUtil.requstParamValidator(requestParamMap)) {
					DemographicRequestDTO demographicRequest = request.getRequest();
					log.info("sessionId", "idType", "id",
							"JSON validator start time : " + DateUtils.getUTCCurrentDateTimeString());
					jsonValidator.validateIdObject(demographicRequest.getDemographicDetails(),
							IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
					log.info("sessionId", "idType", "id",
							"JSON validator end time : " + DateUtils.getUTCCurrentDateTimeString());
					DemographicEntity demographicEntity = demographicRepository
							.findBypreRegistrationId(preRegistrationId);
					if (!serviceUtil.isNull(demographicEntity)) {
						userValidation(userId, demographicEntity.getCreatedBy());
						demographicEntity = demographicRepository.update(serviceUtil.prepareDemographicEntityForUpdate(
								demographicEntity, demographicRequest, demographicEntity.getStatusCode(),
								authUserDetails().getUserId(), preRegistrationId));
					} else {
						throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
								ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.getMessage());
					}
					DemographicUpdateResponseDTO res = serviceUtil.setterForUpdatePreRegistration(demographicEntity);

					// List<DemographicUpdateResponseDTO> saveList = new ArrayList<>();

					// saveList.add(res);
					mainResponseDTO.setResponse(res);
					// mainResponseDTO.setId(updateId);
					// mainResponseDTO.setVersion(version);
				}
			}
			isSuccess = true;
			log.info("sessionId", "idType", "id",
					"Pre Registration end time : " + DateUtils.getUTCCurrentDateTimeString());
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In pre-registration service of updatePreRegistration- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex, mainResponseDTO);

		} finally {
			mainResponseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
			if (isSuccess) {
				setAuditValues(EventId.PRE_402.toString(), EventName.UPDATE.toString(), EventType.BUSINESS.toString(),
						"Pre-Registration data is sucessfully updated in the demographic table",
						AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Failed to update the Pre-Registration data", AuditLogVariables.NO_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			}
		}
		return mainResponseDTO;
	}

	/**
	 * This Method is used to fetch all the applications created by User
	 * 
	 * @param userId
	 *            pass a userId through which user has logged in which can be either
	 *            email Id or phone number
	 * @return List of groupIds
	 * 
	 */
	public MainResponseDTO<DemographicMetadataDTO> getAllApplicationDetails(String userId, String pageIdx) {
		log.info("sessionId", "idType", "id", "In getAllApplicationDetails method of pre-registration service ");
		MainResponseDTO<DemographicMetadataDTO> response = new MainResponseDTO<>();
		Map<String, String> requestParamMap = new HashMap<>();
		DemographicMetadataDTO demographicMetadataDTO = new DemographicMetadataDTO();
		response.setId(retrieveId);
		response.setVersion(version);
		boolean isRetrieveSuccess = false;
		try {
			requestParamMap.put(RequestCodes.USER_ID.getCode(), userId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				List<DemographicEntity> demographicEntities = demographicRepository.findByCreatedBy(userId,
						StatusCodes.CONSUMED.getCode());
				if (!serviceUtil.isNull(demographicEntities)) {
					/*
					 * Fetch all the records for the user irrespective of page index and page sixe
					 */
					if (serviceUtil.isNull(pageIdx)) {
						prepareDemographicResponse(demographicMetadataDTO, demographicEntities);
						demographicMetadataDTO.setNoOfRecords("0");
						demographicMetadataDTO.setTotalRecords(Integer.toString(demographicEntities.size()));
						demographicMetadataDTO.setPageIndex("0");
						response.setResponse(demographicMetadataDTO);
					} else {
						/*
						 * Fetch all the pageable records for the user with respect to page index and
						 * page size
						 */
						Page<DemographicEntity> demographicEntityPage = demographicRepository
								.findByCreatedByOrderByCreateDateTime(userId, StatusCodes.CONSUMED.getCode(),
										PageRequest.of(Integer.parseInt(pageIdx), Integer.parseInt(pageSize)));
						if (!serviceUtil.isNull(demographicEntityPage)
								&& !serviceUtil.isNull(demographicEntityPage.getContent())) {
							prepareDemographicResponse(demographicMetadataDTO, demographicEntityPage.getContent());
							demographicMetadataDTO
									.setNoOfRecords(Integer.toString(demographicEntityPage.getContent().size()));
							demographicMetadataDTO.setTotalRecords(Integer.toString(demographicEntities.size()));
							demographicMetadataDTO.setPageIndex(pageIdx);
							response.setResponse(demographicMetadataDTO);

						} else {
							throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_016.getCode(),
									ErrorMessages.PAGE_NOT_FOUND.getMessage());
						}
					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
							ErrorMessages.NO_RECORD_FOUND_FOR_USER_ID.getMessage());
				}
			}
			isRetrieveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getAllApplicationDetails method of pre-registration service - " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex, response);
		} finally {
			response.setResponsetime(serviceUtil.getCurrentResponseTime());
			if (isRetrieveSuccess) {
				setAuditValues(EventId.PRE_401.toString(), EventName.RETRIEVE.toString(), EventType.BUSINESS.toString(),
						"Retrieve All Pre-Registration id, Full name, Status and Appointment details by user id",
						AuditLogVariables.MULTIPLE_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Retrieve All Pre-Registration data failed", AuditLogVariables.NO_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			}
		}
		return response;
	}

	private void prepareDemographicResponse(DemographicMetadataDTO demographicMetadataDTO,
			List<DemographicEntity> demographicEntities)
			throws ParseException, EncryptionFailedException, JsonProcessingException {

		List<DemographicViewDTO> viewList = new ArrayList<>();
		for (DemographicEntity demographicEntity : demographicEntities) {
			byte[] decryptedString = cryptoUtil.decrypt(demographicEntity.getApplicantDetailJson(),
					DateUtils.getUTCCurrentDateTime());

			String nameValue = serviceUtil.getPreregistrationIdentityJson().getIdentity().getName().getValue();
			String poaValue = serviceUtil.getPreregistrationIdentityJson().getIdentity().getProofOfAddress().getValue();
			String postalCodeValue = serviceUtil.getPreregistrationIdentityJson().getIdentity().getPostalCode()
					.getValue();

			JSONObject documentJsonObject = getDocumentMetadata(demographicEntity, RequestCodes.POA.getCode());

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(new String(decryptedString));
			JSONObject demographicMetadata = new JSONObject();
			demographicMetadata.put(nameValue, serviceUtil.getValueFromIdentity(decryptedString, nameValue));
			demographicMetadata.put(postalCodeValue,
					serviceUtil.getIdJSONValue(jsonObj.toJSONString(), postalCodeValue));
			demographicMetadata.put(poaValue, documentJsonObject);

			DemographicViewDTO viewDto = new DemographicViewDTO();
			viewDto.setPreRegistrationId(demographicEntity.getPreRegistrationId());
			viewDto.setStatusCode(demographicEntity.getStatusCode());
			viewDto.setDemographicMetadata(demographicMetadata);
			BookingRegistrationDTO bookingRegistrationDTO = getAppointmentDetailsRestService(
					demographicEntity.getPreRegistrationId());
			if (bookingRegistrationDTO != null) {
				viewDto.setBookingMetadata(bookingRegistrationDTO);
			}
			viewList.add(viewDto);
		}
		demographicMetadataDTO.setBasicDetails(viewList);
	}

	/**
	 * This Method is used to fetch status of particular preId
	 * 
	 * @param preRegId
	 *            pass preRegId of the user
	 * @return response status of the preRegId
	 * 
	 * 
	 */
	public MainResponseDTO<PreRegistartionStatusDTO> getApplicationStatus(String preRegId, String userId) {
		log.info("sessionId", "idType", "id", "In getApplicationStatus method of pre-registration service ");
		PreRegistartionStatusDTO statusdto = new PreRegistartionStatusDTO();
		MainResponseDTO<PreRegistartionStatusDTO> response = new MainResponseDTO<>();
		Map<String, String> requestParamMap = new HashMap<>();
		response.setId(retrieveStatusId);
		response.setVersion(version);
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preRegId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);

				if (demographicEntity != null) {
					// userValidation(userId, demographicEntity.getCreatedBy());
					String hashString = HashUtill.hashUtill(demographicEntity.getApplicantDetailJson());

					if (HashUtill.isHashEqual(demographicEntity.getDemogDetailHash().getBytes(),
							hashString.getBytes())) {
						statusdto.setPreRegistartionId(demographicEntity.getPreRegistrationId());
						statusdto.setStatusCode(demographicEntity.getStatusCode());
						response.setResponse(statusdto);

					} else {
						throw new HashingException(
								io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_010.name(),
								io.mosip.preregistration.core.errorcodes.ErrorMessages.HASHING_FAILED.name());

					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
							ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.getMessage());

				}
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getApplicationStatus method of pre-registration service - " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex, response);
		}
		return response;
	}

	/**
	 * This Method is used to delete the Individual Application and documents
	 * associated with it
	 * 
	 * @param preregId
	 *            pass the preregId of individual
	 * @return response
	 * 
	 */
	public MainResponseDTO<DeletePreRegistartionDTO> deleteIndividual(String preregId, String userId) {
		log.info("sessionId", "idType", "id", "In deleteIndividual method of pre-registration service ");
		MainResponseDTO<DeletePreRegistartionDTO> response = new MainResponseDTO<>();
		DeletePreRegistartionDTO deleteDto = new DeletePreRegistartionDTO();
		Map<String, String> requestParamMap = new HashMap<>();
		boolean isDeleteSuccess = false;
		response.setId(deleteId);
		response.setVersion(version);
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preregId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preregId);
				if (!serviceUtil.isNull(demographicEntity)) {
					userValidation(userId, demographicEntity.getCreatedBy());
					if (serviceUtil.checkStatusForDeletion(demographicEntity.getStatusCode())) {
						getDocumentServiceToDeleteAllByPreId(preregId);
						if (!(demographicEntity.getStatusCode().equals(StatusCodes.PENDING_APPOINTMENT.getCode()))) {
							getBookingServiceToDeleteAllByPreId(preregId);
						}
						int isDeletedDemo = demographicRepository.deleteByPreRegistrationId(preregId);
						if (isDeletedDemo > 0) {
							deleteDto.setPreRegistrationId(demographicEntity.getPreRegistrationId());
							deleteDto.setDeletedBy(demographicEntity.getCreatedBy());
							deleteDto.setDeletedDateTime(new Date(System.currentTimeMillis()));

						} else {
							throw new RecordFailedToDeleteException(ErrorCodes.PRG_PAM_APP_004.getCode(),
									ErrorMessages.FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD.getMessage());
						}
					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
							ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.getMessage());
				}
			}
			isDeleteSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In pre-registration deleteIndividual service- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex, response);
		} finally {
			response.setResponsetime(serviceUtil.getCurrentResponseTime());
			if (isDeleteSuccess) {
				setAuditValues(EventId.PRE_403.toString(), EventName.DELETE.toString(), EventType.BUSINESS.toString(),
						"Pre-Registration data is successfully deleted from demographic table",
						AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Deletion of Pre-Registration data failed", AuditLogVariables.NO_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			}
		}

		response.setResponse(deleteDto);
		return response;
	}

	/**
	 * This Method is used to retrieve the demographic
	 * 
	 * @param preRegId
	 *            pass the preregId of individual
	 * @return response DemographicData of preRegId
	 */
	public MainResponseDTO<DemographicResponseDTO> getDemographicData(String preRegId) {
		log.info("sessionId", "idType", "id", "In getDemographicData method of pre-registration service ");
		MainResponseDTO<DemographicResponseDTO> response = new MainResponseDTO<>();
		Map<String, String> requestParamMap = new HashMap<>();
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(retrieveDetailsId);
		response.setVersion(version);
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preRegId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);
				if (demographicEntity != null) {
					String hashString = HashUtill.hashUtill(demographicEntity.getApplicantDetailJson());

					if (HashUtill.isHashEqual(demographicEntity.getDemogDetailHash().getBytes(),
							hashString.getBytes())) {

						DemographicResponseDTO createDto = serviceUtil.setterForCreateDTO(demographicEntity);
						response.setResponse(createDto);
					} else {
						throw new HashingException(
								io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_010.name(),
								io.mosip.preregistration.core.errorcodes.ErrorMessages.HASHING_FAILED.name());

					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
							ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.getMessage());
				}
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getDemographicData of pre-registration service- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex, response);
		}

		response.setErrors(null);
		return response;
	}

	/**
	 * This Method is used to update status of particular preId
	 * 
	 * @param preRegId
	 *            pass the preregId of individual
	 * @param status
	 *            pass the status of individual
	 * @return response
	 * 
	 * 
	 */
	public MainResponseDTO<String> updatePreRegistrationStatus(String preRegId, String status, String userId) {
		log.info("sessionId", "idType", "id", "In updatePreRegistrationStatus method of pre-registration service ");
		MainResponseDTO<String> response = new MainResponseDTO<>();
		Map<String, String> requestParamMap = new HashMap<>();
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(updateStatusId);
		response.setVersion(version);
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preRegId);
			requestParamMap.put(RequestCodes.STATUS_CODE.getCode(), status);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);
				statusCheck(demographicEntity, status, userId);
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In updatePreRegistrationStatus method of pre-registration service- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex, response);
		}
		response.setResponse("STATUS_UPDATED_SUCESSFULLY");

		return response;
	}

	/**
	 * This method will check the status before updating.
	 * 
	 * @param demographicEntity
	 *            pass demographicEntity
	 * @param status
	 *            pass status
	 */
	public void statusCheck(DemographicEntity demographicEntity, String status, String userId) {
		if (demographicEntity != null) {
			// userValidation(userId, demographicEntity.getCreatedBy());
			if (serviceUtil.isStatusValid(status)) {
				demographicEntity.setStatusCode(StatusCodes.valueOf(status.toUpperCase()).getCode());
				demographicRepository.update(demographicEntity);
			} else {
				throw new RecordFailedToUpdateException(ErrorCodes.PRG_PAM_APP_005.getCode(),
						ErrorMessages.INVALID_STATUS_CODE.getMessage());
			}
		} else {
			throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
					ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.getMessage());
		}
	}

	/**
	 * This method will iterate the list of demographicEntity and add pre-ids to
	 * list of string
	 * 
	 * @param demographicEntityList
	 *            pass demographicEntityList
	 * @return List of pre-ids
	 */
	public List<String> getPreRegistrationByDateEntityCheck(List<DemographicEntity> demographicEntityList) {
		List<String> preIds = new ArrayList<>();
		if (demographicEntityList != null && !demographicEntityList.isEmpty()) {
			for (DemographicEntity entity : demographicEntityList) {
				if (entity.getDemogDetailHash().equals(HashUtill.hashUtill(entity.getApplicantDetailJson()))) {
					preIds.add(entity.getPreRegistrationId());
				} else {

					log.error("sessionId", "idType", "id", "In dtoSetter method of document service - "
							+ io.mosip.preregistration.core.errorcodes.ErrorMessages.HASHING_FAILED.getMessage());
					throw new HashingException(
							io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_010.getCode(),
							io.mosip.preregistration.core.errorcodes.ErrorMessages.HASHING_FAILED.getMessage());
				}

			}
		} else {
			throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
					ErrorMessages.RECORD_NOT_FOUND_FOR_DATE_RANGE.getMessage());
		}
		return preIds;
	}

	/**
	 * This private Method is used to retrieve booking data by date
	 * 
	 * @param preId
	 * @return BookingRegistrationDTO
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * 
	 */
	private BookingRegistrationDTO getAppointmentDetailsRestService(String preId) {
		log.info("sessionId", "idType", "id",
				"In callGetAppointmentDetailsRestService method of pre-registration service ");

		BookingRegistrationDTO bookingRegistrationDTO = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("preRegistrationId", preId);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(appointmentResourseUrl + "/appointment/");
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<MainResponseDTO<BookingRegistrationDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			uriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id", "In callGetAppointmentDetailsRestService method URL- " + uriBuilder);

			ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<BookingRegistrationDTO>>() {
					}, params);
			if (respEntity.getBody().getErrors() == null) {
				bookingRegistrationDTO = respEntity.getBody().getResponse();
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In callGetAppointmentDetailsRestService method of pre-registration service - " + ex.getMessage());
			return bookingRegistrationDTO;
		}
		return bookingRegistrationDTO;
	}

	/**
	 * This private Method is used to call rest service to delete document by preId
	 * 
	 * @param preregId
	 * @return boolean
	 * 
	 */
	private void getDocumentServiceToDeleteAllByPreId(String preregId) {
		log.info("sessionId", "idType", "id",
				"In callDocumentServiceToDeleteAllByPreId method of pre-registration service ");
		ResponseEntity<MainResponseDTO<DocumentDeleteResponseDTO>> responseEntity = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("preRegistrationId", preregId);
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(docResourceUrl + "/documents/preregistration/");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<DocumentDeleteResponseDTO>> httpEntity = new HttpEntity<>(headers);
			String strUriBuilder = uriBuilder.build().encode().toUriString();
			strUriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id",
					"In callDocumentServiceToDeleteAllByPreId method URL- " + strUriBuilder);
			responseEntity = restTemplate.exchange(strUriBuilder, HttpMethod.DELETE, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<DocumentDeleteResponseDTO>>() {
					}, params);

			if (responseEntity.getBody().getErrors() != null) {
				if (!responseEntity.getBody().getErrors().get(0).getErrorCode()
						.equalsIgnoreCase(ErrorCodes.PRG_PAM_DOC_005.toString())) {
					throw new DocumentFailedToDeleteException(
							responseEntity.getBody().getErrors().get(0).getErrorCode(),
							responseEntity.getBody().getErrors().get(0).getMessage());
				}
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callDocumentServiceToDeleteAllByPreId method of pre-registration service- " + ex.getMessage());
			throw new RestCallException(ErrorCodes.PRG_PAM_APP_014.getCode(),
					ErrorMessages.DOCUMENT_SERVICE_FAILED_TO_CALL.getMessage());
		}
	}

	/**
	 * This method is used to audit all the demographic events
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventType
	 * @param description
	 * @param idType
	 */
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,
			String userId, String userName) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setDescription(description);
		auditRequestDto.setId(idType);
		auditRequestDto.setSessionUserId(userId);
		auditRequestDto.setSessionUserName(userName);
		auditRequestDto.setModuleId(AuditLogVariables.DEM.toString());
		auditRequestDto.setModuleName(AuditLogVariables.DEMOGRAPHY_SERVICE.toString());
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}

	private void getBookingServiceToDeleteAllByPreId(String preregId) {
		log.info("sessionId", "idType", "id",
				"In callBookingServiceToDeleteAllByPreId method of pre-registration service ");
		ResponseEntity<MainResponseDTO<DeleteBookingDTO>> responseEntity = null;
		try {

			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(deleteAppointmentResourseUrl + "/appointment")
					.queryParam("preRegistrationId", preregId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<DeleteBookingDTO>> httpEntity = new HttpEntity<>(headers);
			String strUriBuilder = uriBuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In callBookingServiceToDeleteAllByPreId method URL- " + strUriBuilder);
			responseEntity = restTemplate.exchange(strUriBuilder, HttpMethod.DELETE, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<DeleteBookingDTO>>() {
					});

			if (responseEntity.getBody().getErrors() != null) {
				throw new BookingDeletionFailedException(ErrorCodes.PRG_PAM_DOC_016.getCode(),
						ErrorMessages.BOOKING_FAILED_TO_DELETE.getMessage());

			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callBookingServiceToDeleteAllByPreId method of pre-registration service- " + ex.getMessage());
			throw new BookingDeletionFailedException(ErrorCodes.PRG_PAM_DOC_016.getCode(),
					ErrorMessages.BOOKING_FAILED_TO_DELETE.getMessage());
		}
	}

	public MainResponseDTO<Map<String, String>> getUpdatedDateTimeForPreIds(
			PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO) {
		log.info("sessionId", "idType", "id",
				"In getUpdatedDateTimeForPreIds method of pre-registration service " + preRegIdsByRegCenterIdDTO);
		MainResponseDTO<Map<String, String>> mainResponseDTO = new MainResponseDTO<>();
		mainResponseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
		mainResponseDTO.setId(dateId);
		mainResponseDTO.setVersion(version);

		try {
			Map<String, String> preIdMap = new HashMap<>();
			if (preRegIdsByRegCenterIdDTO.getPreRegistrationIds() != null
					&& !preRegIdsByRegCenterIdDTO.getPreRegistrationIds().isEmpty()) {
				List<String> preIds = preRegIdsByRegCenterIdDTO.getPreRegistrationIds();

				List<String> statusCodes = new ArrayList<>();
				statusCodes.add(StatusCodes.BOOKED.getCode());
				statusCodes.add(StatusCodes.EXPIRED.getCode());

				List<DemographicEntity> demographicEntities = demographicRepository
						.findByStatusCodeInAndPreRegistrationIdIn(statusCodes, preIds);
				if (demographicEntities != null && !demographicEntities.isEmpty()) {
					for (DemographicEntity demographicEntity : demographicEntities) {
						preIdMap.put(demographicEntity.getPreRegistrationId(),
								demographicEntity.getUpdateDateTime().toString());
					}
				} else {
					throw new RecordNotFoundForPreIdsException(ErrorCodes.PRG_PAM_APP_005.getCode(),
							ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.getMessage());
				}
			} else {
				throw new RecordNotFoundForPreIdsException(
						io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_001.getCode(),
						io.mosip.preregistration.core.errorcodes.ErrorMessages.MISSING_REQUEST_PARAMETER.getMessage());
			}

			mainResponseDTO.setResponse(preIdMap);
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getUpdatedDateTimeForPreIds method of pre-registration service- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex, mainResponseDTO);
		}
		return mainResponseDTO;

	}

	public void userValidation(String authUserId, String preregUserId) {
		if (!authUserId.trim().equals(preregUserId.trim())) {
			throw new PreIdInvalidForUserIdException(ErrorCodes.PRG_PAM_APP_017.getCode(),
					ErrorMessages.INVALID_PREID_FOR_USER.getMessage());
		}
	}

	private JSONObject getDocumentMetadata(DemographicEntity demographicEntity, String poa)
			throws JsonProcessingException, ParseException {
		String documentJsonString = "";
		DocumentsMetaData documentsMetaData = getDocDetails(demographicEntity.getPreRegistrationId());
		if (documentsMetaData != null && documentsMetaData.getDocumentsMetaData() != null
				&& !documentsMetaData.getDocumentsMetaData().isEmpty()) {
			for (DocumentMultipartResponseDTO metaData : documentsMetaData.getDocumentsMetaData()) {
				if (metaData.getDocCatCode().equals(poa)) {
					documentJsonString = JsonUtils.javaObjectToJsonString(metaData);
					JSONParser jsonParser = new JSONParser();
					return (JSONObject) jsonParser.parse(documentJsonString);
				}
			}
		}
		return null;

	}

	public DocumentsMetaData getDocDetails(String preId) {
		log.info("sessionId", "idType", "id", "In getDocDetails method of demographic service");
		DocumentsMetaData responsestatusDto = new DocumentsMetaData();
		try {
			Map<String, String> params = new HashMap<>();
			params.put("preRegistrationId", preId);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(docResourceUrl + "/documents/preregistration/");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<DocumentsMetaData>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			uriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id", "In getDocDetails method URL- " + uriBuilder);
			ResponseEntity<MainResponseDTO<DocumentsMetaData>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpEntity, new ParameterizedTypeReference<MainResponseDTO<DocumentsMetaData>>() {
					}, params);
			if (respEntity.getBody().getErrors() != null) {
				log.info("sessionId", "idType", "id",
						"In getDocDetails method of demographic service - Document not found for the pre_registration_id");
			} else {
				Object obj = respEntity.getBody().getResponse();
				ObjectMapper mapper = new ObjectMapper();
				responsestatusDto = mapper.convertValue(obj, DocumentsMetaData.class);
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In getDocDetails method of demographic service - " + ex.getMessage());
			throw new RestCallException(ErrorCodes.PRG_PAM_APP_014.getCode(),
					ErrorMessages.DOCUMENT_SERVICE_FAILED_TO_CALL.getMessage());
		}
		return responsestatusDto;
	}
}
