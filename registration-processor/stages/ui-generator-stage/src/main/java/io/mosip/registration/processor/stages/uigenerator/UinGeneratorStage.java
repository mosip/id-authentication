package io.mosip.registration.processor.stages.uigenerator;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.idgenerator.uin.dto.UinResponseDto;
import io.mosip.kernel.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.stages.util.UinAvailabilityCheck;


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
	
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;
	
	@Autowired
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	/** The registration id. */
	private String registrationId = "";
	
	UinResponseDto uinResponseDto = new UinResponseDto();
	IdResponseDTO idResponseDTO = new IdResponseDTO();
	IdRequestDTO idRequestDTO =  new IdRequestDTO();
	
	@Override
	public MessageDTO process(MessageDTO object) {
		this.registrationId = object.getRid();
		System.out.println(this.registrationId);

	
		InputStream packetMetaInfoStream = adapter.getFile("27847657360002520181208094032" , PacketFiles.PACKETMETAINFO.name());
	
			try {
				PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
						PacketMetaInfo.class);
			
		
		try {
			UinResponseDto uinResponseDto=	(UinResponseDto) registrationProcessorRestClientService.getApi(ApiName.UINGENERATOR, null, "",
					"", UinResponseDto.class);
			
			UinAvailabilityCheck uinAvailabilityCheck = new UinAvailabilityCheck();
			uinAvailabilityCheck.uinCheck("uin",packetMetaInfo);
			
			
			
			/*
			idRequestDTO.setUin(uinResponseDto.getUin());
			idRequestDTO.setRegistrationId(object.getRid());
			idRequestDTO.setId("mosip.id.create");
//			idRequestDTO.setTimestamp();
			
			
			IdResponseDTO idResponseDTO=	(IdResponseDTO) registrationProcessorRestClientService.postApi(ApiName.IDREPOSITORY,
					"", "",idRequestDTO , IdResponseDTO.class);*/
			
			
		} 
		catch (ApisResourceAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		return null;
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
