package io.mosip.kernel.masterdata.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.idgenerator.spi.MachineIdGenerator;
import io.mosip.kernel.core.idgenerator.spi.RegistrationCenterIdGenerator;
import io.mosip.kernel.masterdata.constant.MachineErrorCode;
import io.mosip.kernel.masterdata.constant.RegistrationCenterErrorCode;
import io.mosip.kernel.masterdata.constant.ValidationErrorCode;
import io.mosip.kernel.masterdata.dto.RegCenterPostReqDto;
import io.mosip.kernel.masterdata.dto.RegCenterPutReqDto;
import io.mosip.kernel.masterdata.dto.RegcenterBaseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.RegistrationCenterPostResponseDto;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterHistory;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.exception.ValidationException;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;

@Component
public class RegistrationCenterValidator {

	@Autowired
	private ZoneUtils zoneUtils;

	private String negRegex;
	private String posRegex;

	/**
	 * minimum digits after decimal point in Longitude and latitude
	 */
	@Value("${mosip.min-digit-longitude-latitude:4}")
	private int minDegits;

	/**
	 * get list of secondary languages supported by MOSIP from configuration file
	 */
	@Value("#{'${mosip.secondary-language}'.split(',')}")
	private Set<String> secondaryLangList;

	/**
	 * get list of secondary languages supported by MOSIP from configuration file
	 */
	@Value("${mosip.primary-language}")
	private String primaryLang;

	/**
	 * get list of secondary languages supported by MOSIP from configuration file
	 */
	@Value("${mosip.secondary-language}")
	private String secondaryLang;

	private Set<String> supportedLanguages;

	/**
	 * Constructing regex for matching the Latitude and Longitude format
	 */

	@PostConstruct
	public void constructRegEx() {
		supportedLanguages = new HashSet<>(Arrays.asList(secondaryLang.split(",")));
		supportedLanguages.add(primaryLang);
		negRegex = "^(\\-\\d{1,2}\\.\\d{" + minDegits + ",})$";
		posRegex = "^(\\d{1,2}\\.\\d{" + minDegits + ",})$";
	}

	@Autowired
	RegistrationCenterIdGenerator<String> registrationCenterIdGenerator;

	@Autowired
	RegistrationCenterRepository registrationCenterRepository;

	@Autowired
	RegistrationCenterHistoryRepository registrationCenterHistoryRepository;
	
	@Autowired
	HolidayRepository holidayRepository;
	
	
    // method to compare data
	public <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean isValid(T firstObj, D eachRecord,
			List<ServiceError> errors) {

		return validateCenterType(firstObj, eachRecord, errors) | valdateLatitude(firstObj, eachRecord, errors)
				| validateLongitude(firstObj, eachRecord, errors) | validateContactPhone(firstObj, eachRecord, errors)
				| validateWorkingHours(firstObj, eachRecord, errors)
				| validateCenterStartTime(firstObj, eachRecord, errors)
				| validateCenterEndTime(firstObj, eachRecord, errors)
				| validateLunchStartTime(firstObj, eachRecord, errors)
				| validateLunchEndTime(firstObj, eachRecord, errors) | validateTimeZone(firstObj, eachRecord, errors)
				| validateHolidayCode(firstObj, eachRecord, errors) | validateZoneCode(firstObj, eachRecord, errors);
	}
	
	

	
	//  method to compare Latitude 
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean valdateLatitude(T firstObj, D eachRecord,
			List<ServiceError> errors) {
		if (eachRecord.getLatitude() != null && firstObj.getLatitude() != null) {
			if (eachRecord.getLatitude().trim().equalsIgnoreCase(firstObj.getLatitude().trim())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.LATITUDE_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.LATITUDE_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getLatitude())));

			}
		}
		return false;
	}

	//  method to compare Longitude 
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateLongitude(T firstObj, D eachRecord,
			List<ServiceError> errors) {
		if (eachRecord.getLongitude() != null && firstObj.getLongitude() != null) {
			if (eachRecord.getLongitude().trim().equalsIgnoreCase(firstObj.getLongitude().trim())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.LONGITUDE_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.LONGITUDE_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getLongitude())));

			}
		}
		return false;
	}

	//  method to compare ContactPhone 
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateContactPhone(T firstObj,
			D eachRecord, List<ServiceError> errors) {
		if (eachRecord.getContactPhone() != null && firstObj.getContactPhone() != null) {
			if (eachRecord.getContactPhone().trim().equalsIgnoreCase(firstObj.getContactPhone().trim())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.CONTACT_PHONE_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.CONTACT_PHONE_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getContactPhone())));

			}
		}
		return false;
	}

	//  method to compare WorkingHours 
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateWorkingHours(T firstObj,
			D eachRecord, List<ServiceError> errors) {
		if (eachRecord.getWorkingHours() != null && firstObj.getWorkingHours() != null) {
			if (eachRecord.getWorkingHours().trim().equalsIgnoreCase(firstObj.getWorkingHours().trim())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.WORKING_HOURS_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.WORKING_HOURS_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getWorkingHours())));

			}
		}
		return false;
	}

	//  method to compare CenterStartTime 
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateCenterStartTime(T firstObj,
			D eachRecord, List<ServiceError> errors) {
		if (eachRecord.getCenterStartTime() != null && firstObj.getCenterStartTime() != null) {
			if (eachRecord.getCenterStartTime().equals(firstObj.getCenterStartTime())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.CENTER_STRART_TIME_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.CENTER_STRART_TIME_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getCenterStartTime())));

			}
		}
		return false;
	}

	//  method to compare CenterEndTime 
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateCenterEndTime(T firstObj,
			D eachRecord, List<ServiceError> errors) {
		if (eachRecord.getCenterEndTime() != null && firstObj.getCenterEndTime() != null) {
			if (eachRecord.getCenterEndTime().equals(firstObj.getCenterEndTime())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.CENTER_END_TIME_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.CENTER_END_TIME_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getCenterEndTime())));

			}
		}
		return false;
	}

	//  method to compare LunchStartTime 
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateLunchStartTime(T firstObj,
			D eachRecord, List<ServiceError> errors) {
		if (eachRecord.getLunchStartTime() != null && firstObj.getLunchStartTime() != null) {
			if (eachRecord.getLunchStartTime().equals(firstObj.getLunchStartTime())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.LUNCH_START_TIME_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.LUNCH_START_TIME_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getLunchStartTime())));

			}
		}
		return false;
	}

	//  method to compare LunchEndTime 
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateLunchEndTime(T firstObj,
			D eachRecord, List<ServiceError> errors) {
		if (eachRecord.getLunchEndTime() != null && firstObj.getLunchEndTime() != null) {
			if (eachRecord.getLunchEndTime().equals(firstObj.getLunchEndTime())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.LUNCH_END_TIME_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.LUNCH_END_TIME_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getLunchEndTime())));

			}
		}
		return false;
	}

	//  method to compare TimeZone
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateTimeZone(T firstObj, D eachRecord,
			List<ServiceError> errors) {
		if (eachRecord.getTimeZone() != null && firstObj.getTimeZone() != null) {
			if (eachRecord.getTimeZone().trim().equalsIgnoreCase(firstObj.getTimeZone().trim())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.TIME_ZONE_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.TIME_ZONE_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getTimeZone())));

			}
		}
		return false;
	}

	//  method to compare HolidayCode
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateHolidayCode(T firstObj,
			D eachRecord, List<ServiceError> errors) {
		if (eachRecord.getHolidayLocationCode() != null && firstObj.getHolidayLocationCode() != null) {
			if (eachRecord.getHolidayLocationCode().trim().equalsIgnoreCase(firstObj.getHolidayLocationCode().trim())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.HOLIDAY_LOCATION_CODE_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.HOLIDAY_LOCATION_CODE_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getHolidayLocationCode())));

			}
		}
		return false;
	}

	//  method to compare ZoneCode
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateZoneCode(T firstObj, D eachRecord,
			List<ServiceError> errors) {
		if (eachRecord.getZoneCode() != null && firstObj.getZoneCode() != null) {
			if (eachRecord.getZoneCode().trim().equalsIgnoreCase(firstObj.getZoneCode().trim())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.ZONE_CODE_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.ZONE_CODE_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getZoneCode())));

			}
		}
		return false;
	}

	//  method to compare CenterType
	private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validateCenterType(T firstObj,
			D eachRecord, List<ServiceError> errors) {
		if (eachRecord.getCenterTypeCode() != null && firstObj.getCenterTypeCode() != null) {
			if (eachRecord.getCenterTypeCode().trim().equalsIgnoreCase(firstObj.getCenterTypeCode().trim())) {
				return true;
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.CENTER_TYPE_CODE_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.CENTER_TYPE_CODE_NOT_UNIQUE.getErrorMessage(),
								eachRecord.getCenterTypeCode())));

			}

		}
		return false;
	}

	// ---------------------------------------------------------------------------------------------------------//

	// method to validate the format of the longitude and latitude, zone validation,
	// lunch and center start and end time
//	public <T extends RegcenterBaseDto, D extends RegcenterBaseDto> void validateRegCenterCreateReq(
//			T registrationCenterDto, List<ServiceError> errors) {
//
//		String latitude = registrationCenterDto.getLatitude();
//		String longitude = registrationCenterDto.getLongitude();
//
//		zoneUserMapValidation(registrationCenterDto, errors, getZoneIdsForUser());
//		zoneStartEndTimeGtrValidation(registrationCenterDto, errors);
//		lunchStartEndTimeGrtValidation(registrationCenterDto, errors);
//		formatValidationLongitudeLatitude(errors, latitude, longitude);
//
//	}
	
	public void validateRegCenterCreate(
			RegCenterPostReqDto registrationCenterDto, List<ServiceError> errors) {

		String latitude = registrationCenterDto.getLatitude();
		String longitude = registrationCenterDto.getLongitude();

		zoneUserMapValidation(registrationCenterDto, errors, getZoneIdsForUser());
		zoneStartEndTimeGtrValidation(registrationCenterDto, errors);
		lunchStartEndTimeGrtValidation(registrationCenterDto, errors);
		formatValidationLongitudeLatitude(errors, latitude, longitude);
		holidayVlidation(registrationCenterDto, errors);

	}
	
	// validate Holiday against DB
	private void holidayVlidation(RegCenterPostReqDto registrationCenterDto, List<ServiceError> errors) {
		List<Holiday> holidays = holidayRepository
				.findHolidayByHolidayIdLocationCode(registrationCenterDto.getHolidayLocationCode());
		if (holidays.isEmpty()) {
			errors.add(new ServiceError(RegistrationCenterErrorCode.HOLIDAY_NOT_FOUND.getErrorCode(),
					String.format(RegistrationCenterErrorCode.HOLIDAY_NOT_FOUND.getErrorMessage(),
							registrationCenterDto.getZoneCode())));
		 }
	}

	// list zone Id mapped with the called user
	private List<String> getZoneIdsForUser() {
		List<String> zoneIds;
		List<Zone> zones = zoneUtils.getUserLeafZones(primaryLang);
		zoneIds = zones.parallelStream().map(Zone::getCode).collect(Collectors.toList());
		return zoneIds;
	}

	// validation to check entered zoneCode is mapped with eligible user or not and
	// is valid zoneCode
	private void zoneUserMapValidation(RegCenterPostReqDto registrationCenterDto, List<ServiceError> errors,
			List<String> zoneIds) {

		if (!zoneIds.isEmpty()) {
			if (!zoneIds.contains(registrationCenterDto.getZoneCode())) {
				errors.add(new ServiceError(RegistrationCenterErrorCode.INVALIDE_ZONE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.INVALIDE_ZONE.getErrorMessage(),
								registrationCenterDto.getZoneCode())));
			}
		}
	}

	// validate to check the format of latitude and longitude
	// Latitude or Longitude must have minimum 4 digits after decimal
	private void formatValidationLongitudeLatitude(List<ServiceError> errors, String latitude, String longitude) {

		if (!((Pattern.matches(negRegex, latitude) || Pattern.matches(posRegex, latitude))
				&& (Pattern.matches(negRegex, longitude) || Pattern.matches(posRegex, longitude)))) {
			errors.add(
					new ServiceError(RegistrationCenterErrorCode.REGISTRATION_CENTER_FORMATE_EXCEPTION.getErrorCode(),
							RegistrationCenterErrorCode.REGISTRATION_CENTER_FORMATE_EXCEPTION.getErrorMessage()));
		}
	}

	// validation to check the RegCenter Lunch Start Time is greater
	// than RegCenter
	// Lunch End Time
	private void lunchStartEndTimeGrtValidation(RegCenterPostReqDto registrationCenterDto,
			List<ServiceError> errors) {
		// validation to check the RegCenter Lunch Start Time is greater than RegCenter
		// Lunch End Time
		if (registrationCenterDto.getLunchStartTime().isAfter(registrationCenterDto.getLunchEndTime())) {
			errors.add(new ServiceError(
					RegistrationCenterErrorCode.REGISTRATION_CENTER_LUNCH_START_END_EXCEPTION.getErrorCode(),
					String.format(
							RegistrationCenterErrorCode.REGISTRATION_CENTER_LUNCH_START_END_EXCEPTION.getErrorMessage(),
							registrationCenterDto.getLunchEndTime())));

		}
	}

	// validation to check the RegCenter Start Time is greater than
	// RegCenter End Time
	private void zoneStartEndTimeGtrValidation(RegCenterPostReqDto registrationCenterDto,
			List<ServiceError> errors) {
		if (registrationCenterDto.getCenterStartTime().isAfter(registrationCenterDto.getCenterEndTime())) {
			errors.add(new ServiceError(
					RegistrationCenterErrorCode.REGISTRATION_CENTER_START_END_EXCEPTION.getErrorCode(),
					String.format(RegistrationCenterErrorCode.REGISTRATION_CENTER_START_END_EXCEPTION.getErrorMessage(),
							registrationCenterDto.getCenterEndTime())));
		}
	}

	// ------------------------------------------------------------------------------------------------------------
	// method to validate the primary and secondary language input objects mandatory
	// fields
	public void validatePrimarySencodaryLangMandatoryFields(List<RegCenterPostReqDto> reqRegistrationCenterDto,
			RegistrationCenterPostResponseDto registrationCenterPostResponseDto, List<String> inputLangCodeList,
			List<RegCenterPostReqDto> validateRegistrationCenterDtos,
			List<RegCenterPostReqDto> constraintViolationedSecList, List<ServiceError> errors) {
		List<ServiceError> secErrors = new ArrayList<>();
		RegCenterPostReqDto firstObject = null;

		Optional<RegCenterPostReqDto> defualtLangVal = reqRegistrationCenterDto.stream()
				.filter(i -> i.getLangCode().equals(primaryLang)).findAny();

		if (!defualtLangVal.isPresent()) {
			throw new RequestException(RegistrationCenterErrorCode.DEFAULT_LANGUAGE.getErrorCode(),
					RegistrationCenterErrorCode.DEFAULT_LANGUAGE.getErrorMessage());
		}

		for (RegCenterPostReqDto registrationCenterDto : reqRegistrationCenterDto) {
			if (registrationCenterDto.getLangCode() != null
					&& registrationCenterDto.getLangCode().equals(primaryLang)) {
				firstObject = registrationCenterDto;
			} else if ((registrationCenterDto.getLangCode() != null)
					&& (secondaryLangList.contains(registrationCenterDto.getLangCode()))) {
				firstObject = reqRegistrationCenterDto.get(0);
			}
		}

		constraintViolationPrimSecLangData(reqRegistrationCenterDto, registrationCenterPostResponseDto,
				inputLangCodeList, validateRegistrationCenterDtos, constraintViolationedSecList, errors, secErrors,
				firstObject);
	}


	// method to find the 
	private void constraintViolationPrimSecLangData(List<RegCenterPostReqDto> reqRegistrationCenterDto,
			RegistrationCenterPostResponseDto registrationCenterPostResponseDto, List<String> inputLangCodeList,
			List<RegCenterPostReqDto> validateRegistrationCenterDtos,
			List<RegCenterPostReqDto> constraintViolationedSecList, List<ServiceError> errors,
			List<ServiceError> secErrors, RegCenterPostReqDto firstObject) {
		for (RegCenterPostReqDto registrationCenterDto : reqRegistrationCenterDto) {

			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();

			if ((registrationCenterDto.getLangCode() != null)
					&& (registrationCenterDto.getLangCode().equalsIgnoreCase(primaryLang))) {

				Set<ConstraintViolation<RegCenterPostReqDto>> constraintViolations = validator
						.validate(registrationCenterDto);

				primaryLanguageValidation(validateRegistrationCenterDtos, errors, registrationCenterDto,
						constraintViolations);

			} else if ((registrationCenterDto.getLangCode() != null)
					&& (secondaryLangList.contains(registrationCenterDto.getLangCode()))) {

				secondaryLangValidation(registrationCenterPostResponseDto,
						validateRegistrationCenterDtos, constraintViolationedSecList, secErrors, registrationCenterDto,
						validator, firstObject);
			} else {
				errors.add(new ServiceError(RegistrationCenterErrorCode.LANGUAGE_EXCEPTION.getErrorCode(),
						String.format(RegistrationCenterErrorCode.LANGUAGE_EXCEPTION.getErrorMessage(),
								registrationCenterDto.getLangCode())));
				registrationCenterPostResponseDto.setConstraintViolationError(errors);
			}
			inputLangCodeList.add(registrationCenterDto.getLangCode());
		}
	}

	private void primaryLanguageValidation(List<RegCenterPostReqDto> validateRegistrationCenterDtos,
			List<ServiceError> errors, RegCenterPostReqDto registrationCenterDto,
			Set<ConstraintViolation<RegCenterPostReqDto>> constraintViolations) {
		if (!constraintViolations.isEmpty()) {
			constraintViolationIterator(errors, registrationCenterDto, constraintViolations);
			if (!errors.isEmpty())
				throw new ValidationException(errors);
		} else {
			// call method to validate Zone-Id, longitude and latitude
			//validateRegCenterCreateReq(registrationCenterDto, errors);
			if (!errors.isEmpty()) {
				throw new ValidationException(errors);
			}
			// add primary language Object to list after all validation are true
			validateRegistrationCenterDtos.add(registrationCenterDto);
		}
	}

	// secondary language validation
	public void secondaryLangValidation(RegistrationCenterPostResponseDto registrationCenterPostResponseDto,
			List<RegCenterPostReqDto> validateRegistrationCenterDtos,
			List<RegCenterPostReqDto> constraintViolationedSecList, List<ServiceError> errors,
			RegCenterPostReqDto registrationCenterDto, Validator validator, RegCenterPostReqDto firstObject) {
		Set<ConstraintViolation<RegCenterPostReqDto>> constraintViolations = validator.validate(registrationCenterDto);
		if (!constraintViolations.isEmpty()) {
			constraintViolationIterator(errors, registrationCenterDto, constraintViolations);
			if (!errors.isEmpty()) {
				registrationCenterPostResponseDto.setConstraintViolationError(errors);
			}
			constraintViolationedSecList.add(registrationCenterDto);

		} else {
			//isValid(firstObject, registrationCenterDto, errors);
			//validateRegCenterCreateReq(registrationCenterDto, errors);
			if (!errors.isEmpty()) {
				registrationCenterPostResponseDto.setConstraintViolationError(errors);
				constraintViolationedSecList.add(registrationCenterDto);
			} else {
				validateRegistrationCenterDtos.add(registrationCenterDto);
			}

		}
	}

	// List constraint violation iterator
	private void constraintViolationIterator(List<ServiceError> errors, RegCenterPostReqDto registrationCenterDto,
			Set<ConstraintViolation<RegCenterPostReqDto>> constraintViolations) {

		Iterator<ConstraintViolation<RegCenterPostReqDto>> iterator = constraintViolations.iterator();
		while (iterator.hasNext()) {
			ConstraintViolation<RegCenterPostReqDto> cv = iterator.next();
			errors.add(new ServiceError(ValidationErrorCode.CONSTRAINT_VIOLATION.getErrorCode(),
					ValidationErrorCode.CONSTRAINT_VIOLATION.getErrorMessage() + " for the LangCode- "
							+ registrationCenterDto.getLangCode() + " - " + cv.getPropertyPath() + " "
							+ cv.getMessage()));
		}
	}

	//map DTO to Entity
	public <T extends RegcenterBaseDto> void mapBaseDtoEntity(RegistrationCenter registrationCenterEntity,
			T registrationCenterDto) {
		registrationCenterEntity.setCenterTypeCode(registrationCenterDto.getCenterTypeCode());
		registrationCenterEntity.setLatitude(registrationCenterDto.getLatitude());
		registrationCenterEntity.setLongitude(registrationCenterDto.getLongitude());
		registrationCenterEntity.setLocationCode(registrationCenterDto.getLocationCode());
		registrationCenterEntity.setHolidayLocationCode(registrationCenterDto.getHolidayLocationCode());
		registrationCenterEntity.setContactPhone(registrationCenterDto.getContactPhone());
		registrationCenterEntity.setWorkingHours(registrationCenterDto.getWorkingHours());
		registrationCenterEntity.setPerKioskProcessTime(registrationCenterDto.getPerKioskProcessTime());
		registrationCenterEntity.setCenterStartTime(registrationCenterDto.getCenterStartTime());
		registrationCenterEntity.setCenterEndTime(registrationCenterDto.getCenterEndTime());
		registrationCenterEntity.setLunchStartTime(registrationCenterDto.getLunchStartTime());
		registrationCenterEntity.setLunchEndTime(registrationCenterDto.getLunchEndTime());
		registrationCenterEntity.setTimeZone(registrationCenterDto.getTimeZone());
		registrationCenterEntity.setZoneCode(registrationCenterDto.getZoneCode());
	}

	

	// call method generate ID or validate with DB
	public String generateIdOrvalidateWithDB(String uniqueId) {
		if (uniqueId.isEmpty()) {
			// Get RegistrationCenter Id by calling RegistrationCenterIdGenerator
			// API
			uniqueId = registrationCenterIdGenerator.generateRegistrationCenterId();

		} else {
			List<RegistrationCenter> renRegistrationCenters = registrationCenterRepository
					.findByRegCenterIdAndIsDeletedFalseOrNull(uniqueId);
			if (renRegistrationCenters.isEmpty()) {
				// for the given ID, we don't have data in primary language
				throw new RequestException(RegistrationCenterErrorCode.REGISTRATION_CENTER_ID.getErrorCode(),
						String.format(RegistrationCenterErrorCode.REGISTRATION_CENTER_ID.getErrorMessage(), uniqueId));
			}
		}
		return uniqueId;
	}

	// ----------------------------update operation all methods-------------------
	
	
	
	//  method to validate Put request DTOs, compare data
		public void validatePutRequest(List<RegCenterPutReqDto> regCenterPutReqDtos,
				List<RegCenterPutReqDto> notUpdRegistrationCenterList, List<String> inputIdList,
				List<String> idLangList, List<String> langList, List<ServiceError> errors) {
//			for (RegCenterPutReqDto regCenterDto : regCenterPutReqDtos) {
//				// method to compare Id
//				validateCenterId(regCenterPutReqDtos.get(0), regCenterDto);
//				// method to compare IsActive
//				validateCenterIsActive(regCenterPutReqDtos.get(0), regCenterDto, errors);
//				//called a method to compare PerKioskProcessTimed
//				validatePerKioskProcessTime(regCenterPutReqDtos.get(0), regCenterDto, errors);
//				//called a method to compare data
//				isValid(regCenterPutReqDtos.get(0), regCenterDto, errors);
//				//called a method to validate the format of the longitude and latitude, zone validation,
//				// lunch and center start and end time
//				validateRegCenterCreateReq(regCenterDto, errors);
//				inputIdList.add(regCenterDto.getId());
//				langList.add(regCenterDto.getLangCode());
//				idLangList.add(regCenterDto.getLangCode() + regCenterDto.getId());
//				if (!errors.isEmpty())
//					//if found error then add to notUpdRegistrationCenterList
//					notUpdRegistrationCenterList.add(regCenterDto);
//
//			}
		}
		
		//  method to compare ID 
		public boolean validateCenterId(RegCenterPutReqDto firstObj,
				RegCenterPutReqDto eachRecord) {
			if (eachRecord.getId().trim().equalsIgnoreCase(firstObj.getId().trim())) {
				return true;
			} else {
				throw new RequestException(RegistrationCenterErrorCode.ID_NOT_UNIQUE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.ID_NOT_UNIQUE.getErrorMessage(), eachRecord.getId()));
			}
		}

		// method to compare IsActive 
		public boolean validateCenterIsActive(RegCenterPutReqDto firstObj,
				RegCenterPutReqDto eachRecord, List<ServiceError> errors) {
			if (eachRecord.getIsActive() != null && firstObj.getIsActive() != null) {
				if (eachRecord.getIsActive().equals(firstObj.getIsActive())) {
					return firstObj.getIsActive();
				} else {
					errors.add(new ServiceError(RegistrationCenterErrorCode.IS_ACTIVE_NOT_UNIQUE.getErrorCode(),
							String.format(RegistrationCenterErrorCode.IS_ACTIVE_NOT_UNIQUE.getErrorMessage(),
									eachRecord.getIsActive())));
				}
			}
			return false;

		}

		// method to compare PerKioskProcessTime
		private <T extends RegcenterBaseDto, D extends RegcenterBaseDto> boolean validatePerKioskProcessTime(T firstObj,
				D eachRecord, List<ServiceError> errors) {
			if (eachRecord.getPerKioskProcessTime() != null && firstObj.getPerKioskProcessTime() != null) {
				if (eachRecord.getPerKioskProcessTime().equals(firstObj.getPerKioskProcessTime())) {
					return true;
				} else {
					errors.add(new ServiceError(RegistrationCenterErrorCode.PERKIOSKPROCESSTIME_NOT_UNIQUE.getErrorCode(),
							String.format(RegistrationCenterErrorCode.PERKIOSKPROCESSTIME_NOT_UNIQUE.getErrorMessage(),
									eachRecord.getPerKioskProcessTime())));

				}
			}
			return false;
		}

	// validate for the given ID, do we have records in all supported languages then make True for all records.
	public void isActiveTrueAllSupLang(List<RegCenterPutReqDto> registrationCenterPutReqAdmDto) {
		if (registrationCenterPutReqAdmDto.get(0).getIsActive() != null
				&& registrationCenterPutReqAdmDto.get(0).getIsActive()) {
			// call method to check isActive is true already for the given object
			//isActiveTrueAlreadyValidator(registrationCenterPutReqAdmDto);

			//for the given ID , records are in all supported language or not
			List<RegistrationCenter> renRegistrationCenterList = registrationCenterRepository
					.findByRegCenterIdAndIsDeletedFalseOrNull(registrationCenterPutReqAdmDto.get(0).getId());
			Set<String> languageCodeDb = renRegistrationCenterList.stream().map(regObj -> regObj.getLangCode())
					.collect(Collectors.toSet());
			
			if (languageCodeDb.containsAll(supportedLanguages)) {
				
				// to update isActive for
				renRegistrationCenterList.forEach(i -> {
					i.setIsActive(true);
					i.setUpdatedBy(MetaDataUtils.getContextUser());
					i.setUpdatedDateTime(MetaDataUtils.getCurrentDateTime());

					RegistrationCenterHistory registrationCenterHistory = new RegistrationCenterHistory();
					MapperUtils.map(i, registrationCenterHistory);
					MapperUtils.setBaseFieldValue(i, registrationCenterHistory);
					registrationCenterHistory.setEffectivetimes(i.getUpdatedDateTime());
					registrationCenterHistory.setUpdatedDateTime(i.getUpdatedDateTime());
					registrationCenterHistoryRepository.create(registrationCenterHistory);
				});

				registrationCenterRepository.saveAll(renRegistrationCenterList);

			} else {
				//for the given ID , records are not in all supported language
				throw new RequestException(RegistrationCenterErrorCode.ID_LANGUAGE.getErrorCode(),
						RegistrationCenterErrorCode.ID_LANGUAGE.getErrorMessage());
			}

		}
	}

	// call a method to validate isActive is already true 
	public void isActiveTrueAlreadyValidator(List<RegCenterPutReqDto> registrationCenterPutReqAdmDto) {
		RegistrationCenter renRegistrationCenter = registrationCenterRepository.findByIdAndLangCodeAndIsDeletedTrue(
				registrationCenterPutReqAdmDto.get(0).getId(), registrationCenterPutReqAdmDto.get(0).getLangCode());
		if (renRegistrationCenter.getIsActive() != null && renRegistrationCenter.getIsActive()) {
			throw new RequestException(RegistrationCenterErrorCode.IS_ACTIVE.getErrorCode(),
					RegistrationCenterErrorCode.IS_ACTIVE.getErrorMessage());
		}
	}

	// call a method while updating to created new recored for the ID and Language
	// which is not
	// there in DB
	public List<RegistrationCenterExtnDto> createRegCenterPut(List<RegistrationCenter> newregistrationCenterList,
			RegistrationCenter registrationCenterEntity, RegCenterPutReqDto registrationCenterDto) {
		List<RegistrationCenterExtnDto> newrRegistrationCenterDtoList;
		RegistrationCenterHistory registrationCenterHistoryEntity;
		RegistrationCenter registrationCenter;
		registrationCenterEntity.setId(registrationCenterDto.getId());

		registrationCenter = registrationCenterRepository.create(registrationCenterEntity);

		newregistrationCenterList.add(registrationCenter);

		// creating registration center history
		registrationCenterHistoryEntity = MetaDataUtils.setCreateMetaData(registrationCenterEntity,
				RegistrationCenterHistory.class);
		registrationCenterHistoryEntity.setEffectivetimes(registrationCenterEntity.getCreatedDateTime());
		registrationCenterHistoryEntity.setCreatedDateTime(registrationCenterEntity.getCreatedDateTime());
		registrationCenterHistoryRepository.create(registrationCenterHistoryEntity);

		newrRegistrationCenterDtoList = MapperUtils.mapAll(newregistrationCenterList, RegistrationCenterExtnDto.class);
		return newrRegistrationCenterDtoList;
	}
	
	
	@Autowired
	MachineIdGenerator<String> machineIdGenerator;

	@Autowired
	MachineRepository machineRepository;

	// call method generate ID or validate with DB
	public String generateMachineIdOrvalidateWithDB(String uniqueId) {
		if (uniqueId.isEmpty()) {
			// Get Machine Id by calling MachineIdGenerator API
			uniqueId = machineIdGenerator.generateMachineId();
		} else {
			List<Machine> renMachine = machineRepository
					.findMachineByIdAndIsDeletedFalseorIsDeletedIsNullNoIsActive(uniqueId);
			if (renMachine.isEmpty()) {
				// for the given ID, we don't have data in primary language
				throw new RequestException(MachineErrorCode.MACHINE_ID.getErrorCode(),
						String.format(MachineErrorCode.MACHINE_ID.getErrorMessage(), uniqueId));
			}
		}
		return uniqueId;
	}

}
