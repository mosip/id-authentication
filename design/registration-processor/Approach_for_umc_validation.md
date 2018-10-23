

# Approach for USER, MACHINE and CENTER validator

**Background**

After successful packet structure validation, the packet packet meta info is stored in DB. The operator, supervisor and introducer information will be further validated to check if the packet is created by authorized person.

The target users are -

Server application which will process the packets.
Administrator of the platform who may need to verify the packets.

The key requirements are -
-	Validate user information.
-	Validate machine information.
-	Validate center information.
-	Validate if the user was assigned to the particular machine of same center during creation of the packet.
-	Send response to eventbus on successful/failed validation.

The key non-functional requirements are
-	Performance: Should be able to support processing multiple requests per second.


**Solution**

The key solution considerations are -
- Create vertical "user-machine-center-validator" to validate user, machine and center details.
- On successful packet structure validation, send request to user-machine-center-validator .
- Create UmcValidationProcessor in camel-bridge and route all successful packet structure validation request to umc_bus address. Map the request between vert.x and camel endpoints.
- Add new methods in PacketInfoManager to fetch the user, machine and center details from table.
- Use apache rest client to call [Master-data-APIs](https://github.com/mosip/mosip/wiki/2.4-Master-data-APIs#234-document-formats-master-api). Input will be - {userId + packet creation date}, {centerId + packet creation date}, {machineId + packet creation date}. The api will return the record on or before packet creation date. Registration processor would check if user/center/machine was valid during creation of the packet. 
