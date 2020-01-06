## kernel-auth-adapter


[Background & Design](https://github.com/mosip/mosip/wiki/Auth-Adapter)


 **Maven Dependency**
 
 ```
 	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-auth-adapter</artifactId>
			<version>${project.version}</version>
		</dependency>

 ```

**Application Properties**


```
auth.server.validate.url=http://localhost:8091/v1/authmanager/authorize/validateToken
auth.role.prefix=ROLE_
auth.header.name=Authorization
#auth.server.refreshToken.url=http://localhost:8091/v1/authmanager/authorize/refreshToken

```
