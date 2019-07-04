package io.mosip.idrepository.core.test.builder;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.idrepository.core.builder.AuditRequestBuilder;
import io.mosip.idrepository.core.constant.AuditEvents;
import io.mosip.idrepository.core.constant.AuditModules;
import io.mosip.idrepository.core.constant.IdType;
import io.mosip.idrepository.core.dto.AuditRequestDTO;
import io.mosip.kernel.core.http.RequestWrapper;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
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
		RequestWrapper<AuditRequestDTO> actualRequest = auditBuilder.buildRequest(AuditModules.ID_REPO_IDENTITY_SERVICE,
				AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id",IdType.REG_ID, "desc");
		actualRequest.getRequest().setActionTimeStamp(null);
		AuditRequestDTO expectedRequest = new AuditRequestDTO();
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();

			expectedRequest.setEventId(AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE.getEventId());
			expectedRequest.setEventName(AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE.getEventName());
			expectedRequest.setEventType(AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE.getEventType());
			expectedRequest.setActionTimeStamp(null);
			expectedRequest.setHostName(inetAddress.getHostName());
			expectedRequest.setHostIp(inetAddress.getHostAddress());
			expectedRequest.setApplicationId(env.getProperty("mosip.idrepo.application.id"));
			expectedRequest.setApplicationName(env.getProperty("mosip.idrepo.application.name"));
			expectedRequest.setSessionUserId("sessionUserId");
			expectedRequest.setSessionUserName("sessionUserName");
			expectedRequest.setId("id");
			expectedRequest.setIdType(IdType.REG_ID.getIdType());
			expectedRequest.setCreatedBy(env.getProperty("user.name"));
			expectedRequest.setModuleName(AuditModules.ID_REPO_IDENTITY_SERVICE.getModuleName());
			expectedRequest.setModuleId(AuditModules.ID_REPO_IDENTITY_SERVICE.getModuleId());
			expectedRequest.setDescription("desc");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		assertEquals(expectedRequest, actualRequest.getRequest());
	}

}