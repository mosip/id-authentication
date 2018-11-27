package io.mosip.authentication.service.impl.fingerauth.provider.impl;

import java.util.Map;
import java.util.Optional;

import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;
import io.mosip.authentication.core.dto.fingerprintauth.FingerprintDeviceInfo;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;

/**
 * The Class MantraFingerprintProvider - Provider class for Mantra fingerprint
 * devices.
 *
 * @author Manoj SP
 */
public class MantraFingerprintProvider extends FingerprintProvider implements MFS100Event {

	/** The fp device. */
	private MFS100 fpDevice = new MFS100(this);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#deviceInfo()
	 */
	@Override
	public FingerprintDeviceInfo deviceInfo() {
		FingerprintDeviceInfo dInfo = new FingerprintDeviceInfo();
		if (fpDevice.IsConnected() && fpDevice.Init() == 0) {
			dInfo.setDeviceId(fpDevice.GetDeviceInfo().SerialNo());
			dInfo.setFingerType("Single");
			dInfo.setMake(fpDevice.GetDeviceInfo().Make());
			dInfo.setModel(fpDevice.GetDeviceInfo().Model());
		}
		return dInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#captureFingerprint(java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	public Optional<byte[]> captureFingerprint(Integer quality, Integer timeout) {
		if (fpDevice.IsConnected() && fpDevice.Init() == 0) {
			FingerData fingerData = new FingerData();
			int captureStatus = fpDevice.AutoCapture(fingerData, timeout, false, true); 
																						
			if (captureStatus == 0 && fpDevice.GetLastError().isEmpty()) {
				fpDevice.StopCapture();
				fpDevice.Uninit();
				return Optional.ofNullable(fingerData.WSQImage());
			} else {
				fpDevice.StopCapture();
				fpDevice.Uninit();
			}
		}

		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#segmentFingerprint(byte[])
	 */
	@Override
	public Optional<Map> segmentFingerprint(byte[] fingerImage) {
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see MFS100.MFS100Event#OnCaptureCompleted(boolean, int, java.lang.String,
	 * MFS100.FingerData)
	 */
	@Override
	public void OnCaptureCompleted(boolean status, int arg1, String arg2, FingerData fingerData) {
		// need to be implemented
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see MFS100.MFS100Event#OnPreview(MFS100.FingerData)
	 */
	@Override
	public void OnPreview(FingerData arg0) {
		// need to be implemented
	}
}
