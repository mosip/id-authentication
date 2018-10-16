package io.mosip.kernel.otpmanager.test.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.otpmanager.OtpmanagerBootApplication;
import io.mosip.kernel.otpmanager.exceptionhandler.MosipOtpInvalidArgumentExceptionHandler;
import io.mosip.kernel.otpmanager.service.impl.OtpGeneratorServiceImpl;
import io.mosip.kernel.otpmanager.service.impl.OtpValidatorServiceImpl;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = OtpmanagerBootApplication.class)
@TestPropertySource("classpath:/test.application.properties")
public class ExceptionTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OtpGeneratorServiceImpl service;

	@MockBean
	private OtpValidatorServiceImpl validatorService;

	@Test
	public void testForExceptionWhenKeyIsNull() throws Exception {
		when(service.getOtp(Mockito.any())).thenThrow(MosipOtpInvalidArgumentExceptionHandler.class);
		String json = "{\"key\":null}";
		mockMvc.perform(post("/otpmanager/otps").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$.errors[0].errorCode", is("KER-OTG-001")));
	}

	@Test
	public void testForExceptionWhenKeyLengthInvalid() throws Exception {
		when(validatorService.validateOtp(Mockito.any(), Mockito.any()))
				.thenThrow(MosipOtpInvalidArgumentExceptionHandler.class);
		mockMvc.perform(get("/otpmanager/otps?key=sa&otp=3212").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable());
	}
}
