Technical Design for the User Mapping to Registration

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

On login successful of the user, we show off "New Registration" screen.
There exists the "Menu", which lists the options/ features. On clicking
the "User Mapping" link, it should land up in "User Mapping Screen".
This consist of list of users available at that registration center. A
table with User name, User ID, User Role is shown off. On clicking the
particular user, his/her details should appear below and the logged-in
user gets to map the selected user from table to that Station ID.

-   Super admin -- can map or unmap himself or other RO / RS

-   RO/RS -- can map or unmap other RO / RS

-   One Station ID can be mapped to multiple users

The **target users** are

-   Individual

-   Registration officer

-   Registration Supervisor

The key **requirements** are

-   On successful login, show the "Registration Screen"

-   Click on "User Mapping" Link in menu list

-   Table with list of users at that available at that registration
    center should appear

-   This table consist of the below fields

    -   User name

    -   User ID

    -   User role

-   On clicking the particular row for a user -- That user details
    should be displayed below the table

-   The logged-in user (SA / RO / RS) gets to make that user active or
    inactive i.e., mapped to that Station ID or deactivated from that
    Station ID respectively.

    -   Super admin -- can map or unmap himself or other RO / RS

    -   RO/RS -- can map or unmap other RO / RS

    -   One Station ID can be mapped to multiple users

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

-   On clicking the "User Mapping" list in menu tab on Registration
    screen, it should hit the service to fetch the user details like
    name, ID and role from DB.

-   Create **UserMappingService** and create DTO for the same.

    -   When this service is triggered for fetch() functionality - gets
        the user details

    -   It hits DAO and repository to fetch **list\<users\>**

    -   When a particular user is selected and made active or inactive
        then on saving it hits save() functionality -- maps / Un-maps
        the user from a Station ID

    -   It hits DAO and repository to map / um-map the user to a Station
        ID

    -   Send an Alert message (say) "User mapped successfully" or an
        error message

-   Handle exceptions in using custom Exception handler and send correct
    response to client.

> **UI **

-   Based on validation across the POJO class from the
    UserMappingService, build the UI screen

-   Create the proper alert success/error to intimate the user.

> **Apply the below common criteria**

-   Audit

-   Log

-   Java Documentation

-   Junit

**Classes**:

**UI**: UserMappingScreen

**Controller**: UserMappingController

**Service**: UserMappingService

**DAO**: UserMappingDAO

**Repository:** UserMappingRepository

**DTO**: UserMappingDTO

**DB scripts:**

Need DB script.zip

Class Diagram:

<https://github.com/mosip/mosip/blob/DEV/design/registration/_images/_class_diagram/registration-usermapping-classDiagram.png>

Sequence Diagram:

<https://github.com/mosip/mosip/blob/DEV/design/registration/_images/_sequence_diagram/registration-usermapping-sequenceDiagram.png>
