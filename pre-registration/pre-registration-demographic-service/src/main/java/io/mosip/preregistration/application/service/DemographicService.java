/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.preregistration.application.code.RequestCodes;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.DemographicCreateResponseDTO;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.dto.DemographicUpdateResponseDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.entity.DemographicEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.BookingDeletionFailedException;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
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
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
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
	private JsonValidatorImpl jsonValidator;

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
	private String resourceUrl;

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

	@Value("${mosip.id.validation.identity.email}")
	private String emailRegex;

	@Value("${mosip.id.validation.identity.phone}")
	private String phoneRegex;

	@Value("${mosip.id.validation.identity.postalCode}")
	private String postalRegex;

	@Value("${mosip.id.validation.identity.dateOfBirth}")
	private String dobRegex;

	@Value("${mosip.id.validation.identity.CNIENumber}")
	private String cnieRegex;

	Map<String, String> idValidationFields = new HashMap<>();
	/**
	 * Reference for ${appointmentResourse.url} from property file
	 */
	@Value("${appointmentResourse.url}")
	private String appointmentResourseUrl;

	@Value("${booking.resource.url}")
	private String deleteAppointmentResourseUrl;

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
	public MainListResponseDTO<DemographicCreateResponseDTO> addPreRegistration(
			MainRequestDTO<DemographicRequestDTO> request) {
		log.info("sessionId", "idType", "id", "In addPreRegistration method of pre-registration service ");
		log.info("sessionId", "idType", "id",
				"Pre Registration start time : " + DateUtils.getUTCCurrentDateTimeString());
		requiredRequestMap.put("id", createId);
		MainListResponseDTO<DemographicCreateResponseDTO> mainListResponseDTO = new MainListResponseDTO<>();
		boolean isSuccess = false;
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(request), requiredRequestMap)) {
				DemographicRequestDTO demographicRequest = request.getRequest();
				log.info("sessionId", "idType", "id",
						"JSON validator start time : " + DateUtils.getUTCCurrentDateTimeString());
				jsonValidator.validateJson(demographicRequest.getDemographicDetails().toJSONString());
				log.info("sessionId", "idType", "id",
						"JSON validator end time : " + DateUtils.getUTCCurrentDateTimeString());
				serviceUtil.validation(idValidation(), demographicRequest.getDemographicDetails());
				List<DemographicCreateResponseDTO> saveList = new ArrayList<>();
				log.info("sessionId", "idType", "id",
						"Pre ID generation start time : " + DateUtils.getUTCCurrentDateTimeString());
				String preId = pridGenerator.generateId();
				log.info("sessionId", "idType", "id",
						"Pre ID generation end time : " + DateUtils.getUTCCurrentDateTimeString());
				DemographicEntity demographicEntity = demographicRepository
						.save(serviceUtil.prepareDemographicEntityForCreate(demographicRequest,
								StatusCodes.PENDING_APPOINTMENT.getCode(), authUserDetails().getUserId(), preId));
				DemographicCreateResponseDTO res = serviceUtil.setterForCreatePreRegistration(demographicEntity);
				mainListResponseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
				saveList.add(res);
				mainListResponseDTO.setResponse(saveList);
				mainListResponseDTO.setId(createId);
				mainListResponseDTO.setVersion(version);

			}
			isSuccess = true;
			log.info("sessionId", "idType", "id",
					"Pre Registration end time : " + DateUtils.getUTCCurrentDateTimeString());
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In pre-registration service of addPreRegistration- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);

		} finally {
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
		return mainListResponseDTO;

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
	public MainListResponseDTO<DemographicUpdateResponseDTO> updatePreRegistration(
			MainRequestDTO<DemographicRequestDTO> request, String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In updatePreRegistration method of pre-registration service ");
		log.info("sessionId", "idType", "id",
				"Pre Registration start time : " + DateUtils.getUTCCurrentDateTimeString());
		MainListResponseDTO<DemographicUpdateResponseDTO> mainListResponseDTO = new MainListResponseDTO<>();
		requiredRequestMap.put("id", updateId);
		boolean isSuccess = false;
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(request), requiredRequestMap)) {
				Map<String, String> requestParamMap = new HashMap<>();
				requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preRegistrationId);
				if (ValidationUtil.requstParamValidator(requestParamMap)) {
					DemographicRequestDTO demographicRequest = request.getRequest();
					log.info("sessionId", "idType", "id",
							"JSON validator start time : " + DateUtils.getUTCCurrentDateTimeString());
					jsonValidator.validateJson(demographicRequest.getDemographicDetails().toJSONString());
					log.info("sessionId", "idType", "id",
							"JSON validator end time : " + DateUtils.getUTCCurrentDateTimeString());
					serviceUtil.validation(idValidation(), demographicRequest.getDemographicDetails());
					DemographicEntity demographicEntity = demographicRepository
							.findBypreRegistrationId(preRegistrationId);
					if (!serviceUtil.isNull(demographicEntity)) {
						demographicEntity = demographicRepository.update(serviceUtil.prepareDemographicEntityForUpdate(
								demographicEntity, demographicRequest, demographicEntity.getStatusCode(),
								authUserDetails().getUserId(), preRegistrationId));
					} else {
						throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
								ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.getMessage());
					}
					DemographicUpdateResponseDTO res = serviceUtil.setterForUpdatePreRegistration(demographicEntity);
					List<DemographicUpdateResponseDTO> saveList = new ArrayList<>();
					mainListResponseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
					saveList.add(res);
					mainListResponseDTO.setResponse(saveList);
					mainListResponseDTO.setId(updateId);
					mainListResponseDTO.setVersion(version);
				}
			}
			isSuccess = true;
			log.info("sessionId", "idType", "id",
					"Pre Registration end time : " + DateUtils.getUTCCurrentDateTimeString());
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In pre-registration service of updatePreRegistration- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);

		} finally {
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
		return mainListResponseDTO;
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
	public MainListResponseDTO<PreRegistrationViewDTO> getAllApplicationDetails(String userId) {
		log.info("sessionId", "idType", "id", "In getAllApplicationDetails method of pre-registration service ");
		MainListResponseDTO<PreRegistrationViewDTO> response = new MainListResponseDTO<>();
		List<PreRegistrationViewDTO> viewList = new ArrayList<>();
		PreRegistrationViewDTO viewDto = null;
		Map<String, String> requestParamMap = new HashMap<>();
		boolean isRetrieveSuccess = false;
		try {
			requestParamMap.put(RequestCodes.USER_ID.getCode(), userId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				List<DemographicEntity> demographicEntityList = demographicRepository.findByCreatedBy(userId,
						StatusCodes.CONSUMED.getCode());
				if (!serviceUtil.isNull(demographicEntityList)) {
					for (DemographicEntity demographicEntity : demographicEntityList) {
						byte[] decryptedString = cryptoUtil.decrypt(demographicEntity.getApplicantDetailJson(),
								DateUtils.getUTCCurrentDateTime());
						JSONParser jsonParser = new JSONParser();
						JSONObject jsonObj = (JSONObject) jsonParser.parse(new String(decryptedString));

						String postalcode = serviceUtil.getIdJSONValue(jsonObj.toJSONString(),
								RequestCodes.POSTAL_CODE.getCode());

						JSONArray identityValue = serviceUtil.getValueFromIdentity(decryptedString,
								RequestCodes.FULLNAME.getCode());
						viewDto = new PreRegistrationViewDTO();
						viewDto.setPreRegistrationId(demographicEntity.getPreRegistrationId());
						viewDto.setFullname(identityValue);
						viewDto.setStatusCode(demographicEntity.getStatusCode());
						viewDto.setPostalCode(postalcode);
						BookingRegistrationDTO bookingRegistrationDTO = callGetAppointmentDetailsRestService(
								demographicEntity.getPreRegistrationId());
						if (bookingRegistrationDTO != null) {
							viewDto.setBookingRegistrationDTO(bookingRegistrationDTO);
						}
						viewList.add(viewDto);
					}
					response.setResponse(viewList);
					response.setResponsetime(serviceUtil.getCurrentResponseTime());
					response.setId(retrieveId);
					response.setVersion(version);
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
							ErrorMessages.NO_RECORD_FOUND_FOR_USER_ID.getMessage());
				}
			}
			isRetrieveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getAllApplicationDetails method of pre-registration service - " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);
		} finally {
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

	/**
	 * This Method is used to fetch status of particular preId
	 * 
	 * @param preRegId
	 *            pass preRegId of the user
	 * @return response status of the preRegId
	 * 
	 * 
	 */
	public MainListResponseDTO<PreRegistartionStatusDTO> getApplicationStatus(String preRegId) {
		log.info("sessionId", "idType", "id", "In getApplicationStatus method of pre-registration service ");
		PreRegistartionStatusDTO statusdto = new PreRegistartionStatusDTO();
		MainListResponseDTO<PreRegistartionStatusDTO> response = new MainListResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preRegId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);

				if (demographicEntity != null) {
					String hashString = HashUtill.hashUtill(demographicEntity.getApplicantDetailJson());

					if (demographicEntity.getDemogDetailHash().equals(hashString)) {
						statusdto.setPreRegistartionId(demographicEntity.getPreRegistrationId());
						statusdto.setStatusCode(demographicEntity.getStatusCode());
						statusList.add(statusdto);
						response.setResponse(statusList);
						response.setId(retrieveStatusId);
						response.setVersion(version);
						response.setResponsetime(serviceUtil.getCurrentResponseTime());
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
			new DemographicExceptionCatcher().handle(ex);
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
	public MainListResponseDTO<DeletePreRegistartionDTO> deleteIndividual(String preregId) {
		log.info("sessionId", "idType", "id", "In deleteIndividual method of pre-registration service ");
		MainListResponseDTO<DeletePreRegistartionDTO> response = new MainListResponseDTO<>();
		List<DeletePreRegistartionDTO> deleteList = new ArrayList<>();
		DeletePreRegistartionDTO deleteDto = new DeletePreRegistartionDTO();
		Map<String, String> requestParamMap = new HashMap<>();
		boolean isDeleteSuccess = false;
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preregId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preregId);
				if (!serviceUtil.isNull(demographicEntity)) {
					if (serviceUtil.checkStatusForDeletion(demographicEntity.getStatusCode())) {
						callDocumentServiceToDeleteAllByPreId(preregId);
						if (!(demographicEntity.getStatusCode().equals(StatusCodes.PENDING_APPOINTMENT.getCode()))) {
							callBookingServiceToDeleteAllByPreId(preregId);
						}
						int isDeletedDemo = demographicRepository.deleteByPreRegistrationId(preregId);
						if (isDeletedDemo > 0) {
							deleteDto.setPreRegistrationId(demographicEntity.getPreRegistrationId());
							deleteDto.setDeletedBy(demographicEntity.getCreatedBy());
							deleteDto.setDeletedDateTime(new Date(System.currentTimeMillis()));
							deleteList.add(deleteDto);
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
			new DemographicExceptionCatcher().handle(ex);
		} finally {
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
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(deleteId);
		response.setVersion(version);
		response.setResponse(deleteList);
		return response;
	}

	/**
	 * This Method is used to retrieve the demographic
	 * 
	 * @param preRegId
	 *            pass the preregId of individual
	 * @return response DemographicData of preRegId
	 */
	public MainListResponseDTO<DemographicResponseDTO> getDemographicData(String preRegId) {
		log.info("sessionId", "idType", "id", "In getDemographicData method of pre-registration service ");
		List<DemographicResponseDTO> createDtos = new ArrayList<>();
		MainListResponseDTO<DemographicResponseDTO> response = new MainListResponseDTO<>();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preRegId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);
				if (demographicEntity != null) {
					String hashString = HashUtill.hashUtill(demographicEntity.getApplicantDetailJson());

					if (demographicEntity.getDemogDetailHash().equals(hashString)) {

						DemographicResponseDTO createDto = serviceUtil.setterForCreateDTO(demographicEntity);
						createDtos.add(createDto);
						response.setResponse(createDtos);
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
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(retrieveDetailsId);
		response.setVersion(version);
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
	public MainResponseDTO<String> updatePreRegistrationStatus(String preRegId, String status) {
		log.info("sessionId", "idType", "id", "In updatePreRegistrationStatus method of pre-registration service ");
		MainResponseDTO<String> response = new MainResponseDTO<>();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preRegId);
			requestParamMap.put(RequestCodes.STATUS_CODE.getCode(), status);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);
				statusCheck(demographicEntity, status);
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In updatePreRegistrationStatus method of pre-registration service- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResponse("STATUS_UPDATED_SUCESSFULLY");
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(updateStatusId);
		response.setVersion(version);
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
	public void statusCheck(DemographicEntity demographicEntity, String status) {
		if (demographicEntity != null) {
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
	 * This Method is used to retrieve demographic data by date
	 * 
	 * @param fromDate
	 *            pass fromDate
	 * @param toDate
	 *            pass toDate
	 * @return response List of preRegIds
	 * 
	 * 
	 */
	public MainListResponseDTO<String> getPreRegistrationByDate(LocalDate fromDate, LocalDate toDate) {
		log.info("sessionId", "idType", "id", "In getPreRegistrationByDate method of pre-registration service ");
		MainListResponseDTO<String> response = new MainListResponseDTO<>();
		try {
			LocalDateTime fromLocaldate = fromDate.atStartOfDay();

			LocalDateTime toLocaldate = toDate.atTime(23, 59, 59);

			List<DemographicEntity> details = demographicRepository.findBycreateDateTimeBetween(fromLocaldate,
					toLocaldate);

			response.setResponse(getPreRegistrationByDateEntityCheck(details));

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getPreRegistrationByDate method of pre-registration service - " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(dateId);
		response.setVersion(version);
		response.setErrors(null);
		return response;
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
	private BookingRegistrationDTO callGetAppointmentDetailsRestService(String preId) {
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
	 * This private Method is used to save demographic data
	 *
	 * @param DemographicRequestDTO
	 * @param requestId
	 * @return ResponseDTO<CreatePreRegistrationDTO>
	 *
	 */
	/*
	 * private MainListResponseDTO<DemographicResponseDTO>
	 * create(DemographicRequestDTO demographicRequest) { log.info("sessionId",
	 * "idType", "id", "In createOrUpdate method of pre-registration service ");
	 * MainListResponseDTO<DemographicResponseDTO> response = new
	 * MainListResponseDTO<>(); List<DemographicResponseDTO> saveList = new
	 * ArrayList<>(); log.info("sessionId", "idType", "id",
	 * "Pre ID generation start time : " + DateUtils.getUTCCurrentDateTimeString());
	 * String preId = pridGenerator.generateId(); log.info("sessionId", "idType",
	 * "id", "Pre ID generation end time : " +
	 * DateUtils.getUTCCurrentDateTimeString()); DemographicEntity demographicEntity
	 * = demographicRepository.save(serviceUtil.prepareDemographicEntityForCreate(
	 * demographicRequest, StatusCodes.PENDING_APPOINTMENT.getCode(),
	 * authUserDetails().getUserId(), preId)); DemographicResponseDTO res =
	 * serviceUtil.setterForCreateDTO(demographicEntity);
	 * response.setResponsetime(serviceUtil.getCurrentResponseTime());
	 * saveList.add(res); response.setResponse(saveList); response.setId(id);
	 * response.setVersion(ver); return response; }
	 */

	/**
	 * This private Method is used to update demographic data
	 * 
	 * @param DemographicRequestDTO
	 * @param requestId
	 * @return ResponseDTO<CreatePreRegistrationDTO>
	 * 
	 */
	/*
	 * private MainListResponseDTO<DemographicResponseDTO>
	 * update(DemographicRequestDTO demographicRequest, String preRegistrationId) {
	 * log.info("sessionId", "idType", "id", "In update method of pre-registration
	 * service "); MainListResponseDTO<DemographicResponseDTO> response = new
	 * MainListResponseDTO<>(); List<DemographicResponseDTO> saveList = new
	 * ArrayList<>(); DemographicEntity demographicEntity =
	 * demographicRepository.findBypreRegistrationId(preRegistrationId); if
	 * (!serviceUtil.isNull(demographicEntity)) { demographicEntity =
	 * demographicRepository
	 * .save(serviceUtil.prepareDemographicEntityForUpdate(demographicRequest,
	 * demographicEntity.getStatusCode(), authUserDetails().getUserId(),
	 * preRegistrationId)); } else { throw new
	 * RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
	 * ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name()); }
	 * DemographicResponseDTO res =
	 * serviceUtil.setterForCreateDTO(demographicEntity);
	 * 
	 * response.setResponsetime(serviceUtil.getCurrentResponseTime());
	 * saveList.add(res); response.setResponse(saveList); response.setId(id);
	 * response.setVersion(ver); return response; }
	 */

	/**
	 * This private Method is used to call rest service to delete document by preId
	 * 
	 * @param preregId
	 * @return boolean
	 * 
	 */
	private void callDocumentServiceToDeleteAllByPreId(String preregId) {
		log.info("sessionId", "idType", "id",
				"In callDocumentServiceToDeleteAllByPreId method of pre-registration service ");
		ResponseEntity<MainListResponseDTO<DocumentDeleteResponseDTO>> responseEntity = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("preRegistrationId", preregId);
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(resourceUrl + "/documents/preregistration/");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<DocumentDeleteResponseDTO>> httpEntity = new HttpEntity<>(headers);
			String strUriBuilder = uriBuilder.build().encode().toUriString();
			strUriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id",
					"In callDocumentServiceToDeleteAllByPreId method URL- " + strUriBuilder);
			responseEntity = restTemplate.exchange(strUriBuilder, HttpMethod.DELETE, httpEntity,
					new ParameterizedTypeReference<MainListResponseDTO<DocumentDeleteResponseDTO>>() {
					}, params);

			if (responseEntity.getBody().getErrors() != null) {
				if (!responseEntity.getBody().getErrors().getErrorCode()
						.equalsIgnoreCase(ErrorCodes.PRG_PAM_DOC_005.toString())) {
					throw new DocumentFailedToDeleteException(responseEntity.getBody().getErrors().getErrorCode(),
							responseEntity.getBody().getErrors().getMessage());
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

	private void callBookingServiceToDeleteAllByPreId(String preregId) {
		log.info("sessionId", "idType", "id",
				"In callBookingServiceToDeleteAllByPreId method of pre-registration service ");
		ResponseEntity<MainListResponseDTO<DeleteBookingDTO>> responseEntity = null;
		try {

			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(deleteAppointmentResourseUrl + "/appointment")
					.queryParam("preRegistrationId", preregId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<DeleteBookingDTO>> httpEntity = new HttpEntity<>(headers);
			String strUriBuilder = uriBuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In callBookingServiceToDeleteAllByPreId method URL- " + strUriBuilder);
			responseEntity = restTemplate.exchange(strUriBuilder, HttpMethod.DELETE, httpEntity,
					new ParameterizedTypeReference<MainListResponseDTO<DeleteBookingDTO>>() {
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
			mainResponseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
			mainResponseDTO.setId(dateId);
			mainResponseDTO.setVersion(version);
			mainResponseDTO.setResponse(preIdMap);
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getUpdatedDateTimeForPreIds method of pre-registration service- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);
		}
		return mainResponseDTO;

	}

	public Map<String, String> idValidation() throws ParseException {
		idValidationFields.put(RequestCodes.EMAIL.getCode(), emailRegex);
		idValidationFields.put(RequestCodes.PHONE.getCode(), phoneRegex);
		idValidationFields.put(RequestCodes.DOB.getCode(), dobRegex);
		idValidationFields.put(RequestCodes.CNIE_NUMBER.getCode(), cnieRegex);
		idValidationFields.put(RequestCodes.POSTAL_CODE.getCode(), postalRegex);
		return idValidationFields;

	}

}