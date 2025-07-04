# MOSIP KYC Service Properties Documentation

This document provides a comprehensive overview of all configuration properties used in the MOSIP Identity Authentication (IDA) kyc-auth & kyc-exchange V1 & V2 version APIs, Key Binding & VC Issuance APIs .

## Overview

It includes below functionalities:
- KYC authentication and data exchange (kyc-auth [V1 & V2], kyc-exchange [V1]) 
- Verified claims processing - OIDC4IDA compliance (kyc-exchange [V2])
- Verifiable Credentials (VCI) generation
- Identity key binding

## Property Categories

### 1. Consented Attribute Configuration

#### `ida.idp.consented.individual_id.attribute.name`
- **Default Value**: `individual_id`
- **Type**: String
- **Purpose**: Specifies the attribute name used for individual ID in consented claims. When the individual_id attribute is present in the consented claims list, the UIN/VID is returned in plain text in the response. This attribute is included to enable the ID to be displayed on the Resident portal. To stop the UIN/VID from being included in the response even if it is requested in consented claims list then update the property value to blank.   
- **Sample Value**:
  ```properties
  ida.idp.consented.individual_id.attribute.name=individual_id
  ```

#### `ida.idp.consented.picture.attribute.name`
- **Default Value**: `picture`
- **Type**: String
- **Purpose**: Specifies the attribute name used for picture/photo information in consented claims
- **Usage**: Used when processing biometric photo data in KYC responses
- **Sample Values**:
  ```properties
  ida.idp.consented.picture.attribute.name=picture
  ida.idp.consented.picture.attribute.name=photo
  ida.idp.consented.picture.attribute.name=face
  ```

#### `ida.idp.consented.picture.attribute.prefix`
- **Default Value**: `data:image/jpeg;base64,`
- **Type**: String
- **Purpose**: Specifies the prefix for picture attribute data encoding
- **Usage**: Used when encoding picture data in base64 format
- **Sample Values**:
  ```properties
  ida.idp.consented.picture.attribute.prefix=data:image/jpeg;base64,
  ida.idp.consented.picture.attribute.prefix=data:image/png;base64,
  ```

#### `ida.idp.consented.address.value.separator`
- **Default Value**: ` ` (space)
- **Type**: String
- **Purpose**: Specifies the separator used when concatenating address components
- **Usage**: Used when building formatted address strings in the response
- **Sample Values**:
  ```properties
  ida.idp.consented.address.value.separator= 
  ida.idp.consented.address.value.separator=\n
  ```

### 2. Address Processing Configuration

#### `mosip.ida.idp.consented.address.subset.attributes`
- **Default Value**: `[]` (empty array)
- **Type**: String array
- **Purpose**: Defines subset attributes for address processing when detailed address components are required. The subset attributes has to be as per the OIDC standard subset address attributes. If no subset attributes provided then the response will have all the address attributes in formatted attribute.
[Refer this URL for more information](https://openid.net/specs/openid-connect-core-1_0.html#AddressClaim)
- **Usage**: When configured, address processing switches to subset mode, breaking down address into individual components
- **Sample Values**:
  ```properties
  # Full address breakdown
  mosip.ida.idp.consented.address.subset.attributes=street_address,locality,region,postal_code,country
  
  # Street address only
  mosip.ida.idp.consented.address.subset.attributes=street_address
  
  # Formatted Address
  mosip.ida.idp.consented.address.subset.attributes=
  ```

### 3. Response Type and Encryption Configuration

#### `ida.idp.jwe.response.type.constant`
- **Default Value**: `JWE`
- **Type**: String
- **Purpose**: Defines the constant used to identify JWE (JSON Web Encryption) response type
- **Usage**: Used to determine whether to encrypt the response using JWE when the request specifies this response type
- **Sample Values**:
  ```properties
  ida.idp.jwe.response.type.constant=JWE
  ```

### 4. Issuer Configuration

#### `mosip.ida.idp.add.issuer.response`
- **Default Value**: `true`
- **Type**: Boolean
- **Purpose**: Controls whether to include issuer information in the response
- **Usage**: When enabled, adds `iss` (issuer) and `aud` (audience) fields to the response
- **Sample Values**:
  ```properties
  mosip.ida.idp.add.issuer.response=true
  mosip.ida.idp.add.issuer.response=false
  ```

#### `mosip.ida.idp.issuer.uri`
- **Default Value**: `""` (empty string)
- **Type**: String
- **Purpose**: Specifies the issuer URI to be included in responses when `add.issuer.response` is true
- **Usage**: Used as the value for the `iss` (issuer) field in the response
- **Sample Values**:
  ```properties
  # environment
  mosip.ida.idp.issuer.uri=https://prod.mosip.io/idp
 
  ```

### 5. OIDC Claims Filtering Configuration

#### `mosip.ida.oidc4ida.ignore.standard.claims.list`
- **Default Value**: Not specified (required configuration)
- **Type**: String array
- **Purpose**: Lists standard claims that should be ignored when building verified claims metadata
- **Usage**: Used to filter out certain claims from the verified claims response based on OIDC4IDA specifications
- **Sample Values**:
  ```properties
  # claims to ignore
  mosip.ida.oidc4ida.ignore.standard.claims.list=picture,individual_id,gender,residenceStatus
  ```

### 6. Verifiable Credentials (VCI) Configuration

#### `mosip.ida.config.server.file.storage.uri`
- **Default Value**: `""` (empty string)
- **Type**: String
- **Purpose**: Specifies the URI for config server file storage
- **Usage**: Used for accessing VCI context schemas and configuration files
- **Sample Values**:
  ```properties
  mosip.ida.config.server.file.storage.uri=https://dev.mosip.net/config
  ```

#### `mosip.ida.vercred.context.url.map`
- **Default Value**: Not specified
- **Type**: Map<String, String>
- **Purpose**: Maps VCI context URLs to local configuration URIs
- **Usage**: Used for caching VCI context schemas
- **Sample Values**:
  ```properties
  mosip.ida.vercred.context.url.map={"https://www.w3.org/2018/credentials/v1":"credentials-v1.json","https://www.w3.org/ns/odrl.jsonld":"odrl.json"}
  ```

#### `mosip.ida.vercred.context.uri`
- **Default Value**: `""` (empty string)
- **Type**: String
- **Purpose**: Specifies the URI for VCI context data
- **Usage**: Used for accessing VCI context JSON-LD data
- **Sample Values**:
  ```properties
  mosip.ida.vercred.context.uri=vccontext-ida.jsonld
  ```

#### `mosip.ida.vercred.id.url`
- **Default Value**: `""` (empty string)
- **Type**: String
- **Purpose**: Specifies the base URL for generating VCI IDs
- **Usage**: Used when creating unique identifiers for verifiable credentials
- **Sample Values**:
  ```properties
  mosip.ida.vercred.id.url=https://api.environment.mosip.net/credentials
  ```

#### `mosip.ida.vercred.issuer.url`
- **Default Value**: `""` (empty string)
- **Type**: String
- **Purpose**: Specifies the issuer URL for verifiable credentials
- **Usage**: Used as the issuer identifier in VCI responses
- **Sample Values**:
  ```properties
  mosip.ida.vercred.issuer.url=https://api.environment.mosip.net/.well-known/ida-controller.json
  ```

#### `mosip.ida.vercred.proof.purpose`
- **Default Value**: `""` (empty string)
- **Type**: String
- **Purpose**: Specifies the proof purpose for VCI signatures
- **Usage**: Used when creating cryptographic proofs for verifiable credentials
- **Sample Values**:
  ```properties
  mosip.ida.vercred.proof.purpose=assertionMethod
  ```

#### `mosip.ida.vercred.proof.type`
- **Default Value**: `""` (empty string)
- **Type**: String
- **Purpose**: Specifies the proof type for VCI signatures
- **Usage**: Used when creating cryptographic proofs for verifiable credentials
- **Sample Values**:
  ```properties
  mosip.ida.vercred.proof.type=Ed25519Signature2018
  mosip.ida.vercred.proof.type=RsaSignature2018
  ```

#### `mosip.ida.vercred.proof.verificationmethod`
- **Default Value**: `""` (empty string)
- **Type**: String
- **Purpose**: Specifies the verification method for VCI proofs
- **Usage**: Used when creating cryptographic proofs for verifiable credentials
- **Sample Values**:
  ```properties
  mosip.ida.vercred.proof.verificationmethod=https://api.environment.mosip.net/.well-known/ida-public-key.json
  ```

#### `mosip.ida.vci.supported.cred.types`
- **Default Value**: `""` (empty string)
- **Type**: String array
- **Purpose**: Lists supported credential types for VCI exchange
- **Usage**: Used for validating credential type requests in VCI exchange
- **Sample Values**:
  ```properties
  mosip.ida.vci.supported.cred.types=VerifiableCredential,MOSIPVerifiableCredential
  ```

### 7. Identity Key Binding Configuration

#### `mosip.ida.key.binding.name.default.langCode`
- **Default Value**: `eng`
- **Type**: String
- **Purpose**: Specifies the default language code for key binding certificate names. Reading the name attribute value for the certificate CN value.
- **Usage**: Used when creating certificates for identity key binding
- **Sample Values**:
  ```properties
  mosip.ida.key.binding.name.default.langCode=eng
  ```

#### `mosip.ida.key.binding.certificate.validity.in.days`
- **Default Value**: `90`
- **Type**: Integer
- **Purpose**: Specifies the validity period in days for key binding certificates generation.
- **Usage**: Used when creating certificates for identity key binding
- **Sample Values**:
  ```properties
  mosip.ida.key.binding.certificate.validity.in.days=90
  ```
