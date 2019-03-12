package io.mosip.registration.test.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Files;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.repositories.AuditLogControlRepository;
import io.mosip.registration.repositories.RegTransactionRepository;
import io.mosip.registration.repositories.RegistrationRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.impl.RegPacketStatusServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class RegPacketStatusServiceImplTest {
	@Autowired
	GlobalParamService globalParamService;

	@Autowired
	private RegPacketStatusServiceImpl regPacketStatusServiceImpl;
	@Autowired
	private RegistrationRepository registrationRepository;
	@Autowired
	private AuditLogControlRepository auditLogControlRepository;
	@Autowired
	private RegTransactionRepository regTransactionRepository;
	ResponseDTO responseDTO = null;
	
	@Before
	public void setup() {
		ApplicationContext context=ApplicationContext.getInstance();
		context.setApplicationLanguageBundle();
		context.setApplicationMessagesBundle();
		context.setLocalLanguageProperty();
		context.setLocalMessagesBundle();
		Map<String,Object> map=globalParamService.getGlobalParams();
		map.put(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS,"120");
		context.setApplicationMap(map);

	}
	
	@Test
	public void packetSyncStatusTest() {
		assertEquals(RegistrationConstants.PACKET_STATUS_SYNC_SUCCESS_MESSAGE, 
				regPacketStatusServiceImpl.packetSyncStatus().getSuccessResponseDTO().getMessage());
	}
	/**
	 * Test Case for verifying the deletion of RegPacket.
	 * @throws IOException
	 */
	
	@Test
	public void deleteRegistrationPacketsTest() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("Test String");
		String packetPath=System.getProperty("user.dir")+"\\samplePacket_Ack.html";
		File samplePacket=new File(packetPath);
		FileOutputStream out1=new FileOutputStream(samplePacket);
		byte[] data = sb.toString().getBytes();
		out1.write(data, 0, data.length);
		out1.close();
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-130);
		List<Registration> listRegistration=registrationRepository.findAll();
		List<AuditLogControl> listAuditLog=auditLogControlRepository.findAll();
		AuditLogControl sampleAuditLog=listAuditLog.get(0);
		sampleAuditLog.setRegistrationId("101");
		List<RegistrationTransaction> listRegTransaction=regTransactionRepository.findAll();
		RegistrationTransaction sampleRegTransaction=listRegTransaction.get(0);
		sampleRegTransaction.setRegId("101");
		Registration sampleReg=listRegistration.get(0);
		sampleReg.setId("101");
		sampleReg.setCrDtime(new Timestamp(cal.getTimeInMillis()));
		sampleReg.setAckFilename(packetPath);
		sampleReg.setClientStatusCode(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
		registrationRepository.save(sampleReg);
		auditLogControlRepository.save(sampleAuditLog);
		regTransactionRepository.save(sampleRegTransaction);
		
		responseDTO = regPacketStatusServiceImpl.deleteRegistrationPackets();
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		assertEquals(responseDTO.getSuccessResponseDTO().getMessage(),"Registartion Packets Deletion Successful ");
		
//		responseDTO = regPacketStatusServiceImpl.packetSyncStatus();
//		System.out.println(mapper.writer().writeValueAsString(responseDTO));
//		assertEquals(RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE, 
//				responseDTO.getErrorResponseDTOs().get(0).getMessage());
		
		registrationRepository.saveAll(listRegistration);
		auditLogControlRepository.deleteAll();
		regTransactionRepository.deleteAll();
		regTransactionRepository.saveAll(listRegTransaction);
		auditLogControlRepository.saveAll(listAuditLog);
		Files.delete(samplePacket);
		}
	
	@Test
	public void syncPacketTest() throws JsonProcessingException {
		responseDTO = regPacketStatusServiceImpl.syncPacket();
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
	}
	
}
