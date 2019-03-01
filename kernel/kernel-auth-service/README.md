# 1. Authentication

## 1.1 Send OTP

This service sends an OTP to the user. The caller of this service have to send the channel in which the OTP will be sent. Based on the application ID, the corresponding channel's recepient address will be found out and the OTP is send accordingly. Note: At this point of time, no Auth Token will be generated. 

### Resource URL
### `POST /v1.0/authenticate/sendotp`

### Resource details

Resource Details | Description
------------ | -------------
Response format | A JSON message will be returned. 
Requires Authentication | no

### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
userid|Yes|This is the userid of the user. Based on the useridtype, this will vary.| -NA- | M392380
otpchannel|Yes|This is the channel in which the OTP will be sent. It should be one of the {"EMAIL", "MOBILENUMBER"}| -NA- | MOBILENUMBER
useridtype|Yes|This field is the user id type. It should be one the {"UIN", "USERID"}. Based on the combination of "appid" and "useridtype" the system identifies from which system to pickup the channel's recepient address| -NA- | USERID
appid|Yes|This is the application ID of the caller of this service. It should be on of the {"PREREGISTRATION", "REGISTRATIONCLIENT", "REGISTRATIONPROCESSOR", "IDA"}| -NA- | PREREGISTRATION

### Example Request
```JSON
{
	"id": "mosip.authentication.sendotp",
	"timestamp": "2019-01-24T10:27:48.628Z",
	"ver": "1.0",
	"request": {
		"userid": "M392380",
		"otpchannel": "MOBILENUMBER",
		"useridtype": "USERID",
		"appid": "REGISTRATIONCLIENT"
	}
}
```
### Example Response
```JSON
{
	"message":"OTP had been sent successfully"
}

```

## 1.2 Authenticate UserId and OTP

This service authenticates the use ID and the OTP. If the authentication is successfull, an AuthToken will be sent in the Response header. 

### Resource URL
### `POST /v1.0/authenticate/useridOTP`

### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
userid|Yes|This is the userid of the user against which the OTP had been sent. Based on the useridtype, this will vary.| -NA- | M392380
otp|Yes|This is OTP which is sent to the userid's preferred channel| -NA- | 6473


### Example Request
```JSON
{
	"id": "mosip.authentication.useridOTP",
	"timestamp": "2019-01-24T10:27:48.628Z",
	"ver": "1.0",
	"request": {
		"userid": "M392380",
		"otp": "6473"
	}
}
```
### Example Response
```
Response Cookie:

Set-Cookie →Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTM1MTU2NX0.pCyibViXo31enOgRD60BnKjEpEA-78yzbWnZGChxCIZ5lTpYnhgm-0dtoT3neFebTJ8eAI7-o8jDWMCMqq6uSw; Max-Age=6000000; Expires=Wed, 08-May-2019 19:59:43 GMT; Path=/; Secure; HttpOnly

Set-Cookie →RefreshToken=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTQzMTk2NX0.A3MRm3LMBmdnEkq0kHltBltpxvrk6v5WPLPEWftPh_HsoeJjOK6w7PHXGhMiBu5F1-8RQeVqFW_OpaeC6sgFGw; Max-Age=6000000; Expires=Wed, 08-May-2019 20:00:30 GMT; Path=/; Secure; HttpOnly

JSON Response:
{
	"message":"OTP validation is successfull"
}

```

## 1.3 Authenticate using username and password

This service will authenticate the username and password. 

### Resource URL
### `POST /v1.0/authenticate/useridPwd`

### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
username|Yes|This is the username of the user. | -NA- | M392380
password|Yes|This is the channel in which the OTP will be sent. It should be one of the {"EMAIL", "MOBILENUMBER"}| -NA- | MOBILENUMBER
appid|Yes|This is the application ID of the caller of this service. It should be on of the {"PREREGISTRATION", "REGISTRATIONCLIENT", "REGISTRATIONPROCESSOR", "IDA"}| -NA- | PREREGISTRATION

### Example Request
```JSON
{
	"id": "mosip.authentication.useridPwd",
	"timestamp": "2019-01-24T10:27:48.628Z",
	"ver": "1.0",
	"request": {
		"username": "M392380",
		"password": "fdkj943lkj32k32ew$8Kf",
		"appid": "REGISTRATIONCLIENT"
	}
}
```
### Example Response
```
Response Cookie:

Set-Cookie →Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTM1MTU2NX0.pCyibViXo31enOgRD60BnKjEpEA-78yzbWnZGChxCIZ5lTpYnhgm-0dtoT3neFebTJ8eAI7-o8jDWMCMqq6uSw; Max-Age=6000000; Expires=Wed, 08-May-2019 19:59:43 GMT; Path=/; Secure; HttpOnly

Set-Cookie →RefreshToken=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTQzMTk2NX0.A3MRm3LMBmdnEkq0kHltBltpxvrk6v5WPLPEWftPh_HsoeJjOK6w7PHXGhMiBu5F1-8RQeVqFW_OpaeC6sgFGw; Max-Age=6000000; Expires=Wed, 08-May-2019 20:00:30 GMT; Path=/; Secure; HttpOnly

JSON:
{
	"message":"Username and password combination had been validated successfully"
}

```

## 1.4 Authenticate using clientid and secret key

This service will authenticate the clientid and secret key. When an application try to call any service in the MOSIP system, the call have to be authenticated and authorized. For example, when Pre-registration application calls some master service, the call have to be authenticated first. This call can facilitate the call. 
The clientid would have provided to the caller application before hand using another procedure. So, before making this call, the caller application have to have the clientid and the secret key.  

### Resource URL
### `POST /v1.0/authenticate/clientidsecretkey`

### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
clientid|Yes|This is the client id, provided to the caller application upfront. | -NA- | D72HJDF8
secretkey|Yes|This is the secret key which was provided to the application corresponding to the clientid| -NA- | JSlj8p789sdfjhlsJKDHFS


### Example Request
```JSON
{
	"id": "mosip.authentication.clientidsecretkey",
	"timestamp": "2019-01-24T10:27:48.628Z",
	"ver": "1.0",
	"request": {
		"clientid": "D72HJDF8",
		"secretkey": "JSlj8p789sdfjhlsJKDHFS"
	}
}
```
### Example Response
```
Response Cookie:

Set-Cookie →Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTM1MTU2NX0.pCyibViXo31enOgRD60BnKjEpEA-78yzbWnZGChxCIZ5lTpYnhgm-0dtoT3neFebTJ8eAI7-o8jDWMCMqq6uSw; Max-Age=6000000; Expires=Wed, 08-May-2019 19:59:43 GMT; Path=/; Secure; HttpOnly

Set-Cookie →RefreshToken=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTQzMTk2NX0.A3MRm3LMBmdnEkq0kHltBltpxvrk6v5WPLPEWftPh_HsoeJjOK6w7PHXGhMiBu5F1-8RQeVqFW_OpaeC6sgFGw; Max-Age=6000000; Expires=Wed, 08-May-2019 20:00:30 GMT; Path=/; Secure; HttpOnly

JSON:
{
	"message":"Clientid and Token combination had been validated successfully"
}

```

# 2. Authorization

## 2.1 Validate Token

This service checks the validity of the Auth token.

### Resource URL
### `POST /v1.0/authorize/validateToken`

### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
Authorization|Yes|AuthToken passed in the request cookie| | eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJiMDhmODZhZi0zNWRhLTQ4ZjItOGZhYi1jZWYzOTA0NjYwYmQifQ.-xN_h82PHVTCMA9vdoHrcZxH-x5mb11y1537t3rGzcM

### Example Request
```
Request Cookie:
Authorization eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJiMDhmODZhZi0zNWRhLTQ4ZjItOGZhYi1jZWYzOTA0NjYwYmQifQ.-xN_h82PHVTCMA9vdoHrcZxH-x5mb11y1537t3rGzcM
```
### Example Response
```JSON
{
	"message":"Token had been validated successfully"
}

```

## 2.2 Refresh Token

This service generates a new Auth token using Refresh token on expiration

### Resource URL
### `POST /v1.0/authorize/refreshToken`

### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Cookies and also a JSON message will be returned. 
Requires Authentication | no

### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
Authorization|Yes|AuthToken passed in the request cookie| | eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJiMDhmODZhZi0zNWRhLTQ4ZjItOGZhYi1jZWYzOTA0NjYwYmQifQ.-xN_h82PHVTCMA9vdoHrcZxH-x5mb11y1537t3rGzcM

### Example Request
```
Request Cookie:
Authorization eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJiMDhmODZhZi0zNWRhLTQ4ZjItOGZhYi1jZWYzOTA0NjYwYmQifQ.-xN_h82PHVTCMA9vdoHrcZxH-x5mb11y1537t3rGzcM
```
### Example Response
```JSON
{
	"message":"New auth token generated successfully"
}

```
## 2.3 Invalidate Token

This service invalidates the token

### Resource URL
### `POST /v1.0/authorize/invalidateToken`

### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Cookies and also a JSON message will be returned. 
Requires Authentication | no

### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
Authorization|Yes|AuthToken passed in the request cookie| | eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJiMDhmODZhZi0zNWRhLTQ4ZjItOGZhYi1jZWYzOTA0NjYwYmQifQ.-xN_h82PHVTCMA9vdoHrcZxH-x5mb11y1537t3rGzcM

### Example Request
```
Request Cookie:
Authorization eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJiMDhmODZhZi0zNWRhLTQ4ZjItOGZhYi1jZWYzOTA0NjYwYmQifQ.-xN_h82PHVTCMA9vdoHrcZxH-x5mb11y1537t3rGzcM
```
### Example Response
```JSON
{
	"message":"Token invalidated successfully"
}

```

# 3. OTP services

## 3.1 Generate OTP

The OTP Generator component will receive a request to generate OTP, validate if the OTP generation request is from an authorized source, call OTP generator API with the input parameters (Key), receive the OTP from the OTP generator API which is generated based on the OTP generation policy and respond to the source with the OTP.

The OTP Generator can also reject a request from a blocked/frozen account and assign a validity to each OTP that is generated, based on the defined policy

### Resource URL
### `POST /v1.0/otp/generateOTP`

### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent as a JSON response. 
Requires Authentication | no

### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
Key|Yes|AuthToken passed in the request header| | 

### Example Request
```JSON
{
	"id": "mosip.otp.generateOTP",
	"timestamp": "2019-01-24T10:27:48.628Z",
	"ver": "1.0",
	"request": {
	   "key": "9820173642"
	}
}
```
### Example Response
```JSON
{
  "otp": "849004",
  "status": "GENERATION_SUCCESSFUL"
}

```

## 3.2 Validate OTP

This component facilitates basic validation of an OTP.

This includes: Receiving a request for OTP validation with required input parameters (Key), Validating the pattern of OTP generated based on defined policy, validating if the OTP is active/inactive and responding to the source with a response (Valid/Invalid)

This component also facilitates deletion of every successfully validated OTP when consumed and freezing an account for exceeding the number of retries/wrong input of OTP.

### Resource URL
### `POST /v1.0/otp/validateOTP`

### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent as a JSON response. 
Requires Authentication | no

### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
Key|Yes|AuthToken passed in the request header| -NA- | 9820173642
otp|Yes|OTP which was sent to the user| -NA- | 849004

### Example Request
```JSON
{
	"id": "mosip.otp.validateOTP",
	"timestamp": "2019-01-24T10:27:48.628Z",
	"ver": "1.0",
	"request": {
		"key": "9820173642", 
		"otp":"849004"
	}
}
```
### Example Response
```JSON
{
  "status": "success",
  "message": "VALIDATION SUCCESSFUL"
}

```