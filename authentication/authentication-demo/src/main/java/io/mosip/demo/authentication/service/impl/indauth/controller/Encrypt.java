package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.demo.authentication.service.dto.EncryptionRequestDto;
import io.mosip.demo.authentication.service.dto.EncryptionResponseDto;
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

	private static final Provider provider = new BouncyCastleProvider();

	@PostMapping(path = "/identity/encrypt")
	@ApiOperation(value = "Encrypt Identity with sessionKey and Encrypt Session Key with Public Key", response = EncryptionResponseDto.class)
	public EncryptionResponseDto encrypt(@RequestBody EncryptionRequestDto encryptionRequestDto)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, KeyManagementException {
		EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();

		SecretKey sessionKey = keyGenerator.getSymmetricKey();
		ObjectMapper objMapper = new ObjectMapper();
		// Encrypt data with session key
		Map<String, Object> identityRequest = encryptionRequestDto.getIdentityRequest();
		byte[] data = objMapper.writeValueAsBytes(identityRequest);
		byte[] encryptedData = encryptor.symmetricEncrypt(sessionKey, data);
		encryptionResponseDto.setEncryptedIdentity(Base64.getEncoder().encodeToString(encryptedData));

//		KeyPair asymmetricKey = keyGenerator.getAsymmetricKey();
//		PublicKey publicKey = asymmetricKey.getPublic();
//		byte[] privateKey = asymmetricKey.getPrivate().getEncoded();
//		storePrivateKey(privateKey, encryptionRequestDto.getTspID());
		PublicKey publicKey = loadPublicKey();

		// Encrypt session Key with public Key
		byte[] encryptedsessionKey = encryptor.asymmetricPublicEncrypt(publicKey, sessionKey.getEncoded());
		encryptionResponseDto.setEncryptedSessionKey(Base64.getEncoder().encodeToString(encryptedsessionKey));

		return encryptionResponseDto;
	}

	private PublicKey loadPublicKey()
			throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, KeyManagementException {
		byte[] publicKeyBytes = getPublicKey();
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
		return publicKey;
	}

//	private void storePrivateKey(byte[] encodedvalue, String tspId) {
//		String localpath = environment.getProperty(FILEPATH);
//		Object[] homedirectory = new Object[] { System.getProperty("user.home") + File.separator };
//		String finalpath = MessageFormat.format(localpath, homedirectory);
//		BufferedWriter output = null;
//		try {
//			File fileInfo = new File(finalpath + File.separator + tspId);
//			File parentFile = fileInfo.getParentFile();
//			if (!parentFile.exists()) {
//				parentFile.mkdirs();
//			}
//			Files.write(fileInfo.toPath(), encodedvalue);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public byte[] getPublicKey() throws IOException, KeyManagementException, NoSuchAlgorithmException {

		byte[] output = null;
		JsonNode jsonNode = null;

		turnOffSslChecking();
		String url1 = "https://integ.mosip.io/keymanager/v1.0/publickey/{applicationId}";
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new TestErrorHandler());
		Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("applicationId", "IDA");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<Object> entity = new HttpEntity<Object>(headers);

		URI uri = UriComponentsBuilder.fromUriString(url1).buildAndExpand(uriVariables).toUri();
		uri = UriComponentsBuilder.fromUri(uri).queryParam("timeStamp", TIMESTAMP)
				.queryParam("referenceId", REFERENCEID.toString()).build().toUri();

		ResponseEntity<ObjectNode> exchange = restTemplate.exchange(uri, HttpMethod.GET, entity, ObjectNode.class);

		if (exchange.getBody().has(PUBLICKEY)) {
			jsonNode = exchange.getBody().get(PUBLICKEY);
		}

		output = SerializationUtils.serialize(jsonNode.toString());

		// String localpath = env.getProperty(FILEPATH);
		// Object[] homedirectory = new Object[] { System.getProperty("user.home") +
		// File.separator };
		// String finalpath = MessageFormat.format(localpath, homedirectory);
		// File fileInfo = new File(finalpath + File.separator + filename);
		// byte[] output = null;
		// if (fileInfo.exists()) {
		// output = Files.readAllBytes(fileInfo.toPath());
		// } else {
		// throw new IOException();
		// }
		return output;
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

	class TestErrorHandler extends DefaultResponseErrorHandler {

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			// conversion logic for decoding conversion
			System.err.println(IOUtils.toString(response.getBody(), Charset.defaultCharset()));
		}
	}

}
