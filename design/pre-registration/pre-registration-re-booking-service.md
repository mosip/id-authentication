
# Approach for  Re Booking

**Background**
- Exposing the REST API to re-book an appointment for a citizen with an different time slot or with a different registration client.

The target users are -
   - Pre-Registration portal

The key requirements are -

-   Create the API to re-book an appointment for the selectd Registration center and time slot.

-   Re-Booking appointment should have the detail of:

    -   Pre-Registration Id

    -   Registration center Id

    -   DateTime

    -   Time slot

- System need to fetch the previous booking data from the database with pre-registration id, generate an cache key with registration center id, date and  time slot.

- Once the cache key successfully generated upadte the cache server value with the generated key by -1.

- With new booking details system need to update in a booking table of pre-registration database with pre-registration id. Once updating the data is completed then system need to update the cache server.

- A key should get generate with new Registration center id, date and a time slot. with this generated key cache should get update the value by +1.

-  Once the cache get updated successfully then an status need to update in the applicant_demographic table to "Re-Booked".

The key non-functional requirements are

-   Log the each state of the pre-registration creation:

    -   As a security measures the Pre-Id or applicant information should
        not be logged.

-   Audit :

    -   Each state of the Pre-Registration re booking should be stored into the DB  for audit purpose.

    -   Pre-reg Id and important detail of the applicant should not be audited.

-   Exception :

    -   Any exception occurred during the re booking, the same will be reported to the user with the user understandable exception.

**Solution**

**Booking an appointment :**

-   Create a REST API as '/reBooking' accept the Pre-Registration Id, Registration Center id, booking date time and slot from the pre-registration application portal.

- System need to fetch the previous booking data from the database with pre-registration id, generate an cache key with registration center id, date and  time slot.

-   Generate an cache key using Registration center id, booking date time and slot.

-   Once the key generated successfully update the cache server value using the generated key by -1.

- With new booking details system need to update in a booking table of pre-registration database with pre-registration id. Once updating the data is completed then system need to update the cache server.

- A key should get generate with new Registration center id, date and a time slot. with this generated key cache should get update the value by +1.

-  Once the cache get updated successfully then an status need to update in the applicant_demographic table to "Re-Booked".

-   Audit the exception/start/exit of the each stages of the Pre-registration Re Booking mechanism using AuditManager component.

**Class Diagram**

![pre-registration re-booking service](_images/_class_diagram/pre-registration-re-booking.png)

**Sequence Diagram**

![pre-registration re-booking service](_images/_sequence_diagram/pre-registration-re-booking.png)

**Success / Error Code** 

 While processing the re booking if there is any error or successfully then send the respective success or error code to the UI from API layer as  Response object.

  Code   |       Type  | Message|
-----|----------|-------------|
  PRG_PAM_RCI-001 |  Error   |   User has not been selected any time slot.
  PRG_PAM_RCI_002  | Error   |   Appointment time slot is already booked.
  PRG_PAM_RCI_003  | Error   |   Appointment time slot is already canceled.
  PRG_PAM_RCI_004  | Error   |   Appointment can not be canceled.
  PRG_PAM_RCI_005  | Error   |   Appointment Rebooking cannot be done.


**Dependency Modules**

Component Name | Module Name | Description | 
-----|----------|-------------|
  Audit Manager     |   Kernel        |    To audit the process while creating the pre-registation.
  Exception Manager  |  Kernel     |       To prepare the user defined exception and render to the user.
  Log        |          Kernel         |   To log the process.
  Database Access   |    Kernel      |      To get the database connectivity.


**User Story References**

  **User Story No.** |  **Reference Link** |
  -----|----------|
  **MOS-977**      |     <https://mosipid.atlassian.net/browse/MOS-977>