package io.mosip.preregistration.application.service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.application.code.RequestCodes;
import io.mosip.preregistration.application.code.StatusCodes;
import io.mosip.preregistration.application.dto.CreatePreRegistrationDTO;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.entity.DemographicEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.MissingRequestParameterException;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.exception.system.DateParseException;
import io.mosip.preregistration.application.exception.system.JsonParseException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
import io.mosip.preregistration.core.exceptions.InvalidRequestParameterException;

@Component
public class DemographicServiceUtil {

	public CreatePreRegistrationDTO setterForCreateDTO(DemographicEntity demographicEntity) {
		JSONParser jsonParser = new JSONParser();
		CreatePreRegistrationDTO createDto = new CreatePreRegistrationDTO();
		try {
			createDto.setPreRegistrationId(demographicEntity.getPreRegistrationId());
			createDto.setDemographicDetails((JSONObject) jsonParser
					.parse(new String(demographicEntity.getApplicantDetailJson(), StandardCharsets.UTF_8)));
			createDto.setStatusCode(demographicEntity.getStatusCode());
			createDto.setLangCode(demographicEntity.getLangCode());
			createDto.setCreatedBy(demographicEntity.getCreatedBy());
			createDto.setCreatedDateTime(demographicEntity.getCreateDateTime());
			createDto.setUpdatedBy(demographicEntity.getUpdatedBy());
			createDto.setUpdatedDateTime(demographicEntity.getUpdateDateTime());
		} catch (ParseException e) {
			throw new JsonParseException(ErrorCodes.PRG_PAM_APP_007.toString(),
					ErrorMessages.JSON_PARSING_FAILED.toString(), e.getCause());
		}
		return createDto;
	}

	public DemographicEntity prepareDemographicEntity(CreatePreRegistrationDTO demographicRequest, String requestId,
			String entityType) {
		DemographicEntity demographicEntity = new DemographicEntity();
		demographicEntity.setPreRegistrationId(demographicRequest.getPreRegistrationId());
		demographicEntity.setGroupId("1234567890");
		demographicEntity.setApplicantDetailJson(
				demographicRequest.getDemographicDetails().toJSONString().getBytes(StandardCharsets.UTF_8));
		demographicEntity.setStatusCode(demographicRequest.getStatusCode());
		demographicEntity.setLangCode(demographicRequest.getLangCode());
		demographicEntity.setCrAppuserId(requestId);
		try {
			if (entityType.equals("save")) {
				if (!isNull(demographicRequest.getCreatedBy()) && !isNull(demographicRequest.getCreatedDateTime())
						&& isNull(demographicRequest.getUpdatedBy()) && isNull(demographicEntity.getUpdateDateTime())) {
					demographicEntity.setCreatedBy(demographicRequest.getCreatedBy());
					demographicEntity
							.setCreateDateTime(new Timestamp(demographicRequest.getCreatedDateTime().getTime()));
					demographicEntity.setUpdatedBy(null);
					demographicEntity
							.setUpdateDateTime(new Timestamp(demographicRequest.getCreatedDateTime().getTime()));
				} else {
					throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_APP_012.toString(),
							ErrorMessages.MISSING_REQUEST_PARAMETER.toString());
				}
			} else if (entityType.equals("update")) {
				if (!isNull(demographicRequest.getCreatedBy()) && !isNull(demographicRequest.getCreatedDateTime())
						&& !isNull(demographicRequest.getUpdatedBy())
						&& !isNull(demographicEntity.getUpdateDateTime())) {
					demographicEntity.setCreatedBy(demographicRequest.getCreatedBy());
					demographicEntity
							.setCreateDateTime(new Timestamp(demographicRequest.getCreatedDateTime().getTime()));
					demographicEntity.setUpdatedBy(demographicRequest.getUpdatedBy());
					demographicEntity
							.setUpdateDateTime(new Timestamp(demographicRequest.getUpdatedDateTime().getTime()));
				} else {
					throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_APP_012.toString(),
							ErrorMessages.MISSING_REQUEST_PARAMETER.toString());
				}
			}
		} catch (NullPointerException e) {
			throw new MissingRequestParameterException(ErrorCodes.PRG_PAM_APP_012.toString(),
					ErrorMessages.MISSING_REQUEST_PARAMETER.toString());
		}
		return demographicEntity;
	}

	public Map<String, String> prepareRequestParamMap(
			DemographicRequestDTO<CreatePreRegistrationDTO> demographicRequestDTO) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.toString(), demographicRequestDTO.getId());
		inputValidation.put(RequestCodes.ver.toString(), demographicRequestDTO.getVer());
		inputValidation.put(RequestCodes.reqTime.toString(),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(demographicRequestDTO.getReqTime()));
		inputValidation.put(RequestCodes.request.toString(), demographicRequestDTO.getRequest().toString());
		return inputValidation;
	}

	public String getValueFromIdentity(byte[] demographicData, String identityKey) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObj = (JSONObject) jsonParser.parse(new String(demographicData, StandardCharsets.UTF_8));
		JSONObject identityObj = (JSONObject) jsonObj.get(RequestCodes.identity.toString());
		JSONArray keyArr = (JSONArray) identityObj.get(identityKey);
		JSONObject valueObj = (JSONObject) keyArr.get(0);
		return valueObj.get(RequestCodes.value.toString()).toString();
	}

	public boolean isNull(Object key) {
		if (key instanceof String) {
			if (key.equals(""))
				return true;
		} else if (key instanceof List<?>) {
			if (((List<?>) key).isEmpty())
				return true;
		} else {
			if (key == null)
				return true;
		}
		return false;

	}

	public boolean checkStatusForDeletion(String statusCode) {
		if (!statusCode.equals(StatusCodes.Pending_Appointment.name())
				|| !statusCode.equals(StatusCodes.Booked.name())) {
			throw new OperationNotAllowedException(ErrorCodes.PRG_PAM_APP_003.name(),
					ErrorMessages.DELETE_OPERATION_NOT_ALLOWED.name());
		}

		return true;
	}
	
    public Map<String,Timestamp> dateSetter(Map<String,String> dateMap, String format){
    	Map<String,Timestamp> timeStampMap = new HashMap<>();
    	try {
    	Date fromDate = DateUtils.parse(URLDecoder.decode(dateMap.get("FromDate"), "UTF-8"), format);
		Date toDate = null;
		if (dateMap.get("ToDate") == null || isNull(dateMap.get("ToDate"))) {
			toDate = fromDate;
			Calendar cal = Calendar.getInstance();
			cal.setTime(toDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			toDate = cal.getTime();
		} else {
			toDate = DateUtils.parse(URLDecoder.decode(dateMap.get("ToDate"), "UTF-8"), format);
		}
		timeStampMap.put("FromDate", new Timestamp(fromDate.getTime()));
		timeStampMap.put("ToDate", new Timestamp(toDate.getTime()));
		
    	}catch (java.text.ParseException e) {
    		throw new DateParseException(ErrorCodes.PRG_PAM_APP_011.toString(),
					ErrorMessages.UNSUPPORTED_DATE_FORMAT.toString(), e.getCause());
		} catch (UnsupportedEncodingException e) {
			throw new SystemUnsupportedEncodingException(ErrorCodes.PRG_PAM_APP_009.toString(),
					ErrorMessages.UNSUPPORTED_ENCODING_CHARSET.toString(), e.getCause());
		}
    	return timeStampMap;
    }
}
