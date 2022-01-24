package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.factory.IDAMappingFactory;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.util.DemoNormalizer;
import io.mosip.kernel.biometrics.constant.BiometricType;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IDAMappingFactory.class,
		IDAMappingConfig.class })
@RunWith(SpringRunner.class)
@Import(EnvUtil.class)
@WebMvcTest
public class IdInfoFetcherImplTest {

	
	@InjectMocks
	IdInfoFetcherImpl idInfoFetcherImpl;
	
	/** The demo normalizer. */
	@Autowired(required = false)
	private DemoNormalizer demoNormalizer;
	
	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;
	
	/** The environment. */
	@Autowired
	private EnvUtil environment;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(idInfoFetcherImpl, "demoNormalizer", demoNormalizer);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", environment);
		EnvUtil.setDefaultTemplateLang("eng");
	}
	
	@Test
	public void testGetDemoNormalizer() {
		assertEquals(demoNormalizer, idInfoFetcherImpl.getDemoNormalizer());
	}
	
	@Test
	public void testGetMappingConfig() {
		assertEquals(idMappingConfig, idInfoFetcherImpl.getMappingConfig());
	}
	
	@Test
	public void testGetAvailableDynamicAttributesNamesNull() {
		RequestDTO request = new RequestDTO();
		IdentityDTO demographics = new IdentityDTO();
		demographics.setAge("23");
		demographics.setEmailId("test@test.com");
		demographics.setPhoneNumber("7363048999");
		demographics.setPostalCode("ABC987");
		request.setDemographics(demographics);
		Set<String> dynamicAttributesNames = idInfoFetcherImpl.getAvailableDynamicAttributesNames(request);
		assertTrue(dynamicAttributesNames.isEmpty());
	}
	
	@Test
	public void testGetAvailableDynamicAttributesNames() {
		RequestDTO request = new RequestDTO();
		IdentityDTO demographics = new IdentityDTO();
		demographics.setAge("23");
		demographics.setEmailId("test@test.com");
		demographics.setPhoneNumber("7363048999");
		demographics.setPostalCode("ABC987");
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO idInfo1 = new IdentityInfoDTO();
		idInfo1.setLanguage("eng");
		idInfo1.setValue("Applicantname");
		idInfoList1.add(idInfo1);
		demographics.setName(idInfoList1);
		List<IdentityInfoDTO> idInfoList2 = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO idInfo2 = new IdentityInfoDTO();
		idInfo1.setLanguage("eng");
		idInfo1.setValue("Tirupati");
		idInfoList2.add(idInfo2);
		demographics.setFullAddress(idInfoList2);
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("something", "random");
		metadata.put("someOtherthing", "nothing");
		demographics.setMetadata(metadata);
		request.setDemographics(demographics);
		Set<String> dynamicAttributesNames = idInfoFetcherImpl.getAvailableDynamicAttributesNames(request);
		assertFalse(dynamicAttributesNames.isEmpty());
	}
	
	@Test
	public void testGetUserPreferredLanguages() {
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<IdentityInfoDTO> idInfoList1 = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO idInfo1 = new IdentityInfoDTO();
		idInfo1.setLanguage("eng");
		idInfo1.setValue("English");
		idInfoList1.add(idInfo1);
		EnvUtil.setUserPrefLangAttrName("Name");
		idInfo.put("Name", idInfoList1);
		List<String> list = idInfoFetcherImpl.getUserPreferredLanguages(idInfo);
		assertFalse(list.isEmpty());
	}
	
	@Test
	public void testGetTypeForIdName() {
		IdMapping[] idMapping = new IdMapping[] {IdaIdMapping.FACE,IdaIdMapping.FINGERPRINT };
		Optional<String> obj = idInfoFetcherImpl.getTypeForIdName("Face", idMapping);
		assertFalse(obj.isEmpty());
	}
	
	@Test
	public void testGetIdentityInfo() {
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		idDTO.setDob("25/11/1990");
		idDTO.setAge("25");
		IdentityInfoDTO idInfoDTOs = new IdentityInfoDTO();
		idInfoDTOs.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTOs.setValue("V");
		List<IdentityInfoDTO> idInfoLists = new ArrayList<>();
		idInfoLists.add(idInfoDTOs);
		idDTO.setDobType(idInfoLists);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		Map<String, List<IdentityInfoDTO>> map = idInfoFetcherImpl.getIdentityInfo(DemoMatchType.DYNAMIC, "Name", reqDTO);
		assertTrue(map.isEmpty());
	}
	
	@Test
	public void testGetTemplatesDefaultLanguageCodes() {
		EnvUtil.setDefaultTemplateLang(null);
		List<String> list = idInfoFetcherImpl.getTemplatesDefaultLanguageCodes();
		assertTrue(list.isEmpty());
	}
	
	@Test
	public void testGetBioAttributeNamesforFinger() {
		Map<String, List<IdentityInfoDTO>> idEntity = new HashMap<> ();
		List<IdentityInfoDTO> idInfoList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO idInfo = new IdentityInfoDTO();
		idInfo.setLanguage("eng");
		idInfo.setValue("FINGER");
		idInfoList.add(idInfo);
		idEntity.put(BiometricType.FINGER.value().toString(), idInfoList);
		List<String> list = ReflectionTestUtils.invokeMethod(idInfoFetcherImpl, "getBioAttributeNames", CbeffDocType.FACE,BioMatchType.MULTI_MODAL,idEntity);
		assertFalse(list.isEmpty());
	}
	
	@Test
	public void testGetBioAttributeNamesforIris() {
		Map<String, List<IdentityInfoDTO>> idEntity = new HashMap<> ();
		List<IdentityInfoDTO> idInfoList = new ArrayList<IdentityInfoDTO>();
		IdentityInfoDTO idInfo = new IdentityInfoDTO();
		idInfo.setLanguage("eng");
		idInfo.setValue("IRIS");
		idInfoList.add(idInfo);
		idEntity.put(BiometricType.IRIS.value().toString(), idInfoList);
		List<String> list = ReflectionTestUtils.invokeMethod(idInfoFetcherImpl, "getBioAttributeNames", CbeffDocType.IRIS,BioMatchType.MULTI_MODAL,idEntity);
		assertFalse(list.isEmpty());
	}
}
