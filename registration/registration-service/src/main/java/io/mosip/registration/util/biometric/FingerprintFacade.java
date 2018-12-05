package io.mosip.registration.util.biometric;

import java.io.IOException;

import javafx.scene.image.WritableImage;

/**
 * It takes a decision based on the input provider name and initialize the respective implementation class and perform the 
 * required operation.
 * 
 * @author M1046564
 *
 */
public class FingerprintFacade {
	
	MosipFingerprintProvider fingerprintProvider;

	public MosipFingerprintProvider getFingerprintProviderFactory(String make) {
		fingerprintProvider =null;
		if(make.equals("Mantra")) {
			fingerprintProvider =new MantraFingerprintProvider();
		}
		return fingerprintProvider;
	}

	/**
	 * provide the minutia of a finger.
	 * @return
	 */
	public String getMinutia() {
		return fingerprintProvider.getMinutia();
	}
	
	public byte[] getIsoTemplate() {
		return fingerprintProvider.getIsoTemplate();
	}

	public String getErrorMessage() {
		return fingerprintProvider.getErrorMessage();
		
	}
	
	/**
	 * 
	 * @return
	 */
	public WritableImage getFingerPrintImage() throws IOException{
		return fingerprintProvider.getFingerPrintImage();
	}
	
}
