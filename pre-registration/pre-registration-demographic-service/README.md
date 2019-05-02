# Pre-Registration-demographic-service:

[Background & Design](pre-registration-individual.md)

This Api can be use to store the demographic details of the citizen for a pre-registration.

#### Api Documentation

```
mvn javadoc:javadoc

```

####  POST Operation
#### Path -  `/applications`
#### Summary
Create new pre-registration by demographic details or update demographic details by providing pre-registration id.

**The inputs which have to be provided are:**
1. Pre-Registration Id 
2. Created By
3. Created Date Time
4. Updated By 
5. Updated Date Time 
6. Language Code 
7. Demographic Details 
8. Identity
9. Gender 
10. City
11. Mobile Number
12. Full Name 
13. Local Administrative Authority
14. Date Of Birth 
15. Email
16. Province 
17. Postal Code
18. Address
19. Region
20. CNIE Number

**The response will be true if demographic request is successful, otherwise false** 


#### PUT Operation
#### Path -  `/applications`
#### Summary
Update the pre-registration status by providing pre-registration id and valid status defined in pre-registration system in request parameter.

**The inputs which have to be provided are:**

1. Pre-Registration Id 
2. Status Code 

#### Response:
"response": "STATUS_UPDATED_SUCESSFULLY"

#### DELETE Operation
#### Path -  `/applications`
#### Summary 
Discard the entire pre-registration details based pre-registration id provided in request parameter.

**The inputs which have to be provided are:**

1. Pre-Registration Id

#### Response:
Get Pre-Registration Id , Deleted By and Deleted Time as response if request is successful, otherwise get error message
#### GET Operation
#### Path -  `/applications`
#### Summary
Retrieve All Pre-Registration id, Full name, Status and Appointment details by user id.

**The inputs which have to be provided are:**
1. User Id 

#### Response:
Get Pre-Registration Id , Full Name, Status Code and Demographic details as response if request is successful, otherwise get error message
#### GET Operation
#### Path -  `/applications/status`
#### Summary
Retrieve pre-registration application status by providing the pre-registration id in request parameter.

**The inputs which have to be provided are:**

1. Pre-Registration Id

#### Response:
 Get Pre-Registration Id and Status Code as response if request is successful, otherwise get error message

#### GET Operation
#### Path -  `/applications/byDateTime`
#### Summary 
Retrieve pre-registration ids between created from and to dates provided in request parameters.

**The inputs which have to be provided are:**

1. From Date
2. To Date 

#### Response:
 Get list of Pre-Registration Id as response if request is successful, otherwise get error message
#### GET Operation
#### Path -  `/applications/details`
#### Summary
Retrieve Pre-Registration demographic data by pre-Registration id provided in request parameter.

**The inputs which have to be provided are:**
1. Pre-Registration Id 
#### Response:
 Get details of Pre-Registration Id as response if request is successful, otherwise get error message

