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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
@Controller
public class TemplateGenerator extends BaseService {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(TemplateGenerator.class);

	@Autowired
	QrCodeGenerator<QrVersion> qrCodeGenerator;

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
			TemplateManagerBuilder templateManagerBuilder, String templateType) {

		ResponseDTO response = new ResponseDTO();

		try {
			LOGGER.info(LOG_TEMPLATE_GENERATOR, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					"generateTemplate had been called for preparing Acknowledgement Template.");

			ResourceBundle localProperties = ApplicationContext.localLanguageProperty();
			ResourceBundle applicationLanguageProperties = ApplicationContext.applicationLanguageBundle();

			// Reader templateReader = new BufferedReader(
			// new InputStreamReader(new ByteArrayInputStream(templateText.getBytes())));
			//
			// VelocityContext templateValues = new VelocityContext();

			InputStream is = new ByteArrayInputStream(templateText.getBytes());
			Map<String, Object> templateValues = new HashMap<>();
			ByteArrayOutputStream byteArrayOutputStream = null;

			String platformLanguageCode = ApplicationContext.applicationLanguage().toLowerCase();
			String localLanguageCode = ApplicationContext.localLanguage().toLowerCase();

			String dob = getValue(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getDateOfBirth(), null);

			templateValues.put(RegistrationConstants.TEMPLATE_DATE_USER_LANG_LABEL,
					applicationLanguageProperties.getString("date"));
			templateValues.put(RegistrationConstants.TEMPLATE_DATE_LOCAL_LANG_LABEL, localProperties.getString("date"));

			SimpleDateFormat sdf = new SimpleDateFormat(RegistrationConstants.TEMPLATE_DATE_FORMAT);
			String currentDate = sdf.format(new Date());

			// map the respective fields with the values in the registrationDTO
			templateValues.put(RegistrationConstants.TEMPLATE_DATE, currentDate);

			if (templateType.equals(RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE)) {
				templateValues.put(RegistrationConstants.TEMPLATE_PREVIEW,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
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
					templateValues.put(RegistrationConstants.TEMPLATE_UIN_LOCAL_LANG_LABEL,
							localProperties.getString("uin"));
					templateValues.put(RegistrationConstants.TEMPLATE_UIN,
							registration.getRegistrationMetaDataDTO().getUin());
				} else {
					templateValues.put(RegistrationConstants.TEMPLATE_HEADER_TABLE,
							RegistrationConstants.TEMPLATE_HEADER_TABLE);
					templateValues.put(RegistrationConstants.TEMPLATE_UIN_UPDATE,
							RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
				}
				// QR Code Generation
				StringBuilder qrCodeString = new StringBuilder();
				qrCodeString.append(applicationLanguageProperties.getString("fullName")).append(" : ")
						.append(getValue(
								registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getFullName(),
								platformLanguageCode));
				qrCodeString.append("\n");
				qrCodeString.append(applicationLanguageProperties.getString("age/dob")).append(" : ");

				if (dob == "") {
					qrCodeString.append(getValue(
							registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAge(), null));
				} else {
					qrCodeString.append(DateUtils.formatDate(DateUtils.parseToDate(dob, "yyyy/MM/dd"), "dd-MM-YYYY"));
				}

				qrCodeString.append("\n");
				qrCodeString.append(applicationLanguageProperties.getString("address")).append(" : ");
				qrCodeString.append(getValue(
						registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine1(),
						platformLanguageCode));
				qrCodeString.append("\n");
				qrCodeString.append(getValue(
						registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine2(),
						platformLanguageCode));
				qrCodeString.append("\n");
				qrCodeString.append(applicationLanguageProperties.getString("uinId")).append(" : ")
						.append(registration.getRegistrationId());
				qrCodeString.append("\n");
				qrCodeString.append(applicationLanguageProperties.getString("gender")).append(" : ")
						.append(getValue(
								registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getGender(),
								platformLanguageCode));
				qrCodeString.append("\n");

				try {
					byte[] qrCodeInBytes;
					if (registration.getDemographicDTO().getApplicantDocumentDTO().getCompressedFacePhoto() != null) {
						byte[] applicantPhoto = registration.getDemographicDTO().getApplicantDocumentDTO()
								.getCompressedFacePhoto();

						qrCodeString.append(applicationLanguageProperties.getString("image")).append(" : ")
								.append(CryptoUtil.encodeBase64(applicantPhoto));

						qrCodeInBytes = qrCodeGenerator.generateQrCode(qrCodeString.toString(), QrVersion.V30);
					} else {
						qrCodeInBytes = qrCodeGenerator.generateQrCode(qrCodeString.toString(), QrVersion.V25);
					}

					String qrCodeImageEncodedBytes = CryptoUtil.encodeBase64(qrCodeInBytes);
					templateValues.put(RegistrationConstants.TEMPLATE_QRCODE_SOURCE,
							RegistrationConstants.TEMPLATE_PNG_IMAGE_ENCODING + qrCodeImageEncodedBytes);
				} catch (IOException | QrcodeGenerationException exception) {
					setErrorResponse(response, RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION, null);
					LOGGER.error(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
				}

				templateValues = countMissingIrises(templateValues, registration);

				try {
					BufferedImage eyeImage = ImageIO
							.read(this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_EYE_IMAGE_PATH));
					byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(eyeImage, RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);
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
									exception.getMessage());
						}
					}
				}

				try {
					BufferedImage leftPalmImage = ImageIO.read(
							this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_LEFT_SLAP_IMAGE_PATH));
					byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(leftPalmImage, RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);
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
									exception.getMessage());
						}
					}
				}

				try {
					BufferedImage rightPalmImage = ImageIO.read(
							this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_RIGHT_SLAP_IMAGE_PATH));
					byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(rightPalmImage, RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);
					byte[] rightPalmImageBytes = byteArrayOutputStream.toByteArray();
					String rightPalmImageEncodedBytes = StringUtils
							.newStringUtf8(Base64.encodeBase64(rightPalmImageBytes, false));
					templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_PALM_IMAGE_SOURCE,
							RegistrationConstants.TEMPLATE_PNG_IMAGE_ENCODING + rightPalmImageEncodedBytes);
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
									exception.getMessage());
						}
					}
				}

				try {
					BufferedImage thumbsImage = ImageIO.read(
							this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_THUMBS_IMAGE_PATH));
					byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(thumbsImage, RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);
					byte[] thumbsImageBytes = byteArrayOutputStream.toByteArray();
					String thumbsImageEncodedBytes = StringUtils
							.newStringUtf8(Base64.encodeBase64(thumbsImageBytes, false));
					templateValues.put(RegistrationConstants.TEMPLATE_THUMBS_IMAGE_SOURCE,
							RegistrationConstants.TEMPLATE_PNG_IMAGE_ENCODING + thumbsImageEncodedBytes);
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
									exception.getMessage());
						}
					}
				}

			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_ACKNOWLEDGEMENT,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
				templateValues.put(RegistrationConstants.TEMPLATE_PRE_REG_ID_USER_LANG_LABEL,
						applicationLanguageProperties.getString("preRegistrationId"));
				templateValues.put(RegistrationConstants.TEMPLATE_PRE_REG_ID_LOCAL_LANG_LABEL,
						localProperties.getString("preRegistrationId"));
				if (!registration.getPreRegistrationId().isEmpty()) {
					templateValues.put(RegistrationConstants.TEMPLATE_PRE_REG_ID, registration.getPreRegistrationId());
				} else {
					templateValues.put(RegistrationConstants.TEMPLATE_PRE_REG_ID, "-");
				}

				templateValues.put(RegistrationConstants.TEMPLATE_MODIFY,
						applicationLanguageProperties.getString("modify"));

				try {
					BufferedImage modifyImage = ImageIO.read(
							this.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_MODIFY_IMAGE_PATH));
					byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(modifyImage, RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);
					byte[] modifyImageBytes = byteArrayOutputStream.toByteArray();
					String modifyImageEncodedBytes = StringUtils
							.newStringUtf8(Base64.encodeBase64(modifyImageBytes, false));
					templateValues.put(RegistrationConstants.TEMPLATE_MODIFY_IMAGE_SOURCE,
							RegistrationConstants.TEMPLATE_PNG_IMAGE_ENCODING + modifyImageEncodedBytes);
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
									exception.getMessage());
						}
					}
				}

				for (FingerprintDetailsDTO fpDetailsDTO : registration.getBiometricDTO().getApplicantBiometricDTO()
						.getFingerprintDetailsDTO()) {
					if (fpDetailsDTO.getFingerType().contains(RegistrationConstants.LEFTPALM)) {
						byte[] leftPalmBytes = fpDetailsDTO.getFingerPrint();
						String leftPalmEncodedBytes = StringUtils
								.newStringUtf8(Base64.encodeBase64(leftPalmBytes, false));
						templateValues.put(RegistrationConstants.TEMPLATE_CAPTURED_LEFT_SLAP,
								RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + leftPalmEncodedBytes);
					} else if (fpDetailsDTO.getFingerType().contains(RegistrationConstants.RIGHTPALM)) {
						byte[] rightPalmBytes = fpDetailsDTO.getFingerPrint();
						String rightPalmEncodedBytes = StringUtils
								.newStringUtf8(Base64.encodeBase64(rightPalmBytes, false));
						templateValues.put(RegistrationConstants.TEMPLATE_CAPTURED_RIGHT_SLAP,
								RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + rightPalmEncodedBytes);
					} else if (fpDetailsDTO.getFingerType().contains(RegistrationConstants.THUMBS)) {
						byte[] thumbsBytes = fpDetailsDTO.getFingerPrint();
						String thumbsEncodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(thumbsBytes, false));
						templateValues.put(RegistrationConstants.TEMPLATE_CAPTURED_THUMBS,
								RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + thumbsEncodedBytes);
					}
				}

				for (IrisDetailsDTO capturedIris : registration.getBiometricDTO().getApplicantBiometricDTO()
						.getIrisDetailsDTO()) {
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

			templateValues.put(RegistrationConstants.TEMPLATE_DEMO_INFO,
					applicationLanguageProperties.getString("demographicInformation"));
			templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME_USER_LANG_LABEL,
					applicationLanguageProperties.getString("fullName"));
			templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME_LOCAL_LANG_LABEL,
					localProperties.getString("fullName"));
			templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getFullName(),
							platformLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_FULL_NAME_LOCAL_LANG,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getFullName(),
							localLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_GENDER_USER_LANG_LABEL,
					applicationLanguageProperties.getString("gender"));
			templateValues.put(RegistrationConstants.TEMPLATE_GENDER_LOCAL_LANG_LABEL,
					localProperties.getString("gender"));
			templateValues.put(RegistrationConstants.TEMPLATE_GENDER,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getGender(),
							platformLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_GENDER_LOCAL_LANG,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getGender(),
							localLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_DOB_USER_LANG_LABEL,
					applicationLanguageProperties.getString("ageDatePicker"));
			templateValues.put(RegistrationConstants.TEMPLATE_DOB_LOCAL_LANG_LABEL,
					localProperties.getString("ageDatePicker"));
			if (dob == null || dob == "") {
				templateValues.put(RegistrationConstants.TEMPLATE_DOB, getValue(
						registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAge(), null));
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_DOB,
						DateUtils.formatDate(DateUtils.parseToDate(dob, "yyyy/MM/dd"), "dd-MM-YYYY"));
			}
			templateValues.put(RegistrationConstants.TEMPLATE_AGE_USER_LANG_LABEL,
					applicationLanguageProperties.getString("ageField"));
			templateValues.put(RegistrationConstants.TEMPLATE_AGE_LOCAL_LANG_LABEL,
					localProperties.getString("ageField"));
			templateValues.put(RegistrationConstants.TEMPLATE_AGE,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAge(), null));
			templateValues.put(RegistrationConstants.TEMPLATE_YEARS_USER_LANG,
					applicationLanguageProperties.getString("years"));
			templateValues.put(RegistrationConstants.TEMPLATE_YEARS_LOCAL_LANG, localProperties.getString("years"));
			templateValues.put(RegistrationConstants.TEMPLATE_FOREIGNER_USER_LANG_LABEL,
					applicationLanguageProperties.getString("foreigner"));
			templateValues.put(RegistrationConstants.TEMPLATE_FOREIGNER_LOCAL_LANG_LABEL,
					localProperties.getString("foreigner"));
			templateValues.put(RegistrationConstants.TEMPLATE_RESIDENCE_STATUS,
					applicationLanguageProperties.getString("national"));
			templateValues.put(RegistrationConstants.TEMPLATE_RESIDENCE_STATUS_LOCAL_LANG,
					localProperties.getString("national"));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1_USER_LANG_LABEL,
					applicationLanguageProperties.getString("addressLine1"));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1_LOCAL_LANG_LABEL,
					localProperties.getString("addressLine1"));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine1(),
							platformLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1_LOCAL_LANG,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine1(),
							localLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2_USER_LANG_LABEL,
					applicationLanguageProperties.getString("addressLine2"));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2_LOCAL_LANG_LABEL,
					localProperties.getString("addressLine2"));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine2(),
							platformLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2_LOCAL_LANG,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine2(),
							localLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_REGION_USER_LANG_LABEL,
					applicationLanguageProperties.getString("region"));
			templateValues.put(RegistrationConstants.TEMPLATE_REGION_LOCAL_LANG_LABEL,
					localProperties.getString("region"));
			templateValues.put(RegistrationConstants.TEMPLATE_REGION,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getRegion(),
							platformLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_REGION_LOCAL_LANG,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getRegion(),
							localLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_PROVINCE_USER_LANG_LABEL,
					applicationLanguageProperties.getString("province"));
			templateValues.put(RegistrationConstants.TEMPLATE_PROVINCE_LOCAL_LANG_LABEL,
					localProperties.getString("province"));
			templateValues.put(RegistrationConstants.TEMPLATE_PROVINCE,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getProvince(),
							platformLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_PROVINCE_LOCAL_LANG,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getProvince(),
							localLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY_USER_LANG_LABEL,
					applicationLanguageProperties.getString("localAdminAuthority"));
			templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG_LABEL,
					localProperties.getString("localAdminAuthority"));
			templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY, getValue(registration.getDemographicDTO()
					.getDemographicInfoDTO().getIdentity().getLocalAdministrativeAuthority(), platformLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.getLocalAdministrativeAuthority(), localLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_MOBILE_USER_LANG_LABEL,
					applicationLanguageProperties.getString("mobileNo"));
			templateValues.put(RegistrationConstants.TEMPLATE_MOBILE_LOCAL_LANG_LABEL,
					localProperties.getString("mobileNo"));
			templateValues.put(RegistrationConstants.TEMPLATE_MOBILE,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getPhone(), null));
			templateValues.put(RegistrationConstants.TEMPLATE_POSTAL_CODE_USER_LANG_LABEL,
					applicationLanguageProperties.getString("postalCode"));
			templateValues.put(RegistrationConstants.TEMPLATE_POSTAL_CODE_LOCAL_LANG_LABEL,
					localProperties.getString("postalCode"));
			templateValues.put(RegistrationConstants.TEMPLATE_POSTAL_CODE, getValue(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getPostalCode(), null));
			templateValues.put(RegistrationConstants.TEMPLATE_EMAIL_USER_LANG_LABEL,
					applicationLanguageProperties.getString("emailId"));
			templateValues.put(RegistrationConstants.TEMPLATE_EMAIL_LOCAL_LANG_LABEL,
					localProperties.getString("emailId"));
			String email = getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getEmail(),
					null);
			if (email == null || email.isEmpty()) {
				templateValues.put(RegistrationConstants.TEMPLATE_EMAIL, RegistrationConstants.EMPTY);
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_EMAIL, email);
			}
			templateValues.put(RegistrationConstants.TEMPLATE_CNIE_NUMBER_USER_LANG_LABEL,
					applicationLanguageProperties.getString("cniOrPinNumber"));
			templateValues.put(RegistrationConstants.TEMPLATE_CNIE_LOCAL_LANG_LABEL,
					localProperties.getString("cniOrPinNumber"));
			templateValues.put(RegistrationConstants.TEMPLATE_CNIE_NUMBER, getValue(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getCnieNumber(), null));
			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS_USER_LANG_LABEL,
					applicationLanguageProperties.getString("documents"));
			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS_LOCAL_LANG_LABEL,
					localProperties.getString("documents"));
			StringBuilder documentsList = new StringBuilder();
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
			if (registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
					.getProofOfDateOfBirth() != null) {
				documentsList.append(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
						.getProofOfDateOfBirth().getValue());
			}
			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS, documentsList.toString());

			templateValues.put(RegistrationConstants.TEMPLATE_DOCUMENTS_LOCAL_LANG, documentsList.toString());
			templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_USER_LANG_LABEL,
					applicationLanguageProperties.getString("biometricsHeading"));
			templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_CAPTURED_USER_LANG_LABEL,
					applicationLanguageProperties.getString("biometrics_captured"));
			templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_CAPTURED_LOCAL_LANG_LABEL,
					localProperties.getString("biometrics_captured"));

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
					MessageFormat.format((String) applicationLanguageProperties.getString("biometricscount"),
							String.valueOf(fingersAndIrises[0]), String.valueOf(fingersAndIrises[1])));
			templateValues.put(RegistrationConstants.TEMPLATE_BIOMETRICS_CAPTURED_LOCAL_LANG,
					MessageFormat.format((String) localProperties.getString("biometricscount"),
							String.valueOf(fingersAndIrises[0]), String.valueOf(fingersAndIrises[1])));
			if (registration.getDemographicDTO().getApplicantDocumentDTO().isHasExceptionPhoto()) {
				templateValues.put(RegistrationConstants.TEMPLATE_WITHOUT_EXCEPTION,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
				templateValues.put(RegistrationConstants.TEMPLATE_EXCEPTION_PHOTO_USER_LANG_LABEL,
						applicationLanguageProperties.getString("exceptionphoto"));
				templateValues.put(RegistrationConstants.TEMPLATE_EXCEPTION_PHOTO_LOCAL_LANG_LABEL,
						localProperties.getString("exceptionphoto"));
				byte[] exceptionImageBytes = registration.getDemographicDTO().getApplicantDocumentDTO()
						.getExceptionPhoto();
				String exceptionImageEncodedBytes = StringUtils
						.newStringUtf8(Base64.encodeBase64(exceptionImageBytes, false));
				templateValues.put(RegistrationConstants.TEMPLATE_EXCEPTION_IMAGE_SOURCE,
						RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + exceptionImageEncodedBytes);
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_WITHOUT_EXCEPTION, null);
				templateValues.put(RegistrationConstants.TEMPLATE_WITH_EXCEPTION,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
			templateValues.put(RegistrationConstants.TEMPLATE_PHOTO_USER_LANG,
					applicationLanguageProperties.getString("individualphoto"));
			templateValues.put(RegistrationConstants.TEMPLATE_PHOTO_LOCAL_LANG,
					localProperties.getString("individualphoto"));
			byte[] applicantImageBytes = registration.getDemographicDTO().getApplicantDocumentDTO().getPhoto();
			String applicantImageEncodedBytes = StringUtils
					.newStringUtf8(Base64.encodeBase64(applicantImageBytes, false));
			templateValues.put(RegistrationConstants.TEMPLATE_APPLICANT_IMAGE_SOURCE,
					RegistrationConstants.TEMPLATE_JPG_IMAGE_ENCODING + applicantImageEncodedBytes);

			templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE_USER_LANG_LABEL,
					applicationLanguageProperties.getString("lefteye"));
			templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE_LOCAL_LANG_LABEL,
					localProperties.getString("lefteye"));
			templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE_USER_LANG_LABEL,
					applicationLanguageProperties.getString("righteye"));
			templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE_LOCAL_LANG_LABEL,
					localProperties.getString("righteye"));
			if (registration.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO().isEmpty()) {
				templateValues.put(RegistrationConstants.TEMPLATE_FINGERPRINTS_CAPTURED,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			} else {
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
				// get the quality ranking for fingerprints of the applicant
				HashMap<String, Integer> fingersQuality = getFingerPrintQualityRanking(registration);
				for (Map.Entry<String, Integer> entry : fingersQuality.entrySet()) {
					if (entry.getValue() != 0) {
						// display rank of quality for the captured fingerprints
						templateValues.put(entry.getKey(), entry.getValue());
					} else {
						// display cross mark for missing fingerprints
						templateValues.put(entry.getKey(), RegistrationConstants.TEMPLATE_CROSS_MARK);
					}
				}
				templateValues = countMissingFingers(registration, templateValues, applicationLanguageProperties,
						localProperties);
			}

			templateValues.put(RegistrationConstants.TEMPLATE_RO_IMAGE,
					RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME_USER_LANG_LABEL,
					applicationLanguageProperties.getString("ro_name"));
			templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME_LOCAL_LANG_LABEL,
					localProperties.getString("ro_name"));
			templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME,
					getValue(registration.getOsiDataDTO().getOperatorID(), platformLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_RO_NAME_LOCAL_LANG,
					getValue(registration.getOsiDataDTO().getOperatorID(), localLanguageCode));
			templateValues.put(RegistrationConstants.TEMPLATE_REG_CENTER_USER_LANG_LABEL,
					applicationLanguageProperties.getString("registrationcenter"));
			templateValues.put(RegistrationConstants.TEMPLATE_REG_CENTER_LOCAL_LANG_LABEL,
					localProperties.getString("registrationcenter"));
			templateValues.put(RegistrationConstants.TEMPLATE_REG_CENTER,
					SessionContext.userContext().getRegistrationCenterDetailDTO().getRegistrationCenterName());
			templateValues.put(RegistrationConstants.TEMPLATE_REG_CENTER_LOCAL_LANG, "");
			templateValues.put(RegistrationConstants.TEMPLATE_IMPORTANT_GUIDELINES,
					applicationLanguageProperties.getString("importantguidelines"));

			boolean isChild = registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
					.getParentOrGuardianRIDOrUIN() != null;

			if (isChild) {
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME_USER_LANG_LABEL,
						applicationLanguageProperties.getString("parentName"));
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME, getValue(registration.getDemographicDTO()
						.getDemographicInfoDTO().getIdentity().getParentOrGuardianName(), platformLanguageCode));
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME_LOCAL_LANG_LABEL,
						localProperties.getString("parentName"));
				templateValues.put(RegistrationConstants.TEMPLATE_PARENT_NAME_LOCAL_LANG,
						getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity()
								.getParentOrGuardianName(), localLanguageCode));
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_WITH_PARENT,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}

			Writer writer = new StringWriter();
			// Velocity.evaluate(templateValues, writer, "Acknowledgement Template",
			// templateReader);
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

	private Map<String, Object> countMissingIrises(Map<String, Object> templateValues, RegistrationDTO registration) {
		boolean missingLeftIris = false;
		boolean missingRightIris = false;

		List<BiometricExceptionDTO> exceptionBiometrics = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getBiometricExceptionDTO();
		if (exceptionBiometrics != null) {
			for (BiometricExceptionDTO exceptionBiometric : exceptionBiometrics) {
				if (exceptionBiometric.getBiometricType().equalsIgnoreCase(RegistrationConstants.IRIS)) {
					if (exceptionBiometric.getMissingBiometric().toLowerCase()
							.contains(RegistrationConstants.LEFT.toLowerCase())) {
						missingLeftIris = true;
					} else if (exceptionBiometric.getMissingBiometric().toLowerCase()
							.contains(RegistrationConstants.RIGHT.toLowerCase())) {
						missingRightIris = true;
					}
				}
			}
		}
		if (missingLeftIris) {
			templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE, RegistrationConstants.TEMPLATE_CROSS_MARK);
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_LEFT_EYE, RegistrationConstants.TEMPLATE_RIGHT_MARK);
		}
		if (missingRightIris) {
			templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE, RegistrationConstants.TEMPLATE_CROSS_MARK);
		} else {
			templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_EYE, RegistrationConstants.TEMPLATE_RIGHT_MARK);
		}
		return templateValues;
	}

	private Map<String, Object> countMissingFingers(RegistrationDTO registration, Map<String, Object> templateValues,
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
						MessageFormat.format((String) applicationLanguageProperties.getString("exception"),
								String.valueOf(missingLeftFingers)));
				templateValues.put(RegistrationConstants.TEMPLATE_LEFT_SLAP_EXCEPTION_LOCAL_LANG, MessageFormat
						.format((String) localProperties.getString("exception"), String.valueOf(missingLeftFingers)));
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_MISSING_LEFT_FINGERS,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
			if (missingRightFingers != 0) {
				templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_SLAP_EXCEPTION_USER_LANG,
						MessageFormat.format((String) applicationLanguageProperties.getString("exception"),
								String.valueOf(missingRightFingers)));
				templateValues.put(RegistrationConstants.TEMPLATE_RIGHT_SLAP_EXCEPTION_LOCAL_LANG, MessageFormat
						.format((String) localProperties.getString("exception"), String.valueOf(missingRightFingers)));
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_MISSING_RIGHT_FINGERS,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
			if (missingThumbs != 0) {
				templateValues.put(RegistrationConstants.TEMPLATE_THUMBS_EXCEPTION_USER_LANG, MessageFormat.format(
						(String) applicationLanguageProperties.getString("exception"), String.valueOf(missingThumbs)));
				templateValues.put(RegistrationConstants.TEMPLATE_THUMBS_EXCEPTION_LOCAL_LANG, MessageFormat
						.format((String) localProperties.getString("exception"), String.valueOf(missingThumbs)));
			} else {
				templateValues.put(RegistrationConstants.TEMPLATE_MISSING_THUMBS,
						RegistrationConstants.TEMPLATE_STYLE_HIDE_PROPERTY);
			}
		}
		return templateValues;
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
			String applicationLanguageCode = ApplicationContext.applicationLanguage().toLowerCase();
			InputStream is = new ByteArrayInputStream(templateText.getBytes());
			Map<String, Object> values = new LinkedHashMap<>();

			values.put(RegistrationConstants.TEMPLATE_RESIDENT_NAME,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getFullName(),
							applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_RID,
					getValue(registration.getRegistrationId(), applicationLanguageCode));

			SimpleDateFormat sdf = new SimpleDateFormat(RegistrationConstants.TEMPLATE_DATE_FORMAT);
			String currentDate = sdf.format(new Date());

			values.put(RegistrationConstants.TEMPLATE_DATE, currentDate);
			values.put(RegistrationConstants.TEMPLATE_FULL_NAME,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getFullName(),
							applicationLanguageCode));
			String dob = getValue(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getDateOfBirth(), null);
			if (dob == null || dob == "") {
				values.put(RegistrationConstants.TEMPLATE_DOB, getValue(
						registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAge(), null));
			} else {
				values.put(RegistrationConstants.TEMPLATE_DOB,
						DateUtils.formatDate(DateUtils.parseToDate(dob, "yyyy/MM/dd"), "dd-MM-YYYY"));
			}
			values.put(RegistrationConstants.TEMPLATE_GENDER,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getGender(),
							applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine1(),
							applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getAddressLine2(),
							applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_PROVINCE,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getProvince(),
							applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_REGION,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getRegion(),
							applicationLanguageCode));
			values.put(RegistrationConstants.TEMPLATE_POSTAL_CODE, getValue(
					registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getPostalCode(), null));
			values.put(RegistrationConstants.TEMPLATE_MOBILE,
					getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getPhone(), null));
			String email = getValue(registration.getDemographicDTO().getDemographicInfoDTO().getIdentity().getEmail(),
					null);
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
	@SuppressWarnings({ "unchecked" })
	private HashMap<String, Integer> getFingerPrintQualityRanking(RegistrationDTO registration) {
		// for storing the fingerprints captured and their respective quality scores
		HashMap<String, Double> fingersQuality = new HashMap<>();

		// list of missing fingers
		List<BiometricExceptionDTO> exceptionFingers = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getBiometricExceptionDTO();

		if (exceptionFingers != null) {
			for (BiometricExceptionDTO exceptionFinger : exceptionFingers) {
				if (exceptionFinger.getBiometricType().equalsIgnoreCase(RegistrationConstants.FINGERPRINT)) {
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

	@SuppressWarnings("unchecked")
	private String getValue(Object fieldValue, String lang) {
		LOGGER.info(LOG_TEMPLATE_GENERATOR, APPLICATION_NAME, APPLICATION_ID,
				"Getting values of demographic fields in given specific language");
		String value = RegistrationConstants.EMPTY;

		if (fieldValue instanceof String || fieldValue instanceof Integer || fieldValue instanceof BigInteger
				|| fieldValue instanceof Double) {
			value = String.valueOf(fieldValue);
		} else if (fieldValue instanceof List<?>) {
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

}