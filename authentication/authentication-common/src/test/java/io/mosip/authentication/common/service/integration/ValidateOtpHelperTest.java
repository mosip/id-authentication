package io.mosip.authentication.common.service.integration;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ValidateOtpHelperTest {

	@Mock
	private IdAuthSecurityManager securityManager;
	
	@Mock
	private OtpTxnRepository otpRepo;
	
	@Mock
	private RequireOtpNotFrozenHelper requireOtpNotFrozen;
	
	@InjectMocks
	private ValidateOtpHelper validateOtpHelper;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(validateOtpHelper, "numberOfValidationAttemptsAllowed", 5);
		ReflectionTestUtils.setField(validateOtpHelper, "otpFrozenTimeMinutes", 30);
	}
	
	@Test
	public void testCreateOTPFrozenException() {
		IdAuthenticationBusinessException exception = validateOtpHelper.createOTPFrozenException();
		
		assertNotNull("Exception should not be null", exception);
		assertEquals("Should have correct error code", 
			IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), exception.getErrorCode());
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testValidateOtpWithNoOtpEntity() throws IdAuthenticationBusinessException {
		String pinValue = "123456";
		String otpKey = "testKey";
		String individualId = "1234567890";
		String refIdHash = "hashedRefId";
		
		when(securityManager.hash(individualId)).thenReturn(refIdHash);
		when(otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(
			anyString(), anyList())).thenReturn(Optional.empty());
		
		validateOtpHelper.validateOtp(pinValue, otpKey, individualId);
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
		
		assertTrue("Should return true for valid OTP", result);
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
		assertNotEquals("Hashes should be different for invalid OTP test", storedOtpHash, computedHash);
		doNothing().when(requireOtpNotFrozen).requireOtpNotFrozen(any(OtpTransaction.class), anyBoolean());
		when(otpRepo.save(any(OtpTransaction.class))).thenReturn(otpEntity);
		
		boolean result = validateOtpHelper.validateOtp(pinValue, otpKey, individualId);
		
		assertFalse("Should return false for invalid OTP", result);
		verify(otpRepo, atLeastOnce()).save(any(OtpTransaction.class));
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testValidateOtpWithUnfrozenStatus() throws IdAuthenticationBusinessException {
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
	}
}
