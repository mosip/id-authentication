package io.mosip.kernel.cbeffutil.service.impl;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.cbeffutil.common.CbeffValidator;
import io.mosip.kernel.cbeffutil.container.impl.CbeffContainerImpl;
import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.exception.CbeffException;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.service.CbeffI;

/**
 * This class is used to create,update, validate and search Cbeff data
 * @author Ramadurai Pandian
 *
 */
@Component
public class CbeffImpl implements CbeffI {
	
	@Value("${mosip.kernel.xsdstorage-uri}")
	private String configServerFileStorageURL;
	
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
		URI url = new URI(configServerFileStorageURL+schemaName);
		byte[] fileContent = Files.readAllBytes(Paths.get(url));
		return fileContent;
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

	/* (non-Javadoc)
	 * @see io.mosip.kernel.cbeffutil.service.CbeffI#getLatestBDBData(io.mosip.kernel.cbeffutil.service.search.query.CbeffSearch)
	 */
	@Override
	public Map<String, String> getAllBDBData(byte[] xmlBytes, String type, String subType) throws Exception {
		BIRType bir = CbeffValidator.getBIRFromXML(xmlBytes);
		Map<String, String> bdbMap = CbeffValidator.getAllBDBData(bir, type,subType);
		return bdbMap;
	}

}
