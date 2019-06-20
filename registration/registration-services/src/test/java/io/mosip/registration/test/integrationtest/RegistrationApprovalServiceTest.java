package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.impl.RegistrationApprovalServiceImpl;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

/**
 * This class contains methods to test the RegistrationApprovalService
 * 
 * 
 * @author Priya Soni
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class RegistrationApprovalServiceTest {

	@Autowired
	private RegistrationApprovalServiceImpl registrationApprovalServiceImpl;

	/**
	 * To test whether getEnrollmentByStatus method fetches registration objects
	 * according to the input status
	 *
	 */

	@Test
	public void getEnrollmentByStatusIDTest() {

		List<String> roles = new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getUserContext().setRoles(roles);

		String[] arguments;
		File jsonInputFile = new File(System.getProperty("user.dir")
				+ "\\src\\test\\resources\\testData\\RegistrationApprovalServiceData\\RegistrationApprovalTestResources.json");
		try {
			Object obj;
			try {
				obj = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(new FileReader(jsonInputFile));
				JSONObject jsonObject = (JSONObject) obj;

				JSONArray statusArray = (JSONArray) jsonObject.get("statusCodes");
				arguments = new String[statusArray.size()];
				for (int i = 0; i < statusArray.size(); i++) {
					arguments[i] = statusArray.get(i).toString();
					List<RegistrationApprovalDTO> list = new ArrayList<>();
					list = registrationApprovalServiceImpl.getEnrollmentByStatus(arguments[i]);
					assertTrue(list.size() > 0);
					for (RegistrationApprovalDTO objRes : list) {
						assertTrue(objRes.getId().length() > 0);
					}
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * To test the behavior of getEnrollmentByStatus method for empty input
	 * 
	 *
	 *
	 */
	@Test
	public void getEnrollmentByStatusIDEmptyInputTest() {
		List<String> roles = new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getUserContext().setRoles(roles);

		String status = "";
		List<RegistrationApprovalDTO> list = new ArrayList<>();
		list = registrationApprovalServiceImpl.getEnrollmentByStatus(status);
		assertFalse(list.size() > 0);

	}

	/**
	 * 
	 * To test whether rejected packets have a status comment
	 * 
	 */
	@Test
	public void getEnrollmentByStatusCommentTest() {

		List<String> roles = new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getUserContext().setRoles(roles);

		List<RegistrationApprovalDTO> list = new ArrayList<>();
		list = registrationApprovalServiceImpl.getEnrollmentByStatus("REJECTED");
		for (RegistrationApprovalDTO obj : list) {
			assertTrue(obj.getStatusComment() != null);
		}

	}

	/**
	 * To test the behavior of getEnrollmentByStatusAck method for empty input
	 * 
	 * 
	 */
	/*@Test
	public void getEnrollmentByStatusAckEmptyInputTest() {

		List<String> roles = new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getUserContext().setRoles(roles);

		String status = "";
		List<RegistrationApprovalDTO> list = new ArrayList<>();
		list = registrationApprovalServiceImpl.getEnrollmentByStatus(status);
		assertFalse(list.size() > 0);

	}*/

	/**
	 * 
	 * To test whether acknowledgement file path is provided for every packet
	 * 
	 */
	@Test
	public void getEnrollmentByStatusAckTest() {

		List<String> roles = new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getUserContext().setRoles(roles);

		String[] arguments;
		File jsonInputFile = new File(System.getProperty("user.dir")
				+ "\\src\\test\\resources\\testData\\RegistrationApprovalServiceData\\RegistrationApprovalTestResources.json");
		try {
			Object obj;
			try {
				obj = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(new FileReader(jsonInputFile));
				JSONObject jsonObject = (JSONObject) obj;

				JSONArray statusArray = (JSONArray) jsonObject.get("statusCodes");
				arguments = new String[statusArray.size()];
				for (int i = 0; i < statusArray.size(); i++) {
					arguments[i] = statusArray.get(i).toString();
					List<RegistrationApprovalDTO> list = new ArrayList<>();
					list = registrationApprovalServiceImpl.getEnrollmentByStatus(arguments[i]);
					assertTrue(list.size() > 0);
					for (RegistrationApprovalDTO objRes : list) {
						assertTrue(objRes.getAcknowledgementFormPath().length() >= 0);
					}
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * To test whether status of a registration object is updated using
	 * updateRegistration method
	 * 
	 * 
	 */
	@Test
	public void updateRegistrationTest() {
		List<String> roles = new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getUserContext().setRoles(roles);

		RegistrationApprovalDTO obj = registrationApprovalServiceImpl.getEnrollmentByStatus("REGISTERED").get(0);
		String registrationID = obj.getId();
		String statusComment = obj.getStatusComment();
		Registration updatedRegistration = registrationApprovalServiceImpl.updateRegistration(registrationID,
				"updating", "APPROVED");

		List<RegistrationApprovalDTO> list = new ArrayList<>();
		list = registrationApprovalServiceImpl.getEnrollmentByStatus("REGISTERED");
		assertFalse(list.contains(obj));

		boolean passed = false;

		list = registrationApprovalServiceImpl.getEnrollmentByStatus("APPROVED");
		for (RegistrationApprovalDTO objUpdated : list) {
			if (objUpdated.getId().compareTo(registrationID) == 0) {
				passed = true;
				break;
			}
		}

		assertTrue(passed);

		registrationApprovalServiceImpl.updateRegistration(registrationID, statusComment, "REGISTERED");

	}

	/**
	 * Exception test
	 * 
	 * 
	 */
	@Test(expected = NullPointerException.class)
	public void updateRegistrationExceptionTest() {

		RegistrationApprovalDTO obj = registrationApprovalServiceImpl.getEnrollmentByStatus("REGISTERED").get(0);
		String registrationID = obj.getId();
		registrationApprovalServiceImpl.updateRegistration(registrationID, "updating", "APPROVED");
	}

}
