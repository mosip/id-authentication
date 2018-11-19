package io.mosip.registration.test.webcamera;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.github.sarxos.webcam.Webcam;

import io.mosip.registration.util.webcam.WebcamDeviceImpl;

public class WebCamDeviceTest {
	
	@InjectMocks
	WebcamDeviceImpl webcamDeviceImpl;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Test
	public void connectTest() {
		List<Webcam> webcams = Webcam.getWebcams();
		Webcam webcam = null;
		if(webcams.size()>0) {
			if(webcams.get(0).getName().toLowerCase().contains("integrated")) {
				if(webcams.size()>1) {
					webcam = webcams.get(1);
					webcam.setCustomViewSizes(new Dimension(640, 480));
					assertThat(webcamDeviceImpl.connect(640, 480), is(webcam));
				}
				else {
					assertThat(webcamDeviceImpl.connect(640, 480), is(webcam));
				}
			} else {
				webcam = webcams.get(0);
				webcam.setCustomViewSizes(new Dimension(640, 480));
				assertThat(webcamDeviceImpl.connect(640, 480), is(webcam));
			}		
		}		
	}
	
	@Test
	public void captureImageTest() {
		BufferedImage image = Webcam.getDefault().getImage();		
		assertThat(webcamDeviceImpl.captureImage(Webcam.getDefault()), is(image));
		webcamDeviceImpl.close(Webcam.getDefault());
	}
	
}
