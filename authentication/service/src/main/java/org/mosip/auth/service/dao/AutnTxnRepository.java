package org.mosip.auth.service.dao;

import org.mosip.auth.service.entity.AutnTxn;
import org.mosip.kernel.core.dao.repository.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author Rakesh Roshan
 */
@Repository
public interface AutnTxnRepository extends BaseRepository<AutnTxn, Integer> {

	public AutnTxn findByRequestTxnIdAndUin(String TxnId, String UIN);

}
