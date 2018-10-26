# Approach for OPERATOR, SUPERVISOR and INTRODUCER validator

**Background**

After successful packet structure validation, the packet packet meta info is stored in DB. The operator, supervisor and introducer biometric/password/pin will be further validated to check if the packet is created by authorized person.

The target users are -

Server application which will process the packets.
Administrator of the platform who may need to verify the packets.

The key requirements are -
-	Validate operator biometric.
-	Validate supervisor biometric.
-	Validate introducer biometric.
- Validate operator pin/password.
- Validate supervisor pin/password.
-	Send response to eventbus on successful/failed validation.

The key non-functional requirements are
-	Performance: Should be able to support processing multiple requests per second.

**Solution**

The key solution considerations are -
- Create vertical "OSI-validator" to validate operator, supervisor and introducer biometric authentication.
- In camel bridge after successful packet validation the request will be routed to OSI-validator by default. Create router and request processor to map the request to osi_bus address.
- Add new methods in PacketInfoManager to fetch the operator, supervisor and introducer basic details from table.
- The auth module will provide rest API to validate OSI biometrics and pin. 
    ```Input -> 1. UIN (the UIN of Operator/supervisor/introducer).
                2. biometric as byte array.
       Output -> json with status as TRUE or FALSE.
                 TRUE : valid individual.
                 FALSE : invalid individual.
    ```

