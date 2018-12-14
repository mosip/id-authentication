package io.mosip.registration.device;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.device.gps.GPSFacade;
import io.mosip.registration.device.gps.MosipGPSProvider;
import io.mosip.registration.device.gps.impl.GPSBU343Connector;
import io.mosip.registration.device.gps.impl.GPSBU343Connector.GPSPosition;
import io.mosip.registration.exception.RegBaseCheckedException;
@Ignore
public class GPSIntegrationImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private GPSFacade gpsIntegrationImpl;

	@Mock
	private GPSBU343Connector GPSBU343Connector;

	@Mock
	private GPSFacade mosipGpsConnector;

	@Mock
	private MosipGPSProvider mosipGPSProvider;

	@Mock
	private MosipGPSProvider gpsUtill;

	@Mock
	private GPSPosition gpsPosition;

	@Mock
	MosipGPSProvider gpsConnector;

	List<MosipGPSProvider> gpsConnectionsList;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
	}

	@Before
	public void initialize() throws IOException, URISyntaxException {

	}

	/**
	 * Test method for
	 * {@link io.mosip.registration.device.gps.GPSFacade#getLatLongDtls(double, double)}.
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test
	public void testGetLatLongDtlsFailureCase() throws RegBaseCheckedException {

		Map<String, Object> map = new HashMap<>();

		String serialPortConnected = "COM4";
		int portThreadTime = 1000;

		double centerLat = 72.8790;
		double centerLngt = 12.3478;
		String gpsDeviceName = "GPSBU343Connector";

		ReflectionTestUtils.setField(gpsIntegrationImpl, "serialPortConnected", "COM4");

		ReflectionTestUtils.setField(gpsIntegrationImpl, "portThreadTime", 1000);

		String mockgps = "$GPGGA,,,,,,0,00,,,M,0.0,M,,0000*48,$GPGSA,A1,,,,,,,,,,,,,,,*1E\r\n"
				+ "$GPGSV,3,112,01,00,000,,2,00,000,,03,00,000,,04,00,007,*C\r\n"
				+ "$GPGSV,3,2,12,05,00,000,,06,00,000,,07,00,000,,08,00,000,*77$GPGSV,3,3,12,09,00,000,,10,00,000,,11,00,000,,12,00,000,*7,\r\n"
				+ "$GPRMC,V,,,,,,,,,,N*53\r\n" + "$GPVTG,,TM,,,N,,K,N*2C";

		gpsConnectionsList = new ArrayList<>();
		gpsConnectionsList.add(GPSBU343Connector);

		ReflectionTestUtils.setField(gpsIntegrationImpl, "gpsConnectionsList", gpsConnectionsList);

		Mockito.when(GPSBU343Connector.getComPortGPSData(serialPortConnected, portThreadTime)).thenReturn(mockgps);

		Mockito.when(gpsPosition.getLat()).thenReturn(0.0);

		Mockito.when(gpsPosition.getLon()).thenReturn(0.0);

		Mockito.when(gpsPosition.getResponse()).thenReturn("failure");

		ReflectionTestUtils.setField(gpsPosition, "latitudeFromGps", 0.0);
		ReflectionTestUtils.setField(gpsPosition, "longitudeFromGps", 0.0);
		ReflectionTestUtils.setField(gpsPosition, "response", "failure");

		Mockito.when(gpsUtill.signlaParser(Mockito.anyString())).thenReturn(gpsPosition);

		map = gpsIntegrationImpl.getLatLongDtls(centerLat, centerLngt, gpsDeviceName);

		/*
		 * assertTrue( map.get(RegistrationConstants.GPS_CAPTURE_ERROR_MSG).equals(
		 * RegistrationConstants.GPS_CAPTURE_FAILURE_MSG));
		 */

	}

	/**
	 * Test method for
	 * {@link io.mosip.registration.device.gps.GPSFacade#getLatLongDtls(double, double)}.
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test
	public void testGetLatLongDtlsFailureCaseNoDeviceConnected() throws RegBaseCheckedException {

		Map<String, Object> map = new HashMap<>();

		String serialPortConnected = "";
		int portThreadTime = 1000;
		String gpsDeviceName = "GPSBU343Connector";
		double centerLat = 72.8790;
		double centerLngt = 12.3478;

		ReflectionTestUtils.setField(gpsIntegrationImpl, "serialPortConnected", "");

		ReflectionTestUtils.setField(gpsIntegrationImpl, "portThreadTime", 1000);

		String mockgps = RegistrationConstants.GPS_CAPTURE_FAILURE;

		gpsConnectionsList = new ArrayList<>();
		gpsConnectionsList.add(GPSBU343Connector);

		ReflectionTestUtils.setField(gpsIntegrationImpl, "gpsConnectionsList", gpsConnectionsList);

		Mockito.when(GPSBU343Connector.getComPortGPSData(serialPortConnected, portThreadTime)).thenReturn(mockgps);

		map = gpsIntegrationImpl.getLatLongDtls(centerLat, centerLngt, gpsDeviceName);

		/*
		 * assertTrue(map.get(RegistrationConstants.GPS_CAPTURE_ERROR_MSG)
		 * .equals(RegistrationConstants.GPS_CAPTURE_FAILURE));
		 */

	}

	/**
	 * Test method for
	 * {@link io.mosip.registration.device.gps.GPSFacade#getLatLongDtls(double, double)}.
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test
	public void testGetLatLongDtlsSuccessCase() throws RegBaseCheckedException {

		Map<String, Object> map = new HashMap<>();

		String serialPortConnected = "COM4";
		int portThreadTime = 1000;

		double centerLat = 72.8790;
		double centerLngt = 12.3478;
		String gpsDeviceName = "GPSBU343Connector";
		ReflectionTestUtils.setField(gpsIntegrationImpl, "serialPortConnected", "COM4");

		ReflectionTestUtils.setField(gpsIntegrationImpl, "portThreadTime", 1000);

		String mockgps = "$GPGGA,,,,,,0,00,,,M,0.0,M,,0000*48,$GPGSA,A1,,,,,,,,,,,,,,,*1E\r\n"
				+ "$GPGSV,3,112,01,00,000,,2,00,000,,03,00,000,,04,00,007,*C\r\n"
				+ "$GPGSV,3,2,12,05,00,000,,06,00,000,,07,00,000,,08,00,000,*77$GPGSV,3,3,12,09,00,000,,10,00,000,,11,00,000,,12,00,000,*7,\r\n"
				+ "$GPRMC,V,,,,,,,,,,N*53\r\n" + "$GPVTG,,TM,,,N,,K,N*2C";

		gpsConnectionsList = new ArrayList<>();
		gpsConnectionsList.add(GPSBU343Connector);

		ReflectionTestUtils.setField(gpsIntegrationImpl, "gpsConnectionsList", gpsConnectionsList);

		Mockito.when(GPSBU343Connector.getComPortGPSData(serialPortConnected, portThreadTime)).thenReturn(mockgps);

		Mockito.when(gpsPosition.getLat()).thenReturn(12.9913);

		Mockito.when(gpsPosition.getLon()).thenReturn(80.2457);

		Mockito.when(gpsPosition.getResponse()).thenReturn("success");

		ReflectionTestUtils.setField(gpsPosition, "latitudeFromGps", 12.9913f);
		ReflectionTestUtils.setField(gpsPosition, "longitudeFromGps", 80.2457f);
		ReflectionTestUtils.setField(gpsPosition, "response", RegistrationConstants.GPS_CAPTURE_SUCCESS);

		Mockito.when(gpsUtill.signlaParser(Mockito.anyString())).thenReturn(gpsPosition);

		map = gpsIntegrationImpl.getLatLongDtls(centerLat, centerLngt, gpsDeviceName);

		assertTrue(map.get(RegistrationConstants.GPS_CAPTURE_ERROR_MSG)
				.equals(RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG));

	}

	/**
	 * Test method for
	 * {@link io.mosip.registration.device.gps.GPSFacade#getLatLongDtls(double, double)}.
	 * 
	 * @throws RegBaseCheckedException
	 */
	@Test
	public void testGetLatLongDtlsSuccessCase1() throws RegBaseCheckedException {

		Map<String, Object> map = new HashMap<>();

		String serialPortConnected = "COM4";
		int portThreadTime = 1000;

		double centerLat = 72.8790;
		double centerLngt = 12.3478;
		String gpsDeviceName = "GPSBU343Connector";
		ReflectionTestUtils.setField(gpsIntegrationImpl, "serialPortConnected", "COM4");

		ReflectionTestUtils.setField(gpsIntegrationImpl, "portThreadTime", 1000);

		String mockgps = "$GPRMC,055218.000,A,1259.4845,N,08014.7602,E,0.07,120.70,171018,,,A*64";

		gpsConnectionsList = new ArrayList<>();
		gpsConnectionsList.add(GPSBU343Connector);

		gpsPosition.setLat(12.9913);
		gpsPosition.setLon(80.2457);
		gpsPosition.setResponse("success");

		ReflectionTestUtils.setField(gpsIntegrationImpl, "gpsConnectionsList", gpsConnectionsList);

		Mockito.when(GPSBU343Connector.getComPortGPSData(Mockito.anyString(), Mockito.anyInt())).thenReturn(mockgps);

		Mockito.when(mosipGPSProvider.signlaParser(Mockito.anyString())).thenReturn(gpsPosition);

		Mockito.when(gpsPosition.getLat()).thenReturn(12.9913);

		Mockito.when(gpsPosition.getLon()).thenReturn(80.2457);

		Mockito.when(gpsPosition.getResponse()).thenReturn("success");

		ReflectionTestUtils.setField(gpsPosition, "latitudeFromGps", 12.9913f);
		ReflectionTestUtils.setField(gpsPosition, "longitudeFromGps", 80.2457f);
		ReflectionTestUtils.setField(gpsPosition, "response", RegistrationConstants.GPS_CAPTURE_SUCCESS);

		Mockito.when(gpsUtill.signlaParser(Mockito.anyString())).thenReturn(gpsPosition);

		map = gpsIntegrationImpl.getLatLongDtls(centerLat, centerLngt, gpsDeviceName);

		assertTrue(map.get(RegistrationConstants.GPS_CAPTURE_ERROR_MSG)
				.equals(RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG));

	}

	

}
