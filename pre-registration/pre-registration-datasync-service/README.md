# Pre-Registration-datasync-service:

[Background & Design](pre-registration-individual.md)

This service enables Pre-Registration to a registration client , request to retrieve all pre-registration ids based on registration client id, appointment date and a user type.

#### Api Documentation

```
mvn javadoc:javadoc

```
#### POST Operation
#### Path -  `/sync`
#### Summary


This is used by registration client to retrieve all the pre-registration Ids by date range and registration center Id from the authorize token.

**The inputs which have to be provided are:**

1. id
2. version	
3. requestTime	
4. request.from-date	
5. request.to-date	

#### Response
Return transactionId, countOfPreRegIds, preRegistrationIds if request is successful, otherwise get error message.


#### POST Operation
#### Path -  `sync/consumedPreRegIds`
#### Summary

This is used by registration processor to fetch all processed pre-registration ids and store in 
pre-registration database.

#### Request body Parameters

1. id
2. version
3. requestTime
4. request
5. request.preRegistrationIds

#### Response
Returns transactionId, countOfPreRegIds, preRegistrationIds as response if request is successful, else gets error message.

#### GET Operation
#### Path -  `sync/:preRegistrationId`
#### Summary

This request is used by registration client to retrieve particular pre-registration data based on a pre-registration id.

#### Request path Parameters
1. preRegistrationId

#### Response

This request returns registration-client-id, appointment-date, from-time-slot, to-time-slot,zip-filename, zip-bytes as response if its successful, else gets error message.

