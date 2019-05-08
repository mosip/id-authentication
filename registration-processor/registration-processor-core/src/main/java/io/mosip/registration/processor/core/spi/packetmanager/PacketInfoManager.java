
package io.mosip.registration.processor.core.spi.packetmanager;

import java.util.List;

import io.mosip.registration.processor.core.code.DedupeSourceName;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisApplicationDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegBioRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegDemoDedupeListDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;

/**
 * The Interface PacketInfoManager.
 *
 * @author Horteppa (M1048399)
 * @param <T>
 *            PacketInfoDto
 * @param <A>
 *            the generic type
 */
public interface PacketInfoManager<T, /** D, M, */
		A> {


	/**
	 * Save demographic data.
	 *
	 * @param demographicJsonStream
	 *            the demographic json stream
	 * @param metaData
	 *            the meta data
	 */
	public void saveDemographicInfoJson(byte[] bytes,String regId, List<FieldValue> metaData);


	
	/**
	 * Gets the packetsfor QC user.
	 *
	 * @param qcUserId
	 *            the qc user id
	 * @return the packetsfor QC user
	 */
	public List<A> getPacketsforQCUser(String qcUserId);

	/**
	 * Find demo by id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the list
	 */
	public List<DemographicInfoDto> findDemoById(String regId);



	/**
	 * Gets the applicant registration id by UIN.
	 *
	 * @param uin the uin
	 * @return the registration id by UIN
	 */
	public List<String> getRegIdByUIN(String uin);

	

	/**
	 * Save manual adjudication data.
	 *
	 * @param uniqueMatchedRefIds
	 *            the unique matched ref ids
	 * @param registrationId
	 *            the registration id
	 */


	public void saveManualAdjudicationData(List<String> uniqueMatchedRefIds, String registrationId, DedupeSourceName sourceName);


	/**
	 * Save abis ref.
	 *
	 * @param regAbisRefDto
	 *            the reg abis ref dto
	 */
	public void saveAbisRef(RegAbisRefDto regAbisRefDto);

	/**
	 * Gets the reference id by rid.
	 *
	 * @param rid
	 *            the rid
	 * @return the reference id by rid
	 */
	public List<String> getReferenceIdByRid(String rid);

	/**
	 * Gets the rid by reference id.
	 *
	 * @param refId
	 *            the ref id
	 * @return the rid by reference id
	 */
	public List<String> getRidByReferenceId(String refId);

	/**
	 * Gets the UIN by rid.
	 *
	 * @param Rid the rid
	 * @return the UIN by rid
	 */
	public List<String> getUINByRid(String Rid);
	
	public List<AbisRequestDto> getInsertOrIdentifyRequest(String abisRefId,String requestType,String refRegtrnId);

	public Boolean getIdentifyByTransactionId(String transactionId);

	public List<RegBioRefDto> getBioRefIdByRegId(String regId);

	public List<AbisApplicationDto> getAllAbisDetails();

	public void saveBioRef(RegBioRefDto regBioRefDto);

	public void saveAbisRequest(AbisRequestDto abisRequestDto);

	public List<RegDemoDedupeListDto> getDemoListByTransactionId(String transactionId);

	public String getStatusOfPacketByRegId(String regId);

	public void saveDemoDedupePotentialData(RegDemoDedupeListDto regDemoDedupeListDto);
}