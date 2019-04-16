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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.factory.AuditRequestFactory;
import io.mosip.authentication.common.factory.RestRequestFactory;
import io.mosip.authentication.common.helper.AuditHelper;
import io.mosip.authentication.common.helper.RestHelper;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IDDataValidationException;

/**
 * The Class AuditHelperTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
@WebMvcTest
public class AuditHelperTest {
	
	/** The rest helper. */
	@Mock
	RestHelper restHelper;
	
	/** The audit helper. */
	@InjectMocks
	AuditHelper auditHelper;
	
    /** The mock mvc. */
    @Autowired
    MockMvc mockMvc;
	
    /** The audit factory. */
    @Mock
	AuditRequestFactory auditFactory;
	
    /** The rest factory. */
    @Mock
	RestRequestFactory restFactory;
    
    /** The env. */
    @Autowired
    Environment env;
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
	}
	
	/**
	 * Test audit util.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testAuditUtil() throws IDDataValidationException {
		auditHelper.audit(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");
	}

}
