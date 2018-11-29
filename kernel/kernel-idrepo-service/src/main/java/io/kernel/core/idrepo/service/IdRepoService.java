package io.kernel.core.idrepo.service;

import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.kernel.core.idrepo.dto.IdResponseDTO;
import io.kernel.core.idrepo.entity.Uin;
import io.kernel.core.idrepo.exception.IdRepoAppException;

/**
 * The Interface IdRepoService.
 *
 * @author Manoj SP
 */
public interface IdRepoService {

	/**
	 * Adds the identity.
	 *
	 * @param request the request
	 * @return the id response DTO
	 * @throws IdRepoAppException the id repo app exception
	 */
	IdResponseDTO addIdentity(IdRequestDTO request) throws IdRepoAppException;

	/**
	 * Retrieve identity.
	 *
	 * @param uin the uin
	 * @return the id response DTO
	 * @throws IdRepoAppException the id repo app exception
	 */
	IdResponseDTO retrieveIdentity(String uin) throws IdRepoAppException;

	/**
	 * Update identity.
	 *
	 * @param request the request
	 * @return the id response DTO
	 * @throws IdRepoAppException the id repo app exception
	 */
	IdResponseDTO updateIdentity(IdRequestDTO request) throws IdRepoAppException;

	/**
	 * Generate UIN.
	 *
	 * @return the string
	 * @throws IdRepoAppException the id repo app exception
	 */
	String generateUIN() throws IdRepoAppException;

	/**
	 * Check UIN.
	 *
	 * @param uin the uin
	 * @return the boolean
	 * @throws IdRepoAppException the id repo app exception
	 */
	Boolean checkUIN(String uin) throws IdRepoAppException;

	/**
	 * Construct id response.
	 *
	 * @param id the id
	 * @param uin the uin
	 * @return the id response DTO
	 * @throws IdRepoAppException the id repo app exception
	 */
	IdResponseDTO constructIdResponse(String id, Uin uin) throws IdRepoAppException;
}
