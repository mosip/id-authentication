package io.mosip.registration.processor.core.spi.packetmanager;

import java.io.InputStream;
import java.util.List;

import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;


/**
 * The Interface PacketInfoManager.
 *
 * @author Horteppa (M1048399)
 * @param <T> PacketInfoDto
 * @param <D> DemographicInfo
 */
public interface PacketInfoManager<T,/** D, M,*/ A> {


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
	public void saveDemographicInfoJson(InputStream demographicJsonStream,List<FieldValue> metaData);
	
	/**
	 * Gets the packetsfor QC user.
	 *
	 * @param qcUserId the qc user id
	 * @return the packetsfor QC user
	 */
	public RegOsiDto getOsi(String regid);
	public List<A> getPacketsforQCUser(String qcUserId);
}