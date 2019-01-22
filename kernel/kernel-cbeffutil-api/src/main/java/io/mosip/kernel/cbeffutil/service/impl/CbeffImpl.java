/**
 * 
 */
package io.mosip.kernel.cbeffutil.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Element;

import io.mosip.kernel.cbeffutil.common.CbeffValidator;
import io.mosip.kernel.cbeffutil.container.impl.CbeffContainerImpl;
import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.jaxbclasses.ObjectFactory;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.cbeffutil.jaxbclasses.TestBiometrics;
import io.mosip.kernel.cbeffutil.service.CbeffI;

/**
 * @author Ramadurai Pandian
 *
 */
public class CbeffImpl implements CbeffI {

	@Override
	public byte[] createXML(List<BIR> birList) throws Exception {
		CbeffContainerImpl cbeffContainer = new CbeffContainerImpl();
		BIRType bir = cbeffContainer.createBIRType(birList);
		byte[] xmlByte = CbeffValidator.createXMLBytes(bir);
		return xmlByte;
	}

	@Override
	public byte[] updateXML(List<BIR> birList, byte[] fileBytes) throws Exception {
		CbeffContainerImpl cbeffContainer = new CbeffContainerImpl();
		BIRType bir = cbeffContainer.updateBIRType(birList, fileBytes);
		byte[] xmlByte = CbeffValidator.createXMLBytes(bir);
		return xmlByte;
	}

	@Override
	public boolean validateXML(byte[] xmlBytes, byte[] xsdBytes) throws Exception {
		CbeffContainerImpl cbeffContainer = new CbeffContainerImpl();
		return cbeffContainer.validateXML(xmlBytes, xsdBytes);
	}

	@Override
	public String getTestElementDetails(byte[] xmlBytes, byte[] xsdBytes) throws Exception {
		// TODO
		return null;
	}

	@Override
	public Map<String, String> getBDBBasedOnType(byte[] fileBytes, String type, String subType) throws Exception {
		BIRType bir = CbeffValidator.getBIRFromXML(fileBytes);
		Map<String, String> bdbMap = CbeffValidator.getBDBBasedOnTypeAndSubType(bir, type, subType);
		return bdbMap;
	}

	@Override
	public List<BIRType> getBIRDataFromXML(byte[] xmlBytes) throws Exception {
		BIRType bir = CbeffValidator.getBIRFromXML(xmlBytes);
		return bir.getBIR();
	}

}
