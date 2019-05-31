package io.mosip.registration.test.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.text.ParseException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.icu.text.SimpleDateFormat;

import cern.colt.matrix.doublealgo.Formatter;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SyncDataProcessDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.config.JobConfigurationService;
import io.mosip.registration.service.login.LoginService;

public class IntegrationScenario01_LastCompletedSyncJobs extends BaseIntegrationTest {

	@Autowired
	LoginService loginService;
	@Autowired
	JobConfigurationService jobConfigurationService;
	@Autowired
	private GlobalParamService globalParamService;
	
	
	
	//this integration flow gives the list of completed sync jobs
	//integration flow for checking last successful jobs: login-->getLastCompletedSyncJobs()
	
	public void login() {
		// Get user Details
		UserDTO userDetail = loginService.getUserDetail("mosip");
		
		
				// Password check for login Check if Password is same
				String hashPassword = null;
				String password = "mosip";
				byte[] bytePassword = password.getBytes();
				hashPassword = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytePassword));

				AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
				authenticationValidatorDTO.setUserId("mosip");
				authenticationValidatorDTO.setPassword(hashPassword);

//			     userDetail = loginService.getUserDetail(authenticationValidatorDTO.getUserId());
			     String passwordCheck="";
				if (userDetail.getUserPassword().getPwd().equals(authenticationValidatorDTO.getPassword())) {
					passwordCheck=RegistrationConstants.PWD_MATCH;
					
				} else {
					passwordCheck=RegistrationConstants.PWD_MISMATCH;
				}
				assertEquals(RegistrationConstants.PWD_MATCH, passwordCheck);
	}
	@Test
	public void testLoginIntegration() throws InterruptedException, ParseException {
		login();
		//ResponseDTO responseDTO=jobConfigurationService.getLastCompletedSyncJobs();
		 //List<SyncDataProcessDTO> syncData=(List<SyncDataProcessDTO>) responseDTO.getSuccessResponseDTO().getOtherAttributes().get("SYNC-DATA DTO");
		
		// System.out.println("********"+syncData);
		
		Date timeformat = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String newdate = date.format(timeformat);
		System.out.println("*********"+newdate);
		
		
		jobConfigurationService.initiateJobs();
		ResponseDTO responsedto = jobConfigurationService.executeAllJobs();
		Thread.sleep(5000);
		System.out.println("**********"+responsedto.getSuccessResponseDTO().getMessage());
		
	
	
	}

}
