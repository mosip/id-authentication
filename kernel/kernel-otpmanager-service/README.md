# kernel-otpmanager-service

### 1. Background & Design
```
 This project facilitates generation and validation of OTP for various purposes. EG: Login in Pre-registration.
```
 
 ### 2. [API Documentation <TBA>](TBA)
 ```
 mvn javadoc:javadoc

 ```
 
### 3- Usage Sample
 
 `Usage1:`
 `OTP Generation Request:`
 ```
 {
     "key":"M85301Z"
 }
 ```
`OTP Generation Responses :`
##### Successful Generation :
```
HttpStatus : 201 Created
```
```
{
    "status": "true",
    "message": "GENERATION_SUCCESSFUL"
}
```
##### UnSuccessful Generation, Key Freezed :
  ```
 HttpStatus : 201 Created
 ```
```
{
    "otp": "null",
    "status": "USER_BLOCKED"
}
```

`Usage2:`
 `OTP Validation Request:`
 ```
http://localhost:8085/otp/validate?key=testkey&otp=614491
 ```
  `OTP Validation Responses:`
  ##### Case : Validation Successful
 ```
 HttpStatus : 200 OK
 ``` 
 ```
 {
    "status": "success",
    "message": "VALIDATION_SUCCESSFUL"
}
 ```
 ##### Case : Validation UnSuccessful, Wrong OTP
 ```
 HttpStatus : 406 Not Acceptable
 ```
 ```
 {
    "status": "failure",
    "message": "VALIDATION_UNSUCCESSFUL"
}
 ```
  ##### Case : Validation UnSuccessful, OTP Expired
  ```
 HttpStatus : 406 Not Acceptable
 ``` 
 ```
 {
    "status": "failure",
    "message": "OTP_EXPIRED"
}
 ```
   ##### Case : Validation UnSuccessful, user Blocked
 ```
 HttpStatus : 406 Not Acceptable
 ```
 ```
 {
    "status": "failure",
    "message": "USER_BLOCKED"
}
 ```
 