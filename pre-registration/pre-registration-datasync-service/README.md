# Pre-Registration-datasync-service:

[Background & Design](https://github.com/mosip/mosip/blob/SPRINT11_PREREG_TEAM_BRANCH/docs/design/pre-registration/pre-registration-data-sync-service.md)

This service enables Pre-Registration to a registration client , request to retrieve all pre-registration ids based on registration client id, appointment date and a user type.

[Api Documentation](https://github.com/mosip/mosip/wiki/Pre-Registration-Services#datasync-service-external)

### Default Port and Context Path
```
server.port=9094
server.servlet.context-path=/preregistration/v1
```
#### Url 
```https://{dns-name}:9094/preregistration/v1/sync/swagger-ui.html```

[Application Properties](https://github.com/mosip/mosip/blob/master/config/pre-registration-dev.properties)

The following are the Api name use in this service.

1. POST /sync - This is used by registration client to retrieve all the pre-registration Ids by date range and registration center Id from the authorize token.

2. POST /sync/consumedPreRegIds - This is used by registration processor to fetch all processed pre-registration ids and store in 
pre-registration database.
3. GET /sync/{preRegistrationId} - This request is used by registration client to retrieve particular pre-registration data based on a pre-registration id.

