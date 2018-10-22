

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
- Create a vertical to validate user, machine and center details.
- On successful response from "packet-validator" vertical send request to user-machine-center-validator for next validation.
