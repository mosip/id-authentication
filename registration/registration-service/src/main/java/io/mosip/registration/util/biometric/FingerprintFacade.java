package io.mosip.registration.util.biometric;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.image.WritableImage;

public class FingerprintFacade {
	
	@Autowired
	MosipFingerprintProvider fingerprintProvider;

	public MosipFingerprintProvider getFingerprintProviderFactory(String make) {
		fingerprintProvider=null;
		if(make.equals("Mantra")) {
			fingerprintProvider =new MantraFingerprintProvider();
		}
		return fingerprintProvider;
	}

	public String getMinutia() {
		return fingerprintProvider.getMinutia();
	}

	public String getErrorMessage() {
		return fingerprintProvider.getErrorMessage();
		
	}
	
	public WritableImage getFingerPrintImage() {
		return fingerprintProvider.getFingerPrintImage();
	}
	
}
