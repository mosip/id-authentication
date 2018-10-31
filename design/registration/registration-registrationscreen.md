Technical Design for the Registration Screen

Table of Contents:

I.  Functional Background

    a.  Target users

    b.  Key requirements

    c.  Non-functional requirements

II. Technical Approach

    d.  Service

    e.  UI

    f.  Classes

    g.  Class Diagram

    h.  Sequence Diagram

III. Request and Response

**[Functional Background]{.underline}**
=======================================

On login successful, we show off "New Registration" screen. The
Registration screen is of two widgets based on User language and Local
language. Both of which have same set of fields with different
languages. Once we captured the information on Registration UI we stuff
to a DTO and hit the service, wherein we mapper maps it to Registration
DTO for packet save and other operations. An alert message (say) "Data
captured successfully" on successful saving of data or an error message
has to be communicated back to client.

List of **Data** on Registration screen

-   Demographic data

-   Biometric data

-   Photographic data

-   Documents

-   Introducer

-   Exception

List of **Fields** to be taken care for the each section

-   Demographic data

    -   Full name

    -   Date of Birth/Age

    -   Local Administrative Authority

    -   Mobile Number

    -   Email ID

    -   CNIE Number / PIN Number

    -   Gender -- Male / Female/ Others\... (Dropdown)

    -   Address -- 1. Registration center address 2. Individual address

        -   3 fields -- with Address Line 1,2,3 text field

        -   Extra fields for the address is

            1.  Address L1

            2.  Address L2

            3.  Address L3

            4.  Region

            5.  Province

            6.  City

    -   Parent / Child -- If age is \< 5 years -- then Text field for
        Parent Name and Parent UIN has to appear.

        -   Parent/Guardian Full Name

        -   Parent/Guardian RID/UIN

-   Biometric data

    -   Left/Right thumb

    -   Left/Right Palm

    -   Iris - Left/Right eye

-   Introducer

    -   Parent

    -   HOF

    -   Introducer

-   Documents

    -   POA -- Proof of address

    -   POI -- Proof of Identity

    -   POR -- Proof of reference

The **target users** are

-   Individual

-   Registration officer

-   Registration Supervisor

The key **requirements** are

-   On successful login, show the "Registration Screen"

-   Build UI with , WRT the requirement

-   Registration page is divided into two

    -   User language

    -   Local language

-   Capture the data from Registration UI

-   Map data into Registration DTO

-   Send an Alert message (say) "Data captured successfully" or an error
    message.

<!-- -->

-   Registration DTO for packet save and other operations.

The key **non-functional requirements** are

-   Security:

    -   Should not store any sensitive information as plain text
        information.

    -   The data which resides in the data-base should be in encrypted
        format.

-   Network:

    -   Should able to communicate to the configured URL with proper
        authentication.

    -   The http read timeout parameter to be explicitly set, if client
        unable to connect to the REST service.

    -   Connectivity should happen through SSL mode. The respective key
        to be loaded during the call.

-   Authentication:

    -   While connecting to the server, user authentication is required
        to authenticate by providing the valid credentials.

    -   Invoke the Authenticate service to get the 'JWT token' and pass
        it along with the request to authenticate the request by the
        server.

 **[Technical Approach]{.underline}**
=====================================

The key solution considerations are --

**Service **

-   Create **RegistrationService** and create DTO for the same.

    -   Get the data from Registration UI

    -   Map data into Registration DTO

    -   Send an Alert message (say) "Data captured successfully" or an
        error message

-   Handle exceptions in using custom Exception handler and send correct
    response to client.

> **UI **

-   Build the screen based on the UI requirement, as the fields
    mentioned above.

-   Create the proper alert success/error to intimate the user.

> **Apply the below common criteria**

-   Audit

-   Log

-   Java Documentation

-   Junit

**Classes**:

**Controller**: RegistrationController

**Service**: RegistrationService

**DTO**: RegistrationDTO / UIDTO

Class Diagram:

<https://github.com/mosip/mosip/blob/DEV/design/registration/_images/_class_diagram/registration-registrationscreen-classDiagram.png>

Sequence Diagram:

<https://github.com/mosip/mosip/blob/DEV/design/registration/_images/_sequence_diagram/registration-registrationscreen-sequenceDiagram.png>
