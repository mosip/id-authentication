
# Approach for Authentication Service

**Background**
- Exposing the REST API to authenticate citizen.

The target users are -
   - Pre-Registration UI

The key requirements -

-   Create the REST API to authenticate citizen by user-id and OTP while login and invalidate the token for logout. which internally call the authentication service.

The key non-functional requirements are

-   Log the each state of the pre-registration citizen login:

    -   As a security measures the citizen OTP should not be logged.

-   Exception :

    -   Any exception occurred during the login, the same will
        be reported to the user with the user understandable exception.

**Solution**

**Login - sendOTP :**

- Create a REST API as '/login/sendOtp' accept the user-id, lang code, useridtype, otpchannel from the pre-registration application portal.

- Get the app-id value defined in global configuration and prepare the request object.

- Then do the REST call to send OTP from Authentication service.
refer : https://github.com/mosip/mosip/wiki/AuthN-&-AuthZ-APIs


**Class Diagram**

![pre-registration auth service - sendOTP ](_images/_class_diagram/pre-registration-login-sendOtp.png)

**Sequence Diagram**

![pre-registration auth service - sendOTP](_images/_sequence_diagram/pre-registration-login-sendOtp.png)

**Error Code** 

 While sending the OTP if there is any error then send the respective error code to the UI from API layer as Response object.

  Code   |       Type  | Message|
-----|----------|-------------|
  PRG_AUTH-001 |  Error   |   Failed to send the OTP.


**Login - validateOTP :**

- Create a REST API as '/login/validateOtp' accept the user-id and otp from the pre-registration application portal.

- Then do the REST call to validate OTP from Authentication service.
refer : https://github.com/mosip/mosip/wiki/AuthN-&-AuthZ-APIs

**Class Diagram**

![pre-registration auth service - validateOTP](_images/_class_diagram/pre-registration-login-validateOtp.png)

**Sequence Diagram**

![pre-registration auth service - validateOTP](_images/_sequence_diagram/pre-registration-login-validateOtp.png)

**Error Code** 

  While validating the OTP if there is any error then send the respective error code to the UI from API layer as Response object.
  
  Code   |       Type  | Message|
-----|----------|-------------|
  PRG_AUTH-002 |  Error   |   Failed to validate the OTP.

**Logout - invalidate :**

- Create a REST API as '/logout' accept the AuthToken from the pre-registration application portal.

- Then do the REST call to invalidate the authentication token from Authentication service.
refer : https://github.com/mosip/mosip/wiki/AuthN-&-AuthZ-APIs

**Class Diagram**

![pre-registration auth service - logout](_images/_class_diagram/pre-registration-logout.png)

**Sequence Diagram**

![pre-registration auth service - logout](_images/_sequence_diagram/pre-registration-logout.png)

**Error Code**

  While invalidating AuthToken if there is any error then send the respective error code to the UI from API layer as Response object.
  
  Code   |       Type  | Message|
-----|----------|-------------|
  PRG_AUTH-003 |  Error   |   Failed to invalidate token.

**Dependency Modules**

Component Name | Module Name | Description | 
-----|----------|-------------|
  Authentication Service    |   Kernel        |    To send and validate OTP.
  Exception Manager  |  Kernel     |       To prepare the user defined exception and render to the user.
  Log        |          Kernel         |   To log the process.

**User Story References**

  **User Story No.** |  **Reference Link** |
  -----|----------|
  **MOS-13173**      |     <https://mosipid.atlassian.net/browse/MOS-13173>