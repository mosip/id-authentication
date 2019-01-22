package io.mosip.registration.processor.stages.uigenerator;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.EventId;
import io.mosip.registration.processor.core.constant.EventName;
import io.mosip.registration.processor.core.constant.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.ApplicantDocument;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.stages.uingenerator.dto.UinResponseDto;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.Documents;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.IdRequestDto;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.RequestDto;
import io.mosip.registration.processor.stages.uingenerator.util.TriggerNotificationForUIN;
import io.mosip.registration.processor.stages.uingenerator.util.UinStatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class UinGeneratorStage.
 * 
 * @author M1047487 M1049387
 */
@Service
public class UinGeneratorStage extends MosipVerticleManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(UinGeneratorStage.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;

	/** The cluster address. */
	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	/** The localhost. */
	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	/** The cluster manager url. */
	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	/** The id repo create. */
	@Value("${registration.processor.id.repo.create}")
	private String idRepoCreate;

	/** The id repo update. */
	@Value("${registration.processor.id.repo.update}")
	private String idRepoUpdate;

	/** The adapter. */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The registration processor rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	/** The demographic dedupe repository. */
	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The registration id. */
	private String registrationId = "";

	/** The id response DTO. */
	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The id response DTO. */
	IdResponseDTO idResponseDTO = new IdResponseDTO();

	/** The id request DTO. */
	IdRequestDto idRequestDTO = new IdRequestDto();

	/** The identity json. */
	JSONObject identityJson = null;

	/** The demographic identity. */
	JSONObject demographicIdentity = null;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The trigger notification for UIN. */
	@Autowired
	TriggerNotificationForUIN triggerNotificationForUIN;

	private String idRepoApiVersion = "1.0";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MessageDTO process(MessageDTO object) {

		object.setMessageBusAddress(MessageBusAddress.UIN_GENERATION_BUS_IN);
		object.setInternalError(Boolean.FALSE);
		String description = "";
		boolean isTransactionSuccessful = false;
		this.registrationId = object.getRid();
		UinResponseDto uinResponseDto = null;
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);
		try {
			InputStream idJsonStream = adapter.getFile(registrationId,
					PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
			byte[] idJsonBytes = IOUtils.toByteArray(idJsonStream);
			String getJsonStringFromBytes = new String(idJsonBytes);
			JSONParser parser = new JSONParser();
			identityJson = (JSONObject) parser.parse(getJsonStringFromBytes);
			demographicIdentity = (JSONObject) identityJson.get("identity");
			String uinFieldCheck = (String) demographicIdentity.get("UIN");
			boolean isUinCreate = false;
			if (uinFieldCheck == null || ("").equals(uinFieldCheck.trim())) {
				uinResponseDto = (UinResponseDto) registrationProcessorRestClientService.getApi(ApiName.UINGENERATOR,
						null, "", "", UinResponseDto.class);
				long uinInLong = Long.parseLong(uinResponseDto.getUin());
				demographicIdentity.put("UIN", uinInLong);
				idResponseDTO = sendIdRepoWithUin(registrationId, uinResponseDto.getUin());
				isUinCreate = true;
			} else {
				idResponseDTO = updateIdRepowithUin(registrationId, uinFieldCheck);
			}

			if ((idResponseDTO.getResponse() != null)) {
				if (isUinCreate) {
					demographicDedupeRepository.updateUinWrtRegistraionId(registrationId, uinResponseDto.getUin());
					triggerNotificationForUIN.triggerNotification(uinResponseDto.getUin(), isUinCreate);
				} else {
					triggerNotificationForUIN.triggerNotification(uinFieldCheck, isUinCreate);

				}

				registrationStatusDto.setStatusComment(UinStatusMessage.PACKET_UIN_UPDATION_SUCCESS_MSG);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_UIN_UPDATION_SUCCESS.toString());
				isTransactionSuccessful = true;
				description = "UIN updated succesfully for : " + registrationId;

			} else {
				String statusComment = idResponseDTO.getError().get(0).getErrMessage();
				registrationStatusDto.setStatusComment(statusComment);
				object.setInternalError(Boolean.TRUE);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_UIN_UPDATION_FAILURE.toString());
				isTransactionSuccessful = false;
				description = "UIN updation failure for : " + registrationId;
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						idResponseDTO.getError().get(0).getErrMessage() + "  :  " + idResponseDTO.toString());

			}

			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
		} catch (Exception ex) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage() + ex.getMessage()
							+ ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
		} finally {

			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);

		}

		return object;
	}

	/**
	 * Send id repo with uin.
	 *
	 * @param regId
	 *            the reg id
	 * @param uin
	 *            the uin
	 * @return the id response DTO
	 */
	private IdResponseDTO sendIdRepoWithUin(String regId, String uin) {

		List<Documents> documentInfo = getAllDocumentsByRegId(regId);
		RequestDto requestDto = new RequestDto();
		requestDto.setIdentity(demographicIdentity);
		requestDto.setDocuments(documentInfo);

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(uin);

		idRequestDTO.setId(idRepoCreate);
		idRequestDTO.setRegistrationId(regId);
		idRequestDTO.setRequest(requestDto);
		idRequestDTO.setTimestamp(DateUtils.getUTCCurrentDateTimeString());
		idRequestDTO.setVersion(idRepoApiVersion);

		try {
			String result = (String) registrationProcessorRestClientService.postApi(ApiName.IDREPODEV, pathsegments, "",
					"", idRequestDTO, String.class);
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString() + regId, "Response from IdRepo API", "is : " + result);

			Gson gsonObj = new Gson();
			idResponseDTO = gsonObj.fromJson(result, IdResponseDTO.class);
		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
		}
		return idResponseDTO;

	}

	/**
	 * Gets the all documents by reg id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the all documents by reg id
	 */
	private List<Documents> getAllDocumentsByRegId(String regId) {
		List<Documents> applicantDocuments = new ArrayList<>();
		Documents documentsInfoDto = null;
		List<ApplicantDocument> applicantDocument = packetInfoManager.getDocumentsByRegId(regId);
		for (ApplicantDocument entity : applicantDocument) {
			documentsInfoDto = new Documents();
			documentsInfoDto.setCategory(entity.getDocName());
			documentsInfoDto.setValue(CryptoUtil.encodeBase64(entity.getDocStore()));
			applicantDocuments.add(documentsInfoDto);
		}
		return applicantDocuments;
	}

	/**
	 * Update id repowith uin.
	 *
	 * @param regId
	 *            the reg id
	 * @param uin
	 *            the uin
	 * @return the id response DTO
	 */
	private IdResponseDTO updateIdRepowithUin(String regId, String uin) {
		List<Documents> documentInfo = getAllDocumentsByRegId(regId);
		RequestDto requestDto = new RequestDto();
		requestDto.setIdentity(demographicIdentity);
		requestDto.setDocuments(documentInfo);

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(uin);

		idRequestDTO.setId(idRepoUpdate);
		idRequestDTO.setRegistrationId(regId);
		idRequestDTO.setRequest(requestDto);
		idRequestDTO.setTimestamp(DateUtils.formatToISOString(LocalDateTime.now()));
		idRequestDTO.setVersion(idRepoApiVersion);

		try {
			String result = (String) registrationProcessorRestClientService.postApi(ApiName.IDREPODEV, pathsegments, "",
					"", idRequestDTO, String.class);
			Gson gsonObj = new Gson();
			idResponseDTO = gsonObj.fromJson(result, IdResponseDTO.class);
		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
		}

		return idResponseDTO;
	}

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.UIN_GENERATION_BUS_IN,
				MessageBusAddress.UIN_GENERATION_BUS_OUT);
	}
}
