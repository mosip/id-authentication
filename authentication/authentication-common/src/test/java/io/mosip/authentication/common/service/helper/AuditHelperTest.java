package io.mosip.authentication.common.service.helper;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdType;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
public class AuditHelperTest {

	@Mock
	RestHelperImpl restHelper;

	@InjectMocks
	AuditHelper auditHelper;

	@Mock
	IdInfoFetcherImpl idFetcherImpl;

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
	public void testAuditUtil() throws IDDataValidationException {
		auditHelper.audit(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");
	}

	@Test
	public void TestgetUinorVid() {
		Mockito.when(idFetcherImpl.getUinOrVid(Mockito.any())).thenReturn(Optional.ofNullable("426789089018"));
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		auditHelper.getUinorVid(authRequestDTO);
	}

}
