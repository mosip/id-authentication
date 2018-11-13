package io.mosip.preregistration.application.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.jsonvalidator.dto.JsonValidatorResponseDto;
import io.mosip.kernel.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.jsonvalidator.validator.JsonValidator;
import io.mosip.preregistration.application.code.StateManagment;
import io.mosip.preregistration.application.code.StatusCodes;
import io.mosip.preregistration.application.dao.PreRegistrationDao;
import io.mosip.preregistration.application.dto.CreateDto;
import io.mosip.preregistration.application.dto.DeleteDto;
import io.mosip.preregistration.application.dto.ExceptionInfoDto;
import io.mosip.preregistration.application.dto.ExceptionJSONInfo;
import io.mosip.preregistration.application.dto.ResponseDto;
import io.mosip.preregistration.application.dto.ViewRegistrationResponseDto;
import io.mosip.preregistration.application.entity.PreRegistrationEntity;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.JsonValidationException;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.utils.PreRegistrationErrorMessages;

import io.mosip.preregistration.application.repository.PreRegistrationRepository;
import io.mosip.preregistration.core.exceptions.DatabaseOperationException;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;


/**
 * Registration service interface
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
	private JsonValidator jsonValidator;

	
	@Value("${resource.url}")
	 String resourceUrl;
	
	
	private RestTemplate restTemplate;

//	@Autowired
//	public void restTemplateBeanBuilder(RestTemplateBuilder restTemplateBuilder) {
//		this.restTemplate = restTemplateBuilder.build();
//	}
	@Autowired
	RestTemplateBuilder restTemplateBuilder;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.RegistrationService#addRegistration(java.lang.
	 * Object, java.lang.String)
	 */
	
	public ResponseDto addRegistration(JSONObject applicantDetailJson, String preid)  {
		JsonValidatorResponseDto dto = new JsonValidatorResponseDto();
		ResponseDto<CreateDto> response = new ResponseDto();
		List<CreateDto> saveList = new ArrayList<CreateDto>();
		CreateDto createDto = new CreateDto();
		String prid = preid;
		PreRegistrationEntity entity = new PreRegistrationEntity();
//		entity.setApplicantType("Adult");
//		entity.setGenderCode("Male");
		entity.setLangCode("12L");
//		entity.setLocationCode("9374y");
		entity.setStatusCode("SAVE");
		entity.setGroupId("1234567765444");
		entity.setCr_appuser_id("Rajath");
		entity.setCreatedBy("Rajath");
		entity.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
		
		String json = applicantDetailJson.toString();
	
			try {
				dto = jsonValidator.validateJson(applicantDetailJson.toJSONString(), "mosip-prereg-identity-json-schema.json");
				if (prid == null) {
					prid = pridGenerator.generateId();
					entity.setApplicantDetailJson(json.getBytes());
					entity.setPreRegistrationId(prid);
					preRegistrationDao.save(entity);
					createDto.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
					createDto.setCreatedBy("Rajath");
				} else {
					entity.setApplicantDetailJson(json.getBytes());
					entity.setPreRegistrationId(prid);
					preRegistrationDao.save(entity);
					createDto.setUpdateDateTime(new Timestamp(System.currentTimeMillis()));
					createDto.setUpdatedBy("Rajath");
				}
			} catch (HttpRequestException e) {
				// Log here
				throw new JsonValidationException("HttpRequest exception", e.getCause());
			} catch (JsonValidationProcessingException e) {
				// Log here
				throw new JsonValidationException("JsonValidationProcessing exception", e.getCause());
			} catch (JsonIOException e) {
				// Log here
				throw new JsonValidationException("JsonIO exception", e.getCause());
			} catch (JsonSchemaIOException e) {
				// Log here
				throw new JsonValidationException("JsonSchemaIO exception", e.getCause());
			} catch (FileIOException e) {
				// Log here
				throw new JsonValidationException("FileIO exception", e.getCause());
			}catch (DataAccessLayerException e) {
                //log here
				throw new TablenotAccessibleException(PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, e);

				}

		createDto.setPrId(prid);
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus("true");
		saveList.add(createDto);
		response.setResponse(saveList);
		return response;

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
	
	public List<ExceptionInfoDto> getApplicationDetails(String userId) throws TablenotAccessibleException {

		List<ViewRegistrationResponseDto> response = new ArrayList<>();
		ExceptionInfoDto exceptionInfoDto = new ExceptionInfoDto();

		// int minCreateDateIndex = 0;
		ViewRegistrationResponseDto responseDto;
		List<ExceptionJSONInfo> err = new ArrayList<>();
		List<ExceptionInfoDto> responseList = new ArrayList<>();

		List<PreRegistrationEntity> userDetails = new ArrayList<>();

		try {
			userDetails = preRegistrationRepository.findByuserId(userId);
		} catch (DataAccessLayerException e) {
			responseDto = new ViewRegistrationResponseDto();

			ExceptionJSONInfo TableNotFoundException = new ExceptionJSONInfo("",
					PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE);
			err.add(TableNotFoundException);
			exceptionInfoDto.setStatus(false);
			exceptionInfoDto.setErr(err);
			responseList.add(exceptionInfoDto);
			throw new TablenotAccessibleException(PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, e);

		}
		if (userDetails.equals(null) || userDetails.isEmpty()) {
			ExceptionJSONInfo userIdNotValidException = new ExceptionJSONInfo("", "Please enter valid user Id");
			err.add(userIdNotValidException);
			exceptionInfoDto.setStatus(false);
			exceptionInfoDto.setErr(err);
			responseList.add(exceptionInfoDto);
			return responseList;
		} else {

			for (int i = 0; i < userDetails.size(); i++)

			{
				responseDto = new ViewRegistrationResponseDto();
				responseDto.setPreId(userDetails.get(i).getPreRegistrationId());
//				responseDto.setFirstname(userDetails.get(i).getFirstname());
				responseDto.setStatus_code(userDetails.get(i).getStatusCode());
				response.add(responseDto);

			}

			exceptionInfoDto.setResponse(response);
			exceptionInfoDto.setStatus(true);
			responseList.add(exceptionInfoDto);
		}

		return responseList;

	}

	/**
	 * This Method is used to fetch status of particular groupId
	 * 
	 * @param groupId
	 * @return Map which will contain all PreRegistraton Ids in the group and status
	 * 
	 * 
	 */
	
	public Map<String, String> getApplicationStatus(String groupId) throws TablenotAccessibleException {

		List<PreRegistrationEntity> details = new ArrayList<>();
		try {
			details = preRegistrationRepository.findBygroupId(groupId);
		} catch (DataAccessLayerException e) {

			throw new TablenotAccessibleException(PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, e);
		}
		Map<String, String> response = details.stream().collect(
				Collectors.toMap(PreRegistrationEntity::getPreRegistrationId, PreRegistrationEntity::getStatusCode));

		return response;
	}

	/**
	 * This Method is used to delete the Individual Application and documents
	 * associated with it
	 * 
	 * @param groupId
	 * @param list
	 *            of preRegistrationIds
	 * 
	 * 
	 */
	
	public ResponseDto deleteIndividual(String preregId) {
		restTemplate = restTemplateBuilder.build();
		System.out.println("URL "+resourceUrl);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(resourceUrl)
				.queryParam("preId", preregId);

		Map<String, StatusCodes> responseMap = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<ResponseDto> entity = new HttpEntity<ResponseDto>(headers);

		ResponseDto<DeleteDto> response = new ResponseDto();
		List<DeleteDto> saveList = new ArrayList<DeleteDto>();
		DeleteDto deleteDto = new DeleteDto();
		System.out.println("builder "+builder.build().encode().toUriString());
		String uriBuilder=builder.build().encode().toUriString();

		try {
			PreRegistrationEntity applicant = preRegistrationRepository.findBypreRegistrationId(preregId);
//			if(applicant==null) {
//				throw new RecordNotFoundException(StatusCodes.RECORD_NOT_FOUND_EXCEPTION.toString());
//			}else {
				if (applicant.getStatusCode().equalsIgnoreCase(StateManagment.Pending_Appointmemt.name())
						|| applicant.getStatusCode().equalsIgnoreCase(StateManagment.Booked.name())) {
					System.out.println("rtservice"+restTemplate);
				
					ResponseEntity<ResponseDto> responseEntity = restTemplate.exchange(uriBuilder,
							HttpMethod.DELETE, entity, ResponseDto.class);
					System.out.println("responseEntity"+responseEntity);
					  if (responseEntity.getStatusCode() == HttpStatus.OK) 
					  {
						  responseMap.put("ok", StatusCodes.DOCUMENT_DELETE_SUCCESSFUL);
						  preRegistrationRepository.deleteByPreRegistrationId(preregId);
						  }else {
								throw new DocumentFailedToDeleteException(
										StatusCodes.DOCUMENT_FAILED_TO_DELETE.toString());
					 
						  }
					  deleteDto.setPrId(applicant.getPreRegistrationId());
					  deleteDto.setDeletedDateTime(new Timestamp(System.currentTimeMillis()));
					
					saveList.add(deleteDto);
					
				} else {
					throw new OperationNotAllowedException(
							PreRegistrationErrorMessages.DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT);
				}
			
			//}
			
			// }
		} catch (DataAccessLayerException e) {
			throw new DatabaseOperationException("Failed to delete the appliation", e);
		}
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus("true");
		response.setResponse(saveList);
		
	
		return response;
	}
}