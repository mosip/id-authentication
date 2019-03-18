## kernel-applicanttype-service


[Background & Design](../../docs/design/kernel/kernel-applicanttype.md)


Default Port and Context Path

```
server.port=8094
server.servlet.path=/applicanttype

```

localhost:8094/applicanttype/swagger-ui.html


**Application Properties**

```
mosip.kernel.applicant.type.age.limit = 5

```


**The inputs which have to be provided are:**
We need to provide some attributes and its values : 

individualTypeCode: mandatory
dateofbirth: mandatory
genderCode: mandatory
biometricAvailable: optional


**The response will be true is audit request is successful, otherwise false** 

**Exceptions to be handled while using this functionality:**

1. AuditHandlerException ("KER-AUD-001", "Invalid Audit Request. Required parameters must be present")
2. InvalidFormatException ("KER-AUD-002", "Audit Request format is invalid");

**Usage Sample**
  
  *Request*
  
  ```
{
  "id": "getapplicanttype.code",
  "metadata": {},
  "request": {
    "attributes": [
      {
        "attribute": "individualTypeCode",
        "value": "FR"
      },
      {
        "attribute": "dateofbirth",
        "value": "2012-03-08T11:46:12.640Z"
      },
      {
        "attribute": "genderCode",
        "value": "MLE"
      },
      {
        "attribute": "biometricAvailable",
        "value": false
      }
    ]
  },
  "requesttime": "2012-03-08T11:46:12.640Z",
  "version": "V1.0"
}


  ```
  
  *Response*
  
 HTTP Status: 200 OK
  
  ```
{
  "id": "getapplicanttype.code",
  "version": "V1.0",
  "responsetime": "2019-03-18T09:37:20.401Z",
  "metadata": null,
  "response": {
    "response": {
      "applicantTypeCode": "002"
    }
  },
  "errors": null
}
  ```
  
  



