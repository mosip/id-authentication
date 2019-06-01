package io.mosip.kernel.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;

import io.mosip.service.BaseTestCase;
import io.restassured.http.Cookie;
import io.restassured.response.Response;


public class CommonLibrary extends BaseTestCase{

	private static Logger logger = Logger.getLogger(CommonLibrary.class);

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
		List<String> scenario = new ArrayList<String>();
		String filepath= "src/test/resources/" + module+"/"+fileName;

		String configPaths = "src/test/resources/" +module;

		File folder = new File(configPaths);
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
			
			input = "";
			int[] permutationValidInvalid = new int[requestKeys.size()];
			permutationValidInvalid[0] = 1;
			for (Integer data : permutationValidInvalid) {
				input += data;
			}
			List<String> validInvalid = permutation.pack.Permutation.permutation(input);
		
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
          //logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
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
          // logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
           logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
           return getResponse;
     } // end GET_REQUEST
     
     public Response postRequestWithBodyAsMultipartFormData(JSONObject jsonString, String serviceUri,String cookie) {
          Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
 		Response postResponse=null;
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


    public Response putRequestWithBody(String url,String contentHeader,String acceptHeader,JSONObject valueMap,String cookie) {
    	  logger.info("REST-ASSURED: Sending a PUT request to " + url);
    	  Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
    	  Response getResponse= given().cookie(builder.build()).relaxedHTTPSValidation().contentType(MediaType.APPLICATION_JSON).body(valueMap.toJSONString()).log().all().when().put(url).then().log().all().extract().response();
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


	public Response putRequestWithoutBody(String url,String contentHeader,String acceptHeader,String cookie) {
  logger.info("REST-ASSURED: Sending a PUT request to " + url);
Cookie.Builder builder = new Cookie.Builder("Authorization",cookie);
    	  Response getResponse= given().cookie(builder.build()).relaxedHTTPSValidation().contentType(MediaType.APPLICATION_JSON).log().all().when().put(url).then().log().all().extract().response();
    	  logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	  logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
    	  return getResponse;
	}
	
 	Properties prop = new Properties();
 	
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
 	 	 	public static HashMap<String, String> readConfigProperty(String url, Map<String, String> configParamMap){
 	 	 		
 	 	 		CommonLibrary commonLibrary=new CommonLibrary();
 	 	 		
 	              //Adding Confing parameter keys into reqParams list
 	              List<String> reqParams = new ArrayList<String>();
 	       
 	              //uiConfigParams consist of Confing parameter keys
 	               String configParameters = commonLibrary.kernenReadProperty().get("ConfigParameters");
 	              String[] uiParams = configParameters.split(",");
 	              //Adding Confing parameter keys into reqParams list
 	              for (int i = 0; i < uiParams.length; i++) {
 	                     reqParams.add(uiParams[i]);
 	              }
 	              
 	              RestTemplate restTemplate = new RestTemplate();
 	              //Reading Property file
 	              String s = restTemplate.getForObject(url, String.class);
 	              final Properties p = new Properties();
 	              try {
 	                     p.load(new StringReader(s));
 	              } catch (IOException e1) {
 	                     e1.printStackTrace();
 	              }
 	              for (Entry<Object, Object> e : p.entrySet()) {
 	                     /**
 	                     * getting key and checking whether it is present in reqParams or not
 	                     * If it is present then add key and value into configParamMap
 	                     */
 	                     if (reqParams.contains(String.valueOf(e.getKey()))) {
 	                           configParamMap.put(String.valueOf(e.getKey()), e.getValue().toString());
 	                     }
 	              }
 	              return (HashMap<String, String>) configParamMap;
 	       }


 	  /*     public void retrivePreRegistrationDataForCancelAppointment() {
 	    	  CommonLibrary commonLibrary=new CommonLibrary();
 	              Map<String, String> configParamMap = new HashMap<>();
 	              HashMap<String, String> map = commonLibrary.readConfigProperty("http://104.211.212.28:51000/pre-registration/qa/0.10.0/application-qa.properties", configParamMap);
 	              map = commonLibrary.readConfigProperty("http://104.211.212.28:51000/pre-registration/qa/0.10.0/pre-registration-qa.properties", map);
 	              System.out.println(map);
 	       }*/

 	 	 	
 	 	 	/**
 	 	 	 * @param response
 	 	 	 * This method is for checking the authentication is pass or fail in rest services
 	 	 	 */
 	 	 	public void responseAuthValidation(Response response){
 	 	 		JSONArray errors = null;
				try {
					errors = (JSONArray) ((JSONObject) new JSONParser().parse(response.asString())).get("errors");
				} catch (ParseException e) {
					Assert.assertTrue(false, "Response from the service is not able to parse ");
				}
 				// fetching json array of objects from response
 	 	 		if(errors != null) {
 	 	 			String errorCode = ((JSONObject) errors.get(0)).get("errorCode").toString();
 	 	 			String errorMessage = ((JSONObject)errors.get(0)).get("message").toString();
 	 	 			if(errorCode.contains("ATH")) {
 	 	 				Assert.assertTrue(false, "Failed due to Authentication failure. Error message is='"+errorMessage+"'");
 	 	 			}
 	 	 		}
 	 	 	}
	


}