package io.mosip.kernel.signature.test.integration;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.signature.test.SignatureTestBootApplication;

@SpringBootTest(classes = SignatureTestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CryptoSignatureIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SignatureUtil signatureUtil;

	@MockBean
	private RestTemplate restTemplate;

	private static final String SIGNRESPONSEREQUEST = "{ \"id\": \"string\", \"metadata\": {}, \"request\": { \"data\": \"admin\" }, \"requesttime\": \"2018-12-10T06:12:52.994Z\", \"version\": \"string\" }";
	private static final String VALIDATEWITHPUBLICKEY = "{ \"id\": \"string\", \"metadata\": {}, \"request\": { \"publickey\": \"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnoocJbIeMuAzqSzuJX9CvXmFFka3Fz3C-u9vz6c8RsJSKBCe_SAOi31IvL992kuy1qO4XTS-cUuirx-djuF0E7r5TbQFKlNa-FoPJu8QRIGw2rWVQsc2c0Aqd5cfhr9fgTsM3V3URl1jXY645v9EPE0Ih5E26ld6JQQQ90mpvoa6XlJEf5SUAOuzvr5ws5VoZgEQ6wjO05dZSaEL9vrA5npsNSwLb55FqZb7w9qLZfYbPOBVxUZ-HTddBLP6KvlIHWzsVapjvhUHPgSO0AZDYmx3kkKb7jFuWelPibNyKy619AAnlQX3VR39CKi-6sPLRABs4v-npsFLNz9Wd_VJHwIDAQAB\", \"data\": \"admin\", \"signature\": \"ZeNsCOsdgf0UgpXDMry82hrHS6b1ZKvS-tZ_3HBGQHleIu1fZA6LNTtx7XZPFeC8dxsyuYO_iN3mVExM4J2tPlebzsRtuxHigi9o7DI_2xGqFudzlgoH55CP_BBNUDmGm6m-lTMkRx6X61dKfKDNo2NipZdM-a_cHf6Z0aVAU4LdJhV4xWOOm8Pb8sYIc2Nf6kUJRiidEGrxonUCfXX1XlnjMAo75wu99pN8G0mc7JhOehUqbwuXwKo4sQ694ae4F_AYl70sepX24v-0k0ga9esXR4i9rKaoHbzhQFtt2hangQkxHajq9ZTrXWMhd4msTzjHCKdEPXQFsTbKrgKtDQ\" }, \"requesttime\": \"2018-12-10T06:12:52.994Z\", \"version\": \"string\" }";
	private static final String VALIDATEWITHTIMESTAMP = "{ \"id\": \"string\", \"metadata\": {}, \"request\": { \"signature\": \"DrgkF2vm4WvBe04UNe-RePRcrg77uQpsH3GENRcglBsid-K0UDReeeZVKwimOdwV7Ht1j-_D1BFf2sCrM8ni7ztE5Xc_3TEaniOAnOgZDRSI0GG-uSqjH51AwTSl1PYdStfXtOn6HEfEU68JG7TdAliDI5C7thJ1YNmPnHusIsZzX6sW_VfvSpLeA_RzCqnUDH_VaEzZt_5zRYiQv9van4wt0P7HTfIBlQ5zaeO3wXOc3Pogct3ssKwqdaMmZdc7QTDOFqDZZVceMTIXKyiH-ZVs_u3QXRysiLVdXoz7d7yXHdWxQtzsfMjY7alMJNgbmu4X26LYNRemn65Mmn6ixA\", \"data\": \"test\", \"timestamp\": \"2019-05-20T07:28:04.269Z\" }, \"requesttime\": \"2018-12-10T06:12:52.994Z\", \"version\": \"string\" }";

	StringBuilder builder;
	SignatureResponse signResponse;

	@Before
	public void setup() {
		signResponse = new SignatureResponse();
		signResponse.setData("asdasdsadf4e");
		signResponse.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
	}

	@Test
	@WithUserDetails("reg-processor")
	public void signResponseSuccess() throws Exception {
		when(signatureUtil.sign(Mockito.anyString(), Mockito.anyString())).thenReturn(signResponse);
		mockMvc.perform(post("/sign").contentType(MediaType.APPLICATION_JSON).content(SIGNRESPONSEREQUEST))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("reg-processor")
	public void signResponsePublicKeyValidation() throws Exception {
		when(signatureUtil.validateWithPublicKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(true);
		mockMvc.perform(post("/public/validate").contentType(MediaType.APPLICATION_JSON).content(VALIDATEWITHPUBLICKEY))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("reg-processor")
	public void signResponseTimeStampValidationInvalid() throws Exception {
		when(signatureUtil.validate(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(false);
		mockMvc.perform(post("/validate").contentType(MediaType.APPLICATION_JSON).content(VALIDATEWITHTIMESTAMP))
				.andExpect(status().isOk());
	}

}
