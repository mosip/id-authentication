package io.mosip.authentication.common.service.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.kernel.biosdk.provider.factory.BioAPIFactory;
import io.mosip.kernel.biosdk.provider.spi.iBioProviderApi;
import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class,  })
@SuppressWarnings("unchecked")
public class BioMatcherUtilTest {

	private static final double SUCCESS_SCORE = 100.0;	

	@Mock
	private IdInfoFetcher idInfoFetcher;
	
	@Mock
	private BioAPIFactory bioApiFactory;
	
	@Mock
	private CbeffUtil cbeffUtil;
	
	@InjectMocks
	private BioMatcherUtil bioMatcherUtil;

	Map<String, String> valueMap = new HashMap<>();
	private final String value = "MTIzNDU2Nzg5MHF3ZXJ0eXVpb3Bhc2RmZ2hqa2x6eGN2Ym5tLC5bXXt9Oyc6Ijw+Lz8=";
	
	
	@Before
	public void before() throws Exception {
		//valueMap.put(CbeffConstant.class.getName(), String.valueOf(CbeffConstant.FORMAT_TYPE_FINGER));
		ReflectionTestUtils.setField(bioMatcherUtil, "bdbProcessedLevel", "Raw");
		when(cbeffUtil.getBIRDataFromXML(Mockito.any())).thenReturn(List.of(Mockito.mock(BIRType.class)));
	}
	
	@Test
	public void TestmatchValueFinger() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);

		mockBioProvider();
		
		BioInfo bioInfo = new BioInfo(value, SingleType.FINGER, new String[] {SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.THUMB.value()});
		BIR bir = BioInfo.getBir(value.getBytes(), bioInfo);
		when(cbeffUtil.convertBIRTypeToBIR(Mockito.anyList())).thenReturn(List.of(bir));
		
		double matchValue = bioMatcherUtil.match(valueMap, valueMap, properties);
		assertEquals(0, Double.compare(SUCCESS_SCORE, matchValue));
	}
	
	@Test
	public void TestmatchValueIris() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.IRIS.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		BioInfo bioInfo = new BioInfo(value, SingleType.IRIS, new String[] {SingleAnySubtypeType.RIGHT.value()});
		BIR bir = BioInfo.getBir(value.getBytes(), bioInfo);
		when(cbeffUtil.convertBIRTypeToBIR(Mockito.anyList())).thenReturn(List.of(bir));
		
		double matchValue = bioMatcherUtil.match(valueMap, valueMap, properties);
		assertEquals(0, Double.compare(SUCCESS_SCORE, matchValue));
	}
	
	@Test
	public void TestmatchValueFace() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		mockBioProvider();
		
		BioInfo bioInfo = new BioInfo(value, SingleType.FACE, new String[0] );
		BIR bir = BioInfo.getBir(value.getBytes(), bioInfo);
		when(cbeffUtil.convertBIRTypeToBIR(Mockito.anyList())).thenReturn(List.of(bir));
		
		double matchValue = bioMatcherUtil.match(valueMap, valueMap, properties);
		assertEquals(0, Double.compare(SUCCESS_SCORE, matchValue));
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void TestmatchValueInvalidType() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of("ABC"));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		bioMatcherUtil.match(valueMap, valueMap, properties);
	}
	

	@Test
	public void TestmatchValueWithBioErrorQltyChkFailed() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		BioInfo bioInfo = new BioInfo(value, SingleType.FACE, new String[0]);
		BIR bir = BioInfo.getBir(value.getBytes(), bioInfo);
		when(cbeffUtil.convertBIRTypeToBIR(Mockito.anyList())).thenReturn(List.of(bir));
		
		try {
			bioMatcherUtil.match(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {e.printStackTrace();
			assertEquals(IdAuthenticationErrorConstants.QUALITY_CHECK_FAILED.getErrorCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void TestmatchValueWithBioErrorBioMatchFailed() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		BioInfo bioInfo = new BioInfo(value, SingleType.FACE, new String[0]);
		BIR bir = BioInfo.getBir(value.getBytes(), bioInfo);
		when(cbeffUtil.convertBIRTypeToBIR(Mockito.anyList())).thenReturn(List.of(bir));
		
		try {
			bioMatcherUtil.match(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.BIO_MATCH_FAILED_TO_PERFORM.getErrorCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void TestmatchValueWithBioErrorUnknown() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		BioInfo bioInfo = new BioInfo(value, SingleType.FACE, new String[0]);
		BIR bir = BioInfo.getBir(value.getBytes(), bioInfo);
		when(cbeffUtil.convertBIRTypeToBIR(Mockito.anyList())).thenReturn(List.of(bir));
		
		try {
			bioMatcherUtil.match(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS_BIO.getErrorCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void TestmatchValueWithBioErrorDefault() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		BioInfo bioInfo = new BioInfo(value, SingleType.FACE, new String[0]);
		BIR bir = BioInfo.getBir(value.getBytes(), bioInfo);
		when(cbeffUtil.convertBIRTypeToBIR(Mockito.anyList())).thenReturn(List.of(bir));
		
		bioMatcherUtil.match(valueMap, valueMap, properties);
	}

	@Test
	public void TestInvalidMatchValue() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put("Finger", value);
		Map<String, String> invalidMap = new HashMap<>();
		invalidMap.put("Finger", "invalid");
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		BioInfo bioInfo = new BioInfo(value, SingleType.FINGER, new String[] {SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.THUMB.value()});
		BIR bir = BioInfo.getBir(value.getBytes(), bioInfo);
		when(cbeffUtil.convertBIRTypeToBIR(Mockito.anyList())).thenReturn(List.of(bir));
		
		double matchValue = bioMatcherUtil.match(valueMap, invalidMap, properties);
		assertNotEquals("90.0", matchValue);
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void TestMissingEntityValue() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Map<String, String> invalidMap = new HashMap<>();
		invalidMap.put("invalid", "invalid");
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		double matchValue = bioMatcherUtil.match(valueMap, invalidMap, properties);
		assertNotEquals("90.0", matchValue);
	}

	@Test
	public void TestMatchValuereturnsZerowhenreqInfoisINvalid() throws IdAuthenticationBusinessException, BiometricException {
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);

		mockBioProvider(false);
		
		double matchValue = bioMatcherUtil.match(valueMap, valueMap, properties);
		assertEquals(0, Double.compare(0, matchValue));
	}

	@Test
	public void TesInvalidtMatchValuereturnsZero() throws IdAuthenticationBusinessException, BiometricException {
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider(false);
		
		double matchValue = bioMatcherUtil.match(valueMap, valueMap, properties);
		
		assertEquals(0, (int)matchValue);
	}
	
	private void mockBioProvider() throws BiometricException {
		mockBioProvider(true);
	}

	private void mockBioProvider(boolean match) throws BiometricException {
		iBioProviderApi bioProvider = Mockito.mock(iBioProviderApi.class);
		Mockito.when(bioApiFactory.getBioProvider(Mockito.any(), Mockito.any())).thenReturn(bioProvider);
		Mockito.when(bioProvider.verify(Mockito.any(), Mockito.any(), Mockito.any(), AdditionalMatchers.or(Mockito.any(), Mockito.nullable(Map.class))))
				.thenReturn(match);
	}

	@Test
	public void TestMultipleValues() throws Exception {
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("Face", "Face");
		valueMap.put("Finger", "Finger");
		
		HashMap<String, Object> properties = new HashMap<>();
		IdMapping[] idMappings = new IdMapping[0];
		properties.put(IdMapping.class.getSimpleName(), idMappings);
		
		Mockito.when(idInfoFetcher.getTypeForIdName("Face", idMappings)).thenReturn(Optional.of(SingleType.FACE.value()));
		Mockito.when(idInfoFetcher.getTypeForIdName("Finger", idMappings)).thenReturn(Optional.of(SingleType.FINGER.value()));
		
		mockBioProvider();
		
		List<BIRType> faceBirTypes = List.of(Mockito.mock(BIRType.class));
		when(cbeffUtil.getBIRDataFromXML("Face".getBytes())).thenReturn(faceBirTypes);
		BioInfo bioInfo = new BioInfo(value, SingleType.FACE, new String[0]);
		BIR bir = BioInfo.getBir(value.getBytes(), bioInfo);
		when(cbeffUtil.convertBIRTypeToBIR(faceBirTypes)).thenReturn(List.of(bir));
		
		List<BIRType> fingerBirTypes = List.of(Mockito.mock(BIRType.class));
		when(cbeffUtil.getBIRDataFromXML("Finger".getBytes())).thenReturn(fingerBirTypes);
		BioInfo bioInfo1 = new BioInfo(value, SingleType.FINGER, new String[] {SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.THUMB.value()});
		BIR bir1 = BioInfo.getBir(value.getBytes(), bioInfo1);
		when(cbeffUtil.convertBIRTypeToBIR(fingerBirTypes)).thenReturn(List.of(bir1));
		
		double matchMultiValue = bioMatcherUtil.match(valueMap, valueMap, properties);
		assertEquals(0, Double.compare(SUCCESS_SCORE, matchMultiValue));
	}

	@Test(expected=IdAuthenticationBusinessException.class)
	public void TestInvaidMultipleValues() throws IdAuthenticationBusinessException, BiometricException {
		Map<String, String> reqInfo = new HashMap<>();
		reqInfo.put("", value);
		reqInfo.put(CbeffConstant.class.getName(), String.valueOf(CbeffConstant.FORMAT_TYPE_FINGER));
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put("", "Invalid");
		
		mockBioProvider();
		
		bioMatcherUtil.match(reqInfo, entityInfo, Collections.emptyMap());
	}

	@Test(expected=IdAuthenticationBusinessException.class)
	public void TestUnknownValues() throws IdAuthenticationBusinessException, BiometricException {
		Map<String, String> reqInfo = new HashMap<>();
		reqInfo.put(IdAuthCommonConstants.UNKNOWN_BIO, value);
		reqInfo.put(CbeffConstant.class.getName(), String.valueOf(CbeffConstant.FORMAT_TYPE_FINGER));
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put(IdAuthCommonConstants.UNKNOWN_BIO, value);
		
		mockBioProvider();
		
		bioMatcherUtil.match(reqInfo, entityInfo, Collections.emptyMap());

	}

}
