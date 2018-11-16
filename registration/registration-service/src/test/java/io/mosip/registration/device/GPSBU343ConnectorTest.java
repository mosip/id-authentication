package io.mosip.registration.device;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Enumeration;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import io.mosip.registration.context.SessionContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CommPortIdentifier.class })
public class GPSBU343ConnectorTest {


	@InjectMocks
	private GPSBU343Connector gpsU343Connector;

	@Mock
	private CommPortIdentifier commPortIdentifier;

	@Mock
	private Enumeration<CommPortIdentifier> enumCommPortIdentifier;

	@Mock
	private SerialPort serialPort;

	@Mock
	private InputStream inputStream;

	@Mock
	private StringBuilder deviceData;

	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);

	}

	/**
	 * Initialize.
	 *
	 * @throws IOException         Signals that an I/O exception has occurred.
	 * @throws URISyntaxException  the URI syntax exception
	 * @throws PortInUseException  the port in use exception
	 * @throws NoSuchPortException the no such port exception
	 */
	@Before
	public void initialize() throws IOException, URISyntaxException, PortInUseException, NoSuchPortException {
		mockSerialPort();
		mockPortName("COM4");
	}

	/**
	 * Test method for
	 * {@link io.mosip.registration.device.GPSIntegrationImpl#getLatLongDtls(double, double)}.
	 * 
	 * @throws PortInUseException
	 * @throws NoSuchPortException
	 * @throws IOException
	 */
	@Test
	public void testGPSBU343ConnectorWithGpsDevice() throws PortInUseException, NoSuchPortException, IOException {

		String serialPortConnected = "COM4";

		int portThreadTime = 1000;
		StringBuilder builder = new StringBuilder();

		String value = "$GPG,A,,,,,0,00,,,M,0.0,M,,0000*48$GPGSA,A,1,,,,,,,,,,,,,,,*1E\r\n"
				+ "$GPGSV,3,1,12,01,00,000,,02,00,000,,03400,000,,0,00,000,*7C\r\n"
				+ "$GPGSV,3,2,12,05,00,000,,06,00,000,,07,00,000,,08,00,000,*77$GPGSV,3,3,12,09,00,000,,10,00,000,,11,00,002,,1,00,000,*71$GPRMC,,V,,,,,,,,,,N*53$GPVTT,,,,M,,N,,K,N*C";

		builder.append(value);
		ReflectionTestUtils.setField(gpsU343Connector, "serialPortId", null);

		String gpsVale = gpsU343Connector.getGPSData(serialPortConnected, portThreadTime);

		assertTrue(gpsVale.equals("gpsCaptureFailure"));

	}

	/**
	 * Mock serial port.
	 *
	 * @throws NoSuchPortException the no such port exception
	 * @throws PortInUseException  the port in use exception
	 * @throws IOException         Signals that an I/O exception has occurred.
	 */
	private void mockSerialPort() throws NoSuchPortException, PortInUseException, IOException {
		PowerMockito.mockStatic(CommPortIdentifier.class);
		Mockito.when(commPortIdentifier.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);
		Mockito.when(commPortIdentifier.isCurrentlyOwned()).thenReturn(false);
		Mockito.when(commPortIdentifier.open(Mockito.anyString(), Mockito.anyInt())).thenReturn(serialPort);
		Mockito.when(CommPortIdentifier.getPortIdentifier(Mockito.anyString())).thenReturn(commPortIdentifier);
		Mockito.when(serialPort.getInputStream()).thenReturn(inputStream);
	}

	/**
	 * Mock port name.
	 *
	 * @param portName the port name
	 */
	private void mockPortName(String portName) {
		Mockito.when(commPortIdentifier.getName()).thenReturn(portName);
	}
}
