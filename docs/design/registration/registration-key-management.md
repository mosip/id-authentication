**Design - Key Management**

**Background:**  
  The data captured and stored in to the client machine during different process [such as new registration, update UIN, lost UIN, pre-registration] are encrypted with the different set of keys. The keys have a different set of expiry policies and based on which it will be refreshed. The keys are securely managed into the database.  

  - New Registration Packet : The machine specific public key should be maintained at local system.  
  - Update UIN Packet : It should be secured with the machine specific public key.  
  - Lost UIN Packet :  It should be secured with the machine specific public key.  
  - Pre-registration Packet: It should be secured with symmetric session key.  
  - Database Key : It should be secured with 'bootpassword' key and that should be encrypted using TPM public key.   

    
**Design:**
  **New Registration/ Update UIN/ Lost UIN Packet :**   
   The packet created as output of this process should be encrypted with the machine specific public key, which is download from the remote server.  This key has the expiry policy. Before it expires the next version of key should be downloaded from the server. 
     
  It is stored into the database table [key_store] along with the start and expiry date of the key. Based on which the key would be rendered during packet encryption. There is no key maintained for decryption of packet.  

  - Refer [Policy sync](_registration-policy-sync.md) for more detail. 

  **Pre-Registration Packet :**   
   The pre-registration packet downloaded from remote server should be stored into the local hard disk after encrypting with the session based symmetric key. For each packet, different key would be generated and store the same into the database table along with packet related information. The same key would be used during decryption of packet to display the respective detail in the screen.  
    
  -  Refer [Pre-Registration sync](_registration-Pre-Registration-Sync.md)  for more detail.
  
  **Database Key :**  
   The derby database used in the local machine to store the transaction performed by the operators. The data stored [encrypted] into the db are secured using the 'bootpassword', which is further secured with the TPM public key. The encrypted DB key won't be useful at any other machine other than the right TPM installed machine. This key is stored into the property file.   
   
   - Refer [TPM Design](_registration-TPM-integration.md) for more detail.
   