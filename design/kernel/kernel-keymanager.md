# Key Manager

**1. Background**

In the MOSIP platform, there are many types of keys used in various modules. The users might book an appointment using the Pre-Registration module. During this the users uses the public key to encrypt the message and Pre-Registration module uses the private key to decrypt the message. When these packets are pulled by the Registration module, the Registration module can use the public key of Pre-Registration module to establish the connection.

The keys should be maintained and managed as a separate entity apart from the actual modules. These keys are rotated based on a specific security policy.

![Connections Diagram](_images/kernel_keymanager_connections_diagram.png)

**2. Solution**

The key solution considerations are

- Following are the major components in the Key Manager,
  1. Key generation algorithms.
  2. Key rotation module, which will generate the new keys and map to the application for a specific timeframe.
  3. A RESTful micro service, which will serve the public keys of the requested application.
  4. Validator module to check the authenticity of the caller.
  5. Key store to store the keys.

![Components Diagram](_images/kernel_keymanager_components_diagram.png)

- The sequence of the key generations and the rotation of the keys are as follows,
  1. During the initial setup, the key rotation duration are configured.
  2. Applications are associated to the key rotation configuration
  3. The key expiration check is done during the request time.
  4. The decryption of data happens inside the key manager module.
  5. The public key is returned to the requester.

***2.1 Sequence diagrams***

Following are the various sequences of the flow between the various modules. The following diagrams represents any 2 applications interactions at any point of time. 

****2.1.1 Pre-registration --> Registration****

Following is the sequence diagram of the communication between the Pre-registration and the Registration application, 

![Sequence Diagram](_images/kernel_keymanager_Seq_Prereg_Reg_diagram.png)

****2.1.2 Registration --> Registration Processor****

Following is the sequence diagram of the communication between the Registration and the Registration Processor application, 

![Sequence Diagram](_images/kernel_keymanager_Seq_Reg_RegProc_diagram.png)

****2.1.3 TSP --> IDA:****

Following is the sequence diagram of the communication between the TSP and the IDA application, 

![Sequence Diagram](_images/kernel_keymanager_Seq_TSP_IDA_diagram.png)

****2.1.4 IDA --> Kernel****

Following is the sequence diagram of the communication between the IDA and the Kernel application, 

![Sequence Diagram](_images/kernel_keymanager_Seq_IDA_Kernel_diagram.png)

Following is the flow chart for the step &quot;certValidityCheck()&quot;,

![Sequence Diagram](_images/kernel_keymanager_Flowchart_diagram.png)
