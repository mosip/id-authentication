package io.mosip.authentication.common.service.impl.indauth.service.bio;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.factory.BiometricProviderFactory;
import io.mosip.authentication.core.dto.indauth.DataDTO;
import io.mosip.authentication.core.spi.provider.bio.CogentFingerprintProvider;
import io.mosip.authentication.core.spi.provider.bio.CogentIrisProvider;
import io.mosip.authentication.core.spi.provider.bio.MantraFingerprintProvider;
import io.mosip.authentication.core.spi.provider.bio.MorphoIrisProvider;

/**
 * The Class BioMetricProviderFactoryTest.
 * @author Arun Bose S
 */
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class BioMetricProviderFactoryTest {
	
	@Autowired
	Environment environment;
	
	/** The bio metric provider factory. */
	@InjectMocks
	private BiometricProviderFactory bioMetricProviderFactory;
	
	@Before
	public void setup() {
		bioMetricProviderFactory.initProviders();
		ReflectionTestUtils.setField(bioMetricProviderFactory, "environment", environment);
	}
	
	/**
	 * Bio factory test.
	 */
	@Test
	public void bioFactoryTest() {
		bioMetricProviderFactory.initProviders();
	}
	
	@Test
	public void getBiometricProviderTest() {
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("IIR");
		bioInfo.setDeviceProviderID("cogent");
		assertTrue(bioMetricProviderFactory.getBiometricProvider(bioInfo) instanceof CogentIrisProvider);
	}
	
	@Test
	public void getBiometricProviderTest2() {
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("IIR");
		bioInfo.setDeviceProviderID("morpho");
		assertTrue(bioMetricProviderFactory.getBiometricProvider(bioInfo) instanceof MorphoIrisProvider);
	}
	
	@Test
	public void getBiometricProviderTest3() {
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("IIR");
		bioInfo.setDeviceProviderID("empty");
		assertTrue(bioMetricProviderFactory.getBiometricProvider(bioInfo) instanceof CogentIrisProvider);
	}
	
	@Test
	public void getBiometricProviderTest4() {
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("FMR");
		bioInfo.setDeviceProviderID("cogent");
		assertTrue(bioMetricProviderFactory.getBiometricProvider(bioInfo) instanceof CogentFingerprintProvider);
	}
	
	@Test
	public void getBiometricProviderTest5() {
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("FIR");
		bioInfo.setDeviceProviderID("cogent");
		assertTrue(bioMetricProviderFactory.getBiometricProvider(bioInfo) instanceof CogentFingerprintProvider);
	}
	
	@Test
	public void getBiometricProviderTest6() {
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("FIR");
		bioInfo.setDeviceProviderID("mantra");
		assertTrue(bioMetricProviderFactory.getBiometricProvider(bioInfo) instanceof MantraFingerprintProvider);
	}
	
	@Test
	public void getBiometricProviderTest7() {
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("FMR");
		bioInfo.setDeviceProviderID("mantra");
		assertTrue(bioMetricProviderFactory.getBiometricProvider(bioInfo) instanceof MantraFingerprintProvider);
	}
	
	@Test
	public void getBiometricProviderTest8() {
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("FIR");
		bioInfo.setDeviceProviderID("empty");
		assertTrue(bioMetricProviderFactory.getBiometricProvider(bioInfo) instanceof MantraFingerprintProvider);
	}
	
	@Test
	public void getBiometricProviderTest9() {
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("FMR");
		bioInfo.setDeviceProviderID("empty");
		assertTrue(bioMetricProviderFactory.getBiometricProvider(bioInfo) instanceof MantraFingerprintProvider);
	}
	
	@Test
	public void getBiometricProviderTest10() {
		DataDTO bioInfo = new DataDTO();
		bioInfo.setBioType("none");
		bioInfo.setDeviceProviderID("empty");
		assertNull(bioMetricProviderFactory.getBiometricProvider(bioInfo));
	}

}
