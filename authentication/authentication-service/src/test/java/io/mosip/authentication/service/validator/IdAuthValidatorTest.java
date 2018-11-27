package io.mosip.authentication.service.validator;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.service.impl.otpgen.validator.OTPRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

/**
 * The Class IdAuthValidatorTest.
 * 
 * @author Manoj SP
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class IdAuthValidatorTest {

    /** The uin validator. */
    @Mock
    UinValidatorImpl uinValidator;

    /** The vid validator. */
    @Mock
    VidValidatorImpl vidValidator;

    /** The validator. */
    IdAuthValidator validator = new OTPRequestValidator();

    /** The errors. */
    Errors errors;

    /** The request. */
    OtpRequestDTO request = new OtpRequestDTO();

    /**
     * Setup.
     */
    @Before
    public void setup() {
	errors = new BeanPropertyBindingResult(request, "IdAuthValidator");
	ReflectionTestUtils.setField(validator, "uinValidator", uinValidator);
	ReflectionTestUtils.setField(validator, "vidValidator", vidValidator);
    }
    
    /**
     * Test null id.
     */
    @Test
    public void testNullId() {
	validator.validateId(null, errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	});
    }
    
    /**
     * Test null idv id.
     */
    @Test
    public void testNullIdvId() {
	validator.validateIdvId(null, "D", errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("idvId", ((FieldError) error).getField());
	});
    }
    
    /**
     * Test null id type.
     */
    @Test
    public void testNullIdType() {
	validator.validateIdvId("1234", null, errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("idvIdType", ((FieldError) error).getField());
	});
    }
    
    /**
     * Test incorrect id type.
     */
    @Test
    public void testIncorrectIdType() {
	validator.validateIdvId("1234", "e", errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("idvIdType", ((FieldError) error).getField());
	});
    }

    /**
     * Test invalid UIN.
     */
    @Test
    public void testInvalidUIN() {
	Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("", ""));
	validator.validateIdvId("1234", "D", errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.INVALID_UIN.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("idvId", ((FieldError) error).getField());
	});
    }
    
    /**
     * Test invalid VID.
     */
    @Test
    public void testInvalidVID() {
	Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("", ""));
	validator.validateIdvId("1234", "V", errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.INVALID_VID.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.INVALID_VID.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("idvId", ((FieldError) error).getField());
	});
    }
    
    /**
     * Test null ver.
     */
    @Test
    public void testNullVer() {
	validator.validateVer(null, errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("ver", ((FieldError) error).getField());
	});
    }
    
    /**
     * Test invalid ver.
     */
    @Test
    public void testInvalidVer() {
	validator.validateVer("1234", errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("ver", ((FieldError) error).getField());
	});
    }
    
    /**
     * Test null mua code.
     */
    @Test
    public void testNullMuaCode() {
	validator.validateMuaCode(null, errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("muaCode", ((FieldError) error).getField());
	});
    }
    
    /**
     * Test invalid mua code.
     */
    @Test
    public void testInvalidMuaCode() {
	validator.validateMuaCode("1234", errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("muaCode", ((FieldError) error).getField());
	});
    }
    
    /**
     * Test null txn id.
     */
    @Test
    public void testNullTxnId() {
	validator.validateTxnId(null, errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("txnID", ((FieldError) error).getField());
	});
    }
    
    /**
     * Test invalid txn id.
     */
    @Test
    public void testInvalidTxnId() {
	validator.validateTxnId("1234", errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("txnID", ((FieldError) error).getField());
	});
    }
    
    /**
     * Test null req time.
     */
    @Test
    public void testNullReqTime() {
	validator.validateReqTime(null, errors);
	errors.getAllErrors().forEach(error -> {
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
	    assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
		    error.getDefaultMessage());
	    assertEquals("reqTime", ((FieldError) error).getField());
	});
    }

}
