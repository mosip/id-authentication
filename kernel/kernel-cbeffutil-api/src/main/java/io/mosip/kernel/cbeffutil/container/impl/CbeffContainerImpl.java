/**
 * 
 */
package io.mosip.kernel.cbeffutil.container.impl;

import java.util.ArrayList;
import java.util.List;

import io.mosip.kernel.cbeffutil.common.CbeffValidator;
import io.mosip.kernel.cbeffutil.common.CbeffXSDValidator;
import io.mosip.kernel.cbeffutil.container.CbeffContainerI;
import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRInfoType;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.jaxbclasses.VersionType;

/**
 * @author Ramadurai Pandian
 * 
 * A Container Class where the BIR is created and updated
 *
 */
public class CbeffContainerImpl extends CbeffContainerI<BIR, BIRType> {

	private BIRType birType;

	/**
	 * Method where the initialization of BIR happens
	 * 
	 * @param birList List of BIR data
	 * @return BIRType data with all images
	 */
	@Override
	public BIRType createBIRType(List<BIR> birList) {
		load();
		List<BIRType> birTypeList = new ArrayList<>();
		if (birList != null && birList.size() > 0) {
			for (BIR bir : birList) {
				birTypeList.add(bir.toBIRType(bir));
			}
		}
		birType.setBir(birTypeList);
		return birType;
	}

	private void load() {
		// Creating first version of Cbeff
		birType = new BIRType();
		// Initial Version
		VersionType versionType = new VersionType();
		versionType.setMajor(1);
		versionType.setMinor(1);
		VersionType cbeffVersion = new VersionType();
		cbeffVersion.setMajor(1);
		cbeffVersion.setMinor(1);
		birType.setVersion(versionType);
		birType.setCBEFFVersion(cbeffVersion);
		BIRInfoType birInfo = new BIRInfoType();
		birInfo.setIntegrity(false);
		birType.setBIRInfo(birInfo);
	}

	/**
	 * Method to the update of BIR
	 * 
	 * @param birList List of BIR data
	 * 
	 * @param fileBytes Cbeff XML data as bytes
	 * 
	 * @return birType BIR data with all images
	 */
	@Override
	public BIRType updateBIRType(List<BIR> birList, byte[] fileBytes) throws Exception {
		BIRType birType = CbeffValidator.getBIRFromXML(fileBytes);
		birType.getVersion().setMajor(birType.getVersion().getMajor() + 1);
		birType.getCBEFFVersion().setMajor(birType.getCBEFFVersion().getMajor());
		for (BIR bir : birList) {
			birType.getBIR().add(bir.toBIRType(bir));
		}
		return birType;
	}

	/**
	 * Method to the validate the BIR
	 * 
	 * @param xmlBytes Cbeff XML data as bytes array
	 * 
	 * @param xsdBytes Cbeff XSD data as bytes array
	 * 
	 * @return boolean of validated XML against XSD
	 */
	@Override
	public boolean validateXML(byte[] xmlBytes, byte[] xsdBytes) throws Exception {
		return CbeffXSDValidator.validateXML(xsdBytes, xmlBytes);
	}

}
