package io.mosip.pregistration.datasync.exception;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.pregistration.datasync.dto.DataSyncResponseDTO;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfoDTO;
import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.pregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;

/**
 * Exception Handler
 * 
 * @author M1046129
 *
 */
@RestControllerAdvice
public class DataSyncExceptionHandler {

	/**
	 * DataSyncRecordNotFoundException Handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(DataSyncRecordNotFoundException.class)
	public ResponseEntity<DataSyncResponseDTO> dataSyncRecordNotFound(final DataSyncRecordNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_DATA_SYNC_004.toString(),
				ErrorMessages.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());

		DataSyncResponseDTO responseDto = new DataSyncResponseDTO();

		List<ExceptionJSONInfoDTO> err = new ArrayList<>();
		responseDto.setStatus("false");
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);

	}

	/**
	 * ReverseDataFailedToStoreException Handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(ReverseDataFailedToStoreException.class)
	public ResponseEntity<DataSyncResponseDTO> reverseDataSyncFailedToStore(final ReverseDataFailedToStoreException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_REVESE_DATA_SYNC_001.toString(),
				ErrorMessages.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());

		DataSyncResponseDTO responseDto = new DataSyncResponseDTO();

		List<ExceptionJSONInfoDTO> err = new ArrayList<>();
		responseDto.setStatus("false");
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);

	}

	/**
	 * RecordNotFoundForDateRange hanlding
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(RecordNotFoundForDateRange.class)
	public ResponseEntity<DataSyncResponseDTO> databaseerror(final RecordNotFoundForDateRange e, WebRequest request) {
		ArrayList<ExceptionJSONInfoDTO> err = new ArrayList<>();
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_DATA_SYNC_001.toString(),
				ErrorMessages.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
		err.add(errorDetails);
		DataSyncResponseDTO errorRes = new DataSyncResponseDTO();
		errorRes.setErr(err);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.NOT_FOUND);
	}

	/**
	 * TablenotAccessibleException handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<DataSyncResponseDTO> databaseerror(final TablenotAccessibleException e, WebRequest request) {
		ArrayList<ExceptionJSONInfoDTO> err = new ArrayList<>();
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_APP_002.toString(),
				ErrorMessages.REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());
		err.add(errorDetails);
		DataSyncResponseDTO errorRes = new DataSyncResponseDTO();
		errorRes.setErr(err);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * ZipFileCreationException handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(ZipFileCreationException.class)
	public ResponseEntity<DataSyncResponseDTO> zipNotCreated(final ZipFileCreationException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_DATA_SYNC_005.toString(),
				ErrorMessages.FAILED_TO_CREATE_A_ZIP_FILE.toString());

		DataSyncResponseDTO responseDto = new DataSyncResponseDTO();

		List<ExceptionJSONInfoDTO> err = new ArrayList<>();
		responseDto.setStatus("false");
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(DemographicGetDetailsException.class)
	public ResponseEntity<DataSyncResponseDTO> demogetDetails(final DemographicGetDetailsException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_DATA_SYNC_007.toString(),
				ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString());

		DataSyncResponseDTO responseDto = new DataSyncResponseDTO();

		List<ExceptionJSONInfoDTO> err = new ArrayList<>();
		responseDto.setStatus("false");
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);

	}

	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(DocumentGetDetailsException.class)
	public ResponseEntity<DataSyncResponseDTO> docGetDetails(final DocumentGetDetailsException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_DATA_SYNC_008.toString(),
				ErrorMessages.DOCUMENT_GET_RECORD_FAILED.toString());

		DataSyncResponseDTO responseDto = new DataSyncResponseDTO();

		List<ExceptionJSONInfoDTO> err = new ArrayList<>();
		responseDto.setStatus("false");
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);

	}

}
