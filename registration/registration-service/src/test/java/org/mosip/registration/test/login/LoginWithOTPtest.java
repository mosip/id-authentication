package org.mosip.registration.test.login;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.core.utils.HMACUtil;
import org.mosip.registration.dao.RegistrationAppLoginDAO;
import org.mosip.registration.dao.RegistrationCenterDAO;
import org.mosip.registration.dao.RegistrationUserDetailDAO;
import org.mosip.registration.dao.RegistrationUserPasswordDAO;
import org.mosip.registration.dao.RegistrationUserRoleDAO;
import org.mosip.registration.dto.OtpGeneratorRequestDto;
import org.mosip.registration.dto.OtpGeneratorResponseDto;
import org.mosip.registration.dto.OtpValidatorResponseDto;
import org.mosip.registration.entity.RegistrationAppLoginMethod;
import org.mosip.registration.entity.RegistrationAppLoginMethodID;
import org.mosip.registration.entity.RegistrationCenter;
import org.mosip.registration.entity.RegistrationUserDetail;
import org.mosip.registration.entity.RegistrationUserPassword;
import org.mosip.registration.entity.RegistrationUserPasswordID;
import org.mosip.registration.entity.RegistrationUserRole;
import org.mosip.registration.entity.RegistrationUserRoleID;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.repositories.RegistrationAppLoginRepository;
import org.mosip.registration.repositories.RegistrationCenterRepository;
import org.mosip.registration.repositories.RegistrationUserDetailRepository;
import org.mosip.registration.repositories.RegistrationUserPasswordRepository;
import org.mosip.registration.repositories.RegistrationUserRoleRepository;
import org.mosip.registration.service.LoginServiceImpl;
import org.mosip.registration.util.restclient.ServiceDelegateUtil;
import org.springframework.test.util.ReflectionTestUtils;

public class LoginWithOTPtest {
	@Mock
	ServiceDelegateUtil serviceDelegateUtil;
	
	@Mock
	MosipLogger logger;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	LoginServiceImpl loginServiceImpl;
	
	@Mock
	RegistrationAppLoginRepository registrationAppLoginRepository;
	
	@Mock
	RegistrationAppLoginDAO registrationAppLoginDAO;
	
	@Mock
	RegistrationUserPasswordRepository registrationUserPasswordRepository;
	
	@Mock
	RegistrationUserPasswordDAO registrationUserPasswordDAO;
	
	@Mock
	RegistrationUserDetailRepository registrationUserDetailRepository;
	
	@Mock
	RegistrationUserDetailDAO registrationUserDetailDAO;
	
	@Mock
	RegistrationCenterRepository registrationCenterRepository;
	
	@Mock
	RegistrationCenterDAO registrationCenterDAO; 
	
	@Mock
	RegistrationUserRoleRepository registrationUserRoleRepository;
	
	@Mock
	RegistrationUserRoleDAO registrationUserRoleDAO;

	@Test
	public void getModesOfLoginTest() {
		RegistrationAppLoginMethod registrationAppLoginMethod = new RegistrationAppLoginMethod();
		RegistrationAppLoginMethodID registrationAppLoginMethodID = new RegistrationAppLoginMethodID();
		registrationAppLoginMethodID.setLoginMethod("PWD");
		registrationAppLoginMethod.setMethodSeq(1);
		registrationAppLoginMethod.setRegistrationAppLoginMethodID(registrationAppLoginMethodID);
		List<RegistrationAppLoginMethod> loginList = new ArrayList<RegistrationAppLoginMethod>();
		loginList.add(registrationAppLoginMethod);
		Map<String,Object> modes = new LinkedHashMap<String,Object>();
		
		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueOrderByMethodSeq()).thenReturn(loginList);		
		for(int mode = 0; mode < loginList.size(); mode++) {
			modes.put(""+loginList.get(mode).getMethodSeq(), loginList.get(mode).getRegistrationAppLoginMethodID().getLoginMethod());
		}
		
		loginServiceImpl.getModesOfLogin();
	}
	
	@Test 
	public void validateUserPasswordTest() {
		RegistrationUserPasswordID registrationUserPasswordID = new RegistrationUserPasswordID();
		String password = "mosip";
		byte[] bytePassword = password.getBytes();
		String hashPassword = HMACUtil.digestAsPlainText(HMACUtil.generateHash(bytePassword));
		registrationUserPasswordID.setUsrId("mosip");
		registrationUserPasswordID.setPwd(hashPassword);
		List<RegistrationUserPassword> registrationUserPasswordList = new ArrayList<RegistrationUserPassword>();
		RegistrationUserPassword registrationUserPassword = new RegistrationUserPassword();		
		registrationUserPassword.setRegistrationUserPasswordID(registrationUserPasswordID);
		registrationUserPasswordList.add(registrationUserPassword);
		Mockito.when(registrationUserPasswordRepository.findByRegistrationUserPasswordID(Mockito.anyObject())).thenReturn(registrationUserPasswordList);
		
		loginServiceImpl.validateUserPassword("mosip", "E2E488ECAF91897D71BEAC2589433898414FEEB140837284C690DFC26707B262");
	}
	
	@Test
	public void getUserDetailTest(){
		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setName("Sravya");
		registrationUserDetail.setCntrId("000567");
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);
		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue(Mockito.anyString())).thenReturn(registrationUserDetailList);
		LinkedHashMap<String,String> userDetails = new LinkedHashMap<String,String>();
			if(registrationUserDetailList.size() > 0) {
				userDetails.put("name",registrationUserDetailList.get(0).getName());
				userDetails.put("centerId", registrationUserDetailList.get(0).getCntrId());
			}
		loginServiceImpl.getUserDetail(Mockito.anyString());
	}
	
	@Test
	public void getCenterNameTest() {
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setName("Registration");
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		
		Mockito.when(registrationCenterRepository.findById(Mockito.anyString())).thenReturn(registrationCenterList);
		
		loginServiceImpl.getCenterName(Mockito.anyString());
		
	}
	
	@Test
	public void getRegistrationCenterDetailsTest() {
		RegistrationCenter registrationCenter = new RegistrationCenter();
		
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(registrationCenterRepository.findById(Mockito.anyString())).thenReturn(registrationCenterList);
		
		loginServiceImpl.getRegistrationCenterDetails(Mockito.anyString());
	}
	
	@Test 
	public void getRolesTest() {
		RegistrationUserRole registrationUserRole = new RegistrationUserRole();
		RegistrationUserRoleID registrationUserRoleID = new RegistrationUserRoleID();
		registrationUserRoleID.setUsrId(Mockito.anyString());
		registrationUserRole.setRegistrationUserRoleID(registrationUserRoleID);
		List<RegistrationUserRole> registrationUserRoles = new ArrayList<RegistrationUserRole>();
		registrationUserRoles.add(registrationUserRole);
		Mockito.when(registrationUserRoleRepository.findByRegistrationUserRoleID(registrationUserRoleID)).thenReturn(registrationUserRoles);
		List<String> roles = new ArrayList<String>();
		for(int role = 0; role < registrationUserRoles.size(); role++) {
			roles.add(registrationUserRoles.get(role).getRegistrationUserRoleID().getRoleCode());
		}
		
		loginServiceImpl.getRoles(Mockito.anyString());
	}



	@Test
	public void getOTPSuccessResponseTest() throws ClassNotFoundException, RegBaseCheckedException {
		// LoginServiceImpl loginServiceImpl=new LoginServiceImpl();
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("yash");
		OtpGeneratorResponseDto otpGeneratorResponseDto = new OtpGeneratorResponseDto();
		otpGeneratorResponseDto.setOtp("09876");
		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(OtpGeneratorRequestDto.class)))
				.thenReturn(otpGeneratorResponseDto);

		Assert.assertNotNull(loginServiceImpl.getOTP(otpGeneratorRequestDto.getKey()).getSuccessResponseDTO());
		
	}

	@Test
	public void getOTPFailureResponseTest() throws RegBaseCheckedException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("ya");
		OtpGeneratorResponseDto otpGeneratorResponseDto = null;
		when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(OtpGeneratorRequestDto.class)))
				.thenReturn(otpGeneratorResponseDto);
		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		
		Assert.assertNotNull(loginServiceImpl.getOTP(otpGeneratorRequestDto.getKey()).getErrorResponseDTOs());

	}

	@Test
	public void validateOTPSuccessTest() throws RegBaseCheckedException {
		OtpValidatorResponseDto otpGeneratorRequestDto = new OtpValidatorResponseDto();
		otpGeneratorRequestDto.setOrdMessage("OTP is valid");
		otpGeneratorRequestDto.setstatus("true");
		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(otpGeneratorRequestDto);
		Assert.assertNotNull(loginServiceImpl.validateOTP("yashReddy683", "099887").getSuccessResponseDTO());

	}

	@Test
	public void validateOTPFailureTest() throws RegBaseCheckedException {
		OtpValidatorResponseDto otpGeneratorRequestDto = new OtpValidatorResponseDto();
		otpGeneratorRequestDto.setOrdMessage("OTP is valid");
		otpGeneratorRequestDto.setstatus("false");
		ReflectionTestUtils.setField(loginServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(otpGeneratorRequestDto);

		Assert.assertNotNull(loginServiceImpl.validateOTP("yashReddy683", "099887").getErrorResponseDTOs());

	}

}
