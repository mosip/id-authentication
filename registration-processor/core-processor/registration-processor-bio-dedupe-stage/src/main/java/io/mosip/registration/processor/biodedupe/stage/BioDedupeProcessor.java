package io.mosip.registration.processor.biodedupe.stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.biodedupe.constants.BioDedupeConstants;
import io.mosip.registration.processor.biodedupe.stage.exception.CbeffNotFoundException;
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
import io.mosip.registration.processor.core.constant.AbisConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.idreposervice.IdRepoService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class BioDedupeProcessor.
 *
 * @author Nagalakshmi
 * @author Sowmya
 * @author Horteppa
 */

@Service
@Transactional
public class BioDedupeProcessor {

	/** The utilities. */
	@Autowired
	Utilities utilities;

	@Autowired
	private IdRepoService idRepoService;

	/** The adapter. */
	@Autowired
	private PacketManager adapter;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The registration exception mapper util. */
	@Autowired
	RegistrationExceptionMapperUtil registrationExceptionMapperUtil;

	/** The abis handler util. */
	@Autowired
	private ABISHandlerUtil abisHandlerUtil;

	/** The config server file storage URL. */
	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;

	/** The get reg processor identity json. */
	@Value("${registration.processor.identityjson}")
	private String getRegProcessorIdentityJson;

	/** The age limit. */
	@Value("${mosip.kernel.applicant.type.age.limit}")
	private String ageLimit;

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeProcessor.class);

	/**
	 * Process.
	 *
	 * @param object
	 *            the object
	 * @param stageName
	 *            the stage name
	 * @return the message DTO
	 */
	public MessageDTO process(MessageDTO object, String stageName) {
		String registrationId = object.getRid();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "BioDedupeStage::BioDedupeProcessor::entry");
		LogDescription description = new LogDescription();
		object.setMessageBusAddress(MessageBusAddress.BIO_DEDUPE_BUS_IN);
		object.setInternalError(Boolean.FALSE);
		object.setIsValid(Boolean.FALSE);

		boolean isTransactionSuccessful = false;

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		try {
			registrationStatusDto = registrationStatusService.getRegistrationStatus(registrationId);
			String registrationType = registrationStatusDto.getRegistrationType();
			if (registrationType.equalsIgnoreCase(SyncTypeDto.NEW.toString())) {
				String packetStatus = abisHandlerUtil.getPacketStatus(registrationStatusDto);
				if (packetStatus.equalsIgnoreCase(AbisConstant.PRE_ABIS_IDENTIFICATION)) {
					newPacketPreAbisIdentification(registrationStatusDto, object);
				} else if (packetStatus.equalsIgnoreCase(AbisConstant.POST_ABIS_IDENTIFICATION)) {
					postAbisIdentification(registrationStatusDto, object, registrationType);

				}

			} else if (registrationType.equalsIgnoreCase(SyncTypeDto.UPDATE.toString())
					|| registrationType.equalsIgnoreCase(SyncTypeDto.RES_UPDATE.toString())) {
				String packetStatus = abisHandlerUtil.getPacketStatus(registrationStatusDto);
				if (packetStatus.equalsIgnoreCase(AbisConstant.PRE_ABIS_IDENTIFICATION)) {
					updatePacketPreAbisIdentification(registrationStatusDto, object);
				} else if (packetStatus.equalsIgnoreCase(AbisConstant.POST_ABIS_IDENTIFICATION)) {
					postAbisIdentification(registrationStatusDto, object, registrationType);
				}

			} else if (registrationType.equalsIgnoreCase(SyncTypeDto.LOST.toString())
					&& isValidCbeff(registrationId, registrationType)) {
				String packetStatus = abisHandlerUtil.getPacketStatus(registrationStatusDto);

				if (packetStatus.equalsIgnoreCase(AbisConstant.PRE_ABIS_IDENTIFICATION)) {
					lostPacketPreAbisIdentification(registrationStatusDto, object);
				} else if (packetStatus.equalsIgnoreCase(AbisConstant.POST_ABIS_IDENTIFICATION)) {
					List<String> matchedRegIds = abisHandlerUtil
							.getUniqueRegIds(registrationStatusDto.getRegistrationId(), registrationType);
					lostPacketPostAbisIdentification(registrationStatusDto, object, matchedRegIds);
				}

			}

			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.BIOGRAPHIC_VERIFICATION.toString());
			registrationStatusDto.setRegistrationStageName(stageName);
			isTransactionSuccessful = true;

		} catch (DataAccessException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_DATA_ACCESS_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.DATA_ACCESS_EXCEPTION));
			description.setCode(PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode());
			description.setMessage(PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					description.getCode() + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (ApisResourceAccessException e) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_API_RESOURCE_EXCEPTION.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			description.setCode(PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode());
			description.setMessage(PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					description.getCode() + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description + "\n" + ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (CbeffNotFoundException ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto
					.setStatusComment(PlatformErrorMessages.PACKET_BIO_DEDUPE_CBEFF_NOT_PRESENT.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.CBEFF_NOT_PRESENT_EXCEPTION));
			description.setCode(PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode());
			description.setMessage(PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					description.getCode() + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description.getMessage() + "\n" + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (IdentityNotFoundException | IOException ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.IOEXCEPTION));
			description.setCode(PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode());
			description.setMessage(PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					description.getCode() + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description.getMessage() + "\n" + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} catch (Exception ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(
					registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.EXCEPTION));
			description.setCode(PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getCode());
			description.setMessage(PlatformErrorMessages.PACKET_BIO_DEDUPE_FAILED.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					description.getCode() + " -- " + LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					description.getMessage() + "\n" + ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			object.setIsValid(Boolean.FALSE);
		} finally {
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);

			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_BIO_DEDUPE_SUCCESS.getCode()
					: description.getCode();
			String moduleName = ModuleName.BIO_DEDUPE.name();

			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					moduleId, moduleName, registrationId);
		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "BioDedupeStage::BioDedupeProcessor::exit");
		return object;
	}

	/**
	 * New packet pre abis identification.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 * @param object
	 *            the object
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws PacketDecryptionFailureException
	 */
	private void newPacketPreAbisIdentification(InternalRegistrationStatusDto registrationStatusDto, MessageDTO object)
			throws ApisResourceAccessException, IOException, PacketDecryptionFailureException,
			io.mosip.kernel.core.exception.IOException {
		if (isValidCbeff(registrationStatusDto.getRegistrationId(), registrationStatusDto.getRegistrationType())) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIODEDUPE_INPROGRESS);
			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
			object.setMessageBusAddress(MessageBusAddress.ABIS_HANDLER_BUS_IN);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(), BioDedupeConstants.CBEFF_PRESENT_IN_PACKET);
		} else {
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
			object.setIsValid(Boolean.TRUE);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(), BioDedupeConstants.CBEFF_ABSENT_IN_PACKET);
		}
	}

	/**
	 * Update packet pre abis identification.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 * @param object
	 *            the object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws ApisResourceAccessException
	 * @throws PacketDecryptionFailureException
	 */
	private void updatePacketPreAbisIdentification(InternalRegistrationStatusDto registrationStatusDto,
			MessageDTO object) throws IOException, PacketDecryptionFailureException, ApisResourceAccessException,
			io.mosip.kernel.core.exception.IOException {

		InputStream idJsonStream = adapter.getFile(registrationStatusDto.getRegistrationId(),
				PacketFiles.DEMOGRAPHIC.name() + BioDedupeConstants.FILE_SEPARATOR + PacketFiles.ID.name());
		byte[] bytearray = IOUtils.toByteArray(idJsonStream);
		String jsonString = new String(bytearray);
		JSONObject demographicJson = JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
		JSONObject demographicIdentity = JsonUtil.getJSONObject(demographicJson,
				utilities.getGetRegProcessorDemographicIdentity());

		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PVM_IDENTITY_NOT_FOUND.getMessage());
		JSONObject json = JsonUtil.getJSONObject(demographicIdentity, BioDedupeConstants.INDIVIDUAL_BIOMETRICS);
		if (json != null && !json.isEmpty()) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIODEDUPE_INPROGRESS);
			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
			object.setMessageBusAddress(MessageBusAddress.ABIS_HANDLER_BUS_IN);

			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(), BioDedupeConstants.UPDATE_PACKET_BIOMETRIC_NOT_NULL);
		} else {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIODEDUPE_SUCCESS);
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
			object.setIsValid(Boolean.TRUE);

			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(), BioDedupeConstants.UPDATE_PACKET_BIOMETRIC_NULL);
		}
	}

	/**
	 * Post abis identification.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 * @param object
	 *            the object
	 * @param registrationType
	 *            the registration type
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws PacketDecryptionFailureException
	 */
	private void postAbisIdentification(InternalRegistrationStatusDto registrationStatusDto, MessageDTO object,
			String registrationType) throws ApisResourceAccessException, IOException, PacketDecryptionFailureException,
			io.mosip.kernel.core.exception.IOException {

		List<String> matchedRegIds = abisHandlerUtil.getUniqueRegIds(registrationStatusDto.getRegistrationId(),
				registrationType);
		if (matchedRegIds.isEmpty()) {
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
			object.setIsValid(Boolean.TRUE);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIODEDUPE_SUCCESS);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(), BioDedupeConstants.ABIS_RESPONSE_NULL);

		} else {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIOMETRIC_POTENTIAL_MATCH);
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
			packetInfoManager.saveManualAdjudicationData(matchedRegIds, registrationStatusDto.getRegistrationId(),
					DedupeSourceName.BIO);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(), BioDedupeConstants.ABIS_RESPONSE_NOT_NULL);

		}
	}

	/**
	 * Checks if is valid cbeff.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the boolean
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws PacketDecryptionFailureException
	 */
	private Boolean isValidCbeff(String registrationId, String registrationType) throws ApisResourceAccessException,
			IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {

		List<String> pathSegments = new ArrayList<>();
		pathSegments.add(registrationId);

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "BioDedupeProcessor::isValidCbeff()::get BIODEDUPE service call started : ");

		byte[] bytefile = (byte[]) restClientService.getApi(ApiName.BIODEDUPE, pathSegments, "", "", byte[].class);

		if (bytefile != null) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					"BioDedupeProcessor::isValidCbeff()::get BIODEDUPE service call ended and Fetched ByteFile");
			return true;
		} else if (registrationType.equalsIgnoreCase(SyncTypeDto.LOST.toString())) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "BioDedupeProcessor::isValidCbeff()::get BIODEDUPE service call ended"
							+ BioDedupeConstants.CBEFF_NOT_FOUND);
			throw new CbeffNotFoundException(PlatformErrorMessages.PACKET_BIO_DEDUPE_CBEFF_NOT_PRESENT.getMessage());
		} else {

			int age = utilities.getApplicantAge(registrationId);
			int ageThreshold = Integer.parseInt(ageLimit);
			if (age < ageThreshold) {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						BioDedupeConstants.APPLICANT_TYPE_CHILD);
				return false;
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						BioDedupeConstants.APPLICANT_TYPE_ADULT);
				throw new CbeffNotFoundException(
						PlatformErrorMessages.PACKET_BIO_DEDUPE_CBEFF_NOT_PRESENT.getMessage());
			}

		}

	}

	private void lostPacketPreAbisIdentification(InternalRegistrationStatusDto registrationStatusDto,
			MessageDTO object) {

		registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
		registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIODEDUPE_INPROGRESS);
		registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
		object.setMessageBusAddress(MessageBusAddress.ABIS_HANDLER_BUS_IN);

		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationStatusDto.getRegistrationId(), BioDedupeConstants.LOST_PRE_ABIS_IDENTITIFICATION);

	}

	private void lostPacketPostAbisIdentification(InternalRegistrationStatusDto registrationStatusDto,
			MessageDTO object, List<String> matchedRegIds) throws IOException, ApisResourceAccessException,
			PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {

		String registrationId = registrationStatusDto.getRegistrationId();

		if (matchedRegIds.isEmpty()) {
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
			object.setIsValid(Boolean.FALSE);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIOMETRIC_LOST_PACKET_NO_MATCH);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					BioDedupeConstants.NO_MATCH_FOUND_FOR_LOST + registrationId);

		} else if (matchedRegIds.size() == 1) {

			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
			object.setIsValid(Boolean.TRUE);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIOMETRIC_LOST_PACKET_UNIQUE_MATCH);
			packetInfoManager.saveRegLostUinDet(registrationId, matchedRegIds.get(0));
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					BioDedupeConstants.FOUND_UIN_IN_BIO_CHECK + registrationId);

		} else {

			InputStream idJsonStream = adapter.getFile(registrationStatusDto.getRegistrationId(),
					PacketFiles.DEMOGRAPHIC.name() + BioDedupeConstants.FILE_SEPARATOR + PacketFiles.ID.name());
			byte[] bytearray = IOUtils.toByteArray(idJsonStream);
			String jsonString = new String(bytearray);
			JSONObject demographicJson = JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
			JSONObject demographicIdentity = JsonUtil.getJSONObject(demographicJson,
					utilities.getGetRegProcessorDemographicIdentity());

			if (demographicIdentity == null)
				throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PVM_IDENTITY_NOT_FOUND.getMessage());

			List<String> demoMatchedIds = new ArrayList<>();
			Map<String, String> applicantAttribute = getIdJson(demographicIdentity);
			int matchCount = 0;
			if (!applicantAttribute.isEmpty()) {
				List<String> applicantKeysToMatch = new ArrayList<>(applicantAttribute.keySet());

				for (String matchedRegId : matchedRegIds) {
					JSONObject matchedDemographicIdentity = idRepoService.getIdJsonFromIDRepo(matchedRegId,
							utilities.getGetRegProcessorDemographicIdentity());
					if (matchedDemographicIdentity != null) {
						Map<String, String> matchedAttribute = getIdJson(matchedDemographicIdentity);
						if (!matchedAttribute.isEmpty()) {
							if (compareDemoDedupe(applicantAttribute, matchedAttribute, applicantKeysToMatch)) {
								matchCount++;
								demoMatchedIds.add(matchedRegId);
							}
							if (matchCount > 1)
								break;
						}
					}
				}

				if (matchCount == 1) {

					registrationStatusDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
					object.setIsValid(Boolean.TRUE);
					registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.name());
					registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIOMETRIC_LOST_PACKET_UNIQUE_MATCH);
					packetInfoManager.saveRegLostUinDet(registrationId, demoMatchedIds.get(0));
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
							BioDedupeConstants.FOUND_UIN_IN_DEMO_CHECK + registrationId);
				} else {

					registrationStatusDto.setStatusComment(StatusMessage.PACKET_BIOMETRIC_LOST_PACKET_MULTIPLE_MATCH);
					registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.name());
					registrationStatusDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());

					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(),
							BioDedupeConstants.MULTIPLE_RID_FOUND);
					packetInfoManager.saveManualAdjudicationData(matchedRegIds,
							registrationStatusDto.getRegistrationId(), DedupeSourceName.BIO);
				}

			}

		}
	}

	private boolean compareDemoDedupe(Map<String, String> applicantAttribute, Map<String, String> matchedAttribute,
			List<String> applicantKeysToMatch) {
		boolean isMatch = false;

		for (String key : applicantKeysToMatch) {

			if (applicantAttribute.get(key).equals(matchedAttribute.get(key))) {
				isMatch = true;
			} else {
				isMatch = false;
				return isMatch;
			}

		}
		return isMatch;
	}

	private Map<String, String> getIdJson(JSONObject demographicJsonIdentity) throws IOException {
		Map<String, String> attribute = new LinkedHashMap<>();

		String mapperJsonString = Utilities.getJson(utilities.getConfigServerFileStorageURL(),
				utilities.getGetRegProcessorIdentityJson());
		JSONObject mapperJson = JsonUtil.objectMapperReadValue(mapperJsonString, JSONObject.class);
		JSONObject mapperIdentity = JsonUtil.getJSONObject(mapperJson,
				utilities.getGetRegProcessorDemographicIdentity());
		List<String> mapperJsonKeys = new ArrayList<>(mapperIdentity.keySet());

		for (String key : mapperJsonKeys) {
			JSONObject jsonValue = JsonUtil.getJSONObject(mapperIdentity, key);
			Object jsonObject = JsonUtil.getJSONValue(demographicJsonIdentity,
					(String) jsonValue.get(BioDedupeConstants.VALUE));
			if (jsonObject instanceof ArrayList) {
				JSONArray node = JsonUtil.getJSONArray(demographicJsonIdentity,
						(String) jsonValue.get(BioDedupeConstants.VALUE));
				JsonValue[] jsonValues = JsonUtil.mapJsonNodeToJavaObject(JsonValue.class, node);
				if (jsonValues != null)
					for (int count = 0; count < jsonValues.length; count++) {
						String lang = jsonValues[count].getLanguage();
						attribute.put(key + "_" + lang, jsonValues[count].getValue());
					}

			} else if (jsonObject instanceof LinkedHashMap) {
				JSONObject json = JsonUtil.getJSONObject(demographicJsonIdentity,
						(String) jsonValue.get(BioDedupeConstants.VALUE));
				if (json != null)
					attribute.put(key, json.get(BioDedupeConstants.VALUE).toString());
			} else {
				if (jsonObject != null)
					attribute.put(key, jsonObject.toString());
			}
		}

		return attribute;
	}

}
