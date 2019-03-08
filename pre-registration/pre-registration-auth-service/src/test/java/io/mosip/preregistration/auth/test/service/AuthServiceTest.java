package io.mosip.preregistration.auth.test.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.preregistration.auth.dto.MainRequestDTO;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.dto.OtpUserDTO;
import io.mosip.preregistration.auth.util.AuthCommonUtil;
import io.mosip.preregistration.core.common.dto.AuthNResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AuthCommonUtil.class)
public class AuthServiceTest {

	private MainResponseDTO<AuthNResponse> mainResponseDTO;
	private MainRequestDTO<OtpUserDTO> mainRequestDTO;
	@Before
	public void setUp() {
		 mainResponseDTO=new MainResponseDTO<>();
		 PowerMockito.mockStatic(AuthCommonUtil.class);
	}
	@Test
	public void sendOtpTest() {
		//Mockito.when(AuthCommonUtil.getMainResponseDto(mainRequestDTO)).thenReturn(mainResponseDTO);
	}
}
