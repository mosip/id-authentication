package io.mosip.e2e.util;

import org.json.simple.JSONObject;

import io.mosip.test.GetPreRegistartion;

public class GeneratePreIds {
	public JSONObject getPreids() {
		JSONObject preIds;
		GetPreRegistartion getPreRegistration=new GetPreRegistartion();
		try {
			preIds=getPreRegistration.getPreIdChild("childRequest");
			preIds=getPreRegistration.getPreIdChild("adultRequest");
			return preIds;
		}catch(NullPointerException | IndexOutOfBoundsException e) {
			return null;
		}
	}
}
