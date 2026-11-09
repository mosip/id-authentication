## Documentation for New Properties added for Verified Claims V2 endpoints.

### 1. kyc-exhange V2 Contented Non Language Attributes list

#### `mosip.ida.idp.no.language.attribute.list`
- **Default Value**: `phone`
- **Type**: Comma-separated list of strings
- **Purpose**: This property defines a list of identity attributes that are **not language-specific** and should be handled without language code processing in the MOSIP Identity Authentication (IDA) system's verified claims functionality. If a claim is **included** in this list (e.g., "phone"), the system will process it even if there are no matched locales. If a claim is **not included** in this list and there are no matched locales, the system will skip processing that claim
   
- **Sample Value**:
  ```properties
  mosip.ida.idp.no.language.attribute.list=phone
  ```

#### `mosip.ida.oidc4ida.ignore.standard.claims.list`
- **Default Value**: No Default Value. This configuration is required.
- **Type**: Comma-separated list of strings
- **Purpose**: This property defines a list of standard OIDC claims that should be excluded from the verified claims processing in MOSIP's OIDC4IDA (OpenID Connect for Identity Assurance) implementation. It acts as a filter to prevent standard OIDC claims from being processed as verified claims.
   
- **Sample Value**:
  ```properties
  mosip.ida.oidc4ida.ignore.standard.claims.list=picture,individual_id,gender,residenceStatus
  ```
### 2. Identity mapping: `verifiedAttributes`

#### `verifiedAttributes` (in the identity-mapping JSON referenced by `ida.mapping.property.source`)
- **Default Value**: No Default Value. This mapping entry is required for the V2 endpoints.
- **Type**: List of strings (mapping entry, not a Spring property)
- **Purpose**: Maps the identity attribute that holds the verified-claims metadata of an identity. It is loaded into `IDAMappingConfig.verifiedAttributes` and is added to the attributes fetched for `kyc-auth/v2` and `kyc-exchange/v2` requests (`IdaIdMapping.VERIFIEDATTRIBUTES`).
- **Note**: Add this entry to the `identity-mapping.json` served by the config server, alongside the existing attribute mappings.
