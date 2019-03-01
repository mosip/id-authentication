# Authentication and Authorization in MOSIP platform

#### Background

The authentication and authorization in the MOSIP platform is handled in the a centralized place. OAuth 2.0 authorization framework is used as the standards.JSON Web Token(JWT) is used as a mechanism to implement OAuth2.0 framework. 

#### Solution



**The key solution considerations are**


- A centralized Auth Server handles the authorization request from the platform. 

- Once authenticated, the Auth Server  sends back a Token. There are 2 types of Tokens are used in the MOSIP platform. Once is Access token and the other is the Refresh token. 

- Access token contains the information about the authenticated user and the meta data such as the expiration time, subject, issuer etc., 

- Refresh token is long lived. The refresh token is used to get a new Access token. 

- An additional layers in the Auth service will ensure about the forced logout scenario. Essentially, all the service calls to the Auth Service will go through this creamy layer, where we have a proxy user datastore which will maintain the additional information about the validitiy of the tokens. 

- The Tokens are stored in the creamy layer for an individual user. In case of force logout scenario, this record will be deleted from this proxy user datastore. 

**Tokens Overview**

![Tokens Overview](_images/kernel-authn-tokensoverview.jpg)

**Architectural Principles**

- No resource in the MOSIP can be accessed without Authentication and Authorization. 

- All the auth requests will go via the Auth Server. 

- The user data stores are abstracted behind the Auth Server. 

- The user data stores should be pluggable. 

- The platform's authentication method should support the heterogenous technologies and authentication should happen seamlessly. 

- All the user information are stored only in the Data store. 

- In the rest of the MOSIP platform uses only the userid as the handle to the actual user in the data store. No other user attribute should be used as the handle in the MOSIP platform. 


**Different kinds of Actors in MOSIP platform**

- Following are the identified actors in the authentication, 

1. Human users

- These are the actual human users. 

- These actors exists in LDAP or in Database. 

2. Applications

- These are the applications like Registration client, IDA etc., 

- Applications are provided with credentials like applicationid & secret-key


**Implementing Auth in the web services**

- To ease out the development effort, an Auth Adapter component is distributed to the Web service developers. 

- The developer have to do 3 steps to plug in the Auth Adapter, 

1. Include the Auth Adapter jar file in the classpath. 

2. Inject the Auth Server details 

3. Annotate the server method which have to be protected with the "Role" information. 

Reference: https://github.com/mosip/mosip/wiki/Auth-SpringBoot-User-Guide


**Implementing Auth in JavaFX application**


1. If the actual human user login, pass the username and password to the Auth server.

2. In case of background serivces, pass the applicationid & secret-key. 

3. Once authenticated, the client receives the Auth Token and Refresh token. Client stores them in a secured local store. 

4. In all the REST service calls to server, include the Auth Token. 

5. If the Auth Token is expired, pass the Refresh token and get the Auth token.

6. If the Refresh token is expired, login again. 


**Implementing Auth in Angular application**

1. The human user logs in using the username and the password. 

2. Once authenticated, the client receives the Auth Token and Refresh token. Client stores them in a HTTP-only cookie. 

3. In all the REST service calls to server, include the Auth Token. 

4. If the Auth Token is expired, pass the Refresh token and get the Auth token.

5. If the Refresh token is expired, login again. 

Reference: https://github.com/mosip/mosip/wiki/Auth-Angular-User-Guide


**Data stores for different applications**

1. Pre-Registration --> Relational database

2. Registration --> LDAP

3. Registration processor --> LDAP

4. IDA --> LDAP

**Class diagram**

![Class Diagram](_images/kernel-authn-classdiagram.jpg)

**Login - Swimlane diagram**

![Login - Swimlane Diagram](_images/kernel-authn-login-swimlane.jpg)


**Service call - Swimlane diagram**

![Service call - Swimlane Diagram](_images/kernel-authn-servicecall-swimlane.jpg)


**Sequence diagram**

![Sequence Diagram](_images/kernel-authn-sequencediagram.jpg)

## Implementation


**kernel-auth** [README](../../kernel/kernel-auth/README.md)
