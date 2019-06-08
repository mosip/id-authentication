package io.mosip.registration.test.integrationtest;

//import static io.mosip.registration.test.integrationtest.DBUtil.executeQuery;
//import static io.mosip.registration.test.integrationtest.DBUtil.updateQuery;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import static org.testng.Assert.fail;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.Map.Entry;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//
//import io.mosip.registration.config.AppConfig;
//import io.mosip.registration.constants.RegistrationClientStatusCode;
//import io.mosip.registration.constants.RegistrationConstants;
//import io.mosip.registration.context.ApplicationContext;
//import io.mosip.registration.context.SessionContext;
//import io.mosip.registration.dao.RegistrationDAO;
//import io.mosip.registration.dto.PacketStatusDTO;
//import io.mosip.registration.entity.Registration;
//import io.mosip.registration.service.UserOnboardService;
//import io.mosip.registration.service.config.GlobalParamService;
//import io.mosip.registration.service.packet.impl.ReRegistrationServiceImpl;

/**
 * This class is to test the methods of Re-Registration Service using Junit
 * 
 * @author Priya Soni
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = AppConfig.class)
public class ReregistrationServiceTest {
//
//	@Autowired
//	private ReRegistrationServiceImpl reregistrationServiceImpl;
//
//	@Autowired
//	CommonUtil commonUtil;
//
//	@Autowired
//	private GlobalParamService globalParamService;
//
//	@Autowired
//	UserOnboardService userOBservice;
//
//	@Autowired
//	RegistrationDAO regDAO;
//
//	/**
//	 * Declaring CenterID,StationID global
//	 */
//	private String centerID = null;
//	private String stationID = null;
//
//	IntegrationTestConstants integConstant = new IntegrationTestConstants();
//
//	/**
//	 * This method deletes the entries done in DB for testing purpose
//	 * 
//	 * @param id
//	 * @throws SQLException
//	 */
//	@SuppressWarnings("unused")
//	public void cleanUpOld(String id) {
//		int deleteRegRecordsCount = 0;
//		try {
//			int deleted = updateQuery("DELETE FROM REG.REGISTRATION_TRANSACTION WHERE REG_ID='" + id + "'");
//			if (deleted > 0)
//				deleteRegRecordsCount = updateQuery("DELETE FROM REG.REGISTRATION WHERE ID='" + id + "'");
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	private void cleanUp(String registrationId) {
//		System.out.println("registrationId:: " + registrationId);
//		System.out.println("RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode() : "
//				+ RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
//
//		Registration registration = regDAO
//				.getRegistrationById(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode(), registrationId);
//		if (registration == null)
//			registration = regDAO.getRegistrationById(RegistrationClientStatusCode.RE_REGISTER.getCode(),
//					registrationId);
//		String serverCode = registration.getServerStatusCode();
//		registration.setServerStatusCode(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED);
//		commonUtil.deleteProcessedPackets("");
//
//	}
//
//	@Before
//	public void SetUp() {
//		ApplicationContext applicationContext = ApplicationContext.getInstance();
//		applicationContext.setApplicationLanguageBundle();
//		applicationContext.setApplicationMessagesBundle();
//		applicationContext.setLocalLanguageProperty();
//		applicationContext.setLocalMessagesBundle();
//		applicationContext.setApplicationMap(globalParamService.getGlobalParams());
//		centerID = userOBservice.getMachineCenterId().get(integConstant.CENTERIDLBL);
//		stationID = userOBservice.getMachineCenterId().get(integConstant.STATIONIDLBL);
//
//	}
//
//	private void commonUtil(List<String> roles) {
//		String[] roleset = IntegrationTestConstants.REREGISTRATION_ROLES.split(",");
//		if (roleset.length != 2) {
//			fail("Number of roles wrongly configured, needs to be 2");
//			return;
//		}
//		roles.add(roleset[0]);
//		roles.add(roleset[1]);
//		SessionContext.getInstance().getUserContext().setUserId(IntegrationTestConstants.REREGISTRATION_USERID);
//		SessionContext.getInstance().getUserContext().setRoles(roles);
//	}
//
//	/**
//	 * This test case tests the getAllReRegistrationPackets() method of
//	 * ReRegistrationServiceImpl. It first creates a packet and then invokes the
//	 * method under test to validate if the database contains records related to the
//	 * packet.
//	 * 
//	 */
//	@Test
//	public void getAllReRegistrationPacketsTest() {
//
//		List<String> roles = new ArrayList<>();
//		commonUtil(roles);
//
//		List<PacketStatusDTO> packetsDetailsBeforeOperation = reregistrationServiceImpl.getAllReRegistrationPackets();
//		Integer originalRecordsCount = packetsDetailsBeforeOperation.size();
//		String registrationId = null;
//		try {
//			registrationId = commonUtil.packetCreation(RegistrationClientStatusCode.APPROVED.getCode(),
//					integConstant.REGDETAILSJSON, integConstant.IDENTITYJSON, integConstant.POAPOBPORPOIJPG,
//					integConstant.USERIDVAL, centerID, stationID);
//			String updateQuery = "update REG.REGISTRATION set STATUS_CODE = 'PUSHED',CLIENT_STATUS_CODE='PUSHED', SERVER_STATUS_CODE ='Re-Register' where id='"
//					+ registrationId + "'";
//			DBUtil.updateQuery(updateQuery);
//			System.out.println("registrationId: " + registrationId);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		List<PacketStatusDTO> packetsDetailsAfterOperation = reregistrationServiceImpl.getAllReRegistrationPackets();
//		Integer newRecordsCount = packetsDetailsAfterOperation.size();
//		assertTrue(newRecordsCount > originalRecordsCount);
//		cleanUp(registrationId);
//
//	}
//
//	/**
//	 * test case to test the updateReRegistrationStatus method of
//	 * ReRegistrationServiceImpl service
//	 */
//	@Test
//	public void updateReRegistrationStatusApprovedpacket() {
//
//		Map<String, String> reRegistrationStatus = new HashMap<>();
//		List<String> roles = new ArrayList<>();
//		commonUtil(roles);
//
//		String registrationId = null;
//		try {
//			registrationId = commonUtil.packetCreation(RegistrationClientStatusCode.APPROVED.getCode(),
//					integConstant.REGDETAILSJSON, integConstant.IDENTITYJSON, integConstant.POAPOBPORPOIJPG,
//					integConstant.USERIDVAL, centerID, stationID);
//			String updateQuery = "update REG.REGISTRATION set STATUS_CODE = 'PUSHED',CLIENT_STATUS_CODE='PUSHED', SERVER_STATUS_CODE ='Re-Register' where id='"
//					+ registrationId + "'";
//			DBUtil.updateQuery(updateQuery);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		reRegistrationStatus.put(registrationId, "APPROVED");
//
//		boolean result = reregistrationServiceImpl.updateReRegistrationStatus(reRegistrationStatus);
//		System.out.println("registrationId::: " + registrationId);
//		List<String> ids = null;
//		try {
//			ids = executeQuery("SELECT CLIENT_STATUS_COMMENT FROM REG.REGISTRATION WHERE ID='" + registrationId + "'");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		System.out.println(ids);
//		System.out.println("result " + result);
//
//		assertTrue(ids.get(0).compareTo("Re-Register-APPROVED") == 0);
//
//		cleanUp(registrationId);
//
//	}
//
}
