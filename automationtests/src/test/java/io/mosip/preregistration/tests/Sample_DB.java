package io.mosip.preregistration.tests;

import java.util.List;

import org.testng.annotations.Test;

import io.mosip.dbaccess.KernelMasterDataR;
import io.mosip.dbaccess.prereg_dbread;
import io.mosip.dbentity.AccessToken;
import io.mosip.dbentity.OtpEntity;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;

public class Sample_DB extends BaseTestCase {
	

	@Test
	public static void DB() {
		PreRegistrationLibrary lib=new PreRegistrationLibrary();
		lib.generateOTP();
		//lib.CreatePreReg(createRequest)
		/*String otpQueryStr = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='123'";
		List<Object> otpData = prereg_dbread.fetchOTPFromDB(otpQueryStr, OtpEntity.class);
		String otp = otpData.get(0).toString();
		System.out.println(otp);
		String tokenQueryStr = "SELECT E.auth_token FROM iam.oauth_access_token E WHERE user_id='6371787698'";
		List<Object> token = prereg_dbread.fetchFromDB(tokenQueryStr, AccessToken.class);
		String auth_token = token.get(0).toString();
		System.out.println(auth_token);*/

	}

}
