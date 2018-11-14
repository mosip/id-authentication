package io.mosip.authentication.service.integration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OTPManagerTest.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class })
public class NotificationManagerTest {

	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	AuditRequestFactory auditFactory;

	@Autowired
	Environment environment;

	@InjectMocks
	private RestHelper restHelper;

	@InjectMocks
	private NotificationManager notificationManager;

	@InjectMocks
	private IdTemplateManager idTemplateManager;

	@Before
	public void before() {
		ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		ReflectionTestUtils.setField(auditFactory, "env", environment);
		ReflectionTestUtils.setField(notificationManager, "environment", environment);
		ReflectionTestUtils.setField(notificationManager, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testInValidSendNotification() throws IdAuthenticationBusinessException {
		Set<NotificationType> notificationtype = new HashSet<>();
		notificationtype.add(NotificationType.EMAIL);
		Map<String, Object> values = new HashMap<>();
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("mosip.auth.mail.subject.template", "test");
		mockenv.setProperty("mosip.auth.mail.content.template", "test");
		ReflectionTestUtils.setField(notificationManager, "environment", mockenv);
		String type = SenderType.AUTH.getName();
		notificationManager.sendNotification(notificationtype, values, null, null, type);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testValidSendNotification() throws IdAuthenticationBusinessException, ParseException {
		Set<NotificationType> notificationtype = new HashSet<>();
		NotificationType emailNotification = NotificationType.EMAIL;
		notificationtype.add(emailNotification);
		Map<String, Object> values = new HashMap<>();
		values.put("NAME", "dinesh");
		ZoneOffset offset = ZoneOffset.MAX;
		SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String date = Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(environment.getProperty("datetime.pattern"))).toString();
		Date datetime = formatter.parse(date);
		values.put("DATE", date);
		values.put("TIME", time.format(datetime));
		values.put("UIN", "1234567890");
		values.put("AUTHTYPE", "AUTH");
		values.put("STATUS", "Success");
		String emailId = "dineshkaruppiah.thiagarajan@mindtree.com";
		String phoneNumber = "";
		String sender = SenderType.AUTH.getName();
		notificationManager.sendNotification(notificationtype, values, emailId, phoneNumber, sender);
	}

}
