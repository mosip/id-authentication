package io.mosip.registration.test.webcamera;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryService;
import com.github.sarxos.webcam.WebcamLock;

import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.device.webcam.impl.WebcamSarxosServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Webcam.class })
public class WebcamSarxosServiceTest {
	
	@InjectMocks
	WebcamSarxosServiceImpl webcamSarxosServiceImpl;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	
	@Mock
	private WebcamDiscoveryService discoveryService;
	
	@Mock
	private WebcamLock webcamLock;
	
	@Mock
	private Webcam webcam;
	
	@Before
	public void initialize() {
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put("mosip.registration.webcam_name", "Test-Cam");
		applicationContext.setApplicationMap(temp);
	}
	
	@Test
	public void testIsWebcamConnected() {
		when(webcam.isOpen()).thenReturn(true);
		assertThat(webcamSarxosServiceImpl.isWebcamConnected(), is(true));
	}
	
	@Test
	public void connectSuccessTest() {
		PowerMockito.mockStatic(Webcam.class);
		Webcam webcam = Mockito.mock(Webcam.class);
		List<Webcam> webcams = new ArrayList<>();
		when(webcam.getLock()).thenReturn(webcamLock);
		when(webcam.getName()).thenReturn("Test-Cam");
		when(webcam.getViewSize()).thenReturn(new Dimension(640, 480));
		webcams.add(webcam);
		when(Webcam.getWebcams()).thenReturn(webcams);
		when(Webcam.getDiscoveryService()).thenReturn(discoveryService);
		discoveryService.stop();
		webcamSarxosServiceImpl.connect(640, 480);		
	}
	
	@Test
	public void connectTest() {
		PowerMockito.mockStatic(Webcam.class);
		Webcam webcam = Mockito.mock(Webcam.class);
		List<Webcam> webcams = new ArrayList<>();
		when(webcam.getLock()).thenReturn(webcamLock);
		when(webcam.getName()).thenReturn("Logitech");
		when(webcam.getViewSize()).thenReturn(new Dimension(640, 480));
		webcams.add(webcam);
		when(Webcam.getWebcams()).thenReturn(webcams);
		when(Webcam.getDiscoveryService()).thenReturn(discoveryService);
		discoveryService.stop();
		webcamSarxosServiceImpl.connect(640, 480);		
	}
	
	@Test
	public void captureImageTest() {
//		PowerMockito.mockStatic(Webcam.class);
//		Webcam webcam = Mockito.mock(Webcam.class);
//		when(webcam.getName()).thenReturn("Test-Cam");
//		when(webcam.getViewSize()).thenReturn(new Dimension(640, 480));
//		when(Webcam.getDefault()).thenReturn(webcam);
		BufferedImage image = null;		
		when(webcam.getImage()).thenReturn(image);
		assertThat(webcamSarxosServiceImpl.captureImage(), is(image));
		webcamSarxosServiceImpl.close();
	}
	
}
