package io.mosip.authentication.common.service.impl.bioauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class,  })
public class BioMatcherIntegratorV1Test {

	@InjectMocks
	private BioMatcherIntegratorV1 bioMatcherUtil;

	@InjectMocks
	private BioApiImpl bioApiImpl;
	
	@Mock
	IBioApi bioApi;
	
	@Mock
	private IdInfoFetcher idInfoFetcher;

	Map<String, String> valueMap = new HashMap<>();
	private final String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
	@Before
	public void before() {
		ReflectionTestUtils.setField(bioMatcherUtil, "fingerApi", bioApi);
		ReflectionTestUtils.setField(bioMatcherUtil, "faceApi", bioApi);
		ReflectionTestUtils.setField(bioMatcherUtil, "irisApi", bioApi);
		ReflectionTestUtils.setField(bioMatcherUtil, "compositeBiometricApi", bioApi);
		ReflectionTestUtils.setField(bioMatcherUtil, "idInfoFetcher", idInfoFetcher);
		valueMap.put(CbeffConstant.class.getName(), String.valueOf(CbeffConstant.FORMAT_TYPE_FINGER));
	}

	@Test
	public void TestmatchValueFinger() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Score score = new Score();
		score.setScaleScore(90);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap, properties);
		assertEquals(0, Double.compare(90.0, matchValue));
	}
	
	@Test
	public void TestmatchValueIris() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Score score = new Score();
		score.setScaleScore(90);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.IRIS.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap, properties);
		assertEquals(0, Double.compare(90.0, matchValue));
	}
	
	@Test
	public void TestmatchValueFace() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Score score = new Score();
		score.setScaleScore(90);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap, properties);
		assertEquals(0, Double.compare(90.0, matchValue));
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void TestmatchValueInvalidType() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Score score = new Score();
		score.setScaleScore(90);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of("ABC"));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		bioMatcherUtil.matchValue(valueMap, valueMap, properties);
	}
	

	@Test
	public void TestmatchValueWithBioErrorQltyChkFailed() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Score score = new Score();
		score.setScaleScore(90);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any()))
			.thenThrow(new BiometricException("KER-BIO-003","aaa"));
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		try {
			bioMatcherUtil.matchValue(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.QUALITY_CHECK_FAILED.getErrorCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void TestmatchValueWithBioErrorBioMatchFailed() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Score score = new Score();
		score.setScaleScore(90);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any()))
			.thenThrow(new BiometricException("KER-BIO-004","aaa"));
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		try {
			bioMatcherUtil.matchValue(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.BIO_MATCH_FAILED_TO_PERFORM.getErrorCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void TestmatchValueWithBioErrorUnknown() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Score score = new Score();
		score.setScaleScore(90);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any()))
			.thenThrow(new BiometricException("KER-BIO-005","aaa"));
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		try {
			bioMatcherUtil.matchValue(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS_BIO.getErrorCode(), e.getErrorCode());
		}
	}
	
	@Test
	public void TestmatchValueWithBioErrorDefault() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Score score = new Score();
		score.setScaleScore(90);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any()))
			.thenThrow(new BiometricException("KER-BIO-XXX","aaa"));
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FACE.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		try {
			bioMatcherUtil.matchValue(valueMap, valueMap, properties);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS_BIO.getErrorCode(), e.getErrorCode());
		}
	}

	@Test
	public void TestInvalidMatchValue() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Map<String, String> invalidMap = new HashMap<>();
		invalidMap.put("invalid", "invalid");
		Score score = new Score();
		score.setInternalScore(0);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		double matchValue = bioMatcherUtil.matchValue(valueMap, invalidMap, properties);
		assertNotEquals("90.0", matchValue);
	}

	@Test
	public void TestMatchValuereturnsZerowhenreqInfoisINvalid() throws IdAuthenticationBusinessException, BiometricException {
		Score score = new Score();
		score.setInternalScore(0);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap, properties);
		assertEquals(0, Double.compare(0, matchValue));
	}

	@Test
	public void TesInvalidtMatchValuereturnsZero() throws IdAuthenticationBusinessException, BiometricException {
		Score score = new Score();
		score.setInternalScore(0);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(bioApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		Mockito.when(idInfoFetcher.getTypeForIdName(Mockito.anyString(), Mockito.any())).thenReturn(Optional.of(SingleType.FINGER.value()));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(IdMapping.class.getSimpleName(), new IdMapping[0]);
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap, properties);
		
		assertEquals(0, (int)matchValue);
	}

	@Test
	public void TestMultipleValues() throws IdAuthenticationBusinessException, BiometricException {
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("", value);
		valueMap.put(CbeffConstant.class.getName(), String.valueOf(CbeffConstant.FORMAT_TYPE_FINGER));
		Score score = new Score();
		score.setInternalScore(0);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		CompositeScore compScore = new CompositeScore();
		compScore.setScaledScore(90);
		compScore.setIndividualScores(scores);
		Mockito.when(bioApi.compositeMatch(Mockito.any(BIR[].class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(compScore);
		double matchMultiValue = bioMatcherUtil.matchMultiValue(valueMap, valueMap, Collections.emptyMap());
		assertEquals(0, Double.compare(90.0, matchMultiValue));
	}

	@Test
	public void TestInvaidMultipleValues() throws IdAuthenticationBusinessException, BiometricException {
		Map<String, String> reqInfo = new HashMap<>();
		reqInfo.put("", value);
		reqInfo.put(CbeffConstant.class.getName(), String.valueOf(CbeffConstant.FORMAT_TYPE_FINGER));
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put("", "Invalid");
		Score score = new Score();
		score.setInternalScore(0);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		CompositeScore compScore = new CompositeScore();
		compScore.setInternalScore(0);
		compScore.setIndividualScores(scores);
		Mockito.when(bioApi.compositeMatch(Mockito.any(BIR[].class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(compScore);
		double matchMultiValue = bioMatcherUtil.matchMultiValue(reqInfo, entityInfo, Collections.emptyMap());
		assertNotEquals("90.0", matchMultiValue);
	}

	@Test
	public void TestUnknownValues() throws IdAuthenticationBusinessException, BiometricException {
		Map<String, String> reqInfo = new HashMap<>();
		reqInfo.put(IdAuthCommonConstants.UNKNOWN_BIO, value);
		reqInfo.put(CbeffConstant.class.getName(), String.valueOf(CbeffConstant.FORMAT_TYPE_FINGER));
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put(IdAuthCommonConstants.UNKNOWN_BIO, value);
		Score score = new Score();
		score.setInternalScore(0);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		CompositeScore compScore = new CompositeScore();
		compScore.setInternalScore(0);
		compScore.setIndividualScores(scores);
		Mockito.when(bioApi.compositeMatch(Mockito.any(BIR[].class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(compScore);
		double matchMultiValue = bioMatcherUtil.matchMultiValue(reqInfo, entityInfo, Collections.emptyMap());
		assertNotEquals("90.0", matchMultiValue);

	}

}
