package io.mosip.registration.processor.rest.client.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.rest.client.regcentermachine.builder.RegCenterMachineHistoryClientBuilder;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.MachineHistoryDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.MachineHistoryResponseDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterResponseDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterUserMachineMappingHistoryDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterUserMachineMappingHistoryResponseDto;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class UCMClientTest {
	@InjectMocks
	RegCenterMachineHistoryClientBuilder regCenterMachineHistoryClientBuilder;
	MachineHistoryResponseDto mhrepdto;
	RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto;
	RegistrationCenterResponseDto regrepdto;
	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;
	@Before
	public void setUp() {
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setLongitude("80.24492");
		rcdto.setLatitude("13.0049");
		rcdto.setId("12245");
		
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		 regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		mcdto.setId("yyeqy26356");
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		 offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
	}
	@Test
	public void getCenterSuccessTest() throws ApisResourceAccessException {

		
		Mockito.when(registrationProcessorRestService.getApi(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(regrepdto);
		RegistrationCenterResponseDto resultDto =  regCenterMachineHistoryClientBuilder.getRegistrationCentersHistory("12245","eng","2018-11-28 15:34:20");
		assertEquals(true, resultDto.getRegistrationCenters().get(0).getIsActive());
	}
	
	@Test(expected=ApisResourceAccessException.class)
	public void getCenterExceptionTest() throws ApisResourceAccessException {

		
		Mockito.when(registrationProcessorRestService.getApi(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(ApisResourceAccessException.class);
	    regCenterMachineHistoryClientBuilder.getRegistrationCentersHistory("12245","eng","2018-11-28 15:34:20");
		
	}
	
	@Test
	public void getMachineSuccessTest() throws ApisResourceAccessException {

		
		Mockito.when(registrationProcessorRestService.getApi(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mhrepdto);
		MachineHistoryResponseDto resultDto =  regCenterMachineHistoryClientBuilder.getMachineHistoryIdLangEff("12245","eng","2018-11-28 15:34:20");
		assertEquals(true, resultDto.getMachineHistoryDetails().get(0).getIsActive());
	}
	
	@Test(expected=ApisResourceAccessException.class)
	public void getMachineExceptionTest() throws ApisResourceAccessException {

		
		Mockito.when(registrationProcessorRestService.getApi(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(ApisResourceAccessException.class);
		regCenterMachineHistoryClientBuilder.getMachineHistoryIdLangEff("12245","eng","2018-11-28 15:34:20");
		
	}
	
	@Test
	public void getUCMSuccessTest() throws ApisResourceAccessException {

		
		Mockito.when(registrationProcessorRestService.getApi(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(offrepdto);
		RegistrationCenterUserMachineMappingHistoryResponseDto resultDto =  regCenterMachineHistoryClientBuilder.getRegistrationCentersMachineUserMapping("2018-11-28 15:34:20", "12245", "12245", "dqwFE45");
		assertEquals(true, resultDto.getRegistrationCenters().get(0).getIsActive());
	}
	
	@Test(expected=ApisResourceAccessException.class)
	public void getUCMExceptionTest() throws ApisResourceAccessException {

		
		Mockito.when(registrationProcessorRestService.getApi(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(ApisResourceAccessException.class);
		regCenterMachineHistoryClientBuilder.getRegistrationCentersMachineUserMapping("2018-11-28 15:34:20", "12245", "12245", "dqwFE45");
		
	}
}
