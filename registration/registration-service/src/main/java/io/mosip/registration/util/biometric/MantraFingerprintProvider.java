package io.mosip.registration.util.biometric;

import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;

/**
 * Mantra finger print device specific functionality implemented. 
 * 
 * @author M1046564
 *
 */
public class MantraFingerprintProvider extends FingerprintProvider implements MFS100Event {

	/** The fp device. */
	private MFS100 fpDevice = new MFS100(this);

	private String fingerPrintType = "";

	/**
	 * This method initialize the device and capture the image from device. it waits for the given time and quality to meet.
	 * if not met in the given time then throw the error code and messages. 
	 * outputType - minutia or ISOTemplate
	 */
	public int captureFingerprint(int qualityScore, int captureTimeOut, String outputType) {
		fingerPrintType = outputType;
		if (fpDevice.Init() == 0) {
			minutia = "";
			errorMessage = "";
			fingerDataInByte = null;
			isoTemplate = null;
			
			fpDevice.StartCapture(qualityScore, captureTimeOut, false);
			return 0;
		} else {
			return -1;
		}
		
	}

	/**
	 * Stop and uninitialize the FP device.  
	 */
	public void uninitFingerPrintDevice() {
		fpDevice.StopCapture();
		fpDevice.Uninit();
	}

	/**
	 * Once the image captured then the respective Minutia and the ISO template would be extracted. 
	 */
	@Override
	public void OnCaptureCompleted(boolean status, int erroeCode, String errorMsg, FingerData fingerData) {
		fingerData.Quality();
		fingerDataInByte =fingerData.FingerImage();
		errorMessage = errorMsg;
		
		if (fingerPrintType.equals("minutia")) {
			prepareMinutia(fingerData.ISOTemplate());
		} else {
			isoTemplate = fingerData.ISOTemplate();
		}
		
	}

	@Override
	public void OnPreview(FingerData arg0) {
		// TODO Auto-generated method stub
		
	}

	

}
