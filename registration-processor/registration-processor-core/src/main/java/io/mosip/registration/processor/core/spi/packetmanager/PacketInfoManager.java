package io.mosip.registration.processor.core.spi.packetmanager;

import java.util.List;

import org.apache.ignite.internal.util.typedef.internal.A;
import org.springframework.stereotype.Service;


/**
 * The Interface PacketInfoManager.
 *
 * @author Horteppa (M1048399)
 * @param <T> PacketInfoDto
 * @param <D> DemographicInfo
 */
@Service
public interface PacketInfoManager<T, D, M, A> {


	/**
	 * Save packet data.
	 *
	 * @param packetInfo the packet info
	 */
	public void savePacketData(T packetInfo);


	
	/**
	 * Save demographic data.
	 *
	 * @param demograficData the demografic data
	 */
	public void saveDemographicData(D demograficData,M metaData);
	
	/**
	 * Gets the packetsfor QC user.
	 *
	 * @param qcUserId the qc user id
	 * @return the packetsfor QC user
	 */
	public List<A> getPacketsforQCUser(String qcUserId);
}