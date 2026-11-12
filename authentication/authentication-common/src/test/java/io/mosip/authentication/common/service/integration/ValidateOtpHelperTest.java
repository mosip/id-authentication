package io.mosip.authentication.common.service.integration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidateOtpHelperTest {

	@Mock
	private IdAuthSecurityManager securityManager;
	
	@Mock
	private OtpTxnRepository otpRepo;
	
	@Mock
	private RequireOtpNotFrozenHelper requireOtpNotFrozen;
	
	@InjectMocks
	private ValidateOtpHelper validateOtpHelper;
	
	@BeforeEach
	public void before() {
		ReflectionTestUtils.setField(validateOtpHelper, "numberOfValidationAttemptsAllowed", 5);
		ReflectionTestUtils.setField(validateOtpHelper, "otpFrozenTimeMinutes", 30);
	}
	
	@Test
	public void testCreateOTPFrozenException() {
		IdAuthenticationBusinessException exception = validateOtpHelper.createOTPFrozenException();
		
		assertNotNull(exception, "Exception should not be null");
		assertEquals( 
			IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), exception.getErrorCode(), "Should have correct error code");
	}
	
	@Test
	public void testValidateOtpWithNoOtpEntity() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		String pinValue = "123456";
		String otpKey = "testKey";
		String individualId = "1234567890";
		String refIdHash = "hashedRefId";
		
		when(securityManager.hash(individualId)).thenReturn(refIdHash);
		when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(
			anyString(), anyList())).thenReturn(Optional.empty());
		
		validateOtpHelper.validateOtp(pinValue, otpKey, individualId);
			});
	}
	
	@Test
	public void testValidateOtpWithValidOtp() throws IdAuthenticationBusinessException {
		String pinValue = "123456";
		String otpKey = "testKey";
		String individualId = "1234567890";
		String refIdHash = "hashedRefId";
		
		// Compute actual hash using the same method as the helper
		String otpHash = IdAuthSecurityManager.digestAsPlainText(
			(otpKey + io.mosip.authentication.common.service.util.EnvUtil.getKeySplitter() + pinValue).getBytes());
		
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setRefId(refIdHash);
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setOtpHash(otpHash);
		otpEntity.setExpiryDtimes(LocalDateTime.now().plusMinutes(10));
		otpEntity.setValidationRetryCount(0);
		
		when(securityManager.hash(individualId)).thenReturn(refIdHash);
		when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(
			anyString(), anyList())).thenReturn(Optional.of(otpEntity));
		
		doNothing().when(requireOtpNotFrozen).requireOtpNotFrozen(any(OtpTransaction.class), anyBoolean());
		when(otpRepo.save(any(OtpTransaction.class))).thenReturn(otpEntity);
		
		boolean result = validateOtpHelper.validateOtp(pinValue, otpKey, individualId);
		
		assertTrue(result, "Should return true for valid OTP");
		verify(otpRepo, atLeastOnce()).save(any(OtpTransaction.class));
	}
	
	@Test
	public void testValidateOtpWithInvalidOtp() throws IdAuthenticationBusinessException {
		String pinValue = "123456";
		String otpKey = "testKey";
		String individualId = "1234567890";
		String refIdHash = "hashedRefId";
		String storedOtpHash = "storedHash";
		String providedOtpHash = "differentHash";
		
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setRefId(refIdHash);
		otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
		otpEntity.setOtpHash(storedOtpHash);
		otpEntity.setExpiryDtimes(LocalDateTime.now().plusMinutes(10));
		otpEntity.setValidationRetryCount(0);
		
		when(securityManager.hash(individualId)).thenReturn(refIdHash);
		when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(
			anyString(), anyList())).thenReturn(Optional.of(otpEntity));
		
		// Use a different hash to simulate invalid OTP
		String computedHash = IdAuthSecurityManager.digestAsPlainText(
			(otpKey + io.mosip.authentication.common.service.util.EnvUtil.getKeySplitter() + pinValue).getBytes());
		// Ensure stored hash is different
		assertNotEquals(storedOtpHash, computedHash, "Hashes should be different for invalid OTP test");
		doNothing().when(requireOtpNotFrozen).requireOtpNotFrozen(any(OtpTransaction.class), anyBoolean());
		when(otpRepo.save(any(OtpTransaction.class))).thenReturn(otpEntity);
		
		boolean result = validateOtpHelper.validateOtp(pinValue, otpKey, individualId);
		
		assertFalse(result, "Should return false for invalid OTP");
		verify(otpRepo, atLeastOnce()).save(any(OtpTransaction.class));
	}
	
	@Test
	public void testValidateOtpWithUnfrozenStatus() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		String pinValue = "123456";
		String otpKey = "testKey";
		String individualId = "1234567890";
		String refIdHash = "hashedRefId";
		
		OtpTransaction otpEntity = new OtpTransaction();
		otpEntity.setRefId(refIdHash);
		otpEntity.setStatusCode(IdAuthCommonConstants.UNFROZEN);
		
		when(securityManager.hash(individualId)).thenReturn(refIdHash);
		when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(
			anyString(), anyList())).thenReturn(Optional.of(otpEntity));
		doNothing().when(requireOtpNotFrozen).requireOtpNotFrozen(any(OtpTransaction.class), anyBoolean());
		
		validateOtpHelper.validateOtp(pinValue, otpKey, individualId);
			});
	}
}
