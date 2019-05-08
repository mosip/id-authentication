# Pre-Registration-notification-service:

[Background & Design](https://github.com/mosip/mosip/blob/SPRINT11_PREREG_TEAM_BRANCH/docs/design/pre-registration/pre-registration-notification-service.md)

This service is used by Pre-Registration portal to trigger notification via SMS or Email and get QRCode.

[Api Documentation](https://github.com/mosip/mosip/wiki/Pre-Registration-Services#notification-service-public)


### Default Port and Context Path
```
server.port=9099
server.servlet.context-path=/preregistration/v1
```
#### Url 
```https://{dns-name}:9099/preregistration/v1/notification/swagger-ui.html```

[Application Properties](https://github.com/mosip/mosip/blob/master/config/pre-registration-dev.properties)

The following are the Api name use in this service.

1. POST notification/notify - 
This request is used to notify the pre-registration acknowledgement via Email and SMS.
