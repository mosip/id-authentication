package io.mosip.authentication.service.impl.indauth.service;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
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

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.service.entity.VIDEntity;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.id.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.repository.VIDRepository;

/**
 * This class tests the IdAuthServiceImpl.java
 * 
 * @author Arun Bose
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdAuthServiceTest {

	@Mock
	RestHelper restHelper;

	@Autowired
	Environment env;

	@InjectMocks
	private RestRequestFactory restFactory;

	@InjectMocks
	private AuditRequestFactory auditFactory;

	@Mock
	private VIDRepository vidRepository;

	@InjectMocks
	private IdAuthServiceImpl idAuthServiceImpl;

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.setField(idAuthServiceImpl, "auditFactory", auditFactory);
		ReflectionTestUtils.setField(idAuthServiceImpl, "restFactory", restFactory);
	}

	/**
	 * This method throws IdValidationFailedException when UinEntity is null
	 * 
	 * 
	 */
	@Ignore
	@Test(expected = IdValidationFailedException.class)
	public void testValidateUIN() throws IdAuthenticationBusinessException {
		String uin = "1234567890";
//		Mockito.when(uinRepository.findByUinRefId(Mockito.anyString())).thenReturn(null);
		idAuthServiceImpl.getIdRepoByUinNumber(uin, false);
	}

	/**
	 * This method throws IdValidationFailedException when VIDEntity is null
	 * 
	 */
	@Ignore
	@Test(expected = IdValidationFailedException.class)
	public void testValidateVID() throws IdAuthenticationBusinessException {
		String vid = "1234567890";
		Mockito.when(vidRepository.getOne(Mockito.anyString())).thenReturn(null);
		idAuthServiceImpl.getIdRepoByVidNumber(vid, false);
	}

	/*
	 * This method throws IdValidationFailedException when VIDEntity is not null but
	 * VID is inactive
	 * 
	 */
	@Ignore
	@Test(expected = IdValidationFailedException.class)
	public void testValidateVIDInactive() throws IdAuthenticationBusinessException {
		String vid = "1234567890";
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setActive(false);
		Mockito.when(vidRepository.getOne(Mockito.anyString())).thenReturn(vidEntity);
		idAuthServiceImpl.getIdRepoByVidNumber(vid, false);
	}

	/**
	 * This method throws IdValidationFailedException when VIDEntity is not null but
	 * VID is active but validity expired
	 * 
	 */
	@Ignore
	@Test(expected = IdValidationFailedException.class)
	public void testValidateVIDexpired() throws IdAuthenticationBusinessException {
		String vid = "1234567890";
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setActive(true);
		vidEntity.setExpiryDate(new Date(2018, 9, 24));
		Mockito.when(vidRepository.getOne(Mockito.anyString())).thenReturn(vidEntity);
		idAuthServiceImpl.getIdRepoByVidNumber(vid, false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestAuditData() throws Throwable {
		RestRequestFactory restfactory = Mockito.mock(RestRequestFactory.class);
		Mockito.when(restfactory.buildRequest(Mockito.any(RestServicesConstants.class), Mockito.any(), Mockito.any()))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED));
		ReflectionTestUtils.setField(idAuthServiceImpl, "restFactory", restfactory);
		try {
			ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "auditData");
		} catch (UndeclaredThrowableException e) {
			throw e.getUndeclaredThrowable();

		}

	}
}
