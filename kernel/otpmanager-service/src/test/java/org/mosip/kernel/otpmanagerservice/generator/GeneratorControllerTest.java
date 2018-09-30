package org.mosip.kernel.otpmanagerservice.generator;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mosip.kernel.otpmanagerservice.OtpmanagerServiceApplication;
import org.mosip.kernel.otpmanagerservice.dto.OtpGeneratorResponseDto;
import org.mosip.kernel.otpmanagerservice.service.impl.OtpGeneratorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = OtpmanagerServiceApplication.class)
@TestPropertySource("classpath:/test.application.properties")
public class GeneratorControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OtpGeneratorServiceImpl service;

	@Test
	public void generateOtpTest() throws Exception {
		String otp = "3214";
		OtpGeneratorResponseDto dto = new OtpGeneratorResponseDto();
		dto.setOtp(otp);
		given(service.getOtp(Mockito.any())).willReturn(dto);
		String json = "{\"key\":\"123456789\"}";
		mockMvc.perform(post("/otpmanager/otps").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.otp", is("3214")));
	}
}
