package io.mosip.admin.accountmgmt.integration;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/*@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc*/
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

	@Autowired
	private ObjectMapper objectMapper;

	private StringBuilder uriBuilder;

	@Before
	public void setUp() {
		uriBuilder.append(authManagerBaseUrl);
	}

}
