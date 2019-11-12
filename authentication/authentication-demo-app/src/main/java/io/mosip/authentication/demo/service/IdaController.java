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
@Component
public class IdaController {

	@Autowired
	private Environment env;

	private static final String ASYMMETRIC_ALGORITHM_NAME = "RSA";

	private static final String SSL = "SSL";

	ObjectMapper mapper = new ObjectMapper();

	@FXML
	ComboBox<String> irisCount;

	@FXML
	ComboBox<String> fingerCount;

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
	
	@FXML
	private Button sendAuthRequest;

	private String capture;
	
	private String previousHash;

	@FXML
	private void initialize() {
		responsetextField.setText(null);
		
		ObservableList<String> idTypeChoices = FXCollections.observableArrayList("UIN", "VID", "USERID");
		ObservableList<String> fingerCountChoices = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7",
				"8", "9", "10");
		fingerCount.setItems(fingerCountChoices);
		fingerCount.getSelectionModel().select(0);
		
		ObservableList<String> irisCountChoices = FXCollections.observableArrayList("Left Iris", "Right Iris", "Both Iris");
		irisCount.setItems(irisCountChoices);
		irisCount.getSelectionModel().select(0);
		
		idTypebox.setItems(idTypeChoices);
		idTypebox.setValue("UIN");
		otpAnchorPane.setDisable(true);
		bioAnchorPane.setDisable(true);
		responsetextField.setDisable(true);
		sendAuthRequest.setDisable(true);
		
		idValue.textProperty().addListener((observable, oldValue, newValue) -> {
			updateSendButton();
		});
		
		otpValue.textProperty().addListener((observable, oldValue, newValue) -> {
			updateSendButton();
		});
	}

	@FXML
	private void onFingerPrintAuth() {
		updateBioCapture();
	}

	private void updateBioCapture() {
		capture = null;
		previousHash = null;
		updateBioPane();
		updateSendButton();
	}

	@FXML
	private void onIrisAuth() {
		updateBioCapture();
	}
	
	@FXML
	private void onFaceAuth() {
		updateBioCapture();
	}
	
	private void updateSendButton() {
		if(idValue.getText() == null || idValue.getText().trim().isEmpty()) {
			sendAuthRequest.setDisable(true);
			return;
		}
		
		if(otpAuthType.isSelected()) {
			if(otpValue.getText().trim().isEmpty()) {
				sendAuthRequest.setDisable(true);
				return;
			}
		}
		
		if(isBioAuthType()) {
			if(capture == null) {
				sendAuthRequest.setDisable(true);
				return;
			}
		}
		
		sendAuthRequest.setDisable(!(isBioAuthType() || otpAuthType.isSelected()));
		
		
	}

	private void updateBioPane() {
		if (isBioAuthType()) {
			bioAnchorPane.setDisable(false);
		} else {
			bioAnchorPane.setDisable(true);
		}
		irisCount.setDisable(!irisAuthType.isSelected());
		fingerCount.setDisable(!fingerAuthType.isSelected());
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
		responsetextField.setFont(Font.font("Times New Roman", javafx.scene.text.FontWeight.EXTRA_BOLD, 20));
		if (fingerAuthType.isSelected()) {
			capture = captureFingerprint();
		} 
		if (irisAuthType.isSelected()) {
			capture = captureIris();
		} 
		if (faceAuthType.isSelected()) {
			capture = captureFace();
		}
		
		updateSendButton();
	}

	private String captureFingerprint() throws Exception {
		responsetextField.setText("Capturing Fingerprint...");
		responsetextField.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold");

		String requestBody = "{\n" + 
				"	\"env\": \"Staging\",\n" + 
				"	\"purpose\": \"Auth\",\n" + 
				"	\"specVersion\": \"0.9.2\",\n" + 
				"	\"timeout\": 10000,\n" + 
				"	\"captureTime\": \"0001-01-01T00:00:00\",\n" + 
				"	\"transactionId\": \"1234567890\",\n" + 
				"	\"bio\": [{\n" + 
				"		\"type\": \"FIR\",\n" + 
				"		\"count\": " + getFingerCount() + ",\n" + 
				"		\"requestedScore\": 60,\n" + 
				"		\"deviceId\": \"" + env.getProperty("finger.deviceId") + "\",\n" + 
				"		\"deviceSubId\": " + getFingerDeviceSubId() + ",\n" + 
				"		\"previousHash\": \"" + getPreviousHash() + "\"\n" + 
				"	}],\n" + 
				"	\"customOpts\": [{\n" + 
				"		\"Name\": \"name1\",\n" + 
				"		\"Value\": \"value1\"\n" + 
				"	}]\n" + 
				"}";
		return capturebiometrics(requestBody);
	}
	
	private String getFingerDeviceSubId() {
		return "0";
	}
	
	private String getIrisDeviceSubId() {
		if(irisCount.getSelectionModel().getSelectedIndex() == 0) {
			return String.valueOf(1);
		} else if(irisCount.getSelectionModel().getSelectedIndex() == 1) {
			return String.valueOf(2);
		} else {
			return String.valueOf(3);
		}
	}

	private String getFaceDeviceSubId() {
		return "0";
	}
	
	private String captureIris() throws Exception {
		responsetextField.setText("Capturing Iris...");
		responsetextField.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold");

		String requestBody = "{\n" + 
				"	\"env\": \"Staging\",\n" + 
				"	\"purpose\": \"Auth\",\n" + 
				"	\"specVersion\": \"0.9.2\",\n" + 
				"	\"timeout\": 10000,\n" + 
				"	\"captureTime\": \"0001-01-01T00:00:00\",\n" + 
				"	\"transactionId\": \"1234567890\",\n" + 
				"	\"bio\": [{\n" + 
				"		\"type\": \"IIR\",\n" + 
				"		\"count\": " + getIrisCount() + ",\n" + 
				"		\"requestedScore\": 60,\n" + 
				"		\"deviceId\": \"" + env.getProperty("iris.deviceId") + "\",\n" + 
				"		\"deviceSubId\": " + getIrisDeviceSubId() + ",\n" + 
				"		\"previousHash\": \"" + getPreviousHash() + "\"\n" + 
				"	}],\n" + 
				"	\"customOpts\": [{\n" + 
				"		\"Name\": \"name1\",\n" + 
				"		\"Value\": \"value1\"\n" + 
				"	}]\n" + 
				"}";
		return capturebiometrics(requestBody);
	}
	
	private String captureFace() throws Exception {
		responsetextField.setText("Capturing Face...");
		responsetextField.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold");

		String requestBody = "{\n" + 
				"	\"env\": \"Staging\",\n" + 
				"	\"purpose\": \"Auth\",\n" + 
				"	\"specVersion\": \"0.9.2\",\n" + 
				"	\"timeout\": 30000,\n" + 
				"	\"captureTime\": \"0001-01-01T00:00:00\",\n" + 
				"	\"transactionId\": \"1234567890\",\n" + 
				"	\"bio\": [{\n" + 
				"		\"type\": \"FACE\",\n" + 
				"		\"count\": " + getFaceCount() + ",\n" + 
				"		\"requestedScore\": 0,\n" + 
				"		\"deviceId\": \"" + env.getProperty("face.deviceId") + "\",\n" + 
				"		\"deviceSubId\": " + getFaceDeviceSubId() + ",\n" + 
				"		\"previousHash\": \"" + getPreviousHash() + "\"\n" + 
				"	}],\n" + 
				"	\"customOpts\": [{\n" + 
				"		\"Name\": \"name1\",\n" + 
				"		\"Value\": \"value1\"\n" + 
				"	}]\n" + 
				"}";
		return capturebiometrics(requestBody);
	}

	private String getPreviousHash() {
		return previousHash == null ? "" : previousHash;
	}

	private String getFingerCount() {
		return fingerCount.getValue() == null ? String.valueOf(1) : fingerCount.getValue();
	}
	
	private String getIrisCount() {
		return String.valueOf(irisCount.getSelectionModel().getSelectedIndex() + 1);
	}
	
	private String getFaceCount() {
		return String.valueOf(1);
	}

	@SuppressWarnings("rawtypes")
	private String capturebiometrics(String requestBody) throws Exception {
		System.out.println("Capture request:\n" + requestBody);
		CloseableHttpClient client = HttpClients.createDefault();
		StringEntity requestEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
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
			responsetextField.setStyle("-fx-text-fill: green; -fx-font-size: 20px; -fx-font-weight: bold");
			ObjectMapper objectMapper = new ObjectMapper();
			List dataList = (List) objectMapper.readValue(result.getBytes(), Map.class).get("biometrics");
			for (int i = 0; i < dataList.size(); i++) {
				Map b = (Map) dataList.get(i);
				String dataJws = (String) b.get("data");
				Map dataMap = objectMapper.readValue(CryptoUtil.decodeBase64(dataJws.split("\\.")[1]), Map.class);
				System.out.println((i+1) + " Bio-type: " + dataMap.get("bioType") + " Bio-sub-type: " +  dataMap.get("bioSubType"));
				previousHash = (String) b.get("hash");
			}
		} else {
			responsetextField.setText("Capture Failed");
			responsetextField.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-font-weight: bold");
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
							getBaseUrl() + "/idauthentication/v1/otp/1873299273/735899345"),
					HttpMethod.POST, httpEntity, Map.class);
			System.err.println(response);
			
			if (response.getStatusCode().is2xxSuccessful()) {
				List errors = ((List) response.getBody().get("errors"));
				boolean status = errors == null || errors.isEmpty();
				String responseText = status ? "OTP Request Success" : "OTP Request Failed";
				if (status) {
					responsetextField.setStyle("-fx-text-fill: green; -fx-font-size: 20px; -fx-font-weight: bold");
				} else {
					responsetextField.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-font-weight: bold");
				}
				responsetextField.setText(responseText);
			} else {
				responsetextField.setText("OTP Request Failed with Error");
				responsetextField.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-font-weight: bold");
			}

		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private String getBaseUrl() {
		return env.getProperty("base.url", "https://qa.mosip.io");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@FXML
	private void onSendAuthRequest() throws Exception {
		responsetextField.setText(null);
		responsetextField.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold");
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
		} catch (Exception e) {
			e.printStackTrace();
			responsetextField.setText("Encryption of Auth Request Failed");
			return;
		}

		responsetextField.setText("Authenticating...");
		// Set request block
		authRequestDTO.setRequest(requestDTO);

		authRequestDTO.setTransactionID(getTransactionID());
		authRequestDTO.setRequestTime(getUTCCurrentDateTimeString());
		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setId(getAuthRequestId());
		authRequestDTO.setVersion("1.0");

		Map<String, Object> authRequestMap = mapper.convertValue(authRequestDTO, Map.class);
		authRequestMap.replace("request", kernelEncrypt.getEncryptedIdentity());
		authRequestMap.replace("requestSessionKey", kernelEncrypt.getEncryptedSessionKey());
		authRequestMap.replace("requestHMAC", kernelEncrypt.getRequestHMAC());
		RestTemplate restTemplate = createTemplate();
		HttpEntity<Map> httpEntity = new HttpEntity<>(authRequestMap);
		String url = getUrl();
		System.out.println("Auth URL: " + url);
		
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			responsetextField.setText("Authentication Failed with Error");
			responsetextField.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-font-weight: bold");
		}
	}

	private String getAuthRequestId() {
		return env.getProperty("authRequestId", "mosip.identity.auth");
	}

	private boolean isOtpAuthType() {
		return otpAuthType.isSelected();
	}

	private String getUrl() {
		return env.getProperty("ida.auth.url",
				getBaseUrl() + "/idauthentication/v1/auth/1873299273/735899345");
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
						env.getProperty("ida.publickey.url", getBaseUrl() + "/v1/keymanager/publickey/IDA"))
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
						getBaseUrl() + "/v1/authmanager/authenticate/clientidsecretkey"))
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
		fingerCount.getSelectionModel().select(0);
		irisCount.getSelectionModel().select(0);
		idValue.setText("");
		fingerAuthType.setSelected(false);
		irisAuthType.setSelected(false);
		faceAuthType.setSelected(false);
		otpAuthType.setSelected(false);
		idTypebox.setValue("UIN");
		otpValue.setText("");
		otpAnchorPane.setDisable(true);
		bioAnchorPane.setDisable(true);
		responsetextField.setText("");
		sendAuthRequest.setDisable(false);
		capture = null;
		previousHash = null;
		updateBioPane();
		updateSendButton();
	}

	@FXML 
	private void onOtpValueUpdate() {
		updateSendButton();
	}
}
