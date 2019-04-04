package io.mosip.kernel.applicanttype.test.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.applicanttype.dto.KeyValues;
import io.mosip.kernel.applicanttype.dto.request.RequestDTO;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.util.DateUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ApplicantTypeIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper;

	@Before
	public void setUp() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	}

	@WithUserDetails("individual")
	@Test
	public void getApplicantType() throws Exception {
		RequestWrapper<RequestDTO> request = new RequestWrapper<>();
		RequestDTO dto = new RequestDTO();
		request.setId("applicanttype.getapplicanttype");
		request.setVersion("V1.0");
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		List<KeyValues<String, Object>> list = new LinkedList<>();

		KeyValues<String, Object> k1 = new KeyValues<>();
		k1.setAttribute("individualTypeCode");
		k1.setValue("NFR");
		list.add(k1);
		KeyValues<String, Object> k2 = new KeyValues<>();
		k2.setAttribute("genderCode");
		k2.setValue("MLE");
		list.add(k2);
		KeyValues<String, Object> k3 = new KeyValues<>();
		k3.setAttribute("dateofbirth");
		k3.setValue("1933-01-27T05:00:00.000Z");
		list.add(k3);
		KeyValues<String, Object> k4 = new KeyValues<>();
		k4.setAttribute("biometricAvailable");
		k4.setValue(false);
		list.add(k4);
		dto.setAttributes(list);

		request.setRequest(dto);
		mockMvc.perform(post("/getApplicantType").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request))).andExpect(status().isOk());

	}

	@WithUserDetails("individual")
	@Test
	public void getApplicantTypeDataNotFoundException() throws Exception {
		RequestWrapper<RequestDTO> request = new RequestWrapper<>();
		RequestDTO dto = new RequestDTO();
		request.setId("applicanttype.getapplicanttype");
		request.setVersion("V1.0");
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		List<KeyValues<String, Object>> list = new LinkedList<>();

		KeyValues<String, Object> k1 = new KeyValues<>();
		k1.setAttribute("individualTypeCode");
		k1.setValue("XYZ");
		list.add(k1);
		KeyValues<String, Object> k2 = new KeyValues<>();
		k2.setAttribute("genderCode");
		k2.setValue("MLE");
		list.add(k2);
		KeyValues<String, Object> k3 = new KeyValues<>();
		k3.setAttribute("dateofbirth");
		k3.setValue("1933-01-27T05:00:00.000Z");
		list.add(k3);
		KeyValues<String, Object> k4 = new KeyValues<>();
		k4.setAttribute("biometricAvailable");
		k4.setValue(false);
		list.add(k4);
		dto.setAttributes(list);

		request.setRequest(dto);
		mockMvc.perform(post("/getApplicantType").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request))).andExpect(status().isOk());

	}

	@WithUserDetails("individual")
	@Test
	public void getApplicantTypeInvalidApplicantArgumentException() throws Exception {
		RequestWrapper<RequestDTO> request = new RequestWrapper<>();
		RequestDTO dto = new RequestDTO();
		request.setId("applicanttype.getapplicanttype");
		request.setVersion("V1.0");
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		List<KeyValues<String, Object>> list = new LinkedList<>();

		KeyValues<String, Object> k1 = new KeyValues<>();
		k1.setAttribute("individualTypeCode");
		k1.setValue("NFR");
		list.add(k1);
		KeyValues<String, Object> k2 = new KeyValues<>();
		k2.setAttribute("genderCode");
		k2.setValue("MLE");
		list.add(k2);
		KeyValues<String, Object> k3 = new KeyValues<>();
		k3.setAttribute("dateofbirth");
		k3.setValue("1933-01-27T05:000.000Z");
		list.add(k3);
		KeyValues<String, Object> k4 = new KeyValues<>();
		k4.setAttribute("biometricAvailable");
		k4.setValue(false);
		list.add(k4);
		dto.setAttributes(list);

		request.setRequest(dto);
		mockMvc.perform(post("/getApplicantType").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request))).andExpect(status().isOk());

	}

}