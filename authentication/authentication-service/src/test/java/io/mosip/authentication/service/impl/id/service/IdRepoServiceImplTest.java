package io.mosip.authentication.service.impl.id.service;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
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
	@Mock
	private IdRepoServiceImpl idReposerviceImplMock;
	
	@Autowired
	private Environment environment;

	@Before
	public void before() {
		ReflectionTestUtils.setField(idReposerviceImpl, "restHelper", restHelper);
		ReflectionTestUtils.setField(idReposerviceImpl, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(idReposerviceImpl, "environment", environment);
	}

	@Test
	public void testGetIdRepo() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		Map<String, Object> response = new HashMap<>();
		response.put("status", "activated");
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
		Mockito.when(idReposerviceImpl.getIdenity("76746685", false)).thenReturn(response);

		assertNotNull(response);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGetIdRepo_ThrowException() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, null, Map.class))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.SERVER_ERROR));
		idReposerviceImpl.getIdenity("76746685", false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGetIdRepo_ThrowException2() throws IdAuthenticationBusinessException, RestServiceException {
		RestRequestDTO restRequestDTO = new RestRequestDTO();

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, null, Map.class))
				.thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR));
		idReposerviceImpl.getIdenity("76746685", false);
	}


}
