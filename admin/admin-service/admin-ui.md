* [Entity UI Specifications](#entityui-specs)

# Entity UI Specifications

* [GET /entity](#get-entityUI)

### GET /entity

This service will give the entity configuration to be displayed in the tables.This service will include the action buttons, pagination details, display columns and ellipsis configuration details.

#### Resource URL
<div>https://mosip.io/v1/admin/entity</div>

#### Example Response

```JSON
{  
	"actionButtons":[  
		{  
			"buttonName":"AddCenter",
			"actionType":"redirect",
			"actionURL":"",
			"redirectURL":"/registrationcenters/create"
		},
		{  
			"buttonName":"Download",
			"actionType":"action",
			"actionURL":"/registrationcenters/download",
			"redirectURL":""
		}
	],
	"pagination":[  
		{  
			"displayRows":[  
				10,
				20,
				30
			],
			"defaultValue":10
		}
	],
	"displayColumns":[  
		{  
			"displayField":"centerTypeCode",
			"displayName":"Center",
			"sortOrder":1,
			"sortType":"ASC",
			"showAsLink":true,
			"linkType":"redirect",
			"linkRedirectUrl":"/admin/resources/centers/view?id=centerTypeCode"
		},
		{  
			"displayField":"isActive",
			"displayName":"ActiveStatus",
			"showAsLink":false,
			"linkType":"",
			"linkRedirectUrl":""
		},
		{  
			"displayField":"noOfUsersMapped",
			"displayName":"Users",
			"showAsLink":false,
			"linkType":"",
			"linkRedirectUrl":""
		},
		{  
			"displayField":"noOfMachinesMapped",
			"displayName":"Machines",
			"showAsLink":false,
			"linkType":"",
			"linkRedirectUrl":""
		},
		{  
			"displayField":"noOfDevicesMapped",
			"displayName":"Devices",
			"showAsLink":false,
			"linkType":"",
			"linkRedirectUrl":""
		},
		{  
			"displayField":"contactPerson",
			"displayName":"ContactPerson",
			"showAsLink":false,
			"linkType":"",
			"linkRedirectUrl":""
		},
		{  
			"displayField":"contactNo",
			"displayName":"ContactPerson",
			"showAsLink":false,
			"linkType":"",
			"linkRedirectUrl":""
		},
		{  
			"displayField":"updatedDate",
			"displayName":"UpdatedDate",
			"showAsLink":false,
			"linkType":"",
			"linkRedirectUrl":""
		}
	],
	"ellipsis":[  
		{  
			"displayField":"isActive",
			"toggleValue":[  
				{  
					"value":true,
					"displayValue":"Deactivate"
				},
				{  
					"value":false,
					"displayValue":"Activate"
				}
			],
			"displayAction":"action",
			"displayActionURL":"/registrationcenters?id=regcenterid&(action={toggleValue})",
			"displayRedirectURL":""
		},
		{  
			"displayField":"decomission",
			"displayName":"Decomission",
			"displayAction":"action",
			"displayActionURL":"/registrationcenters?id=regcenterid&action=decomission",
			"displayRedirectURL":""
		},
		{  
			"displayField":"assignusers",
			"displayName":"Users",
			"displayAction":"redirect",
			"displayActionURL":"",
			"displayRedirectURL":"/registrationcenteruser?id=regcenterid"
		},
		{  
			"displayField":"assignmachines",
			"displayName":"Machines",
			"displayAction":"redirect",
			"displayActionURL":"",
			"displayRedirectURL":"/registrationcentermachine?id=regcenterid"
		},
		{  
			"displayField":"assigndevices",
			"displayName":"Devices",
			"displayAction":"redirect",
			"displayActionURL":"",
			"displayRedirectURL":"/registrationcenterdevices?id=regcenterid"
		},
		{  
			"displayField":"editcenter",
			"displayName":"Edit",
			"displayAction":"redirect",
			"displayActionURL":"",
			"displayRedirectURL":"/registrationcenters/edit?id=regcenterid"
		}
	]
}

```