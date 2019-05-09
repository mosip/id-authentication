
package io.mosip.registration.processor.core.spi.packetmanager;

import java.util.List;

import io.mosip.registration.processor.core.code.DedupeSourceName;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisApplicationDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
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
	 * @param bytes
	 *            the bytes
	 * @param regId
	 *            the reg id
	 * @param metaData
	 *            the meta data
	 */
	public void saveDemographicInfoJson(byte[] bytes, String regId, List<FieldValue> metaData);

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
	 * @param uin
	 *            the uin
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
	 * @param sourceName
	 *            the source name
	 */

	public void saveManualAdjudicationData(List<String> uniqueMatchedRefIds, String registrationId,
			DedupeSourceName sourceName);

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
	 * @param rid
	 *            the rid
	 * @return the UIN by rid
	 */
	public List<String> getUINByRid(String rid);

	/**
	 * Gets the insert or identify request.
	 *
	 * @param bioRefId
	 *            the abis ref id
	 * @param requestType
	 *            the request type
	 * @return the insert or identify request
	 */
	public List<AbisRequestDto> getInsertOrIdentifyRequest(String bioRefId, String refRegtrnId);

	/**
	 * Gets the identify by transaction id.
	 *
	 * @param transactionId
	 *            the transaction id
	 * @param identify
	 *            the identify
	 * @return the identify by transaction id
	 */
	public Boolean getIdentifyByTransactionId(String transactionId, String identify);

	/**
	 * Gets the bio ref id by reg id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the bio ref id by reg id
	 */
	public List<RegBioRefDto> getBioRefIdByRegId(String regId);

	/**
	 * Gets the all abis details.
	 *
	 * @return the all abis details
	 */
	public List<AbisApplicationDto> getAllAbisDetails();

	/**
	 * Save bio ref.
	 *
	 * @param regBioRefDto
	 *            the reg bio ref dto
	 */
	public void saveBioRef(RegBioRefDto regBioRefDto);

	/**
	 * Save abis request.
	 *
	 * @param abisRequestDto
	 *            the abis request dto
	 */
	public void saveAbisRequest(AbisRequestDto abisRequestDto);

	/**
	 * Gets the demo list by transaction id.
	 *
	 * @param transactionId
	 *            the transaction id
	 * @return the demo list by transaction id
	 */
	public List<RegDemoDedupeListDto> getDemoListByTransactionId(String transactionId);

	public void saveDemoDedupePotentialData(RegDemoDedupeListDto regDemoDedupeListDto);

	public List<AbisResponseDto> getAbisResponseRecords(String latestTransactionId, String identify);
	
	public List<AbisResponseDetDto> getAbisResponseDetRecords(AbisResponseDto abisResponseDto);
}