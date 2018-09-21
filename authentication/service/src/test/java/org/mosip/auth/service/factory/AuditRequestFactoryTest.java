package org.mosip.auth.service.factory;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.auth.core.constant.AuditServicesConstants;
import org.mosip.auth.core.factory.AuditRequestFactory;
import org.mosip.auth.core.util.dto.AuditRequestDto;
import org.mosip.auth.service.IdAuthenticationApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IdAuthenticationApplication.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebAppConfiguration
public class AuditRequestFactoryTest {

	@Autowired
	AuditRequestFactory auditFactory;

	@Autowired
	Environment env;

	@Test
	public void testBuildRequest() {
		AuditRequestDto actualRequest = auditFactory.buildRequest(AuditServicesConstants.AUDIT_MANAGER_SERVICE);
		actualRequest.setActionTimeStamp(null);

		AuditRequestDto expectedRequest = new AuditRequestDto();
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();

			expectedRequest.setEventId(env.getProperty("audit.eventId"));
			expectedRequest.setEventName(env.getProperty("audit.eventName"));
			expectedRequest.setEventType(env.getProperty("audit.eventType"));
			expectedRequest.setActionTimeStamp(null);
			expectedRequest.setHostName(inetAddress.getHostName());
			expectedRequest.setHostIp(inetAddress.getHostAddress());
			expectedRequest.setApplicationId(env.getProperty("audit.applicationId"));
			expectedRequest.setApplicationName(env.getProperty("audit.applicationName"));
			expectedRequest.setSessionUserId("sessionUserId");
			expectedRequest.setSessionUserName("sessionUserName");
			expectedRequest.setId(env.getProperty("audit.id"));
			expectedRequest.setIdType(env.getProperty("audit.idType"));
			expectedRequest.setCreatedBy("createdBy");
			expectedRequest.setModuleName("moduleName");
			expectedRequest.setModuleId("moduleId");
			expectedRequest.setDescription("description");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		assertEquals(expectedRequest, actualRequest);
	}
	
}
