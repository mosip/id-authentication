package io.mosip.kernel.emailnotification.test.exception;

import static org.hamcrest.CoreMatchers.isA;
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

import io.mosip.kernel.emailnotification.NotificationEmailBootApplication;
import io.mosip.kernel.emailnotification.service.impl.EmailNotificationServiceImpl;
import io.mosip.kernel.emailnotification.util.EmailNotificationUtils;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = NotificationEmailBootApplication.class)
public class MailnotificationExceptionTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	EmailNotificationServiceImpl service;

	@MockBean
	EmailNotificationUtils utils;

	@Test
	public void testToRaiseExceptionForNullContent() throws Exception {
		mockMvc.perform(post("/v1.0/email/send").contentType(MediaType.MULTIPART_FORM_DATA)
				.param("mailTo", "testmail@gmail.com").param("mailSubject", "testsubject"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	@Test
	public void testToRaiseExceptionForNullSubject() throws Exception {
		mockMvc.perform(post("/v1.0/email/send").contentType(MediaType.MULTIPART_FORM_DATA)
				.param("mailTo", "testmail@gmail.com").param("mailContent", "testsubject"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	@Test
	public void testToRaiseExceptionForNullTo() throws Exception {
		mockMvc.perform(post("/v1.0/email/send").contentType(MediaType.MULTIPART_FORM_DATA)
				.param("mailSubject", "testsubject").param("mailContent", "testsubject"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	@Test
	public void testToRaiseExceptionForEmptySubject() throws Exception {
		mockMvc.perform(post("/v1.0/email/send").contentType(MediaType.MULTIPART_FORM_DATA).param("mailTo", "values")
				.param("mailSubject", "   ").param("mailContent", "testsubject")).andExpect(status().isOk())
				.andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	@Test
	public void testToRaiseExceptionForEmptyContent() throws Exception {
		mockMvc.perform(post("/v1.0/email/send").contentType(MediaType.MULTIPART_FORM_DATA).param("mailTo", "values")
				.param("mailSubject", "test subject").param("mailContent", "  ")).andExpect(status().isOk())
				.andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	@Test
	public void testToRaiseExceptionForEmptyWithMultipleTo() throws Exception {
		mockMvc.perform(post("/v1.0/email/send").contentType(MediaType.MULTIPART_FORM_DATA)
				.param("mailTo", "test@gmail.com,,testmail@gmail.com").param("mailSubject", "test subject")
				.param("mailContent", "  ")).andExpect(status().isOk())
				.andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	@Test
	public void testToRaiseExceptionForEmptyWithMultipleToAndMultipleEmpty() throws Exception {
		mockMvc.perform(post("/v1.0/email/send").contentType(MediaType.MULTIPART_FORM_DATA)
				.param("mailTo", "test@gmail.com,,testmail@gmail.com,,testcheck@gmail.com")
				.param("mailSubject", "test subject").param("mailContent", "  ")).andExpect(status().isOk())
				.andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	@Test
	public void testToRaiseExceptionForEmptyTo() throws Exception {
		mockMvc.perform(post("/v1.0/email/send").contentType(MediaType.MULTIPART_FORM_DATA).param("mailTo", "")
				.param("mailSubject", "test subject").param("mailContent", "  ")).andExpect(status().isOk())
				.andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}
}