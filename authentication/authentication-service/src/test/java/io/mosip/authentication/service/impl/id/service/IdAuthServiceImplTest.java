package io.mosip.authentication.service.impl.id.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.id.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.repository.UinRepository;
import io.mosip.authentication.service.repository.VIDRepository;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdAuthServiceImplTest {

	@Mock
	private IdRepoService idRepoService;
	@Mock
	private AuditRequestFactory auditFactory;
	@Mock
	private RestRequestFactory restFactory;
	@Mock
	private RestHelper restHelper;
	@Mock
	UinRepository uinRepository;
	@Mock
	private VIDRepository vidRepository;

	@InjectMocks
	IdAuthServiceImpl idAuthServiceImpl;

	@Before
	public void before() {
		ReflectionTestUtils.setField(idAuthServiceImpl, "idRepoService", idRepoService);
		ReflectionTestUtils.setField(idAuthServiceImpl, "auditFactory", auditFactory);
		ReflectionTestUtils.setField(idAuthServiceImpl, "restFactory", restFactory);
		ReflectionTestUtils.setField(idAuthServiceImpl, "uinRepository", uinRepository);
		ReflectionTestUtils.setField(idAuthServiceImpl, "vidRepository", vidRepository);
	}

	@Test
	public void testGetIdRepoByUinNumber() throws IdAuthenticationBusinessException {
		String uin = "765743965";

		//Mockito.when(idRepoService.getIdRepo(Mockito.anyString())).thenReturn(Mockito.anyMap());
		//ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "auditData");
		//ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "auditData");
		ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "getIdRepoByUinNumber",Mockito.anyString());
		
		//Mockito.when(idAuthServiceImpl.getIdRepoByUinNumber(Mockito.anyString())).thenReturn(Mockito.anyMap());
	}
	
	@Test
	public void testAuditData() {
		ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "auditData");
	}
	
	@Ignore
	@Test
	public void testGetIdRepoByVidNumber() throws IdAuthenticationBusinessException {
		//ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "getIdRepoByVidNumber", Mockito.anyString());
		
		//Mockito.when(idAuthServiceImpl.getIdRepoByVidAsRequest(Mockito.anyString())).thenReturn(Mockito.anyMap());
		Mockito.when(idAuthServiceImpl.getIdRepoByVidNumber(Mockito.anyString())).thenReturn(Mockito.anyMap());
	}
}
