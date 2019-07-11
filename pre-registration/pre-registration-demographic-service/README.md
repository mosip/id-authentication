# Pre-Registration-demographic-service:

[Background & Design](https://github.com/mosip/mosip/blob/SPRINT11_PREREG_TEAM_BRANCH/docs/design/pre-registration/pre-registration-demographic-service.md)

This Api can be use to create and store the demographic details of the citizen for a pre-registration.

[Api Documentation](https://github.com/mosip/mosip/wiki/Pre-Registration-Services#demographic-service-public)

### Default Port and Context Path
```
server.port=9092
server.servlet.context-path=/preregistration/v1
```
#### Url 
```https://{dns-name}:9092/preregistration/v1/applications/swagger-ui.html```

[Application Properties](https://github.com/mosip/mosip/blob/master/config/pre-registration-dev.properties)

The following are the Api name use in this service.

1. POST /applications - This Api is used to create new pre-registration by demographic details by providing the demographic details in request body.

2. PUT /applications/{preRegistrationId} - This Api is use to update the pre-registration details by providing pre-registration id in the path parameter and updated demographic details in request body.

3. GET /applications/{preRegistrationId} - This Api is use to 
retrieve demographic details by providing the pre-registration id.

4. GET /applications - This Api is use to fetch all the applications created by user.

5. GET /applications/status/{preRegistrationId} - This Api is use to fetch the status of the application by providing the pre-registration id.

6. DELETE /applications/{preRegistrationId} - This Api is use to delete the Individual applicant and documents associated with the provided Pre-RegistrationId.

Following are the APIs which are using internally in this service.

1. PUT /applications/status/{preRegistrationId} - This Api is use to update the status of the application by providing the pre-registration id.

2. POST /applications/updatedTime - This Api is use to fetch updated Date Time for List of Pre-Registration Id.
