package io.mosip.preregistration.util;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.collections.Lists;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * Util Class is to perform Booking service smoke and regression test operations
 * 
 * @author Lavanya R
 * @since 1.0.0
 */
public class BookingUtil {

	/**
	 * Declaration of all variables
	 **/

	String testSuite = "";
	JSONObject request;
	Response response;
	//ApplicationLibrary applnLib = new ApplicationLibrary();
	ApplicationLibrary appLib = new ApplicationLibrary();
	PreRegistrationUtil preregUtil = new PreRegistrationUtil();
	Logger logger = Logger.getLogger(BaseTestCase.class);
	String endpoint;

	/**
	 * The method perform cancel the appointment, Which will retrieve the
	 * appointment details for the specified pre-registration id, if appointment
	 * data exists update the availability for the slot by increasing the value
	 * and delete the record from the table and update the demographic record
	 * status "Pending_Appointment" Smoke and Regression test operation
	 *
	 * @param preId
	 * @return Response
	 */
	public Response CancelBookingAppointment(String preID,String cookie) {

		String preReg_CancelAppURI = preregUtil.fetchPreregProp().get("preReg_CancelAppointmentURI") + preID;
		response = appLib.putWithoutData(preReg_CancelAppURI, cookie);
		return response;
	}

	/**
	 * The method perform retrieve Pre-Registration appointment details by
	 * pre-Registration id, Smoke and Regression test operation
	 *
	 * @param preId
	 * @return Response
	 */
	public Response FetchAppointmentDetails(String preID,String cookie) {

		String preRegFetchAppDet = preregUtil.fetchPreregProp().get("preReg_FecthAppointmentDetailsURI") + preID;
		response =appLib.getWithoutParams(preRegFetchAppDet, cookie);
		return response;
	}

	/**
	 * The method returns the registration center dynamically
	 *
	 * @return String
	 */
	public String randomRegistrationCenterId() {
		Random rand = new Random();
		List<String> givenList = Lists.newArrayList("10002", "10013", "10014", "10010", "10015", "10006", "10004",
				"10011", "10008", "10001", "10012", "10005", "10003", "10007", "10009");
		String s = null;
		int numberOfElements = givenList.size();
		for (int i = 0; i < numberOfElements; i++) {
			int randomIndex = rand.nextInt(givenList.size());
			s = givenList.remove(randomIndex);
		}
		return s;

	}

	
	/**
	 * The method perform retrieve all appointment slots 
	 * available for booking based on the specified registration center id, 
	 * Smoke and Regression test operation
	 *
	 * @param regCenId
	 * @param cookie
	 * @return Response
	 */
	public Response FetchCentre(String regCenId,String cookie) {

		String regCenterId = null;
		if (regCenId.equals("null")) {
			regCenterId = randomRegistrationCenterId();
		} else {

			regCenterId =regCenId;
		}
		String preRegFetchCenterIDURI = preregUtil.fetchPreregProp().get("preReg_FetchCenterIDURI") + regCenterId;
		//response = applnLib.getRequestWithoutParm(preRegFetchCenterIDURI);
		response = appLib.getWithoutParams(preRegFetchCenterIDURI, cookie);
		return response;
	}

	
	
	/**
	 * The method perform etrieve all pre-registration ids 
	 * available for specified registration center and date range 
	 * Smoke and Regression test operation
	 *
	 * @param regCenId
	 * @param cookie
	 * @return Response
	 */
	
	public Response retriveAllPreRegIdByRegCenterId(String URI,HashMap<String, String> parm,String cookie) 
	{

		response =appLib.getWithQueryParam(URI, parm, cookie);
		return response;
	}
	
	
	
}
