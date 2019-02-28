# Applicant Type

#### Background

There can be multiple applicant types in the MOSIP system. An applicant type is the combination of Individual type, Gender and Age group. There are only few fixed Applicant types will be available for the 1.0 version of MOSIP. Once the module receives the criteria for an Applicant type, the code is derived in the Java code. Then this code is used to get the "Document category" and the "Document type" associated with the "Document category". The "Document category" and the "Document type" is maintained in the database.

#### Solution



**The key solution considerations are**


- There are 2 services in this module. One will return back the "Applicant type" based on the Individual type, Gender and Age group. The other service will return the Document category and the Document type for the Applicant type. 

- The Applicant type definition is there in the Java code. 

- The Applicant type and the Document mapping is there in the database. 

**Module diagram**



![Module Diagram](_images/kernel-applicanttype.jpg)



## Implementation


**kernel-applicanttype** [README](../../kernel/kernel-applicanttype/README.md)
