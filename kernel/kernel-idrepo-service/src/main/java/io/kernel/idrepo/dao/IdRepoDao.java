package io.kernel.idrepo.dao;

import io.kernel.idrepo.entity.Uin;
import io.kernel.idrepo.exception.IdRepoAppException;

/**
 * The Interface IdRepoDao.
 *
 * @author Manoj SP
 */
public interface IdRepoDao {

	/**
	 * Adds the identity.
	 *
	 * @param uin the uin
	 * @param uinRefId the uin ref id
	 * @param identityInfo the identity info
	 * @return the uin
	 * @throws IdRepoAppException the id repo app exception
	 */
	Uin addIdentity(String uin, String uinRefId, byte[] identityInfo) throws IdRepoAppException;

	/**
	 * Retrieve identity.
	 *
	 * @param uin the uin
	 * @return the uin
	 * @throws IdRepoAppException the id repo app exception
	 */
	Uin retrieveIdentity(String uin) throws IdRepoAppException;

	/**
	 * Update idenity info.
	 *
	 * @param uin the uin
	 * @param identityInfo the identity info
	 * @return the uin
	 * @throws IdRepoAppException the id repo app exception
	 */
	Uin updateIdenityInfo(String uin, byte[] identityInfo) throws IdRepoAppException;

	/**
	 * Update uin status.
	 *
	 * @param uin the uin
	 * @param statusCode the status code
	 * @return the uin
	 * @throws IdRepoAppException the id repo app exception
	 */
	Uin updateUinStatus(String uin, String statusCode) throws IdRepoAppException;
}
