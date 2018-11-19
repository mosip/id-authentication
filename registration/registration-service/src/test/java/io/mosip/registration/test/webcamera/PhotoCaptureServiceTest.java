package io.mosip.registration.test.webcamera;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.github.sarxos.webcam.Webcam;

import io.mosip.registration.service.impl.PhotoCaptureServiceImpl;
import io.mosip.registration.util.webcam.WebcamDeviceImpl;

public class PhotoCaptureServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	PhotoCaptureServiceImpl photoCaptureServiceImpl;
	
	@Mock
	WebcamDeviceImpl webcamDeviceImpl;
	
	@Test
	public void connectTest() {
		List<Webcam> webcams = Webcam.getWebcams();
		Webcam webcam = null;
		if(webcams.size()>0) {
			if(webcams.get(0).getName().toLowerCase().contains("integrated")) {
				if(webcams.size()>1) {
					webcam = webcams.get(1);
					webcam.setCustomViewSizes(new Dimension(640, 480));
					when(webcamDeviceImpl.connect(640, 480)).thenReturn(webcam);
				}
				else {
					when(webcamDeviceImpl.connect(640, 480)).thenReturn(webcam);
				}
			} else {
				webcam = webcams.get(0);
				webcam.setCustomViewSizes(new Dimension(640, 480));
				when(webcamDeviceImpl.connect(640, 480)).thenReturn(webcam);
			}		
		}
		assertThat(photoCaptureServiceImpl.connect(640, 480), is(webcam));
	}
	
	@Test
	public void captureImageTest() {
		BufferedImage image = Webcam.getDefault().getImage();	
		when(webcamDeviceImpl.captureImage(Webcam.getDefault())).thenReturn(image);
		assertThat(photoCaptureServiceImpl.captureImage(Webcam.getDefault()), is(image));
		photoCaptureServiceImpl.close(Webcam.getDefault());
	}
	
}
