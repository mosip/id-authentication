package io.mosip.preregistration.datasync.exception.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.GenericUtil;
import io.mosip.preregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.preregistration.datasync.exception.DemographicGetDetailsException;
import io.mosip.preregistration.datasync.exception.DocumentGetDetailsException;
import io.mosip.preregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.preregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.preregistration.datasync.exception.ZipFileCreationException;
import io.mosip.preregistration.datasync.exception.system.SystemFileIOException;

/**
 * Exception Handler
 * 
 * @author M1046129
 *
 */
@RestControllerAdvice
public class DataSyncExceptionHandler {
	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * DataSyncRecordNotFoundException Handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(DataSyncRecordNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> dataSyncRecordNotFound(final DataSyncRecordNotFoundException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}

	/**
	 * ReverseDataFailedToStoreException Handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(ReverseDataFailedToStoreException.class)
	public ResponseEntity<MainResponseDTO<?>> reverseDataSyncFailedToStore(final ReverseDataFailedToStoreException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}

	/**
	 * RecordNotFoundForDateRange hanlding
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(RecordNotFoundForDateRange.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final RecordNotFoundForDateRange e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}

	/**
	 * TablenotAccessibleException handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(TableNotAccessibleException.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final TableNotAccessibleException e) {
		return GenericUtil.errorResponse(e, e.getMainResposneDTO());
	}

	/**
	 * ZipFileCreationException handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(ZipFileCreationException.class)
	public ResponseEntity<MainResponseDTO<?>> zipNotCreated(final ZipFileCreationException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}

	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(DemographicGetDetailsException.class)
	public ResponseEntity<MainResponseDTO<?>> demogetDetails(final DemographicGetDetailsException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());

	}

	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(DocumentGetDetailsException.class)
	public ResponseEntity<MainResponseDTO<?>> docGetDetails(final DocumentGetDetailsException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());

	}

	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidRequestParamCheck(final InvalidRequestParameterException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());

	}

	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(SystemFileIOException.class)
	public ResponseEntity<MainResponseDTO<?>> fileIOException(final SystemFileIOException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());

	}
	
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<MainResponseDTO<?>> DateFormatException(final InvalidFormatException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_CORE_REQ_003.getCode(),ErrorMessages.INVALID_REQUEST_DATETIME.getMessage());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}
}
