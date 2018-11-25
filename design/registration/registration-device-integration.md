**Design - Device Integration **

**Functional Background**

The functional scope of device integration is to detect the device and get the details of the same corresponding to particular registration center. This can be invoked at any point of time through an integrator which in turns connect to the device to get its details. Any type of device is pluggable to the application.
GPS - We are bound to get the data like the device’s latitude, longitude and distance from the registration center.
Camera – it will be used to capture the applicant’s photo along with the exception detail. 
This includes following devices:

-	GPS
-	Biometric devices (Fingerprint and Iris capture)
-	Scanner
-	Printer
-	Camera

The target users are
-	Super Admin
-	Registration Supervisor
-	Registration officer

The key requirements are
GPS
-	Use GPS to capture the Registration client machine longitude and latitude.
-	Based on the longitude and latitude calculate the distance from the Registration center.
Photo
-	During new registration process, as part of demographic detail capture the applicant’s photo.
-	Allow exception photo capture only if an exception has been marked.
-	Captured image should be displayed for preview.
-	Calculate the quality of image and match it against the pre-defined threshold value.
-	Allow user to capture the images multiple time and display the highest quality of image.
Finger Print
-	Allow the user to login to the application using any one of their finger print.
-	Authenticate the registration process by capturing the Registration officer finger print.
-	EOD Process – before approve/ reject the registration packet, capture the officer authentication.
-	Capture the applicant fingerprint image.
-	De-duplicate validation against the list of users or supervisor enrolled in the machine.
The key non-functional requirements are
-	Modularity: 
o	Separate Api and the respective façade should be provide to the client application to interact with the devices.
o	Device related code shouldn’t be implemented inside the functional specific controller or service class.


 **Solution - GPS**

The integration with the GPS device happening through the respective serial port [Com1..N]. Based on the user connected port the serial port number will vary. During runtime the program will scan across the port and identify the GPS related port and communicate with the same.
-	Create IGPSIntegrator, IGPSConnector, GPSUtil classes.
GPSIntegrator – 
	It communicates between the client class and GPSConnector class. 
	Based on the input it identifies the required Device specific connector class
	and invoke getGPSData() to receive the data from GPS.
IGPSConnector – [GPSBU353S4Connector]
	This interface should be implemented by the device specific implementation class.
	The device specific implementation class should connects to the GPS device through 
	Serial com port and wait for some specified interval to receive the data.
	The received will contain the raw GPS data [NEMA – standard format], 
	which will contain the required information. The same will be sent to the invoking class.
GPSUtil –
	This will have method to parse the GPS standard format [NEMA] data and provide 
	the required longitude and latitude as a String object if we get the good signal from GPS device.
	If the signal is week, then the message [GPRMC] will have the respective indication [V].
	Based on the signal the data will be provided to the application.
 
-	Handle exceptions in using custom Exception handler and send correct response to client.

**Class and Sequence Diagram **

 **Solution - Photo Capture**
•	Webcam-capture –> open source library should be used to capture the data from webcam device.
•	The respective controller, service and interfaces should be created before 
	invoking the api method from ‘Webcam-capture’ lib.
•	Integrate with the webcam api through ‘WebCamDeviceImpl’ class, 
	where the device open, capture and close should be implemented. 
	When ‘open’ method is invoked, need to close if there is any device already been opened.
•	This functionality can be used for both capture applicant and exception photo.
•	If any error occurred, the same to be notified to the user.

	Classes: 		
		Controller: WebCameraController.java
		Service: PhotoCaptureService.java
		API Integration: WebCamDeviceImpl.java

**Sequence and Class Diagram **

 **Solution - Finger Print**
	BIOAuthentication.FXML – where ever authentication required, this component 
		should be rendered to capture the user authentication by passing the required BIO type as input.
	BIOAuthenticationController – 
		It captures the data from UI object and render value to the UI 
		object and invoke the FP façade and validator classes to complete 
		the FP related action triggered from UI component.
		FingerprintValidator – 
	It does validation of finger print minutia.
	boolean validateOneToManyFP(String userId, String userInputMinutia)
	o	Fetch user id specific minutia alone for all FPs from db table.
	o	Then compare against the user input single FP minutia with minutia 
		fetched from db using façade and FP api.
	o	return true – when match found.
	o	return false – when match not found.
	
		boolean validateManyToManyFP(String userId, DTO <UserFingerPrintDTO>)
	o	Fetch all the active user id for a particular machine id from db table.
	o	Fetch user id specific minutia alone for all FPs from db table.
	o	Run through all the FP of an userInputDTO and match against
		all FPs of a particular user id fetched from DB.
		Validate one to one FP mapping like: input thumb FP to db thumb fb. 
	o	The above step should be completed for all the fetched user id from db.
	o	Invoke the façade to match the minutia between two FPs.
	o	return true – when match found break the loop.
	o	return false – when match not found.
	FingerprintFacade	- it acts between client and FP interface to invoke the 
		right vendor specific class and perform the client required operation in 
		vendor specific class. Client doesn’t aware of which vendor specific method to be invoked. 
		That will be taken care in this Façade class.
	FingerprintProvider	- Interface which contains all the required 
		functionality that needs to be implemented by the vendor specific classes. 
		The client classes will work on this interface rather using the 
		implementation [vendor specific] classes directly. 
	MantraFingerprintProvider – Vendor specific class to interact with their specific
		SDK classes to communicate with the device drivers and does the required functionality.
	FP Device Driver	- which is provided by the third party device specific vendor 
		to capture the finger print and transfer to the invoking client application.
		It interface between client application and device.
	 
**Sequence and Class Diagram**


