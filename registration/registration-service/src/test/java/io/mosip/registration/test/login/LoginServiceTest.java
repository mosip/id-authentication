package io.mosip.registration.test.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.dao.AppAuthenticationDAO;
import io.mosip.registration.dao.AppAuthenticationDetails;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.RegistrationScreenAuthorizationDAO;
import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.dao.ScreenAuthorizationDetails;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.repositories.AppAuthenticationRepository;
import io.mosip.registration.repositories.RegistrationCenterRepository;
import io.mosip.registration.repositories.RegistrationScreenAuthorizationRepository;
import io.mosip.registration.repositories.RegistrationUserDetailRepository;
import io.mosip.registration.service.impl.LoginServiceImpl;

public class LoginServiceTest {

	@Mock
	private AuditFactoryImpl auditFactory;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private LoginServiceImpl loginServiceImpl;

	@Mock
	private AppAuthenticationRepository appAuthenticationRepository;

	@Mock
	private AppAuthenticationDAO appAuthenticationDAO;

	@Mock
	private RegistrationUserDetailRepository registrationUserDetailRepository;

	@Mock
	private RegistrationUserDetailDAO registrationUserDetailDAO;

	@Mock
	private RegistrationCenterRepository registrationCenterRepository;

	@Mock
	private RegistrationCenterDAO registrationCenterDAO;
	
	@Mock
	private RegistrationScreenAuthorizationRepository registrationScreenAuthorizationRepository;

	@Mock
	private RegistrationScreenAuthorizationDAO registrationScreenAuthorizationDAO;
	
	@Before
	public void initialize() throws IOException, URISyntaxException {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	@Test
	public void getModesOfLoginTest() {

		List<AppAuthenticationDetails> loginList = new ArrayList<AppAuthenticationDetails>();
		Mockito.when(appAuthenticationRepository.findByIsActiveTrueAndAppAuthenticationMethodIdProcessNameAndRoleCodeOrderByMethodSeq(Mockito.anyString(), Mockito.anyString())).thenReturn(loginList);

		List<String> modes = new ArrayList<>();
		loginList.stream().map(loginMethod -> loginMethod.getAppAuthenticationMethodId().getLoginMethod()).collect(Collectors.toList());

		Mockito.when(appAuthenticationRepository.findByIsActiveTrueAndAppAuthenticationMethodIdProcessNameAndRoleCodeOrderByMethodSeq("LOGIN","*")).thenReturn(loginList);
		
		Set<String> roleSet = new HashSet<>();
		roleSet.add("OFFICER");
		
		Mockito.when(appAuthenticationDAO.getModesOfLogin("LOGIN",roleSet)).thenReturn(modes);
		assertEquals(modes,loginServiceImpl.getModesOfLogin("LOGIN",roleSet));
	}

	@Test
	public void getUserDetailTest() {

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);
		Mockito.when(registrationUserDetailRepository.findByIdIgnoreCaseAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserDetailList);
		
		Mockito.when(registrationUserDetailDAO.getUserDetail(Mockito.anyString())).thenReturn(registrationUserDetail);
		
		assertEquals(registrationUserDetail,loginServiceImpl.getUserDetail("mosip"));		
	}

	@Test
	public void getRegistrationCenterDetailsTest() {

		RegistrationCenter registrationCenter = new RegistrationCenter();

		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(registrationCenterRepository.findByCenterIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationCenterList);
		
		Mockito.when(registrationCenterDAO.getRegistrationCenterDetails(Mockito.anyString())).thenReturn(centerDetailDTO);
		assertEquals(centerDetailDTO,loginServiceImpl.getRegistrationCenterDetails("mosip"));
	}


	@Test
	public void getScreenAuthorizationDetailsTest() {

		Set<ScreenAuthorizationDetails> authorizationList = new HashSet<>();
		List<String> roleList = new ArrayList<>();
		Mockito.when(registrationScreenAuthorizationRepository
				.findByRegistrationScreenAuthorizationIdRoleCodeInAndIsPermittedTrueAndIsActiveTrue(roleList))
				.thenReturn(authorizationList);
		AuthorizationDTO authorizationDTO = new AuthorizationDTO();
		Mockito.when(registrationScreenAuthorizationDAO.getScreenAuthorizationDetails(roleList)).thenReturn(authorizationDTO);
		assertNotNull(loginServiceImpl.getScreenAuthorizationDetails(roleList));

	}

	@Test
	public void updateLoginParamsTest() {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		doNothing().when(registrationUserDetailDAO).updateLoginParams(Mockito.any(RegistrationUserDetail.class));
		
		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setId("mosip");
		registrationUserDetail.setUnsuccessfulLoginCount(0);
		registrationUserDetail.setLastLoginDtimes(new Timestamp(System.currentTimeMillis()));
		registrationUserDetail.setLastLoginMethod("PWD");
		
		loginServiceImpl.updateLoginParams(registrationUserDetail);
	}
}
