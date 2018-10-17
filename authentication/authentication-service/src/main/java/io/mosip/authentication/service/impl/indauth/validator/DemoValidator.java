package io.mosip.authentication.service.impl.indauth.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.dto.indauth.PersonalAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalFullAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDataDTO;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * Validate Demographic info of individual.
 *
 * @author Rakesh Roshan
 */
@Component
public class DemoValidator implements Validator {

	private static final int MAX_AGE = 150;

	private static final int MIN_AGE = 0;

	private MosipLogger mosipLogger;

	private static final String EMAIL_PATTERN = "^[\\_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String DOB_PATTERN = "^([0-9]{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";
	private static final String SESSION_ID = "sessionid";

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		mosipLogger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	@Autowired
	Environment env;

	@Override
	public boolean supports(Class<?> clazz) {
		return AuthRequestDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		AuthRequestDTO authRequestdto = (AuthRequestDTO) target;

		PersonalIdentityDataDTO personalDataDTO = authRequestdto.getPii();

		if (personalDataDTO != null) {
			DemoDTO demodto = personalDataDTO.getDemo();
			if (demodto != null && authRequestdto.getAuthType() != null) {

				completeAddressValidation(authRequestdto.getAuthType(), demodto, errors);

				personalIdentityValidation(authRequestdto.getAuthType(), demodto, errors);

			}
		}

	}

	/**
	 * Address validation for Full Address and Address
	 * 
	 * @param authRequestdto
	 * @param errors
	 */
	private void completeAddressValidation(AuthTypeDTO authType, DemoDTO demodto, Errors errors) {

		/** Address and Full Address both should not be include together */
		if (authType.isAd() && authType.isFad()) {

			mosipLogger.error(SESSION_ID, "DemoValidator", "Address Validation",
					"Address and Full address are mutually exclusive");
			errors.reject(IdAuthenticationErrorConstants.AD_FAD_MUTUALLY_EXCULUSIVE.getErrorCode(),
					IdAuthenticationErrorConstants.AD_FAD_MUTUALLY_EXCULUSIVE.getErrorMessage());
		} else if (authType.isFad()) {
			fullAddressValidation(authType, demodto, errors);

		} else if (authType.isAd()) {
			addressValidation(authType, demodto, errors);
		}
	}

	/**
	 * Full address validation Valid values are “true” or “false”. If the value is
	 * “true” then at least one attribute of element “fad” should be used in
	 * authentication.
	 * 
	 * @param authRequestdto
	 * @param errors
	 */
	// TODO detect input text language and match with configurable language
	private void fullAddressValidation(AuthTypeDTO authType, DemoDTO demodto, Errors errors) {

		PersonalFullAddressDTO personalFullAddressDTO = demodto.getFad();

		if (authType.isFad() && personalFullAddressDTO != null) {

			if (personalFullAddressDTO.getAddrPri() == null && personalFullAddressDTO.getAddrSec() == null) { // &&
																												// primaryLanguage
																												// ==null

				mosipLogger.error(SESSION_ID, "personal Full Address", "Full Address Validation for primary language",
						"At least one attribute of full address should be present");
				errors.reject(IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST.getErrorMessage());

			}

		}

	}

	/**
	 * Address validation Valid values are “true” or “false”. If the value is “true”
	 * then at least one attribute of element “fad” should be used in
	 * authentication.
	 * 
	 * @param authRequestdto
	 * @param errors
	 */
	// TODO detect input text language and match with configurable language
	private void addressValidation(AuthTypeDTO authType, DemoDTO demodto, Errors errors) {

		PersonalAddressDTO personalAddressDTO = demodto.getAd();
		if ((personalAddressDTO.getAddrLine1Pri() == null 
				&& personalAddressDTO.getAddrLine2Pri() == null
				&& personalAddressDTO.getAddrLine3Pri() == null 
				&& personalAddressDTO.getCityPri() == null
				&& personalAddressDTO.getStatePri() == null
				&& personalAddressDTO.getCountryPri() == null
				&& personalAddressDTO.getPinCodePri() == null)
				&&
				(personalAddressDTO.getAddrLine1Sec() == null 
				&& personalAddressDTO.getAddrLine2Sec() == null
				&& personalAddressDTO.getAddrLine3Sec() == null
				&& personalAddressDTO.getCitySec() == null
				&& personalAddressDTO.getStateSec() == null
				&& personalAddressDTO.getCountrySec() == null
				&& personalAddressDTO.getPinCodeSec() == null)) {

			mosipLogger.error(SESSION_ID, "Personal Address",
					"Address Validation",
					"Atleast one attribute for address should be present");
			errors.reject(IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST.getErrorMessage());

		} 
	}

	/**
	 * Validate personal information.
	 * 
	 * @param authRequestdto
	 * @param errors
	 */
	// TODO detect input text language and match with configurable language
	private void personalIdentityValidation(AuthTypeDTO authType, DemoDTO demodto, Errors errors) {

		PersonalIdentityDTO personalIdentityDTO = demodto.getPi();

		if (authType.isPi() && personalIdentityDTO != null) {

			if (isAllPINull(personalIdentityDTO)) {
				mosipLogger.error(SESSION_ID, "Personal info", "personal info should be present",
						"At least select one valid personal info");
				errors.reject(IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION.getErrorMessage());
			} else {
				if (personalIdentityDTO.getDob() != null) {
					try {
						dobValidation(personalIdentityDTO.getDob(), env.getProperty("date.pattern"), errors);
					} catch (ParseException e) {
						mosipLogger.error(SESSION_ID, "ParseException",
								e.getCause() == null ? "" : e.getCause().getMessage(), e.getMessage());
					}
				}

				if (personalIdentityDTO.getAge() != null) {
					checkAge(personalIdentityDTO.getAge(), errors);
				}
				if (personalIdentityDTO.getGender() != null) {
					checkGender(personalIdentityDTO.getGender(), errors);
				}
				if (personalIdentityDTO.getMsPri() != null) {
					checkMatchStrategy(personalIdentityDTO.getMsPri(), "msPri", errors);
				}
				if (personalIdentityDTO.getMsSec() != null) {
					checkMatchStrategy(personalIdentityDTO.getMsSec(), "msSec", errors);
				}
				if (personalIdentityDTO.getMtPri() != null) {
					checkMatchThresold(personalIdentityDTO.getMtPri(), "mtPri", errors);
				}
				if (personalIdentityDTO.getMtSec() != null) {
					checkMatchThresold(personalIdentityDTO.getMtSec(), "mtSec", errors);
				}
				if (personalIdentityDTO.getPhone() != null) {
					checkPhoneNumber(personalIdentityDTO.getPhone(), errors);
				}
				if (personalIdentityDTO.getEmail() != null) {
					checkEmail(personalIdentityDTO.getEmail(), errors);
				}
			}

		}
	}

	/**
	 * Dob validation with pattern
	 * 
	 * @param authRequestdto
	 * @param errors
	 * @throws ParseException
	 */
	private void dobValidation(String dobToValidate, String dateFromat, Errors errors) throws ParseException {

		Pattern pattern = Pattern.compile(DOB_PATTERN);
		Matcher matcher = pattern.matcher(dobToValidate);

		if (matcher.matches()) {
			String dateOfBirth = dobToValidate;
			SimpleDateFormat formatter = new SimpleDateFormat(dateFromat);
			Date dob = formatter.parse(dateOfBirth);
			Instant instantDob = dob.toInstant();

			Instant now = Instant.now();
			if (instantDob.isAfter(now)) {
				errors.rejectValue("pii", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "dob"));
			}
		} else {
			errors.rejectValue("pii", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "dob"));
		}

	}

	private void checkMatchStrategy(String matchStrategy, String ms, Errors errors) {

		if (!matchStrategy.equals("E") && !matchStrategy.equals("P") && !matchStrategy.equals("PH")) {
			errors.rejectValue("pii", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), ms));
		}

	}

	private void checkMatchThresold(Integer matchThresold, String mt, Errors errors) {

		if (matchThresold.intValue() < 1 || matchThresold.intValue() > 100) {
			errors.rejectValue("pii", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), mt));
		}
	}

	private void checkGender(String gender, Errors errors) {

		if (!gender.equals("M") && !gender.equals("F") && !gender.equals("T")) {
			errors.rejectValue("pii", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "gender"));
		}
	}

	private void checkAge(Integer age, Errors errors) {
		if (age.intValue() < MIN_AGE || age.intValue() > MAX_AGE) {
			errors.rejectValue("pii", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "age"));
		}
	}

	private void checkPhoneNumber(String phone, Errors errors) {
		if (phone == null || phone.isEmpty()) {
			errors.rejectValue("pii", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "phone"));
		}
	}

	private void checkEmail(String email, Errors errors) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);

		if (!matcher.matches()) {
			errors.rejectValue("pii", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "email"));
		}
	}

	private boolean isAllPINull(PersonalIdentityDTO personalIdentityDTO) {

		return isAllNull(personalIdentityDTO, PersonalIdentityDTO::getNamePri, PersonalIdentityDTO::getNameSec,
				PersonalIdentityDTO::getAge, PersonalIdentityDTO::getDob, PersonalIdentityDTO::getEmail,
				PersonalIdentityDTO::getGender, PersonalIdentityDTO::getPhone);
	}

	@SafeVarargs
	private static <T> boolean isAllNull(T obj, Function<T, Object>... funcs) {
		return Stream.of(funcs).allMatch(func -> func.apply(obj) == null);
	}

}
