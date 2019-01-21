# License key generation and validation

#### Background

TSPs call the IDA to authenticate the Individuals. There can be various service calls such as Demographic, biometric based authentications. Each service calls have the permission associated. When a service call comes to the IDA, a request is sent to the Kernel module to retrieve the permissions for the License Key.

#### Solution



**The key solution considerations are**

- A service is defined to receive the request to generate a new license key for a TSP. 

- Another service associates the license key with the list of permissions. 

- Another service accepts the incoming requests from the TSPs should be validated for authentication. Check whether the TSPs has the valid license.  

- Then the permissions are retrieved for the identified TSPs. 

- Return the permissions associated with the License keys.  

**Module diagram**



![Module Diagram](_images/LicenseKeyGeneration.jpg)



## Implementation


**kernel-LicenseKeyGeneration** [README](../../kernel/LicenseKeyGeneration/README.md)
