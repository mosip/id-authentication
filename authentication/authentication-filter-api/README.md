# Authentication Filter API 

## About
Authentication Filter API is the component that has the API definition for the Authentication Filters. 

## Authentication Filter
Authentication filters are [configurable](../../docs/configuration.md) filters that are applied before performing actual authentication (OTP/Demograpic/Biometric) of an individual. This can be separately configured for [Authentication Service](../authentication-service) and [Internal Authentication Service](../authentication-internal-service). The implemented filters are the following:
* [Hotlist filters](../authentication-hotlistfilter-impl): To disallow authentication for hotlised Partner IDs/Device data/Individual IDs.
* [Authentication type lock filter](../authentication-authtypelockfilter-impl): To disallow authentication if an individual has locked certain authentication types for themselves.
* [Child authentication filter](https://docs.mosip.io/1.2.0/modules/reference-implementations): To validate if an individual is child, and if so disallow some [configured](https://github.com/mosip/mosip-config) authentication types.

