package io.mosip.registration.device;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.device.GPSUtill.GPSPosition;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * @author M1048290 GPSUtillTest.java 2018
 */
@Ignore
public class GPSUtillTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private Logger logger;

	@InjectMocks
	private GPSUtill gpsUtillMock;

	@Mock
	private AuditFactory auditFactory;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
	}

	@Before
	public void initialize() throws IOException, URISyntaxException {

		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

	}

	/**
	 * Test method for
	 * {@link io.mosip.registration.device.GPSUtill#parse(java.lang.String)}.
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test
	public void gpsParseTestSucess() throws RegBaseCheckedException {

		String line = "$GPRMC,055218.000,A,1259.4845,N,08014.7602,E,0.07,120.70,171018,,,A*64";

		GPSPosition gpsVale = gpsUtillMock.parse(line);
		assertTrue(gpsVale.getResponse().equals("success"));

	}

	/**
	 * Test method for
	 * {@link io.mosip.registration.device.GPSUtill#parse(java.lang.String)}.
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test
	public void gpsParseTestFailure() throws RegBaseCheckedException {

		String line = "$GPGGA,055218.000,A,1259.4845,N,08014.7602,E,0.07,120.70,171018,,,A*64";

		GPSPosition gpsVale = gpsUtillMock.parse(line);
		assertTrue(gpsVale.getLat() == 0.0 && gpsVale.getLon() == 0.0 && gpsVale.getResponse().equals("failure"));

	}

	/**
	 * Test method for
	 * {@link io.mosip.registration.device.GPSUtill#parse(java.lang.String)}.
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test
	public void gpsParseTestWeekSingal() throws RegBaseCheckedException {

		String line = "$GPRMC,055218.000,V,1259.4845,N,08014.7602,E,0.07,120.70,171018,,,A*64";

		GPSPosition gpsVale = gpsUtillMock.parse(line);
		assertTrue(gpsVale.getLat() == 0.0 && gpsVale.getLon() == 0.0 && gpsVale.getResponse().equals("failure"));

	}
}
