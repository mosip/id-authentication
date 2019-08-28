package io.mosip.authentication.internal.service.controller;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.entity.AuthtypeLock;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.authentication.internal.service.impl.UpdateAuthtypeStatusServiceImpl;

/**
 *
 * @author Dinesh Karuppiah.T
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class UpdateAuthtypeStatusServiceImplTest {

	@InjectMocks
	private UpdateAuthtypeStatusServiceImpl authtypeStatusServiceImpl;

	@Mock
	private IdServiceImpl idService;

	@Mock
	private AuthLockRepository authLockRepository;

	@Autowired
	private Environment environment;

	@Before
	public void before() {
		ReflectionTestUtils.setField(authtypeStatusServiceImpl, "environment", environment);
	}

	@Test
	public void TestupdateAuthtypeStatus() throws IdAuthenticationBusinessException {
		AuthTypeStatusDto authTypeStatusDto = getAuthtypestatusdto();
		Map<String, Object> valueMap = new HashMap();
		valueMap.put("uin", "274390482564");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(valueMap);
		List<AuthtypeLock> value = new ArrayList<>();
		Mockito.when(authLockRepository.saveAll(Mockito.any())).thenReturn(value);
		authtypeStatusServiceImpl.updateAuthtypeStatus(authTypeStatusDto);
	}

	private AuthTypeStatusDto getAuthtypestatusdto() {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		authTypeStatusDto.setConsentObtained(true);
		authTypeStatusDto.setIndividualId("274390482564");
		authTypeStatusDto.setIndividualIdType(IdType.UIN.getType());
		List<AuthtypeStatus> request = new ArrayList();
		AuthtypeStatus authtypeStatus = new AuthtypeStatus();
		authtypeStatus.setAuthType(Category.DEMO.getType());
		authtypeStatus.setLocked(true);
		AuthtypeStatus authtypeStatus1 = new AuthtypeStatus();
		authtypeStatus1.setAuthType(Category.BIO.getType());
		authtypeStatus1.setAuthSubType(BioAuthType.FACE_IMG.getType());
		authtypeStatus1.setLocked(false);
		request.add(authtypeStatus);
		request.add(authtypeStatus1);
		authTypeStatusDto.setRequest(request);
		ZoneOffset offset = ZoneOffset.MAX;
		authTypeStatusDto.setRequestTime("2019-08-06T07:26:59.481Z");
		return authTypeStatusDto;
	}

}
