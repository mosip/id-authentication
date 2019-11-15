# Search Lost UIN or RID in registration-processor


**Background**

The resident service can be used to find out lost UIN or RID. The resident will login to resident service portal with OTP validation and search for lost UIN/RID.

**The target users are -**

- Resident service portal.
- System Integrator.
- Administrator.

**The key functional requirements are -**
-	Registration Processor receives a request from Resident Services to find a lost UIN/RID, with the required input parameters.
		- The input parameters received from Resident Services are:
			- ID Type to Find
			- Full Name of Resident
			- Pin Code of Resident
			- Contact Type
			- Contact Value
- Registration Processor initiates a search for RID based on a logic defined, refer Logic/Rule
	- If contact Type is Email then, search the database for RIDs having the same Full Name, PIN Code and Email ID.
	- If contact Type is Phone then, search the database for RIDs having the same Full Name, PIN Code and Email ID.
- Registration Processor finds UIN or RID as per request and returns it.
- If no RID is found then send an error message, stating that, “No Records Found“.
- If multiple RID is found, then validate if all rids belong to same uin. Send error message if not.
- If multiple UIN found then return erorr message.

**The key non-functional requirements are**
- 	Service Availability: Service should be available 24/7.
- 	Performance: 
		- The search operation in Registration Processor should happen in 50ms for 40 million records in DB.
		- The response should be sent back to Resident Services in 100ms.
- 	Security: The Request received by Registration Processor should be Authenticated.
- 	Scalability: Service should be scalable enough to handle peak hour requests.



**Solution**

The key solution considerations are -
**Add and Alter tables**:
- 	"individual_demographic_dedup" table : additional field Hash VALUE will be stored.
		- Add below new fields -
			- 	phone : store hashed value
			- email : store hashed value
			- postal code : store hashed value

**Configuration changes**:
- N/A

**API Specification:**
	API specifiaction can be found from [here](https://github.com/mosip/mosip-docs/wiki/Registration-Processor-APIs#11-lost-uin-or-rid-service) 

**Update "registration-processor-request-handler-service"**:
1. Create new Controller - "SearchRegistrationController". Follow api spec for request and response format. Call service to get the id value(RID/UIN).
2. Create SearchRegistrationService and provide implementation -
```
@Service
public interface SearchRegistrationService {

    public SearchRegistrationResponseDto searchRegistration(SearchRegistrationRequestDto requestDto);
}
```
3. From service implementation call new method io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager.searchRegistrationByDemographicInfo(). Below is the signature -
```
public List<DemographicInfoDto> searchRegistrationByDemographicInfo(DemographicInfoDto demographicInfoDto);
```
  - Add below 3 additional fields in "DemographicInfoDto"
	1. postalCode
	2. phone
	3. email

 - Provide implementation for searchRegistrationByDemographicInfo() and Call PacketInfoDao to get the list of records by name, postalCode, phone and email.
6. Now in service implementation, implement below conditions -
	1. IF (IDTYPE is RID) -
		1. Call PacketInfoManager.searchRegistrationByDemographicInfo() to get the list of rids.
		2. If no RID is found then throw exception with an error message, stating that, “No Records Found“. Handle exception in ExceptionHandler.
		3. If one RID is found, then send the RID in response.
		4. If multiple RID found with "PROCESSED" status then return first PROCESSED rid based on cr_dtimes in table.
		5. If multiple rid found with only "PROCESSING" status then return latest RID by cr_dtimes in table..
	2. IF (IDTYPE is UIN) -
		1. Use same above mentioned steps to find the "PROCESSED" rid.
		2. Additionally call already available  io.mosip.registration.processor.packet.manager.idreposervice.IdRepoService.getUinByRid() method to find the UIN.
		3. If none of the rid is in "PROCESSED" status then throw exception.



**Save additional - "postalCode", "phone" and "email" in demo-dedupe-stage**:
1. After successful dedupe, the demo-dedupe-stage calls PacketInfoManager.saveDemographicInfoJson() to save name dob and gender field in  "individual_demographic_dedup" table. Additionally applicant postalCode, phone and email need to be stored. The input has entire identity json.
2. Add 3 new fields in io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe.java. New file -
```
@Data
public class IndividualDemographicDedupe {
	/** The name. */
	private List<JsonValue[]> name;
	/** The date of birth. */
	private String dateOfBirth;
	/** The gender. */
	private JsonValue[] gender;
	private String postalCode;
	private String phone;
	private String email;
```
3. Add postalCode, phone and email in io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity.java as well.
4. In io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl.saveIndividualDemographicDedupe() method, entire Identity json will be converted to IndividualDemographicDedupe object by calling getIdentityKeysAndFetchValuesFromJSON() method. Map additional postalCode, phone and email also in getIdentityKeysAndFetchValuesFromJSON() method.
5. Now that additional fields are mapped, the information has to be saved in the table. The IndividualDemographicDedupe object will be converted to IndividualDemographicDedupeEntity object inside PacketInfoMapper.converDemographicDedupeDtoToEntity() method. Map additional 3 fields in same metod. The entity is getting saved in db hence additional fields will also be saved. No changes are required to save.


**Class Diagram**
![Packet receiver class diagram](_images/design/registration-processor/packet_receiver_class_diagram.png)

**Sequence Diagram**
![Packet receiver sequence diagram](_images/design/registration-processor/packet_receiver_seq_diagram.png)