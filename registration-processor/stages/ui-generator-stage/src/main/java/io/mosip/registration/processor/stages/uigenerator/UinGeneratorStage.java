package io.mosip.registration.processor.stages.uigenerator;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.EventId;
import io.mosip.registration.processor.core.constant.EventName;
import io.mosip.registration.processor.core.constant.EventType;
import io.mosip.registration.processor.core.constant.IdRepoStatusConstant;
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

/**
 * The Class UinGeneratorStage.
 * @author M1047487
 */
@Service
public class UinGeneratorStage extends MosipVerticleManager {



	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(UinGeneratorStage.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The secs. */
	// @Value("${landingzone.scanner.stage.time.interval}")
	private long secs = 30;

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
	//** The packet info manager. *//*
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The registration id. */
	private String registrationId = "";

	/** The uin response dto. */
	UinResponseDto uinResponseDto = new UinResponseDto();
	
	/** The id response DTO. */
	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	IdResponseDTO idResponseDTO = new IdResponseDTO();
	
	/** The id request DTO. */
	IdRequestDto idRequestDTO =  new IdRequestDto();
	JSONObject identityJson = null;
	
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		String description = "";
		boolean isTransactionSuccessful = false;
		this.registrationId = object.getRid();
		UinResponseDto uinResponseDto= null;


		try {

			InputStream idJsonStream = adapter.getFile(registrationId,PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());
			byte[] idJsonBytes = IOUtils.toByteArray(idJsonStream);
			String getJsonStringFromBytes = new String(idJsonBytes);
			JSONParser parser = new JSONParser(); 
			identityJson = (JSONObject) parser.parse(getJsonStringFromBytes);
			JSONObject	demographicIdentity = (JSONObject) identityJson.get("identity");
			String uinFieldCheck=(String) demographicIdentity.get("uin");

			if(uinFieldCheck==null || ("").equals(uinFieldCheck)) {

				uinResponseDto=	(UinResponseDto) registrationProcessorRestClientService.getApi(ApiName.UINGENERATOR, null, "","", UinResponseDto.class);
				System.out.println("UIN GENERATION HAPPENING:    "+uinResponseDto.getUin());
				//idResponseDTO=sendIdRepoWithUin(isUinAvailable,uinResponseDto);


			}else {
				
			
			}
			

			demographicDedupeRepository.updateUinWrtRegistraionId("27847657360002520181208094036", uinResponseDto.getUin());

		} 
		catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),registrationId,PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage()+e.getMessage());
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
		}catch (Exception ex) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),registrationId,PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage()+ex.getMessage());
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
		}finally {

			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);


		}


		return null;
	}

	/**
	 * Send id repo with uin.
	 *
	 * @param regId the reg id
	 * @param uin the uin
	 * @return the id response DTO
	 */
	private IdResponseDTO sendIdRepoWithUin(String regId, String uin) {
		
		List<Documents> documentInfoDto = getAllDocumentsByRegId(regId);
		idRequestDTO.setId(idRepoCreate);
		idRequestDTO.setStatus(IdRepoStatusConstant.REGISTERED.toString());
		idRequestDTO.setRegistrationId("27847657360002520181208094011");
		idRequestDTO.setUin("284092194624");
		idRequestDTO.setTimestamp(DateUtils.formatToISOString(LocalDateTime.now()));
		idRequestDTO.setRequest(identityJson);
		idRequestDTO.setDocuments(documentInfoDto);

		try {
			String myResponse = (String) registrationProcessorRestClientService.postApi(ApiName.IDREPODEV, "", "",
					idRequestDTO, String.class);
			Gson gsonObj = new Gson();
			idResponseDTO = gsonObj.fromJson(myResponse, IdResponseDTO.class);
		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage() + e.getMessage());
		}
		return idResponseDTO;

	}
	
	/**
	 * Gets the all documents by reg id.
	 *
	 * @param regId the reg id
	 * @return the all documents by reg id
	 */
	private List<Documents> getAllDocumentsByRegId(String regId) {
		List<Documents> applicantDocuments = new ArrayList<>();
		Documents documentsInfoDto;
		List<ApplicantDocument> applicantDocument = packetInfoManager
				.getDocumentsByRegId(regId);
		for (ApplicantDocument entity : applicantDocument) {
			documentsInfoDto = new Documents();
			documentsInfoDto.setDocType(entity.getDocName());
			documentsInfoDto.setDocValue(CryptoUtil.encodeBase64(entity.getDocStore()));
			applicantDocuments.add(documentsInfoDto);
		}
		return applicantDocuments;
	}
	
	

	private IdResponseDTO updateIdRepowithUin(String RegId, String uin) {
		
		List<Documents> documentInfoDto = getAllDocumentsByRegId(RegId);
		idRequestDTO.setId(idRepoUpdate);
		idRequestDTO.setStatus(IdRepoStatusConstant.REGISTERED.toString());
		idRequestDTO.setRegistrationId(RegId);
		idRequestDTO.setUin(uin);
		idRequestDTO.setTimestamp(DateUtils.formatToISOString(LocalDateTime.now()));
		idRequestDTO.setRequest(identityJson);
		idRequestDTO.setDocuments(documentInfoDto);
		try {
			String myResponse = (String) registrationProcessorRestClientService.postApi(ApiName.IDREPODEV, "", "",
					idRequestDTO, String.class);
			Gson gsonObj = new Gson();
			idResponseDTO = gsonObj.fromJson(myResponse, IdResponseDTO.class);
		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage() + e.getMessage());
		}
		return idResponseDTO;
	}


	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {

		MessageDTO mm=new MessageDTO();
		mm.setRid("132345");
		this.process(mm);

		//MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		//this.consumeAndSend(mosipEventBus, MessageBusAddress.UIN_GENERATION_BUS_IN, MessageBusAddress.UIN_GENERATION_BUS_OUT);

	}


}
