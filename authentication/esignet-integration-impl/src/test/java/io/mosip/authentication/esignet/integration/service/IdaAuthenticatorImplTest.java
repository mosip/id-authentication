package io.mosip.authentication.esignet.integration.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWK;

import io.mosip.authentication.esignet.integration.dto.GetAllCertificatesResponse;
import io.mosip.authentication.esignet.integration.dto.IdaKycAuthRequest.Biometric;
import io.mosip.authentication.esignet.integration.dto.IdaKycAuthResponse;
import io.mosip.authentication.esignet.integration.dto.IdaKycExchangeResponse;
import io.mosip.authentication.esignet.integration.dto.IdaOtpResponse;
import io.mosip.authentication.esignet.integration.dto.IdaResponseWrapper;
import io.mosip.authentication.esignet.integration.dto.IdaSendOtpResponse;
import io.mosip.esignet.api.dto.AuthChallenge;
import io.mosip.esignet.api.dto.KycAuthDto;
import io.mosip.esignet.api.dto.KycAuthResult;
import io.mosip.esignet.api.dto.KycExchangeDto;
import io.mosip.esignet.api.dto.KycExchangeResult;
import io.mosip.esignet.api.dto.KycSigningCertificateData;
import io.mosip.esignet.api.dto.SendOtpDto;
import io.mosip.esignet.api.dto.SendOtpResult;
import io.mosip.esignet.api.exception.KycAuthException;
import io.mosip.esignet.api.exception.KycExchangeException;
import io.mosip.esignet.api.exception.KycSigningCertificateException;
import io.mosip.esignet.api.exception.SendOtpException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;
import io.mosip.kernel.signature.dto.JWTSignatureResponseDto;
import io.mosip.kernel.signature.service.SignatureService;
import io.mosip.authentication.esignet.integration.dto.Error;

@SuppressWarnings("deprecation")
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class IdaAuthenticatorImplTest {

	@InjectMocks
	IdaAuthenticatorImpl idaAuthenticatorImpl;

	@Mock
	ObjectMapper mapper;

	@Mock
	RestTemplate restTemplate;

	@Mock
	KeymanagerService keymanagerService;

	@Mock
	KeymanagerUtil keymanagerUtil;

	@Mock
	SignatureService signatureService;

	@Mock
	CryptoCore cryptoCore;

	@Mock
	DigestUtils digestUtils;

	private JWK clientJWK = TestUtil.generateJWK_RSA();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(idaAuthenticatorImpl, "sendOtpUrl", "https:/");
		ReflectionTestUtils.setField(idaAuthenticatorImpl, "kycExchangeUrl", "https://dev.mosip.net");
		ReflectionTestUtils.setField(idaAuthenticatorImpl, "idaVersion", "VersionIDA");
		ReflectionTestUtils.setField(idaAuthenticatorImpl, "symmetricAlgorithm", "AES");
		ReflectionTestUtils.setField(idaAuthenticatorImpl, "symmetricKeyLength", 256);
		ReflectionTestUtils.setField(idaAuthenticatorImpl, "idaPartnerCertificateUrl", "https://test");
		ReflectionTestUtils.setField(idaAuthenticatorImpl, "kycAuthUrl", "https://testkycAuthUrl");
		ReflectionTestUtils.setField(idaAuthenticatorImpl, "authTokenUrl", "https://testAuthTokenUrl");
		ReflectionTestUtils.setField(idaAuthenticatorImpl, "getCertsUrl", "https://testGetCertsUrl");
		ReflectionTestUtils.setField(idaAuthenticatorImpl, "otpChannels", Arrays.asList("otp", "pin", "bio"));
	}

	@Test
	public void doKycAuth_withInvalidDetails_throwsException() throws Exception {
		KycAuthDto kycAuthDto = new KycAuthDto();
		kycAuthDto.setIndividualId("IND1234");
		kycAuthDto.setTransactionId("TRAN1234");
		AuthChallenge authChallenge = new AuthChallenge();
		authChallenge.setAuthFactorType("PIN");
		authChallenge.setChallenge("111111");
		List<AuthChallenge> authChallengeList = new ArrayList<>();
		authChallengeList.add(authChallenge);
		kycAuthDto.setChallengeList(authChallengeList);

		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class))).thenReturn("value");

		X509Certificate certificate = getCertificate();
		Mockito.when(keymanagerUtil.convertToCertificate(Mockito.anyString())).thenReturn(certificate);

		byte[] temp = new byte[2];
		Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(PublicKey.class), Mockito.any())).thenReturn(temp);

		JWTSignatureResponseDto responseDto = Mockito.mock(JWTSignatureResponseDto.class);
		responseDto.setJwtSignedData("jwtSignedData");

		Mockito.when(signatureService.jwtSign(Mockito.any())).thenReturn(responseDto);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<IdaResponseWrapper<IdaKycAuthResponse>>>any())).thenReturn(null);

		Assert.assertThrows(KycAuthException.class,
				() -> idaAuthenticatorImpl.doKycAuth("relyingId", "clientId", kycAuthDto));
	}

	@Test
	public void doKycAuth_withValidDetails_thenPass() throws Exception {
		KycAuthDto kycAuthDto = new KycAuthDto();
		kycAuthDto.setIndividualId("IND1234");
		kycAuthDto.setTransactionId("TRAN1234");
		AuthChallenge authChallenge = new AuthChallenge();
		authChallenge.setAuthFactorType("OTP");
		authChallenge.setChallenge("111111");
		List<AuthChallenge> authChallengeList = new ArrayList<>();
		authChallengeList.add(authChallenge);
		kycAuthDto.setChallengeList(authChallengeList);

		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class))).thenReturn("value");

		X509Certificate certificate = getCertificate();
		Mockito.when(keymanagerUtil.convertToCertificate(Mockito.anyString())).thenReturn(certificate);

		byte[] temp = new byte[2];
		Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(PublicKey.class), Mockito.any())).thenReturn(temp);

		JWTSignatureResponseDto responseDto = Mockito.mock(JWTSignatureResponseDto.class);
		responseDto.setJwtSignedData("jwtSignedData");

		Mockito.when(signatureService.jwtSign(Mockito.any())).thenReturn(responseDto);

		IdaKycAuthResponse idaKycAuthResponse = new IdaKycAuthResponse();
		idaKycAuthResponse.setAuthToken("authToken1234");
		idaKycAuthResponse.setKycToken("kycToken1234");
		idaKycAuthResponse.setKycStatus(true);

		IdaResponseWrapper<IdaKycAuthResponse> idaResponseWrapper = new IdaResponseWrapper<>();
		idaResponseWrapper.setResponse(idaKycAuthResponse);
		idaResponseWrapper.setTransactionID("TRAN123");
		idaResponseWrapper.setVersion("VER1");

		ResponseEntity<IdaResponseWrapper<IdaKycAuthResponse>> responseEntity = new ResponseEntity<IdaResponseWrapper<IdaKycAuthResponse>>(
				idaResponseWrapper, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<IdaResponseWrapper<IdaKycAuthResponse>>>any()))
				.thenReturn(responseEntity);

		KycAuthResult kycAuthResult = new KycAuthResult();

		kycAuthResult = idaAuthenticatorImpl.doKycAuth("relyingId", "clientId", kycAuthDto);

		Assert.assertEquals(kycAuthResult.getKycToken(), kycAuthResult.getKycToken());
	}

	@Test
	public void doKycAuth_withAuthChallengeNull_thenFail() throws Exception {
		KycAuthDto kycAuthDto = new KycAuthDto();
		kycAuthDto.setIndividualId("IND1234");
		kycAuthDto.setTransactionId("TRAN1234");
		kycAuthDto.setChallengeList(null);

		Assert.assertThrows(KycAuthException.class,
				() -> idaAuthenticatorImpl.doKycAuth("relyingId", "clientId", kycAuthDto));
	}

	@Test
	public void doKycAuth_withInvalidAuthChallenge_thenFail() throws Exception {
		KycAuthDto kycAuthDto = new KycAuthDto();
		kycAuthDto.setIndividualId("IND1234");
		kycAuthDto.setTransactionId("TRAN1234");
		AuthChallenge authChallenge = new AuthChallenge();
		authChallenge.setAuthFactorType("Test");
		authChallenge.setChallenge("111111");
		List<AuthChallenge> authChallengeList = new ArrayList<>();
		authChallengeList.add(authChallenge);
		kycAuthDto.setChallengeList(authChallengeList);

		Assert.assertThrows(KycAuthException.class,
				() -> idaAuthenticatorImpl.doKycAuth("relyingId", "clientId", kycAuthDto));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void doKycAuth_withBIOAuthChallenge_thenPass() throws Exception {
		KycAuthDto kycAuthDto = new KycAuthDto();
		kycAuthDto.setIndividualId("IND1234");
		kycAuthDto.setTransactionId("TRAN1234");
		AuthChallenge authChallenge = new AuthChallenge();
		authChallenge.setAuthFactorType("BIO");
		authChallenge.setChallenge("111111");
		List<AuthChallenge> authChallengeList = new ArrayList<>();
		authChallengeList.add(authChallenge);
		kycAuthDto.setChallengeList(authChallengeList);

		Biometric b = new Biometric();
		b.setData(
				"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
		b.setHash("Hash");
		b.setSessionKey("SessionKey");
		b.setSpecVersion("SepecV");
		b.setThumbprint("Thumbprint");
		List<Biometric> bioList = new ArrayList<>();
		bioList.add(b);
		Mockito.when(mapper.readValue(Mockito.any(byte[].class), Mockito.any(TypeReference.class))).thenReturn(bioList);

		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class))).thenReturn("value");

		X509Certificate certificate = getCertificate();
		Mockito.when(keymanagerUtil.convertToCertificate(Mockito.anyString())).thenReturn(certificate);

		byte[] temp = new byte[2];
		Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(PublicKey.class), Mockito.any())).thenReturn(temp);

		JWTSignatureResponseDto responseDto = Mockito.mock(JWTSignatureResponseDto.class);
		responseDto.setJwtSignedData("jwtSignedData");

		Mockito.when(signatureService.jwtSign(Mockito.any())).thenReturn(responseDto);

		IdaKycAuthResponse idaKycAuthResponse = new IdaKycAuthResponse();
		idaKycAuthResponse.setAuthToken("authToken1234");
		idaKycAuthResponse.setKycToken("kycToken1234");
		idaKycAuthResponse.setKycStatus(true);

		IdaResponseWrapper<IdaKycAuthResponse> idaResponseWrapper = new IdaResponseWrapper<>();
		idaResponseWrapper.setResponse(idaKycAuthResponse);
		idaResponseWrapper.setTransactionID("TRAN123");
		idaResponseWrapper.setVersion("VER1");

		ResponseEntity<IdaResponseWrapper<IdaKycAuthResponse>> responseEntity = new ResponseEntity<IdaResponseWrapper<IdaKycAuthResponse>>(
				idaResponseWrapper, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<IdaResponseWrapper<IdaKycAuthResponse>>>any()))
				.thenReturn(responseEntity);

		KycAuthResult kycAuthResult = new KycAuthResult();

		kycAuthResult = idaAuthenticatorImpl.doKycAuth("relyingId", "clientId", kycAuthDto);

		Assert.assertEquals(kycAuthResult.getKycToken(), kycAuthResult.getKycToken());
	}

	@Test
	public void doKycExchange_withValidDetails_thenPass() throws Exception {
		KycExchangeDto kycExchangeDto = new KycExchangeDto();
		kycExchangeDto.setIndividualId("IND1234");
		kycExchangeDto.setKycToken("KYCT123");
		kycExchangeDto.setTransactionId("TRAN123");
		List<String> acceptedClaims = new ArrayList<>();
		acceptedClaims.add("claims");
		kycExchangeDto.setAcceptedClaims(acceptedClaims);
		String[] claimsLacales = new String[] { "claims", "locales" };
		kycExchangeDto.setClaimsLocales(claimsLacales);

		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		JWTSignatureResponseDto responseDto = Mockito.mock(JWTSignatureResponseDto.class);
		responseDto.setJwtSignedData("jwtSignedData");

		Mockito.when(signatureService.jwtSign(Mockito.any())).thenReturn(responseDto);

		IdaKycExchangeResponse idaKycExchangeResponse = new IdaKycExchangeResponse();
		idaKycExchangeResponse.setEncryptedKyc("ENCRKYC123");

		IdaResponseWrapper<IdaKycExchangeResponse> idaResponseWrapper = new IdaResponseWrapper<>();
		idaResponseWrapper.setResponse(idaKycExchangeResponse);
		idaResponseWrapper.setTransactionID("TRAN123");
		idaResponseWrapper.setVersion("VER1");

		ResponseEntity<IdaResponseWrapper<IdaKycExchangeResponse>> responseEntity = new ResponseEntity<IdaResponseWrapper<IdaKycExchangeResponse>>(
				idaResponseWrapper, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<IdaResponseWrapper<IdaKycExchangeResponse>>>any()))
				.thenReturn(responseEntity);

		KycExchangeResult kycExchangeResult = idaAuthenticatorImpl.doKycExchange("relyingPartyId", "clientId",
				kycExchangeDto);

		Assert.assertEquals(idaKycExchangeResponse.getEncryptedKyc(), kycExchangeResult.getEncryptedKyc());
	}

	@Test
	public void doKycExchange_withInvalidDetails_thenFail() throws Exception {
		KycExchangeDto kycExchangeDto = new KycExchangeDto();
		kycExchangeDto.setIndividualId(null);
		kycExchangeDto.setKycToken("KYCT123");
		kycExchangeDto.setTransactionId("TRAN123");
		List<String> acceptedClaims = new ArrayList<>();
		acceptedClaims.add("claims");
		kycExchangeDto.setAcceptedClaims(acceptedClaims);
		String[] claimsLacales = new String[] { "claims", "locales" };
		kycExchangeDto.setClaimsLocales(claimsLacales);

		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		JWTSignatureResponseDto responseDto = Mockito.mock(JWTSignatureResponseDto.class);
		responseDto.setJwtSignedData("jwtSignedData");

		Mockito.when(signatureService.jwtSign(Mockito.any())).thenReturn(responseDto);

		IdaKycExchangeResponse idaKycExchangeResponse = new IdaKycExchangeResponse();
		idaKycExchangeResponse.setEncryptedKyc("ENCRKYC123");

		IdaResponseWrapper<IdaKycExchangeResponse> idaResponseWrapper = new IdaResponseWrapper<>();
		idaResponseWrapper.setResponse(null);
		idaResponseWrapper.setTransactionID("TRAN123");
		idaResponseWrapper.setVersion("VER1");

		ResponseEntity<IdaResponseWrapper<IdaKycExchangeResponse>> responseEntity = new ResponseEntity<IdaResponseWrapper<IdaKycExchangeResponse>>(
				idaResponseWrapper, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<IdaResponseWrapper<IdaKycExchangeResponse>>>any()))
				.thenReturn(responseEntity);

		Assert.assertThrows(KycExchangeException.class,
				() -> idaAuthenticatorImpl.doKycExchange("test-relyingPartyId", "test-clientId", kycExchangeDto));
	}

	@Test
	public void doKycExchange_withInvalidIndividualId_throwsException() throws KycExchangeException, Exception {
		KycExchangeDto kycExchangeDto = new KycExchangeDto();
		kycExchangeDto.setIndividualId("IND1234");
		kycExchangeDto.setKycToken("KYCT123");
		kycExchangeDto.setTransactionId("TRAN123");
		List<String> acceptedClaims = new ArrayList<>();
		acceptedClaims.add("claims");
		kycExchangeDto.setAcceptedClaims(acceptedClaims);
		String[] claimsLacales = new String[] { "claims", "locales" };
		kycExchangeDto.setClaimsLocales(claimsLacales);

		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		JWTSignatureResponseDto responseDto = Mockito.mock(JWTSignatureResponseDto.class);
		responseDto.setJwtSignedData("jwtSignedData");

		Mockito.when(signatureService.jwtSign(Mockito.any())).thenReturn(responseDto);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<IdaResponseWrapper<IdaKycExchangeResponse>>>any()))
				.thenReturn(null);

		Assert.assertThrows(KycExchangeException.class,
				() -> idaAuthenticatorImpl.doKycExchange("relyingId", "clientId", kycExchangeDto));
	}

	@Test
	public void sendOtp_withValidDetails_thenPass() throws Exception {
		SendOtpDto sendOtpDto = new SendOtpDto();
		sendOtpDto.setIndividualId("1234");
		sendOtpDto.setTransactionId("4567");
		List<String> otpChannelsList = new ArrayList<>();
		otpChannelsList.add("channel");
		sendOtpDto.setOtpChannels(otpChannelsList);

		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		JWTSignatureResponseDto responseDto = Mockito.mock(JWTSignatureResponseDto.class);
		responseDto.setJwtSignedData("jwtSignedData");

		Mockito.when(signatureService.jwtSign(Mockito.any())).thenReturn(responseDto);

		IdaOtpResponse idaOtpResponse = new IdaOtpResponse();
		idaOtpResponse.setMaskedEmail("a@gmail.com");
		idaOtpResponse.setMaskedMobile("1234567890");

		IdaSendOtpResponse idaSendOtpResponse = new IdaSendOtpResponse();
		idaSendOtpResponse.setTransactionID(sendOtpDto.getTransactionId());
		idaSendOtpResponse.setVersion("Version123");
		idaSendOtpResponse.setId("123");
		idaSendOtpResponse.setErrors(null);
		idaSendOtpResponse.setResponse(idaOtpResponse);

		ResponseEntity<IdaSendOtpResponse> responseEntity = new ResponseEntity<IdaSendOtpResponse>(idaSendOtpResponse,
				HttpStatus.OK);

		Mockito.when(restTemplate.exchange(any(), eq(IdaSendOtpResponse.class))).thenReturn(responseEntity);

		SendOtpResult sendOtpResult = idaAuthenticatorImpl.sendOtp("rly123", "cli123", sendOtpDto);

		Assert.assertEquals(sendOtpDto.getTransactionId(), sendOtpResult.getTransactionId());
	}

	@Test
	public void sendOtp_withInvalidTransactionId_throwsException() throws SendOtpException, Exception {
		SendOtpDto sendOtpDto = new SendOtpDto();
		sendOtpDto.setIndividualId("1234");
		sendOtpDto.setTransactionId("4567");
		List<String> otpChannelsList = new ArrayList<>();
		otpChannelsList.add("channel");
		sendOtpDto.setOtpChannels(otpChannelsList);

		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		JWTSignatureResponseDto responseDto = Mockito.mock(JWTSignatureResponseDto.class);
		responseDto.setJwtSignedData("jwtSignedData");

		Mockito.when(signatureService.jwtSign(Mockito.any())).thenReturn(responseDto);

		IdaOtpResponse idaOtpResponse = new IdaOtpResponse();
		idaOtpResponse.setMaskedEmail("a@gmail.com");
		idaOtpResponse.setMaskedMobile("1234567890");

		IdaSendOtpResponse idaSendOtpResponse = new IdaSendOtpResponse();
		idaSendOtpResponse.setTransactionID("1324");
		idaSendOtpResponse.setVersion("Version123");
		idaSendOtpResponse.setId("123");
		idaSendOtpResponse.setErrors(null);
		idaSendOtpResponse.setResponse(idaOtpResponse);

		ResponseEntity<IdaSendOtpResponse> responseEntity = new ResponseEntity<IdaSendOtpResponse>(idaSendOtpResponse,
				HttpStatus.OK);

		Mockito.when(restTemplate.exchange(any(), eq(IdaSendOtpResponse.class))).thenReturn(responseEntity);

		Assert.assertThrows(SendOtpException.class, () -> idaAuthenticatorImpl.sendOtp("rly123", "cli123", sendOtpDto));
	}
	
	@Test
	public void sendOtp_withInvalidDetails_throwsException() throws SendOtpException, Exception {
		SendOtpDto sendOtpDto = new SendOtpDto();
		sendOtpDto.setIndividualId(null);
		sendOtpDto.setTransactionId("4567");
		List<String> otpChannelsList = new ArrayList<>();
		otpChannelsList.add("channel");
		sendOtpDto.setOtpChannels(otpChannelsList);

		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		JWTSignatureResponseDto responseDto = Mockito.mock(JWTSignatureResponseDto.class);
		responseDto.setJwtSignedData("jwtSignedData");

		Mockito.when(signatureService.jwtSign(Mockito.any())).thenReturn(responseDto);

		IdaSendOtpResponse idaSendOtpResponse = new IdaSendOtpResponse();
		idaSendOtpResponse.setTransactionID("1324");
		idaSendOtpResponse.setVersion("Version123");
		idaSendOtpResponse.setId("123");
		Error error = new Error("ERR-001", "Invalid Input");
		idaSendOtpResponse.setErrors(Arrays.asList(error));
		idaSendOtpResponse.setResponse(null);

		ResponseEntity<IdaSendOtpResponse> responseEntity = new ResponseEntity<IdaSendOtpResponse>(idaSendOtpResponse,
				HttpStatus.OK);

		Mockito.when(restTemplate.exchange(any(), eq(IdaSendOtpResponse.class))).thenReturn(responseEntity);

		Assert.assertThrows(SendOtpException.class, () -> idaAuthenticatorImpl.sendOtp("rly123", "cli123", sendOtpDto));
	}

	private X509Certificate getCertificate() throws Exception {
		X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
		X500Principal dnName = new X500Principal("CN=Test");
		generator.setSubjectDN(dnName);
		generator.setIssuerDN(dnName); // use the same
		generator.setNotBefore(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
		generator.setNotAfter(new Date(System.currentTimeMillis() + 24 * 365 * 24 * 60 * 60 * 1000));
		generator.setPublicKey(clientJWK.toRSAKey().toPublicKey());
		generator.setSignatureAlgorithm("SHA256WITHRSA");
		generator.setSerialNumber(new BigInteger(String.valueOf(System.currentTimeMillis())));
		return generator.generate(clientJWK.toRSAKey().toPrivateKey());
	}

	@Test
	public void isSupportedOtpChannel_withInvalidChannel_thenFail() {
		Assert.assertFalse(idaAuthenticatorImpl.isSupportedOtpChannel("test"));
	}
	
	@Test
	public void isSupportedOtpChannel_withValidChannel_thenPass() {
		Assert.assertTrue(idaAuthenticatorImpl.isSupportedOtpChannel("OTP"));
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void getAllKycSigningCertificates_withValidDetails_thenPass() throws Exception {
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		ResponseWrapper responseWrapper = new ResponseWrapper();

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("authorization", "test-auth-token");
		ResponseEntity<ResponseWrapper> responseEntity = new ResponseEntity<ResponseWrapper>(responseWrapper, headers,
				HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<ResponseWrapper>>any())).thenReturn(responseEntity);

		GetAllCertificatesResponse getAllCertificatesResponse = new GetAllCertificatesResponse();
		getAllCertificatesResponse.setAllCertificates(new ArrayList<KycSigningCertificateData>());

		ResponseWrapper<GetAllCertificatesResponse> certsResponseWrapper = new ResponseWrapper<GetAllCertificatesResponse>();
		certsResponseWrapper.setId("test-id");
		certsResponseWrapper.setResponse(getAllCertificatesResponse);

		ResponseEntity<ResponseWrapper<GetAllCertificatesResponse>> certsResponseEntity = new ResponseEntity<ResponseWrapper<GetAllCertificatesResponse>>(
				certsResponseWrapper, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<ResponseWrapper<GetAllCertificatesResponse>>>any()))
				.thenReturn(certsResponseEntity);

		List<KycSigningCertificateData> signingCertificates = new ArrayList<>();

		signingCertificates = idaAuthenticatorImpl.getAllKycSigningCertificates();

		Assert.assertSame(signingCertificates, getAllCertificatesResponse.getAllCertificates());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void getAllKycSigningCertificates_withInvalidResponse_throwsException() throws Exception {
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		ResponseWrapper responseWrapper = new ResponseWrapper();

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("authorization", "test-auth-token");
		ResponseEntity<ResponseWrapper> responseEntity = new ResponseEntity<ResponseWrapper>(responseWrapper, headers,
				HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<ResponseWrapper>>any())).thenReturn(responseEntity);

		ResponseWrapper<GetAllCertificatesResponse> certsResponseWrapper = new ResponseWrapper<GetAllCertificatesResponse>();
		certsResponseWrapper.setId("test-id");
		List<ServiceError> errors = new ArrayList<>();
		ServiceError error = new ServiceError("ERR-001", "Certificates not found");
		errors.add(error);
		certsResponseWrapper.setErrors(errors);

		ResponseEntity<ResponseWrapper<GetAllCertificatesResponse>> certsResponseEntity = new ResponseEntity<ResponseWrapper<GetAllCertificatesResponse>>(
				certsResponseWrapper, HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<ResponseWrapper<GetAllCertificatesResponse>>>any()))
				.thenReturn(certsResponseEntity);

		Assert.assertThrows(KycSigningCertificateException.class,
				() -> idaAuthenticatorImpl.getAllKycSigningCertificates());
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void getAllKycSigningCertificates_withErrorResponse_throwsException() throws Exception {
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		ResponseWrapper responseWrapper = new ResponseWrapper();

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("authorization", "test-auth-token");
		ResponseEntity<ResponseWrapper> responseEntity = new ResponseEntity<ResponseWrapper>(responseWrapper, headers,
				HttpStatus.OK);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<ResponseWrapper>>any())).thenReturn(responseEntity);

		ResponseWrapper<GetAllCertificatesResponse> certsResponseWrapper = new ResponseWrapper<GetAllCertificatesResponse>();
		certsResponseWrapper.setId("test-id");
		List<ServiceError> errors = new ArrayList<>();
		ServiceError error = new ServiceError("ERR-001", "Certificates not found");
		errors.add(error);
		certsResponseWrapper.setErrors(errors);

		ResponseEntity<ResponseWrapper<GetAllCertificatesResponse>> certsResponseEntity = new ResponseEntity<ResponseWrapper<GetAllCertificatesResponse>>(
				certsResponseWrapper, HttpStatus.FORBIDDEN);

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<ResponseWrapper<GetAllCertificatesResponse>>>any()))
				.thenReturn(certsResponseEntity);

		Assert.assertThrows(KycSigningCertificateException.class,
				() -> idaAuthenticatorImpl.getAllKycSigningCertificates());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void getAllKycSigningCertificates_withInvalidToken_thenFail() throws Exception {
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("value");

		Mockito.when(restTemplate.exchange(Mockito.<RequestEntity<Void>>any(),
				Mockito.<ParameterizedTypeReference<ResponseWrapper>>any())).thenThrow(RuntimeException.class);

		Assert.assertThrows(KycSigningCertificateException.class,
				() -> idaAuthenticatorImpl.getAllKycSigningCertificates());
	}

}
