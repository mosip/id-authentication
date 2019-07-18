package io.mosip.test;

import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import io.mosip.preregistration.util.PreRegistartionUtil;
import io.restassured.response.Response;

public class GetPreRegistartion {
	JSONObject PreIDs = new JSONObject();
	static PreRegistartionUtil util = new PreRegistartionUtil();

	public String adultPrid() {
		String authToken = util.getToken();
		return authToken;

	}

	public void storePreRegData(String PrID) {
		util.storePreRegistrationData(PrID);
	}

	public JSONObject getPreIdChild(String childRequest) {

		PreIDs.put("childPreidWithDocs", util.getPrIdOfChild(childRequest));

		PreIDs.put("PrIdOfChildWithoutDocs", util.getPrIdOfChildWithoutDocs(childRequest));

		return PreIDs;
	}

	public JSONObject getPreIdAdult(String adultRequest) {
		PreIDs.put("PrIdOfAdultWithDocs", util.getPrIdOfAdult(adultRequest));
		PreIDs.put("PrIdOfAdultWithoutDocs", util.getPrIdOfAdultWithoutDocs(adultRequest));
		return PreIDs;
	}
	public static void main(String[] args) {
		GetPreRegistartion pre=new GetPreRegistartion();
		System.out.println(pre.getPreIdAdult("adultRequest"));
		System.out.println(pre.getPreIdChild("childRequest"));
	}
}
