package io.mosip.kernel.cbeffutil.service.impl;

import java.util.List;
import java.util.Map;

import io.mosip.kernel.cbeffutil.common.CbeffValidator;
import io.mosip.kernel.cbeffutil.container.impl.CbeffContainerImpl;
import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.service.CbeffI;

/**
 * This class is used to create,update, validate and search Cbeff data
 * @author Ramadurai Pandian
 *
 */
public class CbeffImpl implements CbeffI {

	/**
	 * Method used for creating Cbeff XML
	 * 
	 * @param birList pass List of BIR for creating Cbeff data
	 *        
	 * @return return byte array of XML data
	 * 
	 */
	@Override
	public byte[] createXML(List<BIR> birList) throws Exception {
		CbeffContainerImpl cbeffContainer = new CbeffContainerImpl();
		BIRType bir = cbeffContainer.createBIRType(birList);
		byte[] xmlByte = CbeffValidator.createXMLBytes(bir);
		return xmlByte;
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
		byte[] xmlByte = CbeffValidator.createXMLBytes(bir);
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

}
