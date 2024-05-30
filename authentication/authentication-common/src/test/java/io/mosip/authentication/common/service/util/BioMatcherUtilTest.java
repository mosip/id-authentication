package io.mosip.authentication.common.service.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biosdk.provider.factory.BioAPIFactory;
import io.mosip.kernel.biosdk.provider.spi.iBioProviderApi;
import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;

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
	
	@InjectMocks
	private BioMatcherUtil bioMatcherUtil;

	Map<String, String> valueMap = new HashMap<>();
	private final String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
	@Before
	public void before() {		
		ReflectionTestUtils.setField(bioMatcherUtil, "bdbProcessedLevel", "Raw");
	}

	@Test
	public void TestmatchValueFinger() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);

		mockBioProvider();
		
		try {
			double matchValue = bioMatcherUtil.match(valueMap, valueMap, properties);
			assertEquals(0, Double.compare(SUCCESS_SCORE, matchValue));
	} catch (IdAuthenticationBusinessException e) {
		assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), e.getErrorCode());
	}
		
	}
	
	@Test
	public void TestmatchValueIris() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.IRIS.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
				try {
			double matchValue = bioMatcherUtil.match(valueMap, valueMap, properties);
			assertEquals(0, Double.compare(SUCCESS_SCORE, matchValue));
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), e.getErrorCode());
		}
	}
	
	
	@Test
	public void TestmatchValueFace() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		mockBioProvider();
		
		try {
			double matchValue = bioMatcherUtil.match(valueMap, valueMap, properties);
			assertEquals(0, Double.compare(SUCCESS_SCORE, matchValue));
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), e.getErrorCode());
		}
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
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		try {
			bioMatcherUtil.match(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void TestmatchValueWithBioErrorBioMatchFailed() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		try {
			bioMatcherUtil.match(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void TestmatchValueWithBioErrorUnknown() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		try {
			bioMatcherUtil.match(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void TestmatchValueWithBioErrorDefault() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		try {
			bioMatcherUtil.match(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), e.getErrorCode());
		}
		
	}

	@Test
	public void TestInvalidMatchValue() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put("Finger", value);
		Map<String, String> invalidMap = new HashMap<>();
		invalidMap.put("Finger", "invalid");
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		try {
			double matchValue = bioMatcherUtil.match(valueMap, invalidMap, properties);
			assertNotEquals("90.0", matchValue);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), e.getErrorCode());
		}
	}
	
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void TestMissingEntityValue() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Map<String, String> invalidMap = new HashMap<>();
		invalidMap.put("invalid", "invalid");
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		
		mockBioProvider();
		
		double matchValue = bioMatcherUtil.match(valueMap, invalidMap, properties);
		assertNotEquals("90.0", matchValue);
	}

	@Test
	public void TestMatchValuereturnsZerowhenreqInfoisINvalid() throws IdAuthenticationBusinessException, BiometricException {
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);

		mockBioProvider(false);
		
		double matchValue = bioMatcherUtil.match(valueMap, valueMap, properties);
		assertEquals(0, Double.compare(0, matchValue));
	}

	@Test
	public void TesInvalidtMatchValuereturnsZero() throws IdAuthenticationBusinessException, BiometricException {
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(BiometricType.FINGER.value()));
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
	public void TestMultipleValues() throws IdAuthenticationBusinessException, BiometricException {
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("Face", value);
		valueMap.put("Finger", value);
		
		HashMap<String, Object> properties = new HashMap<>();
		IdMapping[] idMappings = new IdMapping[0];
		properties.put(IdMapping.class.getSimpleName(), idMappings);
		
		Mockito.when(idInfoFetcher.getTypeForIdName("Face", idMappings)).thenReturn(Optional.of(BiometricType.FACE.value()));
		Mockito.when(idInfoFetcher.getTypeForIdName("Finger", idMappings)).thenReturn(Optional.of(BiometricType.FINGER.value()));
		
		mockBioProvider();
		
		try {
			double matchMultiValue = bioMatcherUtil.match(valueMap, valueMap, properties);
			assertEquals(0, Double.compare(SUCCESS_SCORE, matchMultiValue));
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), e.getErrorCode());
		}
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
