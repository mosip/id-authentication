# Pre-Registration-batchjob-service:

[Background & Design](https://github.com/mosip/mosip/blob/SPRINT11_PREREG_TEAM_BRANCH/docs/design/pre-registration/pre-registration-batch-jobs-service.md)

This service is used by Pre-Registration portal to update status as expired or consumed and sync the master data for availability.

[Api Documentation](https://github.com/mosip/mosip/wiki/Pre-Registration-Services#batchjob-service-private)


### Default Port and Context Path
```
server.port=9096
server.servlet.context-path=/preregistration/v1
```
#### Url 
```https://{dns-name}:9096/preregistration/v1/batch/swagger-ui.html```

[Application Properties](https://github.com/mosip/mosip/blob/master/config/pre-registration-dev.properties)

The following are the Api name use in this service.

1. PUT batch/expiredStatus - This request is used to update the status from *booked* to *expired* in the database ,once the appointment date is expired .

2. PUT batch/consumedStatus - This request is used to update the status as *consumed* for all pre-Registration ids given by registration processor.

3. GET /appointment/availability/sync - This request is used to synchronize booking slots availability table with master data.
