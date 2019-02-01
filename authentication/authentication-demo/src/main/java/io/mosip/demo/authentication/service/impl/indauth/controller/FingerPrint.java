package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.demo.dto.IdRequestDTO;
import io.mosip.demo.dto.IdResponseDTO;
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
//			return saveFPData(encodedString);
			return encodedString;
		} else {
			System.err.println(fp.GetLastError());
			return "failed";
		}
	}

	

	



	class FingerprintEvent implements MFS100Event {

		public void OnCaptureCompleted(boolean arg0, int arg1, String arg2, FingerData arg3) {
			// TODO Auto-generated method stub

		}

		public void OnPreview(FingerData arg0) {
			// TODO Auto-generated method stub

		}

	}

	class FingerprintDevice extends MFS100 {

		public FingerprintDevice(MFS100Event event) {
			super(event);
		}

	}


}
