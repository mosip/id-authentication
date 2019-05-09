package io.mosip.registration.processor.biodedupe.stage;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.biodedupe.dao.BioDedupDao;
import io.mosip.registration.processor.biodedupe.stage.exception.AdultCbeffNotPresentException;
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
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.RegBioRefEntity;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

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

	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;

	@Value("${registration.processor.identityjson}")
	private String getRegProcessorIdentityJson;

	@Autowired
	Utilities utilities;

	@Value("${mosip.kernel.applicant.type.age.limit}")
	private String ageLimit;

	private static final String RE_PROCESSING = "re-processing";

	private static final String HANDLER = "handler";

	private static final String NEW_PACKET = "New-packet";

	private static final String REG_TYPE_NEW = "New";

	private static final String REG_TYPE_UPDATE = "Update";

	private static final String BIOGRAPHIC_VERIFICATION = "BIOGRAPHIC_VERIFICATION";

	private static final String IDENTIFY = "identify";
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeProcessor.class);

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	BioDedupDao bioDedupDao;

	RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;
	
	@Autowired
	public PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

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
				String packetStatus = utilities.getElapseStatus(registrationStatusDto, BIOGRAPHIC_VERIFICATION);
				if (packetStatus.equalsIgnoreCase(NEW_PACKET) || packetStatus.equalsIgnoreCase(RE_PROCESSING)) {
					newPacketProcessing(registrationStatusDto, object);
				} else if (packetStatus.equalsIgnoreCase(HANDLER)) {
					newPacketHandlerProcessing(registrationStatusDto, object);
				}

			} else if (registrationType.equalsIgnoreCase(REG_TYPE_UPDATE)) {
				String packetStatus = utilities.getElapseStatus(registrationStatusDto, BIOGRAPHIC_VERIFICATION);
				if (packetStatus.equalsIgnoreCase(NEW_PACKET) || packetStatus.equalsIgnoreCase(RE_PROCESSING)) {
					updatePacketProcessing(registrationStatusDto, object);
				} else if (packetStatus.equalsIgnoreCase(HANDLER)) {
					updatePacketHandlerProcessing(registrationStatusDto);
				}

			}

			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.BIOGRAPHIC_VERIFICATION.toString());
			registrationStatusDto.setRegistrationStageName(stageName);
			isTransactionSuccessful = true;
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "BioDedupeStage::BioDedupeProcessor::exit");

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
		} catch (AdultCbeffNotPresentException ex) {
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED.name());
			registrationStatusDto.setStatusComment(ExceptionUtils.getMessage(ex));
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.ADULT_CBEFF_NOT_PRESENT_EXCEPTION));
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

	private Boolean checkCBEFF(String registrationId) throws ApisResourceAccessException, IOException, ParseException {

		List<String> pathSegments = new ArrayList<>();
		pathSegments.add(registrationId);
		byte[] bytefile = (byte[]) restClientService.getApi(ApiName.BIODEDUPE, pathSegments, "", "", byte[].class);

		if (bytefile != null)
			return true;

		else {

			int age = utilities.getApplicantAge(registrationId);
			int ageThreshold = Integer.parseInt(ageLimit);
			if (age < ageThreshold) {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"Applicant type is child and Cbeff not present returning false");
				return false;
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"Applicant type is adult and Cbeff not present throwing exception");
				throw new AdultCbeffNotPresentException(
						PlatformErrorMessages.PACKET_BIO_DEDUPE_CBEFF_NOT_PRESENT.getMessage());
			}

		}

	}

	private String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;

	}

	private void newPacketProcessing(InternalRegistrationStatusDto registrationStatusDto, MessageDTO object)
			throws ApisResourceAccessException, IOException, ParseException {
		if (checkCBEFF(registrationStatusDto.getRegistrationId())) {

			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
			object.setMessageBusAddress(MessageBusAddress.ABIS_HANDLER_BUS_IN);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					"Cbeff is present in the packet, destination stage is abis_handler");
		} else {
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
			object.setIsValid(Boolean.TRUE);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					"Cbeff is absent in the packet for child, destination stage is UIN");
		}
	}

	private void updatePacketProcessing(InternalRegistrationStatusDto registrationStatusDto, MessageDTO object)
			throws IOException {

		String getIdentityJsonString = Utilities.getJson(configServerFileStorageURL, getRegProcessorIdentityJson);
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		RegistrationProcessorIdentity regProcessorIdentityJson = mapIdentityJsonStringToObject
				.readValue(getIdentityJsonString, RegistrationProcessorIdentity.class);

		if (regProcessorIdentityJson.getIdentity().getIndividualBiometrics() != null) {

			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
			object.setMessageBusAddress(MessageBusAddress.ABIS_HANDLER_BUS_IN);

			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					"Update packet individual biometric not null, destination stage is abis_handler");
		}
		registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
		object.setIsValid(Boolean.TRUE);

		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationStatusDto.getRegistrationId(),
				"Update packet individual biometric null, destination stage is UIN");

	}

	private void newPacketHandlerProcessing(InternalRegistrationStatusDto registrationStatusDto, MessageDTO object) {
		String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());

		List<AbisResponseDetEntity> abisResponseDetEntities = bioDedupDao
				.getAbisResponseDetailRecords(latestTransactionId, IDENTIFY);
		if (abisResponseDetEntities.isEmpty()) {
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
			object.setIsValid(Boolean.TRUE);

			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(), "ABIS response Details null, destination stage is UIN");

		} else {
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
			object.setIsValid(Boolean.FALSE);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationStatusDto.getRegistrationId(),
					"ABIS response Details not null, destination stage is Manual_verification");

		}
	}

	private void updatePacketHandlerProcessing(InternalRegistrationStatusDto registrationStatusDto) throws ApisResourceAccessException, IOException {
		String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());
		List<RegBioRefEntity> regBioRefEntities = new ArrayList<>();
		// TODO logic for abis handler update scenario
		/*
		 * 1.get abis requestIDs(List<AbisRequestDto>) from ABIS_REQUEST TABLE based on
		 * latestTransactionId (ref_regtrn_id) 2.get Abis
		 * ResponseIDs(List<AbisResponseDto>) from ABIS_RESPONSE Table Base on
		 * abisRequestDto (abis_request_id) 3.get ABIS_RESPONSE_DET
		 * (List<AbisResponseDetailDto>) from ABIS_RESPONSE_DET based on AbisResponseDto
		 * ( abis_response_id) 4.IF list<AbisResponseDetailDto> is not Emplty THEN get
		 * RIDs from reg_abisref From abis_response_det table base on
		 * (mactched_abis_ref_id) get UIN based on RID check if all UIN are same for all
		 * RID if all are same sent it to UIN stage if all UIN are not same send it to
		 * Manual-varificatin stage
		 * 
		 */

		List<AbisResponseDetEntity> abisResponseDetEntities = bioDedupDao
				.getAbisResponseDetailRecords(latestTransactionId, "identify");
		if (abisResponseDetEntities.isEmpty()) {
			// send to UIN stage
		} else {
			// get RID based on machedRefId of response detail list
			// match existing packet UIN with all respective RID UIN

			for (AbisResponseDetEntity abisResponseDetEntity : abisResponseDetEntities) {
				regBioRefEntities.addAll(bioDedupDao.getBioRefIds(abisResponseDetEntity.getId().getMatchedBioRefId()));
			}
			for (RegBioRefEntity regBioRefEntity : regBioRefEntities) {
				regBioRefEntity.getId().getRegId();
				List<String> pathSegments = new ArrayList<>();
				pathSegments.add("27847657360002520190320095010");
					IdResponseDTO response = (IdResponseDTO) restClientService.getApi(ApiName.IDREPOSITORY, pathSegments, "type", "all", ResponseWrapper.class);
					System.out.println(response);
					Gson gsonObj = new Gson();
					String jsonString = gsonObj.toJson(response.getResponse());
					JSONObject identityJson = (JSONObject) JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
					System.out.println(jsonString);
					String uin = (String) identityJson.get("UIN");
					if(uin!=""/*Current RID UIN*/) {
						// send to Manual Varificatiom
						//packetInfoManager.saveManualAdjudicationData(uniqueMatchedRefIds, String registrationStatusDto.getRegistrationId(),DedupeSourceName sourceName)
					}
			}
		}
	}

}
