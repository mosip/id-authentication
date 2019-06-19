package io.mosip.idrepository.core.spi;

import io.mosip.idrepository.core.exception.IdRepoAppException;

// TODO: Auto-generated Javadoc
/**
 * The Interface IdRepoService - service to provide functionality to create, 
 * retrieve and update Uin data in Id repository.
 *
 * @author Manoj SP
 * @param <REQUEST> the Request Object
 * @param <RESPONSE> the Response Object
 */
public interface IdRepoService<REQUEST, RESPONSE> {

	/**
	 * This service will create a new ID record in ID repository and store 
	 * corresponding demographic and bio-metric documents.
	 *
	 * @param request the request
	 * @param uin     uin
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE addIdentity(REQUEST request, String uin) throws IdRepoAppException;

	/**
	 * This service will retrieve an ID record from ID repository for a given UIN
	 * (Unique Identification Number) and identity type as bio/demo/all.
	 * 
	 * 1. When type=bio is selected, individualBiometrics along with Identity
	 * details of the Individual are returned 
	 * 2. When type=demo is selected,
	 * Demographic documents along with Identity details of the Individual are
	 * returned 
	 * 3. When type=all is selected, both individualBiometrics and
	 * demographic documents are returned along with Identity details of the
	 * Individual 
	 * 4. If no identity type is provided, stored Identity details of the
	 * Individual will be returned as a default response.
	 *
	 * @param uin the uin
	 * @param filter the filter
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE retrieveIdentityByUin(String uin, String filter) throws IdRepoAppException;
	
	/**
	 * This service will retrieve an ID record from ID repository for a given RID
	 * (Registration ID) and identity type as bio/demo/all.
	 * 
	 * 1. When type=bio is selected, individualBiometrics along with Identity
	 * details of the Individual are returned 
	 * 2. When type=demo is selected,
	 * Demographic documents along with Identity details of the Individual are
	 * returned 
	 * 3. When type=all is selected, both individualBiometrics and
	 * demographic documents are returned along with Identity details of the
	 * Individual 
	 * 4. If no identity type is provided, stored Identity details of the
	 * Individual will be returned as a default response.
	 * *
	 * @param rid the rid
	 * @param filter the filter
	 * @return the response
	 * @throws IdRepoAppException  the id repo app exception
	 */ 
	RESPONSE retrieveIdentityByRid(String rid, String filter) throws IdRepoAppException;

	/**
	 * This operation will update an existing ID record in the ID repository for a 
	 * given UIN (Unique Identification Number).
	 *
	 * @param request the request
	 * @param uin     uin
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE updateIdentity(REQUEST request, String uin) throws IdRepoAppException;
}
