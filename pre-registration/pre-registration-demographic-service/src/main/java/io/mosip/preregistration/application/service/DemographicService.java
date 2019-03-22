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

import io.mosip.kernel.auth.adapter.AuthUserDetails;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.preregistration.application.code.RequestCodes;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.entity.DemographicEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.BookingDeletionFailedException;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordFailedToUpdateException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
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
	 * Reference for ${id} from property file
	 */
	@Value("${id}")
	private String id;

	/**
	 * Reference for ${ver} from property file
	 */
	@Value("${ver}")
	private String ver;

	/**
	 * Reference for ${appointmentResourse.url} from property file
	 */
	@Value("${appointmentResourse.url}")
	private String appointmentResourseUrl;

	@Value("${booking.resource.url}")
	private String deleteAppointmentResourseUrl;
	/**
	 * Reference for ${schemaName} from property file
	 */
	@Value("${schemaName}")
	private String schemaName;

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
		requiredRequestMap.put("id", id);
		requiredRequestMap.put("ver", ver);
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
	public MainListResponseDTO<DemographicResponseDTO> addPreRegistration(
			MainRequestDTO<DemographicRequestDTO> demographicRequest) {
		log.info("sessionId", "idType", "id", "In addPreRegistration method of pre-registration service ");
		log.info("sessionId", "idType", "id",
				"Pre Registration start time : " + DateUtils.getUTCCurrentDateTimeString());
		MainListResponseDTO<DemographicResponseDTO> mainListResponseDTO = new MainListResponseDTO<>();
		boolean isSuccess = false;
		try {
			if (ValidationUtil.requestValidator(demographicRequest)) {
				log.info("sessionId", "idType", "id",
						"JSON validator start time : " + DateUtils.getUTCCurrentDateTimeString());
				jsonValidator.validateJson(demographicRequest.getRequest().getDemographicDetails().toJSONString(),
						schemaName);
				log.info("sessionId", "idType", "id",
						"JSON validator end time : " + DateUtils.getUTCCurrentDateTimeString());
				mainListResponseDTO = createOrUpdate(demographicRequest.getRequest(), demographicRequest.getId());
			}
			isSuccess = true;
			log.info("sessionId", "idType", "id",
					"Pre Registration end time : " + DateUtils.getUTCCurrentDateTimeString());
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In pre-registration service of addPreRegistration- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);

		} finally {
			if (!isSuccess) {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Failed to save the Pre-Registration data", AuditLogVariables.NO_ID.toString(),
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
						String postalcode = serviceUtil.getPostalCode(decryptedString,
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
					response.setId(id);
					response.setVersion(ver);
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.NO_RECORD_FOUND_FOR_USER_ID.name());
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
					String hashString = new String(HashUtill.hashUtill(demographicEntity.getApplicantDetailJson()));

					if (demographicEntity.getDemogDetailHash().equals(hashString)) {
						statusdto.setPreRegistartionId(demographicEntity.getPreRegistrationId());
						statusdto.setStatusCode(demographicEntity.getStatusCode());
						statusList.add(statusdto);
						response.setResponse(statusList);
						response.setId(id);
						response.setVersion(ver);
						response.setResponsetime(serviceUtil.getCurrentResponseTime());
					} else {
						throw new HashingException(
								io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_010.name(),
								io.mosip.preregistration.core.errorcodes.ErrorMessages.HASHING_FAILED.name());

					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.INVALID_PRE_REGISTRATION_ID.name());

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
							throw new RecordFailedToDeleteException(ErrorCodes.PRG_PAM_APP_004.name(),
									ErrorMessages.FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD.name());
						}
					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.INVALID_PRE_REGISTRATION_ID.name());
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
		response.setId(id);
		response.setVersion(ver);
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
					String hashString = new String(HashUtill.hashUtill(demographicEntity.getApplicantDetailJson()));

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
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
				}
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getDemographicData of pre-registration service- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(id);
		response.setVersion(ver);
		response.setErr(null);
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
		response.setId(id);
		response.setVersion(ver);
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
				throw new RecordFailedToUpdateException(ErrorCodes.PRG_PAM_APP_005.name(),
						ErrorMessages.INVALID_STATUS_CODE.name());
			}
		} else {
			throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
					ErrorMessages.INVALID_PRE_REGISTRATION_ID.name());
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
		response.setId(id);
		response.setVersion(ver);
		response.setErr(null);
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
				if (entity.getDemogDetailHash()
						.equals(new String(HashUtill.hashUtill(entity.getApplicantDetailJson())))) {
					preIds.add(entity.getPreRegistrationId());
				} else {

					log.error("sessionId", "idType", "id", "In dtoSetter method of document service - "
							+ io.mosip.preregistration.core.errorcodes.ErrorMessages.HASHING_FAILED.name());
					throw new HashingException(
							io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_010.name(),
							io.mosip.preregistration.core.errorcodes.ErrorMessages.HASHING_FAILED.name());
				}

			}
		} else {
			throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.toString(),
					ErrorMessages.RECORD_NOT_FOUND_FOR_DATE_RANGE.toString());
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
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(appointmentResourseUrl)
					.queryParam("pre_registration_id", preId);
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<MainResponseDTO<BookingRegistrationDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			log.info("sessionId", "idType", "id", "In callGetAppointmentDetailsRestService method URL- " + uriBuilder);
			
			ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<BookingRegistrationDTO>>() {
					});
			if(respEntity.getBody().getErrors()==null) {
				bookingRegistrationDTO=respEntity.getBody().getResponse();
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In callGetAppointmentDetailsRestService method of pre-registration service - " + ex.getMessage());
			return bookingRegistrationDTO;
		}
		return bookingRegistrationDTO;
	}

	/**
	 * This private Method is used to save and update demographic data
	 * 
	 * @param DemographicRequestDTO
	 * @param requestId
	 * @return ResponseDTO<CreatePreRegistrationDTO>
	 * 
	 */
	private MainListResponseDTO<DemographicResponseDTO> createOrUpdate(DemographicRequestDTO demographicRequest,
			String requestId) {
		log.info("sessionId", "idType", "id", "In createOrUpdate method of pre-registration service ");
		MainListResponseDTO<DemographicResponseDTO> response = new MainListResponseDTO<>();
		List<DemographicResponseDTO> saveList = new ArrayList<>();
		DemographicEntity demographicEntity;
		if (serviceUtil.isNull(demographicRequest.getPreRegistrationId())) {
			log.info("sessionId", "idType", "id",
					"Pre ID generation start time : " + DateUtils.getUTCCurrentDateTimeString());
			demographicRequest.setPreRegistrationId(pridGenerator.generateId());
			log.info("sessionId", "idType", "id",
					"Pre ID generation end time : " + DateUtils.getUTCCurrentDateTimeString());
			demographicEntity = demographicRepository.save(serviceUtil.prepareDemographicEntity(demographicRequest,
					requestId, RequestCodes.SAVE.getCode(), StatusCodes.PENDING_APPOINTMENT.getCode()));
			setAuditValues(EventId.PRE_407.toString(), EventName.PERSIST.toString(), EventType.BUSINESS.toString(),
					"Pre-Registration data is sucessfully saved in the demographic table",
					AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(), authUserDetails().getUsername());
		} else {
			demographicEntity = demographicRepository
					.findBypreRegistrationId(demographicRequest.getPreRegistrationId());
			if (!serviceUtil.isNull(demographicEntity)) {
				demographicEntity = demographicRepository.save(serviceUtil.prepareDemographicEntity(demographicRequest,
						requestId, RequestCodes.UPDATE.getCode(), demographicEntity.getStatusCode()));
				setAuditValues(EventId.PRE_402.toString(), EventName.UPDATE.toString(), EventType.BUSINESS.toString(),
						"Pre-Registration data is sucessfully updated in the demographic table",
						AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			} else {
				throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
						ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
			}
		}
		DemographicResponseDTO res = serviceUtil.setterForCreateDTO(demographicEntity);
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		saveList.add(res);
		response.setResponse(saveList);
		response.setId(id);
		response.setVersion(ver);
		return response;
	}

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
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(resourceUrl + "/documents/byPreRegId")
					.queryParam("pre_registration_id", preregId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<DocumentDeleteResponseDTO>> httpEntity = new HttpEntity<>(headers);
			String strUriBuilder = uriBuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In callDocumentServiceToDeleteAllByPreId method URL- " + strUriBuilder);
			responseEntity = restTemplate.exchange(strUriBuilder, HttpMethod.DELETE, httpEntity,
					new ParameterizedTypeReference<MainListResponseDTO<DocumentDeleteResponseDTO>>() {
					});

			if (responseEntity.getBody().getErr() != null) {

				throw new DocumentFailedToDeleteException(responseEntity.getBody().getErr().getErrorCode(),
						responseEntity.getBody().getErr().getMessage());

			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callDocumentServiceToDeleteAllByPreId method of pre-registration service- " + ex.getMessage());
			throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_015.name(),
					ErrorMessages.DOCUMENT_FAILED_TO_DELETE.name());
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
					.queryParam("pre_registration_id", preregId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<DeleteBookingDTO>> httpEntity = new HttpEntity<>(headers);
			String strUriBuilder = uriBuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In callBookingServiceToDeleteAllByPreId method URL- " + strUriBuilder);
			responseEntity = restTemplate.exchange(strUriBuilder, HttpMethod.DELETE, httpEntity,
					new ParameterizedTypeReference<MainListResponseDTO<DeleteBookingDTO>>() {
					});

			if (responseEntity.getBody().getErr() != null) {
				throw new BookingDeletionFailedException(ErrorCodes.PRG_PAM_DOC_016.name(),
						ErrorMessages.BOOKING_FAILED_TO_DELETE.name());

			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callBookingServiceToDeleteAllByPreId method of pre-registration service- " + ex.getMessage());
			throw new BookingDeletionFailedException(ErrorCodes.PRG_PAM_DOC_016.name(),
					ErrorMessages.BOOKING_FAILED_TO_DELETE.name());
		}
	}

	public MainResponseDTO<Map<String, String>> getUpdatedDateTimeForPreIds(
			PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO) {
		MainResponseDTO<Map<String, String>> mainResponseDTO = new MainResponseDTO<>();
		try {
			Map<String, String> preIdMap = new HashMap<>();
			if (preRegIdsByRegCenterIdDTO.getPreRegistrationIds() != null) {
				List<String> preIds = preRegIdsByRegCenterIdDTO.getPreRegistrationIds();
				List<DemographicEntity> demographicEntities = demographicRepository
						.findByStatusCodeAndPreRegistrationIdIn(StatusCodes.BOOKED.getCode(), preIds);
				if (demographicEntities != null && !demographicEntities.isEmpty()) {
					for (DemographicEntity demographicEntity : demographicEntities) {
						preIdMap.put(demographicEntity.getPreRegistrationId(),
								demographicEntity.getUpdateDateTime().toString());
					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_013.name(),
							ErrorMessages.RECORD_NOT_FOUND.name());
				}
			} else {
				throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
						ErrorMessages.INVALID_PRE_REGISTRATION_ID.name());
			}
			mainResponseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
			mainResponseDTO.setId(id);
			mainResponseDTO.setVersion(ver);
			mainResponseDTO.setResponse(preIdMap);
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		return mainResponseDTO;

	}

}