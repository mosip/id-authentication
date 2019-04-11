package io.mosip.preregistration.tests;

import java.util.List;
import java.util.Random;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import io.mosip.dbaccess.KernelMasterDataR;
import io.mosip.dbaccess.prereg_dbread;
import io.mosip.dbentity.AccessToken;
import io.mosip.dbentity.OtpEntity;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class Sample_DB extends BaseTestCase {
	public static long generateID() { 
	    Random rnd = new Random();
	    char [] digits = new char[11];
	    digits[0] = (char) (rnd.nextInt(9) + '1');
	    for(int i=1; i<digits.length; i++) {
	        digits[i] = (char) (rnd.nextInt(10) + '0');
	    }
	    return Long.parseLong(new String(digits));
	}
	

	

}
