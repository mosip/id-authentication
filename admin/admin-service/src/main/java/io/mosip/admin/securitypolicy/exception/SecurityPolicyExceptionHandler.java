package io.mosip.admin.securitypolicy.exception;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.admin.securitypolicy.constant.SecurityPolicyConstant;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;

@RestControllerAdvice
public class SecurityPolicyExceptionHandler {

	@ExceptionHandler(SecurityPolicyException.class)
	public ResponseEntity<ResponseWrapper<?>> response(SecurityPolicyException e){
		ServiceError error=new ServiceError(e.getErrorCode(),e.getErrorText());
		ResponseWrapper<ServiceError> responseWrapper=new ResponseWrapper<>();
		responseWrapper.setId(SecurityPolicyConstant.AUTHFACTOR_ID);
		responseWrapper.setMetadata(SecurityPolicyConstant.AUTHFACTOR_ERROR_METADATA);
		responseWrapper.setErrors(Arrays.asList(error));
		responseWrapper.setVersion(SecurityPolicyConstant.AUTHFACTOR_VERSION);
		return new ResponseEntity<>(responseWrapper,HttpStatus.OK);
	}

}
