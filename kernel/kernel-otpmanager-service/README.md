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
     "key":"testkey"
 }
 ```
`OTP Generation Responses :`
##### Successful Generation :

```
{
    "status": "true",
    "message": "VALIDATION_SUCCESSFUL"
}
```
##### UnSuccessful Generation, Key Freezed :

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
 {
    "status": "success",
    "message": "VALIDATION_SUCCESSFUL"
}
 ```
 ##### Case : Validation UnSuccessful, Wrong OTP
 ```
 {
    "status": "failure",
    "message": "VALIDATION_UNSUCCESSFUL"
}
 ```
  ##### Case : Validation UnSuccessful, OTP Expired
 ```
 {
    "status": "failure",
    "message": "OTP_EXPIRED"
}
 ```
   ##### Case : Validation UnSuccessful, user Blocked
 ```
 {
    "status": "failure",
    "message": "USER_BLOCKED"
}
 ```
 