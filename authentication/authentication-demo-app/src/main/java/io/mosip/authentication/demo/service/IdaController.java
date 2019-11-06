package io.mosip.authentication.demo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.X509EncodedKeySpec;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.demo.dto.AuthRequestDTO;
import io.mosip.authentication.demo.dto.AuthTypeDTO;
import io.mosip.authentication.demo.dto.CryptomanagerRequestDto;
import io.mosip.authentication.demo.dto.EncryptionRequestDto;
import io.mosip.authentication.demo.dto.EncryptionResponseDto;
import io.mosip.authentication.demo.dto.OtpRequestDTO;
import io.mosip.authentication.demo.dto.RequestDTO;
import io.mosip.authentication.demo.helper.CryptoUtility;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

/**
 * The Class IdaController.
 * 
 * @author Sanjay Murali
 */
@SuppressWarnings("restriction")
@Component
public class IdaController {

	@Autowired
	private Environment env;

	private static final String ASYMMETRIC_ALGORITHM_NAME = "RSA";

	private static final String SSL = "SSL";

	ObjectMapper mapper = new ObjectMapper();

	ComboBox<String> fingerCount;

	@FXML
	ComboBox<String> count;

	@FXML
	private TextField idValue;

	@FXML
	private CheckBox fingerAuthType;

	@FXML
	private CheckBox irisAuthType;
	
	@FXML
	private CheckBox faceAuthType;

	@FXML
	private CheckBox otpAuthType;

	@FXML
	private ComboBox<String> idTypebox;

	@FXML
	private TextField otpValue;

	@FXML
	private AnchorPane otpAnchorPane;

	@FXML
	private AnchorPane bioAnchorPane;

	@FXML
	private TextField responsetextField;

	@FXML
	private ImageView img;

	@FXML
	private Button requestOtp;

	private String capture;

	@FXML
	private void initialize() {
		responsetextField.setText(null);
		ObservableList<String> idTypeChoices = FXCollections.observableArrayList("UIN", "VID", "USERID");
		count.setValue("1");
		idTypebox.setItems(idTypeChoices);
		idTypebox.setValue("UIN");
		otpAnchorPane.setDisable(true);
		bioAnchorPane.setDisable(true);
		responsetextField.setDisable(true);
	}

	@FXML
	private void onFingerPrintAuth() {
		responsetextField.setText(null);
		if (fingerAuthType.isSelected()) {
			bioAnchorPane.setDisable(false);
		} else {
			bioAnchorPane.setDisable(true);
		}
		irisAuthType.setSelected(false);
		faceAuthType.setSelected(false);
		ObservableList<String> fingerCountChoices = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7",
				"8", "9", "10");
		count.setItems(fingerCountChoices);
	}

	@FXML
	private void onIrisAuth() {
		responsetextField.setText(null);
		if (irisAuthType.isSelected()) {
			bioAnchorPane.setDisable(false);
		} else {
			bioAnchorPane.setDisable(true);
		}
		fingerAuthType.setSelected(false);
		faceAuthType.setSelected(false);
		ObservableList<String> irisCountChoices = FXCollections.observableArrayList("1", "2");
		count.setItems(irisCountChoices);
	}
	
	@FXML
	private void onFaceAuth() {
		responsetextField.setText(null);
		if (faceAuthType.isSelected()) {
			bioAnchorPane.setDisable(false);
		} else {
			bioAnchorPane.setDisable(true);
		}
		fingerAuthType.setSelected(false);
		irisAuthType.setSelected(false);
		ObservableList<String> faceCountChoices = FXCollections.observableArrayList("1");
		count.setItems(faceCountChoices);
	}

	@FXML
	private void onOTPAuth() {
		responsetextField.setText(null);
		otpAnchorPane.setDisable(!otpAnchorPane.isDisable());
	}

	@FXML
	private void onIdTypeChange() {
		responsetextField.setText(null);
	}

	@FXML
	private void onSubTypeSelection() {
		responsetextField.setText(null);
	}

	@FXML
	private void onCapture() throws Exception {
		responsetextField.setText("capturing...");
		responsetextField.setFont(Font.font("Times New Roman", javafx.scene.text.FontWeight.EXTRA_BOLD, 20));
		if (fingerAuthType.isSelected()) {
			capture = captureFingerprint();
		} else if (irisAuthType.isSelected()) {
			capture = captureIris();
		} else  if (faceAuthType.isSelected()) {
			capture = captureFace();
		}
	}

	private String captureFingerprint() throws Exception {
		String requestBody = "{\"env\":\"Staging\",\"mosipProcess\":\"Auth\",\"version\":\"1.0\",\"timeout\":10000,\"captureTime\":\"0001-01-01T00:00:00\",\"transactionId\":\"1234567890\",\"bio\":[{\"type\":\"FIR\",\"count\":"
				+ count.getValue()
				+ ",\"exception\":[],\"requestedScore\":60,\"deviceId\":\"" + env.getProperty("finger.deviceId") + "\",\"deviceSubId\":0,\"previousHash\":\"\"}],\"customOpts\":[{\"Name\":\"name1\",\"Value\":\"value1\"}]}";

		return capturebiometrics(requestBody);
	}
	
	private String captureIris() throws Exception {
		String requestBody = "{\"env\":\"Staging\",\"mosipProcess\":\"Auth\",\"version\":\"1.0\",\"timeout\":10000,\"captureTime\":\"0001-01-01T00:00:00\",\"transactionId\":\"1234567890\",\"bio\":[{\"type\":\"IIR\",\"count\":"
				+ count.getValue()
				+ ",\"exception\":[],\"requestedScore\":60,\"deviceId\":\"" + env.getProperty("iris.deviceId") + "\",\"deviceSubId\":3,\"previousHash\":\"\"}],\"customOpts\":[{\"Name\":\"name1\",\"Value\":\"value1\"}]}";
		Map irisData = mapper.readValue(capturebiometrics(requestBody), Map.class);
		if (count.getValue().equals("1")) {
			((List) irisData.get("biometrics")).remove(1);
		}
		return mapper.writeValueAsString(irisData);
	}
	
	private String captureFace() throws Exception {
		String requestBody = "{\"env\":\"Staging\",\"mosipProcess\":\"Auth\",\"version\":\"1.0\",\"timeout\":40000,\"captureTime\":\"0001-01-01T00:00:00\",\"transactionId\":\"1234567890\",\"bio\":[{\"type\":\"FACE\",\"count\":"
				+ count.getValue()
				+ ",\"exception\":[],\"requestedScore\":0,\"deviceId\":\"" + env.getProperty("face.deviceId") + "\",\"deviceSubId\":0,\"previousHash\":\"\"}],\"customOpts\":[{\"Name\":\"name1\",\"Value\":\"value1\"}]}";
		return mapper.writeValueAsString(mapper.readValue(capturebiometrics(requestBody), Map.class));
	}

	private String capturebiometrics(String requestBody) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		StringEntity requestEntity = new StringEntity(requestBody, ContentType.create("Content-Type", Consts.UTF_8));
		HttpUriRequest request = RequestBuilder.create("CAPTURE").setUri("http://127.0.0.1:4501/capture")
				.setEntity(requestEntity).build();
		CloseableHttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			response = client.execute(request);

			InputStream inputStram = response.getEntity().getContent();
			BufferedReader bR = new BufferedReader(new InputStreamReader(inputStram));
			String line = null;
			while ((line = bR.readLine()) != null) {
				stringBuilder.append(line);
			}
			bR.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = stringBuilder.toString();
		String error = ((Map) mapper.readValue(result, Map.class).get("error")).get("errorCode").toString();
		if (error.equals("0")) {
			responsetextField.setText("Capture Success");
		} else {
			responsetextField.setText("Capture Failed");
		}
		System.out.println(result);
		return result;
	}

	@SuppressWarnings("rawtypes")
	@FXML
	private void onRequestOtp() {
		responsetextField.setText(null);
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		otpRequestDTO.setId("mosip.identity.otp");
		otpRequestDTO.setIndividualId(idValue.getText());
		otpRequestDTO.setIndividualIdType(idTypebox.getValue());
		otpRequestDTO.setOtpChannel(Collections.singletonList("email"));
		otpRequestDTO.setRequestTime(getUTCCurrentDateTimeString());
		otpRequestDTO.setTransactionID(getTransactionID());
		otpRequestDTO.setVersion("1.0");

		try {
			RestTemplate restTemplate = createTemplate();
			HttpEntity<OtpRequestDTO> httpEntity = new HttpEntity<>(otpRequestDTO);
			ResponseEntity<Map> response = restTemplate.exchange(
					env.getProperty("ida.otp.url",
							"https://ext-int.mosip.io/idauthentication/v1/otp/1873299273/735899345"),
					HttpMethod.POST, httpEntity, Map.class);
			System.err.println(response);
			responsetextField.setText("OTP sent");
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@FXML
	private void onSendAuthRequest() throws Exception {
		responsetextField.setText(null);
		responsetextField.setText("Preparing Auth Request...");
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		// Set Auth Type
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setBio(isBioAuthType());
		authTypeDTO.setOtp(isOtpAuthType());
		authRequestDTO.setRequestedAuth(authTypeDTO);
		// set Individual Id
		authRequestDTO.setIndividualId(idValue.getText());
		// Set Individual Id type
		authRequestDTO.setIndividualIdType(idTypebox.getValue());

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setTimestamp(getUTCCurrentDateTimeString());

		if (isOtpAuthType()) {
			requestDTO.setOtp(otpValue.getText());
		}
		
		Map<String, Object> identityBlock = mapper.convertValue(requestDTO, Map.class);
		if (isBioAuthType()) {
			identityBlock.put("biometrics", mapper.readValue(capture, Map.class).get("biometrics"));
		}
		responsetextField.setText("Encrypting Auth Request...");
		System.err.println("******* Request before encryption ************ \n\n");
		System.err.println(identityBlock);
		EncryptionRequestDto encryptionRequestDto = new EncryptionRequestDto();
		encryptionRequestDto.setIdentityRequest(identityBlock);
		EncryptionResponseDto kernelEncrypt = null;
		try {
			kernelEncrypt = kernelEncrypt(encryptionRequestDto, false);
			responsetextField.setText("Encryption successful...Authenticating");
		} catch (Exception e) {
			e.printStackTrace();
			responsetextField.setText("Encryption of Auth Request Failed");
		}
		// Set request block
		authRequestDTO.setRequest(requestDTO);

		authRequestDTO.setTransactionID(getTransactionID());
		authRequestDTO.setRequestTime(getUTCCurrentDateTimeString());
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setVersion("1.0");

		Map<String, Object> authRequestMap = mapper.convertValue(authRequestDTO, Map.class);
		authRequestMap.replace("request", kernelEncrypt.getEncryptedIdentity());
		authRequestMap.replace("requestSessionKey", kernelEncrypt.getEncryptedSessionKey());
		authRequestMap.replace("requestHMAC", kernelEncrypt.getRequestHMAC());
		RestTemplate restTemplate = createTemplate();
		HttpEntity<Map> httpEntity = new HttpEntity<>(authRequestMap);
		String url = getUrl();
		ResponseEntity<Map> authResponse = restTemplate.exchange(url,
				HttpMethod.POST, httpEntity, Map.class);
		if (authResponse.getStatusCode().is2xxSuccessful()) {
			boolean status = (boolean) ((Map<String, Object>) authResponse.getBody().get("response")).get("authStatus");
			String response = status ? "Authentication Success" : "Authentication Failed";
			if (status) {
				responsetextField.setStyle("-fx-text-fill: green; -fx-font-size: 20px; -fx-font-weight: bold");
			} else {
				responsetextField.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-font-weight: bold");
			}
			responsetextField.setText(response);
		} else {
			responsetextField.setText("Authentication Failed with Error");
			responsetextField.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-font-weight: bold");
		}


		System.err.println("Auth Request : \n" + new ObjectMapper().writeValueAsString(authRequestMap));
		System.out.println(identityBlock);
		
		System.err.println("Auth Response : \n" + new ObjectMapper().writeValueAsString(authResponse));
		System.err.println(authResponse.getBody());
	}

	private boolean isOtpAuthType() {
		return otpAuthType.isSelected();
	}

	private String getUrl() {
		return env.getProperty("ida.auth.url",
				"http://52.172.53.239:8090/idauthentication/v1/auth/1873299273/735899345");
	}

	private boolean isBioAuthType() {
		return fingerAuthType.isSelected() || irisAuthType.isSelected() || faceAuthType.isSelected();
	}

	private EncryptionResponseDto kernelEncrypt(EncryptionRequestDto encryptionRequestDto, boolean isInternal)
			throws Exception {
		EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();
		String identityBlock = mapper.writeValueAsString(encryptionRequestDto.getIdentityRequest());

		SecretKey secretKey = cryptoUtil.genSecKey();

		byte[] encryptedIdentityBlock = cryptoUtil.symmetricEncrypt(identityBlock.getBytes(), secretKey);
		encryptionResponseDto.setEncryptedIdentity(Base64.encodeBase64URLSafeString(encryptedIdentityBlock));
		String publicKeyStr = getPublicKey(identityBlock, isInternal);
		PublicKey publicKey = KeyFactory.getInstance(ASYMMETRIC_ALGORITHM_NAME)
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(publicKeyStr)));
		byte[] encryptedSessionKeyByte = cryptoUtil.asymmetricEncrypt((secretKey.getEncoded()), publicKey);
		encryptionResponseDto.setEncryptedSessionKey(Base64.encodeBase64URLSafeString(encryptedSessionKeyByte));
		byte[] byteArr = cryptoUtil.symmetricEncrypt(
				HMACUtils.digestAsPlainText(HMACUtils.generateHash(identityBlock.getBytes())).getBytes(), secretKey);
		encryptionResponseDto.setRequestHMAC(Base64.encodeBase64URLSafeString(byteArr));
		return encryptionResponseDto;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getPublicKey(String data, boolean isInternal)
			throws KeyManagementException, RestClientException, NoSuchAlgorithmException {
		RestTemplate restTemplate = createTemplate();

		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
		request.setApplicationId("IDA");
		request.setData(Base64.encodeBase64URLSafeString(data.getBytes(StandardCharsets.UTF_8)));
		String publicKeyId = env.getProperty("publicKeyId", "PARTNER");
		request.setReferenceId(publicKeyId);
		String utcTime = DateUtils.getUTCCurrentDateTimeString();
		request.setTimeStamp(utcTime);
		Map<String, String> uriParams = new HashMap<>();
		uriParams.put("appId", "IDA");
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(
						env.getProperty("ida.publickey.url", "https://ext-int.mosip.io/v1/keymanager/publickey/IDA"))
				.queryParam("timeStamp", DateUtils.getUTCCurrentDateTimeString())
				.queryParam("referenceId", publicKeyId);
		ResponseEntity<Map> response = restTemplate.exchange(builder.build(uriParams), HttpMethod.GET, null, Map.class);
		return (String) ((Map<String, Object>) response.getBody().get("response")).get("publicKey");
	}

	private RestTemplate createTemplate() throws KeyManagementException, NoSuchAlgorithmException {
		turnOffSslChecking();
		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {
				String authToken = generateAuthToken();
				if (authToken != null && !authToken.isEmpty()) {
					request.getHeaders().set("Cookie", "Authorization=" + authToken);
				}
				return execution.execute(request, body);
			}
		};

		restTemplate.setInterceptors(Collections.singletonList(interceptor));
		return restTemplate;
	}

	private String generateAuthToken() {
		ObjectNode requestBody = mapper.createObjectNode();
		requestBody.put("clientId", "ida_app_user");
		requestBody.put("secretKey", "5debb60adbfcf8feea4a6ed6160092ec");
		requestBody.put("appId", "ida");
		RequestWrapper<ObjectNode> request = new RequestWrapper<>();
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		request.setRequest(requestBody);
		ClientResponse response = WebClient
				.create(env.getProperty("ida.authmanager.url",
						"https://ext-int.mosip.io/v1/authmanager/authenticate/clientidsecretkey"))
				.post().syncBody(request).exchange().block();
		List<ResponseCookie> list = response.cookies().get("Authorization");
		if (list != null && !list.isEmpty()) {
			ResponseCookie responseCookie = list.get(0);
			return responseCookie.getValue();
		}
		return "";
	}

	@SuppressWarnings("unused")
	private HttpEntity<CryptomanagerRequestDto> getHeaders(CryptomanagerRequestDto req) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<CryptomanagerRequestDto>(req, headers);
	}

	private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String arg1)
				throws CertificateException {
		}
	} };

	@Autowired
	private CryptoUtility cryptoUtil;

	public static void turnOffSslChecking() throws KeyManagementException, java.security.NoSuchAlgorithmException {
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance(SSL);
		sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	public static String getUTCCurrentDateTimeString() {
		return OffsetDateTime.now().toInstant().toString();
	}

	public static String getTransactionID() {
		return "1234567890";
	}

	@FXML
	private void onReset() {
		count.setValue("1");
		idValue.setText(null);
		fingerAuthType.setSelected(false);
		irisAuthType.setSelected(false);
		otpAuthType.setSelected(false);
		idTypebox.setValue("UIN");
		otpValue.setText(null);
		otpAnchorPane.setDisable(true);
		bioAnchorPane.setDisable(true);
		responsetextField.setText(null);
	}
}
