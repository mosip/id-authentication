package io.mosip.registration.processor.stages.umcvalidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.regcentermachine.builder.RegCenterMachineHistoryClientBuilder;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.MachineHistoryDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.MachineHistoryResponseDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterResponseDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterUserMachineMappingHistoryDto;
import io.mosip.registration.processor.rest.client.regcentermachine.dto.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.registration.processor.stages.osivalidator.UMCValidator;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

@RunWith(PowerMockRunner.class)
public class UMCValidatorTest {
	@InjectMocks
	UMCValidator umcValidator;
	
	@Mock
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	
	
	@Mock
	RegCenterMachineHistoryClientBuilder umcClient;
	RegistrationCenterMachineDto rcmDto;
	RegOsiDto regOsi;
	@Before
	public void setUp() {
		InternalRegistrationStatusDto registrationStatusDto= new InternalRegistrationStatusDto();
		umcValidator.setRegistrationStatusDto(registrationStatusDto);
		rcmDto=new RegistrationCenterMachineDto();
		regOsi=new RegOsiDto();
		rcmDto.setIsActive(true);
		rcmDto.setLatitude("13.0049");
		rcmDto.setLongitude("80.24492");
		rcmDto.setMachineId("yyeqy26356");
		rcmDto.setPacketCreationDate(LocalDateTime.parse("2018-11-28T15:34:20"));
		rcmDto.setRegcntrId("12245");
		rcmDto.setRegId("2018782130000121112018103016");
		 
		regOsi.setOfficerId("O1234");
		regOsi.setOfficerFingerpImageName("fingerprint");
		regOsi.setOfficerfingerType("RIGHTLITTLE");
		regOsi.setOfficerIrisImageName(null);
		regOsi.setOfficerIrisType("LEFTEYE");
		regOsi.setOfficerPhotoName(null);
		regOsi.setOfficerHashedPin("officerHashedPin");

		regOsi.setSupervisorId("S1234");
		regOsi.setSupervisorFingerpImageName("supervisorFingerpImageName");
		regOsi.setSupervisorFingerType("LEFTINDEX");
		regOsi.setSupervisorIrisImageName("supervisorIrisImageName");
		regOsi.setSupervisorIrisType("LEFTEYE");
		regOsi.setSupervisorPhotoName("supervisorPhotoName");
		
		
		Mockito.when(packetInfoManager.getOsi(anyString())).thenReturn(regOsi);
		Mockito.when(packetInfoManager.getRegistrationCenterMachine(anyString())).thenReturn(rcmDto);
		 
		
	}
	
	@Test
	public void isValidUMCSuccessTest() throws ApisResourceAccessException {
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setLongitude("80.24492");
		rcdto.setLatitude("13.0049");
		rcdto.setId("12245");
		
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		mcdto.setId("yyeqy26356");
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		
		assertTrue(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	
	@Test
	public void UMCMappingNotFoundTest() throws ApisResourceAccessException {
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setLongitude("80.24492");
		rcdto.setLatitude("13.0049");
		rcdto.setId("12245");
		
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		mcdto.setId("yyeqy26356");
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	@Test
	public void UMCMappingNotActiveTest() throws ApisResourceAccessException {
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setLongitude("80.24492");
		rcdto.setLatitude("13.0049");
		rcdto.setId("12245");
		
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		mcdto.setId("yyeqy26356");
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(false);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	
	@Test
	public void machineIdNotFoundTest() throws ApisResourceAccessException {
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setLongitude("80.24492");
		rcdto.setLatitude("13.0049");
		rcdto.setId("12245");
		
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	
	@Test
	public void machinesNotFoundTest() throws ApisResourceAccessException {
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setLongitude("80.24492");
		rcdto.setLatitude("13.0049");
		rcdto.setId("12245");
		
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	
	
	@Test
	public void machineNotActiveTest() throws ApisResourceAccessException {
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setLongitude("80.24492");
		rcdto.setLatitude("13.0049");
		rcdto.setId("12245");
		
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(false);
		mcdto.setId("yyeqy26356");
		
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	@Test
	public void gpsDatanotPresentInMasterTest() throws ApisResourceAccessException {
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setId("12245");
		
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		mcdto.setId("yyeqy26356");
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	
	@Test
	public void WronggpsDataPresentInMasterTest() throws ApisResourceAccessException {
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setId("12245");
		rcdto.setLongitude("80.21492");
		rcdto.setLatitude("13.10049");
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		mcdto.setId("yyeqy26356");
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	
	@Test
	public void gpsDatanotPresentInPacketTest() throws ApisResourceAccessException {
		RegistrationCenterMachineDto rcmDto=new RegistrationCenterMachineDto();
		rcmDto.setIsActive(true);
		rcmDto.setLatitude("13.0049");
		rcmDto.setLongitude("");
		rcmDto.setMachineId(" ");
		rcmDto.setPacketCreationDate(LocalDateTime.parse("2018-11-28T15:34:20"));
		rcmDto.setRegcntrId("12245");
		rcmDto.setRegId("2018782130000121112018103016");
		Mockito.when(packetInfoManager.getRegistrationCenterMachine(anyString())).thenReturn(rcmDto);
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setId("12245");
		rcdto.setLongitude("80.24492");
		rcdto.setLatitude("13.10049");
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		mcdto.setId("yyeqy26356");
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	
	@Test
	public void noRegistrationCentersFoundInMasterTest() throws ApisResourceAccessException {
		
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		mcdto.setId("yyeqy26356");
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	
	@Test
	public void noRegistrationCenterIdsFoundInMasterTest() throws ApisResourceAccessException {
		
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(true);
		rcdto.setLongitude("80.24492");
		rcdto.setLatitude("13.10049");
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		mcdto.setId("yyeqy26356");
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
	
	@Test
	public void registrationCenternotActiveTest() throws ApisResourceAccessException {
		
		RegistrationCenterDto rcdto=new RegistrationCenterDto();
		rcdto.setIsActive(false);
		rcdto.setLongitude("80.24492");
		rcdto.setLatitude("13.0049");
		List<RegistrationCenterDto> rcdtos=new ArrayList<>();
		rcdtos.add(rcdto);
		
		RegistrationCenterResponseDto regrepdto=new RegistrationCenterResponseDto();
		regrepdto.setRegistrationCenters(rcdtos);
		
		MachineHistoryDto mcdto=new MachineHistoryDto();
		mcdto.setIsActive(true);
		mcdto.setId("yyeqy26356");
		
		List<MachineHistoryDto> mcdtos=new ArrayList<>();
		mcdtos.add(mcdto);
		MachineHistoryResponseDto  mhrepdto=new MachineHistoryResponseDto();
		mhrepdto.setMachineHistoryDetails(mcdtos);
		
		
		
		RegistrationCenterUserMachineMappingHistoryDto officerucmdto=new RegistrationCenterUserMachineMappingHistoryDto();
		officerucmdto.setIsActive(true);
		officerucmdto.setCntrId("12245");
		officerucmdto.setMachineId("yyeqy26356");
		officerucmdto.setUsrId("O1234");
		
		List<RegistrationCenterUserMachineMappingHistoryDto> officerucmdtos=new ArrayList<>();
		officerucmdtos.add(officerucmdto);
		
		
		RegistrationCenterUserMachineMappingHistoryResponseDto offrepdto=new RegistrationCenterUserMachineMappingHistoryResponseDto(officerucmdtos);
		
		Mockito.when(umcClient.getRegistrationCentersHistory(anyString(),anyString(),anyString())).
				thenReturn(regrepdto);
		Mockito.when(umcClient.getMachineHistoryIdLangEff(anyString(),anyString(),anyString())).
		thenReturn(mhrepdto);
		Mockito.when(umcClient.getRegistrationCentersMachineUserMapping(anyString(),anyString(),anyString(),anyString())).
		thenReturn(offrepdto);
		
		
		assertFalse(umcValidator.isValidUMC("2018782130000121112018103016"));
	}
}
