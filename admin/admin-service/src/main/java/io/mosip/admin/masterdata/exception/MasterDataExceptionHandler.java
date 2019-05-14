package io.mosip.admin.masterdata.exception;

import java.util.Arrays;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.admin.masterdata.constant.MasterDataCardConstant;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MasterDataExceptionHandler {

	@ExceptionHandler(MasterDataCardException.class)
	public ResponseEntity<?> handleError(MasterDataCardException e){
		ServiceError error=new ServiceError();
		error.setErrorCode(e.getErrorCode());
		error.setMessage(e.getErrorText());
		ResponseWrapper<ServiceError> responseWrapper=new ResponseWrapper<>();
		responseWrapper.setErrors(Arrays.asList(error));
		responseWrapper.setId(MasterDataCardConstant.MASTERDATA_CARD_ID);
		responseWrapper.setVersion(MasterDataCardConstant.MASTERDATA_CARD_VERSION);
		responseWrapper.setMetadata(MasterDataCardConstant.MASTERDATA_CARD_ERROR_METADATA);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}
