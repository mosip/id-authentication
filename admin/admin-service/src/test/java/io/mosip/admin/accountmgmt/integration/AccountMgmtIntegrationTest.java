package io.mosip.admin.accountmgmt.integration;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.accountmgmt.entity.RegistrationCenterUser;
import io.mosip.admin.accountmgmt.entity.id.RegistrationCenterUserID;
import io.mosip.admin.accountmgmt.repository.RegistrationCenterUserRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class AccountMgmtIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	RestTemplate restTemplate;

	@Value("${mosip.admin.accountmgmt.auth-manager-base-uri}")
	private String authManagerBaseUrl;

	@Value("${mosip.admin.accountmgmt.user-name-url}")
	private String userNameUrl;

	@Value("${mosip.admin.accountmgmt.unblock-url}")
	private String unBlockUrl;

	@Value("${mosip.admin.accountmgmt.change-passoword-url}")
	private String changePassword;

	@Value("${mosip.admin.accountmgmt.reset-password-url}")
	private String resetPassword;
	
	@Value("${mosip.admin.accountmgmt.user-detail}")
	private String userDetailBasedOnUidUrl;

	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private RegistrationCenterUserRepository registrationCenterUserRepository;

	private StringBuilder uriBuilder;
	
	private List<RegistrationCenterUser> registrationCenters;

	@Before
	public void setUp() {
		uriBuilder.append(authManagerBaseUrl);
		RegistrationCenterUser reg= new RegistrationCenterUser();
		RegistrationCenterUserID registrationCenterUserID= new RegistrationCenterUserID();
		registrationCenterUserID.setRegCenterId("10001");
		registrationCenterUserID.setUserId("110001");
		reg.setRegistrationCenterUserID(registrationCenterUserID);
		registrationCenters= new ArrayList<>();
		registrationCenters.add(reg);
		
	}
	
	
	@Test
	pulic void getRegistrationCentrerUser() {
		
	}
	
	

}
