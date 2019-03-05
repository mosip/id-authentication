# Approach for activate and deactivate UIN

**Background**

Once the UIN is generated it can be deactivated or reactivated in registration-processor.

The target users are -

The admin applications which will trigger activate and deactivate request.
System administrator who will integrate with registration-processor.

The key requirements are -
- Authenticate the source to accept request for activate or deactivate uin.
- Deactivate UIN in registration-processor.
- Reactivate UIN in registration-processor.
- Route the request to applicable stages based on registrationType ['ACTIVATED', 'DEACTIVATED'].

The key non-functional requirements are
- .
- Performance - Should process thousands of packets per second.

**Solution**

The key solution considerations are -
- Only users with admin roles will be able to trigger deactivate or reactivate request. The 'sync' and 'packet-receiver' APIs should validate the request and allow uploading packets based on user role.
- Each stage in registration-processor communicates with camel-bridge and the bridge reads the camel route configuration to redirect request to next stage. The information is passed from one stage using MessageDto and camel takes decision based on same information. Add one new attribute in MessageDto.
		public class MessageDTO implements Serializable {
			private String rid;
			private Boolean isValid;
			private Boolean internalError;
			private MessageBusAddress messageBusAddress;
			private Integer retryCount;
			// add new attribute registrationType
			private String registrationType;
		}
- The deactivate or reactivate packet will be uploaded using packet-receiver-stage and it will move to virus-scan-stage and packet-uploader-stage to upload the packet to packet store. The camel-bridge will route the request to the next stage based on registrationType. There is no change in camel configuration till uploader-stage. If the request is valid and registrationType contains 'ACTIVATED' or 'DEACTIVATED' then the packet will  move from packet-validator-stage to uin-generator-stage.
