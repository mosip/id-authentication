# Pre-Registration-booking-service:

[Background & Design](https://github.com/mosip/mosip/blob/SPRINT11_PREREG_TEAM_BRANCH/docs/design/pre-registration/pre-registration-booking-service.md)

This service is used by Pre-Registration portal for booking an appointment by taking users basic appointment details.

[Api Documentation](https://github.com/mosip/mosip/wiki/Pre-Registration-Services#booking-service-public)

### Default Port and Context Path
```
server.port=9095
server.servlet.context-path=/preregistration/v1
```
#### Url 
```https://{dns-name}:9095/preregistration/v1/appointment/swagger-ui.html```

[Application Properties](https://github.com/mosip/mosip/blob/master/config/pre-registration-dev.properties)

The following are the Api name use in this service.

1. POST /appointment/{preRegistrationId} - This Api is use to book an appointment for the provided pre-registration Id.

2. POST /appointment - This post Api is use for multiple booking by providing booking details in the request body.

3. PUT /appointment/{preRegistrationId} - This Api is use to cancel an appointment for given pre-registration Id.

4. GET /appointment/{preRegistrationId} - This get api is use for fetch the appointment details for provided pre-registration Id.

5. GET /appointment/availability/{registrationCenterId} - This Api is used to retrieve all the availability details for provided registration center Id.

6. GET /appointment/preRegistrationId/{registrationCenterId}?from_date=:date&to_date=:date - This request is used to retrieve all pre-registration ids available for specified registration center and date range.

Following are the APIs which are using internally in this service.

1. DELETE /appointment - This api is use to delete the Individual booking associated with the pre-registration Id.