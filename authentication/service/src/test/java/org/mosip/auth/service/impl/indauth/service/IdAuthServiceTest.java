package org.mosip.auth.service.impl.indauth.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.mosip.auth.service.dao.UinRepository;
import org.mosip.auth.service.dao.VIDRepository;
import org.mosip.auth.service.entity.UinEntity;
import org.mosip.auth.service.entity.VIDEntity;
import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.helper.RestHelper;
import org.mosip.auth.service.impl.idauth.service.impl.IdAuthServiceImpl;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 * This class tests the IdAuthServiceImpl.java
 * 
 * @author Arun Bose
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes= {TestContext.class, WebApplicationContext.class})
@TestPropertySource(value = { "classpath:audit.properties", "classpath:rest-services.properties", "classpath:log.properties" })
public class IdAuthServiceTest {
	private MosipLogger logger;

	@Mock
	RestHelper restHelper;
	
	@Autowired
	Environment env;
	
	

	@InjectMocks
	private RestRequestFactory  restFactory;
	
	@InjectMocks
	private AuditRequestFactory auditFactory;
	
	@Mock
	private VIDRepository vidRepository;

	@InjectMocks
	private IdAuthServiceImpl idAuthServiceImpl;

	@Mock
	private UinRepository uinRepository;
	
	@Before
	public void before() {
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(env.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(env.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(env.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(env.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(env.getProperty("log4j.appender.Appender.totalCap"));
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
       
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(restHelper, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.invokeMethod(auditFactory, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.invokeMethod(idAuthServiceImpl, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(idAuthServiceImpl, "auditFactory", auditFactory);
		ReflectionTestUtils.setField(idAuthServiceImpl, "restFactory", restFactory);
		ReflectionTestUtils.setField(idAuthServiceImpl, "logger", logger);
	}

	/*
	 * This method throws IdValidationFailedException when UinEntity is null
	 * 
	 * 
	 */
	@Test(expected = IdValidationFailedException.class)
	public void testValidateUIN() throws IdAuthenticationBusinessException {
		String uin = "1234567890";
		Mockito.when(uinRepository.findByUin(Mockito.anyString())).thenReturn(null);
		idAuthServiceImpl.validateUIN(uin);
	}

	/*
	 * This method throws IdValidationFailedException when UinEntity is not null but
	 * UIN is inactive
	 * 
	 */
	@Test(expected = IdValidationFailedException.class)
	public void testValidateUINInactive() throws IdAuthenticationBusinessException {
		String uin = "1234567890";
		UinEntity uinEntity = new UinEntity();
		uinEntity.setActive(false);
		Mockito.when(uinRepository.findByUin(uin)).thenReturn(uinEntity);
		idAuthServiceImpl.validateUIN(uin);
     }

	/*
	 * This method throws IdValidationFailedException when UinEntity is not null but
	 * UIN is active
	 * 
	 */
	@Test
	public void testValidateUinActive() throws IdAuthenticationBusinessException {
		String uin = "1234567890";
		UinEntity uinEntity = new UinEntity();
		uinEntity.setActive(true);
		uinEntity.setId("12345");
		Mockito.when(uinRepository.findByUin(Mockito.anyString())).thenReturn(uinEntity);
		String refId = null;
        refId = idAuthServiceImpl.validateUIN(uin);
        assertEquals(refId, uinEntity.getId());
	}
	
	/*
	 * This method throws IdValidationFailedException when VIDEntity is null 
	 * 
	 */
	@Test(expected = IdValidationFailedException.class)
	public void testValidateVID() throws IdAuthenticationBusinessException {
		String vid = "1234567890";
		Mockito.when(vidRepository.getOne(Mockito.anyString())).thenReturn(null);
		idAuthServiceImpl.validateVID(vid);
	}
	
	/*
	 * This method throws IdValidationFailedException when VIDEntity is not null but
	 * VID is inactive
	 * 
	 */
	@Test(expected = IdValidationFailedException.class)
	public void testValidateVIDInactive() throws IdAuthenticationBusinessException {
		String vid = "1234567890";
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setActive(false);
		Mockito.when(vidRepository.getOne(Mockito.anyString())).thenReturn(vidEntity);
		idAuthServiceImpl.validateVID(vid);
	}
	
	/*
	 * This method throws IdValidationFailedException when VIDEntity is not null but
	 * VID is active but validity expired
	 * 
	 */
	@Test(expected = IdValidationFailedException.class)
	public void testValidateVIDexpired() throws IdAuthenticationBusinessException {
		String vid = "1234567890";
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setActive(true);
		vidEntity.setExpiryDate(new Date(2018, 9, 24));
		Mockito.when(vidRepository.getOne(Mockito.anyString())).thenReturn(vidEntity);
		idAuthServiceImpl.validateVID(vid);
	}
	
	
	/*
	 * This method throws IdValidationFailedException when VIDEntity is not null but
	 * VID is active  and checks for refId in UIN and failed.
	 * 
	 */
	@Test(expected=IdValidationFailedException.class)
	public void testValidateRef() throws IdAuthenticationBusinessException {
		String vid = "1234567890";
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setActive(true);
		vidEntity.setExpiryDate(new Date(2019, 1, 1));
		UinEntity uinEntity=null;
		Mockito.when(vidRepository.getOne(Mockito.anyString())).thenReturn(vidEntity);
		Mockito.when(uinRepository.findById(Mockito.anyString())).thenReturn(Optional.ofNullable(uinEntity));
		idAuthServiceImpl.validateVID(vid);
	}
	
	
	/*
	 * This method throws IdValidationFailedException when VIDEntity is not null but
	 * VID is active  and checks for refId in UIN and UIN is inactive.
	 * 
	 */
	@Test(expected=IdValidationFailedException.class)
	public void testValidateUINInactiveForVID() throws IdAuthenticationBusinessException {
		String vid = "1234567890";
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setActive(true);
		vidEntity.setExpiryDate(new Date());
		UinEntity uinEntity=new UinEntity();
		uinEntity.setActive(false);
		Mockito.when(vidRepository.getOne(Mockito.anyString())).thenReturn(vidEntity);
		Mockito.when(uinRepository.findById(Mockito.any())).thenReturn(Optional.of(uinEntity));
		idAuthServiceImpl.validateVID(vid);
		
	}
	
	
	/*
	 * This method throws IdValidationFailedException when VIDEntity is not null but
	 * VID is active  and checks for refId in UIN and returns refId
	 * 
	 */
	@Test
	public void testValidatebothActive() throws IdAuthenticationBusinessException {
		String vid = "1234567890";
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setRefId("1234");
		vidEntity.setActive(true);
		vidEntity.setExpiryDate(new Date(01/01/2019));
		UinEntity uinEntity=new UinEntity();
		uinEntity.setActive(true);
		Optional<UinEntity> uinEntityopt= Optional.of(uinEntity);
		System.err.println(uinEntityopt.isPresent());
//		UinRepository uinMock=Mockito.mock(UinRepository.class);
//		Mockito.when(uinMock.findById(Mockito.anyString())).thenReturn(uinEntityopt);
//		System.err.println(uinMock.findById("refId").get());
		Mockito.when(uinRepository.findById(Mockito.any())).thenReturn(uinEntityopt);
		Mockito.when(vidRepository.getOne(Mockito.anyString())).thenReturn(vidEntity);
		
		ReflectionTestUtils.setField(idAuthServiceImpl, "uinRepository", uinRepository);
		System.err.println(uinRepository.findById("refId").get());
		String refId=idAuthServiceImpl.validateVID(vid);
		assertEquals(vidEntity.getRefId(),refId );
		
	}
}
