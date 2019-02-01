package io.mosip.preregistration.notification.controller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.preregistration.notification.service.NotificationService;


@RunWith(SpringRunner.class)
@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

	/**
	 * Autowired reference for {@link #MockMvc}
	 */
	@Autowired
	private MockMvc mockMvc;
	
	/**
	 * /**
	 * Creating Mock Bean for NotificationService
	 */
	 @MockBean
	private NotificationService notificationService;
	 
	 @Autowired
	 private NotificationController notificationController;
	 
	 @Before
	 public void setUp() {
		 
		 String json="";
	 }
	
}
