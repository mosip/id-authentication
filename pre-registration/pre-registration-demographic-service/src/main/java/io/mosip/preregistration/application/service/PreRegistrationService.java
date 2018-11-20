<<<<<<< HEAD
package io.mosip.preregistration.application.service;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
 * @author Sanober Noor
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

	// @Autowired
	// public void restTemplateBeanBuilder(RestTemplateBuilder restTemplateBuilder)
	// {
	// this.restTemplate = restTemplateBuilder.build();
	// }
	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.RegistrationService#addRegistration(java.lang.
	 * Object, java.lang.String)
	 */

	public ResponseDto addRegistration(String jsonObject) {


		JsonValidatorResponseDto dto = new JsonValidatorResponseDto();

		ResponseDto<CreateDto> response = new ResponseDto();

		List<CreateDto> saveList = new ArrayList<CreateDto>();
		CreateDto createDto = new CreateDto();
		String prid = null;
		PreRegistrationEntity entity = new PreRegistrationEntity();

		try {
			JSONParser parser= new JSONParser();
			
			JSONObject applicantDetailJson;
			
			applicantDetailJson = (JSONObject) parser.parse(jsonObject);
			 

			JSONObject object = (JSONObject) applicantDetailJson.get("request");
			
			JSONObject obj = (JSONObject) object.get("demographicDetails");
			
			
			
			
			 dto = jsonValidator.validateJson(obj.toString(),
					 "mosip-prereg-identity-json-schema.json");

			prid = (String) object.get("preRegistrationId");
			String json = applicantDetailJson.toString();
			entity.setLangCode((String) (object.get("langCode")));
			entity.setGroupId("1234567890");
			entity.setCr_appuser_id((String) (applicantDetailJson.get("id")));
			entity.setCreatedBy((String) (object.get("createdBy")));

			if (prid == null || prid.equals("")) {
				prid = pridGenerator.generateId();

				entity.setStatusCode((String) (object.get("statusCode")));
				entity.setCreatedBy((String) (object.get("createdBy")));
				entity.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
				entity.setApplicantDetailJson(json.getBytes("UTF-8"));
				entity.setPreRegistrationId(prid);
				preRegistrationDao.save(entity);
				createDto.setCreateDateTime(new Timestamp(System.currentTimeMillis()));

				createDto.setCreatedBy((String) (object.get("createdBy")));
			} else {
				PreRegistrationEntity entityDetail = preRegistrationRepository.findById(PreRegistrationEntity.class,
						prid);
				Timestamp crTime=null;
				if(entityDetail!=null)
					crTime= entityDetail.getCreateDateTime();
				else throw new RecordNotFoundException(PreRegistrationErrorMessages.RECORD_NOT_FOUND);
				preRegistrationRepository.deleteByPreRegistrationId(prid);
				entity.setStatusCode((String) (object.get("statusCode")));
				entity.setUpdatedBy((String) (object.get("updatedBy")));
				entity.setCreateDateTime(crTime);
				entity.setDeletedDateTime(new Timestamp(System.currentTimeMillis()));
				entity.setUpdateDateTime(new Timestamp(System.currentTimeMillis()));
				entity.setApplicantDetailJson(json.getBytes("UTF-8"));
				entity.setPreRegistrationId(prid);
				
				preRegistrationDao.save(entity);
				
				createDto.setUpdateDateTime(new Timestamp(System.currentTimeMillis()));
				createDto.setUpdatedBy((String) (object.get("updatedBy")));

			}
		}
		catch (HttpRequestException e) {
			// Log here
			throw new JsonValidationException("HttpRequest exception", e.getCause());
		} catch (DataAccessLayerException e) {
			// log here
			throw new TablenotAccessibleException(PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, e.getCause());
		}

		catch (JsonValidationProcessingException e) {
			
			throw new JsonValidationException("Json validation processing exception",e.getCause());
		} catch (JsonIOException e) {
			
			throw new JsonValidationException("Json IO exception",e.getCause());
		} catch (JsonSchemaIOException e) {
			
			throw new JsonValidationException("Json schema IO exception",e.getCause());
			
		} catch (FileIOException e) {
			
			throw new JsonValidationException("File IO exception",e.getCause());
			
		} catch (UnsupportedEncodingException e) {
			
			throw new JsonValidationException("Unsupported encoding exception",e.getCause());
			
		} catch (ParseException e) {
			
			throw new JsonValidationException("Parse exception",e.getCause());
		}

		createDto.setPrId(prid);
		createDto.setJson(jsonObject);
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
				// responseDto.setFirstname(userDetails.get(i).getFirstname());
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
		System.out.println("URL " + resourceUrl);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(resourceUrl).queryParam("preId", preregId);

		Map<String, StatusCodes> responseMap = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<ResponseDto> entity = new HttpEntity<ResponseDto>(headers);

		ResponseDto<DeleteDto> response = new ResponseDto();
		List<DeleteDto> saveList = new ArrayList<DeleteDto>();
		DeleteDto deleteDto = new DeleteDto();
		System.out.println("builder " + builder.build().encode().toUriString());
		String uriBuilder = builder.build().encode().toUriString();

		try {
			PreRegistrationEntity applicant = preRegistrationRepository.findBypreRegistrationId(preregId);
			// if(applicant==null) {
			// throw new
			// RecordNotFoundException(StatusCodes.RECORD_NOT_FOUND_EXCEPTION.toString());
			// }else {
			if (applicant.getStatusCode().equalsIgnoreCase(StateManagment.Pending_Appointmemt.name())
					|| applicant.getStatusCode().equalsIgnoreCase(StateManagment.Booked.name())) {
				System.out.println("rtservice" + restTemplate);

				ResponseEntity<ResponseDto> responseEntity = restTemplate.exchange(uriBuilder, HttpMethod.DELETE,
						entity, ResponseDto.class);
				System.out.println("responseEntity" + responseEntity);
				if (responseEntity.getStatusCode() == HttpStatus.OK) {
					responseMap.put("ok", StatusCodes.DOCUMENT_DELETE_SUCCESSFUL);
					preRegistrationRepository.deleteByPreRegistrationId(preregId);
				} else {
					throw new DocumentFailedToDeleteException(StatusCodes.DOCUMENT_FAILED_TO_DELETE.toString());

				}
				deleteDto.setPrId(applicant.getPreRegistrationId());
				deleteDto.setDeletedDateTime(new Timestamp(System.currentTimeMillis()));

				saveList.add(deleteDto);

			} else {
				throw new OperationNotAllowedException(
						PreRegistrationErrorMessages.DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT);
			}

			// }

			// }
		} catch (DataAccessLayerException e) {
			throw new DatabaseOperationException("Failed to delete the appliation", e);
		}
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus("true");
		response.setResponse(saveList);

		return response;
	}
=======
package io.mosip.preregistration.application.service;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
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
import io.mosip.preregistration.application.dto.StatusDto;
import io.mosip.preregistration.application.dto.ViewDto;
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


	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.RegistrationService#addRegistration(java.lang.
	 * Object, java.lang.String)
	 */

	@SuppressWarnings("rawtypes")
	public ResponseDto<CreateDto> addRegistration(String jsonObject) {

		ResponseDto<CreateDto> response = new ResponseDto<>();
		List<CreateDto> saveList = new ArrayList<>();
		CreateDto createDto = new CreateDto();
		String prid = null;
		PreRegistrationEntity entity = new PreRegistrationEntity();

		try {
			JSONParser parser= new JSONParser();
			
			JSONObject applicantDetailJson;
			
			applicantDetailJson = (JSONObject) parser.parse(jsonObject);
			 

			JSONObject reqObject = (JSONObject) applicantDetailJson.get("request");
			
			JSONObject demoObj = (JSONObject) reqObject.get("demographicDetails");
			
		    jsonValidator.validateJson(demoObj.toString(),"mosip-prereg-identity-json-schema.json");

			prid = (String) reqObject.get("preRegistrationId");
			String json = applicantDetailJson.toString();
			entity.setLangCode((String) (reqObject.get("langCode")));
			entity.setGroupId("1234567890");
			entity.setCr_appuser_id((String) (applicantDetailJson.get("id")));
			entity.setCreatedBy((String) (reqObject.get("createdBy")));


			if (prid == null || prid.equals("")) {
				prid = pridGenerator.generateId();

				entity.setStatusCode((String) (reqObject.get("statusCode")));
				entity.setCreatedBy((String) (reqObject.get("createdBy")));
				entity.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
				entity.setApplicantDetailJson(json.getBytes("UTF-8"));
				entity.setPreRegistrationId(prid);
				preRegistrationDao.save(entity);
				createDto.setCreateDateTime(new Timestamp(System.currentTimeMillis()));

				createDto.setCreatedBy((String) (reqObject.get("createdBy")));
			} else {
				PreRegistrationEntity createTime = preRegistrationRepository.findById(PreRegistrationEntity.class,
						prid);
				Timestamp crTime= createTime.getCreateDateTime();
				
				preRegistrationRepository.deleteByPreRegistrationId(prid);
				entity.setStatusCode((String) (reqObject.get("statusCode")));
				entity.setUpdatedBy((String) (reqObject.get("updatedBy")));
				entity.setCreateDateTime(crTime);
				entity.setDeletedDateTime(new Timestamp(System.currentTimeMillis()));
				entity.setUpdateDateTime(new Timestamp(System.currentTimeMillis()));
				entity.setApplicantDetailJson(json.getBytes("UTF-8"));
				entity.setPreRegistrationId(prid);
				
				preRegistrationDao.save(entity);
				
				createDto.setUpdateDateTime(new Timestamp(System.currentTimeMillis()));
				createDto.setUpdatedBy((String) (reqObject.get("updatedBy")));

			}
		}
		catch (HttpRequestException e) {
			// Log here
			throw new JsonValidationException("HttpRequest exception", e.getCause());
		} catch (DataAccessLayerException e) {
			// log here
			throw new TablenotAccessibleException(PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, e.getCause());
		}

		catch (JsonValidationProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonSchemaIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		createDto.setPrId(prid);
		createDto.setJson(jsonObject);
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

	public ResponseDto<ViewDto> getApplicationDetails(String userId) throws TablenotAccessibleException {

		List<ViewDto> viewList = new ArrayList<>();
		ResponseDto<ViewDto> response = new ResponseDto<>();

		List<PreRegistrationEntity> userDetails = new ArrayList<>();

		try {
			userDetails = preRegistrationRepository.findByuserId(userId);
		} catch (DataAccessLayerException e) {

			throw new TablenotAccessibleException(PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, e);

		}
		if (userDetails.equals(null) || userDetails.isEmpty()) {
			List<ExceptionJSONInfo> exceptionJson= new ArrayList<>();
			ExceptionJSONInfo userIdNotValidException = new ExceptionJSONInfo("", "Please enter valid user Id");
		   exceptionJson.add(userIdNotValidException);
		   response.setErr(exceptionJson);
		   response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus("false");
		
		} else {

			for (int i = 0; i < userDetails.size(); i++)

			{
				String applicationJson;
				try {
					applicationJson = new String(userDetails.get(0).getApplicantDetailJson(),"UTF-8");
					System.out.println("Json "+applicationJson);
					org.json.JSONObject jsonObj;
					jsonObj = new org.json.JSONObject(applicationJson);
					org.json.JSONObject reqObj= (org.json.JSONObject) jsonObj.get("request");
					org.json.JSONObject demoObj= (org.json.JSONObject) reqObj.get("demographicDetails");
					org.json.JSONObject identityObj= (org.json.JSONObject) demoObj.get("identity");
					org.json.JSONArray nameArr=identityObj.getJSONArray("FullName");
					org.json.JSONObject nameObj= (org.json.JSONObject) nameArr.get(0);
					
					ViewDto viewDto = new ViewDto();
					viewDto.setPreId(userDetails.get(i).getPreRegistrationId());
					viewDto.setFirstname(nameObj.get("value").toString());
					viewDto.setStatus_code(userDetails.get(i).getStatusCode());
					viewList.add(viewDto);
					response.setResponse(viewList);
					response.setResTime(new Timestamp(System.currentTimeMillis()));
					response.setStatus("true");
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
				
				
				

			}

			
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

	public ResponseDto<StatusDto> getApplicationStatus(String preId) throws TablenotAccessibleException {

		StatusDto statusdto = new StatusDto();
		ResponseDto<StatusDto> response = new ResponseDto<>();

		List<StatusDto> statusList = new ArrayList<>();
		PreRegistrationEntity details = new PreRegistrationEntity();
		try {
			details = preRegistrationRepository.findBypreRegistrationId(preId);
			statusdto.setPreRegistartionId(details.getPreRegistrationId());
			statusdto.setStatusCode(details.getStatusCode());
			statusList.add(statusdto);
			response.setResponse(statusList);
			response.setResTime(new Timestamp(System.currentTimeMillis()));
			response.setStatus("true");
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(PreRegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, e);
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
	@SuppressWarnings("rawtypes")
	public ResponseDto<DeleteDto> deleteIndividual(String preregId) {
		restTemplate = restTemplateBuilder.build();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(resourceUrl+"pre-registration/deleteAllByPreRegId").queryParam("preId", preregId);
           

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	
		HttpEntity<ResponseDto> entity = new HttpEntity<>(headers);

		ResponseDto<DeleteDto> response = new ResponseDto<>();
		List<DeleteDto> saveList = new ArrayList<>();
		DeleteDto deleteDto = new DeleteDto();
		String uriBuilder = builder.build().encode().toUriString();

		try {
			PreRegistrationEntity applicant = preRegistrationRepository.findBypreRegistrationId(preregId);
			if (applicant == null) {
				throw new RecordNotFoundException(StatusCodes.RECORD_NOT_FOUND_EXCEPTION.toString());
			} else {
				if (applicant.getStatusCode().equalsIgnoreCase(StateManagment.Pending_Appointment.name())
						|| applicant.getStatusCode().equalsIgnoreCase(StateManagment.Booked.name())) {

					ResponseEntity<ResponseDto> responseEntity = restTemplate.exchange(uriBuilder, HttpMethod.DELETE,
							entity, ResponseDto.class);

					if (responseEntity.getStatusCode() == HttpStatus.OK) {
						preRegistrationRepository.deleteByPreRegistrationId(preregId);
					} else {
						throw new DocumentFailedToDeleteException(StatusCodes.DOCUMENT_FAILED_TO_DELETE.toString());

					}
					deleteDto.setPrId(applicant.getPreRegistrationId());
					deleteDto.setDeletedDateTime(new Timestamp(System.currentTimeMillis()));

					saveList.add(deleteDto);

				} else {
					throw new OperationNotAllowedException(
							PreRegistrationErrorMessages.DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT);
				}

			}

			// }
		} catch (DataAccessLayerException e) {
			throw new DatabaseOperationException("Failed to delete the appliation", e);
		} catch (HttpClientErrorException e) {
			System.out.println("Error " + e.getResponseBodyAsString());
			List<ExceptionJSONInfo> excepList = new ArrayList<>();
			try {
				org.json.JSONObject json = new org.json.JSONObject(e.getResponseBodyAsString());
				org.json.JSONObject expJson = (org.json.JSONObject) json.getJSONArray("err").get(0);
				ExceptionJSONInfo expInfo = new ExceptionJSONInfo(expJson.get("errorCode").toString(),
						expJson.get("message").toString());
				excepList.add(expInfo);
				response.setErr(excepList);
				response.setResTime(new Timestamp(System.currentTimeMillis()));
				response.setStatus("false");
				return response;
			} catch (JSONException e1) {
				throw new JsonValidationException("Json parsing fails", e1);
			}

		}
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus("true");
		response.setResponse(saveList);

		return response;
	}
>>>>>>> branch 'DEV_SPRINT5_TEAM_BRANCH' of https://github.com/mosip/mosip
}