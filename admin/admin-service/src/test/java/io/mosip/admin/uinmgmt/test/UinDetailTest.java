package io.mosip.admin.uinmgmt.test;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.TestBootApplication;
import io.mosip.admin.uinmgmt.dto.UinDetailResponseDto;
import io.mosip.kernel.core.http.ResponseWrapper;

@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UinDetailTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RestTemplate restTemplate;

	private MockRestServiceServer restServer;

	@Autowired
	private ObjectMapper mapper;

	@Value("${mosip.admin.uinmgmt.uin-detail-search}")
	private String url;

	@Before
	public void setup() {
		restServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getUinDetails() throws Exception {

		String uin = "5731671547";
		UinDetailResponseDto uinDetailResponseDto = new UinDetailResponseDto();
		Map<String, String> map = new HashMap<>();
		map.put("status", "ACTIVE");
		uinDetailResponseDto.setResponse(map);

		ResponseWrapper<UinDetailResponseDto> response = new ResponseWrapper<>();
		response.setId("mosip.id.read");
		response.setVersion("v1");
		response.setResponse(uinDetailResponseDto);

		URI uri = UriComponentsBuilder.fromUriString(url).build(uin);
		// mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		restServer.expect(requestTo(uri))
				.andRespond(withSuccess(mapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
		mockMvc.perform(get("/uin/detail/{uin}", uin)).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getUinDetailsExp() throws Exception {

		String uin = "5731671547";
		UinDetailResponseDto uinDetailResponseDto = new UinDetailResponseDto();
		Map<String, String> map = new HashMap<>();
		map.put("status", "ACTIVE");
		uinDetailResponseDto.setResponse(map);

		ResponseWrapper<UinDetailResponseDto> response = new ResponseWrapper<>();
		response.setId("mosip.id.read");
		response.setVersion("v1");
		response.setResponse(uinDetailResponseDto);

		URI uri = UriComponentsBuilder.fromUriString(url).build(uin);
		// mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		restServer.expect(requestTo(uri)).andRespond(withSuccess().body(mapper.writeValueAsString(response)));
		mockMvc.perform(get("/uin/detail/{uin}", uin)).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getUinDetailsExpNull() throws Exception {

		String uin = "5731671547";
		UinDetailResponseDto uinDetailResponseDto = new UinDetailResponseDto();
		Map<String, String> map = new HashMap<>();
		map.put("status", "ACTIVE");
		uinDetailResponseDto.setResponse(map);

		ResponseWrapper<UinDetailResponseDto> response = new ResponseWrapper<>();
		response.setId("mosip.id.read");
		response.setVersion("v1");
		response.setResponse(uinDetailResponseDto);

		URI uri = UriComponentsBuilder.fromUriString(url).build(uin);
		// mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		restServer.expect(requestTo(uri))
				.andRespond(withSuccess(mapper.writeValueAsString(null), MediaType.APPLICATION_JSON));
		mockMvc.perform(get("/uin/detail/{uin}", uin)).andExpect(status().isOk());
	}

}
