
**Design -- Lost UIN**
======================

**Background**

On login successful, the user can select the "Lost UIN" option in the
screen if any individual requested for the same. The key requirement and
respective technical design is covered below.

The **target users** are

-   Individual
-   Registration officer
-   Registration Supervisor

The key **requirements** are

-   The Registration Officer navigates to the Lost UIN page.
-   User enters demographic data of the individual who has lost his/her UIN.
-   User uploads supporting documents.
-   User marks biometric exceptions if any,
-   User captures all fingerprints and irises.
-   User captures face photograph, and exception photograph if applicable.
-   User views a preview of details captured.
-   User performs operator authentication by providing credentials in the configured mode.
-   User requests Supervisor to provide credentials to authenticate if biometric exceptions were marked.
-   User views acknowledgement of Lost UIN request with a Registration ID assigned to it.
-   An SMS and email confirmation is sent to the individual.

The key **non-functional requirements** are

-   Security:
    -   We should not store the RO/RS plain text credentials or any
        sensitive information.

**Solution**

1.  Get the required configuration to display for the Lost UIN screen
    from the Global Configuration Parameter table.

2.  Create the controller for the **RegistrtaionEditController** to
    display the list of the required editable section on the screen.
    \[Provide submit method to capture the details and maintains in the
    Registration DTO\].

3.  Display the Registration Screen based on the selection of the
    editable fields.

4.  After enter all the fields from the screen, we will call the Packet
    Handler service to do the rest of the process.

5.  The same procedure for this follows the registration procedure only
    the specific fields which are editable should be captured along the
    packet data and mention the 'applicationType' as 'Lost UIN'.

6.  The **RegsitrationValidator** should validate the fields against
    the selection.

7.  The **RegistrtaionController** also should displays based on the
    editable sections.

8.  Finally the desired data will be persisted as part of the
    Registration and Registration Transaction tables.

Note:

    Write a classes as per the attached diagram for Lost UIN which  
    would associate all the controller classes created for  
    New Registration to reuse the functionality. 

    As part of the packet Meta we need to provide which packet it is and the 
    UIN number also we need to provide. 

**Class and Sequence Diagram:**

![Lost UIN Class Diagram](_images/registration-lost-uin.png)

