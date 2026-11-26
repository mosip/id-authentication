package io.mosip.authentication.common.service.impl;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.core.authtype.dto.AuthtypeRequestDto;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.idrepository.core.dto.AuthtypeStatus;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class AuthtypeStatusImplTest {

	@InjectMocks
	private AuthtypeStatusImpl authtypeStatusImpl;

	@Mock
	private AuthLockRepository authLockRepository;

	@Test
	public void TestvalidfetchAuthtypeStatus() throws IdAuthenticationBusinessException {
		AuthtypeRequestDto authtypeRequestDto = getAuthTypeRequestDto();
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		List<Object[]> valuelist = new ArrayList<>();
		// Unlock expiry must be on a UTC basis: AuthtypeStatusImpl compares it with DateUtils2.getUTCCurrentDateTime()
		Object[] authtypeLockStatus = new Object[] { "bio-FMR", "true",
				LocalDateTime.now(Clock.systemUTC()).plusMinutes(1) };
		valuelist.add(authtypeLockStatus);
		Mockito.when(authLockRepository.findByToken(Mockito.anyString())).thenReturn(valuelist);
		List<AuthtypeStatus> authTypeStatus = authtypeStatusImpl.fetchAuthtypeStatus("");
		Assertions.assertEquals(1, authTypeStatus.size());
		Assertions.assertEquals("bio", authTypeStatus.get(0).getAuthType());
		Assertions.assertEquals("FMR", authTypeStatus.get(0).getAuthSubType());
		// locked, but temporarily unlocked until the (future) expiry time
		Assertions.assertEquals(Boolean.FALSE, authTypeStatus.get(0).getLocked());
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
