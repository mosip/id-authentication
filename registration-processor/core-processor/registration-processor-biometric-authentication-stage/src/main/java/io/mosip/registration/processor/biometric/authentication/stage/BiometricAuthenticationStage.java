package io.mosip.registration.processor.biometric.authentication.stage;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.biometric.authentication.constants.BiometricAuthenticationConstants;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.auth.dto.AuthResponseDTO;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.constant.RegistrationType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.BioTypeException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.utils.AuthUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Service
public class BiometricAuthenticationStage extends MosipVerticleManager {
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BiometricAuthenticationStage.class);

	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	private Utilities utility;

	@Autowired
	private PacketManager adapter;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private AuthUtil authUtil;

	@Autowired
	RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	@Value("${mosip.kernel.applicant.type.age.limit}")
	private String ageLimit;

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.BIOMETRIC_AUTHENTICATION_BUS_IN,
				MessageBusAddress.BIOMETRIC_AUTHENTICATION_BUS_OUT);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"BiometricAuthenticationStage::BiometricAuthenticationStage::entry");
		String registrationId = object.getRid();
		object.setMessageBusAddress(MessageBusAddress.BIOMETRIC_AUTHENTICATION_BUS_IN);
		object.setIsValid(Boolean.FALSE);
		object.setInternalError(Boolean.FALSE);
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);

		registrationStatusDto
				.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.BIOMETRIC_AUTHENTICATION.toString());
		registrationStatusDto.setRegistrationStageName(this.getClass().getSimpleName());
		String description = "";
		String code = "";
		boolean isTransactionSuccessful = false;

		try {
			PacketMetaInfo packetMetaInfo = utility.getPacketMetaInfo(registrationId);
			List<FieldValue> metadata = packetMetaInfo.getIdentity().getMetaData();
			IdentityIteratorUtil identityIterator = new IdentityIteratorUtil();
			String registartionType = identityIterator.getFieldValue(metadata,
					BiometricAuthenticationConstants.REGISTRATIONTYPE);
			int applicantAge = utility.getApplicantAge(registrationId);
			int childAgeLimit = Integer.parseInt(ageLimit);
			String applicantType = BiometricAuthenticationConstants.ADULT;
			if (applicantAge <= childAgeLimit && applicantAge > 0) {
				applicantType = BiometricAuthenticationConstants.CHILD;
			}
			if ((registartionType.equalsIgnoreCase(RegistrationType.UPDATE.name())
					|| registartionType.equalsIgnoreCase(RegistrationType.RES_UPDATE.name()))
					&& applicantType.equalsIgnoreCase(BiometricAuthenticationConstants.ADULT)) {

				JSONObject demographicIdentity = utility.getDemographicIdentityJSONObject(registrationId);
				JSONObject individualBioMetricLabel = JsonUtil.getJSONObject(demographicIdentity,
						BiometricAuthenticationConstants.INDIVIDUALBIOMETRICS);
				if (individualBioMetricLabel == null) {
					isTransactionSuccessful = checkIndividualAuthentication(registrationId, metadata,
							registrationStatusDto);
					description = isTransactionSuccessful
							? PlatformSuccessMessages.RPR_PKR_BIOMETRIC_AUTHENTICATION.getMessage()
							: PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_FAILED.getMessage();
				} else {
					String individualBioMetricValue = (String) individualBioMetricLabel
							.get(BiometricAuthenticationConstants.VALUE);

					if (individualBioMetricValue != null && !individualBioMetricValue.isEmpty()) {

						InputStream inputStream = adapter.getFile(registrationId,
								PacketFiles.BIOMETRIC + BiometricAuthenticationConstants.FILE_SEPERATOR
										+ individualBioMetricValue.toUpperCase());

						if (inputStream == null) {
							isTransactionSuccessful = false;
							description = PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_FAILED.getMessage()
									+ BiometricAuthenticationConstants.FILENOTPRESENT;
							regProcLogger.info(LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
									description, "");
							registrationStatusDto.setStatusComment(description);
						} else {
							isTransactionSuccessful = true;
						}

					} else {
						isTransactionSuccessful = true;
						description = PlatformSuccessMessages.RPR_PKR_BIOMETRIC_AUTHENTICATION.getMessage();
					}
				}
			}

			else {
				object.setIsValid(true);
				object.setInternalError(false);
				isTransactionSuccessful = true;
				if (SyncTypeDto.NEW.toString().equalsIgnoreCase(registartionType)) {
					description = BiometricAuthenticationConstants.NEW_PACKET_DESCRIPTION + registrationId;
				} else
					description = BiometricAuthenticationConstants.CHILD_PACKET_DESCRIPTION + registrationId;
			}

		} catch (IOException | io.mosip.kernel.core.exception.IOException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto
					.setStatusComment(PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_IOEXCEPTION.getMessage());
			object.setIsValid(false);
			object.setInternalError(true);
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
			code = PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_IOEXCEPTION.getCode();
			description = PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_IOEXCEPTION.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + e.getMessage() + ExceptionUtils.getStackTrace(e));
		} catch (ApisResourceAccessException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(
					PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_API_RESOURCE_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			code = PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_API_RESOURCE_EXCEPTION.getCode();
			description = PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_API_RESOURCE_EXCEPTION.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + e.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (Exception ex) {

			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(ex.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationStatusMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			code = PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_FAILED.getCode();
			description = PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + ex.getMessage() + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);

		} finally {
			if (isTransactionSuccessful) {
				object.setIsValid(Boolean.TRUE);
				object.setInternalError(Boolean.FALSE);
				registrationStatusDto
						.setStatusComment(PlatformSuccessMessages.RPR_PKR_BIOMETRIC_AUTHENTICATION.getMessage());
			} else {
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
				registrationStatusDto.setStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
			}
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			description = isTransactionSuccessful
					? PlatformSuccessMessages.RPR_PKR_BIOMETRIC_AUTHENTICATION.getMessage()
					: description;
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Success/Error code */
			String moduleId = isTransactionSuccessful
					? PlatformSuccessMessages.RPR_PKR_BIOMETRIC_AUTHENTICATION.getCode()
					: code;
			String moduleName = ModuleName.BIOMETRIC_AUTHENTICATION.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "BiometricAuthenticationStage::BiometricAuthenticationStage::exit");
		return object;
	}

	private boolean idaAuthenticate(InputStream file, Long uin, InternalRegistrationStatusDto registrationStatusDto)
			throws IOException, ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException,
			BiometricException, BioTypeException, ParserConfigurationException, SAXException {
		String UIN = uin.toString();
		byte[] officerbiometric = IOUtils.toByteArray(file);
		boolean idaAuth = false;
		AuthResponseDTO authResponseDTO = authUtil.authByIdAuthentication(UIN,
				BiometricAuthenticationConstants.INDIVIDUAL_TYPE_USERID, officerbiometric);
		if (authResponseDTO.getErrors() == null || authResponseDTO.getErrors().isEmpty()) {
			if (authResponseDTO.getResponse().isAuthStatus()) {
				idaAuth = true;
			} else {
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				registrationStatusDto.setStatusComment(BiometricAuthenticationConstants.INDIVIDUALAUTHENTICATIONFAILED);
				idaAuth = false;
			}
		}
		return idaAuth;
	}

	private boolean checkIndividualAuthentication(String registrationId, List<FieldValue> metadata,
			InternalRegistrationStatusDto registrationStatusDto) throws IOException, PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException, InvalidKeySpecException,
			NoSuchAlgorithmException, BiometricException, BioTypeException, ParserConfigurationException, SAXException {
		IdentityIteratorUtil identityIterator = new IdentityIteratorUtil();
		String individualAuthentication = identityIterator.getFieldValue(metadata,
				BiometricAuthenticationConstants.INDIVIDUALAUTHENTICATION);
		if (individualAuthentication == null || individualAuthentication.isEmpty())
			return false;
		InputStream inputStream = adapter.getFile(registrationId, PacketFiles.BIOMETRIC
				+ BiometricAuthenticationConstants.FILE_SEPERATOR + individualAuthentication.toUpperCase());
		if (inputStream == null) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_FAILED.getMessage()
					+ BiometricAuthenticationConstants.FILENOTPRESENT);
			return false;
		}
		Long uin = utility.getUIn(registrationId);

		return idaAuthenticate(inputStream, uin, registrationStatusDto);

	}

}
