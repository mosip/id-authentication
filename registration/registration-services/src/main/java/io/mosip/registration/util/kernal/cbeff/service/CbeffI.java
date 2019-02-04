/**
 * 
 */
package io.mosip.registration.util.kernal.cbeff.service;

import java.util.List;

import io.mosip.registration.dto.cbeff.BIR;
import io.mosip.registration.dto.cbeff.jaxbclasses.SingleAnySubtypeType;
import io.mosip.registration.dto.cbeff.jaxbclasses.SingleType;

/**
 * @author Ramadurai Pandian
 *
 */
public interface CbeffI {

	public byte[] createXML(List<BIR> cbeffPack) throws Exception;

	public byte[] updateXML(List<BIR> cbeffPackList, byte[] fileBytes) throws Exception;

	public List<String> getBDBBasedOnType(byte[] fileBytes, SingleType singleType) throws Exception;

	public List<String> getBDBBasedOnSubType(byte[] fileBytes, SingleAnySubtypeType singleAnySubtypeType)
			throws Exception;

	public boolean validateXML(byte[] xmlBytes, byte[] xsdBytes) throws Exception;
}
