//package io.mosip.authentication.internal.service.manager;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.when;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.env.Environment;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestContext;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.context.WebApplicationContext;
//
//import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
//import io.mosip.kernel.core.exception.BaseUncheckedException;
//import io.mosip.kernel.crypto.jce.core.CryptoCore;
//import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
//import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
//import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
//import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
//import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
//import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
//import io.mosip.kernel.signature.dto.TimestampRequestDto;
//import io.mosip.kernel.signature.dto.ValidatorResponseDto;
//import io.mosip.kernel.signature.service.SignatureService;
//
///**
// * 
// * @author Nagarjuna
// *
// */
//@Ignore
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
//public class KeyServiceManagerTest {
//
//	@InjectMocks
//	KeyServiceManager keyServiceManager;
//	
//	@Mock
//	private CryptomanagerService cryptomanagerService;
//	
//	@Mock
//	private CryptoCore cryptoCore;
//	
//	@Mock
//	private KeymanagerService keymanagerService;
//	
//	@Mock
//	private SignatureService signatureService;
//	
//	@Autowired
//	private Environment env;
//	
//	@Before
//	public void before() {
//		ReflectionTestUtils.setField(keyServiceManager, "env", env);
//		ReflectionTestUtils.setField(keyServiceManager, "cryptomanagerService", cryptomanagerService);
//		ReflectionTestUtils.setField(keyServiceManager, "keymanagerService", keymanagerService);
//		ReflectionTestUtils.setField(keyServiceManager, "cryptoCore", cryptoCore);
//		ReflectionTestUtils.setField(keyServiceManager, "signatureService", signatureService);
//	}
//	
//	@Test
//	public void testGetPublicKey() throws IdAuthenticationBusinessException {
//		PublicKeyResponse<String> publicKeyResponse = new PublicKeyResponse<>();
//		publicKeyResponse.setPublicKey("YWJjZA");
//		when(keymanagerService.getPublicKey(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(publicKeyResponse);
//		java.util.Optional<String> refId = java.util.Optional.of("ida");
//		PublicKeyResponse<String> response = keyServiceManager.getPublicKey("applicationId", "timeStamp", refId);
//		assertEquals("YWJjZA", response.getPublicKey());
//	}
//	
//	@Test(expected = IdAuthenticationBusinessException.class)
//	public void testGetPublicKeyNoUniqueAliasException() throws IdAuthenticationBusinessException {
//		PublicKeyResponse<String> publicKeyResponse = new PublicKeyResponse<>();
//		publicKeyResponse.setPublicKey("YWJjZA");
//		when(keymanagerService.getPublicKey(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new NoUniqueAliasException("", ""));
//		java.util.Optional<String> refId = java.util.Optional.of("ida");
//		keyServiceManager.getPublicKey("applicationId", "timeStamp", refId);		
//	}
//	
//	@Test(expected = IdAuthenticationBusinessException.class)
//	public void testGetPublicKeyException() throws IdAuthenticationBusinessException {
//		PublicKeyResponse<String> publicKeyResponse = new PublicKeyResponse<>();
//		publicKeyResponse.setPublicKey("YWJjZA");
//		when(keymanagerService.getPublicKey(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new BaseUncheckedException("", ""));
//		java.util.Optional<String> refId = java.util.Optional.of("ida");
//		keyServiceManager.getPublicKey("applicationId", "timeStamp", refId);	
//	}
//	
//	@Test
//	public void testGetSignublicKey() throws IdAuthenticationBusinessException {
//		PublicKeyResponse<String> publicKeyResponse = new PublicKeyResponse<>();
//		publicKeyResponse.setPublicKey("YWJjZA");
//		when(keymanagerService.getSignPublicKey(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(publicKeyResponse);
//		java.util.Optional<String> refId = java.util.Optional.of("ida");
//		PublicKeyResponse<String> response = keyServiceManager.getSignPublicKey("applicationId", "timeStamp", refId);
//		assertEquals("YWJjZA", response.getPublicKey());
//	}
//	
//	@Test(expected = IdAuthenticationBusinessException.class)
//	public void testGetSignPublicKeyNoUniqueAliasException() throws IdAuthenticationBusinessException {
//		PublicKeyResponse<String> publicKeyResponse = new PublicKeyResponse<>();
//		publicKeyResponse.setPublicKey("YWJjZA");
//		when(keymanagerService.getSignPublicKey(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new NoUniqueAliasException("", ""));
//		java.util.Optional<String> refId = java.util.Optional.of("ida");
//		keyServiceManager.getSignPublicKey("applicationId", "timeStamp", refId);		
//	}
//	
//	@Test(expected = IdAuthenticationBusinessException.class)
//	public void testGetSignPublicKeyException() throws IdAuthenticationBusinessException {
//		PublicKeyResponse<String> publicKeyResponse = new PublicKeyResponse<>();
//		publicKeyResponse.setPublicKey("YWJjZA");
//		when(keymanagerService.getSignPublicKey(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new BaseUncheckedException("", ""));
//		java.util.Optional<String> refId = java.util.Optional.of("ida");
//		keyServiceManager.getSignPublicKey("applicationId", "timeStamp", refId);	
//	}
//
//	
//	@Test
//	public void testEncrypt() throws IdAuthenticationBusinessException {
//		when(cryptomanagerService.encrypt(Mockito.any()))
//				.thenReturn(new CryptomanagerResponseDto("YWJjZA"));
//		CryptomanagerResponseDto encrypt = keyServiceManager.encrypt("MOSIP", "20190101", null, null);
//		assertEquals("YWJjZA", encrypt.getData());
//	}
//	
//	@Test(expected = IdAuthenticationBusinessException.class)
//	public void testEncrypt_001() throws IdAuthenticationBusinessException {
//		when(cryptomanagerService.encrypt(Mockito.any()))
//				.thenThrow(new NoUniqueAliasException("IDA-MPA-004","No unique alias found"));
//		keyServiceManager.encrypt("MOSIP", "20190101", null, null);		
//	}
//	
//	@Test(expected = IdAuthenticationBusinessException.class)
//	public void testEncrypt_002() throws IdAuthenticationBusinessException {
//		when(cryptomanagerService.encrypt(Mockito.any()))
//				.thenThrow(new BaseUncheckedException("",""));
//		keyServiceManager.encrypt("MOSIP", "20190101", null, null);		
//	}
//	
//	@Test
//	public void testDecrypt() throws IdAuthenticationBusinessException {
//		when(cryptomanagerService.decrypt(Mockito.any()))
//				.thenReturn(new CryptomanagerResponseDto("abcd"));
//		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
//		request.setAad("VGhpcyBpcyBzYW1wbGUgYWFk");
//		request.setApplicationId("IDA");
//		request.setData("Tewertylknbvghjstdfghjkjhbvghbvgbbv");
//		request.setReferenceId("REFID");
//		request.setSalt("LA7YcvP9DdLIVI5CwFt1SQ");
//		request.setTimeStamp(LocalDateTime.now());
//	
//		CryptomanagerResponseDto decrypt = keyServiceManager.decrypt(request);
//		assertEquals("abcd", decrypt.getData());
//	}
//	
//	@Test(expected = IdAuthenticationBusinessException.class)
//	public void testDecryptNoUniqueAliasException() throws IdAuthenticationBusinessException {
//		when(cryptomanagerService.decrypt(Mockito.any())).thenThrow(new NoUniqueAliasException("", ""));
//		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
//		request.setAad("VGhpcyBpcyBzYW1wbGUgYWFk");
//		request.setApplicationId("IDA");
//		request.setData("Tewertylknbvghjstdfghjkjhbvghbvgbbv");
//		request.setReferenceId("REFID");
//		request.setSalt("LA7YcvP9DdLIVI5CwFt1SQ");
//		request.setTimeStamp(LocalDateTime.now());
//		keyServiceManager.decrypt(request);
//	}
//	
//	@Test(expected = IdAuthenticationBusinessException.class)
//	public void testDecryptException() throws IdAuthenticationBusinessException {
//		when(cryptomanagerService.decrypt(Mockito.any())).thenThrow(new BaseUncheckedException("", ""));
//		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
//		request.setAad("VGhpcyBpcyBzYW1wbGUgYWFk");
//		request.setApplicationId("IDA");
//		request.setData("Tewertylknbvghjstdfghjkjhbvghbvgbbv");
//		request.setReferenceId("REFID");
//		request.setSalt("LA7YcvP9DdLIVI5CwFt1SQ");
//		request.setTimeStamp(LocalDateTime.now());
//		keyServiceManager.decrypt(request);
//	}
//	
//	@Test
//	public void testverifySignature() {
//		String signature = "eyJ4NWMiOlsiTUlJRE5qQ0NBaDZnQXdJQkFnSUlBZWVYcGRmVDF6OHdEUVlKS29aSWh2Y05BUUVGQlFBd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dIaGNOTVRrd016STRNVE16T1RNeldoY05ORFl3T0RFeU1UTXpPVE16V2pCYk1RNHdEQVlEVlFRR0V3VnBibVJwWVRFWk1CY0dBMVVFQ2hNUVRXbHVaSFJ5WldVZ1RHbHRhWFJsWkRFZk1CMEdBMVVFQ3hNV1RXbHVaSFJ5WldVZ1NHa3RWR1ZqYUNCWGIzSnNaREVOTUFzR0ExVUVBeE1FYzJGdWVqQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUkwRWRsNUtRTzViUWhSUHZMZVRoWWhXWVJrSUVHRm9nYXEzWE4zeHI5MllrMFFBV1pMd0ZNS2F4UVVCS01HSmU1UE02VUtqVThINk1rcWxKWEV0VnRsNFBINkdXRTQ2M1J3UFpsRENHNHlIZXdONjRYUDIrTkliZ1QwTFc4UXZpR1ZtTmZ4aGFVOHNSeS9seVh5LzJ6bVpmT0k2WWRxTnNkTVZMeFFER3dIV3ZMWnJIUk5UeGVMejJOS0VEYlFhK0c3bEpWUTJDOFNqT2dxWlQyN3ZiNHNJRDNhQXIrMzFOdSt1OWNDSDhEZDk3Ui9MRXk5alhZLzMxZWxOQ2hVOFlSb1hHTTdVTk9adGpRZWlLVnRuNWhrQVdReDA2ZnI0R1lWa0ZUb3RZdXNjWW42YlVBakxJTDlRSkJ3RERQVFBiRFdtRzN2eXQwUFlSMll4eWlxaDF3VUNBd0VBQVRBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQU85MTE5OG9NdnNkWVIxMGw4aVlCenJQWjEwcHdqckptYndzaFBnRGdxMlhhMkVFK0h5T1Fta2FZRml2MGQwRnZpaDdlUVpRcHpJTnZNbjRLVmVIcWEvaHdOSGZhdmlqc3BIbk96blRCUGcrWm9ad3lsTWJxQ255dXdzbWdaeVlENzdzSFM4NUtHaFNvaFNjaitEMzZidEgrVVlaL0g5Y25tdHgzenhEVXpxTlFnTUNMYmJCZDZHcGpVZy84T25zY2tnaFJZK0lKSFZVblZaZElCMVNqVlFvdzQ1eCtkNEZJMXZxOWFlQ2ZKYTIxS3JnaHJIdUdHRUdydU5URWs1MHIyQnNHY2R4VUt3RFpnMWtNMVZZWVRoWEZUUTdZK29ob2prMXBLdkw2VUNuM1VQdUVkSWY1SjN0Rm5SNUdVd25mbjhBUjVtcm5zK1gzZ0dqTDhjOXlUdz09Il0sImFsZyI6IlJTMjU2In0.MDcxOEY3NDEwMTE4ODdERjFCNTk5NUI0MzkyODFCQzAwRDk1OUVENjIwQ0I3OTE5NEMzMjUxRTk4MUM2RjI1MA.hFzxGmtap9JGRLni4nDrUy_gjP8Yp5MOGaiVPDHcvKV6ZNXlWH2-8LgJw2ZMeZo6XPljJkVtxzGOnpZPILZLKYl1dITsVkhqLVm2so-_1LYyfwexoQgsfbDZDlf5UeNipdBLlr4fkd4htQzsZV5p9vMwmhOoVq_TEBXIY7jZ6QWSJx1HioNajaOzc3lwSM8T0zxYxowQD10tGXj5Wdrk21ruf1XDjL5SNzMRVhOZpZR3SpFL19y5orW0CFCiRld7FpfE9NfSHOR61sGLbQL8AMVUgZQuVkkD_W5j_aAXJGZ8wirWcKFnoI7bBZrrAq-vOjH_QUl0jyO7fIl5QPEDcQ";
//		Mockito.when(cryptoCore.verifySignature(signature)).thenReturn(true);
//		keyServiceManager.verifySignature(signature);		
//	}
//	
//	@Test
//	public void testverifySignatureFalse() {
//		String signature = "eyJ4NWMiOlsiTUlJRE5qQ0NBaDZnQXdJQkFnSUlBZWVYcGRmVDF6OHdEUVlKS29aSWh2Y05BUUVGQlFBd1d6RU9NQXdHQTFVRUJoTUZhVzVrYVdFeEdUQVhCZ05WQkFvVEVFMXBibVIwY21WbElFeHBiV2wwWldReEh6QWRCZ05WQkFzVEZrMXBibVIwY21WbElFaHBMVlJsWTJnZ1YyOXliR1F4RFRBTEJnTlZCQU1UQkhOaGJub3dIaGNOTVRrd016STRNVE16T1RNeldoY05ORFl3T0RFeU1UTXpPVE16V2pCYk1RNHdEQVlEVlFRR0V3VnBibVJwWVRFWk1CY0dBMVVFQ2hNUVRXbHVaSFJ5WldVZ1RHbHRhWFJsWkRFZk1CMEdBMVVFQ3hNV1RXbHVaSFJ5WldVZ1NHa3RWR1ZqYUNCWGIzSnNaREVOTUFzR0ExVUVBeE1FYzJGdWVqQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUkwRWRsNUtRTzViUWhSUHZMZVRoWWhXWVJrSUVHRm9nYXEzWE4zeHI5MllrMFFBV1pMd0ZNS2F4UVVCS01HSmU1UE02VUtqVThINk1rcWxKWEV0VnRsNFBINkdXRTQ2M1J3UFpsRENHNHlIZXdONjRYUDIrTkliZ1QwTFc4UXZpR1ZtTmZ4aGFVOHNSeS9seVh5LzJ6bVpmT0k2WWRxTnNkTVZMeFFER3dIV3ZMWnJIUk5UeGVMejJOS0VEYlFhK0c3bEpWUTJDOFNqT2dxWlQyN3ZiNHNJRDNhQXIrMzFOdSt1OWNDSDhEZDk3Ui9MRXk5alhZLzMxZWxOQ2hVOFlSb1hHTTdVTk9adGpRZWlLVnRuNWhrQVdReDA2ZnI0R1lWa0ZUb3RZdXNjWW42YlVBakxJTDlRSkJ3RERQVFBiRFdtRzN2eXQwUFlSMll4eWlxaDF3VUNBd0VBQVRBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQU85MTE5OG9NdnNkWVIxMGw4aVlCenJQWjEwcHdqckptYndzaFBnRGdxMlhhMkVFK0h5T1Fta2FZRml2MGQwRnZpaDdlUVpRcHpJTnZNbjRLVmVIcWEvaHdOSGZhdmlqc3BIbk96blRCUGcrWm9ad3lsTWJxQ255dXdzbWdaeVlENzdzSFM4NUtHaFNvaFNjaitEMzZidEgrVVlaL0g5Y25tdHgzenhEVXpxTlFnTUNMYmJCZDZHcGpVZy84T25zY2tnaFJZK0lKSFZVblZaZElCMVNqVlFvdzQ1eCtkNEZJMXZxOWFlQ2ZKYTIxS3JnaHJIdUdHRUdydU5URWs1MHIyQnNHY2R4VUt3RFpnMWtNMVZZWVRoWEZUUTdZK29ob2prMXBLdkw2VUNuM1VQdUVkSWY1SjN0Rm5SNUdVd25mbjhBUjVtcm5zK1gzZ0dqTDhjOXlUdz09Il0sImFsZyI6IlJTMjU2In0.MDcxOEY3NDEwMTE4ODdERjFCNTk5NUI0MzkyODFCQzAwRDk1OUVENjIwQ0I3OTE5NEMzMjUxRTk4MUM2RjI1MA.hFzxGmtap9JGRLni4nDrUy_gjP8Yp5MOGaiVPDHcvKV6ZNXlWH2-8LgJw2ZMeZo6XPljJkVtxzGOnpZPILZLKYl1dITsVkhqLVm2so-_1LYyfwexoQgsfbDZDlf5UeNipdBLlr4fkd4htQzsZV5p9vMwmhOoVq_TEBXIY7jZ6QWSJx1HioNajaOzc3lwSM8T0zxYxowQD10tGXj5Wdrk21ruf1XDjL5SNzMRVhOZpZR3SpFL19y5orW0CFCiRld7FpfE9NfSHOR61sGLbQL8AMVUgZQuVkkD_W5j_aAXJGZ8wirWcKFnoI7bBZrrAq-vOjH_QUl0jyO7fIl5QPEDcQ";
//		Mockito.when(cryptoCore.verifySignature(signature)).thenReturn(false);
//		keyServiceManager.verifySignature(signature);		
//	}
//	
//	@Test
//	public void validateSignature() throws IdAuthenticationBusinessException {
//		String data="test";
//		String signedData="rhjhgfghuhgvfgh";
//		TimestampRequestDto dto= new TimestampRequestDto(signedData,data,LocalDateTime.now(ZoneId.of("UTC")));
//		ValidatorResponseDto response = new ValidatorResponseDto();
//		response.setStatus("VALID");
//		response.setMessage("test");
//		Mockito.when(signatureService.validate(dto)).thenReturn(response);		
//		ValidatorResponseDto res = keyServiceManager.validateSinature(dto);	
//		assertEquals("test", res.getMessage());
//	}
//	
//	@Test(expected = IdAuthenticationBusinessException.class)
//	public void validateSignatureWithException() throws IdAuthenticationBusinessException {
//		String data="test";
//		String signedData="rhjhgfghuhgvfgh";
//		TimestampRequestDto dto= new TimestampRequestDto(signedData,data,LocalDateTime.now(ZoneId.of("UTC")));
//		ValidatorResponseDto response = new ValidatorResponseDto();
//		response.setStatus("VALID");
//		response.setMessage("test");
//		Mockito.when(signatureService.validate(dto)).thenThrow(new BaseUncheckedException("",""));		
//		keyServiceManager.validateSinature(dto);
//		
//	}
//}
