package io.mosip.authentication.service.impl.indauth.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

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

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.service.impl.id.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.service.OTPAuthServiceImpl;

// TODO: Auto-generated Javadoc
/**
 * The class validates AuthFacadeImpl.
 *
 * @author Arun Bose
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes= {TestContext.class, WebApplicationContext.class})
public class AuthFacadeImplTest {
	
	/** The env. */
	@Autowired
	Environment env;
	
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
		ReflectionTestUtils.setField(authFacadeImpl, "idAuthService", idAuthServiceImpl);
		ReflectionTestUtils.setField(authFacadeImpl, "otpService", otpAuthServiceImpl);
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
		authResponseDTO.setStatus("n");
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setId("1234567");
		AuthTypeDTO authTypeDTO=new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setAuthType(authTypeDTO);
		Mockito.when(idAuthServiceImpl.validateUIN(Mockito.any())).thenReturn(refId);
		Mockito.when(otpAuthServiceImpl.validateOtp(authRequestDTO, refId))
				.thenReturn(AuthStatusInfoBuilder.newInstance().setStatus(authStatus).build());
		authFacadeImpl.authenticateApplicant(authRequestDTO);
	}
	
	
	/**
	 * This class tests the processAuthType  (OTP)   method where otp validation failed.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test
	public void processAuthTypeTestFail() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		AuthTypeDTO authType=new AuthTypeDTO();
		authRequestDTO.setAuthType(authType);
		authRequestDTO.getAuthType().setOtp(false);
		List<AuthStatusInfo> authStatusList=authFacadeImpl.processAuthType(authRequestDTO, "1233");
		
		assertTrue(authStatusList
				.stream()
				.noneMatch(status -> 
						status.getUsageDataBits()
						.contains(AuthUsageDataBit.USED_OTP) 
				|| status.isStatus()));
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
		Mockito.when(otpAuthServiceImpl.validateOtp(authRequestDTO, "1242"))
				.thenReturn(AuthStatusInfoBuilder.newInstance()
						.setStatus(true)
						.addAuthUsageDataBits(AuthUsageDataBit.USED_OTP)
						.build());
		List<AuthStatusInfo> authStatusList=authFacadeImpl.processAuthType(authRequestDTO, "1242");
		assertTrue(authStatusList
				.stream()
				.anyMatch(status -> status
						.getUsageDataBits()
						.contains(AuthUsageDataBit.USED_OTP) 
				&& status.isStatus()));    }
	
	/**
	 * This class tests the processIdtype  where UIN is passed and gets successful.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Test
	public void processIdtypeUINSuccess() throws IdAuthenticationBusinessException{
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
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
		authRequestDTO.setIdvIdType(IdType.VID.getType());
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
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
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
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		String refId="1234";
		IdValidationFailedException idException =new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_VID);
		Mockito.when(idAuthServiceImpl.validateVID(Mockito.any())).thenThrow(idException);
		authFacadeImpl.processIdType(authRequestDTO);
		
   }
	
	
	
	
	
}	
	
