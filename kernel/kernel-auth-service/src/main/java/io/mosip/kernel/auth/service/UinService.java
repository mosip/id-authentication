/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.dto.MosipUserDto;

/**
 * @author Ramadurai Pandian
 *
 */
public interface UinService {

	MosipUserDto getDetailsFromUin(String uin) throws Exception;

}
