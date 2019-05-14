package io.mosip.admin.usermgmt.util;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.mosip.admin.usermgmt.exception.AdminServiceResponseException;
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
			throw new AdminServiceResponseException(validationErrorList);
		}
	}
}
