package io.mosip.authentication.demo.service.controller;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;
import io.swagger.annotations.ApiOperation;

@RestController
public class FingerPrint {

	@GetMapping(value = "/scan")
	@ApiOperation(value = "Scans the Fingerprint and returns encoded ISO Template", response = String.class)
	public String fingerprint()
			throws KeyManagementException, NoSuchAlgorithmException, RestClientException, JSONException {
		FingerprintDevice fp = new FingerprintDevice(new FingerprintEvent());
		if (fp.Init() == 0) {
			FingerData data = new FingerData();
			int result = fp.AutoCapture(data, 10000, false, true); // fingerdata, timeout, onPreview, detectFinger
			fp.StopCapture();
			fp.Uninit();
			String encodedString = Base64.getEncoder().encodeToString(data.ISOTemplate());
			return encodedString;
		} else {
			System.err.println(fp.GetLastError());
			return "failed";
		}
	}
  class FingerprintEvent implements MFS100Event {
       public void OnCaptureCompleted(boolean arg0, int arg1, String arg2, FingerData arg3) {
       
		}
     public void OnPreview(FingerData arg0) {

		}
   }
 class FingerprintDevice extends MFS100 {

		public FingerprintDevice(MFS100Event event) {
			super(event);
		}

	}


}
