* [Login](#login)
* [Master Data](#master-data)
* [Account Management](#account-management)
* [User Management](#user-management)
* [UIN Services](#uin-services)


# Login

* [GET /security/authfactors](#get-securityauthfactors)
* [POST /login](#post-login)
* [POST /sendotp](#post-sendotp)
* [POST /useridOTP](#post-useridOTP)
* [POST /logout](#post-logout)


### GET /security/authfactors

This service will give back the authentication factors for the login of the user. It will accept the username and find the user's groups. Based on the groups, the auth factors are decided and send back. 

#### Resource URL
<div>https://mosip.io/v1/admin/security/authfactors/{userId}</div>

#### Resource details
Resource Details | Description
------------ | -------------
Response format | JSON
Requires Authentication | Yes

#### Request Part Parameters
Name | Required | Description |  Example
-----|----------|-------------|--------
userid |Yes|User id of the user| UDAE423
timeStamp |Yes|Date-time  in UTC ISO-8601| 2007-12-03T10:15:30Z

#### Request
<div>https://mosip.io/v1/admin/security/authfactors/UDAE423?timeStamp=2018-12-09T06%3A39%3A03.683Z </div>

#### Responses:
##### Success Response:
###### Status code: '200'
###### Description: List of auth factors are returned
```JSON

{
	"id": "mosip.admin.security.authfactors",
	"version": "1.0",
	"metadata": {},
	"responsetime": "2007-12-03T10:15:30Z",
	"errors": [],
	"response": {
		"authtypes": ["PASSWORD", "OTP", "FINGERPRINT_TYPE-1"]
	}
}
```

##### Error Response:
###### Status code: '200'
###### Description: If the user is not found. 
```JSON

{
  "id": "mosip.admin.security.authfactors",
  "version": "1.0",
  "metadata": {},
  "responsetime": "2007-12-03T10:15:30Z",
  "errors": [
    {
      "errorCode": "ADMN-AUTH-USR-NOTFOUND",
      "message": "The userid is not found in the system"
    }
  ]
}
```


### POST /login

This service will authenticate the username and password. This service will call the login service in the Auth API and will return the token back to the caller. 

#### Resource URL
<div>https://mosip.io/v1/admin/login</div>

#### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

#### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
username|Yes|This is the username of the user. | -NA- | M392380
password|Yes|This is the password of the user| -NA- | MOBILENUMBER
appid|Yes|This is the application ID of the caller of this service.| -NA- | ADMIN

#### Example Request
```JSON
{
	"id": "mosip.admin.authentication.login",
	"version":"1.0",	
	"requesttime":"2007-12-03T10:15:30Z",
	"request": {
		"username": "M392380",
		"password": "fdkj943lkj32k32ew$8Kf",
		"appid": "ADMIN"
	}
}
```
#### Example Response

Success Response 

```
Response Cookie:

Set-Cookie →Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTM1MTU2NX0.pCyibViXo31enOgRD60BnKjEpEA-78yzbWnZGChxCIZ5lTpYnhgm-0dtoT3neFebTJ8eAI7-o8jDWMCMqq6uSw; Max-Age=6000000; Expires=Wed, 08-May-2019 19:59:43 GMT; Path=/; Secure; HttpOnly


JSON:
{
	"id": "mosip.admin.authentication.login",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"response": {
                "status": "success",
		"message":"Username and password combination had been validated successfully"
	}
}

```


Error Responses

1. Invalid credentials: If the passed credentials is not correct. 
```JSON

{
	"id": "mosip.admin.authentication.login",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_INVALIDCREDENTIALS",
				"message": "The passed in credentials is not correct"
		  }	
		]
}

```

2. Invalid application ID: If the passed in application is not correct. 
```JSON

{
	"id": "mosip.admin.authentication.login",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_INVALIDAPPID",
				"message": "The passed in application ID is not correct"
		  }	
		]
}

```

### POST /sendotp

This service sends an OTP to the user. The caller of this service have to send the channel in which the OTP will be sent. Based on the application ID, the corresponding channel's recepient address will be found out and the OTP is send accordingly. Note: At this point of time, no Auth Token will be generated. 

#### Resource URL
<div>https://mosip.io/v1/admin/sendotp</div>

#### Resource details

Resource Details | Description
------------ | -------------
Response format | A JSON message will be returned. 
Requires Authentication | no

#### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
userid|Yes|This is the userid of the user. Based on the useridtype, this will vary.| -NA- | M392380
otpchannel|Yes|This is the channel in which the OTP will be sent. It is an array of the enumeration {"EMAIL", "MOBILENUMBER"}. If the channel is not found, ChannelNotSupported error will be sent back| -NA- | MOBILENUMBER
useridtype|Yes|This field is the user id type. It should be one the {"UIN", "USERID"}. Based on the combination of "appid" and "useridtype" the system identifies from which system to pickup the channel's recepient address| -NA- | USERID
appid|Yes|This is the application ID of the caller of this service. It should be on of the {"PREREGISTRATION", "REGISTRATIONCLIENT", "REGISTRATIONPROCESSOR", "IDA"}| -NA- | PREREGISTRATION

#### Example Request
```JSON
{
	"id": "mosip.admin.authentication.sendotp",
	"version":"1.0",	
	"requesttime":"2007-12-03T10:15:30Z",
	"request": {
		"userid": "M392380",
		"otpchannel": ["MOBILE", "EMAIL"],
		"templateParams": {
			"expiryTime":"20 minutes",
			"purpose":"Changing password",
		}
		"useridtype": "USERID",
		"appid": "REGISTRATIONCLIENT"
	}
}
```
#### Example Response

Success Response 

```JSON
{
	"id": "mosip.admin.authentication.sendotp",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"response": {
                "status": "success",
		"message":"OTP had been sent successfully"
	}
}

```

Error Response 

1. Invalid Channel: This is the error response in case if the channel is not valid. 

```JSON
{
	"id": "mosip.admin.authentication.sendotp",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_CHANNEL_INVALID",
				"message": "The passed channel is invalid."
		  }	
		]
}

```

2. Multiple channels not supported: In case, if the caller can send only one channel, then this error will be sent. For example, Pre-Registration module cannot have multiple channels. 

```JSON
{
	"id": "mosip.admin.authentication.sendotp",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_MULTIPLE_CHANNELS",
				"message": "Multiple channels are not supported in your module."
		  }	
		]
}

```



3. User not found: If the passed is not found in the system. 

```JSON
{
	"id": "mosip.admin.authentication.sendotp",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_USER_NOT_FOUND",
				"message": "The passed in user is not found"
		  }	
		]
}

```


4. Channel path not found: If the channel's path is not found. For example, if the channel is email and the email ID is not found for that user. 

```JSON
{
	"id": "mosip.admin.authentication.sendotp",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_CHANNELPATH_NOT_FOUND",
				"message": "The passed in user is not found"
		  }	
		]
}

```
### POST /useridOTP

This service authenticates the use ID and the OTP. If the authentication is successfull, an AuthToken will be sent in the Response header. 

#### Resource URL
<div>https://mosip.io/v1/admin/useridOTP</div>

#### `POST /v1.0/admin/useridOTP`

#### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

#### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
userid|Yes|This is the userid of the user against which the OTP had been sent. Based on the useridtype, this will vary.| -NA- | M392380
otp|Yes|This is OTP which is sent to the userid's preferred channel| -NA- | 6473


#### Example Request
```JSON
{
	"id": "mosip.admin.authentication.useridOTP",
	"version":"1.0",	
	"requesttime":"2007-12-03T10:15:30Z",
	"request": {
		"userid": "M392380",
		"otp": "6473"
	}
}
```
#### Example Response

Success Response 


```
Response Cookie:

Set-Cookie →Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTM1MTU2NX0.pCyibViXo31enOgRD60BnKjEpEA-78yzbWnZGChxCIZ5lTpYnhgm-0dtoT3neFebTJ8eAI7-o8jDWMCMqq6uSw; Max-Age=6000000; Expires=Wed, 08-May-2019 19:59:43 GMT; Path=/; Secure; HttpOnly


JSON Response:
{
	"id": "mosip.admin.authentication.useridOTP",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"response": {
                "status": "success",
		"message":"OTP validation is successfull"
	}
}

```


Error Responses

1. Invalid OTP: If the passed OTP is not valid. 

```JSON

{
	"id": "mosip.admin.authentication.useridOTP",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_INVALIDOTP",
				"message": "The passed in OTP is invalid"
		  }	
		]
}

```


2. Expired OTP: If the passed OTP is expired. 

```JSON

{
	"id": "mosip.admin.authentication.useridOTP",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_EXPIREDOTP",
				"message": "The passed OTP is expired"
		  }	
		]
}
```
### POST /logout

This service will logout the user. This service will call the logout service in the Auth API and will remove the token for the user.

#### Resource URL
<div>https://mosip.io/v1/admin/logout</div>

#### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | yes

### Request 

Cookie : Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTM1MTU2NX0.pCyibViXo31enOgRD60BnKjEpEA-78yzbWnZGChxCIZ5lTpYnhgm-0dtoT3neFebTJ8eAI7-o8jDWMCMqq6uSw; Max-Age=6000000; Expires=Wed, 08-May-2019 19:59:43 GMT; Path=/; Secure; HttpOnly

#### Example Response

Success Response 

```
JSON:
{
	"id": "mosip.admin.authentication.logout",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"response": {
                "status": "success",
				"message":"Username has been logged out successfully"
	}
}

```


Error Responses

1. Invalid token: If the passed token is not correct. 
```JSON

{
	"id": "mosip.admin.authentication.logout",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_INVALIDCREDENTIALS",
				"message": "The passed in token is not correct"
		  }	
		]
}

```
# Master Data

* [GET /mastercards](#get-mastercards)


### GET /mastercards

This service will give back the list of master data which was supposed to be displayed as cards in the master data screen. The list of master data are read from the configuration server and returned as an array based on the requested language. 

#### Resource URL
<div>https://mosip.io/v1/admin/mastercards/{languagecode}</div>

#### Resource details
Resource Details | Description
------------ | -------------
Response format | JSON
Requires Authentication | Yes

#### Request Part Parameters
Name | Required | Description |  Example
-----|----------|-------------|--------
languagecode|Yes|Language code in ISO 639-2 standard| -NA- |eng

#### Request
<div>https://mosip.io/v1/admin/mastercards </div>

#### Responses:
##### Success Response:
###### Status code: '200'
###### Description: List of master data are returned based on the requested language
```JSON

{
	"id": "mosip.admin.mastercards",
	"version": "1.0",
	"metadata": {},
	"responsetime": "2007-12-03T10:15:30Z",
	"errors": [],
	"response": {
		"masterdata": [
                               {
                                 "dataCode": "machines",
                                 "displayName": "Machines"
                               },
                               {
                                 "dataCode": "devices",
                                 "displayName": "Devices"
                               },
                               {
                                 "dataCode": "centers",
                                 "displayName": "Registration Centers"
                               }
                              ]
	}
}
```

##### Error Response:
###### Status code: '200'
###### Description: If the user is not found. 
```JSON

{
  "id": "mosip.admin.mastercards",
  "version": "1.0",
  "metadata": {},
  "responsetime": "2007-12-03T10:15:30Z",
  "errors": [
    {
      "errorCode": "ADMN-LANG-MISSING",
      "message": "The data is not found for the passed language code"
    }
  ]
}
```

# Account Management

* [GET /unblockuser](#get-unblockuser)
* [POST /changepassword](#post-changepassword)
* [POST /resetpassword](#post-resetpassword)
* [GET /username/{mobilenumber}](#get-usernamemobilenumber)

### GET /unblockuser

The user can unblock himself using this service. Once authenticated via OTP, the user will be unblocked. 

#### Resource URL
<div>https://mosip.io/v1/admin/unblockuser</div>

#### Resource details
Resource Details | Description
------------ | -------------
Response format | JSON
Requires Authentication | Yes

#### Request Part Parameters
Name | Required | Description |  Example
-----|----------|-------------|--------
userid |Yes|User id of the user| UDAE423
timeStamp |Yes|Date-time  in UTC ISO-8601| 2007-12-03T10:15:30Z

#### Request
<div>https://mosip.io/v1/admin/unblockuser?userid=UDAE423&timeStamp=2018-12-09T06%3A39%3A03.683Z </div>

#### Responses:
##### Success Response:
###### Status code: '200'
###### Description: List of auth factors are returned
```JSON

{
	"id": "mosip.admin.accountmanagement.unblockuser",
	"version": "1.0",
	"metadata": {},
	"responsetime": "2007-12-03T10:15:30Z",
	"errors": [],
	"response": {
		"Status":"SUCCESS",
		"Message":"The user had been succesfully unblocked"
	}
}
```

##### Error Response:
###### Status code: '200'
###### Description: If the user is not found. 
```JSON

{
  "id": "mosip.admin.accountmanagement.unblockuser",
  "version": "1.0",
  "metadata": {},
  "responsetime": "2007-12-03T10:15:30Z",
  "errors": [
    {
      "errorCode": "ADMN-ACC-USR-NOTFOUND",
      "message": "The userid is not found in the system"
    }
  ]
}
```


### POST /changepassword

This service will change the password to new value. 

#### Resource URL
<div>https://mosip.io/v1/admin/changepassword</div>

#### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

#### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
username|Yes|This is the username of the user. | -NA- | M392380
oldpassword|Yes|This is the old password of the user| -NA- | 6^S98sG#
newpassword|Yes|This is the new password of the user| -NA- | hfsfs32#
appid|Yes|This is the application ID of the caller of this service.| -NA- | ADMIN


#### Example Request
```JSON
{
	"id": "mosip.admin.accountmanagement.changepassword",
	"version":"1.0",	
	"requesttime":"2007-12-03T10:15:30Z",
	"request": {
		"username": "M392380",
		"oldpassword": "6^S98sG#",
		"newpassword": "hfsfs32#",
		"appid": "ADMIN"
	}
}
```
#### Example Response

Success Response 

```
Response Cookie:

Set-Cookie →Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTM1MTU2NX0.pCyibViXo31enOgRD60BnKjEpEA-78yzbWnZGChxCIZ5lTpYnhgm-0dtoT3neFebTJ8eAI7-o8jDWMCMqq6uSw; Max-Age=6000000; Expires=Wed, 08-May-2019 19:59:43 GMT; Path=/; Secure; HttpOnly


JSON:
{
	"id": "mosip.admin.accountmanagement.changepassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"response": {
        "status": "success",
		"message":"New password been set successfully"
	}
}

```


Error Responses

1. Invalid credentials: If the passed credentials is not correct. 
```JSON

{
	"id": "mosip.admin.accountmanagement.changepassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN-ACC-INVLD-CRDNTIALS",
				"message": "The passed in credentials is not correct"
		  }	
		]
}

```

2. Password policy rule not met: If the new password is not meeting the password policy. 
```JSON

{
	"id": "mosip.admin.authentication.changepassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_LNGTH_NOT_STSFIED",
				"message": "The length of the new password is lesser than expected"
		  }	
		]
}

```


3. Invalid application ID: If the passed in application is not correct. 
```JSON

{
	"id": "mosip.admin.accountmanagement.changepassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_INVALIDAPPID",
				"message": "The passed in application ID is not correct"
		  }	
		]
}


```
### POST /resetpassword

This service will reset the password to new value. 

#### Resource URL
<div>https://mosip.io/v1/admin/resetpassword</div>

#### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

#### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
username|Yes|This is the username of the user. | -NA- | M392380
newpassword|Yes|This is the new password of the user| -NA- | hfsfs32#
appid|Yes|This is the application ID of the caller of this service.| -NA- | ADMIN


#### Example Request
```JSON
{
	"id": "mosip.admin.accountmanagement.resetpassword",
	"version":"1.0",	
	"requesttime":"2007-12-03T10:15:30Z",
	"request": {
		"username": "M392380",
		"newpassword": "hfsfs32#",
		"appid": "ADMIN"
	}
}
```
#### Example Response

Success Response 

```
Response Cookie:

Set-Cookie →Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpbmRpdmlkdWFsIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJpbmRpdmlkdWFsQGdtYWlsLmNvbSIsInJvbGUiOiJwZXJzb24iLCJpYXQiOjE1NTEzNDU1NjUsImV4cCI6MTU1MTM1MTU2NX0.pCyibViXo31enOgRD60BnKjEpEA-78yzbWnZGChxCIZ5lTpYnhgm-0dtoT3neFebTJ8eAI7-o8jDWMCMqq6uSw; Max-Age=6000000; Expires=Wed, 08-May-2019 19:59:43 GMT; Path=/; Secure; HttpOnly


JSON:
{
	"id": "mosip.admin.accountmanagement.resetpassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"response": {
        "status": "success",
		"message":"Password reset successfully"
	}
}

```


Error Responses

1. Invalid credentials: If the passed credentials is not correct. 
```JSON

{
	"id": "mosip.admin.accountmanagement.resetpassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN-ACC-INVLD-CRDNTIALS",
				"message": "The passed in credentials is not correct"
		  }	
		]
}

```

2. Password policy rule not met: If the new password is not meeting the password policy. 
```JSON

{
	"id": "mosip.admin.accountmanagement.resetpassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_LNGTH_NOT_STSFIED",
				"message": "The length of the new password is lesser than expected"
		  }	
		]
}

```


3. Invalid application ID: If the passed in application is not correct. 
```JSON

{
	"id": "mosip.admin.accountmanagement.resetpassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_INVALIDAPPID",
				"message": "The passed in application ID is not correct"
		  }	
		]
}

```
### GET /username/{mobilenumber}

The user can get user-name from mobile number.

#### Resource URL
<div>https://mosip.io/v1/admin/actmgmt/username/{mobilenumber}</div>

#### Resource details
Resource Details | Description
------------ | -------------
Response format | JSON
Requires Authentication | Yes

#### Request Part Parameters
Name | Required | Description |  Example
-----|----------|-------------|--------
mobilenumber |Yes|mobile number of the user| 325624646
timeStamp |Yes|Date-time  in UTC ISO-8601| 2007-12-03T10:15:30Z

#### Request
<div>https://mosip.io/v1/admin/actmgmt/username/458575535 </div>

#### Responses:
##### Success Response:
###### Status code: '200'
###### Description: List of auth factors are returned
```JSON

{
	"id": "mosip.admin.accountmanagement.mobilenumber",
	"version": "1.0",
	"metadata": {},
	"responsetime": "2007-12-03T10:15:30Z",
	"errors": [],
	"response": {
		"Status":"SUCCESS",
		"Message":"The user had been succesfully unblocked"
	}
}
```

##### Error Response:
###### Status code: '200'
###### Description: If the user is not found. 
```JSON

{
  "id": "mosip.admin.accountmanagement.mobilenumber",
  "version": "1.0",
  "metadata": {},
  "responsetime": "2007-12-03T10:15:30Z",
  "errors": [
    {
      "errorCode": "ADMN-ACC-USR-NOTFOUND",
      "message": "The userid is not found in the system"
    }
  ]
}
```

# User Management 

* [POST /register](#post-register)
* [POST /rid](#post-rid)
* [POST /password](#post-password)

### POST /register

This service will register a new user.

#### Resource URL
<div>https://mosip.io/v1/admin/usermgmt/register</div>

#### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

#### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
username|Yes|This is the username of the user. | -NA- | M392380
firstName|Yes|This is the firstname of the user| -NA- | test#
lastName|Yes|This is the lastName of the user| -NA- | name#
contactNo|Yes|This is the contactNo of the user| -NA- | 974822990#
emailID|Yes|This is the emailID of the user| -NA- | testname@mosip.io#
dateOfBirth|Yes|This is the dateOfBirth of the user| -NA- | 1975-11-05#
gender|Yes|This is the gender of the user| -NA- | testname@mosip.io#
role|Yes|This is the role of the user| -NA- | SUPER_ADMIN#
appid|Yes|This is the application ID of the caller of this service.| -NA- | ADMIN


#### Example Request
```JSON
{
	"id": "mosip.admin.usermanagement.register",
	"version":"1.0",	
	"requesttime":"2007-12-03T10:15:30Z",
	"request": {
		"username": "M392380",
		"firstName": "test",
		"lastName": "name",
		"contactNo": "974822990",
		"emailID": "testname@mosip.io",
		"dateOfBirth": "1975-11-05",
		"gender": "male",
		"role": "ADMIN",
		"appid": "ADMIN"
	}
}
```
#### Example Response

Success Response 

```

JSON:
{
	"id": "mosip.admin.usermanagement.register",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"response": {
        "status": "success",
		"message":"User created successfully"
	}
}

```


Error Responses

1. Invalid credentials: If the passed credentials is not correct. 
```JSON

{
	"id": "mosip.admin.usermanagement.register",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN-ACC-INVLD-CRDNTIALS",
				"message": "The passed in credentials is not correct"
		  }	
		]
}

```

2. Invalid application ID: If the passed in application is not correct. 
```JSON

{
	"id": "mosip.admin.usermanagement.resetpassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_INVALIDAPPID",
				"message": "The passed in application ID is not correct"
		  }	
		]
}

```
### POST /rid

This service will check for the rid with the user.

#### Resource URL
<div>https://mosip.io/v1/admin/usermgmt/rid</div>

#### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

#### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
username|Yes|This is the username of the user. | -NA- | M392380
rid|Yes|This is the registration of the user| -NA- | test#
appid|Yes|This is the application ID of the caller of this service.| -NA- | ADMIN


#### Example Request
```JSON
{
	"id": "mosip.admin.usermanagement.register",
	"version":"1.0",	
	"requesttime":"2007-12-03T10:15:30Z",
	"request": {
		"username": "M392380",
		"rid": "41355315155",
		"appid": "ADMIN"
	}
}
```
#### Example Response

Success Response 

```

JSON:
{
	"id": "mosip.admin.usermanagement.rid",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"response": {
        "status": "success",
		"message":"Rid verification mail sent successfully"
	}
}

```


Error Responses

1. Invalid credentials: If the passed credentials is not correct. 
```JSON

{
	"id": "mosip.admin.usermanagement.register",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN-ACC-INVLD-CRDNTIALS",
				"message": "The passed in credentials is not correct"
		  }	
		]
}

```

2. Invalid application ID: If the passed in application is not correct. 
```JSON

{
	"id": "mosip.admin.usermanagement.resetpassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_INVALIDAPPID",
				"message": "The passed in application ID is not correct"
		  }	
		]
}

```

3. Invalid application ID: If the passed in application is not correct. 
```JSON

{
	"id": "mosip.admin.usermanagement.resetpassword",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_CHANNELNOTPRESENT",
				"message": "The channel passed with username is not present"
		  }	
		]
}

```
### POST /password

This service will add a new password to the user.

#### Resource URL
<div>https://mosip.io/v1/admin/usermgmt/password</div>

#### Resource details

Resource Details | Description
------------ | -------------
Response format | The response will be sent in the Response Header and also a JSON message will be returned. 
Requires Authentication | no

#### Parameters
Name | Required | Description | Default Value | Example
-----|----------|-------------|---------------|--------
username|Yes|This is the username of the user. | -NA- | M392380
rid|Yes|This is the registration id of the user| -NA- | 24331562664#
password|Yes|This is the new password of the user| -NA- | ksafuff#
appid|Yes|This is the application ID of the caller of this service.| -NA- | ADMIN


#### Example Request
```JSON
{
	"id": "mosip.admin.usermanagement.password",
	"version":"1.0",	
	"requesttime":"2007-12-03T10:15:30Z",
	"request": {
		"username": "M392380",
		"rid": "41355315155",
		"password": "ksafuff",
		"appid": "ADMIN"
	}
}
```
#### Example Response

Success Response 

```

JSON:
{
	"id": "mosip.admin.usermanagement.password",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"response": {
        "status": "success",
		"message":"Password created successfully for user"
	}
}

```


Error Responses

1. Invalid credentials: If the passed credentials is not correct. 
```JSON

{
	"id": "mosip.admin.usermanagement.password",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN-ACC-INVLD-CRDNTIALS",
				"message": "The passed in credentials is not correct"
		  }	
		]
}

```

2. Invalid application ID: If the passed in application is not correct. 
```JSON

{
	"id": "mosip.admin.usermanagement.password",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_INVALIDAPPID",
				"message": "The passed in application ID is not correct"
		  }	
		]
}

```

3. Invalid application ID: If the passed in application is not correct. 
```JSON

{
	"id": "mosip.admin.usermanagement.password",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_CHANNELNOTPRESENT",
				"message": "The channel passed with username is not present"
		  }	
		]
}

```

4. Password policy rule not met: If the new password is not meeting the password policy. 
```JSON

{
	"id": "mosip.admin.usermanagement.password",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_LNGTH_NOT_STSFIED",
				"message": "The length of the new password is lesser than expected"
		  }	
		]
}
```

# UIN Services

* [GET /status/{uin}](#get-status)

### GET /status/{uin}

The user can get status of the uin

#### Resource URL
<div>https://mosip.io/v1/admin/uinmgmt/status/{uin}</div>

#### Resource details
Resource Details | Description
------------ | -------------
Response format | JSON
Requires Authentication | Yes

#### Request Part Parameters
Name | Required | Description |  Example
-----|----------|-------------|--------
uin |Yes|uin number of the user| 325624646
timeStamp |Yes|Date-time  in UTC ISO-8601| 2007-12-03T10:15:30Z

#### Request
<div>https://mosip.io/v1/admin/uinmgmt/status/458575535</div>

#### Responses:
##### Success Response:
###### Status code: '200'
###### Description: returns status of uin
```JSON

{
	"id": "mosip.admin.uinmgmt.status",
	"version": "1.0",
	"metadata": {},
	"responsetime": "2007-12-03T10:15:30Z",
	"errors": [],
	"response": {
		"Status":"Valid",
	}
}
```
Error Responses

1. Invalid credentials: If the passed credentials is not correct. 
```JSON

{
	"id": "mosip.admin.uinmgmt.status",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN-ACC-INVLD-CRDNTIALS",
				"message": "The passed in credentials is not correct"
		  }	
		]
}

```

2. Invalid application ID: If the passed in application is not correct. 
```JSON

{
	"id": "mosip.admin.uinmgmt.status",
	"ver": "1.0",
	"responsetime": "2007-12-03T10:15:30Z",
	"errors":[
			{
				"errorCode": "ADMN_AUTH_ERR_INVALIDAPPID",
				"message": "The passed in application ID is not correct"
		  }	
		]
}

```