package io.mosip.preregistration.documents.exception;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.ExceptionJSONInfo;
import io.mosip.preregistration.documents.dto.ResponseDto;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * Exception Handler
 * 
 * @author M1037717
 *
 */
@RestControllerAdvice
public class DocumentExceptionHandler {

	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ExceptionJSONInfo> databaseerror(final TablenotAccessibleException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_007.toString(),
				StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DocumentNotValidException.class)
	public ResponseEntity<ExceptionJSONInfo> notValidExceptionhadler(final DocumentNotValidException nv,
			WebRequest webRequest) {

		ExceptionJSONInfo jsonInfo = new ExceptionJSONInfo(nv.getErrorCode(), nv.getErrorText());

		return new ResponseEntity<ExceptionJSONInfo>(jsonInfo, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ExceptionJSONInfo> sizeExceedException(final MultipartException me, WebRequest webRequest) {

		ExceptionJSONInfo jsonInfo = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_004.toString(),
				StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());

		return new ResponseEntity<ExceptionJSONInfo>(jsonInfo, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DocumentNotFoundException.class)
	public ResponseEntity<ResponseDto> documentNotFound(final DocumentNotFoundException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_006.toString(),
				StatusCodes.DOCUMENT_IS_MISSING.toString());
		ResponseDto responseDto = new ResponseDto();
		//
		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus("false");
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		System.out.println("responseDto::"+responseDto);
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

}
