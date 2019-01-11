/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.service;

import java.net.URLDecoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.preregistration.application.code.RequestCodes;
import io.mosip.preregistration.application.code.StatusCodes;
import io.mosip.preregistration.application.config.LoggerConfiguration;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.dto.UpdateResponseDTO;
import io.mosip.preregistration.application.entity.DemographicEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordFailedToUpdateException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.util.DemographicExceptionCatcher;
import io.mosip.preregistration.application.repository.DemographicRepository;
import io.mosip.preregistration.application.service.util.DemographicServiceUtil;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentDeleteResponseDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
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
	private RestTemplateBuilder restTemplateBuilder;

	/**
	 * Reference for ${resource.url} from property file
	 */
	@Value("${resource.url}")
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
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestParamMap(demographicRequest),
					requiredRequestMap)) {
				jsonValidator.validateJson(demographicRequest.getRequest().getDemographicDetails().toJSONString(),
						"mosip-prereg-identity-json-schema.json");
				return createOrUpdate(demographicRequest.getRequest(), demographicRequest.getId());
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In pre-registration service of addPreRegistration- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);
		}
		return null;
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
		try {
			requestParamMap.put(RequestCodes.userId.toString(), userId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				List<DemographicEntity> demographicEntityList = demographicRepository.findByCreatedBy(userId,
						StatusCodes.Consumed.toString());
				if (!serviceUtil.isNull(demographicEntityList)) {
					for (DemographicEntity demographicEntity : demographicEntityList) {
						String identityValue = serviceUtil.getValueFromIdentity(
								demographicEntity.getApplicantDetailJson(), RequestCodes.fullName.toString());
						viewDto = new PreRegistrationViewDTO();
						viewDto.setPreId(demographicEntity.getPreRegistrationId());
						viewDto.setFullname(identityValue);
						viewDto.setStatusCode(demographicEntity.getStatusCode());

						BookingRegistrationDTO bookingRegistrationDTO = callGetAppointmentDetailsRestService(
								demographicEntity.getPreRegistrationId());
						if (bookingRegistrationDTO != null) {
							viewDto.setBookingRegistrationDTO(bookingRegistrationDTO);
						}
						viewList.add(viewDto);
					}
					response.setResponse(viewList);
					response.setResTime(serviceUtil.getCurrentResponseTime());
					response.setStatus(Boolean.TRUE);
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.NO_RECORD_FOUND_FOR_USER_ID.name());
				}
			}

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getAllApplicationDetails method of pre-registration service - " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);
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
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preRegId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);
				if (demographicEntity != null) {
					statusdto.setPreRegistartionId(demographicEntity.getPreRegistrationId());
					statusdto.setStatusCode(demographicEntity.getStatusCode());
					statusList.add(statusdto);
					response.setResponse(statusList);
					response.setResTime(serviceUtil.getCurrentResponseTime());
					response.setStatus(Boolean.TRUE);
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
		try {
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preregId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preregId);
				if (!serviceUtil.isNull(demographicEntity)) {
					if (serviceUtil.checkStatusForDeletion(demographicEntity.getStatusCode())) {
						callDocumentServiceToDeleteAllByPreId(preregId);
						int isDeletedDemo = demographicRepository.deleteByPreRegistrationId(preregId);
						if (isDeletedDemo > 0) {
							deleteDto.setPrId(demographicEntity.getPreRegistrationId());
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
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In pre-registration deleteIndividual service- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResTime(serviceUtil.getCurrentResponseTime());
		response.setStatus(Boolean.TRUE);
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
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preRegId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);
				if (demographicEntity != null) {
					DemographicResponseDTO createDto = serviceUtil.setterForCreateDTO(demographicEntity);
					createDtos.add(createDto);
					response.setResponse(createDtos);
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
		response.setResTime(serviceUtil.getCurrentResponseTime());
		response.setStatus(Boolean.TRUE);
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
	public UpdateResponseDTO<String> updatePreRegistrationStatus(String preRegId, String status) {
		log.info("sessionId", "idType", "id", "In updatePreRegistrationStatus method of pre-registration service ");
		UpdateResponseDTO<String> response = new UpdateResponseDTO<>();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preRegId);
			requestParamMap.put(RequestCodes.statusCode.toString(), status);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);
				if (demographicEntity != null) {
					if (serviceUtil.isStatusValid(status)) {
						demographicEntity.setStatusCode(StatusCodes.valueOf(status).toString());
						demographicRepository.update(demographicEntity);
						response.setResponse("STATUS_UPDATED_SUCESSFULLY");
						response.setResTime(new Timestamp(System.currentTimeMillis()));
						response.setStatus("true");
					} else {
						throw new RecordFailedToUpdateException(ErrorCodes.PRG_PAM_APP_005.name(),
								ErrorMessages.INVALID_STATUS_CODE.name());
					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.INVALID_PRE_REGISTRATION_ID.name());
				}
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In updatePreRegistrationStatus method of pre-registration service- " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);
		}
		return response;
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
	public MainListResponseDTO<String> getPreRegistrationByDate(String fromDate, String toDate) {
		log.info("sessionId", "idType", "id", "In getPreRegistrationByDate method of pre-registration service ");
		MainListResponseDTO<String> response = new MainListResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		Map<String, String> reqDateRange = new HashMap<>();
		Map<String, String> inputDateRange = new HashMap<>();
		try {
			reqDateRange.put(RequestCodes.fromDate.toString(), fromDate);
			reqDateRange.put(RequestCodes.toDate.toString(), toDate);
			String format = "yyyy-MM-dd HH:mm:ss";
			String parsedFromDate = URLDecoder.decode(reqDateRange.get(RequestCodes.fromDate.toString()), "UTF-8");
			String parsedToDate = URLDecoder.decode(reqDateRange.get(RequestCodes.toDate.toString()), "UTF-8");
			inputDateRange.put(RequestCodes.fromDate.toString(), parsedFromDate);
			inputDateRange.put(RequestCodes.toDate.toString(), parsedToDate);
			if (ValidationUtil.requstParamValidator(inputDateRange)) {
				Map<String, LocalDateTime> reqTimeStamp = serviceUtil.dateSetter(reqDateRange, format);
				List<DemographicEntity> details = demographicRepository.findBycreateDateTimeBetween(
						reqTimeStamp.get(RequestCodes.fromDate.toString()),
						reqTimeStamp.get(RequestCodes.toDate.toString()));
				if (details != null && !details.isEmpty()) {
					for (DemographicEntity entity : details) {
						preIds.add(entity.getPreRegistrationId());
					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.toString(),
							ErrorMessages.RECORD_NOT_FOUND_FOR_DATE_RANGE.toString());
				}
				response.setResponse(preIds);
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getPreRegistrationByDate method of pre-registration service - " + ex.getMessage());
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResTime(serviceUtil.getCurrentResponseTime());
		response.setStatus(Boolean.TRUE);
		response.setErr(null);
		return response;
	}

	/**
	 * This private Method is used to retrieve booking data by date
	 * 
	 * @param preId
	 * @return BookingRegistrationDTO
	 * 
	 */
	private BookingRegistrationDTO callGetAppointmentDetailsRestService(String preId) {
		log.info("sessionId", "idType", "id",
				"In callGetAppointmentDetailsRestService method of pre-registration service ");
		RestTemplate restTemplate = restTemplateBuilder.build();
		BookingRegistrationDTO bookingRegistrationDTO = null;
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(appointmentResourseUrl)
					.queryParam("pre_registration_id", preId);
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<MainResponseDTO<BookingRegistrationDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			log.info("sessionId", "idType", "id", "In callGetAppointmentDetailsRestService method URL- " + uriBuilder);
			ResponseEntity<MainResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					MainResponseDTO.class);
			if (respEntity.getBody().isStatus()) {
				ObjectMapper mapper = new ObjectMapper();
				bookingRegistrationDTO = mapper.convertValue(respEntity.getBody().getResponse(),
						BookingRegistrationDTO.class);
			}
		} catch (RestClientException ex) {
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
			demographicRequest.setPreRegistrationId(pridGenerator.generateId());
			demographicEntity = demographicRepository
					.save(serviceUtil.prepareDemographicEntity(demographicRequest, requestId, "save"));
		} else {
			demographicEntity = demographicRepository
					.findBypreRegistrationId(demographicRequest.getPreRegistrationId());
			if (!serviceUtil.isNull(demographicEntity)) {
				demographicRepository.deleteByPreRegistrationId(demographicRequest.getPreRegistrationId());
				demographicEntity = demographicRepository
						.save(serviceUtil.prepareDemographicEntity(demographicRequest, requestId, "update"));
			} else {
				throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
						ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
			}
		}
		DemographicResponseDTO res = serviceUtil.setterForCreateDTO(demographicEntity);
		response.setResTime(serviceUtil.getCurrentResponseTime());
		response.setStatus(Boolean.TRUE);
		saveList.add(res);
		response.setResponse(saveList);
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
		ResponseEntity<MainListResponseDTO> responseEntity = null;
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(resourceUrl + "pre-registration/deleteAllByPreRegId")
					.queryParam("pre_registration_id", preregId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<DocumentDeleteResponseDTO>> httpEntity = new HttpEntity<>(headers);
			String strUriBuilder = uriBuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In callDocumentServiceToDeleteAllByPreId method URL- " + strUriBuilder);
			responseEntity = restTemplate.exchange(strUriBuilder, HttpMethod.DELETE, httpEntity,
					MainListResponseDTO.class);

			if (!responseEntity.getBody().isStatus()) {
				if (!responseEntity.getBody().getErr().getErrorCode()
						.equalsIgnoreCase(ErrorCodes.PRG_PAM_DOC_005.toString())) {
					throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_015.name(),
							ErrorMessages.DOCUMENT_FAILED_TO_DELETE.name());
				}
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callDocumentServiceToDeleteAllByPreId method of pre-registration service- " + ex.getMessage());
			throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_015.name(),
					ErrorMessages.DOCUMENT_FAILED_TO_DELETE.name());
		}
	}
}