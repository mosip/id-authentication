package io.mosip.kernel.util;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author M9010714
 *
 */
public class KernelAuthentication extends BaseTestCase{
	// Declaration of all variables
	String folder="kernel";
	String cookie;
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	
	private String individual_appid=props.get("individual_appid");
	private String individual_password=props.get("individual_password");
	private String individual_userName=props.get("individual_userName");
	
	private String regProc_appid=props.get("regProc_appid");
	private String regProc_password=props.get("regProc_password");
	private String regProc_userName=props.get("regProc_userName");
	
	private String registrationAdmin_appid=props.get("registrationAdmin_appid");;
	private String registrationAdmin_password=props.get("registrationAdmin_password");
	private String registrationAdmin_userName=props.get("registrationAdmin_userName");
	
	private String ida_appid=props.get("ida_appid");
	private String ida_password=props.get("ida_password");
	private String ida_userName=props.get("ida_userName");
	
	private String registrationOfficer_appid=props.get("registrationOfficer_appid");
	private String registrationOfficer_password=props.get("registrationOfficer_password");
	private String registrationOfficer_userName=props.get("registrationOfficer_userName");
	
	private String registrationSupervisor_appid=props.get("registrationSupervisor_appid");
	private String registrationSupervisor_password=props.get("registrationSupervisor_password");
	private String registrationSupervisor_userName=props.get("registrationSupervisor_userName");
	
	private String zonalAdmin_appid=props.get("zonalAdmin_appid");
	private String zonalAdmin_password=props.get("zonalAdmin_password");
	private String zonalAdmin_userName=props.get("zonalAdmin_userName");
	
	private String zonalApprover_appid=props.get("zonalApprover_appid");
	private String zonalApprover_password=props.get("zonalApprover_password");
	private String zonalApprover_userName=props.get("zonalApprover_userName");
	
	private String authenticationEndpoint = props.get("authentication");
	private String sendOtp = props.get("sendOtp");
	private String useridOTP = props.get("useridOTP");
	private String testsuite="/Authorization";	
	private ApplicationLibrary appl=new ApplicationLibrary();

	@SuppressWarnings("unchecked")
	public String getAuthForIndividual() {	
		// getting request and expected response jsondata from json files.
        JSONObject actualRequest_generation = getRequestJson(testsuite+"/OtpGeneration");
        //Getting the userId from request
        String key=((JSONObject)actualRequest_generation.get("request")).get("userId").toString();
        // getting request and expected response jsondata from json files.
        JSONObject actualRequest_validation = getRequestJson(testsuite+"/OtpGeneration");
        //sending for otp
        appl.postWithJson(sendOtp, actualRequest_generation);
        //Getting the status of the UIN 
        String query="SELECT o.otp FROM kernel.otp_transaction o where id='"+key+"'";
        List<String> status_list = new KernelDataBaseAccess().getDbData( query,"kernel");
        String otp=status_list.get(0);
        ((JSONObject)actualRequest_validation.get("request")).put("otp", otp);
        Response otpValidate=appl.postWithJson(useridOTP, actualRequest_validation);
        cookie=otpValidate.getCookie("Authorization");
		return cookie;
	}

	
	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationProcessor() {
		JSONObject actualrequest = getRequestJson(testsuite);
		
		JSONObject request=new JSONObject();
		request.put("appId", regProc_appid);
		request.put("password", regProc_password);
		request.put("userName", regProc_userName);
		actualrequest.put("request", request); 
		
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForIDA() {
		JSONObject actualrequest = getRequestJson(testsuite);
		
		JSONObject request=new JSONObject();
		request.put("appId", ida_appid);
		request.put("password", ida_password);
		request.put("userName", ida_userName);
		actualrequest.put("request", request);
		
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationAdmin() {
		JSONObject actualrequest = getRequestJson(testsuite);

		JSONObject request=new JSONObject();
		request.put("appId", registrationAdmin_appid);
		request.put("password", registrationAdmin_password);
		request.put("userName", registrationAdmin_userName);
		actualrequest.put("request", request);
		
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationOfficer() {
		JSONObject actualrequest = getRequestJson(testsuite);
		
		JSONObject request=new JSONObject();
		request.put("appId", registrationOfficer_appid);
		request.put("password", registrationOfficer_password);
		request.put("userName", registrationOfficer_userName);
		actualrequest.put("request", request);
		
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForRegistrationSupervisor() {
		JSONObject actualrequest = getRequestJson(testsuite);
		
		JSONObject request=new JSONObject();
		request.put("appId", registrationSupervisor_appid);
		request.put("password", registrationSupervisor_password);
		request.put("userName", registrationSupervisor_userName);
		actualrequest.put("request", request);
	
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForZonalAdmin() {
		JSONObject actualrequest = getRequestJson(testsuite);
		
		JSONObject request=new JSONObject();
		request.put("appId", zonalAdmin_appid);
		request.put("password", zonalAdmin_password);
		request.put("userName", zonalAdmin_userName);
		actualrequest.put("request", request);
	
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	
	@SuppressWarnings("unchecked")
	public String getAuthForZonalApprover() {
		JSONObject actualrequest = getRequestJson(testsuite);
		
		JSONObject request=new JSONObject();
		request.put("appId", zonalApprover_appid);
		request.put("password", zonalApprover_password);
		request.put("userName", zonalApprover_userName);
		actualrequest.put("request", request);
	
		Response reponse=appl.postWithJson(authenticationEndpoint, actualrequest);
		cookie=reponse.getCookie("Authorization");
		return cookie;
	}
	//Reading the request file from folder
	public JSONObject getRequestJson(String testSuite){
		JSONObject Request=null;
		String configPath = folder + "/" + testSuite+"/request.json";
		return new CommonLibrary().readJsonData(configPath);
		
	}
}
