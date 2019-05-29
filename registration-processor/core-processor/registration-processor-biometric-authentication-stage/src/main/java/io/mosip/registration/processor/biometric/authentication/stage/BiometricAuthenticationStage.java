package io.mosip.registration.processor.biometric.authentication.stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Service
public class BiometricAuthenticationStage extends MosipVerticleManager {
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BiometricAuthenticationStage.class);
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;
	@Autowired
	private Utilities utility;
	@Autowired
	private FileSystemAdapter adapter;
	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;
	RegistrationExceptionMapperUtil registrationStatusMapperUtil = new RegistrationExceptionMapperUtil();
	private static final String INDIVIDUALBIOMETRICS = "individualBiometrics";
	private static final String VALUE = "VALUE";
	private static final String INDIVIDUALAUTHENTICATION = "authenticationBiometricFileName";
	private static final String REGISTRATIONTYPE = "registrationType";
	public static final String FILE_SEPERATOR = "\\";
	private static final String ADULT = "Adult";
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
		String registrationId = object.getRid();
		object.setMessageBusAddress(MessageBusAddress.BIOMETRIC_AUTHENTICATION_BUS_IN);
		object.setIsValid(Boolean.FALSE);
		object.setInternalError(Boolean.FALSE);
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);

		registrationStatusDto.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.OSI_VALIDATE.toString());
		registrationStatusDto.setRegistrationStageName(this.getClass().getSimpleName());
		String description = "";
		String code = "";
		boolean isTransactionSuccessful = false;

		try {
			PacketMetaInfo packetMetaInfo = utility.getPacketMetaInfo(registrationId);
			List<FieldValue> metadata = packetMetaInfo.getIdentity().getMetaData();
			IdentityIteratorUtil identityIterator = new IdentityIteratorUtil();
			String registartionType = identityIterator.getFieldValue(metadata, REGISTRATIONTYPE);
			int applicantAge = utility.getApplicantAge(registrationId);
			int childAgeLimit = Integer.parseInt(ageLimit);
			String applicantType = "ADULT";
			if (applicantAge <= childAgeLimit && applicantAge > 0) {
					 applicantType = "CHILD";
			}
			if ((registartionType.equalsIgnoreCase(RegistrationType.UPDATE.name())
					|| registartionType.equalsIgnoreCase(RegistrationType.RES_UPDATE.name()))
					&& applicantType.equalsIgnoreCase(ADULT)) {
				
				JSONObject demographicIdentity = utility.getDemographicIdentityJSONObject(registrationId);
				JSONObject individualBioMetricLabel = JsonUtil.getJSONObject(demographicIdentity, INDIVIDUALBIOMETRICS);
				if(individualBioMetricLabel == null) {
					isTransactionSuccessful = checkIndividualAuthentication(registrationId, metadata);
					description = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_BIOMETRIC_AUTHENTICATION.getMessage():PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_FAILED.getMessage();
				}
				else {
					String individualBioMetricValue = (String) individualBioMetricLabel.get(VALUE);
					
					if (individualBioMetricValue != null && !individualBioMetricValue.isEmpty()) {
						
						InputStream inputStream = adapter.getFile(registrationId,
								PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + individualBioMetricValue.toUpperCase());
							
						if(inputStream == null) {
							isTransactionSuccessful = false;
						}
						else {
							isTransactionSuccessful = checkIndividualAuthentication(registrationId, metadata);
							description = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_BIOMETRIC_AUTHENTICATION.getMessage():PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_FAILED.getMessage();
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
				description="The packet is child packet for registration id" +registrationId;
			}

		} catch (IOException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_IOEXCEPTION.getMessage());
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
			registrationStatusDto.setStatusComment(PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_API_RESOURCE_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			code = PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_API_RESOURCE_EXCEPTION.getCode();
			description = PlatformErrorMessages.BIOMETRIC_AUTHENTICATION_API_RESOURCE_EXCEPTION.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), code, registrationId,
					description + e.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} finally {
			if (isTransactionSuccessful) {
				object.setIsValid(Boolean.TRUE);
				object.setInternalError(Boolean.FALSE);
			}
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			description = isTransactionSuccessful ? PlatformSuccessMessages.RPR_PKR_BIOMETRIC_AUTHENTICATION.getMessage()
					: description;
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Succes/Error code */
			String moduleId = isTransactionSuccessful
					? PlatformSuccessMessages.RPR_PKR_BIOMETRIC_AUTHENTICATION.getCode()
					: code;
			String moduleName = ModuleName.BIOMETRIC_AUTHENTICATION.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}

		return object;
	}

	private boolean idaAuthenticate(InputStream file, Long uin) {
		if (file != null && uin != null)
			return true;
		return false;

	}

	private boolean checkIndividualAuthentication(String registrationId, List<FieldValue> metadata)
			throws IOException {
		IdentityIteratorUtil identityIterator = new IdentityIteratorUtil();
		String individualAuthentication = identityIterator.getFieldValue(metadata, INDIVIDUALAUTHENTICATION);
		if (individualAuthentication == null || individualAuthentication.isEmpty())
			return false;
		InputStream inputStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + individualAuthentication.toUpperCase());
		Long uin = utility.getUIn(registrationId);

		return idaAuthenticate(inputStream, uin);

	}

}
