package io.mosip.kernel.masterdata.utils;

import java.util.ArrayList;
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
import io.mosip.kernel.masterdata.constant.RegistrationCenterErrorCode;
import io.mosip.kernel.masterdata.constant.ValidationErrorCode;
import io.mosip.kernel.masterdata.dto.RegCenterPostReqPrimAdmDto;
import io.mosip.kernel.masterdata.dto.RegcenterBaseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterPutReqAdmDto;
import io.mosip.kernel.masterdata.dto.postresponse.RegistrationCenterPostResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.exception.ValidationException;

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
       * Constructing regex for matching the Latitude and Longitude format
       */

       @PostConstruct
       public void constructRegEx() {
             negRegex = "^(\\-\\d{1,2}\\.\\d{" + minDegits + ",})$";
             posRegex = "^(\\d{1,2}\\.\\d{" + minDegits + ",})$";
       }

       private static final int ONE = 1;
       private static final int TEN = 10;

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

       public boolean validateCenterId(RegistrationCenterPutReqAdmDto firstObj, RegistrationCenterPutReqAdmDto eachRecord) {
             if (eachRecord.getId().trim().equalsIgnoreCase(firstObj.getId().trim())) {
                    return true;
             } else {
                    throw new RequestException(RegistrationCenterErrorCode.ID_NOT_UNIQUE.getErrorCode(),
                          String.format(RegistrationCenterErrorCode.ID_NOT_UNIQUE.getErrorMessage(), eachRecord.getId()));
             }
       }

       public boolean validateCenterIsActive(RegistrationCenterPutReqAdmDto firstObj,
                    RegistrationCenterPutReqAdmDto eachRecord, List<ServiceError> errors) {
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
       public <T extends RegcenterBaseDto, D extends RegcenterBaseDto> void validateRegCenterCreateReq(
                    T registrationCenterDto, List<ServiceError> errors) {

             String latitude = registrationCenterDto.getLatitude();
             String longitude = registrationCenterDto.getLongitude();

             zoneUserMapValidation(registrationCenterDto, errors, getZoneIdsForUser());
              zoneStartEndTimeGtrValidation(registrationCenterDto, errors);
              lunchStartEndTimeGrtValidation(registrationCenterDto, errors);
             formatValidationLongitudeLatitude(errors, latitude, longitude);

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
       private <T extends RegcenterBaseDto> void zoneUserMapValidation(T registrationCenterDto, List<ServiceError> errors,
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
       private <T extends RegcenterBaseDto> void lunchStartEndTimeGrtValidation(T registrationCenterDto,
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
       private <T extends RegcenterBaseDto> void zoneStartEndTimeGtrValidation(T registrationCenterDto,
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
       public void validatePrimarySencodaryLangMandatoryFields(List<RegCenterPostReqPrimAdmDto> reqRegistrationCenterDto,
                    RegistrationCenterPostResponseDto registrationCenterPostResponseDto, List<String> inputLangCodeList,
                    List<RegCenterPostReqPrimAdmDto> validateRegistrationCenterDtos,
                    List<RegCenterPostReqPrimAdmDto> constraintViolationedSecList, List<ServiceError> errors) {
             List<ServiceError> secErrors = new ArrayList<>();
             RegCenterPostReqPrimAdmDto firstObject = null;
             
             Optional<RegCenterPostReqPrimAdmDto> defualtLangVal = reqRegistrationCenterDto.stream().filter(i-> i.getLangCode().equals(primaryLang)).findAny();

             if(!defualtLangVal.isPresent()) {
                    throw new RequestException(
                              RegistrationCenterErrorCode.DEFAULT_LANGUAGE.getErrorCode(),
                          RegistrationCenterErrorCode.DEFAULT_LANGUAGE.getErrorMessage());
             }
             
             for (RegCenterPostReqPrimAdmDto registrationCenterDto : reqRegistrationCenterDto) {
                    if(registrationCenterDto.getLangCode() != null && registrationCenterDto.getLangCode().equals(primaryLang)){
                          firstObject=registrationCenterDto;
                    }else if ((registrationCenterDto.getLangCode() != null)
                                 && (secondaryLangList.contains(registrationCenterDto.getLangCode()))) { 
                           firstObject=reqRegistrationCenterDto.get(0);
                    }
             }
             
              
             for (RegCenterPostReqPrimAdmDto registrationCenterDto : reqRegistrationCenterDto) {

                    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                    Validator validator = factory.getValidator();
                    
                    if ((registrationCenterDto.getLangCode() != null)
                                 && (registrationCenterDto.getLangCode().equalsIgnoreCase(primaryLang))) {
                          
                           Set<ConstraintViolation<RegCenterPostReqPrimAdmDto>> constraintViolations = validator
                                        .validate(registrationCenterDto);

                           primaryLanguageValidation(validateRegistrationCenterDtos, errors, registrationCenterDto,
                                       constraintViolations);

                    } else if ((registrationCenterDto.getLangCode() != null)
                                 && (secondaryLangList.contains(registrationCenterDto.getLangCode()))) {
                          
                           secondaryLangValidation(reqRegistrationCenterDto, registrationCenterPostResponseDto,
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

       private void primaryLanguageValidation(List<RegCenterPostReqPrimAdmDto> validateRegistrationCenterDtos,
                    List<ServiceError> errors, RegCenterPostReqPrimAdmDto registrationCenterDto,
                    Set<ConstraintViolation<RegCenterPostReqPrimAdmDto>> constraintViolations) {
             if (!constraintViolations.isEmpty()) {
                    constraintViolationIterator(errors, registrationCenterDto, constraintViolations);
                    if (!errors.isEmpty())
                          throw new ValidationException(errors);
             } else {
                    // call method to validate Zone-Id, longitude and latitude
                    validateRegCenterCreateReq(registrationCenterDto, errors);
                    if (!errors.isEmpty()) {
                          throw new ValidationException(errors);
                    }
                    // add primary language Object to list after all validation are true
                   validateRegistrationCenterDtos.add(registrationCenterDto);
             }
       }

       // secondary language validation
       public void secondaryLangValidation(List<RegCenterPostReqPrimAdmDto> reqRegistrationCenterDto,
                    RegistrationCenterPostResponseDto registrationCenterPostResponseDto,
                    List<RegCenterPostReqPrimAdmDto> validateRegistrationCenterDtos,
                    List<RegCenterPostReqPrimAdmDto> constraintViolationedSecList, List<ServiceError> errors,
                    RegCenterPostReqPrimAdmDto registrationCenterDto, Validator validator,
                    RegCenterPostReqPrimAdmDto firstObject) {
              Set<ConstraintViolation<RegCenterPostReqPrimAdmDto>> constraintViolations = validator
                          .validate(registrationCenterDto);
             if (!constraintViolations.isEmpty()) {
                    constraintViolationIterator(errors, registrationCenterDto, constraintViolations);
                    if (!errors.isEmpty()) {
                    registrationCenterPostResponseDto.setConstraintViolationError(errors);
                    }
                    constraintViolationedSecList.add(registrationCenterDto);

             } else {
                    isValid(firstObject, registrationCenterDto, errors);
                    validateRegCenterCreateReq(registrationCenterDto, errors);
                    if (!errors.isEmpty()) {
                    registrationCenterPostResponseDto.setConstraintViolationError(errors);
                           constraintViolationedSecList.add(registrationCenterDto);
                    } else {
                          validateRegistrationCenterDtos.add(registrationCenterDto);
                    }

             }
       }

       // List constraint violation iterator
       private void constraintViolationIterator(List<ServiceError> errors,
                    RegCenterPostReqPrimAdmDto registrationCenterDto,
                    Set<ConstraintViolation<RegCenterPostReqPrimAdmDto>> constraintViolations) {

              Iterator<ConstraintViolation<RegCenterPostReqPrimAdmDto>> iterator = constraintViolations.iterator();
             while (iterator.hasNext()) {
                    ConstraintViolation<RegCenterPostReqPrimAdmDto> cv = iterator.next();
                    errors.add(new ServiceError(ValidationErrorCode.CONSTRAINT_VIOLATION.getErrorCode(),
                                ValidationErrorCode.CONSTRAINT_VIOLATION.getErrorMessage() + " for the LangCode- "
                                              + registrationCenterDto.getLangCode() + " - " + cv.getPropertyPath() + " "
                                              + cv.getMessage()));
             }
       }

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

       public void validatePutRequest(List<RegistrationCenterPutReqAdmDto> registrationCenterPutReqAdmDto,
                    List<RegistrationCenterPutReqAdmDto> notUpdRegistrationCenterList, List<String> inputIdList,
                    List<String> idLangList, List<String> langList, List<ServiceError> errors) {
             for (RegistrationCenterPutReqAdmDto regCenterDto : registrationCenterPutReqAdmDto) {
                    validateCenterId(registrationCenterPutReqAdmDto.get(0), regCenterDto);
                validateCenterIsActive(registrationCenterPutReqAdmDto.get(0), regCenterDto, errors);
             validatePerKioskProcessTime(registrationCenterPutReqAdmDto.get(0), regCenterDto, errors);
                    isValid(registrationCenterPutReqAdmDto.get(0), regCenterDto, errors);
                    validateRegCenterCreateReq(regCenterDto, errors);
                    inputIdList.add(regCenterDto.getId());
                    langList.add(regCenterDto.getLangCode());
                    idLangList.add(regCenterDto.getLangCode() + regCenterDto.getId());
                    if (!errors.isEmpty())
                           notUpdRegistrationCenterList.add(regCenterDto);
                    

             }
       }

}

