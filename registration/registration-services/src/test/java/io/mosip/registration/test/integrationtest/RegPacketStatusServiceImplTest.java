package io.mosip.registration.test.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
				regPacketStatusServiceImpl.packetSyncStatus("System").getSuccessResponseDTO().getMessage());
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
		assertEquals("REGISTRATION_DELETION_BATCH_JOBS_SUCCESS",responseDTO.getSuccessResponseDTO().getMessage());
		
		registrationRepository.saveAll(listRegistration);
		auditLogControlRepository.deleteAll();
		regTransactionRepository.deleteAll();
		regTransactionRepository.saveAll(listRegTransaction);
		auditLogControlRepository.saveAll(listAuditLog);
		Files.delete(samplePacket);
		}
	
	@Test
	public void syncPacketTest() throws JsonProcessingException {
		responseDTO = regPacketStatusServiceImpl.syncPacket("System");
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		assertEquals(RegistrationConstants.SUCCESS, 
				responseDTO.getSuccessResponseDTO().getMessage());
	}
	
	@Test
	public void packetSyncStatusTestRegIdLessThanTwentyNineChars() throws JsonProcessingException {
		setupDBWithRegId();
		responseDTO = regPacketStatusServiceImpl.packetSyncStatus("System");
		deleteTestData();
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		assertEquals(RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE, 
				responseDTO.getErrorResponseDTOs().get(0).getMessage());
	}
	
	public void setupDBWithRegId() {
		String insertQuery = "insert into reg.registration (id, reg_type, ref_reg_id, status_code, client_Status_code, reg_usr_id, lang_code, regcntr_id, approver_usr_id, is_active, cr_by, cr_dtimes) " + 
				"values ('10011100110018820190311171', 'N', '12345', 'REGISTERED', 'PUSHED', 'mosip', 'ENG', '20916', 'mosip', 'true', 'mosip', '2019-03-11 11:45:01.406')";
		executeQuery(insertQuery);
	}
	
	public void deleteTestData() {
		String deleteQuery = "delete from reg.registration where id = '10011100110018820190311171'";
		executeQuery(deleteQuery);
	}
	
	public void executeQuery(String query) {
		int count = 0;
		Connection con = null;
		PreparedStatement pre = null;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			con = DriverManager.getConnection("jdbc:derby:" + System.getProperty("user.dir") + "/reg;bootPassword=mosip12345");
			pre = con.prepareStatement(query);
			count = pre.executeUpdate();

			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pre.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		}
		if (count == 1) {
			System.out.println("Query executed successfully");
		}else {
			System.out.println("Unable to execute query");
		}
	}
	
}
