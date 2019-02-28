package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SyncDataProcessDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.config.JobConfigurationService;

public class IntegrationScenario01_LoginCheck extends BaseIntegrationTest {

	@Autowired
	LoginService loginService;
	@Autowired
	JobConfigurationService jobConfigurationService;
	@Autowired
	private GlobalParamService globalParamService;
	@Before
	public void setUp() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());

	}
	
	public void login() {
		// Get user Details
		UserDetail userDetail = loginService.getUserDetail("mosip");
		
		
		// Create DTO
		LoginUserDTO userDTO = new LoginUserDTO();
		userDTO.setUserId("mosip");
		userDTO.setPassword("mosip");
		// Create User Session
		ApplicationContext.getInstance().getApplicationMap().put("userDTO", userDTO);
		// Password check for login Check if Password is same
		String hashPassword = null;
		String password = "mosip";
		byte[] bytePassword = password.getBytes();
		hashPassword = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytePassword));

		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId("mosip");
		authenticationValidatorDTO.setPassword(hashPassword);

//	     userDetail = loginService.getUserDetail(authenticationValidatorDTO.getUserId());
	     String passwordCheck="";
		if (userDetail.getUserPassword().getPwd().equals(authenticationValidatorDTO.getPassword())) {
			passwordCheck=RegistrationConstants.PWD_MATCH;
			
		} else {
			passwordCheck=RegistrationConstants.PWD_MISMATCH;
		}
		assertEquals(RegistrationConstants.PWD_MATCH, passwordCheck);
	}
	@Test
	public void testLoginIntegration() {
		login();
		
		ResponseDTO responseDTO=jobConfigurationService.getLastCompletedSyncJobs();
		//Assert.assertTrue(responseDTO.getSuccessResponseDTO().getCode().equals("INFORMATION"));
		//Assert.assertTrue(responseDTO.getSuccessResponseDTO().getOtherAttributes().size()>0);
		 List<SyncDataProcessDTO> syncData=(List<SyncDataProcessDTO>) responseDTO.getSuccessResponseDTO().getOtherAttributes().get("SYNC-DATA DTO");
		 
		 System.out.println("********"+syncData);
		 
		/* boolean found=false;
				 for(SyncDataProcessDTO data:syncData) {
			 if(data.getJobName().equals("Master Data Sync")) {
				 found=true;
				 Assert.assertTrue(found);
				 found=false;
			 } else if(data.getJobName().equals("Login Credentials Sync")) {
				 found=true;
				 Assert.assertTrue(found);
				 found=false;
			 }
			 
		 }*/
	
	}

}
