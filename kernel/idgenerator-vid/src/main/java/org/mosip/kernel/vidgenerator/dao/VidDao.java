package org.mosip.kernel.vidgenerator.dao;

import org.mosip.kernel.vidgenerator.model.VId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for VidGenerator
 * 
 * @author M1043226
 * @since 1.0.0
 *
 */
@Repository
public interface VidDao extends JpaRepository<VId, String> {

}
