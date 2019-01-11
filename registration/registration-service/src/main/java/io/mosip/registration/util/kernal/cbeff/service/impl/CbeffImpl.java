/**
 * 
 */
package io.mosip.registration.util.kernal.cbeff.service.impl;

import java.util.List;

import io.mosip.registration.dto.cbeff.BIR;
import io.mosip.registration.dto.cbeff.jaxbclasses.BIRType;
import io.mosip.registration.dto.cbeff.jaxbclasses.SingleAnySubtypeType;
import io.mosip.registration.dto.cbeff.jaxbclasses.SingleType;
import io.mosip.registration.util.kernal.cbeff.common.CbeffValidator;
import io.mosip.registration.util.kernal.cbeff.container.impl.CbeffContainerImpl;
import io.mosip.registration.util.kernal.cbeff.service.CbeffI;

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
	public List<String> getBDBBasedOnType(byte[] fileBytes, SingleType singleType) throws Exception {
		BIRType bir = CbeffValidator.getBIRFromXML(fileBytes);
		List<String> BDBList = CbeffValidator.getBDBListFromType(singleType, bir);
		return BDBList;
	}

	@Override
	public List<String> getBDBBasedOnSubType(byte[] fileBytes, SingleAnySubtypeType singleAnySubType) throws Exception {
		BIRType bir = CbeffValidator.getBIRFromXML(fileBytes);
		List<String> subTypeList = CbeffValidator.getBDBListFromSubType(singleAnySubType, bir);
		return subTypeList;
	}

	@Override
	public boolean validateXML(byte[] xmlBytes, byte[] xsdBytes) throws Exception {
		CbeffContainerImpl cbeffContainer = new CbeffContainerImpl();
		return cbeffContainer.validateXML(xmlBytes, xsdBytes);
	}

}
