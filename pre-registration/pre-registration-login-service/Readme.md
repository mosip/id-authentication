# Pre-Registration-login-service:

[Background & Design](https://github.com/mosip/mosip/blob/SPRINT11_PREREG_TEAM_BRANCH/docs/design/pre-registration/pre-registration-login-service.md)

This service details used by Pre-Registration portal to authenticate user by sending OTP to the user, validating with userid and OTP.

[Api Documentation](https://github.com/mosip/mosip/wiki/Pre-Registration-Services#login-service-public)

### Default Port and Context Path
```
server.port=9090
server.servlet.context-path=/preregistration/v1
```
#### Url 
```https://{dns-name}:9090/preregistration/v1/login/swagger-ui.html```

[Application Properties](https://github.com/mosip/mosip/blob/master/config/pre-registration-dev.properties)

The following are the Api name use in this service.

1. POST /login/sendOtp - This Api is use to send the otp to the provided userId in the request body.
2. POST /login/validateOtp - This request will validate the OTP with respect to userid and provide the authorize token in the browser cookies. 
3. POST /login/invalidateToken - This request will invalidate the authorization token when force logout is done.
4. GET /login/config - This request will load the configuration parameters while loading the pre-registration portal page.