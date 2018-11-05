
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

-   Expose the API to Registration Processer will provide with the list of Pre-Registration IDs for which it wants to update Pre-Registration status.

Non Functional Scope
--------------------

-   Log the each state of the reverse data sync:

    -   As a security measures the Pre-Id or registration processor information should not be logged.

-   Audit :

    -   Registartion processor ID, Transaction ID, Timestamp should be stored into the DB for audit purpose.

    -   Pre-reg Id and important detail of the applicant should not be audited.

-   Exception :

    -   Any exception occurred during the pre-registration reverse data sync, the same will be reported to the registartion processor in a understandable exception.


Technical Approach
==================

Design Detail
-------------

> The detailed technical process for pre-registartion creation is
> provided below:
>
> **Reterive all pre-registration Ids :**

-   Create a REST API as '/storeConsumedPreRegistrationIds' accept the Data Sync JSON object  from the registration-processor.

- The Registration Processor will provide the List of Pre-Registration IDs received by it(from Registration Client). 

- The System will generate a Transaction ID and store all the Pre-Registration ids in table.

-  The System will calculate the count of the Pre-Registration IDs being sent.

-  Pre-Registration will receive the List of Pre-Registration Ids and will send an Acknowledgement of the Receipt ("need to be check BA(Vyas)")

-   Audit the exception/start/exit of the each stages of the reverse data sync mechanism using AuditManager component.

Class Diagram
-------------

> **https://github.com/mosip/mosip/blob/DEV/design/pre-registration/_images/_class_diagram/reverse-data-sync-service-classDiagram.png**

Sequence Diagram
----------------
>**https://github.com/mosip/mosip/blob/DEV/design/registration/_images/_sequence_diagram/reverse-data-sync-service.png**


Success / Error Code 
=====================

> While processing the Pre-Registration if there is any error or successfully
> then send the respective success or error code to the UI from API layer as  Response object.

  Code   |       Type  | Message|
-----|----------|-------------|
  0000      |             Success |   Packet Successfully created


Dependency Modules
==================
Component Name | Module Name | Description | 
-----|----------|-------------|
  Audit Manager     |   Kernel        |    To audit the process while reverse data sync.
  Exception Manager  |  Kernel     |       To prepare the user defined exception and render to the user.
  Log        |          Kernel         |   To log the process.
  Database Access   |    Kernel      |      To get the database connectivity


User Story References
=====================

  **User Story No.**   **Reference Link**
  -------------------- -----------------------------------------------
  **MOS-1999**           <https://mosipid.atlassian.net/browse/MOS-1999>
