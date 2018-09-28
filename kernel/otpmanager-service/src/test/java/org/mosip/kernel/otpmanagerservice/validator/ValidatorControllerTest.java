package org.mosip.kernel.otpmanagerservice.validator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.kernel.otpmanagerservice.OtpmanagerServiceApplication;
import org.mosip.kernel.otpmanagerservice.service.impl.OtpValidatorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = OtpmanagerServiceApplication.class)
public class ValidatorControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OtpValidatorServiceImpl service;

	@Test
	public void validateOtpTest() throws Exception {
		mockMvc.perform(get("/otpmanager/otps?key=sagarmahapatra&otp=3212").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable());
	}
}
