## Registration:
Registration module provides a desktop application for Registration Officers/Supervisors to register an Individual in MOSIP, by capturing demographic and biometric details of an Individual.
Registration module uses data captured by `Pre-Registration` module if an Individual has booked for an appointment in the Registration Centre.
Registration Processor processes the data captured by Registration module to complete the registration process.

**Configuration**
Configurations used for ID Repo are available in [mosip-config](https://github.com/mosip/mosip-config)

### Build
Below command should be run in the parent project **authentication**
`mvn clean install`