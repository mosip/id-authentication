/**
 * 
 */
package io.mosip.registration.util.kernal.cbeff.container;

import java.util.List;

/**
 * @author Ramadurai Pandian
 *
 */
public abstract class CbeffContainerI<T, U> {

	public abstract U createBIRType(List<T> bir) throws Exception;

	public abstract U updateBIRType(List<T> bir, byte[] fileBytes) throws Exception;

	public abstract boolean validateXML(byte[] fileBytes, byte[] xsdBytes) throws Exception;

}
