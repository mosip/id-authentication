package io.mosip.authentication.service.impl.indauth.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.spi.idauth.demo.PersonalAddressDTO;
import io.mosip.authentication.core.spi.idauth.demo.PersonalFullAddressDTO;
import io.mosip.authentication.core.spi.idauth.demo.PersonalIdentityDTO;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * 
 *
 * @author Rakesh Roshan
 */
@Component
@PropertySource("classpath:application-local.properties")
public class DemoValidator implements Validator {

	private MosipLogger mosipLogger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		mosipLogger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	@Autowired
	private SpringValidatorAdapter validator;

	@Autowired
	Environment env;

	@Override
	public boolean supports(Class<?> clazz) {
		return DemoDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		AuthRequestDTO authRequestdto = (AuthRequestDTO) target;
		DemoDTO demodto = authRequestdto.getPersonalDataDTO().getDemoDTO();

		// javax.validator constraint validation
		//validator.validate(demodto, errors);
		// address validation
		completeAddressValidation(authRequestdto, errors);

		// PID validation
		personalIdentityValidation(authRequestdto, errors);

	}

	/**
	 * Address validation for Full Address and Address
	 * 
	 * @param authRequestdto
	 * @param errors
	 */
	private void completeAddressValidation(AuthRequestDTO authRequestdto, Errors errors) {

		/** Address and Full Address both should not be include together */
		if (authRequestdto.getAuthType().isAd() && authRequestdto.getAuthType().isFad()) {

			mosipLogger.error("SessionID NA", "DemoValidator", "Address Validation",
					"Address and Full address are mutually exclusive");
			errors.reject(IdAuthenticationErrorConstants.AD_FAD_MUTUALLY_EXCULUSIVE.getErrorCode(),
					IdAuthenticationErrorConstants.AD_FAD_MUTUALLY_EXCULUSIVE.getErrorMessage());
		} else if (authRequestdto.getAuthType().isFad()) {
			fullAddressValidation(authRequestdto, errors);

		} else if (authRequestdto.getAuthType().isAd()) {
			addressValidation(authRequestdto, errors);
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
	private void fullAddressValidation(AuthRequestDTO authRequestdto, Errors errors) {

		if (authRequestdto.getAuthType().isFad()) {

			String primaryLanguage = authRequestdto.getPersonalDataDTO().getDemoDTO().getLangPri();
			String secondaryLanguage = authRequestdto.getPersonalDataDTO().getDemoDTO().getLangSec();

			if (primaryLanguage == null && secondaryLanguage == null) {
				mosipLogger.error("SessionID56", "personalFullAddressDTO",
						"Select one of language for Full Address Validation",
						"Atleast select one of language-type for full address");
				errors.reject(IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST.getErrorMessage());
			}

			PersonalFullAddressDTO personalFullAddressDTO = authRequestdto.getPersonalDataDTO().getDemoDTO()
					.getPersonalFullAddressDTO();

			if (primaryLanguage != null && (personalFullAddressDTO.getAddrPri() == null
					&& personalFullAddressDTO.getMsPri() == null && personalFullAddressDTO.getMtPri() == null)) {

				mosipLogger.error("SessionID12", "personal Full Address",
						"Full Address Validation for primary language",
						"At least one attribute of full address should be present");
				errors.reject(IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST_PRI.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST_PRI.getErrorMessage());

			}

			if (secondaryLanguage != null && (personalFullAddressDTO.getAddrSec() == null
					&& personalFullAddressDTO.getMsSec() == null && personalFullAddressDTO.getMtSec() == null)) {

				mosipLogger.error("SessionID34", "personal Full Address",
						"Full Address Validation for secondary language",
						"At least one attribute of full address should be present");
				errors.reject(IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST_SEC.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST_SEC.getErrorMessage());
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
	private void addressValidation(AuthRequestDTO authRequestdto, Errors errors) {

		if (authRequestdto.getAuthType().isAd()) {
			PersonalAddressDTO personalAddressDTO = authRequestdto.getPersonalDataDTO().getDemoDTO()
					.getPersonalAddressDTO();

			String primaryLanguage = authRequestdto.getPersonalDataDTO().getDemoDTO().getLangPri();
			String secondaryLanguage = authRequestdto.getPersonalDataDTO().getDemoDTO().getLangSec();

			if (primaryLanguage == null && secondaryLanguage == null) {
				mosipLogger.error("SessionID635837", "Personal Address", "Address Validation for secondary language",
						"At least one attribute of  address should be present");
				errors.reject(IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST.getErrorMessage());
			}

			if (primaryLanguage != null && (personalAddressDTO.getAddrLine1Pri() == null
					&& personalAddressDTO.getAddrLine2Pri() == null && personalAddressDTO.getAddrLine3Pri() == null
					&& personalAddressDTO.getCountryPri() == null && personalAddressDTO.getPinCodePri() == null)) {

				mosipLogger.error("SessionID54645", "Personal Address for primary language",
						"Address Validation for primary language",
						"Atleast one attribute of  address should be present");
				errors.reject(IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_PRI.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_PRI.getErrorMessage());

			}

			if (secondaryLanguage != null && (personalAddressDTO.getAddrLine1Sec() == null
					&& personalAddressDTO.getAddrLine2Sec() == null && personalAddressDTO.getAddrLine3Sec() == null
					&& personalAddressDTO.getCountrySec() == null && personalAddressDTO.getPinCodeSec() == null)) {

				mosipLogger.error("SessionID9865", "Personal Address for secondary language",
						"Address Validation for secondary language",
						"At least one attribute of  address should be present");
				errors.reject(IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_SEC.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_SEC.getErrorMessage());

			}
		}
	}

	/**
	 * 
	 * @param authRequestdto
	 * @param errors
	 */
	private void personalIdentityValidation(AuthRequestDTO authRequestdto, Errors errors) {

		PersonalIdentityDTO personalIdentityDTO = authRequestdto.getPersonalDataDTO().getDemoDTO()
				.getPersonalIdentityDTO();

		if (authRequestdto.getAuthType().isPi()) {

			// TODO dobType integrate with CK

			if (personalIdentityDTO.getDob() != null) {
				try {
					dobValidation(authRequestdto, errors);
				} catch (ParseException e) {
					mosipLogger.error("sessionID-NA", "ParseException", e.getCause().toString(), e.getMessage());
				}
			}

			if (isAllPINull(personalIdentityDTO)) {
				mosipLogger.error("SessionID123", "Personal info", "personal info should be present",
						"At least select one valid personal info");
				errors.reject(IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION.getErrorMessage());
			}
			String primaryLanguage = authRequestdto.getPersonalDataDTO().getDemoDTO().getLangPri();
			if (personalIdentityDTO.getNamePri() != null && primaryLanguage == null) {
				mosipLogger.error("SessionID", "Personal identity", "personal info for secondary language",
						"Primary Language code (langPri) should be present ");
				errors.reject(IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_PRI.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_PRI.getErrorMessage());

			}

			String secondaryLanguage = authRequestdto.getPersonalDataDTO().getDemoDTO().getLangSec();
			if (personalIdentityDTO.getNameSec() != null && secondaryLanguage == null) {
				mosipLogger.error("SessionID", "Personal identity", "personal info for secondary language",
						"Primary Language code (langSec) should be present ");
				errors.reject(IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_SEC.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_SEC.getErrorMessage());

			}
		}

	}

	private void dobValidation(AuthRequestDTO authRequestdto, Errors errors) throws ParseException {

		String pidob = authRequestdto.getPersonalDataDTO().getDemoDTO().getPersonalIdentityDTO().getDob();
		SimpleDateFormat formatter = new SimpleDateFormat(env.getProperty("date.pattern"));
		Date dob = formatter.parse(pidob);
		Instant instantDob = dob.toInstant();

		Instant now = Instant.now();

		if (instantDob.isAfter(now)) {
			errors.reject(IdAuthenticationErrorConstants.INVALID_DOB_YEAR.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_DOB_YEAR.getErrorMessage());
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
