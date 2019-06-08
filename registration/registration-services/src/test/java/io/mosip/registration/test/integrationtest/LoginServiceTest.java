package io.mosip.registration.test.integrationtest;

//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.testng.annotations.DataProvider;
//
//import io.mosip.registration.config.AppConfig;
//import io.mosip.registration.context.ApplicationContext;
//import io.mosip.registration.dto.AuthorizationDTO;
//import io.mosip.registration.dto.RegistrationCenterDetailDTO;
//import io.mosip.registration.entity.UserDetail;
//import io.mosip.registration.service.login.impl.LoginServiceImpl;
//import net.minidev.json.JSONArray;
//import net.minidev.json.JSONObject;
//import net.minidev.json.parser.JSONParser;
//import net.minidev.json.parser.ParseException;

/**
 * This class contains test cases for login service
 * 
 * @author Priya Soni
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = AppConfig.class)
public class LoginServiceTest extends BaseIntegrationTest {

//	@Autowired
//	private LoginServiceImpl loginServiceImpl;
//
//	IntegrationTestConstants integConstants = new IntegrationTestConstants();
//
//	@BeforeClass
//	public static void setUp() {
//		System.out.println("---------------------------");
//		ApplicationContext applicationContext = ApplicationContext.getInstance();
//		applicationContext.setApplicationLanguageBundle();
//		applicationContext.setApplicationMessagesBundle();
//		applicationContext.setLocalLanguageProperty();
//		applicationContext.setLocalMessagesBundle();
//	}
//
//	/**
//	 * This method tests the functionality of getModesOfLogin method Verify whether
//	 * list of modes of login is returned with respect to the user roles passed as
//	 * input
//	 * 
//	 * @throws URISyntaxException
//	 * 
//	 */
////	@Test
////	public void getModesOfLoginTest() throws URISyntaxException {
////
////		String path = new ClassPathResource(
////				"src/test/resources/testData/LoginServiceData/LoginServiceTestResources.json").getPath();
////		File jsonInputFile = new File(path);
////
////		try {
////			Object obj;
////			try {
////				obj = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(new FileReader(jsonInputFile));
////				JSONObject jsonObject = (JSONObject) obj;
////
////				Set<String> roleSet = new HashSet<>();
////				JSONArray rolesArray = (JSONArray) jsonObject.get("roles");
////				for (int i = 0; i < rolesArray.size(); i++) {
////					roleSet.add(rolesArray.get(i).toString());
////				}
////
////				List<String> modes = loginServiceImpl.getModesOfLogin("login_auth", roleSet); // onboard_auth
////				assertNotNull(modes);
////
////			} catch (ParseException e) {
////				e.printStackTrace();
////			}
////
////		} catch (FileNotFoundException e) {
////			e.printStackTrace();
////		}
////
////	}
//
//	@Test
//	public void getModesOfLoginTestInvalidAuthtype() {
//		Set<String> roleSet = new HashSet<>();
//		String authType = "login_auth_wrong";
//		String role = "REGISTRATION_SUPERVISOR";
//		roleSet.add(role);
//
//		List<String> modes = loginServiceImpl.getModesOfLogin(authType, roleSet); // onboard_auth
//		assertTrue(modes.size() == 0);
//
//	}
//
//	@Test
//	public void getModesOfLoginTestLoginAuthTypeSingleRole() {
//
//		Set<String> roleSet = new HashSet<>();
//		String authType = "login_auth";
//		String role = "REGISTRATION_SUPERVISOR";
//		roleSet.add(role);
//
//		List<String> modes = loginServiceImpl.getModesOfLogin(authType, roleSet); // onboard_auth
//		assertTrue(modes.size() > 0);
//
//	}
//
//	@Test
//	public void getModesOfLoginTestLoginAuthTypeMultipleRoles() {
//
//		Set<String> roleSet = new HashSet<>();
//		String authType = "login_auth";
//		String role = "REGISTRATION_SUPERVISOR";
//		String role1 = "REGISTRATION_OFFICER";
//		roleSet.add(role);
//		roleSet.add(role1);
//
//		List<String> modes = loginServiceImpl.getModesOfLogin(authType, roleSet); // onboard_auth
//		assertTrue(modes.size() > 0);
//	}
//
//	@Test
//	public void getModesOfLoginTestOnboardAuthTypeMultipleRoles() {
//
//		Set<String> roleSet = new HashSet<>();
//		String authType = "onboard_auth";
//		String role = "REGISTRATION_SUPERVISOR";
//		String role1 = "REGISTRATION_OFFICER";
//		roleSet.add(role);
//		roleSet.add(role1);
//
//		List<String> modes = loginServiceImpl.getModesOfLogin(authType, roleSet); // onboard_auth
//		assertTrue(modes.size() > 0);
//	}
//
//	@Test
//	public void getModesOfLoginTestOnboardAuthTypeSingleRole() {
//
//		Set<String> roleSet = new HashSet<>();
//		String authType = "onboard_auth";
//		String role = "REGISTRATION_SUPERVISOR";
//		roleSet.add(role);
//
//		List<String> modes = loginServiceImpl.getModesOfLogin(authType, roleSet); // onboard_auth
//		assertTrue(modes.size() > 0);
//	}
//
//	/**
//	 * This method tests the functionality of getUserDetail method Verify whether
//	 * user details are returned with respect to the user id passed as input
//	 * 
//	 * 
//	 */
//	@Test
//	public void getUserDetailTest() {
//		String path = new ClassPathResource(
//				"src/test/resources/testData/LoginServiceData/LoginServiceTestResources.json").getPath();
//
//		File jsonInputFile = new File(path);
//		try {
//			Object obj = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(new FileReader(jsonInputFile));
//			JSONObject jsonObject = (JSONObject) obj;
//			JSONArray array = (JSONArray) jsonObject.get("userIds");
//
//			for (int i = 0; i < array.size(); i++) {
//
//				UserDetail userDetail = loginServiceImpl.getUserDetail(array.get(i).toString());
//				assertTrue(userDetail.getId() != null);
//
//			}
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * Test case to fetch user detail for non-existing user
//	 */
//	@Test
//	public void getUserDetailTestWrongUsername() {
//
//		String username = "abcdef"; // Read from external file
//
//		UserDetail userDetail = loginServiceImpl.getUserDetail(username);
//		assertTrue(userDetail == null);
//
//	}
//
//	/**
//	 * This method tests the functionality of getRegistrationCenterDetails method
//	 * Verify whether registration center details are returned with respect to the
//	 * center id passed as input
//	 */
//	@Test
//	public void getRegistrationCenterDetailsTest() {
//
//		String path = new ClassPathResource(
//				"src/test/resources/testData/LoginServiceData/LoginServiceTestResources.json").getPath();
//
//		File jsonInputFile = new File(path);
//
//		try {
//			Object obj = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(new FileReader(jsonInputFile));
//			JSONObject jsonObject = (JSONObject) obj;
//			JSONArray array = (JSONArray) jsonObject.get("regCenterIds");
//
//			for (int i = 0; i < array.size(); i++) {
//				RegistrationCenterDetailDTO centerDetailDTO = loginServiceImpl
//						.getRegistrationCenterDetails(array.get(i).toString(), "eng");
//				assertTrue(centerDetailDTO.getRegistrationCenterName() != null);
//			}
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * 
//	 * This method tests the functionality of getScreenAuthorizationDetails method
//	 * Verify whether Screen Authorization Details are returned with respect to the
//	 * user roles passed as input
//	 * 
//	 */
//	@Test
//	public void getScreenAuthorizationDetailsTest() {
//		String path = new ClassPathResource(
//				"src/test/resources/testData/LoginServiceData/LoginServiceTestResources.json").getPath();
//
//		File jsonInputFile = new File(path);
//
//		try {
//			Object obj;
//			try {
//				obj = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(new FileReader(jsonInputFile));
//				JSONObject jsonObject = (JSONObject) obj;
//
//				List<String> roleSet = new ArrayList<>();
//				JSONArray rolesArray = (JSONArray) jsonObject.get("roles");
//				for (int i = 0; i < rolesArray.size(); i++) {
//					roleSet.add(rolesArray.get(i).toString());
//					assertNotNull(loginServiceImpl.getScreenAuthorizationDetails(roleSet).getAuthorizationScreenId());
//				}
//
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	@Test
//	public void getScreenAuthorizationDetailsTestSingleRole() {
//		List<String> roleSet = new ArrayList<>();
//		String role = "REGISTRATION_SUPERVISOR";
//		roleSet.add(role);
//		AuthorizationDTO authorizationDTO = loginServiceImpl.getScreenAuthorizationDetails(roleSet);
//		assertNotNull(authorizationDTO.getAuthorizationScreenId());
//		assertTrue(authorizationDTO.getAuthorizationScreenId().size() != 0);
//	}
//
//	@Test
//	public void getScreenAuthorizationDetailsTestWrongRole() {
//		List<String> roleSet = new ArrayList<>();
//		String role = "REGISTRATION_SUPERVISOR1";
//		roleSet.add(role);
//		AuthorizationDTO authorizationDTO = loginServiceImpl.getScreenAuthorizationDetails(roleSet);
//		assertNotNull(authorizationDTO.getAuthorizationScreenId());
//		assertTrue(authorizationDTO.getAuthorizationScreenId().size() == 0);
//	}
//
//	@Test
//	public void getScreenAuthorizationDetailsTestTwoRole() {
//		List<String> roleSet = new ArrayList<>();
//		String role = "REGISTRATION_SUPERVISOR";
//		String role1 = "REGISTRATION_OFFICER";
//		roleSet.add(role);
//		roleSet.add(role1);
//		AuthorizationDTO authorizationDTO = loginServiceImpl.getScreenAuthorizationDetails(roleSet);
//		assertNotNull(authorizationDTO.getAuthorizationScreenId());
//	}
//
//	@Test
//	public void getScreenAuthorizationDetailsTestThreeRole() {
//
//		List<String> roleSet = new ArrayList<>();
//		String role = "REGISTRATION_SUPERVISOR";
//		String role1 = "REGISTRATION_OFFICER";
//		String role2 = "REGISTRATION_ADMIN";
//		roleSet.add(role);
//		roleSet.add(role1);
//		roleSet.add(role2);
//		AuthorizationDTO authorizationDTO = loginServiceImpl.getScreenAuthorizationDetails(roleSet);
//		assertNotNull(authorizationDTO.getAuthorizationScreenId());
//
//	}
//
//	/**
//	 * This test is to check whether updateLoginParams method updates attributes of
//	 * a UserDetail object
//	 * 
//	 */
//	@Test
//	public void updateLoginParamsTest() {
//
//		UserDetail userDetail = loginServiceImpl.getUserDetail(integConstants.USERIDVAL);
//		String newId = "newId" + System.currentTimeMillis();
//		userDetail.setId(newId);
//		loginServiceImpl.updateLoginParams(userDetail);
//		assertNotNull(loginServiceImpl.getUserDetail(newId));
//
//		// clean up
//		userDetail = loginServiceImpl.getUserDetail(newId);
//		userDetail.setId(integConstants.USERIDVAL);
//		loginServiceImpl.updateLoginParams(userDetail);
//	}
//
//	/**
//	 * This test checks whether DataIntegrityViolationException is thrown when
//	 * trying to update a new user detail object
//	 * 
//	 * 
//	 */
//	@Test(expected = DataIntegrityViolationException.class)
//	public void updateLoginParamsExceptionTest() {
//
//		UserDetail userDetail = new UserDetail();
//		userDetail.setId("newId");
//		loginServiceImpl.updateLoginParams(userDetail);
//	}
//
}
