package io.mosip.kernel.cbeffutil.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.cbeffutil.container.impl.CbeffContainerImpl;
import io.mosip.kernel.core.cbeffutil.common.CbeffValidator;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.exception.CbeffException;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;

/**
 * This class is used to create,update, validate and search Cbeff data
 * @author Ramadurai Pandian
 *
 */
@Component
public class CbeffImpl implements CbeffUtil {
	
	/*
	 * XSD storage path from config server
	 * */
	
	@Value("${mosip.kernel.xsdstorage-uri}")
	private String configServerFileStorageURL;
	
	/*
	 * XSD file name
	 * */
	
	@Value("${mosip.kernel.xsdfile}")
	private String schemaName;

	/**
	 * Method used for creating Cbeff XML
	 * 
	 * @param birList pass List of BIR for creating Cbeff data
	 *        
	 * @return return byte array of XML data
	 * @throws CbeffException 
	 * @throws Exception 
	 * 
	 */
	@Override
	public byte[] createXML(List<BIR> birList) throws Exception  {
		CbeffContainerImpl cbeffContainer = new CbeffContainerImpl();
		BIRType bir = cbeffContainer.createBIRType(birList);
		byte[] xsd = getXSDfromConfigServer();
		byte[] xmlByte = CbeffValidator.createXMLBytes(bir,xsd);
		return xmlByte;
	}

	private byte[] getXSDfromConfigServer() throws URISyntaxException, IOException {
		InputStream input = new URL(configServerFileStorageURL+schemaName).openStream();
		byte[] fileContent = readbytesFromStream(input);
		return fileContent;
	}

	private byte[] readbytesFromStream(InputStream inputStream) throws IOException {
		 ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		  // this is storage overwritten on each iteration with bytes
		  int bufferSize = 1024;
		  byte[] buffer = new byte[bufferSize];
		  // we need to know how may bytes were read to write them to the byteBuffer
		  int len = 0;
		  while ((len = inputStream.read(buffer)) != -1) {
		    byteBuffer.write(buffer, 0, len);
		  }
		  // and then we can return your byte array.
		  return byteBuffer.toByteArray();

	}

	/**
	 * Method used for updating Cbeff XML
	 * 
	 * @param birList pass List of BIR for creating Cbeff data
	 *        
	 * @return return byte array of XML data
	 * 
	 */
	@Override
	public byte[] updateXML(List<BIR> birList, byte[] fileBytes) throws Exception {
		CbeffContainerImpl cbeffContainer = new CbeffContainerImpl();
		BIRType bir = cbeffContainer.updateBIRType(birList, fileBytes);
		byte[] xsd = getXSDfromConfigServer();
		byte[] xmlByte = CbeffValidator.createXMLBytes(bir,xsd);
		return xmlByte;
	}

	/**
	 * Method used for validating XML against XSD
	 * 
	 * @param xmlBytes byte array of XML data
	 * 
	 * @param xsdBytes byte array of XSD data
	 *        
	 * @return boolean if data is valid or not
	 * 
	 */
	@Override
	public boolean validateXML(byte[] xmlBytes, byte[] xsdBytes) throws Exception {
		CbeffContainerImpl cbeffContainer = new CbeffContainerImpl();
		return cbeffContainer.validateXML(xmlBytes, xsdBytes);
	}

	/**
	 * Method used for validating XML against XSD
	 * 
	 * @param fileBytes byte array of XML data
	 * 
	 * @param type to be searched
	 * 
	 * @param subType to be searched
	 *        
	 * @return bdbMap Map of type and String of encoded biometric data
	 * 
	 */
	@Override
	public Map<String, String> getBDBBasedOnType(byte[] fileBytes, String type, String subType) throws Exception {
		BIRType bir = CbeffValidator.getBIRFromXML(fileBytes);
		Map<String, String> bdbMap = CbeffValidator.getBDBBasedOnTypeAndSubType(bir, type, subType);
		return bdbMap;
	}

	/**
	 * Method used for getting list of BIR from XML bytes
	 * 
	 * @param xmlBytes byte array of XML data
	 *        
	 * @return List of BIR data extracted from XML
	 * 
	 */
	@Override
	public List<BIRType> getBIRDataFromXML(byte[] xmlBytes) throws Exception {
		BIRType bir = CbeffValidator.getBIRFromXML(xmlBytes);
		return bir.getBIR();
	}

	/**
	 * Method used for getting Map of BIR from XML bytes with type and subType
	 * 
	 * @param xmlBytes byte array of XML data
	 * 
	 * @param String type
	 * 
	 * @param String subType
	 *        
	 * @return bdbMap Map of BIR data extracted from XML
	 * 
	 */
	@Override
	public Map<String, String> getAllBDBData(byte[] xmlBytes, String type, String subType) throws Exception {
		BIRType bir = CbeffValidator.getBIRFromXML(xmlBytes);
		Map<String, String> bdbMap = CbeffValidator.getAllBDBData(bir, type,subType);
		return bdbMap;
	}

}
