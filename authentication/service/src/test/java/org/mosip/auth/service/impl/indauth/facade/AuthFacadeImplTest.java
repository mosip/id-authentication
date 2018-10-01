package org.mosip.auth.service.impl.indauth.facade;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.dto.indauth.AuthResponseDTO;
import org.mosip.auth.core.dto.indauth.AuthTypeDTO;
import org.mosip.auth.core.dto.indauth.IdType;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.helper.RestHelper;
import org.mosip.auth.service.impl.idauth.service.impl.IdAuthServiceImpl;
import org.mosip.auth.service.impl.indauth.service.OTPAuthServiceImpl;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

// TODO: Auto-generated Javadoc
/**
 * The class validates AuthFacadeImpl.
 *
 * @author Arun Bose
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes= {TestContext.class, WebApplicationContext.class})
@TestPropertySource(value = { "classpath:audit.properties", "classpath:rest-services.properties", "classpath:log.properties" })
public class AuthFacadeImplTest {
	
	/** The rest helper. */
	@Mock
	RestHelper restHelper;
	
	/** The env. */
	@Autowired
	Environment env;
	
	/*@InjectMocks
	private MosipLogger logger;*/

	/*@InjectMocks
	MosipRollingFileAppender idaRollingFileAppender;*/

	/** The rest factory. */
	@InjectMocks
	private RestRequestFactory  restFactory;
	
	/** The audit factory. */
	@InjectMocks
	private AuditRequestFactory auditFactory;
	
	/** The auth facade impl. */
	@InjectMocks
	private AuthFacadeImpl authFacadeImpl;
	
	/** The id auth service impl. */
	@Mock
	private IdAuthServiceImpl idAuthServiceImpl;
	
	/** The otp auth service impl. */
	@Mock
	private OTPAuthServiceImpl otpAuthServiceImpl;

	
	/**
	 * Before.
	 */
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
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(authFacadeImpl, "auditFactory", auditFactory);
		ReflectionTestUtils.setField(authFacadeImpl, "restFactory", restFactory);
	}
	
	
	/**
	 * This class tests the authenticateApplicant method where it checks the  IdType and AuthType.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	
	@Test
	public void authenticateApplicantTest() throws IdAuthenticationBusinessException {
		String refId="1234";
		boolean authStatus=false;
		AuthResponseDTO authResponseDTO=new AuthResponseDTO();
		authResponseDTO.setStatus(false);
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN);
		authRequestDTO.setId("1234567");
		AuthTypeDTO authTypeDTO=new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setAuthType(authTypeDTO);
		Mockito.when(idAuthServiceImpl.validateUIN(Mockito.any())).thenReturn(refId);
		Mockito.when(otpAuthServiceImpl.validateOtp(authRequestDTO, refId)).thenReturn(authStatus);
		AuthResponseDTO authenticateApplicant = authFacadeImpl.authenticateApplicant(authRequestDTO);
	}
	
	
	/**
	 * This class tests the processAuthType  (OTP)   method where otp validation failed.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test
	public void processAuthTypeTestFail() throws IdAuthenticationBusinessException{
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		AuthTypeDTO authType=new AuthTypeDTO();
		authRequestDTO.setAuthType(authType);
		authRequestDTO.getAuthType().setOtp(false);
		boolean authStatus=authFacadeImpl.processAuthType(authRequestDTO, Mockito.any());
		assertEquals(authStatus,false);
    }
	 
	
	/**
	 * This class tests the processAuthType  (OTP)   method where otp validation gets successful.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	
	@Test
	public void processAuthTypeTestSuccess() throws IdAuthenticationBusinessException{
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		AuthTypeDTO authTypeDTO=new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setAuthType(authTypeDTO);
		Mockito.when(otpAuthServiceImpl.validateOtp(authRequestDTO,"1242")).thenReturn(true);
		boolean authStatus=authFacadeImpl.processAuthType(authRequestDTO, "1242");
		assertEquals(authStatus,true);
    }
	
	/**
	 * This class tests the processIdtype  where UIN is passed and gets successful.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test
	public void processIdtypeUINSuccess() throws IdAuthenticationBusinessException{
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN);
		String refId="1234";
		Mockito.when(idAuthServiceImpl.validateUIN(Mockito.any())).thenReturn(refId);
		String referenceId=authFacadeImpl.processIdType(authRequestDTO);
		assertEquals(referenceId,refId);
    }
	
	/**
	 * This class tests the processIdtype  where VID is passed and gets successful.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test
     public void processIdtypeVIDSuccess() throws IdAuthenticationBusinessException{
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.VID);
		String refId="1234";
		Mockito.when(idAuthServiceImpl.validateVID(Mockito.any())).thenReturn(refId);
		String referenceId=authFacadeImpl.processIdType(authRequestDTO);
		assertEquals(referenceId,refId);
    }
	
	/**
	 * This class tests the processIdtype  where UIN is passed and gets failed.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void processIdtypeUINFailed() throws IdAuthenticationBusinessException{
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN);
		String refId="1234";
		IdValidationFailedException idException =new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_UIN);
		Mockito.when(idAuthServiceImpl.validateUIN(Mockito.any())).thenThrow(idException);
		String referenceId=authFacadeImpl.processIdType(authRequestDTO);
		//assertEquals(referenceId,refId);
    }
	
	
	/**
	 * This class tests the processIdtype  where VID is passed and gets failed.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	
	@Test(expected=IdAuthenticationBusinessException.class)
    public void processIdtypeVIDFailed() throws IdAuthenticationBusinessException{
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.VID);
		String refId="1234";
		IdValidationFailedException idException =new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_VID);
		Mockito.when(idAuthServiceImpl.validateVID(Mockito.any())).thenThrow(idException);
		authFacadeImpl.processIdType(authRequestDTO);
		
   }
	
	
	
	
	
}	
	
