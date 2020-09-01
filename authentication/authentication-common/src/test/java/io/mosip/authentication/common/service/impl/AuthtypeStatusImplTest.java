package io.mosip.authentication.common.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.authtype.dto.AuthtypeRequestDto;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class AuthtypeStatusImplTest {

	@InjectMocks
	private AuthtypeStatusImpl authtypeStatusImpl;
	
	@Mock
	private IdAuthSecurityManager securityManager;

	@Mock
	private IdServiceImpl idService;

	@Mock
	private AuthLockRepository authLockRepository;
	
	@Test
	public void TestvalidfetchAuthtypeStatus() throws IdAuthenticationBusinessException {
		AuthtypeRequestDto authtypeRequestDto = getAuthTypeRequestDto();
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(value);
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("213213214325254326643");
		
		List<Object[]> valuelist = new ArrayList<>();
		Object[] authtypeLockStatus = new Object[] {"bio-FMR", "y"};
		valuelist.add(authtypeLockStatus);
		Mockito.when(authLockRepository.findByToken(Mockito.anyString())).thenReturn(valuelist);
		List<AuthtypeStatus> authTypeStatus = authtypeStatusImpl.fetchAuthtypeStatus(authtypeRequestDto);
	}

	@Test
	public void TestvalidfetchAuthtypeStatuswithParam() throws IdAuthenticationBusinessException {
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(value);
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("213213214325254326643");

		List<Object[]> valuelist = new ArrayList<>();
		Object[] authtypeLockStatus = new Object[] {"bio-FMR", "y"};
		valuelist.add(authtypeLockStatus);
		authtypeLockStatus = new Object[] {"demo", "n"};
		valuelist.add(authtypeLockStatus);
		Mockito.when(authLockRepository.findByToken(Mockito.anyString())).thenReturn(valuelist);
		List<AuthtypeStatus> authTypeStatus = authtypeStatusImpl.fetchAuthtypeStatus("9172985031",
				IdType.UIN.getType());
	}

	private AuthtypeRequestDto getAuthTypeRequestDto() {
		AuthtypeRequestDto authtypeRequestDto = new AuthtypeRequestDto();
		authtypeRequestDto.setIndividualId("9172985031");
		authtypeRequestDto.setIndividualIdType(IdType.UIN.getType());
		List<AuthtypeStatus> authtypes = new ArrayList<AuthtypeStatus>();
		AuthtypeStatus authtypeStatus = new AuthtypeStatus();
		authtypeStatus.setAuthSubType(BioAuthType.FACE_IMG.getType());
		authtypeStatus.setAuthSubType(BioAuthType.FACE_IMG.getType());
		authtypeStatus.setLocked(true);
		authtypes.add(authtypeStatus);
		authtypeRequestDto.setAuthtypes(authtypes);
		return authtypeRequestDto;
	}

}
