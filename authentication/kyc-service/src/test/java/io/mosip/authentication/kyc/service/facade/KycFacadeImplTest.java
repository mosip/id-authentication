/**
 * 
 */
package io.mosip.authentication.kyc.service.facade;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.facade.AuthFacadeImpl;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * @author M1047697
 *
 */
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
public class KycFacadeImplTest {

	@InjectMocks
	private KycFacadeImpl kycFacade;

	@InjectMocks
	private AuthFacadeImpl authFacadeImpl;

	@Mock
	private IdInfoFetcher idInfoFetcher;

	@Autowired
	Environment env;

	@Before
	public void beforeClass() {
		ReflectionTestUtils.setField(kycFacade, "authFacade", authFacadeImpl);
		ReflectionTestUtils.setField(kycFacade, "authFacade", authFacadeImpl);
		ReflectionTestUtils.setField(authFacadeImpl, "idInfoFetcher", idInfoFetcher);
	}

	@Test
	public void TestKycFacade() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);
		kycFacade.authenticateIndividual(authRequestDTO, true, "123456");
	}

}
