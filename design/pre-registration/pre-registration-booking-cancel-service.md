
# Approach for  Cancel Booking

**Background**
- Exposing the REST API to cancel an booking appointment for a citizen.

The target users are -
   - Pre-Registration portal

The key requirements are -

-   Create the API to cancel an appointment for the selectd Registration center.

-   Cancel Booking appointment should have the detail of:

    -   Pre-Registration Id

    -   Registration center Id

    -   DateTime

    -   Time slot

- This ablove details need to update in a pre-registration database. Once updating the data is completed then system need to update the cache server.

- A key should get generate with Registration center id, date and a time slot. with this generated key cache should get update the value by +1.

-  Once the cache get updated successfully then an status need to update in the applicant_demographic table to "Canceled".

The key non-functional requirements are

-   Log the each state of the pre-registration creation:

    -   As a security measures the Pre-Id or applicant information should
        not be logged.

-   Audit :

    -   Each state of the Pre-Registration cancel booking should be stored into the DB  for audit purpose.

    -   Pre-reg Id and important detail of the applicant should not be audited.

-   Exception :

    -   Any exception occurred during the cancel registration booking, the same will be reported to the user with the user understandable exception.

**Solution**

**Booking an appointment :**

-   Create a REST API as '/cancelBooking' accept the Pre-Registration Id, Registration Center id, booking date time and slot from the pre-registration application portal.

-   Generate an cache key using Registration center id, booking date time and slot.

-   Once the key generated successfully update the cache using the generated key by incrementing the value by 1.

-   Updating the booking data in the pre-registration booking table. after updating the booking data, system need to update the main table (applicant_demographic) with the status code "Canceled"

-   Audit the exception/start/exit of the each stages of the Pre-registration Cancel Booking mechanism using AuditManager component.

**Class Diagram**

![pre-registration cancel booking service](_class_diagram/pre-registration-cancel-booking-service-classDiagram.png)

**Sequence Diagram**

![pre-registration cancel booking service](_sequence_diagram/pre-registration-cancel-booking.png)

**Success / Error Code** 

 While processing the Registration client booking if there is any error or successfully then send the respective success or error code to the UI from API layer as  Response object.

  Code   |       Type  | Message|
-----|----------|-------------|
  PRG_PAM_RCI-001 |  Error   |   User has not been selected any time slot.
  PRG_PAM_RCI_002  | Error   |   Appointment time slot is already booked.
  PRG_PAM_RCI_003  | Error   |   Appointment time slot is already canceled.
  PRG_PAM_RCI_004  | Error   |   Appointment can not be canceled.


**Dependency Modules**

Component Name | Module Name | Description | 
-----|----------|-------------|
  Audit Manager     |   Kernel        |    To audit the process while creating the pre-registation.
  Exception Manager  |  Kernel     |       To prepare the user defined exception and render to the user.
  Log        |          Kernel         |   To log the process.
  Database Access   |    Kernel      |      To get the database connectivity


**User Story References**

  **User Story No.** |  **Reference Link** |
  -----|----------|
  **MOS-665**      |     <https://mosipid.atlassian.net/browse/MOS-665>