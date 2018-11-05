
# MOSIP Pre Registration 

![Pre Registration](_images/MOSIP_Pre-Registration.png){width="8.570833333333333in"
height="3.1166666666666667in"}a

Copyright Information {#copyright-information .TOCHeading}
=====================

This document is the exclusive property of
\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_; the recipient agrees that they may not
copy, transmit, use or disclose the confidential and proprietary
information in this document by any means without the expressed and
written consent of Mindtree. By accepting a copy, the recipient agrees
to adhere to these conditions to the confidentiality of
\_\_\_\_\_\_\_\_\_\_\_\_\_ practices and procedures; and to use these
documents solely for responding to \_\_\_\_\_\_\_\_\_\_\_\_\_\_
operations methodology.

Revision History {#revision-history .TOCHeading}
================

  **Ver**   **Change Description**   **Sections**       **Date**      **Author**              **Reviewer**
  --------- ------------------------ ------------------ ------------- ----------------------- --------------
  0.1       First Draft              All                24-Aug-18     Ravi C Balaji, Rudra Prasad Tripathy


References {#references .TOCHeading}
==========

+------+---------------+------+----------+
| No   | Document Name | Ver. | Location |
+======+===============+======+==========+
| 1.   |               |      |          |
+------+---------------+------+----------+
| 2.   |               |      |          |
+------+---------------+------+----------+
| 3.   |               |      |          |
+------+---------------+------+----------+
| 4.   |               |      |          |
+------+---------------+------+----------+

Glossary {#glossary .TOCHeading}
========

  **Terminology**   **Definition**                                        **Remarks**
  ----------------- ----------------------------------------------------- ------------------

**
**

Table of Contents {#table-of-contents .TOCHeading}
=================

[Copyright Information 2](#copyright-information)

[Revision History 2](#revision-history)

[References 2](#references)

[Glossary 2](#glossary)

[Table of Contents 3](#_Toc524000490)

[Part A: Background 5](#part-a-background)

[1 Introduction 5](#introduction)

[1.1 Context 5](#context)

[1.2 Purpose of this document 5](#purpose-of-this-document)

[2 Scope 5](#scope)

[2.1 Functional Scope 5](#functional-scope)

[2.2 Non Functional Scope 6](#non-functional-scope)

[2.3 Out of Scope 7](#out-of-scope)

[3 Technical Approach 7](#technical-approach)

[3.1 Design Detail 7](#design-detail)

[3.2 Class Diagram 11](#class-diagram)

[3.3 Sequence Diagram 11](#sequence-diagram)

[4 Success / Error Code 12](#success-error-code)

[5 Dependency Modules 12](#dependency-modules)

[6 User Story References 13](#user-story-references)


Part A: Background {#part-a-background .PartHeader}
==================

Introduction
============

Context
-------

MOSIP is developed as an open source framework project. The java
standard design principles will be followed to design the component.

Purpose of this document
------------------------

This document provides the low level technical design approach of a
particular functionality in MOSIP Platform. It details out the in depth
technical area of a particular scope.

Scope
=====

Functional Scope
----------------

-   Expose the API to update the demographic details and supporting document of the citizen for a pre-registration, where the data captured in the pre-registration portal should be update in database.

-   Demographic form should have the detail of:

    -   Name

    -   Age/DOB

    -   Gender Type

    -   Address

    -   Location details

    -   Contatct details

    -   CINE / PIN number.

- Document should have following type:
     - Proof of Identity

     - Proof of Address

     - Proof of DOB

- Once the demographic data is updated an REST API should get called and get updated in the database.

-  Only "Pending-Appointment" status pre-registation form should allow to update the demographic information and documents.

-  The API should return the success / failure status code along with
    the respective message.

- To update document, it should be a different microservice as per the technical architecture. 

- For each document update it should hit the rest api to delete the existing document in database and store the new document as per the document category.

- Integrating with Pre-Registartion UI client for pre-registration form(demoragphic and document).

Non Functional Scope
--------------------

-   Security :

    -  The Pre-Registartion JSON form should be encrypt and stored in the     pre-registartion database.

-   Log the each state of the pre-registration creation:

    -   As a security measures the Pre-Id or applicant information should
        not be logged.

-   Audit :

    -   Each state of the Pre-Registration creation should be stored into the DB
        for audit purpose.

    -   Pre-reg Id and important detail of the applicant should not be audited.

-   Exception :

    -   Any exception occurred during the pre-registration, the same will
        be reported to the user with the user understandable exception.


Out of Scope
------------

- Key management for encrypting JSON.

- Scanning the uploaded document for virus.

- Document quality checking to meet the threshold acceptence level.

- Encrypting the uploaded document and store it in database.

Technical Approach
==================

Design Detail
-------------

> The detailed technical process for pre-registartion creation is
> provided below:
>
> **Update existing pre-registration :**

-   Create a REST API as '/applications' accept the ID Definition JSON object (#id-definition-structure) from the pre-registration application portal.

- Once all the mandatory fields enter then only "Save" button get enable to call the REST API to create. 

-   Validate the request object against the ID Definition schema and render the respective error message.

-   If the provided request JSON object is valid then continue with the rest
    of the process.

-  Encrypte the requested JSON and store it in database with pre-registration id from the request object.

-   Audit the exception/start/exit of the each stages of the Pre-registration update mechanism using AuditManager component.


> **Update Document:**

-   Create a REST Service as '/documents' accept the JSON object (#document-json-structure) and a multipart file from the pre-registration application portal..

-   In the document JSON object it should contain the pre-registration id, document id, document type, document category, document format uploaded by and uploaded timestamp.

- 	Following operation need to be happen:

	Step1: Document Virus scanning, if successful go to next step otherwise throw an exception.{will be taken care in the integration user story}
	
	Step2: Document quality checking, if successful go to next step otherwise throw an exception.{will be taken care in the integration user story}
	
	Step3: Document encryption, if successful go to next step otherwise throw an exception.{will be taken care in the integration user story}
	
	Step4: update the document in document table along with the pre-registartion id and document id.

Class Diagram
-------------

> **https://github.com/mosip/mosip/blob/DEV/design/pre-registration/_images/_class_diagram/pre-registartion-application-update-classDiagram.png**

> **https://github.com/mosip/mosip/blob/DEV/design/pre-registration/_images/_class_diagram/pre-registartion-document-update-classDiagram.png**

Sequence Diagram
----------------
>**https://github.com/mosip/mosip/blob/DEV/design/registration/_images/_sequence_diagram/pre-registration-demographic-update.png**

>**https://github.com/mosip/mosip/blob/DEV/design/registration/_images/_sequence_diagram/pre-registration-document-update.png**

Success / Error Code 
=====================

> While processing the Pre-Registration if there is any error or successfully
> then send the respective success or error code to the UI from API layer as  Response object.

  Code   |       Type  | Message|
-----|----------|-------------|
  0000      |             Success |   Packet Successfully created
  PRG_PAM_APP_001  |  Error   |   Unable create the pre-registration.
  PRG_PAM_APP_002  |  Error   |   Registration table not accessible
  PRG_PAM_APP_003  |  Error   |   Delete operation not allowed
  PRG_PAM_APP_004  |  Error   |   Failed to delete the registration
  PRG_PAM_APP_005  |  Error   |   Unable to fetch the registartions
  PRG_PAM_APP_006  |  Error   |   Failed to update the demographic data.
  PRG-PAM_APP_007  |  Error   |   Blacklisted Word entered.
  PRG_PAM_DOC_001 |  Error   |   Document failed to upload
  PRG_PAM_DOC_002 |  Error   |   Document exceeding premitted size
  PRG_PAM_DOC_003 |  Error   |   Document type not supported
  PRG_PAM_DOC_004 |  Error   |   Document invalid format
  PRG_PAM_DOC_005 |  Error   |   Document failed in virus scan
  PRG_PAM_DOC_006 |  Error   |   Document failed in quality check
  PRG_PAM_DOC_007 | Error    |   Document failed in encryption
  PRG_PAM_DOC_008 | Error    |   Document failed in decryption
  PRG_PAM_DOC_009 | Error    |   Document not present
  PRG_PAM_DOC_010 |  Error   |   Document failed to delete
  PRG_PAM_DOC_011 | Error    |   Document failed to copy
  PRG_PAM_DOC_012 | Error    |   Document failed to update

Dependency Modules
==================
Component Name | Module Name | Description | 
-----|----------|-------------|
  Audit Manager     |   Kernel        |    To audit the process while creating the pre-registation.
  Exception Manager  |  Kernel     |       To prepare the user defined exception and render to the user.
  Log        |          Kernel         |   To log the process.
  JOSN Utility    |     Kernel       |     To validate the ID definition JSON object over ID Definition Schema
  Database Access   |    Kernel      |      To get the database connectivity


User Story References
=====================

  **User Story No.**   **Reference Link**
  -------------------- -----------------------------------------------
  **MOS-628**           <https://mosipid.atlassian.net/browse/MOS-628>
