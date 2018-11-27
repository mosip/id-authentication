package io.mosip.kernel.auditmanager.test;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.auditmanager.service.impl.AuditManagerServiceImpl;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AuditExceptionTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuditManagerServiceImpl service;

	@Test
	public void auditInvalidRequestExceptionTest() throws Exception {
		String json = "{\r\n" + 
				"  \"eventName\": \"string\",\r\n" + 
				"  \"eventType\": \"string\",\r\n" + 
				"  \"actionTimeStamp\": \"2018-09-10T11:39:28.191Z\",\r\n" + 
				"  \"hostName\": \"string\",\r\n" + 
				"  \"hostIp\": \"string\",\r\n" + 
				"  \"applicationId\": \"string\",\r\n" + 
				"  \"applicationName\": \"string\",\r\n" + 
				"  \"sessionUserId\": \"string\",\r\n" + 
				"  \"sessionUserName\": \"string\",\r\n" + 
				"  \"id\": \"string\",\r\n" + 
				"  \"idType\": \"string\",\r\n" + 
				"  \"createdBy\": \"string\",\r\n" + 
				"  \"moduleName\": \"string\",\r\n" + 
				"  \"moduleId\": \"string\",\r\n" + 
				"  \"description\": \"string\"\r\n" + 
				"}";
		mockMvc.perform(post("/auditmanager/audits").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].errorCode", is("KER-AUD-001")));
	}
	
	@Test
	public void auditInvalidFormatExceptionTest() throws Exception {

		String json = "{\r\n" + 
				"  \"eventId\": \"string\",\r\n" + 
				"  \"eventName\": \"string\",\r\n" + 
				"  \"eventType\": \"string\",\r\n" + 
				"  \"actionTimeStamp\": \"2018-09-10T\",\r\n" + 
				"  \"hostName\": \"string\",\r\n" + 
				"  \"hostIp\": \"string\",\r\n" + 
				"  \"applicationId\": \"string\",\r\n" + 
				"  \"applicationName\": \"string\",\r\n" + 
				"  \"sessionUserId\": \"string\",\r\n" + 
				"  \"sessionUserName\": \"string\",\r\n" + 
				"  \"id\": \"string\",\r\n" + 
				"  \"idType\": \"string\",\r\n" + 
				"  \"createdBy\": \"string\",\r\n" + 
				"  \"moduleName\": \"string\",\r\n" + 
				"  \"moduleId\": \"string\",\r\n" + 
				"  \"description\": \"string\"\r\n" + 
				"}";
		mockMvc.perform(post("/auditmanager/audits").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].errorCode", is("KER-AUD-002")));
	}
	
	@Test
	public void auditConstraintExceptionTest() throws Exception {

		String json = "{\r\n" + 
				"  \"eventId\": \"\",\r\n" + 
				"  \"eventName\": \"string\",\r\n" + 
				"  \"eventType\": \"string\",\r\n" + 
				"  \"actionTimeStamp\": \"2018-09-10T11:39:28.191Z\",\r\n" + 
				"  \"hostName\": \"string\",\r\n" + 
				"  \"hostIp\": \"string\",\r\n" + 
				"  \"applicationId\": \"string\",\r\n" + 
				"  \"applicationName\": \"string\",\r\n" + 
				"  \"sessionUserId\": \"string\",\r\n" + 
				"  \"sessionUserName\": \"string\",\r\n" + 
				"  \"id\": \"string\",\r\n" + 
				"  \"idType\": \"string\",\r\n" + 
				"  \"createdBy\": \"string\",\r\n" + 
				"  \"moduleName\": \"string\",\r\n" + 
				"  \"moduleId\": \"string\",\r\n" + 
				"  \"description\": \"string\"\r\n" + 
				"}";
		mockMvc.perform(post("/auditmanager/audits").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].errorCode", is("KER-AUD-001")));
	}
	
	
}
