package org.mosip.auth.service.factory;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mosip.auth.core.util.dto.AuditRequestDto;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestPropertySource(value= {"classpath:audit.properties","classpath:rest-services.properties", "classpath:log.properties"})
public class AuditRequestFactoryTest {
	
    @Autowired
    MockMvc mockMvc;

	@InjectMocks
	AuditRequestFactory auditFactory;

	@Autowired
	Environment env;
	
	@Before
	public void before() {
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(env.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(env.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(env.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(env.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(env.getProperty("log4j.appender.Appender.totalCap"));
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.invokeMethod(auditFactory, "initializeLogger", mosipRollingFileAppender);
		
	}
	
	@Test
	public void testBuildRequest() {
		AuditRequestDto actualRequest = auditFactory.buildRequest("IDA", "desc");
		actualRequest.setActionTimeStamp(null);

		AuditRequestDto expectedRequest = new AuditRequestDto();
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();

			expectedRequest.setEventId("eventId"); //
			expectedRequest.setEventName("eventName"); //
			expectedRequest.setEventType("eventType"); //
			expectedRequest.setActionTimeStamp(null);
			expectedRequest.setHostName(inetAddress.getHostName());
			expectedRequest.setHostIp(inetAddress.getHostAddress());
			expectedRequest.setApplicationId(env.getProperty("application.id")); // config application.id
			expectedRequest.setApplicationName(env.getProperty("application.name")); // config application.name
			expectedRequest.setSessionUserId("sessionUserId");
			expectedRequest.setSessionUserName("sessionUserName");
			expectedRequest.setId("id");
			expectedRequest.setIdType("idType");
			expectedRequest.setCreatedBy("createdBy"); // system
			expectedRequest.setModuleName("moduleName"); // get from constant
			expectedRequest.setModuleId("IDA"); // parameter
			expectedRequest.setDescription("desc"); // parameter

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		assertEquals(expectedRequest, actualRequest);
	}
	
}
