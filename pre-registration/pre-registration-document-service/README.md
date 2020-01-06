# Pre-Registration-document-service:
[Background & Design](https://github.com/mosip/mosip/blob/SPRINT11_PREREG_TEAM_BRANCH/docs/design/pre-registration/pre-registration-document-service.md)

This service enables Pre-Registration portal to request for uploading the document for a particular pre-registration.

[Api Documentation](https://github.com/mosip/mosip/wiki/Pre-Registration-Services#document-service-public)

### Default Port and Context Path
```
server.port=9093
server.servlet.context-path=/preregistration/v1
```
#### Url 
```https://{dns-name}:9093/preregistration/v1/documents/swagger-ui.html```

[Application Properties](https://github.com/mosip/mosip/blob/master/config/pre-registration-dev.properties)

The following are the Api name use in this service.
1. POST /documents/{preRegistrationId} - This Api is use to upload the document for a pre-registration Id by providing file and file details.

2. PUT /documents/{preRegistrationId} - This Api is use to copy the document from source pre-registration Id in request parameter to destination pre-registration Id provided in the path variable.

3. GET /documents/preregistration/{preRegistrationId} - This get api is use to fetch list of meta data of document for provided pre-registration Id.

4. GET /documents/{documentId}?preRegistrationId=:preRegistrationId - This get api is use to fetch the content of the document for document Id.

5. DELETE /documents/{documentId}?preRegistrationId=:preRegistrationId  - This Api is use to delete the document for provide document Id.

6. DELETE /documents/preregistration/{preRegistrationId} - This Api is use to delete all the document associated with the provided pre-registration Id.
