package org.mosip.kernal.auditmanager.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.OffsetDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import org.mosip.kernel.auditmanager.config.AuditConfig;
import org.mosip.kernel.auditmanager.exception.MosipAuditManagerException;
import org.mosip.kernel.auditmanager.handler.AuditRequestHandler;
import org.mosip.kernel.auditmanager.model.Audit;
import org.mosip.kernel.auditmanager.repository.AuditRepository;
import org.mosip.kernel.auditmanager.request.AuditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuditConfig.class)
public class AuditEventTest {

	@Autowired
	private AuditRequestHandler handler;

	@MockBean
	private AuditRepository auditRepository;

	@Test
	public void auditBuilderTest() {

		Mockito.when(auditRepository.create(ArgumentMatchers.any(Audit.class))).thenReturn(new Audit());

		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();

		auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now()).setApplicationId("applicationId")
				.setApplicationName("applicationName").setCreatedBy("createdBy").setDescription("description")
				.setEventId("eventId").setEventName("eventName").setEventType("eventType").setHostIp("hostIp")
				.setHostName("hostName").setId("id").setIdType("idType").setModuleId("moduleId")
				.setModuleName("moduleName").setSessionUserId("sessionUserId").setSessionUserName("sessionUserName");

		AuditRequest auditRequest = auditRequestBuilder.build();
		handler.writeAudit(auditRequest);

		assertThat(handler.writeAudit(auditRequestBuilder.build()), is(true));
	}

	@Test(expected = MosipAuditManagerException.class)
	public void auditBuilderExceptionTest() {

		Mockito.when(auditRepository.create(ArgumentMatchers.any(Audit.class))).thenReturn(new Audit());

		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();

		auditRequestBuilder.setApplicationId("applicationId").setApplicationName("applicationName")
				.setCreatedBy("createdBy").setDescription("description").setEventId("eventId").setEventName("eventName")
				.setEventType("eventType").setHostIp("hostIp").setHostName("hostName").setId("id").setIdType("idType")
				.setModuleId("moduleId").setModuleName("moduleName").setSessionUserId("sessionUserId")
				.setSessionUserName("sessionUserName");

		AuditRequest auditRequest = auditRequestBuilder.build();
		handler.writeAudit(auditRequest);

	}

}