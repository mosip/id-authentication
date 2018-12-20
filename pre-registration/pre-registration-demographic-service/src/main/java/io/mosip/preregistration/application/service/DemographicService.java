package io.mosip.preregistration.application.service;

import java.sql.Timestamp;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.preregistration.application.code.RequestCodes;
import io.mosip.preregistration.application.code.StatusCodes;
import io.mosip.preregistration.application.dao.DemographicDAO;
import io.mosip.preregistration.application.dto.BookingRegistrationDTO;
import io.mosip.preregistration.application.dto.BookingResponseDTO;
import io.mosip.preregistration.application.dto.CreatePreRegistrationDTO;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.dto.ResponseDTO;
import io.mosip.preregistration.application.dto.UpdateResponseDTO;
import io.mosip.preregistration.application.entity.DemographicEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.util.DemographicExceptionCatcher;
import io.mosip.preregistration.application.repository.DemographicRepository;
import io.mosip.preregistration.application.service.util.DemographicServiceUtil;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * Demographic Service
 * 
 * @author M1037717
 *
 */
@Service
public class DemographicService {

	/**
	 * Field for {@link #RegistrationDao}
	 */
	@Autowired
	private DemographicDAO preRegistrationDao;

	/**
	 * Field for {@link #MosipPridGenerator<String>}
	 */
	@Autowired
	private PridGenerator<String> pridGenerator;

	/**
	 * Field for {@link #RegistrationRepositary}
	 */
	@Autowired
	private DemographicRepository preRegistrationRepository;

	@Autowired
	private DemographicServiceUtil serviceUtil;

	@Autowired
	private JsonValidatorImpl jsonValidator;

	@Value("${resource.url}")
	private String resourceUrl;

	@Value("${id}")
	private String id;

	@Value("${ver}")
	private String ver;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	protected String trueStatus = "true";

	Map<String, String> requiredRequestMap = new HashMap<>();

	@PostConstruct
	public void setup() {
		requiredRequestMap.put("id", id);
		requiredRequestMap.put("ver", ver);
	}

	@Value("${appointmentResourse.url}")
	private String appointmentResourseUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.RegistrationService#addPreRegistration(java.
	 * lang. Object, java.lang.String)
	 */
	public ResponseDTO<CreatePreRegistrationDTO> addPreRegistration(
			DemographicRequestDTO<CreatePreRegistrationDTO> demographicRequest) {
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap = serviceUtil.prepareRequestParamMap(demographicRequest);
			if (ValidationUtil.requestValidator(requestParamMap, requiredRequestMap)) {
				jsonValidator.validateJson(demographicRequest.getRequest().getDemographicDetails().toJSONString(),
						"mosip-prereg-identity-json-schema.json");
				return createOrUpdate(demographicRequest.getRequest(), demographicRequest.getId());
			}
		} catch (Exception ex) {
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
	public ResponseDTO<PreRegistrationViewDTO> getAllApplicationDetails(String userId) {
		ResponseDTO<PreRegistrationViewDTO> response = new ResponseDTO<>();
		List<PreRegistrationViewDTO> viewList = new ArrayList<>();
		PreRegistrationViewDTO viewDto = null;
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.userId.toString(), userId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				List<DemographicEntity> preRegistrationEntityList = preRegistrationRepository.findByuserId(userId);
				if (!serviceUtil.isNull(preRegistrationEntityList)) {
					for (DemographicEntity preRegistrationEntity : preRegistrationEntityList) {
						String identityValue = serviceUtil.getValueFromIdentity(
								preRegistrationEntity.getApplicantDetailJson(), RequestCodes.FullName.toString());
						viewDto = new PreRegistrationViewDTO();
						viewDto.setPreId(preRegistrationEntity.getPreRegistrationId());
						viewDto.setFullname(identityValue);
						viewDto.setStatusCode(preRegistrationEntity.getStatusCode());

						BookingRegistrationDTO bookingRegistrationDTO = callGetAppointmentDetailsRestService(
								preRegistrationEntity.getPreRegistrationId());
						if (bookingRegistrationDTO != null) {
							viewDto.setBookingRegistrationDTO(bookingRegistrationDTO);
						}
						viewList.add(viewDto);
					}
					response.setResponse(viewList);
					response.setResTime(new Timestamp(System.currentTimeMillis()));
					response.setStatus(trueStatus);
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.NO_RECORD_FOUND_FOR_USER_ID.name());
				}
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		return response;
	}

	/**
	 * This Method is used to fetch status of particular preId
	 * 
	 * @param preId
	 * @return ResponseDto<StatusDto>
	 * 
	 * 
	 */
	public ResponseDTO<PreRegistartionStatusDTO> getApplicationStatus(String preRegId) {
		PreRegistartionStatusDTO statusdto = new PreRegistartionStatusDTO();
		ResponseDTO<PreRegistartionStatusDTO> response = new ResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preRegId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = preRegistrationRepository.findBypreRegistrationId(preRegId);
				if (demographicEntity != null) {
					statusdto.setPreRegistartionId(demographicEntity.getPreRegistrationId());
					statusdto.setStatusCode(demographicEntity.getStatusCode());
					statusList.add(statusdto);
					response.setResponse(statusList);
					response.setResTime(new Date(System.currentTimeMillis()));
					response.setStatus(trueStatus);
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.NO_RECORD_FOUND_FOR_USER_ID.name());
				}
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		return response;
	}

	/**
	 * This Method is used to delete the Individual Application and documents
	 * associated with it
	 * 
	 * @param preregId
	 * @return ResponseDto<DeleteDto>
	 */
	public ResponseDTO<DeletePreRegistartionDTO> deleteIndividual(String preregId) {
		ResponseDTO<DeletePreRegistartionDTO> response = new ResponseDTO<>();
		List<DeletePreRegistartionDTO> deleteList = new ArrayList<>();
		DeletePreRegistartionDTO deleteDto = new DeletePreRegistartionDTO();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preregId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity preRegistrationEntity = preRegistrationRepository.findBypreRegistrationId(preregId);
				if (!serviceUtil.isNull(preRegistrationEntity)) {
					if (serviceUtil.checkStatusForDeletion(preRegistrationEntity.getStatusCode())) {
						callDocumentServiceToDeleteAllByPreId(preregId);
						int isDeletedDemo = preRegistrationRepository.deleteByPreRegistrationId(preregId);
						if (isDeletedDemo > 0) {
							deleteDto.setPrId(preRegistrationEntity.getPreRegistrationId());
							deleteDto.setDeletedDateTime(new Date(System.currentTimeMillis()));
							deleteList.add(deleteDto);
						} else {
							throw new RecordFailedToDeleteException(ErrorCodes.PRG_PAM_APP_004.name(),
									ErrorMessages.FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD.name());
						}
					}
				} else {
					throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
							ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
				}
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResTime(new Date(System.currentTimeMillis()));
		response.setStatus("true");
		response.setResponse(deleteList);
		return response;
	}

	/**
	 * This Method is used to reterive the demographic
	 * 
	 * @param preregId
	 * @return ResponseDto<CreatePreRegistrationDTO>
	 */
	public ResponseDTO<CreatePreRegistrationDTO> getDemographicData(String preRegId) {
		List<CreatePreRegistrationDTO> createDtos = new ArrayList<>();
		ResponseDTO<CreatePreRegistrationDTO> response = new ResponseDTO<>();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preRegId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				DemographicEntity demographicEntity = preRegistrationRepository.findBypreRegistrationId(preRegId);
				CreatePreRegistrationDTO createDto = serviceUtil.setterForCreateDTO(demographicEntity);
				createDtos.add(createDto);
				response.setResponse(createDtos);
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResTime(new Date(System.currentTimeMillis()));
		response.setStatus("true");
		response.setErr(null);
		return response;
	}

	/**
	 * This Method is used to update status of particular preId
	 * 
	 * @param preId
	 * @param preId
	 * @return UpdateResponseDTO<String>
	 * 
	 * 
	 */
	public UpdateResponseDTO<String> updatePreRegistrationStatus(String preRegId, String status) {
		UpdateResponseDTO<String> response = new UpdateResponseDTO<>();
		DemographicEntity preRegistrationEntity = new DemographicEntity();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.preRegistrationId.toString(), preRegId);
			requestParamMap.put(RequestCodes.statusCode.toString(), status);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				preRegistrationEntity = preRegistrationRepository.findBypreRegistrationId(preRegId);
				preRegistrationEntity.setStatusCode(StatusCodes.valueOf(status).toString());
				preRegistrationRepository.update(preRegistrationEntity);
				response.setResponse("Status Updated sucessfully");
				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus("true");
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		return response;
	}

	/**
	 * This Method is used to reterive demographic data by date
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return UpdateResponseDTO<String>
	 * 
	 * 
	 */
	public ResponseDTO<String> getPreRegistrationByDate(String fromDate, String toDate) {
		ResponseDTO<String> response = new ResponseDTO<>();
		List<String> preIds = new ArrayList<>();
		Map<String, String> reqDateRange = new HashMap<>();
		try {
			reqDateRange.put("FromDate", fromDate);
			reqDateRange.put("ToDate", toDate);
			if (ValidationUtil.requstParamValidator(reqDateRange)) {
				Map<String, Timestamp> reqTimeStamp = serviceUtil.dateSetter(reqDateRange, "yyyy-MM-dd HH:mm:ss");
				List<DemographicEntity> details = preRegistrationRepository
						.findBycreateDateTimeBetween(reqTimeStamp.get("FromDate"), reqTimeStamp.get("ToDate"));
				for (DemographicEntity entity : details) {
					preIds.add(entity.getPreRegistrationId());
				}
				response.setResponse(preIds);
			}
		} catch (Exception ex) {
			new DemographicExceptionCatcher().handle(ex);
		}
		response.setResTime(new Date(System.currentTimeMillis()));
		response.setStatus("true");
		response.setErr(null);
		return response;
	}

	/**
	 * This private Method is used to reterive booking data by date
	 * 
	 * @param preId
	 * @return BookingRegistrationDTO
	 * 
	 */
	private BookingRegistrationDTO callGetAppointmentDetailsRestService(String preId) {
		RestTemplate restTemplate = restTemplateBuilder.build();
		BookingResponseDTO<?> resultDto = null;
		BookingRegistrationDTO bookingRegistrationDTO = null;
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(appointmentResourseUrl)
					.queryParam("preRegID", preId);
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<BookingRegistrationDTO> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			ResponseEntity<?> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					BookingResponseDTO.class);
			if (respEntity.getStatusCode() == HttpStatus.OK) {
				resultDto = (BookingResponseDTO<?>) respEntity.getBody();
				ObjectMapper mapper = new ObjectMapper();
				bookingRegistrationDTO = mapper.convertValue(resultDto.getResponse(), BookingRegistrationDTO.class);
			}
		} catch (RestClientException e) {
			return bookingRegistrationDTO;
		}

		return bookingRegistrationDTO;
	}

	/**
	 * This private Method is used to save and update demographic data
	 * 
	 * @param CreatePreRegistrationDTO
	 * @param requestId
	 * @return ResponseDTO<CreatePreRegistrationDTO>
	 * 
	 */
	private ResponseDTO<CreatePreRegistrationDTO> createOrUpdate(CreatePreRegistrationDTO demographicRequest,
			String requestId) {
		ResponseDTO<CreatePreRegistrationDTO> response = new ResponseDTO<>();
		List<CreatePreRegistrationDTO> saveList = new ArrayList<>();
		if (serviceUtil.isNull(demographicRequest.getPreRegistrationId())) {
			demographicRequest.setPreRegistrationId(pridGenerator.generateId());
			DemographicEntity demographicEntity = serviceUtil.prepareDemographicEntity(demographicRequest, requestId,
					"save");
			preRegistrationDao.save(demographicEntity);
		} else {
			DemographicEntity demographicEntity = preRegistrationRepository
					.findBypreRegistrationId(demographicRequest.getPreRegistrationId());
			if (!serviceUtil.isNull(demographicEntity)) {
				preRegistrationRepository.deleteByPreRegistrationId(demographicRequest.getPreRegistrationId());
				demographicEntity = serviceUtil.prepareDemographicEntity(demographicRequest, requestId, "update");
				preRegistrationDao.save(demographicEntity);
			} else {
				throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
						ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
			}
		}
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(trueStatus);
		saveList.add(demographicRequest);
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
	private boolean callDocumentServiceToDeleteAllByPreId(String preregId) {
		ResponseEntity<?> responseEntity = null;
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(resourceUrl + "pre-registration/deleteAllByPreRegId").queryParam("preId", preregId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<ResponseDTO<?>> httpEntity = new HttpEntity<>(headers);

			String strUriBuilder = uriBuilder.build().encode().toUriString();

			responseEntity = restTemplate.exchange(strUriBuilder, HttpMethod.DELETE, httpEntity, ResponseDTO.class);

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				return true;
			} else {
				throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_015.name(),
						ErrorMessages.DOCUMENT_FAILED_TO_DELETE.name());
			}
		} catch (RestClientException e) {
			throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_015.name(),
					ErrorMessages.DOCUMENT_FAILED_TO_DELETE.name());
		}
	}
}