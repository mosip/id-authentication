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

We need to provide the Map<String,Object> and the key, value pairs are as follows :

individualTypeCode: mandatory

dateofbirth: mandatory

genderCode: mandatory

biometricAvailable: optional


*Valid values for above keys are as follows :*
 
individualTypeCode: FR,NFR

dateofbirth: must be in this pattern yyyy-MM-dd'T'HH:mm:ss.SSS'Z'

genderCode: MLE,FLE

biometricAvailable: true,false


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
  
  



