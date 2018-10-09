package io.mosip.registration.processor.core.spi.packetinfo.service;
/**
 * @author Horteppa (M1048399)
 *
 * @param <T>
 *            PacketInfoDto
 */
public interface PacketInfoManager<T,B,D> {

	/**
	 * Save packet info.
	 *
	 * @param packetInfo the packet info
	 * @return true, if successful
	 */
	public boolean savePacketInfo(T packetInfo);
	
	/**
	 * Save bio metric info.
	 *
	 * @param bioMetricData the bio metric data
	 * @return true, if successful
	 */
	public boolean saveBioMetricInfo(B bioMetricData);
	
	/**
	 * Save demografic data.
	 *
	 * @param demograficData the demografic data
	 * @return true, if successful
	 */
	public boolean saveDemograficData(D demograficData);
}