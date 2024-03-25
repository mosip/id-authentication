# On demand Template Extraction Guide

## Overview
The resident has registered and obtain a valid UIN/VID and proceed with authentication by entering UIN/VID and get error as UIN is not available in database, then resident couldn’t proceed with authentication further as the provided UIN/VID is not available in IDA DB.
This could happen when a country has large number of registrations completed, processed and issued UINs.
Since resident is eager to authenticate, we want to prioritize the credential processing (along with template extraction) of this resident, we can tell him/her to retry and complete the authentication.
While getting the error UIN/VID is not available in database the websub event is triggered which publish the data in to the topic and the subscriber will receive a message for futher processing.

## Configuration files
id-authentication-default.properties: 
```
ida-topic-on-demand-template-extraction=AUTHENTICATION_ERRORS
mosip.ida.ondemand.template.extraction.partner.id=mpartner-default-tempextraction

```

## Image source
```
![On demand-template-extraction](https://github.com/mosip/id-authentication/blob/release-1.2.0.2/docs/images/On_demand_template_extraction.png)

```
## Event Structure publish to kafka
```
{
	"publisher": "IDA",
	"topic": "AUTHENTICATION_ERRORS",
	"publishedOn": "2024-01-03T09:45:13.992Z", (Current date and time)
	"event": {
		"id": "<unique auto generated id> ",
		"data": {
			"error_Code" : "IDA-MLC-018"
			"error_message" : "UIN not available in database"
			"requestdatetime": "yyyy-mm-ddThh:mm:ss", (Current date and time)
			"individualId" :"<value of UIN/VID> (This should be encrypted with a public key)"
			"authPartnerId" :"<value of partner id>" 
			"individualIdType": "UIN",
			"entityName": "<value of partner name>",
			"requestSignature": ""
		}
	}
}
```