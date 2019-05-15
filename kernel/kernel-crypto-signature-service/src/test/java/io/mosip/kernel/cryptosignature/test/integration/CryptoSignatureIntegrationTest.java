package io.mosip.kernel.cryptosignature.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.cryptosignature.test.CryptoSignatureTestBootApplication;

@SpringBootTest(classes = CryptoSignatureTestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CryptoSignatureIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SignatureUtil signatureUtil;

	private static final String SIGNRESPONSEREQUEST = "{ \"id\": \"string\", \"metadata\": {}, \"request\": { \"response\": \"admin\" }, \"requesttime\": \"2018-12-10T06:12:52.994Z\", \"version\": \"string\" }";

	StringBuilder builder;
	SignatureResponse signResponse;

	@Value("${mosip.kernel.syncdata.auth-manager-base-uri}")
	private String authBaseUri;

	@Value("${mosip.kernel.syncdata.auth-manager-roles}")
	private String authAllRolesUri;

	@Before
	public void setup() {
		signResponse = new SignatureResponse();
		signResponse.setData("asdasdsadf4e");
		signResponse.setResponseTime(LocalDateTime.now(ZoneOffset.UTC));
	}

	private void mockSuccess() {
		when(signatureUtil.signResponse(Mockito.anyString())).thenReturn(signResponse);
	}

	@Test
	@WithUserDetails("reg-processor")
	public void signResponseSuccess() throws Exception {
		mockSuccess();
		mockMvc.perform(post("/signresponse").contentType(MediaType.APPLICATION_JSON).content(SIGNRESPONSEREQUEST))
				.andExpect(status().isOk());
	}

}
