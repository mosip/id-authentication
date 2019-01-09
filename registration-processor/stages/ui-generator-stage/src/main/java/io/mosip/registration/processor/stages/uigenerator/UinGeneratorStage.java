package io.mosip.registration.processor.stages.uigenerator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.IdRepoStatusConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.ApplicantDocument;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity;
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

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;
	
	@Value("${registration.processor.id.repo.create}")
	private String idRepoCreate;

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;
	
	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The registration id. */
	private String registrationId = "";

	UinResponseDto uinResponseDto = new UinResponseDto();
	IdResponseDTO idResponseDTO = new IdResponseDTO();
	IdRequestDto idRequestDTO =  new IdRequestDto();
	
	String identityInfo = "{  \r\n" + 
			"   \"identity\":{  \r\n" + 
			"      \"firstName\":{  \r\n" + 
			"         \"label\":\"First Name\",\r\n" + 
			"         \"values\":[  \r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"ar\",\r\n" + 
			"               \"value\":\"ابراهيم\"\r\n" + 
			"            },\r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"fr\",\r\n" + 
			"               \"value\":\"Ibrahim\"\r\n" + 
			"            }\r\n" + 
			"         ]\r\n" + 
			"      },\r\n" + 
			"      \"middleName\":{  \r\n" + 
			"         \"label\":\"Middle Name\",\r\n" + 
			"         \"values\":[  \r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"ar\",\r\n" + 
			"               \"value\":\"بن\"\r\n" + 
			"            },\r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"fr\",\r\n" + 
			"               \"value\":\"Ibn\"\r\n" + 
			"            }\r\n" + 
			"         ]\r\n" + 
			"      },\r\n" + 
			"      \"lastName\":{  \r\n" + 
			"         \"label\":\"Last Name\",\r\n" + 
			"         \"values\":[  \r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"ar\",\r\n" + 
			"               \"value\":\"علي\"\r\n" + 
			"            },\r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"fr\",\r\n" + 
			"               \"value\":\"Ali\"\r\n" + 
			"            }\r\n" + 
			"         ]\r\n" + 
			"      },\r\n" + 
			"      \"dateOfBirth\":{  \r\n" + 
			"         \"label\":\"Date Of Birth\",\r\n" + 
			"         \"value\":\"1955/04/15\"\r\n" + 
			"      },\r\n" + 
			"      \"gender\":{  \r\n" + 
			"         \"label\":\"Gender\",\r\n" + 
			"         \"values\":[  \r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"ar\",\r\n" + 
			"               \"value\":\"الذكر\"\r\n" + 
			"            },\r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"fr\",\r\n" + 
			"               \"value\":\"mâle\"\r\n" + 
			"            }\r\n" + 
			"         ]\r\n" + 
			"      },\r\n" + 
			"      \"addressLine1\":{  \r\n" + 
			"         \"label\":\"Address Line 1\",\r\n" + 
			"         \"values\":[  \r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"ar\",\r\n" + 
			"               \"value\":\"عنوان العينة سطر 1\"\r\n" + 
			"            },\r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"fr\",\r\n" + 
			"               \"value\":\"exemple d'adresse ligne 1\"\r\n" + 
			"            }\r\n" + 
			"         ]\r\n" + 
			"      },\r\n" + 
			"      \"addressLine2\":{  \r\n" + 
			"         \"label\":\"Address Line 2\",\r\n" + 
			"         \"values\":[  \r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"ar\",\r\n" + 
			"               \"value\":\"عنوان العينة سطر 2\"\r\n" + 
			"            },\r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"fr\",\r\n" + 
			"               \"value\":\"exemple d'adresse ligne 2\"\r\n" + 
			"            }\r\n" + 
			"         ]\r\n" + 
			"      },\r\n" + 
			"      \"region\":{  \r\n" + 
			"         \"label\":\"Region\",\r\n" + 
			"         \"values\":[  \r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"ar\",\r\n" + 
			"               \"value\":\"طنجة - تطوان - الحسيمة\"\r\n" + 
			"            },\r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"fr\",\r\n" + 
			"               \"value\":\"Tanger-Tétouan-Al Hoceima\"\r\n" + 
			"            }\r\n" + 
			"         ]\r\n" + 
			"      },\r\n" + 
			"      \"province\":{  \r\n" + 
			"         \"label\":\"Province\",\r\n" + 
			"         \"values\":[  \r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"ar\",\r\n" + 
			"               \"value\":\"فاس-مكناس\"\r\n" + 
			"            },\r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"fr\",\r\n" + 
			"               \"value\":\"Fès-Meknès\"\r\n" + 
			"            }\r\n" + 
			"         ]\r\n" + 
			"      },\r\n" + 
			"      \"phone\":{  \r\n" + 
			"         \"label\":\"Land Line\",\r\n" + 
			"         \"value\":\"9878967890\"\r\n" + 
			"      },\r\n" + 
			"      \"email\":{  \r\n" + 
			"         \"label\":\"Business Email\",\r\n" + 
			"         \"value\":\"abc@xyz.com\"\r\n" + 
			"      },\r\n" + 
			"      \"parentOrGuardianName\":{  \r\n" + 
			"         \"label\":\"Parent/Guardian\",\r\n" + 
			"         \"values\":[  \r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"ar\",\r\n" + 
			"               \"value\":\"سلمى\"\r\n" + 
			"            },\r\n" + 
			"            {  \r\n" + 
			"               \"language\":\"fr\",\r\n" + 
			"               \"value\":\"salma\"\r\n" + 
			"            }\r\n" + 
			"         ]\r\n" + 
			"      },\r\n" + 
			"      \"proofOfAddress\":{  \r\n" + 
			"         \"format\":\"cbeff\",\r\n" + 
			"         \"category\":\"drivingLicense\",\r\n" + 
			"         \"value\":\"test\"\r\n" + 
			"      },\r\n" + 
			"      \"proofOfIdentity\":{  \r\n" + 
			"         \"format\":\"txt\",\r\n" + 
			"         \"category\":\"passport\",\r\n" + 
			"         \"value\":\"test\"\r\n" + 
			"      }\r\n" + 
			"   }\r\n" + 
			"}";


	@Override
	public MessageDTO process(MessageDTO object) {
		String description = "";
		boolean isTransactionSuccessful = false;

		this.registrationId = object.getRid();
		System.out.println(this.registrationId);
		UinResponseDto uinResponseDto= null;
		sendIdRepoWithUin(true,uinResponseDto);

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



	private IdResponseDTO sendIdRepoWithUin(boolean uinAvailable,UinResponseDto uinResponseDto) {	
		
/*		File demographicJsonFile =  FileUtils.getFile("D:\\Mosip\\workspace\\MOS-1065\\mosip\\registration-processor\\packet-info-storage-service\\src\\test\\resources\\DemographicInfo.json");
		InputStream packetDemographicInfoStream = null;
		try {
			packetDemographicInfoStream = new FileInputStream(demographicJsonFile);
			identityInfo = (JSONObject) JsonUtil.inputStreamtoJavaObject(packetDemographicInfoStream,
					JSONObject.class);
			//documentInfoDto = new ArrayList<>();


		} catch (UnsupportedEncodingException | FileNotFoundException e1) {
			e1.printStackTrace();
		} */

		if(uinAvailable) {
			JSONParser parser = new JSONParser(); 
			JSONObject json = new JSONObject();
			try {
				json = (JSONObject) parser.parse(identityInfo);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			List<Documents> documentInfoDto = getAllDocumentsByRegId(uinResponseDto);
			idRequestDTO.setId(idRepoCreate);
			idRequestDTO.setStatus(IdRepoStatusConstant.REGISTERED.toString());
			idRequestDTO.setRegistrationId("27847657360002520181208094034");
			idRequestDTO.setUin("827063769462");
			idRequestDTO.setTimestamp(null);
			idRequestDTO.setRequest(json);
			idRequestDTO.setDocuments(documentInfoDto);

			try {
				idResponseDTO = (IdResponseDTO) registrationProcessorRestClientService.postApi(ApiName.IDREPODEV, "","", idRequestDTO, IdResponseDTO.class);
				System.out.println(idResponseDTO);
			} catch (ApisResourceAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(uinAvailable);
		}
		
		else 
		{

		}
		return idResponseDTO;	
		
	}
	
/*	private IdResponseDTO getIdRepoWithUin(UinResponseDto uinResponseDto) {
		List<String> pathsegments=new ArrayList<>();
		try {
			pathsegments.add(uinResponseDto.getUin());
			idResponseDTO = (IdResponseDTO) registrationProcessorRestClientService.getApi(ApiName.IDREPOSITORY, pathsegments, "","", IdResponseDTO.class);
			System.out.println(idResponseDTO);
		} catch (ApisResourceAccessException e) {
			e.printStackTrace();
		}
		System.out.println(idResponseDTO);
		
		
		return idResponseDTO;
	}*/
	
	private List<Documents> getAllDocumentsByRegId(UinResponseDto uinResponseDto) {
		List<Documents> applicantDocuments = new ArrayList<>();
		Documents documentsInfoDto = new Documents();

		List<ApplicantDocument> test = packetInfoManager.getDocumentsByRegId("27847657360002520181208094033");
		for (ApplicantDocument entity : test) {
			documentsInfoDto.setDocType(entity.getDocName());
			documentsInfoDto.setDocValue(CryptoUtil.encodeBase64(entity.getDocStore()));
			applicantDocuments.add(documentsInfoDto);
		}
		applicantDocuments.add(documentsInfoDto);
		return applicantDocuments;
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
