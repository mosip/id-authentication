# ID-Authentication Monolothic Architecture

This involves below changes in ID-Authentication Module:
1. Store Individual's data for UIN and VID in IDA DB and use it for authentication.
2. Convert REST calls made to KeyManager service to a Java API call.
3. Cache master data used by ID-Authentication modules such as SMS and  Email Templates, Genders, Titles.
4. Cache partner details and policy and based on them perform MISP-Partner validation


## 1. Store Individual's data for UIN and VID in IDA DB and use it for authentication
This involes following steps:
1. ID-Repository module will notify IDA using a event notification API exposed in IDA's Internal Auth Module. The events are below:
  i. Create UIN
  ii. Update UIN
  iii. Create VID
  iv. Update VID
2. ID-Authentication's event notification handler service will connect to ID-Repository to fetch the Individual's record for the UIN/VID and will store in IDA DB. The Individual's data stored in IDA DB are as below:
  i. UIN Hash / VID Hash
  ii. Individual's encrypted demographic data
  iii. Individual's encrypted biometric data
  iv. Expiry time or the UIN/VID as applicable for its status.
  v. VID's transaction limit based on its policy.
3. ID-Authentication will use the data stored in IDA DB for any authentication request.

## 2. Convert REST calls made to KeyManager service to a Java API call
This involves following steps:
1. Build keymanager as Java library to use in IDA
2. Store master keys in a softhms and provide access to that in IDA.
3. Store the keys for different key aliases in IDA DB.
4. Create encryption and get-public key services in IDA's internal auth module that uses the keys stored in IDA, which will be used by the authentication service users to create authentication request.
5. For any encryption and decrytion of data these local keys will used by IDA via the keymanager Java library.


