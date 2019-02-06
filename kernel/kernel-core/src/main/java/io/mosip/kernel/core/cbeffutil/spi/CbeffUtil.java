/**
 * 
 */
package io.mosip.kernel.core.cbeffutil.spi;

import java.util.List;
import java.util.Map;

import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;

/**
 * @author Ramadurai Pandian
 * 
 * Interface for Cbeff Interface
 *
 */
public interface CbeffUtil {

	public byte[] createXML(List<BIR> cbeffPack) throws Exception;
	
	public byte[] updateXML(List<BIR> cbeffPackList, byte[] fileBytes) throws Exception;

	public boolean validateXML(byte[] xmlBytes, byte[] xsdBytes) throws Exception;
	
	public Map<String,String> getBDBBasedOnType(byte[] fileBytes,String type,String subType)  throws Exception;
	
	public List<BIRType> getBIRDataFromXML(byte[] xmlBytes) throws Exception;
	
	public Map<String, String> getAllBDBData(byte[] xmlBytes, String type, String subType) throws Exception;
}
