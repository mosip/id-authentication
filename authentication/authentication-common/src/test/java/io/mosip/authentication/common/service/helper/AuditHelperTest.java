package io.mosip.authentication.common.service.helper;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.idrepository.core.helper.RestHelper;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@Import(EnvUtil.class)
public class AuditHelperTest {

	@Mock
	RestHelper restHelper;

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

	@InjectMocks
	EnvUtil env;
	
	@Mock
	Environment environment;
	
	@Autowired
	ObjectMapper mapper;

	@Before
	public void before() {
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.setField(auditHelper, "mapper", mapper);
		ReflectionTestUtils.setField(auditHelper, "env", env);
	}

	@Test
	public void testAuditStatusWithEnumIdType() throws IDDataValidationException {
		auditHelper.audit(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");
	}
	
	@Test
	public void testAuditStatusWithStringIdType() throws IDDataValidationException {
		auditHelper.audit(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN.name(), "desc");
	}
	
	@Test
	public void testAuditExceptionWithEnumIdType() throws IDDataValidationException {
		IdAuthenticationBaseException e = new IdAuthenticationBaseException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		auditHelper.audit(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, e);
	}
	
	@Test
	public void testAuditExceptionWithStringIdType() throws IDDataValidationException {
		IdAuthenticationBaseException e = new IdAuthenticationBaseException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		auditHelper.audit(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN.name(), e);
	}
	
	@Test
	public void testAuditAuthException() throws IDDataValidationException {
		IdAuthenticationBaseException e = new IdAuthenticationBaseException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		AuthRequestDTO authRequestDTO = createAuthRequest();
		
		EnvUtil.setIsFmrEnabled(false);
		
		auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , e);
		
		auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , e);
		
		auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , e);
		
		auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , e);
		
		auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , e);

		EnvUtil.setIsFmrEnabled(true);
		auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , e);
		
		List<BioIdentityInfoDTO> biometrics = authRequestDTO.getRequest().getBiometrics();
		biometrics.remove(0);
		auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , e);
		
		biometrics.remove(0);
		auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , e);
		
		biometrics.remove(0);
		auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , e);
		
		biometrics.remove(0);
		auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , e);


	}
	
	private AuthRequestDTO createAuthRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		
		RequestDTO request = new RequestDTO();
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		
		BioIdentityInfoDTO bioId1 = new BioIdentityInfoDTO();
		DataDTO data1 = new DataDTO();
		data1.setBioType("FMR");
		bioId1.setData(data1 );
		biometrics.add(bioId1);
		
		bioId1 = new BioIdentityInfoDTO();
		data1 = new DataDTO();
		data1.setBioType("Finger");
		bioId1.setData(data1 );
		biometrics.add(bioId1);
		
		bioId1 = new BioIdentityInfoDTO();
		data1 = new DataDTO();
		data1.setBioType("Iris");
		bioId1.setData(data1 );
		biometrics.add(bioId1);
		
		bioId1 = new BioIdentityInfoDTO();
		data1 = new DataDTO();
		data1.setBioType("FACE");
		bioId1.setData(data1 );
		biometrics.add(bioId1);
		
		request.setBiometrics(biometrics);
		authRequestDTO.setRequest(request );
		
		return authRequestDTO;
	}

	@Test
	public void testAuditAuthStatus() throws IDDataValidationException {
		AuthRequestDTO authRequestDTO = createAuthRequest();
		EnvUtil.setIsFmrEnabled(false);

		auditHelper.auditStatusForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO , "Status: true");
	
	}

}
