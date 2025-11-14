package io.mosip.authentication.common.service.builder;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.common.service.impl.match.PinMatchType;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;

@RunWith(SpringRunner.class)
@Import(EnvUtil.class)
public class MatchInputBuilderTest {

	@Mock
	private IdInfoFetcher idInfoFetcher;
	
	@Autowired
	private EnvUtil environment;
	
	@Mock
	private MappingConfig mappingConfig;
	
	@InjectMocks
	private MatchInputBuilder matchInputBuilder;
	
	@Before
	public void setup() {
		ReflectionTestUtils.setField(matchInputBuilder, "idInfoFetcher", idInfoFetcher);
		ReflectionTestUtils.setField(matchInputBuilder, "environment", environment);
	}
	
	@Test
	public void buildMatchInputTest_Demo() {
		AuthRequestDTO authRequestDto = new AuthRequestDTO();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("fra");
		identityInfoDTO1.setValue("Mamta");
		IdentityInfoDTO identityInfoDTO2 = new IdentityInfoDTO();
		identityInfoDTO2.setLanguage("eng");
		identityInfoDTO2.setValue("Mamta");
		
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		nameList.add(identityInfoDTO1);
		nameList.add(identityInfoDTO2);
		
		IdentityDTO demographics = new IdentityDTO();
		demographics.setName(nameList);
		
		Map<String, List<String>> demoMetadata = new HashMap<>();
		demoMetadata.put("residenceStatus", List.of("residenceStatus"));
		
		Map<String, Object> demoMetadata1 = new HashMap<>();
		demoMetadata1.put("residenceStatus",nameList);
		demographics.setMetadata(demoMetadata1);
		
		RequestDTO request = new RequestDTO();
		request.setDemographics(demographics);
		authRequestDto.setRequest(request);
		
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		demoEntity.put("residenceStatus", nameList);
		
		List<String> languages = new ArrayList<String>();
		languages.add("eng");
		languages.add("fra");
		
		Set<String> set = new HashSet<String>();
		set.add("residenceStatus");
		
		Map<String, String> map = new HashMap<>();
		map.put("test", "test");
		
		Mockito.when(idInfoFetcher.getSystemSupportedLanguageCodes()).thenReturn(languages);
		Mockito.when(idInfoFetcher.getMappingConfig()).thenReturn(mappingConfig);
		Mockito.when(mappingConfig.getDynamicAttributes()).thenReturn(demoMetadata);
		Mockito.when(idInfoFetcher.getIdentityInfo(DemoMatchType.DYNAMIC, "residenceStatus",authRequestDto.getRequest())).thenReturn(demoEntity);
		Mockito.when(idInfoFetcher.getMappingConfig().getDynamicAttributes()).thenReturn(demoMetadata);
		Mockito.when(idInfoFetcher.getAvailableDynamicAttributesNames(authRequestDto.getRequest())).thenReturn(set);
		Mockito.when(idInfoFetcher.getIdentityRequestInfo(DemoMatchType.GENDER, request, "fra")).thenReturn(map);
		assertEquals(1, matchInputBuilder.buildMatchInput(authRequestDto, DemoAuthType.values(), DemoMatchType.values(), demoEntity).size());
	}
	
	@Test
	public void buildMatchInputTest_Bio() {
		AuthRequestDTO authRequestDto = new AuthRequestDTO();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("fra");
		identityInfoDTO1.setValue("Mamta");
		IdentityInfoDTO identityInfoDTO2 = new IdentityInfoDTO();
		identityInfoDTO2.setLanguage("eng");
		identityInfoDTO2.setValue("Mamta");
		
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		nameList.add(identityInfoDTO1);
		nameList.add(identityInfoDTO2);
		
		IdentityDTO demographics = new IdentityDTO();
		demographics.setName(nameList);
		
		RequestDTO request = new RequestDTO();
		request.setDemographics(demographics);
		authRequestDto.setRequest(request);
		
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		demoEntity.put("fullName", nameList);
		
		List<String> languages = new ArrayList<String>();
		languages.add("eng");
		languages.add("fra");
		Mockito.when(idInfoFetcher.getSystemSupportedLanguageCodes()).thenReturn(languages);
		matchInputBuilder.buildMatchInput(authRequestDto, BioAuthType.values(), BioMatchType.values(), demoEntity);
	}
	
	@Test
	public void buildMatchInputTest_Pin() {
		AuthRequestDTO authRequestDto = new AuthRequestDTO();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("fra");
		identityInfoDTO1.setValue("Mamta");
		IdentityInfoDTO identityInfoDTO2 = new IdentityInfoDTO();
		identityInfoDTO2.setLanguage("eng");
		identityInfoDTO2.setValue("Mamta");
		
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		nameList.add(identityInfoDTO1);
		nameList.add(identityInfoDTO2);
		
		IdentityDTO demographics = new IdentityDTO();
		demographics.setName(nameList);
		
		RequestDTO request = new RequestDTO();
		request.setDemographics(demographics);
		authRequestDto.setRequest(request);
		
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		demoEntity.put("fullName", nameList);
		
		List<String> languages = new ArrayList<String>();
		languages.add("eng");
		languages.add("fra");
		Mockito.when(idInfoFetcher.getSystemSupportedLanguageCodes()).thenReturn(languages);
		matchInputBuilder.buildMatchInput(authRequestDto, PinAuthType.values(), PinMatchType.values(), demoEntity);
	}
	
	//io.mosip.authentication.core.exception.IdAuthUncheckedException: IDA-DEA-002 --> Unsupported Language Code - fra for attribute residenceStatus
	@Test(expected = IdAuthUncheckedException.class)
	public void checkIdentityInfoLanguageTest1() {
		AuthRequestDTO authRequestDto = new AuthRequestDTO();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("eng");
		identityInfoDTO1.setValue("Mamta");
		
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		nameList.add(identityInfoDTO1);
		
		IdentityDTO demographics = new IdentityDTO();
		demographics.setName(nameList);
		
		Map<String, List<String>> demoMetadata = new HashMap<>();
		demoMetadata.put("residenceStatus", List.of("residenceStatus"));
		
		Map<String, Object> demoMetadata1 = new HashMap<>();
		demoMetadata1.put("residenceStatus",nameList);
		demographics.setMetadata(demoMetadata1);
		
		RequestDTO request = new RequestDTO();
		request.setDemographics(demographics);
		authRequestDto.setRequest(request);
		
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		demoEntity.put("residenceStatus", nameList);
		
		List<String> languages = new ArrayList<String>();
		Mockito.when(idInfoFetcher.getSystemSupportedLanguageCodes()).thenReturn(languages);
		Mockito.when(idInfoFetcher.getMappingConfig()).thenReturn(mappingConfig);
		Mockito.when(mappingConfig.getDynamicAttributes()).thenReturn(demoMetadata);
		Mockito.when(idInfoFetcher.getIdentityInfo(DemoMatchType.DYNAMIC, "residenceStatus",authRequestDto.getRequest())).thenReturn(demoEntity);
		Mockito.when(idInfoFetcher.getMappingConfig().getDynamicAttributes()).thenReturn(demoMetadata);
		matchInputBuilder.buildMatchInput(authRequestDto, DemoAuthType.values(), DemoMatchType.values(), demoEntity);
	}
	
	//io.mosip.authentication.core.exception.IdAuthUncheckedException: IDA-MLC-006 --> Missing Input Parameter - residenceStatus: language
	@Test(expected = IdAuthUncheckedException.class)
	public void checkIdentityInfoLanguageTest2() {
		AuthRequestDTO authRequestDto = new AuthRequestDTO();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("eng");
		identityInfoDTO1.setValue("mamta");
		
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		nameList.add(identityInfoDTO1);
		
		List<IdentityInfoDTO> nameList1 = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO2 = new IdentityInfoDTO();
		identityInfoDTO2.setLanguage(null);
		identityInfoDTO2.setValue(null);
		nameList1.add(identityInfoDTO2);
		
		IdentityDTO demographics = new IdentityDTO();
		demographics.setName(nameList);
		
		Map<String, List<String>> demoMetadata = new HashMap<>();
		demoMetadata.put("residenceStatus", List.of("residenceStatus"));
		
		Map<String, Object> demoMetadata1 = new HashMap<>();
		demoMetadata1.put("residenceStatus",nameList);
		demographics.setMetadata(demoMetadata1);
		
		RequestDTO request = new RequestDTO();
		request.setDemographics(demographics);
		authRequestDto.setRequest(request);
		
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		demoEntity.put("residenceStatus", nameList);
		
		Map<String, List<IdentityInfoDTO>> demoEntity1 = new HashMap<>();
		demoEntity1.put("residenceStatus", nameList1);
		
		List<String> languages = new ArrayList<String>();
		languages.add("eng");
		languages.add("fra");
		Mockito.when(idInfoFetcher.getSystemSupportedLanguageCodes()).thenReturn(languages);
		Mockito.when(idInfoFetcher.getMappingConfig()).thenReturn(mappingConfig);
		Mockito.when(mappingConfig.getDynamicAttributes()).thenReturn(demoMetadata);
		Mockito.when(idInfoFetcher.getIdentityInfo(DemoMatchType.DYNAMIC, "residenceStatus",authRequestDto.getRequest())).thenReturn(demoEntity1);
		Mockito.when(idInfoFetcher.getMappingConfig().getDynamicAttributes()).thenReturn(demoMetadata);
		matchInputBuilder.buildMatchInput(authRequestDto, DemoAuthType.values(), DemoMatchType.values(), demoEntity);
	}

	//io.mosip.authentication.core.exception.IdAuthUncheckedException: IDA-MLC-009 --> Invalid Input Parameter - residenceStatus: language
	@Test(expected = IdAuthUncheckedException.class)
	public void checkIdentityInfoLanguageTest3() {
		AuthRequestDTO authRequestDto = new AuthRequestDTO();
		IdentityInfoDTO identityInfoDTO1 = new IdentityInfoDTO();
		identityInfoDTO1.setLanguage("eng");
		identityInfoDTO1.setValue("mamta");
		
		List<IdentityInfoDTO> nameList = new ArrayList<>();
		nameList.add(identityInfoDTO1);
		
		List<IdentityInfoDTO> nameList1 = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO2 = new IdentityInfoDTO();
		identityInfoDTO2.setLanguage("");
		identityInfoDTO2.setValue(null);
		nameList1.add(identityInfoDTO2);
		
		IdentityDTO demographics = new IdentityDTO();
		demographics.setName(nameList);
		
		Map<String, List<String>> demoMetadata = new HashMap<>();
		demoMetadata.put("residenceStatus", List.of("residenceStatus"));
		
		Map<String, Object> demoMetadata1 = new HashMap<>();
		demoMetadata1.put("residenceStatus",nameList);
		demographics.setMetadata(demoMetadata1);
		
		RequestDTO request = new RequestDTO();
		request.setDemographics(demographics);
		authRequestDto.setRequest(request);
		
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		demoEntity.put("residenceStatus", nameList);
		
		Map<String, List<IdentityInfoDTO>> demoEntity1 = new HashMap<>();
		demoEntity1.put("residenceStatus", nameList1);
		
		List<String> languages = new ArrayList<String>();
		languages.add("eng");
		languages.add("fra");
		
		Mockito.when(idInfoFetcher.getSystemSupportedLanguageCodes()).thenReturn(languages);
		Mockito.when(idInfoFetcher.getMappingConfig()).thenReturn(mappingConfig);
		Mockito.when(mappingConfig.getDynamicAttributes()).thenReturn(demoMetadata);
		Mockito.when(idInfoFetcher.getIdentityInfo(DemoMatchType.DYNAMIC, "residenceStatus",authRequestDto.getRequest())).thenReturn(demoEntity1);
		Mockito.when(idInfoFetcher.getMappingConfig().getDynamicAttributes()).thenReturn(demoMetadata);
		matchInputBuilder.buildMatchInput(authRequestDto, DemoAuthType.values(), DemoMatchType.values(), demoEntity);
	}
}
