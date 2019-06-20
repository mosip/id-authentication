package io.mosip.registration.test.integrationtest;

import static io.mosip.registration.test.integrationtest.DBUtil.executeQuery;
import static io.mosip.registration.test.integrationtest.DBUtil.updateQuery;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.service.packet.impl.ReRegistrationServiceImpl;

/**
 * This class is to test the methods of Re-Registration Service using Junit
 * 
 * @author Priya Soni
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class ReregistrationServiceTest {

	@Autowired
	private ReRegistrationServiceImpl reregistrationServiceImpl;


	/**
	 * This method deletes the entries done in DB for testing purpose
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public void cleanUp(String id) throws SQLException {
		updateQuery("DELETE FROM REG.REGISTRATION_TRANSACTION WHERE REG_ID='" + id + "'");
		updateQuery("DELETE FROM REG.REGISTRATION WHERE ID='" + id + "'");
	}

	/**
	 * This method tests whether getAllReRegistrationPackets method returns the
	 * packets marked as Re-register
	 * 
	 */
	@Test
	public void getAllReRegistrationPacketsTest() {

		List<String> roles = new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getUserContext().setRoles(roles);

		String id = System.currentTimeMillis() + "";

		try {
			updateQuery(
					"INSERT INTO REG.REGISTRATION (ID,REG_TYPE,REF_REG_ID,PREREG_ID,STATUS_CODE,LANG_CODE,STATUS_COMMENT,STATUS_DTIMES,ACK_FILENAME,CLIENT_STATUS_CODE,SERVER_STATUS_CODE,CLIENT_STATUS_DTIME,SERVER_STATUS_DTIME,CLIENT_STATUS_COMMENT,SERVER_STATUS_COMMENT,REG_USR_ID,REGCNTR_ID,APPROVER_USR_ID,APPROVER_ROLE_CODE,FILE_UPLOAD_STATUS,UPLOAD_COUNT,UPLOAD_DTIMES,LATEST_REGTRN_ID,LATEST_TRN_TYPE_CODE,LATEST_TRN_STATUS_CODE,LATEST_TRN_LANG_CODE,LATEST_REGTRN_DTIMES,IS_ACTIVE,CR_BY,CR_DTIMES,UPD_BY,UPD_DTIMES)"
							+ "VALUES  ('" + id
							+ "','N','12345',NULL,'PUSHED','ENG',NULL,{ts '2019-02-18 14:37:12.956'},'..//PacketStore/30-Jan-2019/20916100110005020190130161624_Ack.png','PUSHED','Re-Register',{ts '2019-02-18 14:37:12.956'},NULL,'Gender-Photo Mismatch',NULL,'mosip','20916','mosip','SUPERVISOR',NULL,2,{ts '2019-02-18 14:37:18.571'},NULL,NULL,NULL,NULL,NULL,true,'mosip',{ts '2019-01-30 16:17:29.699'},'mosip',{ts '2019-02-18 14:37:12.956'})");
			List<PacketStatusDTO> list = reregistrationServiceImpl.getAllReRegistrationPackets();
			assertNotNull(list);
			cleanUp(id);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method tests whether getAllReRegistrationPackets method returns all the
	 * packets marked as Re-register
	 * 
	 */
	@Test
	public void getAllReRegistrationPacketsCheckNumberTest() {
		List<String> roles = new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getUserContext().setRoles(roles);
		
		String id = System.currentTimeMillis() + "";
		List<String> listExpected = new ArrayList<>();
		
		try {
			updateQuery(
					"INSERT INTO REG.REGISTRATION (ID,REG_TYPE,REF_REG_ID,PREREG_ID,STATUS_CODE,LANG_CODE,STATUS_COMMENT,STATUS_DTIMES,ACK_FILENAME,CLIENT_STATUS_CODE,SERVER_STATUS_CODE,CLIENT_STATUS_DTIME,SERVER_STATUS_DTIME,CLIENT_STATUS_COMMENT,SERVER_STATUS_COMMENT,REG_USR_ID,REGCNTR_ID,APPROVER_USR_ID,APPROVER_ROLE_CODE,FILE_UPLOAD_STATUS,UPLOAD_COUNT,UPLOAD_DTIMES,LATEST_REGTRN_ID,LATEST_TRN_TYPE_CODE,LATEST_TRN_STATUS_CODE,LATEST_TRN_LANG_CODE,LATEST_REGTRN_DTIMES,IS_ACTIVE,CR_BY,CR_DTIMES,UPD_BY,UPD_DTIMES)"
							+ "VALUES  ('" + id
							+ "','N','12345',NULL,'PUSHED','ENG',NULL,{ts '2019-02-18 14:37:12.956'},'..//PacketStore/30-Jan-2019/20916100110005020190130161624_Ack.png','PUSHED','Re-Register',{ts '2019-02-18 14:37:12.956'},NULL,'Gender-Photo Mismatch',NULL,'mosip','20916','mosip','SUPERVISOR',NULL,2,{ts '2019-02-18 14:37:18.571'},NULL,NULL,NULL,NULL,NULL,true,'mosip',{ts '2019-01-30 16:17:29.699'},'mosip',{ts '2019-02-18 14:37:12.956'})");
			listExpected = executeQuery("select id from reg.registration where server_status_code='Re-Register'");
			
			List<PacketStatusDTO> listActual = reregistrationServiceImpl.getAllReRegistrationPackets();
			System.out.println("size " + listExpected.size());
			assertTrue(listExpected.size() == listActual.size());
			cleanUp(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		
		
	}

	/**
	 * 
	 * This method tests whether the status of re-registered packet is updated
	 * 
	 * @throws SQLException
	 * 
	 */
	@Test
	public void updateReRegistrationStatusTest() throws SQLException {
		Map<String, String> reRegistrationStatus = new HashMap<>();

		List<String> roles = new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getUserContext().setRoles(roles);

		String id = System.currentTimeMillis() + "";

		updateQuery(
				"INSERT INTO REG.REGISTRATION (ID,REG_TYPE,REF_REG_ID,PREREG_ID,STATUS_CODE,LANG_CODE,STATUS_COMMENT,STATUS_DTIMES,ACK_FILENAME,CLIENT_STATUS_CODE,SERVER_STATUS_CODE,CLIENT_STATUS_DTIME,SERVER_STATUS_DTIME,CLIENT_STATUS_COMMENT,SERVER_STATUS_COMMENT,REG_USR_ID,REGCNTR_ID,APPROVER_USR_ID,APPROVER_ROLE_CODE,FILE_UPLOAD_STATUS,UPLOAD_COUNT,UPLOAD_DTIMES,LATEST_REGTRN_ID,LATEST_TRN_TYPE_CODE,LATEST_TRN_STATUS_CODE,LATEST_TRN_LANG_CODE,LATEST_REGTRN_DTIMES,IS_ACTIVE,CR_BY,CR_DTIMES,UPD_BY,UPD_DTIMES)"
						+ "VALUES  ('" + id
						+ "','N','12345',NULL,'SYNCED','ENG',NULL,{ts '2019-02-18 14:37:12.956'},'..//PacketStore/30-Jan-2019/20916100110005020190130161624_Ack.png','SYNCED',NULL,{ts '2019-02-18 14:37:12.956'},NULL,'Gender-Photo Mismatch',NULL,'mosip','20916','mosip','SUPERVISOR',NULL,2,{ts '2019-02-18 14:37:18.571'},NULL,NULL,NULL,NULL,NULL,true,'mosip',{ts '2019-01-30 16:17:29.699'},'mosip',{ts '2019-02-18 14:37:12.956'})");

		reRegistrationStatus.put(id, "APPROVED");

		boolean result = reregistrationServiceImpl.updateReRegistrationStatus(reRegistrationStatus);

		List<String> ids = executeQuery("SELECT CLIENT_STATUS_COMMENT FROM REG.REGISTRATION WHERE ID='" + id + "'");

		System.out.println(ids);
		System.out.println("result " + result);

		assertTrue(ids.get(0).compareTo("Re-Register-APPROVED") == 0);

		cleanUp(id);

	}
}
