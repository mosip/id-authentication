package io.mosip.authentication.common.service.factory;

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

import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.dto.AuditRequestDto;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.kernel.core.http.RequestWrapper;

/**
 * The Class AuditRequestFactoryTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuditRequestFactoryTest {
	
	/** The audit factory. */
	@InjectMocks
	AuditRequestFactory auditFactory;

	/** The env. */
	@Autowired
	Environment env;
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
	}
	
	/**
	 * Test build request.
	 */
	@Test
	public void testBuildRequest() {
		RequestWrapper<AuditRequestDto> actualRequest = auditFactory.buildRequest(AuditModules.FINGERPRINT_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");
		actualRequest.setRequesttime(null);
		actualRequest.getRequest().setActionTimeStamp(null);

		AuditRequestDto expectedRequest = new AuditRequestDto();
		RequestWrapper<AuditRequestDto> expected = new RequestWrapper<>();
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();

			expectedRequest.setEventId(AuditEvents.AUTH_REQUEST_RESPONSE.getEventId()); 
			expectedRequest.setEventName(AuditEvents.AUTH_REQUEST_RESPONSE.getEventName()); 
			expectedRequest.setEventType(AuditEvents.AUTH_REQUEST_RESPONSE.getEventType()); 
			expectedRequest.setActionTimeStamp(null);
			expectedRequest.setHostName(inetAddress.getHostName());
			expectedRequest.setHostIp(inetAddress.getHostAddress());
			expectedRequest.setApplicationId(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID)); 
			expectedRequest.setApplicationName(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_NAME)); 
			expectedRequest.setSessionUserId("sessionUserId");
			expectedRequest.setSessionUserName("sessionUserName");
			expectedRequest.setId("id");
			expectedRequest.setIdType(IdType.UIN.name());
			expectedRequest.setCreatedBy(env.getProperty(IdAuthConfigKeyConstants.USER_NAME)); 
			expectedRequest.setModuleName(AuditModules.FINGERPRINT_AUTH.getModuleName());
			expectedRequest.setModuleId(AuditModules.FINGERPRINT_AUTH.getModuleId());
			expectedRequest.setDescription("desc");
			expected = RestRequestFactory.createRequest(expectedRequest);
			expected.setRequesttime(null);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		assertEquals(expected, actualRequest);
	}
	
}
