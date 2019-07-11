package io.mosip.admin.securitypolicy.test;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.TestBootApplication;
import io.mosip.admin.securitypolicy.dto.UserRoleDto;
import io.mosip.admin.securitypolicy.dto.UserRoleResponseDto;
import io.mosip.kernel.core.exception.ServiceError;

@SpringBootTest(classes=TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class SecurityPolicyControllerTest {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MockMvc mockMvc;

	private MockRestServiceServer mockServer;

	@Value("${mosip.admin.security.policy.userrole-auth-url}")
	private String authmanagerUserRoleUrl;

	@Value("${mosip.admin.app-id}")
	private String appId;

	@Autowired
	private ObjectMapper mapper;

	@Before
	public void init() {
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	private String url = "/security/authfactors/{username}";

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetAuthFactorSuccess() throws Exception {
		String user = "zonalAdmin";

		UserRoleResponseDto response = new UserRoleResponseDto();
		UserRoleDto userRole = new UserRoleDto();
		userRole.setRole("ZONALADMIN");
		userRole.setUserId(user);
		response.setResponse(userRole);

		mockServer.expect(requestToUriTemplate(authmanagerUserRoleUrl, appId, user)).andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
						.body(mapper.writeValueAsString(response)));

		mockMvc.perform(get(url, user)).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetAuthFactorNoPolicyFound() throws Exception {

		String user = "zonalAdmin";

		UserRoleResponseDto response = new UserRoleResponseDto();
		UserRoleDto userRole = new UserRoleDto();
		userRole.setRole("ADMIN");
		userRole.setUserId(user);
		response.setResponse(userRole);

		mockServer.expect(requestToUriTemplate(authmanagerUserRoleUrl, appId, user)).andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
						.body(mapper.writeValueAsString(response)));

		mockMvc.perform(get(url, user)).andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testGetAuthFactorRestClientException() throws Exception {

		String user = "zonalAdmin";

		UserRoleResponseDto response = new UserRoleResponseDto();
		UserRoleDto userRole = new UserRoleDto();
		userRole.setRole("ADMIN");
		userRole.setUserId(user);
		response.setResponse(userRole);

		mockServer.expect(requestToUriTemplate(authmanagerUserRoleUrl, appId, user)).andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_XML)
						.body(mapper.writeValueAsString(response)));

		mockMvc.perform(get(url, user)).andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testGetAuthFactorClientError() throws Exception {
		
		String user = "zonalAdmin";

		UserRoleResponseDto response = new UserRoleResponseDto();
		response.setErrors(Arrays.asList(new ServiceError("XXX","Error Occured")));

		mockServer.expect(requestToUriTemplate(authmanagerUserRoleUrl, appId, user)).andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_XML)
						.body(mapper.writeValueAsString(response)));
		mockMvc.perform(get(url, user)).andExpect(status().isOk());
	}
}
