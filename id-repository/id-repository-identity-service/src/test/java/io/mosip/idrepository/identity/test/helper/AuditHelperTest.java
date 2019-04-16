package io.mosip.idrepository.identity.test.helper;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.idrepository.core.constant.AuditEvents;
import io.mosip.idrepository.core.constant.AuditModules;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.idrepository.identity.builder.AuditRequestBuilder;
import io.mosip.idrepository.identity.builder.RestRequestBuilder;
import io.mosip.idrepository.identity.helper.AuditHelper;
import io.mosip.idrepository.identity.helper.RestHelper;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
public class AuditHelperTest {

	@Mock
	RestHelper restHelper;

	@InjectMocks
	AuditHelper auditHelper;

	@Autowired
	MockMvc mockMvc;

	@Mock
	AuditRequestBuilder auditBuilder;

	@Mock
	RestRequestBuilder restBuilder;

	@Autowired
	Environment env;

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditBuilder, "env", env);
		ReflectionTestUtils.setField(restBuilder, "env", env);
	}

	@Test
	public void testAuditUtil() throws IdRepoDataValidationException {
		auditHelper.audit(AuditModules.CREATE_IDENTITY, AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id", "desc");
	}

}
