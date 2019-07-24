package io.mosip.admin.integration;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
import io.mosip.admin.uinmgmt.dto.UinGenerationStatusDto;
import io.mosip.admin.uinmgmt.service.UinGenerationStatusService;
import io.mosip.kernel.core.http.ResponseWrapper;

@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UinGenerationStatusTest {

	@Autowired
	UinGenerationStatusService uinStatusService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MockMvc mockMvc;

	private MockRestServiceServer restServer;

	@Value("${mosip.admin.packetstatus.api}")
	private String url;
	
	@Value("${mosip.admin.packetstatus.api.version}")
	private String version;
	
	@Value("${mosip.admin.packetstatus.api.statuscode}")
	private String statusCode;
	
	@Value("${mosip.admin.packetstatus.api.packetId}")
	private String packetId;


	@Before
	public void setup() {
		restServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Autowired
	private ObjectMapper mapper;

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetUinGenerationStatus() throws Exception {

		ResponseWrapper<List<UinGenerationStatusDto>> response = new ResponseWrapper<>();
		response.setId(packetId);
		response.setVersion(version);
		response.setResponsetime(LocalDateTime.now(ZoneId.of("UTC")));
		String rid = "01006768480002820190122190830";
		List<UinGenerationStatusDto> uingen = new ArrayList<>();
		UinGenerationStatusDto uinDetailResponseDto = new UinGenerationStatusDto();
		uinDetailResponseDto.setRegistrationId(rid);
		uinDetailResponseDto.setStatusCode(statusCode);
		uingen.add(uinDetailResponseDto);

		response.setResponse(uingen);

		URI uri = UriComponentsBuilder.fromUriString(url).build(rid);
		restServer.expect(requestTo(uri))
				.andRespond(withSuccess(mapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
		mockMvc.perform(get("/packetstatus/{rid}", rid)).andExpect(status().isOk());

	}

	@Test
	@WithUserDetails("reg-admin")
	public void testGetUinGenerationStatusException() throws Exception {

		ResponseWrapper<List<UinGenerationStatusDto>> response = new ResponseWrapper<>();
		response.setId(packetId);
		response.setVersion(version);
		response.setResponsetime(LocalDateTime.now(ZoneId.of("UTC")));
		String rid = "01006768480002820190122190830";
		List<UinGenerationStatusDto> uingen = new ArrayList<>();
		UinGenerationStatusDto uinDetailResponseDto = new UinGenerationStatusDto();
		uinDetailResponseDto.setRegistrationId(rid);
		uinDetailResponseDto.setStatusCode(statusCode);
		uingen.add(uinDetailResponseDto);

		response.setResponse(uingen);

		URI uri = UriComponentsBuilder.fromUriString(url).build(rid);
		restServer.expect(requestTo(uri)).andRespond(withServerError());
		mockMvc.perform(get("/packetstatus/{rid}", rid)).andExpect(status().isInternalServerError());
	}

}
