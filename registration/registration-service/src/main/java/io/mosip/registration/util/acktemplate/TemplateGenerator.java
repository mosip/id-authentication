package io.mosip.registration.util.acktemplate;

import static io.mosip.registration.constants.LoggerConstants.LOG_TEMPLATE_GENERATOR;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.ValuesDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;

/**
 * Generates Velocity Template for the creation of acknowledgement
 * 
 * @author Himaja Dhanyamraju
 *
 */
public class TemplateGenerator extends BaseService {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(TemplateGenerator.class);

	protected ApplicationContext applicationContext = ApplicationContext.getInstance();

	/**
	 * @param templateText
	 *            - string which contains the data of template that is used to
	 *            generate acknowledgement
	 * @param registration
	 *            - RegistrationDTO to display required fields on the template
	 * @return writer - After mapping all the fields into the template, it is
	 *         written into a StringWriter and returned
	 * @throws RegBaseCheckedException
	 */
	public ResponseDTO generateTemplate(String templateText, RegistrationDTO registration,
			TemplateManagerBuilder templateManagerBuilder) {

		ResponseDTO response = new ResponseDTO();

		try {
			LOGGER.debug(LOG_TEMPLATE_GENERATOR, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					"generateTemplate had been called for preparing Acknowledgement Template.");

			ResourceBundle localProperties = applicationContext.getLocalLanguageProperty();
			InputStream is = new ByteArrayInputStream(templateText.getBytes());
			Map<String, Object> templateValues = new HashMap<>();
			ByteArrayOutputStream byteArrayOutputStream = null;

			templateValues.put(RegistrationConstants.TEMPLATE_REGISTRATION_ID, registration.getRegistrationId());

			SimpleDateFormat sdf = new SimpleDateFormat(RegistrationConstants.TEMPLATE_DATE_FORMAT);
			String currentDate = sdf.format(new Date());

			// map the respective fields with the values in the registrationDTO
			templateValues.put(RegistrationConstants.TEMPLATE_DATE, currentDate);
			templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME, getDemographicValueInPlatformLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getFullName().getValues()));
			String dob = registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getDateOfBirth()
					.getValue();
			if (dob == null) {
				templateValues.put(RegistrationConstants.TEMPLATE_DOB,
						registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAge());
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_DOB,
						DateUtils.formatDate(DateUtils.parseToDate(dob, "yyyy/MM/dd"), "dd-MM-YYYY"));
			}
			templateValues.put(RegistrationConstants.TEMPLATE_GENDER, getDemographicValueInPlatformLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getGender().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1,
					getDemographicValueInPlatformLanguage(registration.getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getAddressLine1().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2,
					getDemographicValueInPlatformLanguage(registration.getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getAddressLine2().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE3,
					getDemographicValueInPlatformLanguage(registration.getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getAddressLine3().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_CITY, getDemographicValueInPlatformLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getCity().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_STATE, getDemographicValueInPlatformLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getProvince().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_COUNTRY, getDemographicValueInPlatformLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getRegion().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_POSTAL_CODE,
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getPostalCode());
			templateValues.put(RegistrationConstants.TEMPLATE_MOBILE,
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getPhone().getValue());
			templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY,
					getDemographicValueInPlatformLanguage(registration.getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getLocalAdministrativeAuthority().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_CNIE_NUMBER,
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getCnieNumber());
			templateValues.put(RegistrationConstants.TEMPLATE_CNIE_LOCAL_LANG_LABEL,
					localProperties.getString("cnie_number"));

			boolean isChild = registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
					.getParentOrGuardianRIDOrUIN() != null
					&& !registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.getParentOrGuardianRIDOrUIN().isEmpty();

			if (isChild) {
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME,
						getDemographicValueInPlatformLanguage(registration.getDemographicDTO().getDemographicInfoDTO()
								.getIdentity().getParentOrGuardianName().getValues()));
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_UIN, registration.getDemographicDTO()
						.getDemographicInfoDTO().getIdentity().getParentOrGuardianRIDOrUIN());
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME_LOCAL_LANG_LABEL,
						localProperties.getString("parent_name"));
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_UIN_LOCAL_LANG_LABEL,
						localProperties.getString("parent_uin"));
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME_LOCAL_LANG,
						getDemographicValueInLocalLanguage(registration.getDemographicDTO().getDemographicInfoDTO()
								.getIdentity().getParentOrGuardianName().getValues()));
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_WITH_PARENT_DETAILS,
						RegistrationConstants.TEMPLATE_STYLE_HIDDEN_PROPERTY);
			}

			String email = registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getEmail().getValue();
			if (email == null || email == RegistrationConstants.EMPTY) {
				templateValues.put(RegistrationConstants.TEMPLATE_EMAIL, RegistrationConstants.EMPTY);
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_EMAIL, email);
			}

			StringBuffer documentsList = new StringBuffer();
			if (registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getProofOfIdentity() != null) {
				documentsList.append(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
						.getProofOfIdentity().getValue()).append(", ");
			}
			if (registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getProofOfAddress() != null) {
				documentsList.append(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
						.getProofOfAddress().getValue()).append(", ");
			}
			if (registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
					.getProofOfRelationship() != null) {
				documentsList.append(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
						.getProofOfRelationship().getValue()).append(", ");
			}
			if (registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getDateOfBirthProof() != null) {
				documentsList.append(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
						.getDateOfBirthProof().getValue());
			}

			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS, documentsList.toString());
			templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME, registration.getOsiDataDTO().getOperatorID());

			byte[] applicantImageBytes = registration.getDemographicDTO().getApplicantDocumentDTO().getPhoto();
			String applicantImageEncodedBytes = StringUtils
					.newStringUtf8(Base64.encodeBase64(applicantImageBytes, false));
			templateValues.put(RegistrationConstants.TEMPLATE_IMAGE_SOURCE,
					RegistrationConstants.TEMPLATE_IMAGE_ENCODING + applicantImageEncodedBytes);

			if (registration.getDemographicDTO().getApplicantDocumentDTO().isHasExceptionPhoto()) {
				templateValues.put(RegistrationConstants.TEMPLATE_WITH_EXCEPTION_IMAGE, null);
				templateValues.put(RegistrationConstants.TEMPLATE_WITHOUT_EXCEPTION_IMAGE,
						RegistrationConstants.TEMPLATE_STYLE_PROPERTY);
				byte[] exceptionImageBytes = registration.getDemographicDTO().getApplicantDocumentDTO()
						.getExceptionPhoto();
				String exceptionImageEncodedBytes = StringUtils
						.newStringUtf8(Base64.encodeBase64(exceptionImageBytes, false));
				templateValues.put(RegistrationConstants.TEMPLATE_EXCEPTION_IMAGE_SOURCE,
						RegistrationConstants.TEMPLATE_IMAGE_ENCODING + exceptionImageEncodedBytes);
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_WITHOUT_EXCEPTION_IMAGE, null);
				templateValues.put(RegistrationConstants.TEMPLATE_WITH_EXCEPTION_IMAGE,
						RegistrationConstants.TEMPLATE_STYLE_PROPERTY);
			}
			/*
			 * get the quality ranking for fingerprints of the applicant HashMap<String,
			 * Integer> fingersQuality = getFingerPrintQualityRanking(registration); int
			 * count=1; for (Map.Entry<String, Integer> entry : fingersQuality.entrySet()) {
			 * if (entry.getValue() != 0) { // display rank of quality for the captured
			 * fingerprints velocityContext.put(entry.getKey(), count++); } else { //
			 * display cross mark for missing fingerprints
			 * velocityContext.put(entry.getKey(),
			 * RegistrationConstants.TEMPLATE_MISSING_FINGER); } }
			 */
			try {
				BufferedImage handsImage = ImageIO
						.read(this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_HANDS_IMAGE_PATH));
				byteArrayOutputStream = new ByteArrayOutputStream();
				ImageIO.write(handsImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE, byteArrayOutputStream);
				byte[] handsImageBytes = byteArrayOutputStream.toByteArray();
				String handsImageEncodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(handsImageBytes, false));
				templateValues.put(RegistrationConstants.TEMPLATE_HANDS_IMAGE_SOURCE,
						RegistrationConstants.TEMPLATE_IMAGE_ENCODING + handsImageEncodedBytes);
			} catch (IOException ioException) {
				setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
				LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
			} finally {
				if (byteArrayOutputStream != null) {
					try {
						byteArrayOutputStream.close();
					} catch (IOException exception) {
						setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION,
								null);
						LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
					}
				}
			}
			try {
				BufferedImage qrCodeImage = ImageIO
						.read(this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_QRCODE_IMAGE_PATH));
				byteArrayOutputStream = new ByteArrayOutputStream();
				ImageIO.write(qrCodeImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE, byteArrayOutputStream);
				byte[] qrCodeImageBytes = byteArrayOutputStream.toByteArray();
				String qrCodeImageEncodedBytes = StringUtils
						.newStringUtf8(Base64.encodeBase64(qrCodeImageBytes, false));
				templateValues.put(RegistrationConstants.TEMPLATE_QRCODE_SOURCE,
						RegistrationConstants.TEMPLATE_IMAGE_ENCODING + qrCodeImageEncodedBytes);
			} catch (IOException ioException) {
				setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
				LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
			} finally {
				if (byteArrayOutputStream != null) {
					try {
						byteArrayOutputStream.close();
					} catch (IOException exception) {
						setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION,
								null);
						LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
					}
				}
			}

			templateValues.put(RegistrationConstants.TEMPLATE_DATE_LOCAL_LANG_LABEL, localProperties.getString("date"));
			templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME_LOCAL_LANG_LABEL,
					localProperties.getString("full_name"));
			templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME_LOCAL_LANG, getDemographicValueInLocalLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getFullName().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_DOB_LOCAL_LANG_LABEL,
					localProperties.getString("date_of_birth"));
			templateValues.put(RegistrationConstants.TEMPLATE_GENDER_LOCAL_LANG_LABEL,
					localProperties.getString("gender"));
			templateValues.put(RegistrationConstants.TEMPLATE_GENDER_LOCAL_LANG, getDemographicValueInLocalLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getGender().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1_LOCAL_LANG_LABEL,
					localProperties.getString("address_line1"));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1_LOCAL_LANG,
					getDemographicValueInLocalLanguage(registration.getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getAddressLine1().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2_LOCAL_LANG_LABEL,
					localProperties.getString("address_line2"));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2_LOCAL_LANG,
					getDemographicValueInLocalLanguage(registration.getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getAddressLine2().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE3_LOCAL_LANG_LABEL,
					localProperties.getString("address_line3"));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE3_LOCAL_LANG,
					getDemographicValueInLocalLanguage(registration.getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getAddressLine3().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_CITY_LOCAL_LANG_LABEL, localProperties.getString("city"));
			templateValues.put(RegistrationConstants.TEMPLATE_CITY_LOCAL_LANG, getDemographicValueInLocalLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getCity().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_PROVINCE_LOCAL_LANG_LABEL,
					localProperties.getString("province"));
			templateValues.put(RegistrationConstants.TEMPLATE_PROVINCE_LOCAL_LANG, getDemographicValueInLocalLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getProvince().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_COUNTRY_LOCAL_LANG_LABEL,
					localProperties.getString("region"));
			templateValues.put(RegistrationConstants.TEMPLATE_COUNTRY_LOCAL_LANG, getDemographicValueInLocalLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getRegion().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG_LABEL,
					localProperties.getString("local_authority"));
			templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG,
					getDemographicValueInLocalLanguage(registration.getDemographicDTO().getDemographicInfoDTO()
							.getIdentity().getLocalAdministrativeAuthority().getValues()));
			templateValues.put(RegistrationConstants.TEMPLATE_POSTAL_CODE_LOCAL_LANG_LABEL,
					localProperties.getString("postal_code"));
			templateValues.put(RegistrationConstants.TEMPLATE_EMAIL_LOCAL_LANG_LABEL,
					localProperties.getString("email"));
			templateValues.put(RegistrationConstants.TEMPLATE_MOBILE_LOCAL_LANG_LABEL,
					localProperties.getString("mobile"));
			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS_LOCAL_LANG_LABEL,
					localProperties.getString("documents"));
			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS_LOCAL_LANG, documentsList.toString());
			templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME_LOCAL_LANG_LABEL,
					localProperties.getString("ro_name"));
			templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME_LOCAL_LANG,
					registration.getOsiDataDTO().getOperatorID());

			templateValues.put("rightIndexFinger", "1");
			templateValues.put("rightMiddleFinger", "4");
			templateValues.put("rightRingFinger", "2");
			templateValues.put("rightLittleFinger", "5");
			templateValues.put("rightThumb", "3");
			templateValues.put("leftIndexFinger", "6");
			templateValues.put("leftMiddleFinger", "2");
			templateValues.put("leftRingFinger", "2");
			templateValues.put("leftLittleFinger", "4");
			templateValues.put("leftThumb", "5");

			// get the total count of fingerprints captured and irises captured
			List<FingerprintDetailsDTO> capturedFingers = registration.getBiometricDTO().getApplicantBiometricDTO()
					.getFingerprintDetailsDTO();

			List<IrisDetailsDTO> capturedIris = registration.getBiometricDTO().getApplicantBiometricDTO()
					.getIrisDetailsDTO();

			int[] fingersAndIrises = {
					capturedFingers.stream()
							.mapToInt(capturedFinger -> capturedFinger.getSegmentedFingerprints().size()).sum(),
					capturedIris.size() };

			templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_CAPTURED,
					"Fingers (" + fingersAndIrises[0] + "), Iris (" + fingersAndIrises[1] + "), Face");
			templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_LOCAL_LANG,
					"Fingers (" + fingersAndIrises[0] + "), Iris (" + fingersAndIrises[1] + "), Face");
			templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_LOCAL_LANG_LABEL,
					localProperties.getString("biometrics_captured"));

			Writer writer = new StringWriter();
			try {
				LOGGER.debug(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
						"merge method of TemplateManager had been called for preparing Acknowledgement Template.");

				TemplateManager templateManager = templateManagerBuilder.build();
				InputStream inputStream = templateManager.merge(is, templateValues);
				String defaultEncoding = null;
				IOUtils.copy(inputStream, writer, defaultEncoding);
			} catch (IOException ioException) {
				setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
				LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
			}
			LOGGER.debug(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
					"generateTemplate method has been ended for preparing Acknowledgement Template.");

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put(RegistrationConstants.TEMPLATE_NAME, writer);
			setSuccessResponse(response, RegistrationConstants.SUCCESS, responseMap);
		} catch (ParseException parseException) {
			setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
			LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID, parseException.getMessage());
		} catch (RuntimeException runtimeException) {
			setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
			LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID, runtimeException.getMessage());
		}
		return response;
	}

	/**
	 * @param templateText
	 *            - string which contains the data of template that is used to
	 *            generate notification
	 * @param registration
	 *            - RegistrationDTO to display required fields on the template
	 * @return writer - After mapping all the fields into the template, it is
	 *         written into a StringWriter and returned
	 * @throws RegBaseCheckedException
	 */
	public Writer generateNotificationTemplate(String templateText, RegistrationDTO registration,
			TemplateManagerBuilder templateManagerBuilder) throws RegBaseCheckedException {

		try {
			InputStream is = new ByteArrayInputStream(templateText.getBytes());
			Map<String, Object> values = new LinkedHashMap<>();

			values.put(RegistrationConstants.TEMPLATE_RESIDENT_NAME,
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getFullName());
			values.put(RegistrationConstants.TEMPLATE_REGISTRATION_ID, registration.getRegistrationId());

			SimpleDateFormat sdf = new SimpleDateFormat(RegistrationConstants.TEMPLATE_DATE_FORMAT);
			String currentDate = sdf.format(new Date());

			values.put(RegistrationConstants.TEMPLATE_DATE, currentDate);
			values.put(RegistrationConstants.TEMPLATE_FULL_NAME, getDemographicValueInLocalLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getFullName().getValues()));
			String dob = registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getDateOfBirth()
					.getValue();
			if (dob == null) {
				values.put(RegistrationConstants.TEMPLATE_DOB,
						registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAge());
			} else {
				values.put(RegistrationConstants.TEMPLATE_DOB,
						DateUtils.formatDate(DateUtils.parseToDate(dob, "yyyy/MM/dd"), "dd-MM-YYYY"));
			}
			values.put(RegistrationConstants.TEMPLATE_GENDER, getDemographicValueInLocalLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getGender().getValues()));
			values.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1, getDemographicValueInLocalLanguage(registration
					.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine1().getValues()));
			values.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2, getDemographicValueInLocalLanguage(registration
					.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine2().getValues()));
			values.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE3, getDemographicValueInLocalLanguage(registration
					.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine3().getValues()));
			values.put(RegistrationConstants.TEMPLATE_CITY, getDemographicValueInLocalLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getCity().getValues()));
			values.put(RegistrationConstants.TEMPLATE_STATE, getDemographicValueInLocalLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getProvince().getValues()));
			values.put(RegistrationConstants.TEMPLATE_COUNTRY, getDemographicValueInLocalLanguage(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getRegion().getValues()));
			values.put(RegistrationConstants.TEMPLATE_POSTAL_CODE,
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getPostalCode());
			values.put(RegistrationConstants.TEMPLATE_MOBILE,
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getPhone().getValue());
			String email = registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getEmail().getValue();
			if (email == null || email.isEmpty()) {
				values.put(RegistrationConstants.TEMPLATE_EMAIL, RegistrationConstants.EMPTY);
			} else {
				values.put(RegistrationConstants.TEMPLATE_EMAIL, email);
			}
			Writer writer = new StringWriter();
			try {
				TemplateManager templateManager = templateManagerBuilder.build();
				String defaultEncoding = null;
				InputStream inputStream = templateManager.merge(is, values);
				IOUtils.copy(inputStream, writer, defaultEncoding);
			} catch (IOException exception) {
				LOGGER.debug(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
						"generateNotificationTemplate method has been ended for preparing Notification Template.");
			}
			return writer;
		} catch (ParseException parseException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_PACKET_DATE_PARSER_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_DATE_PARSER_CODE.getErrorMessage(), parseException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.TEMPLATE_GENERATOR_SMS_EXCEPTION,
					runtimeException.getMessage(), runtimeException);
		}
	}

	/**
	 * @param enrolment
	 *            - EnrolmentDTO to get the biometric details
	 * @return hash map which gives the set of fingerprints captured and their
	 *         respective rankings based on quality score
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private HashMap<String, Integer> getFingerPrintQualityRanking(RegistrationDTO registration) {
		// for storing the fingerprints captured and their respective quality scores
		HashMap<String, Double> fingersQuality = new HashMap<>();

		// list of missing fingers
		List<BiometricExceptionDTO> exceptionFingers = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getFingerPrintBiometricExceptionDTO();
		//
		if (exceptionFingers != null) {
			for (BiometricExceptionDTO exceptionFinger : exceptionFingers) {
				fingersQuality.put(exceptionFinger.getMissingBiometric(), (double) 0);
			}
		}
		List<FingerprintDetailsDTO> availableFingers = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getFingerprintDetailsDTO();
		for (FingerprintDetailsDTO availableFinger : availableFingers) {
			fingersQuality.put(availableFinger.getFingerType(), availableFinger.getQualityScore());
		}

		Object[] fingerQualitykeys = fingersQuality.entrySet().toArray();
		Arrays.sort(fingerQualitykeys, new Comparator<Object>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			public int compare(Object fingetPrintQuality1, Object fingetPrintQuality2) {
				return ((Map.Entry<String, Double>) fingetPrintQuality2).getValue()
						.compareTo(((Map.Entry<String, Double>) fingetPrintQuality1).getValue());
			}
		});

		LinkedHashMap<String, Double> fingersQualitySorted = new LinkedHashMap<>();
		for (Object fingerPrintQualityKey : fingerQualitykeys) {
			String finger = ((Map.Entry<String, Double>) fingerPrintQualityKey).getKey();
			double quality = ((Map.Entry<String, Double>) fingerPrintQualityKey).getValue();
			fingersQualitySorted.put(finger, quality);
		}

		HashMap<String, Integer> fingersQualityRanking = new HashMap<>();
		int rank = 1;
		double prev = 1.0;
		for (Map.Entry<String, Double> entry : fingersQualitySorted.entrySet()) {
			if (entry.getValue() != 0) {
				if (Double.compare(entry.getValue(), prev) == 0 || Double.compare(prev, 1.0) == 0) {
					fingersQualityRanking.put(entry.getKey(), rank);
				} else {
					fingersQualityRanking.put(entry.getKey(), ++rank);
				}
				prev = entry.getValue();
			} else {
				fingersQualityRanking.put(entry.getKey(), entry.getValue().intValue());
			}
		}

		return fingersQualityRanking;
	}

	private String getDemographicValueInPlatformLanguage(LinkedList<ValuesDTO> demographicValues) {
		LOGGER.debug(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting values of demographic fields in platform language");
		String platformLanguageCode = AppConfig.getApplicationProperty("application_language");

		Optional<ValuesDTO> demographicValue = demographicValues.stream()
				.filter(value -> value.getLanguage().equals(platformLanguageCode)).findFirst();

		LOGGER.debug(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting values of demographic fields in platform language has been completed");

		return demographicValue.isPresent() ? demographicValue.get().getValue() : RegistrationConstants.EMPTY;
	}

	private String getDemographicValueInLocalLanguage(LinkedList<ValuesDTO> demographicValues) {
		LOGGER.debug(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting values of demographic fields in local language");
		String localLanguageCode = AppConfig.getApplicationProperty("application_language");

		Optional<ValuesDTO> demographicValue = demographicValues.stream()
				.filter(value -> value.getLanguage().equals(localLanguageCode)).findFirst();

		LOGGER.debug(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting values of demographic fields in platform language has been completed");

		return demographicValue.isPresent() ? demographicValue.get().getValue() : RegistrationConstants.EMPTY;
	}

}