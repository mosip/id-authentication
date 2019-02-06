package io.mosip.kernel.idrepo.helper;

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

import io.mosip.kernel.core.idrepo.constant.AuditEvents;
import io.mosip.kernel.core.idrepo.constant.AuditModules;
import io.mosip.kernel.core.idrepo.exception.IdRepoDataValidationException;
import io.mosip.kernel.idrepo.factory.AuditRequestFactory;
import io.mosip.kernel.idrepo.factory.RestRequestFactory;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
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
	AuditRequestFactory auditFactory;
	
    @Mock
	RestRequestFactory restFactory;
    
    @Autowired
    Environment env;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
	}
	
	@Test
	public void testAuditUtil() throws IdRepoDataValidationException {
		auditHelper.audit(AuditModules.CREATE_IDENTITY, AuditEvents.CREATE_IDENTITY_REQUEST_RESPONSE, "id", "desc");
	}

}
