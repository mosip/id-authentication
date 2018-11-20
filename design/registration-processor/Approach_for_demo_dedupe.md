# Approach for Demo Dedupe

**Background**

After successful OSI validation, the demo dedupe will be performed on Resident demographic information to find out if there are potential duplicate present.

The target users are -

Server application which will process the packets.
Administrator of the platform who may need to verify the packets.

The key requirements are -
-	Find potential demo dedupe records matching GENDER and DOB.
-	Perform demo dedupe on all potential 'demo dedupe records' with 'Resident demographic information' using levenshtein distance algorithm.
-	Perform authentication using auth service on resident biometric details and list of potential demo dedupes. For ex - for resident p there are 2 potential dedupe p' and p''. Call auth service to authenticate user by passing ref ids of p' and p'' and biometrics of p. If same person has enrolled before then auth service will identify p .
- Fail demo dedupe if auth service identifies the resident.
- Save the potential duplicates in Manual adjudication table when auth service doesnot identify the resident.
-	Route request to next vertical when there is no potential duplicates found for resident.

The key non-functional requirements are
-	Performance: Should be able to perform demo dedupe on millions of potential duplicate records per second.

**Solution**

The key solution considerations are -
- Create vertical "Demo-dedupe" to to perform dedupe check on resident demographic information.
- Add 3 new functionalities in packet-info-storage-service module -
	1. Get the applicant demographic information by registration id. There will be max 2 records by language -
		1) local language record.
		2) user language record.
	2. Step 1 will return applicant demographic information. Now get all potential duplicate by resident 'gender' and 'dob' when the DOB is verified. NOTE : there will be a flag in demo dedupe table which will indicate if the dob is verified or not.
	3. There can be scenarios where resident doesnot have date of birth proof. In that case system will backtrack date based on age. For example - in 2018 if resident with age 18 doesnot have date of birth proof then system will assign dob as 01/01/2000. In these scenarios there will be a flag "dob verified" with true/false values in applicant demographic table. Get all the **unverified** dob records with 'resident dob year + 1' and 'resident dob year - 1' to perform demo dedupe. For example -
	```
	if ((resident dob is not verified) && resident dob is 01/01/2000) {
	// find all unverified records with DOB year -> '2000 + 1' and '2000 - 1'
	getPotentialDuplicatesWithUnverifiedDob(Gender, starting dob range -> 1999, ending dob range -> 2001);
	}
```
- Now the list of potential duplicate list are present to perform demo-dupe. Iterate the list and for each record and do the following -
	1. perform dedupe in local language.
	2. perform dedupe on local language.
	3. Calculate average score. (local language score + user language score) / 2
	4. If only one language is present then consider that as final score.
	5. check if it exceeds the threashold defined. By default the threashold is 80% which is configurable.
-  Registration-processor will use levenshtein distance algorithm and perform demo dedupe on name and address. There is an weight associated with every field. Read the weight from config server -
```
registration.processor.demo.dedupe.name.weight=50
registration.processor.demo.dedupe.addressline1.weight=5
registration.processor.demo.dedupe.addressline2.weight=5
registration.processor.demo.dedupe.addressline3.weight=5
registration.processor.demo.dedupe.addressline4.weight=5
registration.processor.demo.dedupe.addressline5.weight=5
registration.processor.demo.dedupe.addressline6.weight=5
registration.processor.demo.dedupe.pincode.weight=10
registration.processor.demo.dedupe.overall.weight=90
```
- Now the 
- Call 'packet-store-adapter-ceph' service to get the biometric for operator/supervisor/introducer from inside the packet. 
- The [Auth-rest-service](https://github.com/mosip/mosip/blob/DEV/design/authentication/Auth_Request_REST_service.md) will be responsible for validating OSI info and the service accepts encoded biometrics. The encoded image will be present inside the packet by default.
- Check if the following information is present inside packet. If available then verify information against user type. A country may choose not to send certain information(ex - fingerprint/iris etc). Hence if one type of information is not present inside packet then move to next check.

```
```
