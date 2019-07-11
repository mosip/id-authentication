package io.mosip.admin.usermgmt.util;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.usermgmt.constant.UserMgmtErrorCode;
import io.mosip.admin.usermgmt.exception.UsermanagementServiceResponseException;
import io.mosip.admin.usermgmt.exception.UsermanagementServiceException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;

public class UserMgmtUtil {

	//mock for now
	public static String validateUserRid(String rid,String userName) {
		return "SUCCESS";
	}
	
	
	public static void throwExceptionIfExist(ResponseEntity<String> response) {
		String responseBody = response.getBody();
		List<ServiceError> validationErrorList = ExceptionUtils.getServiceErrorList(responseBody);
		if (!validationErrorList.isEmpty()) {
			throw new UsermanagementServiceResponseException(validationErrorList);
		}
	}
	
	public static <S> S getResponse(ObjectMapper objectMapper,ResponseEntity<String> response, Class<S> clazz) {
		try {
			JsonNode res =objectMapper.readTree(response.getBody());
			return objectMapper.readValue(res.get("response").toString(), clazz);
		} catch (IOException e) {
			throw new UsermanagementServiceException(UserMgmtErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
					UserMgmtErrorCode.INTERNAL_SERVER_ERROR.getErrorMessage() + e.getMessage());
		}
	}
}
