**Design - TPM Device Integration **

A Trusted Platform Module (TPM) is a specialized chip on a local machines that stores RSA encryption keys specific to the host system for hardware authentication. Each TPM chip contains an RSA key pair called the Endorsement Key (EK). The pair is maintained inside the chip and cannot be accessed by software.   

The Registration client application leverages this TPM capabilities and secure the application, data and mark the senders identity in the request before sending to external system.  

This document covers the technical design of Registration client application integration with TPM [Truster Platform Module]. 

The following functionalities would be implemented by integrating with the TPM:  

   1. Secure the symmetric key using encryption.  
   2. Decryption of Symmetric key during runtime.  
   3. Sign the requests sent from Registration client to server by using Endorsement private key.    
   4. Extract the Endorsement public Key from the TPM and share the same with the server component.  
      

**TPM Integration Block and Sequence Diagram:**  
![TPM Integration Block and Sequence Diagram:](_images/TPM_Integration_design.png)  


The Microsoft provided Java libraries !(https://github.com/Microsoft/TSS.MSR/tree/master/TSS.Java) would be used to integrate with the TPM device and implement the required functionalities. 


1. Java Utility provided to:
   a.	to connect to the TPM and extract the EK pub key and store it [encoded format] into a particular file [pub.key].  
   b.	to encrypt the [DB and binary jar] symmetric key using EK pub key {which would be decrypted with in the application using the TPM EK private key}.  
   
2.	Operator should send the pub key to the admin person to get it inserted into the db.   
3.	In machine_master table, should have the facility to hold the pub key instead of mac id.  
4.	Once the user logged into the machine [with online service], the initial master sync  to be triggered with “pub key” in the request. The same should be validated by kernel service while providing the master sync response. This process would make sure that the sender's identity is validated by the server and only the authenticated machine would get the success response.  
5.	Whenever sending the requests to the server component, the requests would be signed using EK private key and attach it to the header of the requests. The server component would validate the same using the sender's specific public key.

  
 
