package io.mosip.authentication.service.impl.fingerauth.provider.impl;

import java.util.Map;
import java.util.Optional;

import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;
import io.mosip.authentication.core.dto.fingerprintauth.FingerprintDeviceInfo;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;

/**
 * @author Manoj SP
 *
 */
public class MantraFingerprintProvider extends FingerprintProvider implements MFS100Event {
	private MFS100 fpDevice = new MFS100(this);

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

	@Override
	public Optional<byte[]> captureFingerprint(Integer quality, Integer timeout) {
		if (fpDevice.IsConnected() && fpDevice.Init() == 0) {
			FingerData fingerData = new FingerData();
			int captureStatus = fpDevice.AutoCapture(fingerData, timeout, false, true); // set onPreview as false and set detectFinger as true
			if (captureStatus == 0 && fpDevice.GetLastError().isEmpty()) { 
				//TODO Check for Quality and Nfiq
				fpDevice.StopCapture();
				fpDevice.Uninit();
				return Optional.ofNullable(fingerData.WSQImage());
			} else {
				// log "Capture failed : " + fpDevice.GetLastError());
				fpDevice.StopCapture();
				fpDevice.Uninit();
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional<Map<?, ?>> segmentFingerprint(byte[] fingerImage) {
		return Optional.empty();
	}

	@Override
	public void OnCaptureCompleted(boolean arg0, int arg1, String arg2, FingerData arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnPreview(FingerData arg0) {
		// TODO Auto-generated method stub

	}
}
