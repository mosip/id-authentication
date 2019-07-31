# Admin Login

#### Background

Admin application is used by the adminstrators who configure the MOSIP platform. The administrators have to be authenticated before accessing the Admin application and have to be authorized for various functionalities in the application. When an administrator logs in, he can be associated with various roles. And various roles have various combinations of the login factors among the username/password, OTP or Biometrics based logins. 

#### Solution

The login module first decides the roles and their corresponding login factors. Based on the login factors the authentication mechanisms are shown. 

**The key solution considerations are**

1. Configurability: The login factors for a role should be configurable. If a country decides to choose the login factors initially or later, they should be able to do so. 

2. Robust: The solution should be robust to handle the authentication. The user should be able to login successfully, only after all the configured authentication factors are successfull. 

3. Concurrent logins: Concurrent login is allowed during login. 


**Sequence diagram**


![Sequence Diagram](_images/admin-login.jpg)


## Implementation


**admin-login** [README](../../../admin/admin-login/README.md)


