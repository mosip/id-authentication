package io.mosip.registration.device;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.device.gps.impl.GPSBU343Connector;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({ CommPortIdentifier.class })
public class GPSBU343ConnectorTest {

	@InjectMocks
	private GPSBU343Connector gpsU343Connector;

	@Mock
	private CommPortIdentifier commPortIdentifier;

	@Mock
	private Enumeration<CommPortIdentifier> portListEnumeration;

	@Mock
	private SerialPort serialPortId;

	@Mock
	private StringBuilder deviceData;

	@Mock
	private AuditFactoryImpl auditFactory;
	
	@Mock
	private Logger logger;

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
	 * @throws Exception
	 */
	@Before
	public void initialize() throws Exception {

		mockSerialPort();
		mockPortName("COM4");
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
	}

	/**
	 * Test method for
	 * {@link io.mosip.registration.device.gps.impl.GPSIntegrationImpl#getLatLongDtls(double, double)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGPSBU343ConnectorWithGpsDevice() throws Exception {

		String serialPortConnected = "COM4";
		String value = "$GPG,A,,,,,0,00,,,M,0.0,M,,0000*48$GPGSA,A,1,,,,,,,,,,,,,,,*1";

		ReflectionTestUtils.setField(gpsU343Connector, "inputStream", new ByteArrayInputStream(value.getBytes()));

		int portThreadTime = 1000;

		Mockito.when(portListEnumeration.hasMoreElements()).thenReturn(true);
		Mockito.when(portListEnumeration.nextElement()).thenReturn(commPortIdentifier);
		Mockito.when(commPortIdentifier.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);

		CommPort commPort = Mockito.mock(CommPort.class);
		Mockito.when(commPort.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		ReflectionTestUtils.setField(gpsU343Connector, "serialPortId", null);
		Mockito.when(commPortIdentifier.open(Mockito.anyString(), Mockito.anyInt())).thenReturn(serialPortId);

		Mockito.when(serialPortId.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		SerialPortEvent event = mock(SerialPortEvent.class);
		Mockito.when(event.getEventType()).thenReturn(1);

		gpsU343Connector.serialEvent(event);

		String gpsVale = gpsU343Connector.getComPortGPSData(serialPortConnected, portThreadTime);

		assertTrue(gpsVale.contains("$GP"));

	}

	/**
	 * Test GPSBU 343 connector port N ull case.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testGPSBU343ConnectorPortNUllCase() throws Exception {

		String serialPortConnected = null;
		String value = "$GPG,A,,,,,0,00,,,M,0.0,M,,0000*48$GPGSA,A,1,,,,,,,,,,,,,,,*1";

		ReflectionTestUtils.setField(gpsU343Connector, "inputStream", new ByteArrayInputStream(value.getBytes()));

		int portThreadTime = 1000;

		Mockito.when(portListEnumeration.hasMoreElements()).thenReturn(true);
		Mockito.when(portListEnumeration.nextElement()).thenReturn(commPortIdentifier);
		Mockito.when(commPortIdentifier.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);

		CommPort commPort = Mockito.mock(CommPort.class);
		Mockito.when(commPort.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		ReflectionTestUtils.setField(gpsU343Connector, "serialPortId", null);
		Mockito.when(commPortIdentifier.open(Mockito.anyString(), Mockito.anyInt())).thenReturn(serialPortId);

		Mockito.when(serialPortId.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		SerialPortEvent event = mock(SerialPortEvent.class);
		Mockito.when(event.getEventType()).thenReturn(1);

		gpsU343Connector.serialEvent(event);

		String gpsVale = gpsU343Connector.getComPortGPSData(serialPortConnected, portThreadTime);

		assertTrue(gpsVale.contains("$GP"));

	}

	/**
	 * Test GPSBU 343 connector port failure case 1.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testGPSBU343ConnectorPortFailureCase1() throws Exception {

		String serialPortConnected = null;
		String value = RegistrationConstants.GPS_CAPTURE_FAILURE_MSG;

		ReflectionTestUtils.setField(gpsU343Connector, "inputStream", new ByteArrayInputStream(value.getBytes()));

		int portThreadTime = 1000;

		Mockito.when(portListEnumeration.hasMoreElements()).thenReturn(false);
		Mockito.when(portListEnumeration.nextElement()).thenReturn(commPortIdentifier);
		Mockito.when(commPortIdentifier.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);

		CommPort commPort = Mockito.mock(CommPort.class);
		Mockito.when(commPort.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		ReflectionTestUtils.setField(gpsU343Connector, "serialPortId", null);
		Mockito.when(commPortIdentifier.open(Mockito.anyString(), Mockito.anyInt())).thenReturn(serialPortId);

		Mockito.when(serialPortId.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		SerialPortEvent event = mock(SerialPortEvent.class);
		Mockito.when(event.getEventType()).thenReturn(1);

		gpsU343Connector.serialEvent(event);

		String gpsVale = gpsU343Connector.getComPortGPSData(serialPortConnected, portThreadTime);

		assertTrue(gpsVale.equals(RegistrationConstants.GPS_CAPTURE_FAILURE_MSG));

	}

	@Test
	public void testGPSBU343ConnectorPortFailureCase2() throws Exception {

		String serialPortConnected = null;
		String value = RegistrationConstants.GPS_CAPTURE_FAILURE;

		ReflectionTestUtils.setField(gpsU343Connector, "inputStream", new ByteArrayInputStream(value.getBytes()));

		int portThreadTime = 1000;

		Mockito.when(portListEnumeration.hasMoreElements()).thenReturn(false);
		Mockito.when(portListEnumeration.nextElement()).thenReturn(commPortIdentifier);
		Mockito.when(commPortIdentifier.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);

		CommPort commPort = Mockito.mock(CommPort.class);
		Mockito.when(commPort.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		ReflectionTestUtils.setField(gpsU343Connector, "serialPortId", null);
		Mockito.when(commPortIdentifier.open(Mockito.anyString(), Mockito.anyInt())).thenReturn(serialPortId);

		Mockito.when(serialPortId.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		SerialPortEvent event = mock(SerialPortEvent.class);
		Mockito.when(event.getEventType()).thenReturn(1);

		gpsU343Connector.serialEvent(event);

		String gpsVale = gpsU343Connector.getComPortGPSData(serialPortConnected, portThreadTime);

		assertTrue(gpsVale.equals(RegistrationConstants.GPS_CAPTURE_FAILURE));

	}

	@Test
	public void testGPSBU343ConnectorPortFailureCase3() throws Exception {

		String serialPortConnected = null;
		String value = RegistrationConstants.GPS_CAPTURE_PORT_FAILURE_MSG;

		ReflectionTestUtils.setField(gpsU343Connector, "inputStream", new ByteArrayInputStream(value.getBytes()));

		int portThreadTime = 1000;

		Mockito.when(portListEnumeration.hasMoreElements()).thenReturn(false);
		Mockito.when(portListEnumeration.nextElement()).thenReturn(commPortIdentifier);
		Mockito.when(commPortIdentifier.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);

		CommPort commPort = Mockito.mock(CommPort.class);
		Mockito.when(commPort.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		ReflectionTestUtils.setField(gpsU343Connector, "serialPortId", null);
		Mockito.when(commPortIdentifier.open(Mockito.anyString(), Mockito.anyInt())).thenReturn(serialPortId);

		Mockito.when(serialPortId.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		SerialPortEvent event = mock(SerialPortEvent.class);
		Mockito.when(event.getEventType()).thenReturn(1);

		gpsU343Connector.serialEvent(event);

		String gpsVale = gpsU343Connector.getComPortGPSData(serialPortConnected, portThreadTime);

		assertTrue(gpsVale.equals(RegistrationConstants.GPS_CAPTURE_PORT_FAILURE_MSG));

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGPSBU343ConnectorPortFailureCase4() throws Exception {

		String serialPortConnected = null;
		String value = RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE;

		ReflectionTestUtils.setField(gpsU343Connector, "inputStream", new ByteArrayInputStream(value.getBytes()));

		int portThreadTime = 1000;

		Mockito.when(portListEnumeration.hasMoreElements()).thenReturn(false);
		Mockito.when(portListEnumeration.nextElement()).thenReturn(commPortIdentifier);
		Mockito.when(commPortIdentifier.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);

		CommPort commPort = Mockito.mock(CommPort.class);
		Mockito.when(commPort.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		ReflectionTestUtils.setField(gpsU343Connector, "serialPortId", null);
		Mockito.when(commPortIdentifier.open(Mockito.anyString(), Mockito.anyInt())).thenReturn(serialPortId);

		Mockito.when(serialPortId.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		SerialPortEvent event = mock(SerialPortEvent.class);
		Mockito.when(event.getEventType()).thenReturn(1);

		gpsU343Connector.serialEvent(event);

		String gpsVale = gpsU343Connector.getComPortGPSData(serialPortConnected, portThreadTime);

		assertTrue(gpsVale.equals(RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE));

	}

	@Test
	public void testGPSBU343ConnectorPortFailureCase5() throws Exception {

		String serialPortConnected = null;

		int portThreadTime = 1000;

		Mockito.when(portListEnumeration.hasMoreElements()).thenReturn(false);
		ReflectionTestUtils.setField(gpsU343Connector, "portEnumList", null);

		String gpsVale = gpsU343Connector.getComPortGPSData(serialPortConnected, portThreadTime);

		assertTrue(gpsVale.equals(RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE));

	}

	/**
	 * Test GPSBU 343 connector port failure case 6.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testGPSBU343ConnectorPortFailureCase6() throws Exception {

		String serialPortConnected = null;
		String value = "";

		ReflectionTestUtils.setField(gpsU343Connector, "inputStream", new ByteArrayInputStream(value.getBytes()));

		int portThreadTime = 1000;

		Mockito.when(portListEnumeration.hasMoreElements()).thenReturn(false);
		Mockito.when(portListEnumeration.nextElement()).thenReturn(commPortIdentifier);
		Mockito.when(commPortIdentifier.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);

		CommPort commPort = Mockito.mock(CommPort.class);
		Mockito.when(commPort.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		ReflectionTestUtils.setField(gpsU343Connector, "serialPortId", null);
		Mockito.when(commPortIdentifier.open(Mockito.anyString(), Mockito.anyInt())).thenReturn(serialPortId);

		Mockito.when(serialPortId.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes()));
		SerialPortEvent event = mock(SerialPortEvent.class);
		Mockito.when(event.getEventType()).thenReturn(1);

		gpsU343Connector.serialEvent(event);

		String gpsVale = gpsU343Connector.getComPortGPSData(serialPortConnected, portThreadTime);

		assertTrue(gpsVale.equals(RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE));

	}

	private void mockSerialPort() throws NoSuchPortException, PortInUseException, IOException {

		PowerMockito.mockStatic(CommPortIdentifier.class);
		CommPortIdentifier commPortIdentifier = Mockito.mock(CommPortIdentifier.class);
		Mockito.when(portListEnumeration.hasMoreElements()).thenReturn(true);
		Mockito.when(portListEnumeration.nextElement()).thenReturn(commPortIdentifier);
		Mockito.when(CommPortIdentifier.getPortIdentifiers()).thenReturn(portListEnumeration);

		Mockito.when(commPortIdentifier.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);
		Mockito.when(commPortIdentifier.isCurrentlyOwned()).thenReturn(false);
		Mockito.when(commPortIdentifier.open(Mockito.anyString(), Mockito.anyInt())).thenReturn(serialPortId);

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
