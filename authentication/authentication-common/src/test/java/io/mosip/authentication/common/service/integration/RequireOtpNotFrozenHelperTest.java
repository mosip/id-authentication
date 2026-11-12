package io.mosip.authentication.common.service.integration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.core.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequireOtpNotFrozenHelperTest {

    @InjectMocks
    private RequireOtpNotFrozenHelper helper;

    @Mock
    private OtpTxnRepository otpRepo;

    private OtpTransaction otpEntity;

    @BeforeEach
    public void setup() {
        // Set @Value injected fields
        ReflectionTestUtils.setField(helper, "otpFrozenTimeMinutes", 30);
        ReflectionTestUtils.setField(helper, "numberOfValidationAttemptsAllowed", 5);

        otpEntity = new OtpTransaction();
        otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
    }

    /**
     * Test createOTPFrozenException()
     */
    @Test
    public void testCreateOTPFrozenException() {
        IdAuthenticationBusinessException ex = helper.createOTPFrozenException();

        assertEquals(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), ex.getErrorCode());
        assertTrue(ex.getErrorText().contains("5"));
        assertTrue(ex.getErrorText().contains("30"));
    }

    /**
     * OTP is frozen and frozen time is NOT over → must throw exception
     */
    @Test
    public void testRequireOtpNotFrozenFrozenAndTimeNotOverShouldThrow() throws Exception {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
        otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime()); // updated just now

        helper.requireOtpNotFrozen(otpEntity, false);
    		});
	}

    /**
     * OTP frozen but frozen time IS over → should unfreeze and save
     */
    @Test
    public void testRequireOtpNotFrozenFrozenAndTimeOverShouldUnfreezeAndSave() throws Exception {
        // Set updDTimes to 31 minutes ago → frozen time is over
        LocalDateTime oldTime = DateUtils.getUTCCurrentDateTime().minusMinutes(31);
        otpEntity.setUpdDTimes(oldTime);

        helper.requireOtpNotFrozen(otpEntity, true);

        assertEquals(IdAuthCommonConstants.UNFROZEN, otpEntity.getStatusCode());
        verify(otpRepo, times(1)).save(otpEntity);
    }

    /**
     * OTP is frozen and time over but saveEntity = false → should NOT save
     */
    @Test
    public void testRequireOtpNotFrozenTimeOverButNoSave() throws Exception {
        LocalDateTime oldTime = DateUtils.getUTCCurrentDateTime().minusMinutes(40);
        otpEntity.setUpdDTimes(oldTime);

        helper.requireOtpNotFrozen(otpEntity, false);

        assertEquals(IdAuthCommonConstants.UNFROZEN, otpEntity.getStatusCode());
        verify(otpRepo, never()).save(any());
    }

    /**
     * OTP is NOT frozen → nothing should happen
     */
    @Test
    public void testRequireOtpNotFrozenNotFrozenNoAction() throws Exception {
        otpEntity.setStatusCode(IdAuthCommonConstants.UNFROZEN);

        helper.requireOtpNotFrozen(otpEntity, true);

        verify(otpRepo, never()).save(any());
    }
}
