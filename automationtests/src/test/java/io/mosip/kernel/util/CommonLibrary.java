package io.mosip.kernel.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.http.Cookie;
import io.restassured.response.Response;


public class CommonLibrary extends BaseTestCase{

	private static Logger logger = Logger.getLogger(CommonLibrary.class);
	PreRegistrationLibrary lib=new PreRegistrationLibrary();

	public static void configFileWriter(String folderPath,String requestKeyFile,String generationType,String baseFileName)
				throws Exception {
		String splitRegex = Pattern.quote(System.getProperty("file.separator"));
		String string[]=new String[2];
		string=folderPath.split(splitRegex);
		@SuppressWarnings("unused")
		String api=string[0];
		@SuppressWarnings("unused")
		String testSuite=string[1];

		String requestFilePath= "src/test/resources/"+folderPath+"/"+requestKeyFile;
		String configFilePath="src/test/resources/"+folderPath+"/"+"FieldConfig.properties";

		JSONObject requestKeys= (JSONObject) new JSONParser().parse(new FileReader(requestFilePath));
		Properties properties = new Properties();
		Properties cloneProperties=new Properties();
		properties.load(new FileReader(new File(configFilePath)));
		cloneProperties.load(new FileReader(new File(configFilePath)));
		@SuppressWarnings("unused")
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
		logger.error(e);
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
	
	
	
	
	public Response postRequest(String url, Object body, String contentHeader, String acceptHeader,String cookie) {

		Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
		
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	} // end POST_REQUEST
	
	public Response postRequest(String url, Object body, String contentHeader, String acceptHeader) {

		
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	} // end POST_REQUEST
	
	public Response dataSyncPostRequest(String url, Object body, String contentHeader, String acceptHeader,String cookie) {
		  Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	
	public Response authPostRequest(String url, Object body, String contentHeader, String acceptHeader,String cookie) {

		Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
		
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	} 
	
	
	
	/**
	 * @author Arjun
	 * this method is specifically for email notification
	 * @param jsonString
	 * @param serviceUri
	 * @return
	 */
	public Response post_RequestWithBodyAsMultipartFormData(JSONObject jsonString, String serviceUri,String cookie) {
		Response postResponse=null;
		Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
		if(jsonString.get("attachments").toString().isEmpty()) {
		postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType("multipart/form-data")
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
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType("multipart/form-data")
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
    
	public Response putRequest(String url, Object body, String contentHeader, String acceptHeader,String cookie) {
		
		Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
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
    @SuppressWarnings("deprecation")
	public Response getRequestPathQueryParam(String url,HashMap<String, String> path_value,HashMap<String, List<String>> query_value,String cookie) {
          logger.info("REST-ASSURED: Sending a GET request to " + url);
          
          Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
          Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParameters(path_value).queryParams(query_value)
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
     public Response getRequestQueryParam(String url,HashMap<String, String> valueMap,String cookie) {
           logger.info("REST-ASSURED: Sending a GET request to " + url);
        
           Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
      	 Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap)
                             .log().all().when().get(url).then().log().all().extract().response();
           // log then response
           logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
           logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
           return getResponse;
     } // end GET_REQUEST
     
     public Response getRequestQueryParamDataSync(String url,HashMap<String, String> valueMap,String cookie) {
         logger.info("REST-ASSURED: Sending a GET request to " + url);
  
		  Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
    	 Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap)
                           .log().all().when().get(url).then().log().all().extract().response();
         // log then response
         logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
         logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
         return getResponse;
   }
    

    /**
     * REST ASSURED GET request method
     *
     * @param url
     *            destination of the request
     * @return Response object that has the REST response
     */
     public Response getRequestPathParameters(String url,HashMap<String, String> valueMap,String cookie) {
           logger.info("REST-ASSURED: Sending a GET request to " + url);
           Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
           Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(valueMap)
                       .log().all().when().get(url).then().log().all().extract().response();
           // log then response
           logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
           logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
           return getResponse;
     } // end GET_REQUEST
     
     
     public Response getRequestWithoutParameters(String url,String cookie) {
         logger.info("REST-ASSURED: Sending a GET request to " + url);
         Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
         Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation()
                     .log().all().when().get(url).then().log().all().extract().response();
         // log then response
         logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
         logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
         return getResponse;
   } // end GET_REQUEST
    
    public Response putRequest(String url,String contentHeader,String acceptHeader,HashMap<String, String> valueMap,String cookie) {
    	  logger.info("REST-ASSURED: Sending a PUT request to " + url);
    	  Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
    	  Response getResponse= given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap).log().all().when().put(url).then().log().all().extract().response();
    	  logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	  logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
    	  return getResponse;
   }
    
    public Response deleteRequest(String url,HashMap<String, String> valueMap,String cookie) {
    	logger.info("REST-ASSURED: Sending a DELETE request to   "+ url);
    	Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
		Response getResponse=given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap).log().all().when().delete(url).then().log().all().extract().response();
    	logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    public Response deleteRequestPathParameters(String url,HashMap<String, String> valueMap,String cookie) {
    	logger.info("REST-ASSURED: Sending a DELETE request to   "+ url);
    	Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
    	Response getResponse=given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(valueMap).log().all().when().delete(url).then().log().all().extract().response();
    	logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    public Response postDataPacket(File file,String url,String cookie) {
    	logger.info("REST:ASSURED:Sending a data packet to"+url);
    	Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
    	Response getResponse=given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file",file).expect().when().post(url);
    	logger.info("REST:ASSURED: The response from request is:"+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    public Response postJSONwithFile(Object body,File file,String url,String contentHeader,String cookie) {
    	logger.info("REST:ASSURED:Sending a data packet to"+url);
    	logger.info("Request DTO for document upload is"+ body);
    	logger.info("Name of the file is"+file.getName());
    	Response getResponse = null;
		/*
    	 * Fetch to get the param name to be passed in the request
    	 */
    	
    	String Document_request=fetch_IDRepo().get("req.Documentrequest");
    
    	  Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
		  getResponse=given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file",file).formParam(Document_request, body).contentType(contentHeader).expect().when().post(url);
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
     public Response getRequestQueryParam(String url, String contentHeader, String acceptHeader, String urls,String cookie) {
           logger.info("REST-ASSURED: Sending a GET request to " + url);
           Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
           Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation()
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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Response getRequestPathParam(String url,String id,String keyId, java.lang.String timestamp, java.lang.String keytimestamp,String cookie) {
        logger.info("REST-ASSURED: Sending a GET request to " + url);
        Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
        HashMap params= new HashMap();
        params.put(keyId, id);
        params.put(keytimestamp, timestamp);
        Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(params)
                    .log().all().when().get(url).then().log().all().extract().response();
        // log then response
        logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
        logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
        return getResponse;
  } // end GET_REQUEST

    public Response putRequestWithBody(String url,String contentHeader,String acceptHeader,JSONObject valueMap,String cookie) {
    	  logger.info("REST-ASSURED: Sending a PUT request to " + url);
    	  Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
    	  Response getResponse= given().cookie(builder.build()).relaxedHTTPSValidation().contentType(MediaType.APPLICATION_JSON).body(valueMap.toJSONString()).log().all().when().put(url).then().log().all().extract().response();
    	  logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	  logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
    	  return getResponse;
   }
    
   
      
    public Response postRequestWithQueryParams(String url, Object body, String contentHeader, String acceptHeader,HashMap<String, String> valueMap,String cookie) {
    	Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
  		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).queryParams(valueMap).contentType(contentHeader)
  				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
  		// log then response
  		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
  		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
  		return postResponse;
  	} // end POST_REQUEST

    
    
 // GLOBAL CLASS VARIABLES
 	
 	Properties prop = new Properties();
 	
 	 	public Map<String, String> fetch_IDRepo()
 	 	{
 	 		try {
 				prop.load(new FileInputStream( "src/config/IDRepo.properties" ));
 			} catch (IOException e1) {
 				 
 				e1.printStackTrace();
 			}

 			Map<String, String> mapProp = prop.entrySet().stream().collect(
 			    Collectors.toMap(
 			        e -> (String) e.getKey(),
 			        e -> (String) e.getValue()
 			    ));
 	 		
			return mapProp;
 	 		

 	 	
 	 	}
 	 	

 	 	 // GLOBAL CLASS VARIABLES
 	 	
 	 	 	public Map<String, String> kernenReadProperty()
 	 	 	{
 	 	 		try {
 	 				prop.load(new FileInputStream( "src/config/Kernel.properties" ));
 	 			} catch (IOException e1) {
 	 				 
 	 				e1.printStackTrace();
 	 			}

 	 			Map<String, String> mapProp = prop.entrySet().stream().collect(
 	 			    Collectors.toMap(
 	 			        e -> (String) e.getKey(),
 	 			        e -> (String) e.getValue()
 	 			    ));
 	 	 		
 				return mapProp;
 	 	 		

 	 	 	
 	 	 	}

	public Response putRequestWithoutBody(String url,String contentHeader,String acceptHeader,String cookie) {
  logger.info("REST-ASSURED: Sending a PUT request to " + url);
Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
    	  Response getResponse= given().cookie(builder.build()).relaxedHTTPSValidation().contentType(MediaType.APPLICATION_JSON).log().all().when().put(url).then().log().all().extract().response();
    	  logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	  logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
    	  return getResponse;
	}
	public Response adminPutRequestWithoutBody(String url,String contentHeader,String acceptHeader) {
		  logger.info("REST-ASSURED: Sending a PUT request to " + url);
		  PreRegistrationLibrary lib=new PreRegistrationLibrary();
		  String preRegAdminAuthToken = lib.preRegAdminToken();
		  Cookie.Builder builder = new Cookie.Builder("Authorization",preRegAdminAuthToken);
		    	  Response getResponse= given().cookie(builder.build()).relaxedHTTPSValidation().contentType(MediaType.APPLICATION_JSON).log().all().when().put(url).then().log().all().extract().response();
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
	public Response patchRequest(String url, Object body, String contentHeader, String acceptHeader,String cookie) {
		 Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
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
		    public Response postJSONwithFileParam(Object body,File file,String url,String contentHeader,String langCodeKey,String value,String cookie) {
		    	logger.info("REST:ASSURED:Sending a data packet to"+url);
		    	logger.info("Request DTO for document upload is"+ body);
		    	logger.info("Name of the file is"+file.getName());
		    	Response getResponse = null;
				/*
		    	 * Fetch to get the param name to be passed in the request
		    	 */
		    	
		    	 Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
		    	String Notification_request=fetch_IDRepo().get("req.notify");
		    	 getResponse=given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file",file).formParam(Notification_request, body).formParam(langCodeKey,value).contentType(contentHeader).expect().when().post(url);
		    	
		    	
		    	logger.info("REST:ASSURED: The response from request is:"+getResponse.asString());
		    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
		    	return getResponse;
		    } 
		 



	public Response getRequestWithoutBody(String url,String contentHeader,String acceptHeader,String cookie) {
        logger.info("REST-ASSURED: Sending a Get request to " + url);
               Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
               Response getResponse= given().cookie(builder.build()).relaxedHTTPSValidation().contentType(MediaType.APPLICATION_JSON).log().all().when().get(url).then().log().all().extract().response();
               logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
               logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
               return getResponse;
             }

	@SuppressWarnings("deprecation")
	public Response getRequestPathQueryParamString(String url,HashMap<String, String> pathValue,HashMap<String, String> queryValue,String cookie) {
        logger.info("REST-ASSURED: Sending a GET request to " + url);
        Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
        Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParameters(pathValue).queryParams(queryValue)
                    .log().all().when().get(url).then().log().all().extract().response();
        // log then response
        logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
        logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
        return getResponse;
  } // end GET_REQUEST 

}