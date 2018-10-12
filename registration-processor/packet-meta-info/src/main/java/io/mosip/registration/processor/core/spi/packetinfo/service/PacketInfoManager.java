package io.mosip.registration.processor.core.spi.packetinfo.service;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.packet.dto.DocumentDetail;

/**
 * @author Horteppa (M1048399)
 *
 * @param <T>
 *            PacketInfoDto
 */
@Service
public interface PacketInfoManager<T, B, D> {

	/**
	 * Save packet info.
	 *
	 * @param packetInfo
	 *            the packet info
	 * @return true, if successful
	 */
	public void savePacketData(T packetInfo) throws Exception;

	/**
	 * Save bio metric info.
	 *
	 * @param bioMetricData
	 *            the bio metric data
	 * @return true, if successful
	 */
	public void saveBioMetricData(B bioMetricData)throws Exception;

	/**
	 * Save demografic data.
	 *
	 * @param demograficData
	 *            the demografic data
	 * @return true, if successful
	 */
	public void saveDemographicData(D demograficData) throws Exception;
}