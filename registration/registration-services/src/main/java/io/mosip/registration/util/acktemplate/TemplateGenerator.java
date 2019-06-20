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
import java.math.BigInteger;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.qrcodegenerator.exception.QrcodeGenerationException;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.dto.demographic.ValuesDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.BaseService;

/**
 * Generates Velocity Template for the creation of acknowledgement
 * 
 * @author Himaja Dhanyamraju
 *
 */
@Controller
public class TemplateGenerator extends BaseService {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(TemplateGenerator.class);

	@Autowired
	private QrCodeGenerator<QrVersion> qrCodeGenerator;

	private String consentText;

	private String guidelines;

	public String getGuidelines() {
		return guidelines;
	}

	public void setGuidelines(String guidelines) {
		this.guidelines = guidelines;
	}

	public String getConsentText() {
		return consentText;
	}

	public void setConsentText(String consentText) {
		this.consentText = consentText;
	}

	/**
	 * This method generates the Registration Preview / Acknowledgement page by mapping all the applicant details
	 * including demographic details, documents, biometrics and photos that are captured as a part of 
	 * registration to the place-holders given in the html template. 
	 * 
	 * <p>
	 * Returns the {@link ResponseDTO} object.
	 * </p>
	 * 
	 * <p> 
	 * If all the data is mapped successfully to the html template, 
	 * {@link SuccessResponseDTO} will be set in {@link ResponseDTO} object. 
	 * The generated template is stored in the success response which will be used further to display the
	 * Registration Preview / Acknowledgement.
	 * </p>
	 * 
	 * <p>
	 * If any exception occurs, {@link ErrorResponseDTO} will be set in
	 * {@link ResponseDTO} object
	 * </p>
	 * 
	 * @param templateText
	 *            - string which contains the data of template that is used to
	 *            generate acknowledgement
	 * @param registration
	 *            - RegistrationDTO to display required fields on the template
	 * @param templateManagerBuilder
	 *            - The Builder which generates template by mapping values to
	 *            respective place-holders in template
	 * @param templateType
	 *            - The type of template that is required (like
	 *            email/sms/acknowledgement)
	 * @return {@link ResponseDTO} which specifies either success response or error response
	 *         after the generation of Registration Preview / Acknowledgement
	 */
	public ResponseDTO generateTemplate(String templateText, RegistrationDTO registration,
			TemplateManagerBuilder templateManagerBuilder, String templateType) {

		ResponseDTO response = new ResponseDTO();

		try {
			LOGGER.info(LOG_TEMPLATE_GENERATOR, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					"generateTemplate had been called for preparing Acknowledgement Template.");

			ResourceBundle localProperties = ApplicationContext.localLanguageProperty();
			ResourceBundle applicationLanguageProperties = ApplicationContext.applicationLanguageBundle();

			InputStream is = new ByteArrayInputStream(templateText.getBytes());
			Map<String, Object> templateValues = new WeakHashMap<>();

			String documentDisableFlag = String
					.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_DISABLE_FLAG));
			String fingerPrintDisableFlag = String
					.valueOf(ApplicationContext.map().get(RegistrationConstants.FINGERPRINT_DISABLE_FLAG));
			String irisDisableFlag = String
					.valueOf(ApplicationContext.map().get(RegistrationConstants.IRIS_DISABLE_FLAG));
			String faceDisableFlag = String
					.valueOf(ApplicationContext.map().get(RegistrationConstants.FACE_DISABLE_FLAG));
			IndividualIdentity individualIdentity = (IndividualIdentity) registration.getDemographicDTO()
					.getDemographicInfoDTO().getIdentity();

			boolean isChild = (boolean) SessionContext.map().get(RegistrationConstants.IS_Child);

			if (templateType.equals(RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE)) {
				/* Set-up Registration Acknowledgement related content */
				setUpAcknowledgementContent(registration, templateValues, response, applicationLanguageProperties,
						localProperties, fingerPrintDisableFlag, irisDisableFlag, individualIdentity);
				/* Set-up important guidelines that are configured by the country */
				setUpImportantGuidelines(templateValues, guidelines);
			} else {
				/* Set-up Registration Preview related content */
				setUpPreviewContent(registration, templateValues, isChild, response, applicationLanguageProperties,
						localProperties, fingerPrintDisableFlag);
			}

			if (registration.getSelectionListDTO() != null) {
				if (registration.getSelectionListDTO().isBiometrics()
						|| registration.getBiometricDTO().getApplicantBiometricDTO().getIrisDetailsDTO() != null) {
					templateValues = countMissingIrises(templateValues, registration, isChild, templateType);
				} else {
					if (!RegistrationConstants.ENABLE.equalsIgnoreCase(faceDisableFlag) || registration
							.getBiometricDTO().getApplicantBiometricDTO().getExceptionFace().getFace() == null) {
						templateValues.put(RegistrationConstants.TEMPLATE_IRIS_DISABLED,
								RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
					}
					templateValues.put(RegistrationConstants.TEMPLATE_IRIS_ENABLED,
							RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
				}
			} else {
				templateValues = countMissingIrises(templateValues, registration, isChild, templateType);
			}

			/* Set-up demographic information related content */
			setUpDemographicInfo(registration, templateValues, applicationLanguageProperties, localProperties,
					individualIdentity);

			/* Set-up the list of documents submitted by the applicant */
			setUpDocuments(templateValues, applicationLanguageProperties, localProperties, individualIdentity,
					documentDisableFlag);

			/* Set-up captured biometrics count */
			setUpBiometricsCount(templateValues, registration, applicationLanguageProperties, localProperties,
					fingerPrintDisableFlag, irisDisableFlag, faceDisableFlag, isChild);

			/* Set-up captured images of applicant */
			setUpCapturedImages(templateValues, registration, isChild, applicationLanguageProperties, localProperties,
					faceDisableFlag);

			/* Set-up Biometrics related content */
			setUpBiometricContent(templateValues, registration, isChild, applicationLanguageProperties, localProperties,
					fingerPrintDisableFlag, irisDisableFlag, faceDisableFlag);

			/* Set-up Registration Office and Officer related content */
			setUpROContent(templateValues, registration, applicationLanguageProperties, localProperties);

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
				LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			}
			LOGGER.debug(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
					"generateTemplate method has been ended for preparing Acknowledgement Template.");

			Map<String, Object> responseMap = new WeakHashMap<>();
			responseMap.put(RegistrationConstants.TEMPLATE_NAME, writer);
			setSuccessResponse(response, RegistrationConstants.SUCCESS, responseMap);
		} catch (RuntimeException runtimeException) {
			setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
			LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
		return response;
	}

	private void setUpImportantGuidelines(Map<String, Object> templateValues, String guidelines) {
		String[] importantGuidelines = guidelines.split(RegistrationConstants.SPLIT_DELIMITOR);
		StringBuilder formattedGuidelines = new StringBuilder();
		for (String importantGuideline : importantGuidelines) {
			formattedGuidelines.append(RegistrationConstants.LIST_ITEM_OPENING_TAG).append(importantGuideline)
					.append(RegistrationConstants.LIST_ITEM_CLOSING_TAG);
		}
		templateValues.put(RegistrationConstants.TEMPLATE_GUIDELINES, formattedGuidelines.toString());
	}

	private void setUpROContent(Map<String, Object> templateValues, RegistrationDTO registration,
			ResourceBundle applicationLanguageProperties, ResourceBundle localProperties) {
		templateValues.put(RegistrationConstants.TEMPLATE_RO_IMAGE, RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME_USER_LANG_LABEL,
				applicationLanguageProperties.getString("ro_name"));
		templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME_LOCAL_LANG_LABEL,
				localProperties.getString("ro_name"));
		templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME,
				getValue(registration.getOsiDataDTO().getOperatorID()));
		templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME_LOCAL_LANG, RegistrationConstants.EMPTY);
		templateValues.put(RegistrationConstants.TEMPLATE_REG_CENTER_USER_LANG_LABEL,
				applicationLanguageProperties.getString("registrationcenter"));
		templateValues.put(RegistrationConstants.TEMPLATE_REG_CENTER_LOCAL_LANG_LABEL,
				localProperties.getString("registrationcenter"));
		templateValues.put(RegistrationConstants.TEMPLATE_REG_CENTER,
				SessionContext.userContext().getRegistrationCenterDetailDTO().getRegistrationCenterName());
		templateValues.put(RegistrationConstants.TEMPLATE_REG_CENTER_LOCAL_LANG, RegistrationConstants.EMPTY);
		templateValues.put(RegistrationConstants.TEMPLATE_IMPORTANT_GUIDELINES,
				applicationLanguageProperties.getString("importantguidelines"));
	}

	private void setUpCapturedImages(Map<String, Object> templateValues, RegistrationDTO registration, boolean isChild,
			ResourceBundle applicationLanguageProperties, ResourceBundle localProperties, String faceDisableFlag) {
		boolean parentPhotoCaptured = false;
		if (registration.getBiometricDTO().getIntroducerBiometricDTO() != null
				&& registration.getBiometricDTO().getIntroducerBiometricDTO().getFace() != null
				&& registration.getBiometricDTO().getIntroducerBiometricDTO().getFace().getFace() != null) {
			parentPhotoCaptured = true;
		}
		if (!parentPhotoCaptured) {
			templateValues.put(RegistrationConstants.PARENT_PHOTO_CAPTURED,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		}
		if (isChild || registration.isUpdateUINNonBiometric()) {
			if (registration.getBiometricDTO().getIntroducerBiometricDTO().getExceptionFace() != null && registration
					.getBiometricDTO().getIntroducerBiometricDTO().getExceptionFace().getFace() != null) {
				byte[] exceptionImageBytes = registration.getBiometricDTO().getIntroducerBiometricDTO()
						.getExceptionFace().getFace();
				setUpExceptionPhoto(exceptionImageBytes, templateValues, applicationLanguageProperties,
						localProperties);
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_WITHOUT_EXCEPTION, null);
				templateValues.put(RegistrationConstants.TEMPLATE_WITH_EXCEPTION,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
		} else if (registration.getBiometricDTO().getApplicantBiometricDTO().isHasExceptionPhoto()) {
			byte[] exceptionImageBytes = registration.getBiometricDTO().getApplicantBiometricDTO().getExceptionFace()
					.getFace();
			setUpExceptionPhoto(exceptionImageBytes, templateValues, applicationLanguageProperties, localProperties);
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_WITHOUT_EXCEPTION, null);
			templateValues.put(RegistrationConstants.TEMPLATE_WITH_EXCEPTION,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		}

		if (RegistrationConstants.ENABLE.equalsIgnoreCase(faceDisableFlag)) {
			templateValues.put(RegistrationConstants.TEMPLATE_PHOTO_USER_LANG,
					applicationLanguageProperties.getString("individualphoto"));
			templateValues.put(RegistrationConstants.TEMPLATE_PHOTO_LOCAL_LANG,
					localProperties.getString("individualphoto"));
			byte[] applicantImageBytes;
			if (registration.isUpdateUINNonBiometric() && !registration.isUpdateUINChild()) {
				applicantImageBytes = registration.getBiometricDTO().getIntroducerBiometricDTO().getFace().getFace();
			} else {
				applicantImageBytes = registration.getBiometricDTO().getApplicantBiometricDTO().getFace().getFace();
			}

			String applicantImageEncodedBytes = StringUtils
					.newStringUtf8(Base64.encodeBase64(applicantImageBytes, false));
			templateValues.put(RegistrationConstants.TEMPLATE_APPLICANT_IMAGE_SOURCE,
					RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + applicantImageEncodedBytes);
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_FACE_CAPTURE_ENABLED,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		}
	}

	private void setUpExceptionPhoto(byte[] exceptionImageBytes, Map<String, Object> templateValues,
			ResourceBundle applicationLanguageProperties, ResourceBundle localProperties) {
		templateValues.put(RegistrationConstants.TEMPLATE_WITHOUT_EXCEPTION,
				RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		templateValues.put(RegistrationConstants.TEMPLATE_EXCEPTION_PHOTO_USER_LANG_LABEL,
				applicationLanguageProperties.getString("exceptionphoto"));
		templateValues.put(RegistrationConstants.TEMPLATE_EXCEPTION_PHOTO_LOCAL_LANG_LABEL,
				localProperties.getString("exceptionphoto"));
		String exceptionImageEncodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(exceptionImageBytes, false));
		templateValues.put(RegistrationConstants.TEMPLATE_EXCEPTION_IMAGE_SOURCE,
				RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + exceptionImageEncodedBytes);
	}

	private void setUpBiometricContent(Map<String, Object> templateValues, RegistrationDTO registration,
			boolean isChild, ResourceBundle applicationLanguageProperties, ResourceBundle localProperties,
			String fingerPrintDisableFlag, String irisDisableFlag, String faceDisableFlag) {
		boolean exceptionWithParentPhoto = false;
		// iris is configured
		if (RegistrationConstants.ENABLE.equalsIgnoreCase(irisDisableFlag)
				&& ((registration.getSelectionListDTO() == null && !isChild)
						|| (registration.getSelectionListDTO() == null && isChild)
						|| (registration.getSelectionListDTO() != null
								&& registration.getSelectionListDTO().isBiometrics()))) {
			if (isChild || registration.isUpdateUINNonBiometric()) {
				if (registration.getBiometricDTO().getIntroducerBiometricDTO().getFace() == null
						|| (registration.getBiometricDTO().getIntroducerBiometricDTO().getFace() != null && registration
								.getBiometricDTO().getIntroducerBiometricDTO().getFace().getFace() == null)
								&& registration.getBiometricDTO().getIntroducerBiometricDTO().getIrisDetailsDTO()
										.isEmpty()) {
					if (!RegistrationConstants.ENABLE.equalsIgnoreCase(faceDisableFlag) || registration
							.getBiometricDTO().getIntroducerBiometricDTO().getExceptionFace().getFace() == null) {
						templateValues.put(RegistrationConstants.TEMPLATE_IRIS_DISABLED,
								RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
					} else {
						exceptionWithParentPhoto = true;
					}
					templateValues.put(RegistrationConstants.TEMPLATE_IRIS_ENABLED,
							RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
				} else {
					if (registration.getBiometricDTO().getIntroducerBiometricDTO().getExceptionFace()
							.getFace() != null) {
						if (registration.getBiometricDTO().getIntroducerBiometricDTO().getFace() != null && registration
								.getBiometricDTO().getIntroducerBiometricDTO().getFace().getFace() != null) {
							templateValues.put(RegistrationConstants.IRIS_WITH_EXCEPTION,
									RegistrationConstants.TEMPLATE_IRIS);
						} else {
							templateValues.put(RegistrationConstants.IRIS_WITH_EXCEPTION,
									RegistrationConstants.IRIS_WITH_EXCEPTION_STYLE);
						}
					} else {
						templateValues.put(RegistrationConstants.IRIS_STYLE,
								RegistrationConstants.IRIS_WITHOUT_EXCEPTION_STYLE);
					}
				}
			} else {
				templateValues.put(RegistrationConstants.IRIS_STYLE, RegistrationConstants.IRIS_WITHOUT_EXCEPTION);
				templateValues.put(RegistrationConstants.IRIS_WITH_EXCEPTION, RegistrationConstants.TEMPLATE_IRIS);
			}
			templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE_USER_LANG_LABEL,
					applicationLanguageProperties.getString("lefteye"));
			templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE_LOCAL_LANG_LABEL,
					localProperties.getString("lefteye"));
			templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE_USER_LANG_LABEL,
					applicationLanguageProperties.getString("righteye"));
			templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE_LOCAL_LANG_LABEL,
					localProperties.getString("righteye"));
			if (!exceptionWithParentPhoto) {
				templateValues.put(RegistrationConstants.TEMPLATE_IRIS_DISABLED,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
		} else {
			if (!RegistrationConstants.ENABLE.equalsIgnoreCase(faceDisableFlag)
					|| (((isChild || registration.isUpdateUINNonBiometric()) && registration.getBiometricDTO()
							.getIntroducerBiometricDTO().getExceptionFace().getFace() == null)
							|| ((!isChild && !registration.isUpdateUINNonBiometric()) && registration.getBiometricDTO()
									.getApplicantBiometricDTO().getExceptionFace().getFace() == null))) {
				templateValues.put(RegistrationConstants.TEMPLATE_IRIS_DISABLED,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
			templateValues.put(RegistrationConstants.TEMPLATE_IRIS_ENABLED,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		}

		if (parentPhotoCaptured) {
			templateValues.put(RegistrationConstants.PARENT_PHOTO_NOT_CAPTURED,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			templateValues.put(RegistrationConstants.PARENT_PHOTO_PRIMARY_LANG,
					applicationLanguageProperties.getString("parentPhoto"));
			templateValues.put(RegistrationConstants.PARENT_PHOTO_LOCAL_LANG, localProperties.getString("parentPhoto"));
			byte[] parentImageBytes = registration.getBiometricDTO().getIntroducerBiometricDTO().getFace().getFace();
			String parentImageEncodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(parentImageBytes, false));
			templateValues.put(RegistrationConstants.PARENT_IMAGE_SOURCE,
					RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + parentImageEncodedBytes);
		}

		if (RegistrationConstants.ENABLE.equalsIgnoreCase(fingerPrintDisableFlag)
				&& ((registration.getSelectionListDTO() != null && registration.getSelectionListDTO().isBiometrics())
						|| (registration.getSelectionListDTO() == null && !isChild)
						|| (registration.getSelectionListDTO() == null && isChild))) {
			templateValues.put(RegistrationConstants.TEMPLATE_FINGERPRINTS_CAPTURED, null);
			templateValues.put(RegistrationConstants.TEMPLATE_LEFT_PALM_USER_LANG_LABEL,
					applicationLanguageProperties.getString("lefthandpalm"));
			templateValues.put(RegistrationConstants.TEMPLATE_LEFT_PALM_LOCAL_LANG_LABEL,
					localProperties.getString("lefthandpalm"));
			templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_PALM_USER_LANG_LABEL,
					applicationLanguageProperties.getString("righthandpalm"));
			templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_PALM_LOCAL_LANG_LABEL,
					localProperties.getString("righthandpalm"));
			templateValues.put(RegistrationConstants.TEMPLATE_THUMBS_USER_LANG_LABEL,
					applicationLanguageProperties.getString("thumbs"));
			templateValues.put(RegistrationConstants.TEMPLATE_THUMBS_LOCAL_LANG_LABEL,
					localProperties.getString("thumbs"));
<<<<<<< HEAD
			if (isChild || registration.isUpdateUINChild()) {
				if (registration.getBiometricDTO().getIntroducerBiometricDTO().getFace() != null
						&& registration.getBiometricDTO().getIntroducerBiometricDTO().getFace().getFace() != null) {
					templateValues.put(RegistrationConstants.PARENT_PHOTO_NOT_CAPTURED,
							RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
					templateValues.put(RegistrationConstants.PARENT_PHOTO_PRIMARY_LANG,
							applicationLanguageProperties.getString("parentPhoto"));
					templateValues.put(RegistrationConstants.PARENT_PHOTO_LOCAL_LANG,
							localProperties.getString("parentPhoto"));
					byte[] parentImageBytes = registration.getBiometricDTO().getIntroducerBiometricDTO().getFace()
							.getFace();
					String parentImageEncodedBytes = StringUtils
							.newStringUtf8(Base64.encodeBase64(parentImageBytes, false));
					templateValues.put(RegistrationConstants.PARENT_IMAGE_SOURCE,
							RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + parentImageEncodedBytes);
=======
			if (isChild || registration.isUpdateUINNonBiometric()) {
				if (parentPhotoCaptured) {
>>>>>>> 55442bec8b0b7257e86524eff51c77f99a33dc9f
					setUpParentFingerprints(registration, templateValues);
				} else {
					templateValues.put(RegistrationConstants.PARENT_PHOTO_CAPTURED,
							RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
					if (!registration.getBiometricDTO().getIntroducerBiometricDTO().getFingerprintDetailsDTO()
							.isEmpty()) {
						for (FingerprintDetailsDTO fingerprint : registration.getBiometricDTO()
								.getIntroducerBiometricDTO().getFingerprintDetailsDTO()) {
							if (fingerprint.getFingerType().contains(RegistrationConstants.LEFTPALM)) {
								templateValues.put(RegistrationConstants.TEMPLATE_CHILD_LEFT,
										RegistrationConstants.PARENT_STYLE);
								templateValues.put(RegistrationConstants.PARENT_RIGHT_SLAP,
										RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
								templateValues.put(RegistrationConstants.PARENT_THUMBS,
										RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
								templateValues.put(RegistrationConstants.TEMPLATE_LEFT_INDEX_FINGER,
										RegistrationConstants.TEMPLATE_RIGHT_MARK);
							} else if (fingerprint.getFingerType().contains(RegistrationConstants.RIGHTPALM)) {
								templateValues.put(RegistrationConstants.TEMPLATE_CHILD_RIGHT,
										RegistrationConstants.PARENT_STYLE);
								templateValues.put(RegistrationConstants.PARENT_LEFT_SLAP,
										RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
								templateValues.put(RegistrationConstants.PARENT_THUMBS,
										RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
								templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_LITTLE_FINGER,
										RegistrationConstants.TEMPLATE_RIGHT_MARK);
							} else if (fingerprint.getFingerType().contains(RegistrationConstants.THUMBS)) {
								templateValues.put(RegistrationConstants.TEMPLATE_CHILD_THUMBS,
										RegistrationConstants.PARENT_STYLE);
								templateValues.put(RegistrationConstants.PARENT_LEFT_SLAP,
										RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
								templateValues.put(RegistrationConstants.PARENT_RIGHT_SLAP,
										RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
								templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_THUMB_FINGER,
										RegistrationConstants.TEMPLATE_RIGHT_MARK);
							}
						}
						templateValues.put(RegistrationConstants.TEMPLATE_IS_CHILD,
								RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
						templateValues.put(RegistrationConstants.TEMPLATE_MISSING_LEFT_FINGERS,
								RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
						templateValues.put(RegistrationConstants.TEMPLATE_MISSING_RIGHT_FINGERS,
								RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
						templateValues.put(RegistrationConstants.TEMPLATE_MISSING_THUMBS,
								RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
					} else {
						templateValues.put(RegistrationConstants.TEMPLATE_FINGERPRINTS_CAPTURED,
								RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
					}
				}
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_CHILD_LEFT,
						RegistrationConstants.TEMPLATE_LEFT_INDEX_FINGER);
				templateValues.put(RegistrationConstants.TEMPLATE_CHILD_RIGHT,
						RegistrationConstants.TEMPLATE_RIGHT_LITTLE_FINGER);
				templateValues.put(RegistrationConstants.TEMPLATE_CHILD_THUMBS,
						RegistrationConstants.TEMPLATE_RIGHT_THUMB_FINGER);
				// get the quality ranking for fingerprints of the applicant
				Map<String, Integer> fingersQuality = getFingerPrintQualityRanking(registration);
				for (Map.Entry<String, Integer> entry : fingersQuality.entrySet()) {
					if (entry.getValue() != 0) {
						if (registration.getRegistrationMetaDataDTO().getRegistrationCategory() != null
								&& registration.getRegistrationMetaDataDTO().getRegistrationCategory()
										.equals(RegistrationConstants.PACKET_TYPE_LOST)) {
							// display tick mark for the captured fingerprints
							templateValues.put(entry.getKey(), RegistrationConstants.TEMPLATE_RIGHT_MARK);
						} else {
							// display rank of quality for the captured fingerprints
							templateValues.put(entry.getKey(), entry.getValue());
						}
					} else {
						// display cross mark for missing fingerprints
						templateValues.put(entry.getKey(), RegistrationConstants.TEMPLATE_CROSS_MARK);
					}
				}
				countMissingFingers(registration, templateValues, applicationLanguageProperties, localProperties);
			}
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_FINGERPRINTS_CAPTURED,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		}
	}

	private void setUpParentFingerprints(RegistrationDTO registration, Map<String, Object> templateValues) {
		templateValues.put(RegistrationConstants.TEMPLATE_CHILD_LEFT, RegistrationConstants.TEMPLATE_LEFT_INDEX_FINGER);
		templateValues.put(RegistrationConstants.TEMPLATE_CHILD_RIGHT,
				RegistrationConstants.TEMPLATE_RIGHT_LITTLE_FINGER);
		templateValues.put(RegistrationConstants.TEMPLATE_CHILD_THUMBS,
				RegistrationConstants.TEMPLATE_RIGHT_THUMB_FINGER);
		// get the quality ranking for fingerprints of the Introducer
		Map<String, Integer> fingersQuality = new WeakHashMap<>();

		// list of missing fingers
		List<BiometricExceptionDTO> exceptionFingers = registration.getBiometricDTO().getIntroducerBiometricDTO()
				.getBiometricExceptionDTO();

		if (exceptionFingers != null) {
			for (BiometricExceptionDTO exceptionFinger : exceptionFingers) {
				if (exceptionFinger.getBiometricType().equalsIgnoreCase(RegistrationConstants.FINGERPRINT)
						&& exceptionFinger.getReason().equals(RegistrationConstants.MISSING_BIOMETRICS)) {
					fingersQuality.put(exceptionFinger.getMissingBiometric(), 0);
				}
			}
		}

		for (Map.Entry<String, Integer> entry : fingersQuality.entrySet()) {
			// display cross mark for missing fingerprints
			templateValues.put(entry.getKey(), RegistrationConstants.TEMPLATE_CROSS_MARK);
		}
	}

	private void setUpBiometricsCount(Map<String, Object> templateValues, RegistrationDTO registration,
			ResourceBundle applicationLanguageProperties, ResourceBundle localProperties, String fingerPrintDisableFlag,
			String irisDisableFlag, String faceDisableFlag, boolean isChild) {

		templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_USER_LANG_LABEL,
				applicationLanguageProperties.getString("biometricsHeading"));
		templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_LOCAL_LANG_LABEL,
				localProperties.getString("biometricsHeading"));
		templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_CAPTURED_USER_LANG_LABEL,
				applicationLanguageProperties.getString("biometrics_captured"));
		templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_CAPTURED_LOCAL_LANG_LABEL,
				localProperties.getString("biometrics_captured"));

		List<FingerprintDetailsDTO> capturedFingers;
		List<IrisDetailsDTO> capturedIris;

		if ((registration.getSelectionListDTO() == null && !isChild)
				|| (registration.getSelectionListDTO() != null && !registration.isUpdateUINNonBiometric())) {
			// get the total count of fingerprints captured and irises captured
			capturedFingers = registration.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO();
			capturedIris = registration.getBiometricDTO().getApplicantBiometricDTO().getIrisDetailsDTO();
		} else {
			capturedFingers = registration.getBiometricDTO().getIntroducerBiometricDTO().getFingerprintDetailsDTO();
			capturedIris = registration.getBiometricDTO().getIntroducerBiometricDTO().getIrisDetailsDTO();
		}
		int[] fingersAndIrises = { capturedFingers.stream()
				.mapToInt(capturedFinger -> capturedFinger.getSegmentedFingerprints().size()).sum(),
				capturedIris.size() };

		StringBuilder biometricsCaptured = new StringBuilder();
		StringBuilder biometricsCapturedLocalLang = new StringBuilder();

		if (RegistrationConstants.ENABLE.equalsIgnoreCase(fingerPrintDisableFlag)) {

			if (registration.getSelectionListDTO() != null) {
				if (registration.getSelectionListDTO().isBiometrics() || registration.getBiometricDTO()
						.getApplicantBiometricDTO().getFingerprintDetailsDTO() != null) {
					addToCapturedBiometrics(biometricsCaptured, biometricsCapturedLocalLang,
							applicationLanguageProperties, localProperties, "fingersCount", fingersAndIrises[0]);
				}
			} else {
				addToCapturedBiometrics(biometricsCaptured, biometricsCapturedLocalLang, applicationLanguageProperties,
						localProperties, "fingersCount", fingersAndIrises[0]);
			}
		}
		if (RegistrationConstants.ENABLE.equalsIgnoreCase(irisDisableFlag)) {
			if (registration.getSelectionListDTO() != null) {
				if (registration.getSelectionListDTO().isBiometrics()
						|| registration.getBiometricDTO().getApplicantBiometricDTO().getIrisDetailsDTO() != null) {
					addToCapturedBiometrics(biometricsCaptured, biometricsCapturedLocalLang,
							applicationLanguageProperties, localProperties, "irisCount", fingersAndIrises[1]);
				}
			} else {
				addToCapturedBiometrics(biometricsCaptured, biometricsCapturedLocalLang, applicationLanguageProperties,
						localProperties, "irisCount", fingersAndIrises[1]);
			}
		}
		if (RegistrationConstants.ENABLE.equalsIgnoreCase(faceDisableFlag)) {
			if (biometricsCaptured.length() > 1) {
				biometricsCaptured.append(applicationLanguageProperties.getString("comma"));
				biometricsCapturedLocalLang.append(localProperties.getString("comma"));
			}
			biometricsCaptured.append(applicationLanguageProperties.getString("faceCount"));
			biometricsCapturedLocalLang.append(localProperties.getString("faceCount"));
		}

		if (RegistrationConstants.ENABLE.equalsIgnoreCase(fingerPrintDisableFlag)
				|| RegistrationConstants.ENABLE.equalsIgnoreCase(irisDisableFlag)
				|| RegistrationConstants.ENABLE.equalsIgnoreCase(faceDisableFlag)) {

			templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_CAPTURED, biometricsCaptured);
			templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_CAPTURED_LOCAL_LANG,
					biometricsCapturedLocalLang);
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_ENABLED,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		}
	}

	private void setUpDocuments(Map<String, Object> templateValues, ResourceBundle applicationLanguageProperties,
			ResourceBundle localProperties, IndividualIdentity individualIdentity, String documentDisableFlag) {
		if (RegistrationConstants.ENABLE.equalsIgnoreCase(documentDisableFlag)) {
			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS_USER_LANG_LABEL,
					applicationLanguageProperties.getString("documents"));
			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS_LOCAL_LANG_LABEL,
					localProperties.getString("documents"));
			StringBuilder documentsList = new StringBuilder();
			if (individualIdentity.getProofOfIdentity() != null) {
				documentsList.append(individualIdentity.getProofOfIdentity().getValue());
			}
			if (individualIdentity.getProofOfAddress() != null) {
				if (documentsList.length() > 0) {
					documentsList.append(", ");
				}
				documentsList.append(individualIdentity.getProofOfAddress().getValue());
			}
			if (individualIdentity.getProofOfRelationship() != null) {
				if (documentsList.length() > 0) {
					documentsList.append(", ");
				}
				documentsList.append(individualIdentity.getProofOfRelationship().getValue());
			}
			if (individualIdentity.getProofOfDateOfBirth() != null) {
				if (documentsList.length() > 0) {
					documentsList.append(", ");
				}
				documentsList.append(individualIdentity.getProofOfDateOfBirth().getValue());
			}
			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS, documentsList.toString());
			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS_LOCAL_LANG, RegistrationConstants.EMPTY);
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS_ENABLED,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		}
	}

	private void setUpDemographicInfo(RegistrationDTO registration, Map<String, Object> templateValues,
			ResourceBundle applicationLanguageProperties, ResourceBundle localProperties,
			IndividualIdentity individualIdentity) {
		String platformLanguageCode = ApplicationContext.applicationLanguage();
		String localLanguageCode = ApplicationContext.localLanguage();
		String dob = getValue(individualIdentity.getDateOfBirth());

		templateValues.put(RegistrationConstants.TEMPLATE_DATE_USER_LANG_LABEL,
				applicationLanguageProperties.getString("date"));
		templateValues.put(RegistrationConstants.TEMPLATE_DATE_LOCAL_LANG_LABEL, localProperties.getString("date"));

		SimpleDateFormat sdf = new SimpleDateFormat(RegistrationConstants.TEMPLATE_DATE_FORMAT);
		String currentDate = sdf.format(new Date());

		// map the respective fields with the values in the registrationDTO
		templateValues.put(RegistrationConstants.TEMPLATE_DATE, currentDate);

		templateValues.put(RegistrationConstants.TEMPLATE_DEMO_INFO,
				applicationLanguageProperties.getString("demographicInformation"));
		templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME_USER_LANG_LABEL,
				applicationLanguageProperties.getString("fullName"));
		templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME_LOCAL_LANG_LABEL,
				localProperties.getString("fullName"));
		templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME,
				getValue(registration.isNameNotUpdated() ? registration.getRegistrationMetaDataDTO().getFullName()
						: individualIdentity.getFullName(), platformLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME_LOCAL_LANG,
				getValue(registration.isNameNotUpdated() ? registration.getRegistrationMetaDataDTO().getFullName()
						: individualIdentity.getFullName(), localLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_GENDER_USER_LANG_LABEL,
				applicationLanguageProperties.getString("gender"));
		templateValues.put(RegistrationConstants.TEMPLATE_GENDER_LOCAL_LANG_LABEL, localProperties.getString("gender"));
		templateValues.put(RegistrationConstants.TEMPLATE_GENDER,
				getValue(individualIdentity.getGender(), platformLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_GENDER_LOCAL_LANG,
				getValue(individualIdentity.getGender(), localLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_DOB_USER_LANG_LABEL,
				applicationLanguageProperties.getString("ageDatePicker"));
		templateValues.put(RegistrationConstants.TEMPLATE_DOB_LOCAL_LANG_LABEL,
				localProperties.getString("ageDatePicker"));
		if (dob != null && !dob.isEmpty()) {
			templateValues.put(RegistrationConstants.TEMPLATE_DOB,
					DateUtils.formatDate(DateUtils.parseToDate(dob, "yyyy/MM/dd"), "dd-MM-YYYY"));
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_DOB, RegistrationConstants.EMPTY);
		}
		templateValues.put(RegistrationConstants.TEMPLATE_AGE_USER_LANG_LABEL,
				applicationLanguageProperties.getString("ageField"));
		templateValues.put(RegistrationConstants.TEMPLATE_AGE_LOCAL_LANG_LABEL, localProperties.getString("ageField"));
		templateValues.put(RegistrationConstants.TEMPLATE_AGE, getValue(individualIdentity.getAge()));

		if (!getValue(individualIdentity.getAge()).isEmpty()) {
			templateValues.put(RegistrationConstants.TEMPLATE_YEARS_USER_LANG,
					applicationLanguageProperties.getString("years"));
			templateValues.put(RegistrationConstants.TEMPLATE_YEARS_LOCAL_LANG,
					"/ " + localProperties.getString("years"));
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_YEARS_USER_LANG, RegistrationConstants.EMPTY);
			templateValues.put(RegistrationConstants.TEMPLATE_YEARS_LOCAL_LANG, RegistrationConstants.EMPTY);
		}
		templateValues.put(RegistrationConstants.TEMPLATE_FOREIGNER_USER_LANG_LABEL,
				applicationLanguageProperties.getString("foreigner"));
		templateValues.put(RegistrationConstants.TEMPLATE_FOREIGNER_LOCAL_LANG_LABEL,
				localProperties.getString("foreigner"));
		templateValues.put(RegistrationConstants.TEMPLATE_RESIDENCE_STATUS,
				getValue(individualIdentity.getResidenceStatus(), platformLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_RESIDENCE_STATUS_LOCAL_LANG,
				getValue(individualIdentity.getResidenceStatus(), localLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1_USER_LANG_LABEL,
				applicationLanguageProperties.getString("addressLine1"));
		templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1_LOCAL_LANG_LABEL,
				localProperties.getString("addressLine1"));
		templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1,
				getValue(individualIdentity.getAddressLine1(), platformLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1_LOCAL_LANG,
				getValue(individualIdentity.getAddressLine1(), localLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2_USER_LANG_LABEL,
				applicationLanguageProperties.getString("addressLine2"));
		templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2_LOCAL_LANG_LABEL,
				localProperties.getString("addressLine2"));
		templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2,
				getValue(individualIdentity.getAddressLine2(), platformLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2_LOCAL_LANG,
				getValue(individualIdentity.getAddressLine2(), localLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_REGION_USER_LANG_LABEL,
				applicationLanguageProperties.getString("region"));
		templateValues.put(RegistrationConstants.TEMPLATE_REGION_LOCAL_LANG_LABEL, localProperties.getString("region"));
		templateValues.put(RegistrationConstants.TEMPLATE_REGION,
				getValue(individualIdentity.getRegion(), platformLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_REGION_LOCAL_LANG,
				getValue(individualIdentity.getRegion(), localLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_PROVINCE_USER_LANG_LABEL,
				applicationLanguageProperties.getString("province"));
		templateValues.put(RegistrationConstants.TEMPLATE_PROVINCE_LOCAL_LANG_LABEL,
				localProperties.getString("province"));
		templateValues.put(RegistrationConstants.TEMPLATE_PROVINCE,
				getValue(individualIdentity.getProvince(), platformLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_PROVINCE_LOCAL_LANG,
				getValue(individualIdentity.getProvince(), localLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY_USER_LANG_LABEL,
				applicationLanguageProperties.getString("localAdminAuthority"));
		templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG_LABEL,
				localProperties.getString("localAdminAuthority"));
		templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY,
				getValue(individualIdentity.getLocalAdministrativeAuthority(), platformLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG,
				getValue(individualIdentity.getLocalAdministrativeAuthority(), localLanguageCode));
		templateValues.put(RegistrationConstants.TEMPLATE_MOBILE_USER_LANG_LABEL,
				applicationLanguageProperties.getString("mobileNo"));
		templateValues.put(RegistrationConstants.TEMPLATE_MOBILE_LOCAL_LANG_LABEL,
				localProperties.getString("mobileNo"));
		templateValues.put(RegistrationConstants.TEMPLATE_MOBILE, getValue(individualIdentity.getPhone()));
		templateValues.put(RegistrationConstants.TEMPLATE_POSTAL_CODE_USER_LANG_LABEL,
				applicationLanguageProperties.getString("postalCode"));
		templateValues.put(RegistrationConstants.TEMPLATE_POSTAL_CODE_LOCAL_LANG_LABEL,
				localProperties.getString("postalCode"));
		templateValues.put(RegistrationConstants.TEMPLATE_POSTAL_CODE, getValue(individualIdentity.getPostalCode()));
		templateValues.put(RegistrationConstants.TEMPLATE_EMAIL_USER_LANG_LABEL,
				applicationLanguageProperties.getString("emailId"));
		templateValues.put(RegistrationConstants.TEMPLATE_EMAIL_LOCAL_LANG_LABEL, localProperties.getString("emailId"));

		String email = getValue(individualIdentity.getEmail());
		if (email != null && !email.isEmpty()) {
			templateValues.put(RegistrationConstants.TEMPLATE_EMAIL, email);
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_EMAIL, RegistrationConstants.EMPTY);
		}

		templateValues.put(RegistrationConstants.TEMPLATE_CNIE_NUMBER_USER_LANG_LABEL,
				applicationLanguageProperties.getString("cniOrPinNumber"));
		templateValues.put(RegistrationConstants.TEMPLATE_CNIE_LOCAL_LANG_LABEL,
				localProperties.getString("cniOrPinNumber"));
		templateValues.put(RegistrationConstants.TEMPLATE_CNIE_NUMBER, getValue(individualIdentity.getCnieNumber()));
		boolean isChild = individualIdentity.getParentOrGuardianName() != null;

		if (isChild || registration.isUpdateUINNonBiometric()) {
			templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME_USER_LANG_LABEL,
					applicationLanguageProperties.getString("parentName"));
			templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME,
					getValue(individualIdentity.getParentOrGuardianName(), platformLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME_LOCAL_LANG_LABEL,
					localProperties.getString("parentName"));
			templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME_LOCAL_LANG,
					getValue(individualIdentity.getParentOrGuardianName(), localLanguageCode));
			if (registration.getSelectionListDTO() != null) {
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_UIN_USER_LANG_LABEL,
						applicationLanguageProperties.getString("uinUpdateParentUIN"));
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_UIN_LOCAL_LANG_LABEL,
						localProperties.getString("uinUpdateParentUIN"));
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_UIN_USER_LANG_LABEL,
						applicationLanguageProperties.getString("parentUIN"));
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_UIN_LOCAL_LANG_LABEL,
						localProperties.getString("parentUIN"));
			}
			templateValues.put(RegistrationConstants.TEMPLATE_PARENT_UIN,
					getValue(individualIdentity.getParentOrGuardianRID() == null
							? individualIdentity.getParentOrGuardianUIN()
							: individualIdentity.getParentOrGuardianRID()));
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_WITH_PARENT,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		}
	}

	private void setUpPreviewContent(RegistrationDTO registration, Map<String, Object> templateValues, boolean isChild,
			ResponseDTO response, ResourceBundle applicationLanguageProperties, ResourceBundle localProperties,
			String fingerPrintDisableFlag) {
		ByteArrayOutputStream byteArrayOutputStream = null;

		templateValues.put(RegistrationConstants.TEMPLATE_ACKNOWLEDGEMENT,
				RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		templateValues.put(RegistrationConstants.TEMPLATE_PRE_REG_ID_USER_LANG_LABEL,
				applicationLanguageProperties.getString("preRegistrationId"));
		templateValues.put(RegistrationConstants.TEMPLATE_PRE_REG_ID_LOCAL_LANG_LABEL,
				localProperties.getString("preRegistrationId"));
		if (registration.getPreRegistrationId() != null && !registration.getPreRegistrationId().isEmpty()) {
			templateValues.put(RegistrationConstants.TEMPLATE_PRE_REG_ID, registration.getPreRegistrationId());
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_PRE_REG_ID, "-");
		}

		templateValues.put(RegistrationConstants.TEMPLATE_MODIFY, applicationLanguageProperties.getString("modify"));

		try {
			BufferedImage modifyImage = ImageIO
					.read(this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_MODIFY_IMAGE_PATH));
			byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(modifyImage, RegistrationConstants.IMAGE_FORMAT_PNG, byteArrayOutputStream);
			byte[] modifyImageBytes = byteArrayOutputStream.toByteArray();
			String modifyImageEncodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(modifyImageBytes, false));
			templateValues.put(RegistrationConstants.TEMPLATE_MODIFY_IMAGE_SOURCE,
					RegistrationConstants.TEMPLATE_PNG_IMAGE_ENCODING + modifyImageEncodedBytes);
		} catch (IOException ioException) {
			setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
			LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		} finally {
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
				} catch (IOException exception) {
					setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
					LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
							exception.getMessage() + ExceptionUtils.getStackTrace(exception));
				}
			}
		}
		if (RegistrationConstants.ENABLE.equalsIgnoreCase(fingerPrintDisableFlag)) {
			boolean leftPalmCaptured = false;
			boolean rightPalmCaptured = false;
			boolean thumbsCaptured = false;
			List<FingerprintDetailsDTO> fingerprintDetailsDTO;
			if (isChild || registration.isUpdateUINNonBiometric()) {
				fingerprintDetailsDTO = registration.getBiometricDTO().getIntroducerBiometricDTO()
						.getFingerprintDetailsDTO();
			} else {
				fingerprintDetailsDTO = registration.getBiometricDTO().getApplicantBiometricDTO()
						.getFingerprintDetailsDTO();
			}
			for (FingerprintDetailsDTO fpDetailsDTO : fingerprintDetailsDTO) {
				if (fpDetailsDTO.getFingerType().contains(RegistrationConstants.LEFTPALM)) {
					leftPalmCaptured = true;
					byte[] leftPalmBytes = fpDetailsDTO.getFingerPrint();
					String leftPalmEncodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(leftPalmBytes, false));
					templateValues.put(RegistrationConstants.TEMPLATE_CAPTURED_LEFT_SLAP,
							RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + leftPalmEncodedBytes);
				} else if (fpDetailsDTO.getFingerType().contains(RegistrationConstants.RIGHTPALM)) {
					rightPalmCaptured = true;
					byte[] rightPalmBytes = fpDetailsDTO.getFingerPrint();
					String rightPalmEncodedBytes = StringUtils
							.newStringUtf8(Base64.encodeBase64(rightPalmBytes, false));
					templateValues.put(RegistrationConstants.TEMPLATE_CAPTURED_RIGHT_SLAP,
							RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + rightPalmEncodedBytes);
				} else if (fpDetailsDTO.getFingerType().contains(RegistrationConstants.THUMBS)) {
					thumbsCaptured = true;
					byte[] thumbsBytes = fpDetailsDTO.getFingerPrint();
					String thumbsEncodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(thumbsBytes, false));
					templateValues.put(RegistrationConstants.TEMPLATE_CAPTURED_THUMBS,
							RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + thumbsEncodedBytes);
				}
			}
			if (!leftPalmCaptured) {
				templateValues.put(RegistrationConstants.TEMPLATE_LEFT_SLAP_CAPTURED,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
			if (!rightPalmCaptured) {
				templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_SLAP_CAPTURED,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
			if (!thumbsCaptured) {
				templateValues.put(RegistrationConstants.TEMPLATE_THUMBS_CAPTURED,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
		}

		templateValues.put(RegistrationConstants.TEMPLATE_CONSENT_HEADING,
				applicationLanguageProperties.getString("consentHeading"));
		templateValues.put(RegistrationConstants.TEMPLATE_CONSENT_DATA, consentText);
		templateValues.put(RegistrationConstants.TEMPLATE_CONSENT_YES, applicationLanguageProperties.getString("yes"));
		templateValues.put(RegistrationConstants.TEMPLATE_CONSENT_NO, applicationLanguageProperties.getString("no"));
		if (registration.getRegistrationMetaDataDTO().getConsentOfApplicant() != null) {
			String consent = registration.getRegistrationMetaDataDTO().getConsentOfApplicant();
			if (consent.equalsIgnoreCase(RegistrationConstants.YES)) {
				templateValues.put(RegistrationConstants.TEMPLATE_CONSENT_SELECTED_YES,
						RegistrationConstants.TEMPLATE_CONSENT_CHECKED);
			} else if (consent.equalsIgnoreCase(RegistrationConstants.NO)) {
				templateValues.put(RegistrationConstants.TEMPLATE_CONSENT_SELECTED_NO,
						RegistrationConstants.TEMPLATE_CONSENT_CHECKED);
			}
		}
	}

	private void setUpAcknowledgementContent(RegistrationDTO registration, Map<String, Object> templateValues,
			ResponseDTO response, ResourceBundle applicationLanguageProperties, ResourceBundle localProperties,
			String fingerPrintDisableFlag, String irisDisableFlag, IndividualIdentity individualIdentity) {
		ByteArrayOutputStream byteArrayOutputStream = null;

		templateValues.put(RegistrationConstants.TEMPLATE_PREVIEW, RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		templateValues.put(RegistrationConstants.TEMPLATE_RID_USER_LANG_LABEL,
				applicationLanguageProperties.getString("registrationid"));
		templateValues.put(RegistrationConstants.TEMPLATE_RID_LOCAL_LANG_LABEL,
				localProperties.getString("registrationid"));
		templateValues.put(RegistrationConstants.TEMPLATE_RID, registration.getRegistrationId());
		if (registration.getRegistrationMetaDataDTO().getUin() != null
				&& !registration.getRegistrationMetaDataDTO().getUin().isEmpty()) {
			templateValues.put(RegistrationConstants.TEMPLATE_HEADER_TABLE,
					RegistrationConstants.TEMPLATE_UIN_HEADER_TABLE);
			templateValues.put(RegistrationConstants.TEMPLATE_UIN_USER_LANG_LABEL,
					applicationLanguageProperties.getString("uin"));
			templateValues.put(RegistrationConstants.TEMPLATE_UIN_LOCAL_LANG_LABEL, localProperties.getString("uin"));
			templateValues.put(RegistrationConstants.TEMPLATE_UIN, registration.getRegistrationMetaDataDTO().getUin());
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_HEADER_TABLE,
					RegistrationConstants.TEMPLATE_HEADER_TABLE);
			templateValues.put(RegistrationConstants.TEMPLATE_UIN_UPDATE,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
		}

		// QR Code Generation
		generateQRCode(registration, templateValues, response, applicationLanguageProperties);

		if (RegistrationConstants.ENABLE.equalsIgnoreCase(irisDisableFlag)) {
			try {
				BufferedImage eyeImage = ImageIO
						.read(this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_EYE_IMAGE_PATH));
				byteArrayOutputStream = new ByteArrayOutputStream();
				ImageIO.write(eyeImage, RegistrationConstants.IMAGE_FORMAT_PNG, byteArrayOutputStream);
				byte[] eyeImageBytes = byteArrayOutputStream.toByteArray();
				String eyeImageEncodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(eyeImageBytes, false));
				templateValues.put(RegistrationConstants.TEMPLATE_EYE_IMAGE_SOURCE,
						RegistrationConstants.TEMPLATE_PNG_IMAGE_ENCODING + eyeImageEncodedBytes);
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
						LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
								exception.getMessage() + ExceptionUtils.getStackTrace(exception));
					}
				}
			}
		}

		if (RegistrationConstants.ENABLE.equalsIgnoreCase(fingerPrintDisableFlag)) {
			try {
				BufferedImage leftPalmImage = ImageIO
						.read(this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_LEFT_SLAP_IMAGE_PATH));
				byteArrayOutputStream = new ByteArrayOutputStream();
				ImageIO.write(leftPalmImage, RegistrationConstants.IMAGE_FORMAT_PNG, byteArrayOutputStream);
				byte[] leftPalmImageBytes = byteArrayOutputStream.toByteArray();
				String leftPalmImageEncodedBytes = StringUtils
						.newStringUtf8(Base64.encodeBase64(leftPalmImageBytes, false));
				templateValues.put(RegistrationConstants.TEMPLATE_LEFT_PALM_IMAGE_SOURCE,
						RegistrationConstants.TEMPLATE_PNG_IMAGE_ENCODING + leftPalmImageEncodedBytes);
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
						LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
								exception.getMessage() + ExceptionUtils.getStackTrace(exception));
					}
				}
			}

			try {
				BufferedImage rightPalmImage = ImageIO.read(
						this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_RIGHT_SLAP_IMAGE_PATH));
				byteArrayOutputStream = new ByteArrayOutputStream();
				ImageIO.write(rightPalmImage, RegistrationConstants.IMAGE_FORMAT_PNG, byteArrayOutputStream);
				byte[] rightPalmImageBytes = byteArrayOutputStream.toByteArray();
				String rightPalmImageEncodedBytes = StringUtils
						.newStringUtf8(Base64.encodeBase64(rightPalmImageBytes, false));
				templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_PALM_IMAGE_SOURCE,
						RegistrationConstants.TEMPLATE_PNG_IMAGE_ENCODING + rightPalmImageEncodedBytes);
			} catch (IOException ioException) {
				setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
				LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			} finally {
				if (byteArrayOutputStream != null) {
					try {
						byteArrayOutputStream.close();
					} catch (IOException exception) {
						setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION,
								null);
						LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
								exception.getMessage() + ExceptionUtils.getStackTrace(exception));
					}
				}
			}

			try {
				BufferedImage thumbsImage = ImageIO
						.read(this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_THUMBS_IMAGE_PATH));
				byteArrayOutputStream = new ByteArrayOutputStream();
				ImageIO.write(thumbsImage, RegistrationConstants.IMAGE_FORMAT_PNG, byteArrayOutputStream);
				byte[] thumbsImageBytes = byteArrayOutputStream.toByteArray();
				String thumbsImageEncodedBytes = StringUtils
						.newStringUtf8(Base64.encodeBase64(thumbsImageBytes, false));
				templateValues.put(RegistrationConstants.TEMPLATE_THUMBS_IMAGE_SOURCE,
						RegistrationConstants.TEMPLATE_PNG_IMAGE_ENCODING + thumbsImageEncodedBytes);
			} catch (IOException ioException) {
				setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
				LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			} finally {
				if (byteArrayOutputStream != null) {
					try {
						byteArrayOutputStream.close();
					} catch (IOException exception) {
						setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION,
								null);
						LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
								exception.getMessage() + ExceptionUtils.getStackTrace(exception));
					}
				}
			}
		}
	}

	private void generateQRCode(RegistrationDTO registration, Map<String, Object> templateValues, ResponseDTO response,
			ResourceBundle applicationLanguageProperties) {
		StringBuilder qrCodeString = new StringBuilder();

		qrCodeString.append(applicationLanguageProperties.getString("registrationid")).append(" : ").append("\n")
				.append(registration.getRegistrationId());
		try {
			byte[] qrCodeInBytes = qrCodeGenerator.generateQrCode(qrCodeString.toString(), QrVersion.V4);

			String qrCodeImageEncodedBytes = CryptoUtil.encodeBase64(qrCodeInBytes);
			templateValues.put(RegistrationConstants.TEMPLATE_QRCODE_SOURCE,
					RegistrationConstants.TEMPLATE_PNG_IMAGE_ENCODING + qrCodeImageEncodedBytes);
		} catch (IOException | QrcodeGenerationException exception) {
			setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
			LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
		}
	}

	private void addToCapturedBiometrics(StringBuilder biometricsCaptured, StringBuilder biometricsCapturedLocalLang,
			ResourceBundle applicationLanguageProperties, ResourceBundle localProperties, String biometricType,
			int count) {
		if (biometricsCaptured.length() > 1) {
			biometricsCaptured.append(applicationLanguageProperties.getString("comma"));
			biometricsCapturedLocalLang.append(localProperties.getString("comma"));
		}
		biometricsCaptured.append(MessageFormat.format((String) applicationLanguageProperties.getString(biometricType),
				String.valueOf(count)));
		biometricsCapturedLocalLang
				.append(MessageFormat.format(localProperties.getString(biometricType), String.valueOf(count)));
	}

	private Map<String, Object> countMissingIrises(Map<String, Object> templateValues, RegistrationDTO registration,
			boolean isChild, String templateType) {
		if (RegistrationConstants.ENABLE.equalsIgnoreCase(
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.IRIS_DISABLE_FLAG)))) {
			List<IrisDetailsDTO> irisDetailsDTOs;
			if (isChild || registration.isUpdateUINNonBiometric()) {
				irisDetailsDTOs = registration.getBiometricDTO().getIntroducerBiometricDTO().getIrisDetailsDTO();
			} else {
				irisDetailsDTOs = registration.getBiometricDTO().getApplicantBiometricDTO().getIrisDetailsDTO();
			}
			if (irisDetailsDTOs.size() == 2) {
				if (templateType.equals(RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE)) {
					templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE,
							RegistrationConstants.TEMPLATE_RIGHT_MARK);
					templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE,
							RegistrationConstants.TEMPLATE_RIGHT_MARK);
				} else {
					for (IrisDetailsDTO capturedIris : irisDetailsDTOs) {
						if (capturedIris.getIrisType().contains(RegistrationConstants.LEFT)) {
							byte[] leftIrisBytes = capturedIris.getIris();
							String leftIrisEncodedBytes = StringUtils
									.newStringUtf8(Base64.encodeBase64(leftIrisBytes, false));
							templateValues.put(RegistrationConstants.TEMPLATE_CAPTURED_LEFT_EYE,
									RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + leftIrisEncodedBytes);
						} else if (capturedIris.getIrisType().contains(RegistrationConstants.RIGHT)) {
							byte[] rightIrisBytes = capturedIris.getIris();
							String rightIrisEncodedBytes = StringUtils
									.newStringUtf8(Base64.encodeBase64(rightIrisBytes, false));
							templateValues.put(RegistrationConstants.TEMPLATE_CAPTURED_RIGHT_EYE,
									RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + rightIrisEncodedBytes);
						}
					}
				}

			} else if (irisDetailsDTOs.size() == 1) {
				if (irisDetailsDTOs.get(0).getIrisType().contains(RegistrationConstants.LEFT)) {
					if (templateType.equals(RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE)) {
						if (isChild || registration.isUpdateUINNonBiometric()) {
							templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE,
									RegistrationConstants.TEMPLATE_RIGHT_MARK);
							templateValues.put(RegistrationConstants.PARENT_RIGHT_EYE,
									RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
						} else {
							templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE,
									RegistrationConstants.TEMPLATE_RIGHT_MARK);
							templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE,
									RegistrationConstants.TEMPLATE_CROSS_MARK);
						}
					} else {
						byte[] leftIrisBytes = irisDetailsDTOs.get(0).getIris();
						String leftIrisEncodedBytes = StringUtils
								.newStringUtf8(Base64.encodeBase64(leftIrisBytes, false));
						templateValues.put(RegistrationConstants.TEMPLATE_CAPTURED_LEFT_EYE,
								RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + leftIrisEncodedBytes);
						templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE_CAPTURED,
								RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
					}

				} else {
					if (templateType.equals(RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE)) {
						if (isChild || registration.isUpdateUINNonBiometric()) {
							templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE,
									RegistrationConstants.TEMPLATE_RIGHT_MARK);
							templateValues.put(RegistrationConstants.PARENT_LEFT_EYE,
									RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
						} else {
							templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE,
									RegistrationConstants.TEMPLATE_CROSS_MARK);
							templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE,
									RegistrationConstants.TEMPLATE_RIGHT_MARK);
						}
					} else {
						byte[] rightIrisBytes = irisDetailsDTOs.get(0).getIris();
						String rightIrisEncodedBytes = StringUtils
								.newStringUtf8(Base64.encodeBase64(rightIrisBytes, false));
						templateValues.put(RegistrationConstants.TEMPLATE_CAPTURED_RIGHT_EYE,
								RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + rightIrisEncodedBytes);
						templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE_CAPTURED,
								RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
					}
				}
			} else if (irisDetailsDTOs.isEmpty()) {
				if (templateType.equals(RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE)) {
					if (isChild || registration.isUpdateUINNonBiometric()) {
						if (registration.getBiometricDTO().getIntroducerBiometricDTO().getFace() != null && registration
								.getBiometricDTO().getIntroducerBiometricDTO().getFace().getFace() != null) {
							templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE,
									RegistrationConstants.TEMPLATE_CROSS_MARK);
							templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE,
									RegistrationConstants.TEMPLATE_CROSS_MARK);
						} else {
							templateValues.put(RegistrationConstants.PARENT_LEFT_EYE,
									RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
							templateValues.put(RegistrationConstants.PARENT_RIGHT_EYE,
									RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
						}
					} else {
						templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE,
								RegistrationConstants.TEMPLATE_CROSS_MARK);
						templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE,
								RegistrationConstants.TEMPLATE_CROSS_MARK);
					}
				} else {
					templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE_CAPTURED,
							RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
					templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE_CAPTURED,
							RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
				}
			}
		}
		return templateValues;
	}

	private void countMissingFingers(RegistrationDTO registration, Map<String, Object> templateValues,
			ResourceBundle applicationLanguageProperties, ResourceBundle localProperties) {
		int missingLeftFingers = 0;
		int missingRightFingers = 0;
		int missingThumbs = 0;
		List<BiometricExceptionDTO> exceptionFingers = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getBiometricExceptionDTO();
		if (exceptionFingers != null) {
			for (BiometricExceptionDTO exceptionFinger : exceptionFingers) {
				if (exceptionFinger.getBiometricType().equalsIgnoreCase(RegistrationConstants.FINGERPRINT)) {
					if (exceptionFinger.getMissingBiometric().toLowerCase()
							.contains(RegistrationConstants.THUMB.toLowerCase())) {
						missingThumbs++;
					} else if (exceptionFinger.getMissingBiometric().toLowerCase()
							.contains(RegistrationConstants.LEFT.toLowerCase())) {
						missingLeftFingers++;
					} else if (exceptionFinger.getMissingBiometric().toLowerCase()
							.contains(RegistrationConstants.RIGHT.toLowerCase())) {
						missingRightFingers++;
					}
				}
			}
			if (missingLeftFingers != 0) {
				templateValues.put(RegistrationConstants.TEMPLATE_LEFT_SLAP_EXCEPTION_USER_LANG,
						missingLeftFingers + " " + applicationLanguageProperties.getString("exceptionCount"));
				templateValues.put(RegistrationConstants.TEMPLATE_LEFT_SLAP_EXCEPTION_LOCAL_LANG,
						localProperties.getString("exceptionCount") + " " + missingLeftFingers);
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_MISSING_LEFT_FINGERS,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
			if (missingRightFingers != 0) {
				templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_SLAP_EXCEPTION_USER_LANG,
						missingRightFingers + " " + applicationLanguageProperties.getString("exceptionCount"));
				templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_SLAP_EXCEPTION_LOCAL_LANG,
						localProperties.getString("exceptionCount") + " " + missingRightFingers);
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_MISSING_RIGHT_FINGERS,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
			if (missingThumbs != 0) {
				templateValues.put(RegistrationConstants.TEMPLATE_THUMBS_EXCEPTION_USER_LANG,
						missingThumbs + " " + applicationLanguageProperties.getString("exceptionCount"));
				templateValues.put(RegistrationConstants.TEMPLATE_THUMBS_EXCEPTION_LOCAL_LANG,
						localProperties.getString("exceptionCount") + " " + missingThumbs);
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_MISSING_THUMBS,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
		}
	}

	/**
	 * This method generates the content that will be sent to the applicant via email/SMS after a successful 
	 * registration.
	 * 
	 * <p>
	 * The details that are required to be attached in the email/SMS will be mapped to the place-holders given
	 * in the HTML template and then, the template is build.
	 * </p>
	 * 
	 * <p>
	 * Returns the generated content in string format.
	 * </p>
	 * 
	 * @param templateText
	 *            - string which contains the data of template that is used to
	 *            generate notification
	 * @param registration
	 *            - RegistrationDTO to display required fields on the template
	 * @param templateManagerBuilder
	 *            - The Builder which generates template by mapping values to
	 *            respective place-holders in template
	 * @return writer - After mapping all the fields into the template, it is
	 *         written into a StringWriter and returned
	 */
	public Writer generateNotificationTemplate(String templateText, RegistrationDTO registration,
			TemplateManagerBuilder templateManagerBuilder) {

		try {
			String applicationLanguageCode = ApplicationContext.applicationLanguage().toLowerCase();
			InputStream is = new ByteArrayInputStream(templateText.getBytes());
			Map<String, Object> values = new LinkedHashMap<>();
			IndividualIdentity individualIdentity = (IndividualIdentity) registration.getDemographicDTO()
					.getDemographicInfoDTO().getIdentity();

			values.put(RegistrationConstants.TEMPLATE_RESIDENT_NAME,
					getValue(registration.isNameNotUpdated() ? registration.getRegistrationMetaDataDTO().getFullName()
							: individualIdentity.getFullName(), applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_RID, getValue(registration.getRegistrationId()));

			SimpleDateFormat sdf = new SimpleDateFormat(RegistrationConstants.TEMPLATE_DATE_FORMAT);
			String currentDate = sdf.format(new Date());

			values.put(RegistrationConstants.TEMPLATE_DATE, currentDate);
			values.put(RegistrationConstants.TEMPLATE_FULL_NAME,
					getValue(registration.isNameNotUpdated() ? registration.getRegistrationMetaDataDTO().getFullName()
							: individualIdentity.getFullName(), applicationLanguageCode));
			String dob = getValue(individualIdentity.getDateOfBirth());

			if (dob == null || dob == "") {
				values.put(RegistrationConstants.TEMPLATE_DOB, getValue(individualIdentity.getAge()));
			} else {
				values.put(RegistrationConstants.TEMPLATE_DOB,
						DateUtils.formatDate(DateUtils.parseToDate(dob, "yyyy/MM/dd"), "dd-MM-YYYY"));
			}

			values.put(RegistrationConstants.TEMPLATE_GENDER,
					getValue(individualIdentity.getGender(), applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1,
					getValue(individualIdentity.getAddressLine1(), applicationLanguageCode));
			String addressLine2 = getValue(individualIdentity.getAddressLine2(), applicationLanguageCode);
			if (addressLine2 == null || addressLine2.isEmpty()) {
				values.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2, RegistrationConstants.EMPTY);
			} else {
				values.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2, addressLine2);
			}
			String addressLine3 = getValue(individualIdentity.getAddressLine3(), applicationLanguageCode);
			if (addressLine3 == null || addressLine3.isEmpty()) {
				values.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE3, RegistrationConstants.EMPTY);
			} else {
				values.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE3, addressLine3);
			}
			values.put(RegistrationConstants.TEMPLATE_PROVINCE,
					getValue(individualIdentity.getProvince(), applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_CITY,
					getValue(individualIdentity.getCity(), applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_REGION,
					getValue(individualIdentity.getRegion(), applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_POSTAL_CODE, getValue(individualIdentity.getPostalCode()));
			values.put(RegistrationConstants.TEMPLATE_MOBILE, getValue(individualIdentity.getPhone()));

			String email = getValue(individualIdentity.getEmail());
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
				LOGGER.info(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
						"generateNotificationTemplate method has been ended for preparing Notification Template.");
			}
			return writer;
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
	@SuppressWarnings({ "unchecked" })
	private Map<String, Integer> getFingerPrintQualityRanking(RegistrationDTO registration) {
		// for storing the fingerprints captured and their respective quality scores
		Map<String, Double> fingersQuality = new WeakHashMap<>();

		// list of missing fingers
		List<BiometricExceptionDTO> exceptionFingers = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getBiometricExceptionDTO();

		if (exceptionFingers != null) {
			for (BiometricExceptionDTO exceptionFinger : exceptionFingers) {
				if (exceptionFinger.getBiometricType().equalsIgnoreCase(RegistrationConstants.FINGERPRINT)
						&& exceptionFinger.getReason().equals(RegistrationConstants.MISSING_BIOMETRICS)) {
					fingersQuality.put(exceptionFinger.getMissingBiometric(), (double) 0);
				}
			}
		}
		List<FingerprintDetailsDTO> availableFingers = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getFingerprintDetailsDTO();
		for (FingerprintDetailsDTO availableFinger : availableFingers) {
			List<FingerprintDetailsDTO> segmentedFingers = availableFinger.getSegmentedFingerprints();
			for (FingerprintDetailsDTO segmentedFinger : segmentedFingers) {
				fingersQuality.put(segmentedFinger.getFingerType(), segmentedFinger.getQualityScore());
			}
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

		Map<String, Integer> fingersQualityRanking = new WeakHashMap<>();
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

	@SuppressWarnings("unchecked")
	private String getValue(Object fieldValue, String lang) {
		LOGGER.info(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting values of demographic fields in given specific language");
		String value = RegistrationConstants.EMPTY;

		if (fieldValue instanceof List<?>) {
			Optional<ValuesDTO> demoValueInRequiredLang = ((List<ValuesDTO>) fieldValue).stream()
					.filter(valueDTO -> valueDTO.getLanguage().equals(lang)).findFirst();

			if (demoValueInRequiredLang.isPresent() && demoValueInRequiredLang.get().getValue() != null) {
				value = demoValueInRequiredLang.get().getValue();
			}
		}

		LOGGER.info(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting values of demographic fields in given specific language has been completed");
		return value;
	}

	private String getValue(Object fieldValue) {
		LOGGER.info(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID, "Getting values of demographic fields");
		String value = RegistrationConstants.EMPTY;

		if (fieldValue instanceof String || fieldValue instanceof Integer || fieldValue instanceof BigInteger
				|| fieldValue instanceof Double) {
			value = String.valueOf(fieldValue);
		}

		LOGGER.info(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting values of demographic fields has been completed");
		return value;
	}
<<<<<<< HEAD
=======

	/**
	 * To count the number of exceptions for face/iris/fingerprint
	 */
	private Map<String, Integer> exceptionFingersCount(RegistrationDTO registration, int leftSlapCount,
			int rightSlapCount, int thumbCount, int irisCount) {

		Map<String, Integer> exceptionCountMap = new HashMap<>();
		List<BiometricExceptionDTO> biometricExceptionDTOs;
		if ((registration.getSelectionListDTO() == null && (boolean) SessionContext.map().get(RegistrationConstants.IS_Child))
				|| (registration.getSelectionListDTO() != null
						&& registration.getSelectionListDTO().isParentOrGuardianDetails())) {
			biometricExceptionDTOs = registration.getBiometricDTO().getIntroducerBiometricDTO()
					.getBiometricExceptionDTO();
			for (BiometricExceptionDTO biometricExceptionDTO : biometricExceptionDTOs) {
				if ((biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.LEFT.toLowerCase())
						&& biometricExceptionDTO.isMarkedAsException())
						&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.THUMB)
						&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.EYE)) {
					leftSlapCount++;
				}
				if ((biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.RIGHT.toLowerCase())
						&& biometricExceptionDTO.isMarkedAsException())
						&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.THUMB)
						&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.EYE)) {
					rightSlapCount++;
				}
				if ((biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.THUMB)
						&& biometricExceptionDTO.isMarkedAsException())) {
					thumbCount++;
				}
				if ((biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.EYE)
						&& biometricExceptionDTO.isMarkedAsException())) {
					irisCount++;
				}
			}
			exceptionCountMap.put(RegistrationConstants.LEFTSLAPCOUNT, leftSlapCount);
			exceptionCountMap.put(RegistrationConstants.RIGHTSLAPCOUNT, rightSlapCount);
			exceptionCountMap.put(RegistrationConstants.THUMBCOUNT, thumbCount);
			exceptionCountMap.put(RegistrationConstants.EXCEPTIONCOUNT,
					leftSlapCount + rightSlapCount + thumbCount + irisCount);
		}

		return exceptionCountMap;
	}
>>>>>>> 55442bec8b0b7257e86524eff51c77f99a33dc9f
}