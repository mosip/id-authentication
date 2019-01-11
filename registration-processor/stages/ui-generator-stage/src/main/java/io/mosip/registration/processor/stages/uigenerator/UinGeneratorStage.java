package io.mosip.registration.processor.stages.uigenerator;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
	//** The packet info manager. *//*
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The registration id. */
	private String registrationId = "";

	/** The uin response dto. */
	UinResponseDto uinResponseDto = new UinResponseDto();
	
	/** The id response DTO. */
	IdResponseDTO idResponseDTO = new IdResponseDTO();
	
	/** The id request DTO. */
	IdRequestDto idRequestDTO =  new IdRequestDto();
	
	/** The identity info. */
	String identityInfo = "{\n" + 
			"    \"identity\" : {\n" + 
			"      \"IDSchemaVersion\" : 1.0,\n" + 
			"      \"UIN\" : 629140831958,\n" + 
			"      \"fullName\" : [ {\n" + 
			"        \"language\" : \"ara\",\n" + 
			"        \"value\" : \"ابراهيم بن علي\"\n" + 
			"      }, {\n" + 
			"        \"language\" : \"fre\",\n" + 
			"        \"value\" : \"Ibrahim Ibn Ali\"\n" + 
			"      } ],\n" + 
			"      \"dateOfBirth\" : \"1955/04/15\",\n" + 
			"      \"age\" : 45,\n" + 
			"      \"gender\" : [ {\n" + 
			"        \"language\" : \"ara\",\n" + 
			"        \"value\" : \"الذكر\"\n" + 
			"      }, {\n" + 
			"        \"language\" : \"fre\",\n" + 
			"        \"value\" : \"mâle\"\n" + 
			"      } ],\n" + 
			"      \"addressLine1\" : [ {\n" + 
			"        \"language\" : \"ara\",\n" + 
			"        \"value\" : \"عنوان العينة سطر 1\"\n" + 
			"      }, {\n" + 
			"        \"language\" : \"fre\",\n" + 
			"        \"value\" : \"exemple d'adresse ligne 1\"\n" + 
			"      } ],\n" + 
			"      \"addressLine2\" : [ {\n" + 
			"        \"language\" : \"ara\",\n" + 
			"        \"value\" : \"عنوان العينة سطر 2\"\n" + 
			"      }, {\n" + 
			"        \"language\" : \"fre\",\n" + 
			"        \"value\" : \"exemple d'adresse ligne 2\"\n" + 
			"      } ],\n" + 
			"      \"addressLine3\" : [ {\n" + 
			"        \"language\" : \"ara\",\n" + 
			"        \"value\" : \"عنوان العينة سطر 2\"\n" + 
			"      }, {\n" + 
			"        \"language\" : \"fre\",\n" + 
			"        \"value\" : \"exemple d'adresse ligne 2\"\n" + 
			"      } ],\n" + 
			"      \"region\" : [ {\n" + 
			"        \"language\" : \"ara\",\n" + 
			"        \"value\" : \"طنجة - تطوان - الحسيمة\"\n" + 
			"      }, {\n" + 
			"        \"language\" : \"fre\",\n" + 
			"        \"value\" : \"Tanger-Tétouan-Al Hoceima\"\n" + 
			"      } ],\n" + 
			"      \"province\" : [ {\n" + 
			"        \"language\" : \"ara\",\n" + 
			"        \"value\" : \"فاس-مكناس\"\n" + 
			"      }, {\n" + 
			"        \"language\" : \"fre\",\n" + 
			"        \"value\" : \"Fès-Meknès\"\n" + 
			"      } ],\n" + 
			"      \"city\" : [ {\n" + 
			"        \"language\" : \"ara\",\n" + 
			"        \"value\" : \"الدار البيضاء\"\n" + 
			"      }, {\n" + 
			"        \"language\" : \"fre\",\n" + 
			"        \"value\" : \"Casablanca\"\n" + 
			"      } ],\n" + 
			"      \"postalCode\" : \"570004\",\n" + 
			"      \"phone\" : \"9876543210\",\n" + 
			"      \"email\" : \"abc@xyz.com\",\n" + 
			"      \"CNIENumber\" : 6789545678909,\n" + 
			"      \"localAdministrativeAuthority\" : [ {\n" + 
			"        \"language\" : \"ara\",\n" + 
			"        \"value\" : \"سلمى\"\n" + 
			"      }, {\n" + 
			"        \"language\" : \"fre\",\n" + 
			"        \"value\" : \"salma\"\n" + 
			"      } ],\n" + 
			"      \"parentOrGuardianRIDOrUIN\" : 212124324784912,\n" + 
			"      \"parentOrGuardianName\" : [ {\n" + 
			"        \"language\" : \"ara\",\n" + 
			"        \"value\" : \"سلمى\"\n" + 
			"      }, {\n" + 
			"        \"language\" : \"fre\",\n" + 
			"        \"value\" : \"salma\"\n" + 
			"      } ],\n" + 
			"      \"proofOfAddress\" : {\n" + 
			"        \"format\" : \"pdf\",\n" + 
			"        \"type\" : \"drivingLicense\",\n" + 
			"        \"value\" : \"fileReferenceID\"\n" + 
			"      },\n" + 
			"      \"proofOfIdentity\" : {\n" + 
			"        \"format\" : \"txt\",\n" + 
			"        \"type\" : \"passport\",\n" + 
			"        \"value\" : \"fileReferenceID\"\n" + 
			"      },\n" + 
			"      \"proofOfRelationship\" : {\n" + 
			"        \"format\" : \"pdf\",\n" + 
			"        \"type\" : \"passport\",\n" + 
			"        \"value\" : \"fileReferenceID\"\n" + 
			"      },\n" + 
			"      \"proofOfDateOfBirth\" : {\n" + 
			"        \"format\" : \"pdf\",\n" + 
			"        \"type\" : \"passport\",\n" + 
			"        \"value\" : \"fileReferenceID\"\n" + 
			"      },\n" + 
			"      \"individualBiometrics\" : {\n" + 
			"        \"format\" : \"cbeff\",\n" + 
			"        \"version\" : 1.0,\n" + 
			"        \"value\" : \"fileReferenceID\"\n" + 
			"      },\n" + 
			"      \"parentOrGuardianBiometrics\" : {\n" + 
			"        \"format\" : \"cbeff\",\n" + 
			"        \"version\" : 1.0,\n" + 
			"        \"value\" : \"fileReferenceID\"\n" + 
			"      }\n" + 
			"    }\n" + 
			"}";


	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		String description = "";
		boolean isTransactionSuccessful = false;

		this.registrationId = object.getRid();
		System.out.println(this.registrationId);
		UinResponseDto uinResponseDto= null;

/*
		try {

			UinResponseDto uinResponseDto=	(UinResponseDto) registrationProcessorRestClientService.getApi(ApiName.UINGENERATOR, null, "","", UinResponseDto.class);
			System.out.println("UIN GENERATION HAPPENING:    "+uinResponseDto.getUin());
			UinAvailabilityCheck uinAvailabilityCheck = new UinAvailabilityCheck();
			uinAvailabilityCheck.uinCheck("27847657360002520181208094036",adapter);*/
			
			//call idrepo method
			//if response is succesfull from idrepo service update uin in demo dedupe table.
		//demographicDedupeRepository.updateUinWrtRegistraionId("27847657360002520181208094036", uinResponseDto.getUin());
		
			//after succesfully updation of UIn in reg processor DB call trigger function for  triggerring  sms and email to particualr UIN holder
	
	/*	} 
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


		}*/


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
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(identityInfo);
		} catch (ParseException e1) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage() + e1.getMessage());
		}
		List<Documents> documentInfo = getAllDocumentsByRegId(regId);
		idRequestDTO.setId(idRepoCreate);
		idRequestDTO.setStatus(IdRepoStatusConstant.REGISTERED.toString());
		idRequestDTO.setRegistrationId(regId);
		idRequestDTO.setUin(uin);
		idRequestDTO.setTimestamp(DateUtils.formatToISOString(LocalDateTime.now()));
		idRequestDTO.setRequest(json);
		idRequestDTO.setDocuments(documentInfo);
		try {
			String result = (String) registrationProcessorRestClientService.postApi(ApiName.IDREPODEV, "", "",
					idRequestDTO, String.class);
			Gson gsonObj = new Gson();
			idResponseDTO = gsonObj.fromJson(result, IdResponseDTO.class);
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
	
	
	/**
	 * Update IdRepo with uin.
	 *
	 * @param RegId as the registration Id
	 * @param uin the uin
	 * @return the IdResponse DTO with the failure/success
	 */
	private IdResponseDTO updateIdRepowithUin(String RegId, String uin) {
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(identityInfo);
		} catch (ParseException e1) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage() + e1.getMessage());
		}
		List<Documents> documentInfo = getAllDocumentsByRegId(RegId);
		idRequestDTO.setId(idRepoUpdate);
		idRequestDTO.setStatus(IdRepoStatusConstant.REGISTERED.toString());
		idRequestDTO.setRegistrationId(RegId);
		idRequestDTO.setUin(uin);
		idRequestDTO.setTimestamp(DateUtils.formatToISOString(LocalDateTime.now()));
		idRequestDTO.setRequest(json);
		idRequestDTO.setDocuments(documentInfo);
		try {
			String result = (String) registrationProcessorRestClientService.postApi(ApiName.IDREPODEV, "", "",
					idRequestDTO, String.class);
			Gson gsonObj = new Gson();
			idResponseDTO = gsonObj.fromJson(result, IdResponseDTO.class);
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
