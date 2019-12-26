package io.mosip.admin.packetstatusupdater;


import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.TestBootApplication;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;

@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG, printOnlyOnFailure = false)
public class PacketStatusIntegrationTest {

	@Autowired
	private RestTemplate restTemplate;
	
	
	private MockRestServiceServer mockRestServiceServer;
	
	/** The packet update status url. */
	@Value("${mosip.kernel.packet-status-update-url}")
	private String packetUpdateStatusUrl;

	/** The zone validation url. */
	@Value("${mosip.kernel.zone-validation-url}")
	private String zoneValidationUrl;

	@Value("${mosip.primary-language:eng}")
	private String primaryLang;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private List<ServiceError> validationErrorList=null;
	
	@Autowired
	private MockMvc mockMvc;
	
	private String POSITIVE_RESPONSE_REG_PROC="{\r\n  \"id\": \"mosip.registration.transaction\",\r\n  \"version\": \"1.0\",\r\n  \"responsetime\": \"2019-12-11T09:45:45.544Z\",\r\n  \"response\": [\r\n    {\r\n      \"id\": \"60c5f55d-8f22-48d0-8b55-edcd724417bc\",\r\n      \"registrationId\": \"10002100320002420191210085947\",\r\n      \"transactionTypeCode\": \"PACKET_RECEIVER\",\r\n      \"parentTransactionId\": null,\r\n      \"statusCode\": \"SUCCESS\",\r\n      \"statusComment\": \"Packet has reached Packet Receiver\",\r\n      \"createdDateTimes\": \"2019-12-10T09:05:06.709\"\r\n    },\r\n    {\r\n      \"id\": \"7aa593d7-8f1e-413a-abca-34b752caa795\",\r\n      \"registrationId\": \"10002100320002420191210085947\",\r\n      \"transactionTypeCode\": \"PACKET_RECEIVER\",\r\n      \"parentTransactionId\": \"60c5f55d-8f22-48d0-8b55-edcd724417bc\",\r\n      \"statusCode\": \"SUCCESS\",\r\n      \"statusComment\": \"Packet is Uploaded to Landing Zone\",\r\n      \"createdDateTimes\": \"2019-12-10T09:05:08.038\"\r\n    }\r\n  ],\r\n  \"errors\": null\r\n}";
	
	private String PARSE_ERROR_RESPONSE_REG_PROC="{\r\n    \"id\": \"mosip.registration.transaction\",\r\n    \"version\": \"1.0\",\r\n    \"responsetime\": \"2019-12-03T05:21:57.024Z\",\r\n    \"response\": [\r\n        {\r\n            \"id\": \"96939b0b-982f-431c-abe0-489cf4ca9734\",\r\n            \"registrationId\": \"10001100010000120190722123617\",\r\n            \"transactionTypeCode\": \"DEMOGRAPHIC_VERIFICATION\",\r\n            \"parentTransactionId\": \"85ed38a6-3fcc-42e7-9afd-366d424c93f7\",\r\n            \"statusCode\": \"IN_PROGRESS\",\r\n            \"statusComment\": null,\r\n            \"createdDateTimes\": \"2019-08-07T15:58:17.204\"\r\n        }\r\n\t],\t\r\n\t\t\t\r\n\t\t\t\"errors\":null\r\n\t\t\t";
	
	private String POSITIVE_RESPONSE_ZONE_VALIATION="{\r\n    \"id\": null,\r\n    \"version\": null,\r\n    \"responsetime\": \"2019-12-02T09:45:24.512Z\",\r\n    \"metadata\": null,\r\n    \"response\": true,\r\n    \"errors\": []\r\n}";
	@Before
	public void setUp() {
		mockRestServiceServer=MockRestServiceServer.bindTo(restTemplate).build();
		ServiceError serviceError= new ServiceError();
		serviceError.setErrorCode("KER-MSD-403");
		serviceError.setMessage("Forbidden");
		validationErrorList= new ArrayList<ServiceError>();
		validationErrorList.add(serviceError);
		
		
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testPacketStatusUpdate() throws Exception {
		UriComponentsBuilder uribuilder = UriComponentsBuilder.fromUriString(zoneValidationUrl).queryParam("rid",
				"1000012232223243224234");
		mockRestServiceServer.expect(requestTo(uribuilder.toUriString())).andRespond(withSuccess().body(POSITIVE_RESPONSE_ZONE_VALIATION));
		mockRestServiceServer.expect(requestTo(packetUpdateStatusUrl.toString() + "/"+primaryLang+"/1000012232223243224234"))
		.andRespond(withSuccess().body(POSITIVE_RESPONSE_REG_PROC));
		
		mockMvc.perform(
				get("/packetstatusupdate").param("rid","1000012232223243224234")).andExpect(status().isOk());
		
		
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testPacketStatusUpdate401Excption() throws Exception {
		UriComponentsBuilder uribuilder = UriComponentsBuilder.fromUriString(zoneValidationUrl).queryParam("rid",
				"1000012232223243224234");
		mockRestServiceServer.expect(requestTo(uribuilder.toUriString())).andRespond(withUnauthorizedRequest());
		
		
		mockMvc.perform(
				get("/packetstatusupdate").param("rid","1000012232223243224234")).andExpect(status().is5xxServerError());
		
		
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testPacketStatusUpdate403Excption() throws Exception {
		UriComponentsBuilder uribuilder = UriComponentsBuilder.fromUriString(zoneValidationUrl).queryParam("rid",
				"1000012232223243224234");
		mockRestServiceServer.expect(requestTo(uribuilder.toUriString())).andRespond(withStatus(HttpStatus.FORBIDDEN));
		
		
		mockMvc.perform(
				get("/packetstatusupdate").param("rid","1000012232223243224234")).andExpect(status().is5xxServerError());
		
		
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testPacketStatusUpdate401ExcptionValidationError() throws Exception {
		UriComponentsBuilder uribuilder = UriComponentsBuilder.fromUriString(zoneValidationUrl).queryParam("rid",
				"1000012232223243224234");
		ResponseWrapper<?> validatationResponse= new ResponseWrapper<>();
		validatationResponse.setErrors(validationErrorList);
		mockRestServiceServer.expect(requestTo(uribuilder.toUriString())).andRespond(withUnauthorizedRequest().body(objectMapper.writeValueAsString(validatationResponse)));
		
		
		mockMvc.perform(
				get("/packetstatusupdate").param("rid","1000012232223243224234")).andExpect(status().is5xxServerError());
		
		
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testPacketStatusUpdate403ExcptionValidateionError() throws Exception {
		UriComponentsBuilder uribuilder = UriComponentsBuilder.fromUriString(zoneValidationUrl).queryParam("rid",
				"1000012232223243224234");
		mockRestServiceServer.expect(requestTo(uribuilder.toUriString())).andRespond(withSuccess().body(POSITIVE_RESPONSE_ZONE_VALIATION));
		ResponseWrapper<?> validatationResponse= new ResponseWrapper<>();
		validatationResponse.setErrors(validationErrorList);
		mockRestServiceServer.expect(requestTo(packetUpdateStatusUrl.toString() + "/"+primaryLang+"/1000012232223243224234"))
		.andRespond(withStatus(HttpStatus.FORBIDDEN).body(objectMapper.writeValueAsString(validatationResponse)));
		
		
		mockMvc.perform(
				get("/packetstatusupdate").param("rid","1000012232223243224234")).andExpect(status().is5xxServerError());
		
		
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testPacketStatusUpdate500ExcptionValidateionError() throws Exception {
		UriComponentsBuilder uribuilder = UriComponentsBuilder.fromUriString(zoneValidationUrl).queryParam("rid",
				"1000012232223243224234");
		mockRestServiceServer.expect(requestTo(uribuilder.toUriString())).andRespond(withSuccess().body(POSITIVE_RESPONSE_ZONE_VALIATION));
		ResponseWrapper<?> validatationResponse= new ResponseWrapper<>();
		validatationResponse.setErrors(validationErrorList);
		mockRestServiceServer.expect(requestTo(packetUpdateStatusUrl.toString() + "/"+primaryLang+"/1000012232223243224234"))
		.andRespond(withBadRequest());
		
		
		mockMvc.perform(
				get("/packetstatusupdate").param("rid","1000012232223243224234")).andExpect(status().is5xxServerError());
		
		
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testPacketStatusUpdateValidationError() throws Exception {
		UriComponentsBuilder uribuilder = UriComponentsBuilder.fromUriString(zoneValidationUrl).queryParam("rid",
				"1000012232223243224234");
		ResponseWrapper<?> validatationResponse= new ResponseWrapper<>();
		validatationResponse.setErrors(validationErrorList);
		mockRestServiceServer.expect(requestTo(uribuilder.toUriString())).andRespond(withSuccess().body(POSITIVE_RESPONSE_ZONE_VALIATION));
		mockRestServiceServer.expect(requestTo(packetUpdateStatusUrl.toString() + "/"+primaryLang+"/1000012232223243224234"))
		.andRespond(withSuccess().body(objectMapper.writeValueAsString(validatationResponse)));
		
		mockMvc.perform(
				get("/packetstatusupdate").param("rid","1000012232223243224234")).andExpect(status().isOk());
		
		
	}
	
	
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testPacketStatusUpdateParseException() throws Exception {
		UriComponentsBuilder uribuilder = UriComponentsBuilder.fromUriString(zoneValidationUrl).queryParam("rid",
				"1000012232223243224234");
		ResponseWrapper<?> validatationResponse= new ResponseWrapper<>();
		validatationResponse.setErrors(validationErrorList);
		mockRestServiceServer.expect(requestTo(uribuilder.toUriString())).andRespond(withSuccess().body(PARSE_ERROR_RESPONSE_REG_PROC));
		mockRestServiceServer.expect(requestTo(packetUpdateStatusUrl.toString() + "/"+primaryLang+"/1000012232223243224234"))
		.andRespond(withSuccess().body(objectMapper.writeValueAsString(validatationResponse)));
		
		mockMvc.perform(
				get("/packetstatusupdate").param("rid","1000012232223243224234")).andExpect(status().isInternalServerError());
		
		
	}
	
	
	
	
}
