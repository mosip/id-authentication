# Authentication Filter API Component
## About
Authentication Filter API is the component that have the API definition for the Authentication Filters. 

# Authentication Filter
Authentication filters are [configurable]() filters that are applied before performing actual authentication (OTP/Demograpic/Biometric) of an individual. 
This can be separately configured for [Authentication Service]() and [Internal Authentication Service]().
Below are such authentication filters implemented, 
* [Hotlist filters]() - used to disallow authentication for hotlised Partner IDs/Device data/Individual IDs.
* [Auth Type Lock filter]() - used to disallow authentication if an individual has locked certain authentication types for themselves.
* [Child Authentication filter]() - used to validate if an individual is child, and if so disallow some [configured]() authentication types.

