
package io.mosip.registration.processor.biodedupe.stage;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.biodedupe.stage.exception.AdultCbeffNotPresentException;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.TransactionService;

/**
 * @author Nagalakshmi
 * @author Sowmya
 *
 */
/*
 * @Transactional removed temporarily since the refid is not getting saved
 * immediately in abisref table. TODO : need to fix this.
 */
@Service
public class BioDedupeProcessor {

	@Autowired
	private RegistrationStatusDao registrationStatusDao;

	@Value("${registration.processor.reprocess.elapse.time}")
	private long elapseTime;

	@Autowired
	private TransactionService<TransactionDto> transcationStatusService;

	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;

	@Value("${registration.processor.identityjson}")
	private String getRegProcessorIdentityJson;

	@Autowired
	Utilities utilities;

	@Value("${mosip.kernel.applicant.type.age.limit}")
	private String age_threshold;

	private static final String RE_PROCESSING = "re-processing";

	private static final String HANDLER = "handler";

	private static final String NEW_PACKET = "New-packet";

	private static final String REG_TYPE_NEW = "New";

	private static final String REG_TYPE_UPDATE = "Update";

	private static final String INPROGRESS = "IN-PROGRESS";

	private static final String BIOGRAPHIC_VERIFICATION = "BIOGRAPHIC_VERIFICATION";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeProcessor.class);

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	String description = "";

	private String code = "";

	public MessageDTO process(MessageDTO object, String stageName) {
		object.setMessageBusAddress(MessageBusAddress.BIO_DEDUPE_BUS_IN);
		object.setInternalError(Boolean.FALSE);

		boolean isTransactionSuccessful = false;

		String registrationId = object.getRid();
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);
		try {

			String registrationType = registrationStatusDto.getRegistrationType();
			if (registrationType.equalsIgnoreCase(REG_TYPE_NEW)) {
				String packetStatus = getElapseStatus(registrationStatusDto);
				if (packetStatus.equalsIgnoreCase(NEW_PACKET) || packetStatus.equalsIgnoreCase(RE_PROCESSING)) {
					newPacketProcessing(registrationStatusDto);
				} else if (packetStatus.equalsIgnoreCase(HANDLER)) {

				}

			} else if (registrationType.equalsIgnoreCase(REG_TYPE_UPDATE)) {
				String packetStatus = getElapseStatus(registrationStatusDto);
				if (packetStatus.equalsIgnoreCase(NEW_PACKET) || packetStatus.equalsIgnoreCase(RE_PROCESSING)) {
					updatePacketProcessing(registrationStatusDto);
				} else if (packetStatus.equalsIgnoreCase(HANDLER)) {

				}
			}

			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.BIOGRAPHIC_VERIFICATION.toString());
			registrationStatusDto.setRegistrationStageName(stageName);

		} catch (DataAccessException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_REPROCESSING.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_DATA_ACCESS_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.DATA_ACCESS_EXCEPTION));
			code = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					code + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (ApisResourceAccessException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_REPROCESSING.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_API_RESOURCE_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			code = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					code + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (ParseException ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.PARSE_EXCEPTION));
			code = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					code + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (IOException ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
			code = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					code + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (Exception ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			code = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					code + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} finally {
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);

			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_BIO_DEDUPE_SUCCESS.getCode() : code;
			String moduleName = ModuleName.BIO_DEDUPE.name();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}
		return object;
	}

	private String getElapseStatus(InternalRegistrationStatusDto registrationStatusDto) {

		if (registrationStatusDto.getLatestTransactionTypeCode().equalsIgnoreCase(BIOGRAPHIC_VERIFICATION)) {
			LocalDateTime createdDateTime = registrationStatusDto.getCreateDateTime();
			LocalDateTime currentDateTime = LocalDateTime.now();
			Duration duration = Duration.between(createdDateTime, currentDateTime);
			long secondsDiffernce = duration.getSeconds();
			if (secondsDiffernce > elapseTime)
				return RE_PROCESSING;
			else
				return HANDLER;
		}
		return NEW_PACKET;
	}

	private Boolean checkCBEFF(String registrationId) throws ApisResourceAccessException, IOException, ParseException {

		List<String> pathSegments = new ArrayList<>();
		pathSegments.add(registrationId);
		byte[] bytefile = (byte[]) restClientService.getApi(ApiName.BIODEDUPE, pathSegments, "", "", byte[].class);

		if (bytefile != null)
			return true;

		else {

			int age = utilities.getApplicantAge(registrationId);
			int ageThreshold = Integer.parseInt(age_threshold);

			if (age < ageThreshold) {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"Applicant type is child and Cbeff not present returning false");
				return false;
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"Applicant type is adult and Cbeff not present");
				throw new AdultCbeffNotPresentException(
						PlatformErrorMessages.PACKET_BIO_DEDUPE_CBEFF_NOT_PRESENT.getMessage());
			}

		}

	}

	private String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;

	}

	private void newPacketProcessing(InternalRegistrationStatusDto registrationStatusDto)
			throws ApisResourceAccessException, IOException, ParseException {
		if (checkCBEFF(registrationStatusDto.getRegistrationId())) {
			String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());

			registrationStatusDto.setLatestRegistrationTransactionId(latestTransactionId);
			TransactionDto transactionDto = new TransactionDto(UUID.randomUUID().toString(),
					registrationStatusDto.getRegistrationId(), latestTransactionId, BIOGRAPHIC_VERIFICATION, "",
					INPROGRESS, registrationStatusDto.getStatusComment());
			transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
			transactionDto.setReferenceIdType("Added registration record");
			transcationStatusService.addRegistrationTransaction(transactionDto);

			// move to abis handler stage using messageDTO}
		} else {
			// mve to UIN
		}
	}

	private void updatePacketProcessing(InternalRegistrationStatusDto registrationStatusDto) throws IOException {

		String getIdentityJsonString = Utilities.getJson(configServerFileStorageURL, getRegProcessorIdentityJson);
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		RegistrationProcessorIdentity regProcessorIdentityJson = mapIdentityJsonStringToObject
				.readValue(getIdentityJsonString, RegistrationProcessorIdentity.class);

		if (regProcessorIdentityJson.getIdentity().getIndividualBiometrics() != null) {

			String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());

			registrationStatusDto.setLatestRegistrationTransactionId(latestTransactionId);
			TransactionDto transactionDto = new TransactionDto(UUID.randomUUID().toString(),
					registrationStatusDto.getRegistrationId(), latestTransactionId, BIOGRAPHIC_VERIFICATION, "",
					INPROGRESS, registrationStatusDto.getStatusComment());
			transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
			transactionDto.setReferenceIdType("Added registration record");
			transcationStatusService.addRegistrationTransaction(transactionDto);

			// move to handler

		}
		// move to UIN

	}

	private void newPacketHandler(InternalRegistrationStatusDto registrationStatusDto) {

		String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());

		registrationStatusDto.setLatestRegistrationTransactionId(latestTransactionId);
		TransactionDto transactionDto = new TransactionDto(UUID.randomUUID().toString(),
				registrationStatusDto.getRegistrationId(), latestTransactionId, "Bio-dedupe", "", "IN-Progress",
				registrationStatusDto.getStatusComment());
		transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
		transactionDto.setReferenceIdType("Added registration record");
		transcationStatusService.addRegistrationTransaction(transactionDto);

		// no UIN

	}

}
