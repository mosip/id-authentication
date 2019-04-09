# Pre-Registration-batchjob-service:

[Background & Design](pre-registration-individual.md)

This service is used by Pre-Registration portal to update an expired pre registration id and consumed pre registration id and master data sync for availability.

#### Api Documentation

```
mvn javadoc:javadoc

```

#### PUT Operation
#### Path - `batch/expiredApplication`
#### Summary

This request is used to update status of appointment expired pre-registration ids to expired status in database.

#### Response

On success it returns a message saying 'Expired status updated successfully' else gives and error message.

#### PUT Operation
#### Path - `batch/consumedApplication`
#### Summary

This request is used to update the consumed status for all pre-Registration ids given by registration processor.

#### Response

On success it returns a message saying 'Consumed status updated successfully' else gives and error message.

#### PUT Operation
#### Path - `batch/availabilitySync`
#### Summary

This request is used to synchronize booking slots availability table with master data.

#### Response

On success it returns a message saying 'Master Data Sync is successful' else gives and error message.
