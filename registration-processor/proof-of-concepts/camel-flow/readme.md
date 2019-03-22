# Camel-Vertx Flows
This poc demonstrates the use of different xml for different flows in MOSIP Registration Processor.
## Flow description
* The POC has 3 distinct flows, one each for New UIN, Update UIN and Activate UIN. 
* One vertx-camel-bidge is created with 3 different xml files with each xml corresponding to one flow.
* 5 verticles are created named verticle-1 to verticle-5.
* New UIN flow : Verticle-1->Verticle-2-Verticle-3->Verticle-4->Verticle5
* Update UIN flow : Vertilce-1->Verticle-3->Verticle-5
* Activate UIN flow : Verticle-1-> Verticle-5
## Running the project
Each project can be run as independent java projects. It should be made sure that all the projects are running and connected through hazelcast cluster by verifying the below mentioned snippet on console: 
```
Members {size:6, ver:6} [
	Member [127.0.0.1]:5701 - 5ca09266-4ea6-4942-936a-f0308ae3bdef this
	Member [127.0.0.1]:5702 - 3b5ba92e-66f1-46de-a800-f6cf2cbfc6dd
	Member [127.0.0.1]:5703 - 3c2b4825-d214-4e44-9074-f5c799c18f05
	Member [127.0.0.1]:5704 - 52914be3-a69b-4c9d-852a-5d1b34e610e8
	Member [127.0.0.1]:5705 - f2897a96-7407-480c-ab4c-71a726ad2a82
	Member [127.0.0.1]:5706 - 8a6b9c85-9ec4-4c18-a104-0a9aadcadb98
]
```
## Initiating the flow
Verticle 1 is the stage with a rest endpoint attached to it. This end point is to be used to initiate the flow. Below mentioned steps can be followed to initiate the flow.
* Check health of verticle one using the URL http://localhost:8080
* Trigger the flow by using POST method for rest endpoint http://localhost:8080/initiate with the request
New UIN : 
```
{
	"rid":"123456",
	"isValid":true,
	"requestType":"new"
}
```
Update UIN : 
```
{
	"rid":"123456",
	"isValid":true,
	"requestType":"update"
}
```
Activate UIN :
```
{
	"rid":"123456",
	"isValid":true,
	"requestType":"activate"
}
```
The flow can be verified by checking console of each of the verticles.
