package io.mosip.registration.device.fp.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;
import io.mosip.registration.constants.DeviceTypes;
import io.mosip.registration.device.fp.FingerprintProvider;
import io.mosip.registration.service.BaseService;

/**
 * Mantra finger print device specific functionality implemented.
 * 
 * @author SaravanaKumar G
 *
 */
@Component
public class MantraFingerprintProvider extends FingerprintProvider implements MFS100Event {

	@Autowired
	private BaseService baseService;

	/** The fp device. */
	private MFS100 fpDevice = new MFS100(this);

	private String fingerPrintType = "";

	/**
	 * This method initializes the device and captures the image from device. It
	 * waits for the given time and quality to meet. If the quality is not met in
	 * the given time then throws the error code and messages. The outputType will
	 * be either minutia or ISOTemplate.
	 * 
	 * <p>
	 * Returns the integer value based on the action performed.
	 * <p>
	 * Returns 0 if the device is initialized and fingerprint is captured with
	 * required quality score and within given time.
	 * </p>
	 * <p>
	 * Returns -1 if the device is not initialized.
	 * </p>
	 * <p>
	 * Returns -2 if the device is not valid.
	 * </p>
	 * </p>
	 *
	 * @param qualityScore
	 *            - the minimum quality score that is required
	 * @param captureTimeOut
	 *            - the capture time out
	 * @param outputType
	 *            - the output type whether minutia or ISOTemplate
	 * @return the integer value based on the device initiation and fingerprint
	 *         capture
	 */
	public int captureFingerprint(int qualityScore, int captureTimeOut, String outputType) {
		fingerPrintType = outputType;
		if (fpDevice.Init() == 0) {
			if (baseService.isValidDevice(DeviceTypes.FINGERPRINT, fpDevice.GetDeviceInfo().SerialNo())) {
				minutia = "";
				errorMessage = "";
				fingerDataInByte = null;
				isoTemplate = null;

				fpDevice.StartCapture(qualityScore, captureTimeOut, false);
				return 0;
			} else {
				return -2;
			}
		} else {
			return -1;
		}
	}

	/**
	 * This method is used to stop capture and uninitialize the FP device.
	 */
	public void uninitFingerPrintDevice() {
		fpDevice.StopCapture();
		fpDevice.Uninit();
	}

	/**
	 * Once the image is captured, then the respective Minutia and the ISO template
	 * would be extracted.
	 * 
	 * <p>
	 * The captured image which is in byte array format and the respective Minutia
	 * or ISO Template are extracted from the {@link FingerData} after the capture
	 * is completed.
	 * </p>
	 *
	 * @param status
	 *            - the boolean variable which indicates the status of capture
	 * @param erroeCode
	 *            - the error code if any
	 * @param errorMsg
	 *            - the error message if any
	 * @param fingerData
	 *            - the {@link FingerData} which contains the captured finger
	 *            details
	 */
	@Override
	public void OnCaptureCompleted(boolean status, int erroeCode, String errorMsg, FingerData fingerData) {
		fingerData.Quality();
		fingerDataInByte = fingerData.FingerImage();
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
