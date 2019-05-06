package io.mosip.authentication.partnerdemo.service.controller;

import java.util.Base64;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.ApiOperation;

@RestController
public class FingerPrint {

	private static Logger logger = IdaLogger.getLogger(FingerPrint.class);

	private static final String SESSION_ID = "SESSION_ID";

	@GetMapping(value = "/scan")
	@ApiOperation(value = "Scans the Fingerprint and returns encoded ISO Template", response = String.class)
	public String fingerprint() {
		FingerprintDevice fp = new FingerprintDevice(new FingerprintEvent());
		if (fp.Init() == 0) {
			FingerData data = new FingerData();
			fp.AutoCapture(data, 10000, false, true); // fingerdata, timeout, onPreview, detectFinger
			fp.StopCapture();
			fp.Uninit();
			return Base64.getEncoder().encodeToString(data.ISOTemplate());
		} else {
			logger.error(SESSION_ID, this.getClass().getSimpleName(), "Inside Fingerprint", fp.GetLastError());
			return "failed";
		}
	}

	class FingerprintEvent implements MFS100Event {
		public void OnCaptureCompleted(boolean arg0, int arg1, String arg2, FingerData arg3) {
			// OnCaptureCompleted method
		}

		public void OnPreview(FingerData arg0) {
			// OnPreview method
		}
	}

	class FingerprintDevice extends MFS100 {

		public FingerprintDevice(MFS100Event event) {
			super(event);
		}

	}

}
