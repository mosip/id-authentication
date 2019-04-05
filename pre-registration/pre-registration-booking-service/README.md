# Pre-Registration-booking-service:

[Background & Design](pre-registration-individual.md)

This service is used by Pre-Registration portal for booking an appointment by taking users basic appointment details.

#### Api Documentation

```
mvn javadoc:javadoc

```

####  POST Operation
#### Path -  `/preRegistrationId`
#### Summary

This request is used to book a registration center. If the appointment data exists for user, then it will cancel and update the new data else it will book a new appointment based upon the date and registration center selected.

#### Request Path Parameters

1. preRegsitrationId

#### Request body Parameters

1. Id
2. version
3. requestTime
4. request
5. request.registration_center_id
6. request.appointment_date	
7. request.time_slot_from	
8. request.time_slot_from	


#### Response

Returns a message saying that appointment booked successfully else returns a error message.


#### PUT Operation
#### Path -  `/preRegistrationId`
#### Summary

This request is used to reterive the appointement details for the specified pre-registration id, if exist update the availability for the slot and delete the record from the table and update the demographic record status "Pending_Appointment".

#### Response

Returns a message saying appointment cancelled successfully else returns a error message.

#### GET Operation
#### Path -  `appointment/:preRegistrationId`
#### Summary

This request is used to retrieve Pre-Registration appointment details by using pre-Registration id.

#### Request Path Parameters

1. preRegistrationId

#### Response

Returns registration_center_id, appointment_date , time_slot_from, time_slot_to as response if request is successful else gives a error message.

#### GET Operation
#### Path -  `appointment/:preRegistrationId`
#### Summary

This request is used to retrieve all appointment slots available for booking based on the specified registration center id.

#### Request Path Parameters

1. registrationCenterId

#### Response

On successful response it returns registrationCenterId , an array centerDetails containing date, timeslots, and a boolean variable holiday. In case of error returns appropriate error message.


#### GET Operation
#### Path -  `appointment/:registrationCenterId?`
#### Summary

This request is used to retrieve all pre-registration ids available for specified registration center and date range.

#### Request Path Parameters

1. registrationCenterId

#### Request query Parameters

1. fromDate
2. toDate

#### Response

On a successful response it returns a list of preRegistrationIds, on error it will give a message saying 'No available slots found for specified registration center with date range'.
