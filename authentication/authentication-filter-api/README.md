# Authentication Filter API Component
## About
Authentication Filter API is the component that have the API definition for the Authentication Filters. 

## Authentication Filter
Authentication filters are [configurable](https://github.com/mosip/mosip-config) filters that are applied before performing actual authentication (OTP/Demograpic/Biometric) of an individual. 
This can be separately configured for [Authentication Service](../authentication-service) and [Internal Authentication Service](../authentication-internal-service).
Below are such authentication filters implemented, 
* [Hotlist filters](../authentication-hotlistfilter-impl) - used to disallow authentication for hotlised Partner IDs/Device data/Individual IDs.
* [Authentication Type Lock filter](../authentication-authtypelockfilter-impl) - used to disallow authentication if an individual has locked certain authentication types for themselves.
* [Child Authentication filter](https://github.com/mosip/mosip-ref-impl/tree/1.2.0-rc2/authentication/authentication-childauthfilter-impl) - used to validate if an individual is child, and if so disallow some [configured](https://github.com/mosip/mosip-config) authentication types.

