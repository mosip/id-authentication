package io.mosip.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.restassured.response.Response;


public class CommonLibrary {

	private static Logger logger = Logger.getLogger(CommonLibrary.class);

	public static void configFileWriter(String folderPath,String requestKeyFile,String generationType,String baseFileName)
				throws Exception {
		String splitRegex = Pattern.quote(System.getProperty("file.separator"));
		String string[]=new String[2];
		string=folderPath.split(splitRegex);
		String api=string[0];
		String testSuite=string[1];

		String requestFilePath= "src/test/resources/"+folderPath+"/"+requestKeyFile;
		String configFilePath="src/test/resources/"+folderPath+"/"+"FieldConfig.properties";

		JSONObject requestKeys= (JSONObject) new JSONParser().parse(new FileReader(requestFilePath));
		Properties properties = new Properties();
		Properties cloneProperties=new Properties();
		properties.load(new FileReader(new File(configFilePath)));
		cloneProperties.load(new FileReader(new File(configFilePath)));
		Set<String> keys=properties.stringPropertyNames();
		
	try { 
			for(Object key: requestKeys.keySet()) { 

				if(properties.getProperty(key.toString())!=null) {
					properties.setProperty(key.toString(), "invalid");
					properties.setProperty("filename", "invalid_"+key.toString());
					File file = new File(configFilePath);
					FileOutputStream fileOut = new FileOutputStream(file);
			 		properties.store(fileOut,"FieldConfig.properties");
			 		/*try {
						new Main().TestRequestReponseGenerator(api, testSuite,generationType);
						}catch(org.json.JSONException exp) {
							exp.printStackTrace();
						}*/
					properties.remove(key.toString());
					properties.setProperty(key.toString(), "valid");
					properties.remove("filename");
					properties.setProperty("filename", baseFileName);
					properties.store(fileOut, "FieldConfig.properties");
					fileOut.close();
				}
				
	}
			
	}catch (Exception e) {
		// TODO: handle exception
	}
	cloneProperties.remove("prereg_id_custom");
	cloneProperties.setProperty("prereg_id_custom", "");
	properties.clear();
	File file = new File(configFilePath);
	FileOutputStream fileOut = new FileOutputStream(file);
	properties.store(fileOut, null);
	cloneProperties.store(fileOut, null);
	}
	public static void scenarioFileCreator(String fileName,String module,String testType,String ouputFile) throws IOException, ParseException {
		String input = "";
//		String cpyModule="";
//	    for(int i=0;i<module.length();i++){
//	    	if(module.charAt(i)=='\\')
//	    		cpyModule+='/';
//	    	else
//	    		cpyModule+=module.charAt(i);
//	    }
//	    module=cpyModule;
		List<String> scenario = new ArrayList<String>();
		String filepath= "src/test/resources/" + module+"/"+fileName;

		String configPaths = "src/test/resources/" +module;

		File folder = new File(configPaths);
		System.out.println("Config Path is : "+configPaths);
		System.out.println("Folder exists  : "+ folder.exists());
		File[] listOfFolders = folder.listFiles();
		Map<String,String> jiraID= new HashMap<String,String>();
		int id=1000;
		for(int k=0;k<listOfFolders.length;k++) {
			jiraID.put(listOfFolders[k].getName(), "MOS-"+id);
			id++; 
		}
		JSONObject requestKeys= (JSONObject) new JSONParser().parse(new FileReader(filepath));
		if(testType.equals("smoke")) {
		input += "{";
		input += "\"testType\":" + "\"smoke\",";
		for(int k=0;k<listOfFolders.length;k++) {
		if(listOfFolders[k].getName().toLowerCase().contains("smoke")) {
			input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
			input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
			for(Object obj: requestKeys.keySet()) {
				input += '"' + obj.toString() + '"' + ":" + "\"valid\",";
			}
			input += "\"status\":" + "\"\"";
			input += "}";
			scenario.add(input);
			input="";
			input += "{";
			input += "\"testType\":" + "\"smoke\",";
		}
		}
	}
		else if(testType.equals("regression")) {
		input = "";
		int[] permutationValidInvalid = new int[requestKeys.size()];
		permutationValidInvalid[0] = 1;
		for (Integer data : permutationValidInvalid) {
			input += data;
		}
		List<String> validInvalid = permutation.pack.Permutation.permutation(input);
		System.out.println("--------------------------------->"+validInvalid);
		input = "";
		for (String validInv : validInvalid) {
			input += "{";
			input += "\"testType\":" + "\"regression\",";
			int i = 0;
			for(Object obj: requestKeys.keySet()) {
				if (validInv.charAt(i) == '0') {
					input += '"' + obj.toString() + '"' + ":" + "\"valid\"" + ",";
				}
				else if (validInv.charAt(i) == '1') {
					input += '"' + obj.toString() + '"' + ":" + "\"invalid\"" + ",";
					for(int k=0;k<listOfFolders.length;k++) {
						if(listOfFolders[k].getName().toLowerCase().contains(obj.toString().toLowerCase())) {
							input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
							input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
							id++;
							break;
						}
				}
				}
				i++;
			}
			input += "\"status\":" + "\"\"";
			input += "}";
			scenario.add(input);
			input = "";
		}
		}
		else if(testType.toLowerCase().equals("smokeandregression")){
			System.out.println("in Smoke---------------------------------------------------------------------------------------------->");
			input += "{";
			input += "\"testType\":" + "\"smoke\",";
			//input += "\"jiraId\":" + "\"MOS-1000\",";
			for(int k=0;k<listOfFolders.length;k++) {
			if(listOfFolders[k].getName().contains("smoke")) {
				input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
				input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
				for(Object obj: requestKeys.keySet()) {
					input += '"' + obj.toString() + '"' + ":" + "\"valid\",";
				}
				input += "\"status\":" + "\"\"";
				input += "}";
				scenario.add(input);
				input="";
				input += "{";
				input += "\"testType\":" + "\"smoke\",";
			}
			}
			System.out.println("Scenario is ---------------------------------------------------------------------->"+scenario);
			input = "";
			int[] permutationValidInvalid = new int[requestKeys.size()];
			permutationValidInvalid[0] = 1;
			for (Integer data : permutationValidInvalid) {
				input += data;
			}
			List<String> validInvalid = permutation.pack.Permutation.permutation(input);
			System.out.println("--------------------------------->"+validInvalid);
			input = "";
			for (String validInv : validInvalid) {
				input += "{";
				input += "\"testType\":" + "\"regression\",";
				//input += "\"jiraId\":" + "\"MOS-1000\",";
				int i = 0;
				/*for (Field f : fields) {
					if (validInv.charAt(i) == '0')
						input += '"' + f.getName() + '"' + ":" + "\"valid\"" + ",";
					if (validInv.charAt(i) == '1')
						input += '"' + f.getName() + '"' + ":" + "\"invalid\"" + ",";
					i++;
				}
				*/
				for(Object obj: requestKeys.keySet()) {
					if (validInv.charAt(i) == '0') {
						input += '"' + obj.toString() + '"' + ":" + "\"valid\"" + ",";
					}
					else if (validInv.charAt(i) == '1') {
						input += '"' + obj.toString() + '"' + ":" + "\"invalid\"" + ",";
						for(int k=0;k<listOfFolders.length;k++) {
							if(listOfFolders[k].getName().toLowerCase().contains(obj.toString().toLowerCase())) {
								input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
								input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
								id++;
								break;
							}
					}
					}
					i++;
			
				}
				
				input += "\"status\":" + "\"\"";
				input += "}";
				scenario.add(input);
				input = "";
			}
		}
		
		
		
		//System.out.println(scenario);

		String configpath="src/test/resources/" + module+"/"+ouputFile;

		File json = new File(configpath);
		FileWriter fw = new FileWriter(json);
		fw.write(scenario.toString());
		fw.flush();
		fw.close();

	}
	
	
	public Response postRequest(String url, Object body, String contentHeader, String acceptHeader) {

		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		//logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		//logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	} // end POST_REQUEST

	public Response postRequestToDecrypt(String url, Object body, String contentHeader, String acceptHeader) {

		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).when().post(url).then().extract().response();
		// log then response
		//logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		//logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @author Arjun
	 * this method is specifically for email notification
	 * @param jsonString
	 * @param serviceUri
	 * @return
	 */
	public Response postRequestWithBodyAsMultipartFormData(JSONObject jsonString, String serviceUri) {
		Response postResponse=null;
		if(jsonString.get("attachments").toString().isEmpty()) {
		postResponse = given().relaxedHTTPSValidation().contentType("multipart/form-data")
				.multiPart("mailContent", (String) jsonString.get("mailContent"))
				.multiPart("mailTo", (String) jsonString.get("mailTo"))
				.multiPart("mailSubject", (String) jsonString.get("mailSubject"))
				.multiPart("mailCc", (String) jsonString.get("mailCc"))
				.post(serviceUri)
				.then()
				.log()
				.all()
				.extract()
				.response();
		}else {
			postResponse = given().relaxedHTTPSValidation().contentType("multipart/form-data")
					.multiPart("attachments",new File((String) jsonString.get("attachments")))
					.multiPart("mailContent", (String) jsonString.get("mailContent"))
					.multiPart("mailTo", (String) jsonString.get("mailTo"))
					.multiPart("mailSubject", (String) jsonString.get("mailSubject"))
					.multiPart("mailCc", (String) jsonString.get("mailCc"))
					.post(serviceUri)
					.then()
					.log()
					.all()
					.extract()
					.response();
		}
			    
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
    
	public Response putRequest(String url, Object body, String contentHeader, String acceptHeader) {

		Response putResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().put(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	} // end PUT_REQUEST


	/**
	 * REST ASSURED GET request method
	 *
	 * @param url
	 *            destination of the request
	 * @return Response object that has the REST response
	 */
// end GET_REQUEST


	public Response postFileEncrypt(File file, String url) {
		logger.info("REST:ASSURED:Sending a data packet to" + url);

		Response getResponse = given().relaxedHTTPSValidation().multiPart("encryptedFile", file).expect().when().post(url);
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}
	public Response Post_File_Decrypt(File file, String url) {
		logger.info("REST:ASSURED:Sending a data packet to" + url);

		Response getResponse = given().relaxedHTTPSValidation().multiPart("decryptedFile", file).expect().when().post(url);
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}


    /**
    * REST ASSURED GET request method
    *
    * @param url
    *            destination of the request
    * @return Response object that has the REST response
    */
    public Response getRequestPathQueryParam(String url,HashMap<String, String> path_value,HashMap<String, List<String>> query_value) {
          logger.info("REST-ASSURED: Sending a GET request to " + url);
          Response getResponse = given().relaxedHTTPSValidation().pathParameters(path_value).queryParams(query_value)
                      .log().all().when().get(url).then().log().all().extract().response();
          // log then response
          logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
          logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
          return getResponse;
    } // end GET_REQUEST
    
    /**
     * REST ASSURED GET request method
     *
     * @param url
     *            destination of the request
     * @return Response object that has the REST response
     */
     public Response getRequestQueryParam(String url,HashMap<String, String> valueMap) {
           logger.info("REST-ASSURED: Sending a GET request to " + url);
           Response getResponse = given().relaxedHTTPSValidation().queryParams(valueMap)
                       .log().all().when().get(url).then().log().all().extract().response();
           // log then response
           logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
           logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
           return getResponse;
     } // end GET_REQUEST
    

    /**
     * REST ASSURED GET request method
     *
     * @param url
     *            destination of the request
     * @return Response object that has the REST response
     */
     public Response getRequestPathParameters(String url,HashMap<String, String> valueMap) {
           logger.info("REST-ASSURED: Sending a GET request to " + url);
           Response getResponse = given().relaxedHTTPSValidation().pathParams(valueMap)
                       .log().all().when().get(url).then().log().all().extract().response();
           // log then response
           logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
           logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
           return getResponse;
     } // end GET_REQUEST
     public Response getRequestWithoutParameters(String url) {
         logger.info("REST-ASSURED: Sending a GET request to " + url);
         Response getResponse = given().relaxedHTTPSValidation()
                     .log().all().when().get(url).then().log().all().extract().response();
         // log then response
         logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
         logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
         return getResponse;
   } // end GET_REQUEST
    
    public Response putRequest(String url,String contentHeader,String acceptHeader,HashMap<String, String> valueMap) {
    	  logger.info("REST-ASSURED: Sending a PUT request to " + url);
    	  Response getResponse= given().relaxedHTTPSValidation().queryParams(valueMap).log().all().when().put(url).then().log().all().extract().response();
    	  logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	  logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
    	  return getResponse;
   }
    
    public Response deleteRequest(String url,HashMap<String, String> valueMap) {
    	logger.info("REST-ASSURED: Sending a DELETE request to   "+ url);
    	Response getResponse=given().relaxedHTTPSValidation().queryParams(valueMap).log().all().when().delete(url).then().log().all().extract().response();
    	logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    public Response deleteRequestPathParameters(String url,HashMap<String, String> valueMap) {
    	logger.info("REST-ASSURED: Sending a DELETE request to   "+ url);
    	Response getResponse=given().relaxedHTTPSValidation().pathParams(valueMap).log().all().when().delete(url).then().log().all().extract().response();
    	logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    public Response postDataPacket(File file,String url) {
    	logger.info("REST:ASSURED:Sending a data packet to"+url);
    	
    	Response getResponse=given().relaxedHTTPSValidation().multiPart("file",file).expect().when().post(url);
    	logger.info("REST:ASSURED: The response from request is:"+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    public Response postJsonWithFile(Object body,File file,String url,String contentHeader) {
    	logger.info("REST:ASSURED:Sending a data packet to"+url);
    	logger.info("Request DTO for document upload is"+ body);
    	logger.info("Name of the file is"+file.getName());
    	Response getResponse = null;

		/*
    	 * Fetch to get the param name to be passed in the request
    	 */
    	String Document_request=fetch_IDRepo("req.Documentrequest");
    
    	 getResponse=given().relaxedHTTPSValidation().multiPart("file",file).formParam(Document_request, body).contentType(contentHeader).expect().when().post(url);
    	logger.info("REST:ASSURED: The response from request is:"+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    
    /**
     * REST ASSURED GET request method
     *
     * @param url
     *            destination of the request
     * @return Response object that has the REST response
     */
     public Response getRequestQueryParam(String url, String contentHeader, String acceptHeader, String urls) {
           logger.info("REST-ASSURED: Sending a GET request to " + url);
           Response getResponse = given().relaxedHTTPSValidation()
                       .log().all().when().get(url+"?"+urls).then().log().all().extract().response();
           // log then response
           logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
           logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
           return getResponse;
     } // end GET_REQUEST
    
     
    public static void backUpFiles(String source, String destination) {
    	//String time = java.time.LocalDate.now().toString()+"--"+java.time.LocalTime.now().toString();
    	 Calendar cal = Calendar.getInstance();
         cal.setTime(Date.from(Instant.now()));
  
    	String result = String.format(
                  "%1$tY-%1$tm-%1$td-%1$tk-%1$tS-%1$tp", cal);
    //System.out.println(System.getProperty("APPDATA"));
		String filePath="src/test/resources/APPDATA/MosipUtil/UtilFiles/"+destination+"/"+result;
		File sourceFolder = new File(source);
		File dest = new File(filePath);
		try {
		FileUtils.copyDirectory(sourceFolder,dest);
		logger.info("Please Check Your %APPDATA% in C drive to get access to the generted files");
		}catch(IOException e) {
			logger.info("Check %APPDATA%");
		}
    }
    
    public Response getRequestPathParam(String url,String id,String keyId, java.lang.String timestamp, java.lang.String keytimestamp) {
        logger.info("REST-ASSURED: Sending a GET request to " + url);
        HashMap params= new HashMap();
        params.put(keyId, id);
        params.put(keytimestamp, timestamp);
        Response getResponse = given().relaxedHTTPSValidation().pathParams(params)
                    .log().all().when().get(url).then().log().all().extract().response();
        // log then response
        logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
        logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
        return getResponse;
  } // end GET_REQUEST

    public Response putRequestWithBody(String url,String contentHeader,String acceptHeader,JSONObject valueMap) {
    	  logger.info("REST-ASSURED: Sending a PUT request to " + url);
    	  
    	  Response getResponse= given().relaxedHTTPSValidation().contentType(MediaType.APPLICATION_JSON).body(valueMap.toJSONString()).log().all().when().put(url).then().log().all().extract().response();
    	  logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	  logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
    	  return getResponse;
   }
    
   
      
    public Response postRequestWithQueryParams(String url, Object body, String contentHeader, String acceptHeader,HashMap<String, String> valueMap) {

  		Response postResponse = given().relaxedHTTPSValidation().body(body).queryParams(valueMap).contentType(contentHeader)
  				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
  		// log then response
  		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
  		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
  		return postResponse;
  	} // end POST_REQUEST

    
    
 // GLOBAL CLASS VARIABLES
 	private Properties prop;
 	
 	 	public String fetch_IDRepo(String element)
 	 	{
 	 		
 	 		
 	 		String IDRepo_Element = null;
 	 		try {
 	 			logger.info("Fetching ID Repo related properties to validate in the response");
 	 			prop = new Properties();
 	 			InputStream inputStream = new FileInputStream(

 	 					"src/config/IDRepo.properties");

 	 			prop.load(inputStream);
 	 			switch(element)
 	 			{
 	 			case "req.Documentrequest":
 	 				
 	 				IDRepo_Element=prop.getProperty("req.Documentrequest"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for req.Documentreques.  " +IDRepo_Element);
 	 			break;
 	 			
 	 			case "preReg_CreateApplnURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_CreateApplnURI"); 	
 	 				
 	 				
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_CreateApplnURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_BookingAppointmentURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_BookingAppointmentURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_BookingAppointmentURI.  " +IDRepo_Element);
 	 			break;
               case "preReg_CancelAppointmentURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_CancelAppointmentURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_CancelAppointmentURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_FetchAllApplicationCreatedByUserURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_FetchAllApplicationCreatedByUserURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_FetchAllApplicationCreatedByUserURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_FetchStatusOfApplicationURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_FetchStatusOfApplicationURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_FetchStatusOfApplicationURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_FetchAllPreRegistrationIdsURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_FetchAllPreRegistrationIdsURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_FetchAllPreRegistrationIdsURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_FecthAppointmentDetailsURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_FecthAppointmentDetailsURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_FecthAppointmentDetailsURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_FetchRegistrationDataURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_FetchRegistrationDataURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_FetchRegistrationDataURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_FetchPreRegistrationByDateAndTimeURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_FetchPreRegistrationByDateAndTimeURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_FetchPreRegistrationByDateAndTimeURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_FetchPreRegistrationURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_FetchPreRegistrationURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_FetchPreRegistrationURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_DocumentUploadURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_DocumentUploadURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_DocumentUploadURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_FetchCenterIDURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_FetchCenterIDURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_FetchCenterIDURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_DataSyncnURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_DataSyncnURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_DataSyncnURI.  " +IDRepo_Element);
 	 			break;
                case "bookedPreIdByRegId_URI":
 	 				
 	 				IDRepo_Element=prop.getProperty("bookedPreIdByRegId_URI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for bookedPreIdByRegId_URI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_FetchAllDocumentURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_FetchAllDocumentURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_FetchAllDocumentURI.  " +IDRepo_Element);
 	 			break;
 	 			case "prereg_DeleteDocumentByDocIdURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("prereg_DeleteDocumentByDocIdURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for prereg_DeleteDocumentByDocIdURI.  " +IDRepo_Element);
 	 			break;
               case "preReg_ExpiredURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_ExpiredURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_ExpiredURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_DeleteAllDocumentByPreIdURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_DeleteAllDocumentByPreIdURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_DeleteAllDocumentByPreIdURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_CopyDocumentsURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_CopyDocumentsURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_CopyDocumentsURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_FetchBookedPreIdByRegIdURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_FetchBookedPreIdByRegIdURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_FetchBookedPreIdByRegIdURI.  " +IDRepo_Element);
 	 			break;
 	 			case "preReg_UpdateStatusAppURII":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_UpdateStatusAppURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_CopyDocumentsURI.  " +IDRepo_Element);
 	 			break;

             case "preReg_RetriveBookedPreIdsByRegId":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_RetriveBookedPreIdsByRegId"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_RetriveBookedPreIdsByRegId.  " +IDRepo_Element);
 	 			break;

 	 			case "preReg_DiscardApplnURI":
 	 				
 	 				IDRepo_Element=prop.getProperty("preReg_DiscardApplnURI"); 	
 	 			
 	 			logger.info("Configs from properties file is fetched for preReg_DiscardApplnURI.  " +IDRepo_Element);
 	 			break;
 	 			
 	 			

 	 			default:
 	 				break;
 	 			
 	 			
 	 			}

 	 		} catch (IOException e) {
 	 			logger.error("Could not find the properties file.\n" + e);
 	 		}
 	 		
 	 		
 	 		
 	 		return IDRepo_Element;
 	 	
 	 	}
 	 	
 	 	
	public Response putRequestWithoutBody(String url,String contentHeader,String acceptHeader) {
  logger.info("REST-ASSURED: Sending a PUT request to " + url);
    	  
    	  Response getResponse= given().relaxedHTTPSValidation().contentType(MediaType.APPLICATION_JSON).log().all().when().put(url).then().log().all().extract().response();
    	  logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	  logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
    	  return getResponse;
	}
	
	/**
	 * @author Arjun
	 * for id repo
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @return
	 */
	public Response patchRequest(String url, Object body, String contentHeader, String acceptHeader) {

		Response putResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().patch(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	} 
	
	/*public Response Post_JSONwithFileParam(Object body,File file,String url,String contentHeader,String langCodeKey,String value) {
	       logger.info("REST:ASSURED:Sending a data packet to"+url);
	       logger.info("Request DTO for document upload is"+ body);
	       logger.info("Name of the file is"+file.getName());
	       Response getResponse = null;
	             
	        * Fetch to get the param name to be passed in the request
	        
	       
	       String Notification_request=fetch_IDRepo("req.notify");
	        getResponse=given().relaxedHTTPSValidation().multiPart("file",file).formParam(Notification_request, body).formParam(langCodeKey,value).contentType(contentHeader).expect().when().post(url);
	       
	       
	       logger.info("REST:ASSURED: The response from request is:"+getResponse.asString());
	       logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
	       return getResponse;
	    }
	*/
	
		 //Notify
		    public Response postJsonWithFileParam(Object body,File file,String url,String contentHeader,String langCodeKey,String value) {
		    	logger.info("REST:ASSURED:Sending a data packet to"+url);
		    	logger.info("Request DTO for document upload is"+ body);
		    	logger.info("Name of the file is"+file.getName());
		    	Response getResponse = null;
				/*
		    	 * Fetch to get the param name to be passed in the request
		    	 */
		    	
		    	String Notification_request=fetch_IDRepo("req.notify");
		    	 getResponse=given().relaxedHTTPSValidation().multiPart("file",file).formParam(Notification_request, body).formParam(langCodeKey,value).contentType(contentHeader).expect().when().post(url);
		    	
		    	
		    	logger.info("REST:ASSURED: The response from request is:"+getResponse.asString());
		    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
		    	return getResponse;
		    } 
		 



	public Response getRequestWithoutBody(String url,String contentHeader,String acceptHeader) {
        logger.info("REST-ASSURED: Sending a Get request to " + url);
               
               Response getResponse= given().relaxedHTTPSValidation().contentType(MediaType.APPLICATION_JSON).log().all().when().get(url).then().log().all().extract().response();
               logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
               logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
               return getResponse;
             }
}