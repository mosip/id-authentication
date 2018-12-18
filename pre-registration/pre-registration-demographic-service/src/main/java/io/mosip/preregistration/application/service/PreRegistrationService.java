package io.mosip.preregistration.application.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.preregistration.application.code.RequestCodes;
import io.mosip.preregistration.application.code.StatusCodes;
import io.mosip.preregistration.application.dao.PreRegistrationDao;
import io.mosip.preregistration.application.dto.BookingRegistrationDTO;
import io.mosip.preregistration.application.dto.BookingResponseDTO;
import io.mosip.preregistration.application.dto.CreatePreRegistrationDTO;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.dto.ResponseDTO;
import io.mosip.preregistration.application.dto.UpdateResponseDTO;
import io.mosip.preregistration.application.entity.PreRegistrationEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.system.DateParseException;
import io.mosip.preregistration.application.exception.system.JsonParseException;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.exception.system.SystemFileIOException;
import io.mosip.preregistration.application.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
import io.mosip.preregistration.application.repository.PreRegistrationRepository;
import io.mosip.preregistration.core.exceptions.InvalidRequestParameterException;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * Pre-Registration service
 * 
 * @author M1037717
 *
 */
@Service
public class PreRegistrationService {

	/**
	 * Field for {@link #RegistrationDao}
	 */
	@Autowired
	private PreRegistrationDao preRegistrationDao;

	/**
	 * Field for {@link #MosipPridGenerator<String>}
	 */
	@Autowired
	private PridGenerator<String> pridGenerator;

	/**
	 * Field for {@link #RegistrationRepositary}
	 */
	@Autowired
	private PreRegistrationRepository preRegistrationRepository;

	@Autowired
	private JsonValidatorImpl jsonValidator;

	@Value("${resource.url}")
	private String resourceUrl;

	@Value("${id}")
	private String id;

	@Value("${ver}")
	private String ver;

	private JSONParser jsonParser = new JSONParser();

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
	public ResponseDTO<CreatePreRegistrationDTO> addPreRegistration(String jsonString) {

		CreatePreRegistrationDTO createDto = new CreatePreRegistrationDTO();
		String prid = null;
		PreRegistrationEntity preRegistrationEntity = null;
		Map<String, String> inputValidation = new HashMap<>();
		JSONObject applicantDetailJson;
		try {
			applicantDetailJson = (JSONObject) jsonParser.parse(jsonString);
			JSONObject reqObject = (JSONObject) applicantDetailJson.get(RequestCodes.request.toString());
			JSONObject demoObj = (JSONObject) reqObject.get(RequestCodes.demographicDetails.toString());
			inputValidation = checkInputValidation(applicantDetailJson);

			InvalidRequestParameterException requestParameterexception = ValidationUtil
					.requestValidator(inputValidation, requiredRequestMap);
			if (requestParameterexception == null) {

				jsonValidator.validateJson(demoObj.toString(), "mosip-prereg-identity-json-schema.json");

				preRegistrationEntity = new PreRegistrationEntity();
				prid = (String) reqObject.get(RequestCodes.preRegistrationId.toString());
				String json = applicantDetailJson.toString();
				preparePreIdEntity(preRegistrationEntity, applicantDetailJson, reqObject);
				createDto.setDemographicDetails(demoObj);

				if (nullChecker(prid)) {
					if (!nullChecker(preRegistrationEntity.getCreatedBy())) {
						prid = pridGenerator.generateId();
						preRegistrationDao
								.save(createPreregistrationEntity(prid, preRegistrationEntity, reqObject, json));
						createDto.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
						createDto.setCreatedBy((String) (reqObject.get(RequestCodes.createdBy.toString())));
					} else {
						throw new OperationNotAllowedException(ErrorCodes.PRG_PAM_APP_010.toString(),
								ErrorMessages.UNABLE_TO_CREATE_THE_PRE_REGISTRATION.toString());
					}
				} else {
					preRegistrationEntity = preRegistrationRepository.findBypreRegistrationId(prid);
					if (!nullChecker(preRegistrationEntity)) {
						preRegistrationEntity.setUpdatedBy((String) (reqObject.get(RequestCodes.updatedBy.toString())));
						preRegistrationRepository.deleteByPreRegistrationId(prid);
						preRegistrationDao
								.save(updatePreregistrationEntity(prid, preRegistrationEntity, reqObject, json));
						setterForCreateDTO(createDto, preRegistrationEntity, reqObject);
					} else {
						throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
								ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
					}
				}
			} else {
				throw requestParameterexception;
			}
		} catch (HttpRequestException e) {
			throw new JsonValidationException(ErrorCodes.PRG_PAM_APP_007.name(),
					ErrorMessages.JSON_HTTP_REQUEST_EXCEPTION.name(), e.getCause());
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_PAM_APP_002.toString(),
					ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
		} catch (JsonValidationProcessingException e) {
			throw new JsonValidationException(ErrorCodes.PRG_PAM_APP_007.name(),
					ErrorMessages.JSON_VALIDATION_PROCESSING_EXCEPTION.name(), e.getCause());
		} catch (JsonIOException e) {
			throw new JsonValidationException(ErrorCodes.PRG_PAM_APP_007.name(), ErrorMessages.JSON_IO_EXCEPTION.name(),
					e.getCause());
		} catch (JsonSchemaIOException e) {
			throw new JsonValidationException(ErrorCodes.PRG_PAM_APP_007.name(),
					ErrorMessages.JSON_SCHEMA_IO_EXCEPTION.name(), e.getCause());
		} catch (FileIOException e) {
			throw new SystemFileIOException(ErrorCodes.PRG_PAM_APP_009.name(), ErrorMessages.FILE_IO_EXCEPTION.name(),
					e.getCause());
		} catch (ParseException e) {
			throw new JsonParseException(ErrorCodes.PRG_PAM_APP_007.toString(),
					ErrorMessages.JSON_PARSING_FAILED.toString(), e.getCause());
		}

		createDto.setPrId(prid);
		ResponseDTO<CreatePreRegistrationDTO> response = new ResponseDTO<>();
		List<CreatePreRegistrationDTO> saveList = new ArrayList<>();
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(trueStatus);
		saveList.add(createDto);
		response.setResponse(saveList);

		return response;

	}

	private void setterForCreateDTO(CreatePreRegistrationDTO createDto, PreRegistrationEntity preRegistrationEntity,
			JSONObject reqObject) {
		createDto.setCreatedBy(preRegistrationEntity.getCreatedBy());
		createDto.setCreateDateTime(preRegistrationEntity.getCreateDateTime());
		createDto.setUpdateDateTime(new Timestamp(System.currentTimeMillis()));
		createDto.setUpdatedBy((String) (reqObject.get(RequestCodes.updatedBy.toString())));
	}

	private PreRegistrationEntity updatePreregistrationEntity(String prid, PreRegistrationEntity preRegistrationEntity,
			JSONObject reqObject, String json) {
		preRegistrationEntity.setStatusCode((String) (reqObject.get(RequestCodes.statusCode.toString())));
		preRegistrationEntity.setUpdatedBy((String) (reqObject.get(RequestCodes.updatedBy.toString())));
		preRegistrationEntity.setCreateDateTime(preRegistrationEntity.getCreateDateTime());
		preRegistrationEntity.setDeletedDateTime(new Timestamp(System.currentTimeMillis()));
		preRegistrationEntity.setUpdateDateTime(new Timestamp(System.currentTimeMillis()));
		preRegistrationEntity.setApplicantDetailJson(json.getBytes(StandardCharsets.UTF_8));
		preRegistrationEntity.setPreRegistrationId(prid);
		return preRegistrationEntity;
	}

	private PreRegistrationEntity createPreregistrationEntity(String prid, PreRegistrationEntity preRegistrationEntity,
			JSONObject reqObject, String json) {
		preRegistrationEntity.setStatusCode((String) (reqObject.get(RequestCodes.statusCode.toString())));
		preRegistrationEntity.setCreatedBy((String) (reqObject.get(RequestCodes.createdBy.toString())));
		preRegistrationEntity.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
		preRegistrationEntity.setApplicantDetailJson(json.getBytes(StandardCharsets.UTF_8));
		preRegistrationEntity.setPreRegistrationId(prid);
		return preRegistrationEntity;
	}

	private Map<String, String> checkInputValidation(JSONObject applicantDetailJson) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.toString(), applicantDetailJson.get(RequestCodes.id.toString()).toString());
		inputValidation.put(RequestCodes.ver.toString(),
				applicantDetailJson.get(RequestCodes.ver.toString()).toString());
		inputValidation.put(RequestCodes.reqTime.toString(),
				applicantDetailJson.get(RequestCodes.reqTime.toString()).toString());
		inputValidation.put(RequestCodes.request.toString(),
				applicantDetailJson.get(RequestCodes.request.toString()).toString());
		return inputValidation;
	}

	private void preparePreIdEntity(PreRegistrationEntity preRegistrationEntity, JSONObject applicantDetailJson,
			JSONObject reqObject) {
		preRegistrationEntity.setLangCode((String) (reqObject.get(RequestCodes.langCode.toString())));
		preRegistrationEntity.setGroupId("1234567890");
		preRegistrationEntity.setCr_appuser_id((String) (applicantDetailJson.get(RequestCodes.id.toString())));
		preRegistrationEntity.setCreatedBy((String) (reqObject.get(RequestCodes.createdBy.toString())));
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
		List<PreRegistrationViewDTO> viewList = new ArrayList<>();
		ResponseDTO<PreRegistrationViewDTO> response = new ResponseDTO<>();
		List<PreRegistrationEntity> preRegistrationEntityList = null;
		JSONObject jsonObj = null;
		PreRegistrationViewDTO viewDto = null;
		try {
			preRegistrationEntityList = preRegistrationRepository.findByuserId(userId);
			if (preRegistrationEntityList == null || preRegistrationEntityList.isEmpty()) {
				throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
						ErrorMessages.NO_RECORD_FOUND_FOR_USER_ID.name());
			} else {
				for (PreRegistrationEntity preRegistrationEntity : preRegistrationEntityList) {
					jsonObj = (JSONObject) jsonParser
							.parse(new String(preRegistrationEntity.getApplicantDetailJson(), StandardCharsets.UTF_8));
					JSONObject reqObj = (JSONObject) jsonObj.get(RequestCodes.request.toString());
					JSONObject demoObj = (JSONObject) reqObj.get(RequestCodes.demographicDetails.toString());
					JSONObject identityObj = (JSONObject) demoObj.get(RequestCodes.identity.toString());
					JSONArray nameArr = (JSONArray) identityObj.get(RequestCodes.FullName.toString());
					JSONObject nameObj = (JSONObject) nameArr.get(0);

					viewDto = new PreRegistrationViewDTO();
					viewDto.setPreId(preRegistrationEntity.getPreRegistrationId());
					viewDto.setFullname(nameObj.get(RequestCodes.value.toString()).toString());
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
			}
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_PAM_APP_002.toString(),
					ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
		} catch (ParseException e) {
			throw new JsonParseException(ErrorCodes.PRG_PAM_APP_007.toString(),
					ErrorMessages.JSON_PARSING_FAILED.toString(), e.getCause());
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
	public ResponseDTO<PreRegistartionStatusDTO> getApplicationStatus(String preId) {
		PreRegistartionStatusDTO statusdto = new PreRegistartionStatusDTO();
		ResponseDTO<PreRegistartionStatusDTO> response = new ResponseDTO<>();
		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		PreRegistrationEntity preRegistrationEntity = new PreRegistrationEntity();
		try {
			preRegistrationEntity = preRegistrationRepository.findBypreRegistrationId(preId);
			if (preRegistrationEntity != null) {
				statusdto.setPreRegistartionId(preRegistrationEntity.getPreRegistrationId());
				statusdto.setStatusCode(preRegistrationEntity.getStatusCode());
				statusList.add(statusdto);
				response.setResponse(statusList);
				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus(trueStatus);
			} else {
				throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
						ErrorMessages.NO_RECORD_FOUND_FOR_USER_ID.name());
			}

		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_PAM_APP_002.toString(),
					ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
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
		String strUriBuilder = null;

		HttpEntity<ResponseDTO<?>> httpEntity = null;
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(resourceUrl + "pre-registration/deleteAllByPreRegId").queryParam("preId", preregId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			httpEntity = new HttpEntity<>(headers);

			strUriBuilder = uriBuilder.build().encode().toUriString();

			PreRegistrationEntity preRegistrationEntity = preRegistrationRepository.findBypreRegistrationId(preregId);

			if (!nullChecker(preRegistrationEntity)) {
				String preRegStatusCode = preRegistrationEntity.getStatusCode();
				if (preRegStatusCode.equals(StatusCodes.Pending_Appointment.name())
						|| preRegStatusCode.equals(StatusCodes.Booked.name())) {
					ResponseEntity<?> responseEntity = restTemplate.exchange(strUriBuilder, HttpMethod.DELETE,
							httpEntity, ResponseDTO.class);
					if (responseEntity.getStatusCode() == HttpStatus.OK) {
						int isDeleted = preRegistrationRepository.deleteByPreRegistrationId(preregId);
						if (isDeleted <= 0) {
							throw new RecordFailedToDeleteException(ErrorCodes.PRG_PAM_APP_004.name(),
									ErrorMessages.FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD.name());
						} else {
							deleteDto.setPrId(preRegistrationEntity.getPreRegistrationId());
							deleteDto.setDeletedDateTime(new Timestamp(System.currentTimeMillis()));
							deleteList.add(deleteDto);
						}
					} else {
						throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_015.name(),
								ErrorMessages.DOCUMENT_FAILED_TO_DELETE.name());
					}
				} else {
					throw new OperationNotAllowedException(ErrorCodes.PRG_PAM_APP_003.name(),
							ErrorMessages.DELETE_OPERATION_NOT_ALLOWED.name());
				}
			} else {
				throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
						ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
			}
		} catch (DataAccessLayerException e) {
			throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
					ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
		} catch (RestClientException e) {
			throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_015.name(),
					ErrorMessages.DOCUMENT_FAILED_TO_DELETE.name());
		}
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus("true");
		response.setResponse(deleteList);

		return response;
	}

	public ResponseDTO<CreatePreRegistrationDTO> getPreRegistration(String preRegId) {
		List<CreatePreRegistrationDTO> createDtos = new ArrayList<>();
		CreatePreRegistrationDTO createDto = new CreatePreRegistrationDTO();
		ResponseDTO<CreatePreRegistrationDTO> response = new ResponseDTO<>();
		PreRegistrationEntity details = new PreRegistrationEntity();
		JSONParser parser = new JSONParser();
		JSONObject applicantDetailJson;
		try {
			details = preRegistrationRepository.findBypreRegistrationId(preRegId);
			createDto.setPrId(details.getPreRegistrationId());
			createDto.setCreatedBy(details.getCreatedBy());
			createDto.setCreateDateTime(details.getCreateDateTime());
			createDto.setUpdatedBy(details.getUpdatedBy());
			createDto.setUpdateDateTime(details.getUpdateDateTime());

			applicantDetailJson = (JSONObject) parser
					.parse(new String(details.getApplicantDetailJson(), StandardCharsets.UTF_8));
			JSONObject reqObject = (JSONObject) applicantDetailJson.get(RequestCodes.request.toString());
			JSONObject demoObj = (JSONObject) reqObject.get(RequestCodes.demographicDetails.toString());

			createDto.setDemographicDetails(demoObj);
			createDtos.add(createDto);
			response.setResponse(createDtos);
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_PAM_APP_002.toString(),
					ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
		} catch (ParseException e) {
			throw new JsonParseException(ErrorCodes.PRG_PAM_APP_007.toString(),
					ErrorMessages.JSON_PARSING_FAILED.toString(), e.getCause());
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

	public UpdateResponseDTO<String> updatePreRegistrationStatus(String preId, String status) {

		UpdateResponseDTO<String> response = new UpdateResponseDTO<>();

		PreRegistrationEntity preRegistrationEntity = new PreRegistrationEntity();
		try {
			preRegistrationEntity = preRegistrationRepository.findBypreRegistrationId(preId);
			preRegistrationEntity.setStatusCode(StatusCodes.valueOf(status).toString());

			preRegistrationRepository.update(preRegistrationEntity);

			response.setResponse("Status Updated sucessfully");
			response.setResTime(new Timestamp(System.currentTimeMillis()));
			response.setStatus("true");
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_PAM_APP_002.toString(),
					ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());
		} catch (IllegalArgumentException e) {
			throw new SystemIllegalArgumentException(ErrorCodes.PRG_PAM_APP_006.toString(),
					ErrorMessages.INVAILD_STATUS_CODE.toString(), e);
		}

		return response;
	}

	public BookingRegistrationDTO callGetAppointmentDetailsRestService(String preId) {
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

	public ResponseDTO<String> getPreRegistrationByDate(String fromDate, String toDate) {
		ResponseDTO<String> response = new ResponseDTO<>();
		List<PreRegistrationEntity> details = new ArrayList<>();
		List<String> preIds = new ArrayList<>();
		try {
			String dateFormat = "yyyy-MM-dd HH:mm:ss";
			Date myFromDate = DateUtils.parse(URLDecoder.decode(fromDate, "UTF-8"), dateFormat);
			Date myToDate = null;
			if (toDate == null) {
				myToDate = myFromDate;
				Calendar cal = Calendar.getInstance();
				cal.setTime(myToDate);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				myToDate = cal.getTime();
			} else {
				myToDate = DateUtils.parse(URLDecoder.decode(toDate, "UTF-8"), dateFormat);
			}

			details = preRegistrationRepository.findBycreateDateTimeBetween(new Timestamp(myFromDate.getTime()),
					new Timestamp(myToDate.getTime()));

			for (PreRegistrationEntity entity : details) {
				preIds.add(entity.getPreRegistrationId());
			}
			response.setResponse(preIds);
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_PAM_APP_002.toString(),
					ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
		} catch (java.text.ParseException e) {
			throw new DateParseException(ErrorCodes.PRG_PAM_APP_011.toString(),
					ErrorMessages.UNSUPPORTED_DATE_FORMAT.toString(), e.getCause());
		} catch (UnsupportedEncodingException e) {
			throw new SystemUnsupportedEncodingException(ErrorCodes.PRG_PAM_APP_009.toString(),
					ErrorMessages.UNSUPPORTED_ENCODING_CHARSET.toString(), e.getCause());
		}
		response.setResTime(new Date(System.currentTimeMillis()));
		response.setStatus("true");
		response.setErr(null);
		return response;
	}

	public boolean nullChecker(Object key) {
		if (key instanceof String) {
			if (key.equals(""))
				return true;
		} else {
			if (key == null)
				return true;
		}
		return false;

	}
}