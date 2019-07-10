package io.mosip.preregistration.util;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * Util Class is to perform Document service smoke and regression test operations
 * 
 * @author Lavanya R
 * @since 1.0.0
 */
public class DocumentUploadUtil 
{

	/**
	 * Declaration of all variables
	 **/
	
	String testSuite = "";
	JSONObject request;
	Response response;
	ApplicationLibrary applnLib = new ApplicationLibrary();
	io.mosip.kernel.service.ApplicationLibrary appLib=new io.mosip.kernel.service.ApplicationLibrary();
	PreRegistrationUtil preregUtil=new PreRegistrationUtil();
	Logger logger = Logger.getLogger(BaseTestCase.class);
	String endpoint;
	
	
	 /**
   	 * The method perform documentUpload used to upload document with the metadata which include 
   	 * document category code, document type code and document format for a pre-registration Id.
   	 * Smoke and Regression test operation
   	 *  
   	 * @param map
   	 * @param preId
   	 * @param file
   	 * @param fileKeyName
   	 * @return cookie
   	 */
     public Response documentUpload(HashMap<String, String> map,String preId, File file,String cookie) {
    	
    	 
    	String preReg_DocumentUploadURI = preregUtil.fetchPreregProp().get("preReg_DocumentURI");
    	System.err.println(preReg_DocumentUploadURI);
    	String fileKeyVal= preregUtil.fetchPreregProp().get("req.fileKey");
    	logger.info("File path::"+fileKeyVal);
    	preReg_DocumentUploadURI=preReg_DocumentUploadURI+preId;
    	
    	response = appLib.postWithFileFormParams(preReg_DocumentUploadURI, map, file, fileKeyVal, cookie);
    	
 		return response;
 	}
     
	
	
	
	/**
	 * The method perform Delete Document By Document Id Smoke and Regression test operation
	 *
	 * @param docId
	 * @param preId
	 * @return Response
	 */
	public Response deleteAllDocumentByDocId(String docId, String preId) {

		HashMap<String, String> parm = new HashMap<>();
		parm.put("preRegistrationId", preId);

		String preregDeleteDocumentByDocIdURI = preregUtil.fetchPreregProp().get("preReg_DeleteDocumentByDocumentIdURI") + docId;
		response = applnLib.deleteRequestPathAndQueryParam(preregDeleteDocumentByDocIdURI, parm);

		return response;
	}
	
	/**
	 * The method perform Delete Document By PreRegisrtation Id Smoke and Regression test operation
	 *  
	 * @param preId
	 * @return Response
	 */
     public Response deleteAllDocumentByPreId(String preId) {
		
		String deleteDocumetByPreIdURI=preregUtil.fetchPreregProp().get("preReg_DeleteDocumentByPreregistrationIdURI")+preId;
		response=applnLib.deleteRequestWithPathParam(deleteDocumetByPreIdURI);
		return response;
	}
     
     
     /**
 	 * The method perform retrieve all documents metadata associated with particular pre-registration
 	 *  Smoke and Regression test operation
 	 *  
 	 * @param preId
 	 * @return Response
 	 */
     
     public Response getAllDocumentForPreId(String preId) {
 		String preRegGetDocByPreIdURI = preregUtil.fetchPreregProp().get("preReg_GetDocByPreId")+preId;
 		response = applnLib.getRequestWithoutParm(preRegGetDocByPreIdURI);
 		return response;
 	}
     
     /**
  	 * The method perform retrieve the document for a particular document id from the File System server
  	 *  Smoke and Regression test operation
  	 *  
  	 * @param preId
  	 * @param DocId
  	 * @return Response
  	 */
     public Response getAllDocumentForDocId(String preId, String docId) {
 		HashMap<String, String> parm = new HashMap<>();
 		parm.put("preRegistrationId", preId);

 		String preRegGetDocByDocId =  preregUtil.fetchPreregProp().get("preReg_GetDocByDocId") + docId;
 		response = applnLib.getRequestPathAndQueryParam(preRegGetDocByDocId, parm);
 		return response;
 	}
     
     
     /**
   	 * The method perform copy the document from source pre-registration id to destination pre-registration id 
   	 * with the specified document category code
   	 *  Smoke and Regression test operation
   	 *  
   	 * @param destPreId
   	 * @param sourcePreId
   	 * @param docCatCode
   	 * @return Response
   	 */
     public Response copyUploadedDocuments(String destPreId, String sourcePreId, String docCatCode) {

 		String preRegCopyDocumentsURI = preregUtil.fetchPreregProp().get("preReg_CopyDocumentsURI") + destPreId;

 		HashMap<String, String> parm = new HashMap<>();
 		parm.put("catCode", docCatCode);
 		parm.put("sourcePreId", sourcePreId);
 		response = applnLib.put_Request_pathAndMultipleQueryParam(preRegCopyDocumentsURI, parm);
 		return response;
 	}
     
     
}