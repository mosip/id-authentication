package io.mosip.kernal.auditmanager.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.config.AuditConfig;
import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.auditmanager.impl.AuditHandlerImpl;
import io.mosip.kernel.auditmanager.repository.AuditRepository;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.auditmanager.exception.AuditManagerException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuditConfig.class)
public class AuditEventTest {

	@Autowired
	private AuditHandlerImpl auditHandlerImpl;

	@MockBean
	private AuditRepository auditRepository;

	@Test
	public void auditBuilderTest() {

		Mockito.when(auditRepository.create(ArgumentMatchers.any(Audit.class))).thenReturn(new Audit());

		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();

		auditRequestBuilder.setActionTimeStamp(LocalDateTime.now()).setApplicationId("applicationId")
				.setApplicationName("applicationName").setCreatedBy("createdBy").setDescription("description")
				.setEventId("eventId").setEventName("eventName").setEventType("eventType").setHostIp("hostIp")
				.setHostName("hostName").setId("id").setIdType("idType").setModuleId("moduleId")
				.setModuleName("moduleName").setSessionUserId("sessionUserId").setSessionUserName("sessionUserName");

		AuditRequestDto auditRequest = auditRequestBuilder.build();
		auditHandlerImpl.addAudit(auditRequest);

		assertThat(auditHandlerImpl.addAudit(auditRequestBuilder.build()), is(true));
	}

	@Test(expected = AuditManagerException.class)
	public void auditBuilderExceptionTest() {

		Mockito.when(auditRepository.create(ArgumentMatchers.any(Audit.class))).thenReturn(new Audit());

		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();

		auditRequestBuilder.setApplicationId("applicationId").setApplicationName("applicationName")
				.setCreatedBy("createdBy").setDescription("description").setEventId("eventId").setEventName("eventName")
				.setEventType("eventType").setHostIp("hostIp").setHostName("hostName").setId("id").setIdType("idType")
				.setModuleId("moduleId").setModuleName("moduleName").setSessionUserId("sessionUserId")
				.setSessionUserName("sessionUserName");

		AuditRequestDto auditRequest = auditRequestBuilder.build();
		auditHandlerImpl.addAudit(auditRequest);

	}

}