package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
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
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.demo.authentication.service.EncryptHelper.CryptoUtility;
import io.mosip.demo.authentication.service.dto.CryptomanagerRequestDto;
import io.mosip.demo.authentication.service.dto.CryptomanagerResponseDto;
import io.mosip.demo.authentication.service.dto.EncryptedRequest;
import io.mosip.demo.authentication.service.dto.EncryptionRequestDto;
import io.mosip.demo.authentication.service.dto.EncryptionResponseDto;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.swagger.annotations.ApiOperation;


/**
 * The Class Encrypt.
 *
 * @author Dinesh Karuppiah
 * @author A
 */

@RestController
public class Encrypt {

	/** The Constant FILEPATH. */
	private static final String FILEPATH = "sample.privatekey.filepath";
	
	/** The Constant FORMAT. */
	private static final String FORMAT = "UTF-8";

	/** The Constant RSA. */
	private static final String RSA = "RSA";

	/** The Constant TIMESTAMP. */
	private static final LocalDateTime TIMESTAMP = LocalDateTime.now();

	/** The Constant REFERENCEID. */
	private static final String REFERENCEID = "516283648960";

	/** The Constant PUBLICKEY. */
	private static final String PUBLICKEY = "publicKey";
	

	
	/** The Constant ASYMMETRIC_ALGORITHM. */
	private static final String ASYMMETRIC_ALGORITHM ="RSA";
	
	/** The Constant ASYMMETRIC_ALGORITHM. */
	private static final String SSL ="SSL";

	/** The environment. */
	@Autowired
	private Environment environment;

	/** The key generator. */
	@Autowired
	private KeyGenerator keyGenerator;

	/** The encryptor. */
	@Autowired
	private EncryptorImpl encryptor;
	
	/** The obj mapper. */
	@Autowired
	private ObjectMapper objMapper;
	
/**  KeySplitter. */
	
	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;
	
	/** The encrypt URL. */
	@Value("${mosip.kernel.encrypt-url}")
	private String encryptURL;
	
	/** The app ID. */
	@Value("${application.id}")
	private String appID;

	/** The Constant provider. */
	private static final Provider provider = new BouncyCastleProvider();

	/**
	 * Encrypt.
	 *
	 * @param encryptionRequestDto the encryption request dto
	 * @return the encryption response dto
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws InvalidKeySpecException the invalid key spec exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws KeyManagementException the key management exception
	 * @throws RestClientException the rest client exception
	 * @throws JSONException the JSON exception
	 */
	@PostMapping(path = "/identity/encrypt")
	@ApiOperation(value = "Encrypt Identity with sessionKey and Encrypt Session Key with Public Key", response = EncryptionResponseDto.class)
	public EncryptionResponseDto encrypt(@RequestBody EncryptionRequestDto encryptionRequestDto)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, KeyManagementException,
			RestClientException, JSONException {
		
		return kernelEncrypt(encryptionRequestDto);

	}
	
	private EncryptionResponseDto kernelEncrypt(EncryptionRequestDto encryptionRequestDto) throws KeyManagementException, RestClientException, NoSuchAlgorithmException, JsonProcessingException, IOException, JSONException {
		
		String encryptedResponse = getEncryptedValue(objMapper.writeValueAsString(encryptionRequestDto.getIdentityRequest()), encryptionRequestDto.getTspID());
		System.err.println("Demo"+encryptedResponse);
		EncryptionResponseDto encryptionResponseDto = split(encryptedResponse);
        return encryptionResponseDto;
		
	}

	

	

	/**
	 * Split.
	 *
	 * @param value the value
	 * @return the encrypted request
	 */
	private EncryptionResponseDto split(String value) {
		EncryptionResponseDto encryptionResponse = new EncryptionResponseDto();
		byte[] encryptedHybridData = Base64.decodeBase64(value);
		int keyDemiliterIndex = 0;
		keyDemiliterIndex = CryptoUtil.getSplitterIndex(encryptedHybridData, keyDemiliterIndex, keySplitter);
		byte[] encryptedKey = Arrays.copyOfRange(encryptedHybridData, 0, keyDemiliterIndex);
		byte[] encryptedData = Arrays.copyOfRange(encryptedHybridData, keyDemiliterIndex + keySplitter.length(),
				encryptedHybridData.length);
		encryptionResponse.setEncryptedSessionKey(Base64.encodeBase64URLSafeString(encryptedKey));
		encryptionResponse.setEncryptedIdentity(Base64.encodeBase64URLSafeString(encryptedData));
		return encryptionResponse;
	}



	/**
	 * Gets the encrypted value.
	 *
	 * @param data the data
	 * @param tspID the tsp ID
	 * @return the encrypted value
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws KeyManagementException the key management exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws RestClientException the rest client exception
	 * @throws JSONException the JSON exception
	 */
	public String getEncryptedValue(String data, String tspID)
			throws IOException, KeyManagementException, NoSuchAlgorithmException, RestClientException, JSONException {

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

	/**
	 * Gets the headers.
	 *
	 * @param req the req
	 * @return the headers
	 */
	private HttpEntity getHeaders(CryptomanagerRequestDto req)  {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity(req, headers);
	}

	/** The Constant UNQUESTIONING_TRUST_MANAGER. */
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

	/**
	 * Turn off ssl checking.
	 *
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyManagementException the key management exception
	 */
	public static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance(Encrypt.SSL);
		sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	/**
	 * The Class TestErrorHandler.
	 */
	class TestErrorHandler extends DefaultResponseErrorHandler {

		/* (non-Javadoc)
		 * @see org.springframework.web.client.DefaultResponseErrorHandler#handleError(org.springframework.http.client.ClientHttpResponse)
		 */
		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			// conversion logic for decoding conversion
			System.err.println(IOUtils.toString(response.getBody(), Charset.defaultCharset()));
		}
	}

}
