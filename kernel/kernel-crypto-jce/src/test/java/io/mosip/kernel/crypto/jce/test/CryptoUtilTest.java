package io.mosip.kernel.crypto.jce.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.crypto.jce.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.jce.util.CryptoUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CryptoUtilTest {

	@Test(expected = NullDataException.class)
	public void testverifyDataNullData() {
		CryptoUtils.verifyData(null);
	}

	@Test(expected = InvalidDataException.class)
	public void testverifyDataEmptyData() {
		CryptoUtils.verifyData(new byte[0]);
	}

	@Test(expected = NullDataException.class)
	public void testverifyDataNullDataWithErrorCode() {
		CryptoUtils.verifyData(null, SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorCode(),
				SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorMessage());
	}

	@Test(expected = InvalidDataException.class)
	public void testverifyDataEmptyDataWithErrorCode() {
		CryptoUtils.verifyData(new byte[0],SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorCode(),SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorMessage());
	}

}