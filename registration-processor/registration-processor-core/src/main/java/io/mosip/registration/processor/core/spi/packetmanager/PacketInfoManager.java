
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
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;

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
	 * Gets the insert or identify request.
	 *
	 * @param bioRefId
	 *            the abis ref id
	 * @param refRegtrnId
	 *            the ref regtrn id
	 * @return the insert or identify request
	 */
	public List<AbisRequestDto> getInsertOrIdentifyRequest(String bioRefId, String refRegtrnId);

	/**
	 * Gets the abis transaction id by request id.
	 *
	 * @param requestId
	 *            the request id
	 * @return the abis transaction id by request id
	 */
	public List<String> getAbisTransactionIdByRequestId(String requestId);

	/**
	 * Gets the identify req list by transaction id.
	 *
	 * @param transactionId the transaction id
	 * @param requestType the request type
	 * @return the identify req list by transaction id
	 */
	public List<AbisRequestDto> getIdentifyReqListByTransactionId(String transactionId, String requestType);

	/**
	 * Gets the abis request by request id.
	 *
	 * @param abisRequestId
	 *            the abis request id
	 * @return the abis request by request id
	 */
	public AbisRequestDto getAbisRequestByRequestId(String abisRequestId);

	/**
	 * Gets the batch id by request id.
	 *
	 * @param abisRequestId
	 *            the abis request id
	 * @return the batch id by request id
	 */
	public String getBatchIdByRequestId(String abisRequestId);

	/**
	 * Gets the batch statusby batch id.
	 *
	 * @param batchId
	 *            the batch id
	 * @return the batch statusby batch id
	 */
	public List<String> getBatchStatusbyBatchId(String batchId);

	/**
	 * Gets the reference id by batch id.
	 *
	 * @param batchId the batch id
	 * @return the reference id by batch id
	 */
	public List<String> getReferenceIdByBatchId(String batchId);

	/**
	 * Gets the insert or identify request.
	 *
	 * @param bioRefId
	 *            the bio ref id
	 * @param refRegtrnId
	 *            the ref regtrn id
	 * @param requestType
	 *            the request type
	 * @return the insert or identify request
	 */
	public List<AbisRequestDto> getInsertOrIdentifyRequest(String bioRefId, String refRegtrnId, String requestType);

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

	/**
	 * Save demo dedupe potential data.
	 *
	 * @param regDemoDedupeListDto
	 *            the reg demo dedupe list dto
	 */
	public void saveDemoDedupePotentialData(RegDemoDedupeListDto regDemoDedupeListDto);

	/**
	 * Gets the abis response records.
	 *
	 * @param latestTransactionId
	 *            the latest transaction id
	 * @param identify
	 *            the identify
	 * @return the abis response records
	 */
	public List<AbisResponseDto> getAbisResponseRecords(String latestTransactionId, String identify);

	/**
	 * Gets the abis response records.
	 *
	 * @param abisRefId
	 *            the abis ref id
	 * @param latestTransactionId
	 *            the latest transaction id
	 * @param identify
	 *            the identify
	 * @return the abis response records
	 */
	public List<AbisResponseDto> getAbisResponseRecords(String abisRefId, String latestTransactionId, String identify);

	/**
	 * Gets the abis response I ds.
	 *
	 * @param abisRequestId
	 *            the abis request id
	 * @return the abis response I ds
	 */
	public List<AbisResponseDto> getAbisResponseIDs(String abisRequestId);

	/**
	 * Gets the abis response det records.
	 *
	 * @param abisResponseDto
	 *            the abis response dto
	 * @return the abis response det records
	 */
	public List<AbisResponseDetDto> getAbisResponseDetRecords(AbisResponseDto abisResponseDto);

	/**
	 * Gets the abis response det records.
	 *
	 * @param List
	 *            abisResponseDto the abis response dto
	 * @return the abis response det records
	 */
	public List<AbisResponseDetDto> getAbisResponseDetRecordsList(List<String> abisResponseDto);

	/**
	 * Gets the abis response details.
	 *
	 * @param abisResponseId
	 *            the abis response id
	 * @return the abis response details
	 */
	public List<AbisResponseDetDto> getAbisResponseDetails(String abisResponseId);

	/**
	 * Save individual demographic dedupe update packet.
	 *
	 * @param demoDedupeData
	 *            the demo dedupe data
	 * @param regId
	 *            the reg id
	 */
	public void saveIndividualDemographicDedupeUpdatePacket(IndividualDemographicDedupe demoDedupeData, String regId);

	/**
	 * Gets the identity keys and fetch values from JSON.
	 *
	 * @param demographicJsonString
	 *            the demographic json string
	 * @return the identity keys and fetch values from JSON
	 */
	public IndividualDemographicDedupe getIdentityKeysAndFetchValuesFromJSON(String demographicJsonString);

	/**
	 * Gets the abis requests by bio ref id.
	 *
	 * @param bioRefId
	 *            the bio ref id
	 * @return the abis requests by bio ref id
	 */
	public List<AbisRequestDto> getAbisRequestsByBioRefId(String bioRefId);
	
	/**
	 * Gets the abis processed requests app code by bio ref id.
	 *
	 * @param bioRefId the bio ref id
	 * @param requestType the request type
	 * @param processed the processed
	 * @return the abis processed requests app code by bio ref id
	 */
	public List<String> getAbisProcessedRequestsAppCodeByBioRefId(String bioRefId,String requestType,String processed);
}