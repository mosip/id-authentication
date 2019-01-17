package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.demo.authentication.service.dto.CryptomanagerRequestDto;
import io.mosip.demo.authentication.service.dto.CryptomanagerResponseDto;
import io.mosip.demo.authentication.service.dto.EncryptionRequestDto;
import io.mosip.demo.authentication.service.dto.EncryptionResponseDto;
import io.mosip.demo.authentication.service.dto.EncryptedRequest;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Dinesh Karuppiah
 */

@RestController
public class Encrypt {

	private static final String FILEPATH = "sample.privatekey.filepath";
	private static final String FORMAT = "UTF-8";

	/** The Constant RSA. */
	private static final String RSA = "RSA";

	private static final LocalDateTime TIMESTAMP = LocalDateTime.now();

	private static final String REFERENCEID = "516283648960";

	private static final String PUBLICKEY = "publicKey";

	@Autowired
	private Environment environment;

	@Autowired
	private KeyGenerator keyGenerator;

	@Autowired
	private EncryptorImpl encryptor;
	
	@Autowired
	private ObjectMapper objMapper;
	
/** KeySplitter */
	
	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;
	
	@Value("${mosip.kernel.encrypt-url}")
	private String encryptURL;
	
	@Value("${application.id}")
	private String appID;

	private static final Provider provider = new BouncyCastleProvider();

	@PostMapping(path = "/identity/encrypt")
	@ApiOperation(value = "Encrypt Identity with sessionKey and Encrypt Session Key with Public Key", response = EncryptionResponseDto.class)
	public EncryptedRequest encrypt(@RequestBody EncryptionRequestDto encryptionRequestDto)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, KeyManagementException,
			RestClientException, JSONException {
		EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();
		String encryptedResponse = getEncryptedValue(objMapper.writeValueAsString(encryptionRequestDto.getIdentityRequest()), encryptionRequestDto.getTspID());
		System.err.println("Demo"+encryptedResponse);
		EncryptedRequest value = split(encryptedResponse);
        return value;

	}

	private EncryptedRequest split(String value) {
		EncryptedRequest encryptedEntity = new EncryptedRequest();
		byte[] encryptedHybridData = Base64.decodeBase64(value);
		int keyDemiliterIndex = 0;
		keyDemiliterIndex = CryptoUtil.getSplitterIndex(encryptedHybridData, keyDemiliterIndex, keySplitter);
		byte[] encryptedKey = Arrays.copyOfRange(encryptedHybridData, 0, keyDemiliterIndex);
		byte[] encryptedData = Arrays.copyOfRange(encryptedHybridData, keyDemiliterIndex + keySplitter.length(),
				encryptedHybridData.length);
		encryptedEntity.setKey(Base64.encodeBase64URLSafeString(encryptedKey));
		encryptedEntity.setData(Base64.encodeBase64URLSafeString(encryptedData));
		return encryptedEntity;
	}



	public String getEncryptedValue(String data, String tspID)
			throws IOException, KeyManagementException, NoSuchAlgorithmException, RestClientException, JSONException {

		byte[] output = null;
		JsonNode jsonNode = null;

		turnOffSslChecking();
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new TestErrorHandler());
		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
		request.setApplicationId(appID);
		request.setData(Base64.encodeBase64URLSafeString(data.getBytes(StandardCharsets.UTF_8)));
		request.setReferenceId(tspID);
		String utcTime = DateUtils.getUTCCurrentDateTimeString();
		request.setTimeStamp(utcTime);

		ResponseEntity<CryptomanagerResponseDto> response = restTemplate.exchange(encryptURL, HttpMethod.POST,
				getHeaders(request), CryptomanagerResponseDto.class);
		return response.getBody().getData();


	}

	private HttpEntity getHeaders(CryptomanagerRequestDto req)  {
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

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub
						InputStream inStream = null;
						
						// String filename = System.getProperty("JAVA_HOME")
						//	        + "/jre/lib/security/cacerts".replace('/', File.separatorChar);
						 try {
							 // Loading the CA cert
							 URL url = getClass().getResource("tcp/cacert.pem");
							inStream = new FileInputStream(url.getFile());
							//inStream = new FileInputStream(filename);
							CertificateFactory  cf= CertificateFactory.getInstance("X.509");
							X509Certificate  caCertificate = (X509Certificate) cf.generateCertificate(inStream);
							
							inStream.close();
							
							// Verifing  CA Certificate by public key
							for(X509Certificate cert : certs) {
								
								if (cert.equals(caCertificate)) {
									cert.verify(caCertificate.getPublicKey());
									//If we end here certificate is trusted. Check if it has expired.
									cert.checkValidity();
								}
								
							}
							
						} catch (Exception  e) {
							
						}

		}
	} };

	public static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	class TestErrorHandler extends DefaultResponseErrorHandler {

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			// conversion logic for decoding conversion
			System.err.println(IOUtils.toString(response.getBody(), Charset.defaultCharset()));
		}
	}

}
