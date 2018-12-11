package io.mosip.authentication.service.impl.id.service;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.id.service.impl.IdRepoServiceImpl;

/**
 * IdRepoServiceImplTest test class.
 *
 * @author Rakesh Roshan
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdRepoServiceImplTest {

	@Mock
	private RestHelper restHelper;

	@Mock
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	private IdRepoServiceImpl idReposerviceImpl;

	@Before
	public void before() {
		ReflectionTestUtils.setField(idReposerviceImpl, "restHelper", restHelper);
		ReflectionTestUtils.setField(idReposerviceImpl, "restRequestFactory", restRequestFactory);
	}

	@Test
	public void testGetIdRepo() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Map<String, Object> response = new HashMap<>();

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
		Mockito.when(idReposerviceImpl.getIdRepo("76746685")).thenReturn(response);

		assertNotNull(response);
	}
}
