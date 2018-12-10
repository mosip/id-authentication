package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Scanner;

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

	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, RestClientException, JSONException {
		FingerPrint fp = new FingerPrint();
		fp.saveFPData("Rk1SACAyMAAAAAFuAAABPAFiAMUAxQEAAAAoOICiALRdQ4CsAOSGZICiAJQuQ0C0AJWsQ0ByAMHeUIBqAL7EUEB7AOqAUEBnAKa4ZEBuAJO4ZECuAHbRUICkAQ58XUBcANsuQ0EAAMzRZEDeAHHUZED8AO4nSUCPAFcaZEDzAQ4MZEBrASmEV0CNAVQAQ0C6AKqmZICUAOB0XUB6AMxcSYCwAO8bZEB3AKLKZECBAJLHZIDgAKjIZEBlANgaQ0DkAJzNZIDSAP8bZEDkAPAnZIDEARIKZECiAGIKXUChASACXUBlAQYCXYB5AGCwZEDPAFTmV0B/AEscSUDqAVB/NYDAAKytZEDNALixZICSAOUGXYDZALm4ZECMAI7JSYC6AIbBV0CnAH22UIDOAIbIZEBuAOsASUCVAQv9XYD1ALDPZED5AOCxUIDNAGnSZEBbAPoRXUD8AITXV0BLAIOuUEDPAS8GZEBVAFwkNQAA");
	}

	@GetMapping(value = "/scan")
	@ApiOperation(value = "Scans the Fingerprint and returns encoded ISO Template", response = String.class)
	public String fingerprint() throws KeyManagementException, NoSuchAlgorithmException, RestClientException, JSONException {
		FingerprintDevice fp = new FingerprintDevice(new FingerprintEvent());
		System.err.println(fp.Init());
		if (fp.Init() == 0) {
			System.err.println(fp.GetLastError());
			FingerData data = new FingerData();
			int result = fp.AutoCapture(data, 10000, false, true); // fingerdata, timeout, onPreview, detectFinger
			fp.StopCapture();
			fp.Uninit();
			String encodedString = Base64.getEncoder().encodeToString(data.ISOTemplate());

			return saveFPData(encodedString);
		} else {
			System.err.println(fp.GetLastError());
			return "failed";
		}
	}

	private String saveFPData(String encodedString) throws KeyManagementException, NoSuchAlgorithmException, RestClientException, JSONException {

		turnOffSslChecking();
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new TestErrorHandler());
		String fooResourceUrl = "https://integ.mosip.io/idrepo/v1.0/identity";
		IdRequestDTO req = new IdRequestDTO();
		req.setId("mosip.id.create");
		req.setRegistrationId(RandomStringUtils.randomNumeric(10));
		IdentityDTO identityDTO = new IdentityDTO();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setValue(encodedString);
		identityDTO.setLeftIndex(Collections.singletonList(identityInfoDTO));
		req.setRequest(identityDTO);
		req.setStatus("REGISTERED");
		req.setTimestamp("2018-12-10T14:29:10.301+0000");
		req.setVer("1.0");
		
		ResponseEntity<IdResponseDTO> response = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, getHeaders(req), IdResponseDTO.class);
		System.err.println("UIN >>>>>>>>>>>" + response.getBody().getResponse().getEntity());
		return response.getBody().getResponse().getEntity();
	}

private HttpEntity getHeaders(IdRequestDTO req) throws JSONException {
HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    return new HttpEntity(req, headers);
    }

	private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub

		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub

		}
	} };

	public static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
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
	
	class TestErrorHandler extends DefaultResponseErrorHandler {

	    @Override
	    public void handleError(ClientHttpResponse response) throws IOException {
	        //conversion logic for decoding conversion
	        System.err.println(IOUtils.toString(response.getBody(), Charset.defaultCharset()));
	    }
	}
}
