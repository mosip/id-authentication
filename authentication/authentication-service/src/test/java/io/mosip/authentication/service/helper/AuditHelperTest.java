package io.mosip.authentication.service.helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource(value = { "classpath:audit.properties", "classpath:rest-services.properties", "classpath:log.properties" })
public class AuditHelperTest {
	
	@Mock
	RestHelper restHelper;
	
	@InjectMocks
	AuditHelper auditHelper;
	
    @Autowired
    MockMvc mockMvc;
	
    @Mock
	AuditRequestFactory auditFactory;
	
    @Mock
	RestRequestFactory restFactory;
    
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
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(auditFactory, "initializeLogger", mosipRollingFileAppender);
	}
	
	@Test
	public void testAuditUtil() throws IDDataValidationException {
		System.err.println(auditFactory);
		auditHelper.audit("moduleId", "description");
	}

}
