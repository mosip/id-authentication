package org.mosip.auth.service.impl.indauth.service;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Calendar;
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
import org.mosip.auth.service.impl.idauth.service.impl.IdAuthServiceImpl;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * This class tests the IdAuthServiceImpl.java
 * 
 * @author Arun Bose
 */

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class IdAuthServiceTest {
	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	@Autowired
	private RestRequestFactory  restFactory;
	
	@Autowired
	private AuditRequestFactory auditFactory;
	
	@Mock
	private VIDRepository vidRepository;

	@InjectMocks
	private IdAuthServiceImpl idAuthServiceImpl;

	@Mock
	private UinRepository uinRepository;
	
	@Before
	public void before() {
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
		vidEntity.setExpiryDate(new Date(2019, 1, 1));
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
