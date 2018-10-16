package io.mosip.authentication.service.impl.indauth.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.PersonalAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalFullAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDTO;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * 
 *
 * @author Rakesh Roshan
 */
@Component
public class DemoValidator implements Validator {

	private MosipLogger mosipLogger;

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

		String primaryLanguage = authRequestdto.getPii().getDemo().getLangPri();
		String secondaryLanguage = authRequestdto.getPii().getDemo().getLangSec();

		if (primaryLanguage != null) {
			checkValidPrimaryLanguageCode(primaryLanguage, errors);
		}
		if (secondaryLanguage != null) {
			checkValidSecondaryLanguageCode(secondaryLanguage, errors);
		}

		completeAddressValidation(authRequestdto, errors);

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

			String primaryLanguage = authRequestdto.getPii().getDemo().getLangPri();
			String secondaryLanguage = authRequestdto.getPii().getDemo().getLangSec();
			PersonalFullAddressDTO personalFullAddressDTO = authRequestdto.getPii().getDemo()
					.getFad();

			if (primaryLanguage == null && secondaryLanguage == null) {
				mosipLogger.error("SessionID56", "personalFullAddressDTO",
						"Select one of language for Full Address Validation",
						"Atleast select one of language-type for full address");
				errors.reject(IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST.getErrorMessage());
			}

			if ((personalFullAddressDTO.getAddrPri() != null
					|| personalFullAddressDTO.getMsPri() != null && personalFullAddressDTO.getMtPri() != null) && primaryLanguage == null) {

				mosipLogger.error("SessionID12", "personal Full Address",
						"Full Address Validation for primary language",
						"At least one attribute of full address should be present");
				errors.reject(IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST_PRI.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_FULL_ADDRESS_REQUEST_PRI.getErrorMessage());

			}

			if ((personalFullAddressDTO.getAddrSec() != null
					|| personalFullAddressDTO.getMsSec() != null || personalFullAddressDTO.getMtSec() != null) && secondaryLanguage == null) {

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
			PersonalAddressDTO personalAddressDTO = authRequestdto.getPii().getDemo()
					.getAd();

			String primaryLanguage = authRequestdto.getPii().getDemo().getLangPri();
			String secondaryLanguage = authRequestdto.getPii().getDemo().getLangSec();

			if (primaryLanguage == null && secondaryLanguage == null) {
				mosipLogger.error("SessionID635837", "Personal Address", "Address Validation for secondary language",
						"At least one attribute of  address should be present");
				errors.reject(IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST.getErrorMessage());
			}

			if ((personalAddressDTO.getAddrLine1Pri() != null
					|| personalAddressDTO.getAddrLine2Pri() != null || personalAddressDTO.getAddrLine3Pri() != null
					|| personalAddressDTO.getCountryPri() != null || personalAddressDTO.getPinCodePri() != null) && primaryLanguage == null) {

				mosipLogger.error("SessionID54645", "Personal Address for primary language",
						"Address Validation for primary language",
						"Atleast one attribute of  address should be present");
				errors.reject(IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_PRI.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_PRI.getErrorMessage());

			}

			if ((personalAddressDTO.getAddrLine1Sec() != null
					|| personalAddressDTO.getAddrLine2Sec() != null || personalAddressDTO.getAddrLine3Sec() != null
					|| personalAddressDTO.getCountrySec() != null || personalAddressDTO.getPinCodeSec() != null) && secondaryLanguage != null) {

				mosipLogger.error("SessionID9865", "Personal Address for secondary language",
						"Address Validation for secondary language",
						"At least one attribute of  address should be present");
				errors.reject(IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_SEC.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_ADDRESS_REQUEST_SEC.getErrorMessage());

			}
		}
	}

	/**
	 * Validate personal information.
	 * 
	 * @param authRequestdto
	 * @param errors
	 */
	private void personalIdentityValidation(AuthRequestDTO authRequestdto, Errors errors) {

		PersonalIdentityDTO personalIdentityDTO = authRequestdto.getPii().getDemo()
				.getPi();

		if (authRequestdto.getAuthType().isPi()) {

			// TODO dobType integrate with CK
			String primaryLanguage = authRequestdto.getPii().getDemo().getLangPri();
			String secondaryLanguage = authRequestdto.getPii().getDemo().getLangSec();

			if (personalIdentityDTO.getDob() != null) {
				try {
					dobValidation(authRequestdto, errors);
				} catch (ParseException e) {
					mosipLogger.error("sessionID-NA", "ParseException",
							 e.getCause() == null ? "" : e.getCause().getMessage(), e.getMessage());
				}
			}
			if (isAllPINull(personalIdentityDTO)) {
				mosipLogger.error("SessionID123", "Personal info", "personal info should be present",
						"At least select one valid personal info");
				errors.reject(IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION.getErrorMessage());
			}
			if (personalIdentityDTO.getNamePri() != null && primaryLanguage == null) {
				mosipLogger.error("SessionIDPri", "Personal identity", "personal info for secondary language",
						"Primary Language code (langPri) should be present ");
				errors.reject(IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_PRI.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_PRI.getErrorMessage());
			}
			if (personalIdentityDTO.getNameSec() != null && secondaryLanguage == null) {
				mosipLogger.error("SessionIDSec", "Personal identity", "personal info for secondary language",
						"Primary Language code (langSec) should be present ");
				errors.reject(IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_SEC.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_PERSONAL_INFORMATION_SEC.getErrorMessage());
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
	private void dobValidation(AuthRequestDTO authRequestdto, Errors errors) throws ParseException {

		String pidob = authRequestdto.getPii().getDemo().getPi().getDob();
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

	/**
	 * Verify valid language code for primary language.
	 * 
	 * @param primaryLanguage
	 * @param errors
	 */
	private void checkValidPrimaryLanguageCode(String primaryLanguage, Errors errors) {
		Locale locale = new Locale.Builder().setLanguageTag(primaryLanguage).build();
		if (!LocaleUtils.isAvailableLocale(locale)) {
			mosipLogger.error("SessionID", "Secondary Language", "code for secondary language",
					"Valid secondary Language code  should be present ");
			errors.reject(IdAuthenticationErrorConstants.INVALID_PRIMARY_LANGUAGE_CODE.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_PRIMARY_LANGUAGE_CODE.getErrorMessage());
		}

	}

	/**
	 * Verify valid language code for secondary language.
	 * 
	 * @param secondaryLanguage
	 * @param errors
	 */
	private void checkValidSecondaryLanguageCode(String secondaryLanguage, Errors errors) {
		Locale locale = new Locale.Builder().setLanguageTag(secondaryLanguage).build();
		if (!LocaleUtils.isAvailableLocale(locale)) {
			mosipLogger.error("SessionID", "Secondary Language", "code for secondary language",
					"Valid secondary Language code  should be present ");
			errors.reject(IdAuthenticationErrorConstants.INVALID_SECONDARY_LANGUAGE_CODE.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_SECONDARY_LANGUAGE_CODE.getErrorMessage());
		}

	}

}
