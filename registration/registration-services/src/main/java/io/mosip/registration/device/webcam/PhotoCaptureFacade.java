package io.mosip.registration.device.webcam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.device.webcam.impl.WebcamSarxosServiceImpl;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author Himaja Dhanyamraju
 */
@Component
public class PhotoCaptureFacade extends WebcamSarxosServiceImpl {

	@Autowired
	private IMosipWebcamService webcamProvider;
	
	private List<IMosipWebcamService> webCamProviders;
	
	public IMosipWebcamService getPhotoProviderFactory(String make) {
		for (IMosipWebcamService mosipWebcamProvider : webCamProviders) {
			if (mosipWebcamProvider.getClass().getName().toLowerCase().contains(make.toLowerCase())) {
				webcamProvider = mosipWebcamProvider;
			}
		}
		return webcamProvider;
	}

	@Autowired
	public void setWebCamProviders(List<IMosipWebcamService> mosipWebcamProvider) {
		this.webCamProviders = mosipWebcamProvider;
	}
}
