package io.mosip.kernel.masterdata.exceptionhandler;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.RegistrationCenterErrorCode;
import io.mosip.kernel.masterdata.constant.RegistrationCenterUserMappingHistoryErrorCode;
import io.mosip.kernel.masterdata.exception.BiometricAttributeNotFoundException;
import io.mosip.kernel.masterdata.exception.BiometricTypeFetchException;
import io.mosip.kernel.masterdata.exception.BiometricTypeMappingException;
import io.mosip.kernel.masterdata.exception.BiometricTypeNotFoundException;
import io.mosip.kernel.masterdata.exception.BlacklistedWordsFetchException;
import io.mosip.kernel.masterdata.exception.BlacklistedWordsIllegalArgException;
import io.mosip.kernel.masterdata.exception.BlacklistedWordsMappingException;
import io.mosip.kernel.masterdata.exception.DocumentCategoryFetchException;
import io.mosip.kernel.masterdata.exception.DocumentCategoryNotFoundException;
import io.mosip.kernel.masterdata.exception.GenderTypeFetchException;
import io.mosip.kernel.masterdata.exception.GenderTypeMappingException;
import io.mosip.kernel.masterdata.exception.GenderTypeNotFoundException;
import io.mosip.kernel.masterdata.exception.HolidayFetchException;
import io.mosip.kernel.masterdata.exception.HolidayMappingException;
import io.mosip.kernel.masterdata.exception.InvalidDateTimeFormatException;
import io.mosip.kernel.masterdata.exception.LanguageFetchException;
import io.mosip.kernel.masterdata.exception.LanguageMappingException;
import io.mosip.kernel.masterdata.exception.LanguageNotFoundException;
import io.mosip.kernel.masterdata.exception.LocationDatabaseException;
import io.mosip.kernel.masterdata.exception.LocationRecordsNotFoundException;
import io.mosip.kernel.masterdata.exception.MachineDetailFetchException;
import io.mosip.kernel.masterdata.exception.MachineDetailMappingException;
import io.mosip.kernel.masterdata.exception.MachineDetailNotFoundException;
import io.mosip.kernel.masterdata.exception.MachineHistoryFetchException;
import io.mosip.kernel.masterdata.exception.MachineHistoryMappingException;
import io.mosip.kernel.masterdata.exception.MachineHistroyNotFoundException;
import io.mosip.kernel.masterdata.exception.NoBlacklistedWordsFoundException;
import io.mosip.kernel.masterdata.exception.NoHolidayDataFoundException;
import io.mosip.kernel.masterdata.exception.RegistrationCenterFetchException;
import io.mosip.kernel.masterdata.exception.RegistrationCenterMappingException;
import io.mosip.kernel.masterdata.exception.RegistrationCenterNotFoundException;
import io.mosip.kernel.masterdata.exception.RegistrationCenterUserMachineMappingFetchHistoryException;
import io.mosip.kernel.masterdata.exception.RegistrationCenterUserMachineMappingHistoryException;
import io.mosip.kernel.masterdata.exception.RegistrationCenterUserMachineMappingNotFoundHistoryException;
import io.mosip.kernel.masterdata.exception.DeviceFetchException;
import io.mosip.kernel.masterdata.exception.DeviceMappingException;
import io.mosip.kernel.masterdata.exception.DeviceNotFoundException;
import io.mosip.kernel.masterdata.exception.TemplateFetchException;
import io.mosip.kernel.masterdata.exception.TemplateMappingException;
import io.mosip.kernel.masterdata.exception.TemplateNotFoundException;
import io.mosip.kernel.masterdata.exception.TitleFetchException;
import io.mosip.kernel.masterdata.exception.TitleMappingException;
import io.mosip.kernel.masterdata.exception.TitleNotFoundException;

/**
 * Rest Controller Advice for Master Data
 * 
 * @author Dharmesh Khandelwal
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class MasterDataControllerAdvice {
	private static final String ERR = "error";

	@ExceptionHandler(RegistrationCenterUserMachineMappingFetchHistoryException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> registrationCenterFetchException(
			final RegistrationCenterUserMachineMappingFetchHistoryException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(RegistrationCenterUserMachineMappingHistoryException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> registrationCenterMappingException(
			final RegistrationCenterUserMachineMappingHistoryException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(DateTimeParseException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> numberFormatException(final DateTimeParseException e) {
		ErrorBean error = new ErrorBean(
				RegistrationCenterUserMappingHistoryErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),
				e.getMessage() + MasterDataConstant.DATETIMEFORMAT);
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	private Map<String, ArrayList<ErrorBean>> setError(ErrorBean error) {
		ArrayList<ErrorBean> errorList = new ArrayList<>();
		errorList.add(error);
		Map<String, ArrayList<ErrorBean>> map = new HashMap<>();
		map.put(ERR, errorList);
		return map;
	}

	@ExceptionHandler(RegistrationCenterUserMachineMappingNotFoundHistoryException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> registrationCenterrMachineMappingHistoryNotFoundException(
			final RegistrationCenterUserMachineMappingNotFoundHistoryException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(RegistrationCenterFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> registrationCenterFetchException(
			final RegistrationCenterFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(RegistrationCenterMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> registrationCenterMappingException(
			final RegistrationCenterMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(NumberFormatException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> numberFormatException(final NumberFormatException e) {
		ErrorBean error = new ErrorBean(RegistrationCenterErrorCode.NUMBER_FORMAT_EXCEPTION.getErrorCode(),
				e.getMessage());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(RegistrationCenterNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> registrationCenterNotFoundException(
			final RegistrationCenterNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidDateTimeFormatException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> machineHistoryInvalideDateTimeFormateException(
			final InvalidDateTimeFormatException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	
	@ExceptionHandler(LocationDatabaseException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> locationHierarchyDatabaseException(
			final LocationDatabaseException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(LocationRecordsNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> locationHierarchyNoRecordsFoundException(
			final LocationRecordsNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(HolidayFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> holidayFetchException(final HolidayFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(NoHolidayDataFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> holidayInvalidIdException(
			final NoHolidayDataFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(HolidayMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> holidayMappingException(final HolidayMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(GenderTypeFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> genderTypeFetchException(
			final GenderTypeFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(GenderTypeNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> genderTypeNotFoundException(
			final GenderTypeNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(GenderTypeMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> genderTypeMappingException(
			final GenderTypeMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(DocumentCategoryNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> doumentCategoryNotFoundException(
			final DocumentCategoryNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(DocumentCategoryFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> documentCategoryFetchException(
			final DocumentCategoryFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(BiometricTypeFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> biometricTypeFetchException(
			final BiometricTypeFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(BiometricTypeMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> biometricTypeMappingException(
			final BiometricTypeMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(BiometricTypeNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> biometricTypeNotFoundException(
			final BiometricTypeNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BiometricAttributeNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> biometricAttributeNotFoundException(
			final BiometricAttributeNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(MachineDetailNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> machineDetailNotFoundException(
			final MachineDetailNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * This method handle MachineDetailFetchException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(MachineDetailFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> machineDetailFetchException(
			final MachineDetailFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * This method handle MachineDetailMappingException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */

	@ExceptionHandler(MachineDetailMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> machineDetailMappingException(
			final MachineDetailMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * This method handle MachineHistoryFetchException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(MachineHistoryFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> machineHistoryFetchException(
			final MachineHistoryFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * This method handle MachineHistoryMappingException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(MachineHistoryMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> machineHistoryMappingException(
			final MachineHistoryMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(MachineHistroyNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> machineHistoryNotFoundException(
			final MachineHistroyNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * This method handle DeviceFetchException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(DeviceFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> deviceFetchException(
			final DeviceFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(DeviceMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> deviceMappingException(
			final DeviceMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(DeviceNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> deviceNotFoundException(
			final DeviceNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(BlacklistedWordsMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> blacklistedWordsMappingException(
			final BlacklistedWordsMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(BlacklistedWordsFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> blacklistedWordsFetchException(
			final BlacklistedWordsFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(NoBlacklistedWordsFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> noBlacklistedWordsFoundException(
			final NoBlacklistedWordsFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(BlacklistedWordsIllegalArgException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> blacklistedWordsIllegalArgException(
			final BlacklistedWordsIllegalArgException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * This method handle when exception occur while fetching Language.
	 * 
	 * @param e
	 *            is of type LanguageFetchException
	 * @return ResponseEntity
	 */
	@ExceptionHandler(LanguageFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> handleLanguageFetchException(
			final LanguageFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.SERVICE_UNAVAILABLE);
	}

	/**
	 * This method handle when exception due to not record found.
	 * 
	 * @param e
	 *            is of type LanguageNotFoundException
	 * @return ResponseEntity
	 */
	@ExceptionHandler(LanguageNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> handleLanguageNotFoundException(
			final LanguageNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NO_CONTENT);
	}

	/**
	 * This method handle when exception while mapping Language.
	 * 
	 * @param e
	 *            is of type LanguageMappingException
	 * @return ResponseEntity
	 */
	@ExceptionHandler(LanguageMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> handleLanguageMappingException(
			final LanguageMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(TitleFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> titleFetchException(final TitleFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(TitleNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> titleNotFoundException(final TitleNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(TitleMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> titleMappingException(final TitleMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(TemplateFetchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> templateFetchException(final TemplateFetchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(TemplateMappingException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> templateMappingException(
			final TemplateMappingException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(TemplateNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> templateNotFoundException(
			final TemplateNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
	}

}
