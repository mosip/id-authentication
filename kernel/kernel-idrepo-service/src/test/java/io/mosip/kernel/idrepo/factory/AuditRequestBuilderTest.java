package io.mosip.kernel.idrepo.factory;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.idrepo.constant.AuditEvents;
import io.mosip.kernel.core.idrepo.constant.AuditModules;
import io.mosip.kernel.idrepo.builder.AuditRequestBuilder;
import io.mosip.kernel.idrepo.dto.AuditRequestDto;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuditRequestBuilderTest {

	@InjectMocks
	AuditRequestBuilder auditBuilder;

	@Autowired
	Environment env;

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditBuilder, "env", env);
	}

	@Test
	public void testBuildRequest() {
		RequestWrapper<AuditRequestDto> actualRequest = auditBuilder.buildRequest(AuditModules.CREATE_IDENTITY,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id", "desc");
		actualRequest.getRequest().setActionTimeStamp(null);
		AuditRequestDto expectedRequest = new AuditRequestDto();
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();

			expectedRequest.setEventId(AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE.getEventId());
			expectedRequest.setEventName(AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE.getEventName());
			expectedRequest.setEventType(AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE.getEventType());
			expectedRequest.setActionTimeStamp(null);
			expectedRequest.setHostName(inetAddress.getHostName());
			expectedRequest.setHostIp(inetAddress.getHostAddress());
			expectedRequest.setApplicationId(env.getProperty("mosip.kernel.idrepo.application.id"));
			expectedRequest.setApplicationName(env.getProperty("mosip.kernel.idrepo.application.name"));
			expectedRequest.setSessionUserId("sessionUserId");
			expectedRequest.setSessionUserName("sessionUserName");
			expectedRequest.setId("id");
			expectedRequest.setIdType("UIN");
			expectedRequest.setCreatedBy(env.getProperty("user.name"));
			expectedRequest.setModuleName(AuditModules.CREATE_IDENTITY.getModuleName());
			expectedRequest.setModuleId(AuditModules.CREATE_IDENTITY.getModuleId());
			expectedRequest.setDescription("desc");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		assertEquals(expectedRequest, actualRequest.getRequest());
	}

}
