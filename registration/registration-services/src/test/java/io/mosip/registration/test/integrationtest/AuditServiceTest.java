package io.mosip.registration.test.integrationtest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.audit.impl.AuditServiceImpl;
import io.mosip.registration.service.config.GlobalParamService;

import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuditServiceTest extends BaseIntegrationTest{
	
	@Autowired
	private AuditServiceImpl auditServiceImpl;
	@Autowired
	private   AuditLogControlDAO repo;
	@Autowired
	private  GlobalParamService globalParamService;
	
	public void setGlobalConfig() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());
	}
	
	public void setupData() throws JsonParseException, JsonMappingException, IOException {
//		PacketHandlerServiceTest packet = new PacketHandlerServiceTest();
//		packet.testHandelPacket();
		updateDB("3");
//		AuditLogControl data = new AuditLogControl();
//		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
//	
//		data.setRegistrationId("20916100110005020190130161624");
//		data.setAuditLogFromDateTime(Timestamp.valueOf(LocalDateTime.now().minusHours(3)));
//		data.setAuditLogToDateTime(Timestamp.valueOf(LocalDateTime.now().minusMinutes(10)));
//		data.setAuditLogSyncDateTime(currentTimestamp);
//		data.setAuditLogPurgeDateTime(currentTimestamp);
//		data.setCrBy("TestUser");
//		data.setCrDtime(currentTimestamp);
//		data.setUpdBy("TestUser");
//		data.setUpdDtimes(currentTimestamp);
//		repo.save(data);
	}
	
	@Test
	public void auditLogsDeleteTestConfiguredDaysNull() throws JsonProcessingException {
		setGlobalConfig();
		/**
		 * Create backup of value
		 */
		String value=  ApplicationContext.getInstance().map().get(RegistrationConstants.AUDIT_LOG_DELETION_CONFIGURED_DAYS).toString();		
		
		ApplicationContext.getInstance().map().put(RegistrationConstants.AUDIT_LOG_DELETION_CONFIGURED_DAYS, null);
		ResponseDTO responseDTO = auditServiceImpl.deleteAuditLogs();
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		Assert.assertEquals(io.mosip.registration.constants.RegistrationConstants.AUDIT_LOGS_DELETION_FLR_MSG, 
				responseDTO.getErrorResponseDTOs().get(0).getMessage());
		/**
		 * revert back state 
		 */
		ApplicationContext.getInstance().map().put(RegistrationConstants.AUDIT_LOG_DELETION_CONFIGURED_DAYS, value);
	}
	
	@Test
	public void testAuditLogsDelete() throws IOException {
		setGlobalConfig();
		setupData();
		ResponseDTO responseDTO = auditServiceImpl.deleteAuditLogs();
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		Assert.assertEquals(io.mosip.registration.constants.RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG, 
				responseDTO.getSuccessResponseDTO().getMessage());
	}
	
	@Test
	public void testAuditLogsDeleteNoLogs() throws JsonProcessingException {
		updateDB("100");
		try {
			ResponseDTO responseDTO = auditServiceImpl.deleteAuditLogs();
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(mapper.writer().writeValueAsString(responseDTO));
			Assert.assertEquals(io.mosip.registration.constants.RegistrationConstants.AUDIT_LOGS_DELETION_EMPTY_MSG, 
					responseDTO.getSuccessResponseDTO().getMessage());
		} finally {
			updateDB("3");
		}
	}
	
	public void updateDB(String val) {
		Connection con;
		PreparedStatement pre;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			con = DriverManager.getConnection("jdbc:derby:D:/Mosip_QA_080_build/mosip/registration/registration-services/reg;bootPassword=mosip12345", "", "");
			pre = con.prepareStatement("update master.global_param set val='"+val+"' where code='AUDIT_LOG_DELETION_CONFIGURED_DAYS'");
			int count = pre.executeUpdate();

			con.commit();

			if (count == 1) {
				System.out.println("Updated AUDIT_LOG_DELETION_CONFIGURED_DAYS to "+val);
			}

			pre.close();
			con.close();
		} catch (Exception e) {
			System.out.println("Unable to update database");
		}
	}
}
