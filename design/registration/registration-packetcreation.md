Design - Packet Creation

**Background**
==================
	As part of the registration process,RO will capture all the details of the individual and the infomration called as packet should be stored in desired location. 
	The packet should be encrypted before saving into the location. This docomuent illustrtaes about the which information we are capturing as part of the packet and the encryption logic which we are using to encrypt the packet.


The **target users** are

-   Supervisor
-   Officer
-   Registrtaion Processor

The key **requirements** are

-   Expose the API to create the Enrollment packet, where the data
    captured in the ID Issuance client UI application should be stored.

-   Packet should have the detail of:

    -   Applicant -- Demo and Bio-metric

    -   Introducer -- Demo and Bio-metric

    -   HOF -- Demo and Bio-metric

    -   Officer / Supervisor -- Bio-Metric

    -   Enrollment Id.

    -   Packet Metadata.

    -   Enrollment Acknowledgement form.

-   Enrollment packet should be stored in encrypted format in the local
    hard disk.

-   Once the packet is created the same shouldn't be sent to the server
    until it is approved by the Enrollment Supervisor.

-   The API should return the success / failure status code along with
    the respective message.

The key **non-functional requirements** are

-   Security :

    -   The Enrollment packet shouldn't be decryptable other than
        Enrollment Server.

    -   Hash out the data -- the hash code of the data should be sent
        along with the packet.

    -   Along with the packet, the hash should also be using RSA public
        encrypted.

    -   Un-encrypted data shouldn't be stored in local hard disk during
        the creation of Enrollment packet.

    -   The IDIS application able to get the RSA public key from Core
        Kernel module.

    -   The IDIS able to generate the AES key seed \[256-bit\] by using
        the EO ID, MAC of the machine and timestamp.

-   Log the each state of the packet creation:

    -   As a security measures the UIN or customer information should
        not be logged.

-   Cache :

    -   Enrollment packet data shouldn't be cached and clear off all the
        data from the JVM local memory once the packet is created in
        local hard disk.

-   Audit :

    -   Each state of the packet creation should be stored into the DB
        for audit purpose.

    -   UIN and important detail of the customer should not be audited.

-   Exception :

    -   Any exception occurred during the packet creation the same will
        be reported to the user with the user understandable exception.

-   Data History :

    -   The IDIS able to authenticate by using the Core Kernal module.

    -   Maintain the Enrollment id, status and other high level info in
        the database table.

-   Configuration:

    -   Public Key -- the respective byte values will be present in the
        database table along with the expiry detail.

    -   Before initiating the enrollment process, the key expiry to be
        validated.


**Solution**

1. The detailed technical process for Enrollment packet creation is
   provided below:

**Packet API:**

-   Create a Java component as 'PacketHandler' with 'createPacket'
    method to accept the Defined [DTO
    structure](#entity-object-structure) from the client application.

-   Validate the request object against the Business rule and render the
    respective error message \[user defined\] to the invoking client
    application if any rule failed. \[this activity will be taken care
    in the next sprint\]

-   If the provided request object is valid then continue with the rest
    of the process.

-   Prepare the Zip object, which is to be stored into a configured
    location.

-   Get the Demographic byte stream from the respective DTO object and
    store it into the Zip object using right folder path \[....\].

-   Get the Biometric byte stream from the respective DTO object and
    store it into the Zip object using right folder path \[....\].

-   Get the Proof of documents byte stream from the respective DTO
    object and store it into the Zip object using right folder path
    \[....\].

-   Get the 'Enrollment ID' from the respective request object, write
    the same into the File object and save the file object into the Zip
    object.

-   Hash :

    -   Generate the Hash for the Biometric, Demographic and EID of
        Resident Information.

    -   Use the HMAC generation from Java 8 \[MD5 Hashing -- SHA256\]

-   Store the generated Hash in a file and append to the created Zip
    object.

-   Capture the Enrollment Officer/Supervisor Authentication finger
    image from the respective DTO object and append to the Zip object.

-   Create the Packet Info JSON file, which contains the **Meta data**
    information about packet and appended to the existing Zip object.

-   Session Key Encryption:

    -   Session key generation is \[MAC of machine + EO Id + Timestamp\]
        should not exceed 32 characters.

    -   Pass the created Zip object \[in-memory\] through the AES-256
        bit encryption.

    -   Pass the Random Session Key as a seed to this AES encryption.

    -   Get the Enrollment Officer Id from user context object.

    -   

-   RSA Public Key Encryption:

    -   AES Session key bytes pass through the RSA public key
        encryption.

-   Use the "\#KEY\_SPLITTER\#" as a key separator for the AES encrypted
    bytes and the RSA Public key encrypted Session key seed.

-   Append the RSA Public key Encrypted Session Key, Key Separator to
    the AES encrypted bytes.

-   Save the encrypted data as a ZIP in local file system under the
    defined location in configuration file.

-   Append the EO and machine information as a META-INFO JSON file and
    create another ZIP out of it. \[Packet Zip + META-INFO JSON\]

-   Audit the exception/start/exit of the each stages of the packet
    encryption mechanism using AuditManager component.

-   The final zip name should be as enrollemntid+CurrentTimestamp \[28
    digit\].

-   Timestamp format is \[DDMMYYYYHHMMSSS\]

-   Once the packet has been successfully created then update the packet
    information in the 'Enrollment' table.

**Client \[UI\] Application:**

-   Invoking client application should store all information about the
    resident as desired format of [DTO
    objects](#entity-object-structure).

-   Enrollment ID should have already been generated and pass it in the
    EnrollmentDTO object.

-   Invoke the 'PacketHandler'.createPacket(EnrollmentDTO) method to
    prepare the Enrollment packet at the configured location in local
    machine.

> **Packet Archival:**

-   Get the Packet status using the 'Enrollment packet status' reader
    REST service. If the status is UIN generated /Updated, we need to
    update the same info to the database and clean the packet. {will be
    taken care in the next sprint}

-   

### Packet Structure 

> ![](media/image2.png){width="4.645833333333333in"
> height="3.2096030183727033in"}

-   Create date wise folder, if not exists. \[Sample: 12-SEP-2018 \]

-   Biometric and Demographic folders should have the below sub folder
    structure.

    -   Applicant

    -   Introducer

    -   HOF

-   **Biometric File: **

    ![cid:image006.jpg\@01D433FD.27942630](media/image3.jpeg){width="3.8541666666666665in"
    height="1.7847222222222223in"}

-   **Demographic :**

    ![](media/image4.png){width="3.8239621609798777in"
    height="1.3680555555555556in"}

### Folder level Data: 

1.  **Biometric**

<!-- -->

a.  Applicant

    -   LetThumb.jpg/png

    -   RightThumb.jpg/png

    -   LeftPalm.jpg/png

    -   RightPalm.jpg/png

    -   LeftEye.jpg/png

    -   RightEye.jpg/png

b.  HOF

    -   **HOF LeftThumb.jpg/png**

c.  Introducer

    -   **LeftThumb.jpg/png**

<!-- -->

2.  **Demographic **

    a.  Applicant

        -   ProofOfIdentity.docx

        -   ProofOfResidenty.docx

        -   ProofOfAddress.docx

        -   ApplicantPhoto.jpg/png

        -   ExceptionPhoto.jpg/png \[If Exceptional cases\]

        -   Enrollment Acknowledgement.jpg

    b.  Demographic\_info.json

3.  **RegistrationID.txt**

4.  **HMAC File.txt **

5.  **Packet\_MetaInfo.json **

6.  **Enrollment Officer Bio Image\[JPEG\]**

7.  **Enrollment Supervisor Bio Image\[JPEG\]**

### Validations:

-   Verify the Packet decryption, but this not in our scope but as a
    demo we need to show.

-   The seed length should be 256-bit.

-   The packet structure should be validated.

-   The packet name should be unique and the name of the packet
    is\[EnrollmentID+TimeStamp\[DDMMYYYYHHMMSSS\]\]

Class Diagram
-------------
	_images/\_class\_diagram/registration-packetcreation-classDiagram.png

Sequence Diagram
----------------
	_images/\_sequence\_diagram/registration-packetcreation-sequenceDiagram.png

User Story References
=====================

  **User Story No.**   **Reference Link**
  -------------------- -----------------------------------------------
  **MOS-64**           <https://mosipid.atlassian.net/browse/MOS-64>
  **MOS-65**           <https://mosipid.atlassian.net/browse/MOS-65>

