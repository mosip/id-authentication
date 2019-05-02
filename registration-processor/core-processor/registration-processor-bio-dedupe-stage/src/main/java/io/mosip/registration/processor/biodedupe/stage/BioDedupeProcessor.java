
package io.mosip.registration.processor.biodedupe.stage;

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

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.bio.dedupe.exception.ABISAbortException;
import io.mosip.registration.processor.bio.dedupe.exception.ABISInternalError;
import io.mosip.registration.processor.bio.dedupe.exception.UnableToServeRequestABISException;
import io.mosip.registration.processor.bio.dedupe.exception.UnexceptedError;
import io.mosip.registration.processor.biodedupe.stage.utils.StatusMessage;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.DedupeSourceName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.ResponseStatusCode;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
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

	private final String RE_PROCESSING = "re-processing";

	private final String HANDLER = "handler";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeProcessor.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	private static final String BIO = "BIO";

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The bio dedupe service. */
	@Autowired
	private BioDedupeService bioDedupeService;

	RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	String description = "";

	private String code = "";

	public MessageDTO process(MessageDTO object, String stageName) throws ApisResourceAccessException, ParseException {
		object.setMessageBusAddress(MessageBusAddress.BIO_DEDUPE_BUS_IN);
		object.setInternalError(Boolean.FALSE);

		boolean isTransactionSuccessful = false;

		String registrationId = object.getRid();

		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);

		String registrationType = registrationStatusDto.getRegistrationType();

		///////////////////////////////////////////////////////////////////////
		if (registrationType.equalsIgnoreCase("NEW")) {
			if (registrationStatusDto.getLatestTransactionTypeCode().equalsIgnoreCase("BIOGRAPHIC_VERIFICATION")) {
				LocalDateTime createdDateTime = registrationStatusDto.getCreateDateTime();

				if (getElapseStatus(createdDateTime).equalsIgnoreCase(RE_PROCESSING) && checkCBEFF(registrationId)) {
					scen1(registrationStatusDto);
				}

			} else {
				scen1(registrationStatusDto);
			}
		} else if (registrationType.equalsIgnoreCase("UPDATE")) {
			if (registrationStatusDto.getLatestTransactionTypeCode().equalsIgnoreCase("BIOGRAPHIC_VERIFICATION")) {
				LocalDateTime createdDateTime = registrationStatusDto.getCreateDateTime();

				if (getElapseStatus(createdDateTime).equalsIgnoreCase(RE_PROCESSING) && checkCBEFF(registrationId)) {
					scen2(registrationStatusDto);
				}

			} else {
				scen2(registrationStatusDto);
			}
		}
		////////////////////////////////////////////////////////////////////////////////////
		registrationStatusDto
				.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.BIOGRAPHIC_VERIFICATION.toString());
		registrationStatusDto.setRegistrationStageName(stageName);

		try {
			String insertionResult = bioDedupeService.insertBiometrics(registrationId);
			if (insertionResult.equalsIgnoreCase(ResponseStatusCode.SUCCESS.name())) {
				List<String> matchedRegIds = bioDedupeService.performDedupe(registrationId);
				checkBiometricPotentialMatch(matchedRegIds, registrationStatusDto, registrationId, object);
			} else {
				object.setIsValid(Boolean.FALSE);
				code = PlatformErrorMessages.RPR_BIO_BIOMETRIC_INSERTION_TO_ABIS.getCode();
				description = registrationStatusDto.getStatusComment() + registrationId;
				registrationStatusDto
						.setStatusComment(PlatformErrorMessages.RPR_BIO_BIOMETRIC_INSERTION_TO_ABIS.name());
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.toString());
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
			}
			registrationStatusDto.setUpdatedBy(USER);
			isTransactionSuccessful = true;
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
					code + " - " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId, description);
		} catch (ABISInternalError e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getMessage());
			code = PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode();
			description = PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					code + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (ABISAbortException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getMessage());
			code = PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode();
			description = PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					code + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (UnexceptedError e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_UNEXCEPTED_EXCEPTION.getMessage());
			code = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					code + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (UnableToServeRequestABISException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getMessage());
			code = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode();
			description = PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage() + " -- " + registrationId;
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					code + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (FSAdapterException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_REPROCESSING.name());
			registrationStatusDto
					.setStatusComment(PlatformErrorMessages.PACKET_BDD_PACKET_STORE_NOT_ACCESSIBLE.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION));

			code = PlatformErrorMessages.PACKET_BDD_PACKET_STORE_NOT_ACCESSIBLE.getCode();
			description = PlatformErrorMessages.PACKET_BDD_PACKET_STORE_NOT_ACCESSIBLE.getMessage();
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					code + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
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

	private void checkBiometricPotentialMatch(List<String> matchedRegIds,
			InternalRegistrationStatusDto registrationStatusDto, String registrationId, MessageDTO object) {
		if (matchedRegIds != null && !matchedRegIds.isEmpty()) {
			object.setIsValid(Boolean.FALSE);
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIOMETRIC_POTENTIAL_MATCH);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_POTENTIAL_MATCH.toString());
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());

			code = PlatformSuccessMessages.RPR_BIO_METRIC_POTENTIAL_MATCH.getCode();
			description = PlatformSuccessMessages.RPR_BIO_METRIC_POTENTIAL_MATCH.getMessage();
			packetInfoManager.saveManualAdjudicationData(matchedRegIds, registrationId, DedupeSourceName.BIO);
		} else {
			object.setIsValid(Boolean.TRUE);
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIODEDUPE_SUCCESS);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_SUCCESS.toString());
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());

			code = PlatformSuccessMessages.RPR_BIO_DEDUPE_SUCCESS.getCode();
			description = PlatformSuccessMessages.RPR_BIO_DEDUPE_SUCCESS.getMessage();

		}
	}

	private String getElapseStatus(LocalDateTime createdDateTime) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Duration duration = Duration.between(createdDateTime, currentDateTime);
		long secondsDiffernce = duration.getSeconds();
		if (secondsDiffernce > elapseTime)
			return RE_PROCESSING;
		else
			return HANDLER;
	}

	private Boolean checkCBEFF(String registrationId) throws ApisResourceAccessException {

		List<String> pathSegments = new ArrayList<>();
		pathSegments.add(registrationId);
		byte[] bytefile = (byte[]) restClientService.getApi(ApiName.BIODEDUPE, pathSegments, "", "", byte[].class);

		if (bytefile != null)
			return true;

		else {

			// age calc

			// if applicant type adult throw excptn else return true

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Byte file not found from BioDedupe api");

		}
		return false;
	}

	private String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;

	}

	private void scen1(InternalRegistrationStatusDto registrationStatusDto) {

		String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());

		registrationStatusDto.setLatestRegistrationTransactionId(latestTransactionId);
		TransactionDto transactionDto = new TransactionDto(UUID.randomUUID().toString(),
				registrationStatusDto.getRegistrationId(), latestTransactionId, "Bio-dedupe", "", "IN-Progress",
				registrationStatusDto.getStatusComment());
		transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
		transactionDto.setReferenceIdType("Added registration record");
		transcationStatusService.addRegistrationTransaction(transactionDto);

		// move to abis handler stage using messageDTO
	}

	private void scen2(InternalRegistrationStatusDto registrationStatusDto) {

		// base on reg id get ID json and chk for individual bio metrics
		// yes move to abis handler stage using messageDTO

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

	private void scen4a(InternalRegistrationStatusDto registrationStatusDto) {

		// base on reg id get ID json and chk for individual bio metrics
		// yes move to abis handler stage using messageDTO

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
