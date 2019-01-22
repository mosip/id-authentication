/**
 * 
 */
package io.mosip.kernel.cbeffutil.service;

import java.util.List;
import java.util.Map;

import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;

/**
 * @author Ramadurai Pandian
 * 
 *
 */
public interface CbeffI {

	public byte[] createXML(List<BIR> cbeffPack) throws Exception;
	
	public byte[] updateXML(List<BIR> cbeffPackList, byte[] fileBytes) throws Exception;

	public boolean validateXML(byte[] xmlBytes, byte[] xsdBytes) throws Exception;
	
	public String getTestElementDetails(byte[] xmlBytes,byte[] xsdBytes) throws Exception;
	
	public Map<String,String> getBDBBasedOnType(byte[] fileBytes,String type,String subType)  throws Exception;
	
	public List<BIRType> getBIRDataFromXML(byte[] xmlBytes) throws Exception;
}
