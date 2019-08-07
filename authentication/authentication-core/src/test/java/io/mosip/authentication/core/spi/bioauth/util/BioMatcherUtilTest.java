package io.mosip.authentication.core.spi.bioauth.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;
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
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.entity.BIR;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class BioMatcherUtilTest {

	@InjectMocks
	private BioMatcherUtil bioMatcherUtil;

	@InjectMocks
	private BioApiImpl bioApiImpl;
	
	@Mock
	IBioApi fingerApi;

	Map<String, String> valueMap = new HashMap<>();
	private final String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
	@Before
	public void before() {
		ReflectionTestUtils.setField(bioMatcherUtil, "fingerApi", fingerApi);
		ReflectionTestUtils.setField(bioMatcherUtil, "faceApi", bioApiImpl);
		ReflectionTestUtils.setField(bioMatcherUtil, "irisApi", bioApiImpl);
		valueMap.put(CbeffConstant.class.getName(), String.valueOf(CbeffConstant.FORMAT_TYPE_FINGER));
	}

	@Test
	public void TestmatchValue() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Score score = new Score();
		score.setInternalScore(90);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(fingerApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap);
		assertEquals(0, Double.compare(90.0, matchValue));
	}

	@Test
	public void TestInvalidMatchValue() throws IdAuthenticationBusinessException, BiometricException {
		valueMap.put(value, value);
		Map<String, String> invalidMap = new HashMap<>();
		invalidMap.put("invalid", "invalid");
		Score score = new Score();
		score.setInternalScore(0);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(fingerApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		double matchValue = bioMatcherUtil.matchValue(valueMap, invalidMap);
		assertNotEquals("90.0", matchValue);
	}

	@Test
	public void TestMatchValuereturnsZerowhenreqInfoisINvalid() throws IdAuthenticationBusinessException, BiometricException {
		Score score = new Score();
		score.setInternalScore(0);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(fingerApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap);
		assertEquals(0, Double.compare(0, matchValue));
	}

	@Test
	public void TesInvalidtMatchValuereturnsZero() throws IdAuthenticationBusinessException, BiometricException {
		Score score = new Score();
		score.setInternalScore(0);
		Score[] scores = Stream.of(score).toArray(Score[]::new);
		Mockito.when(fingerApi.match(Mockito.any(BIR.class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(scores);
		double matchValue = bioMatcherUtil.matchValue(valueMap, valueMap);
		
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
		compScore.setInternalScore(90);
		compScore.setIndividualScores(scores);
		Mockito.when(fingerApi.compositeMatch(Mockito.any(BIR[].class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(compScore);
		double matchMultiValue = bioMatcherUtil.matchMultiValue(valueMap, valueMap);
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
		Mockito.when(fingerApi.compositeMatch(Mockito.any(BIR[].class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(compScore);
		double matchMultiValue = bioMatcherUtil.matchMultiValue(reqInfo, entityInfo);
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
		Mockito.when(fingerApi.compositeMatch(Mockito.any(BIR[].class), Mockito.any(BIR[].class), Mockito.any())).thenReturn(compScore);
		double matchMultiValue = bioMatcherUtil.matchMultiValue(reqInfo, entityInfo);
		assertNotEquals("90.0", matchMultiValue);

	}

}
