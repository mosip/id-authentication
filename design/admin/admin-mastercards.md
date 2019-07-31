# Master data cards

#### Background

When an administrator logs in to the Admin portal, the administrator sees the list of master data which are available in the MOSIP platform. If any of the master data card is clicked, the corresponding REST API should be called the response should be rendered in the screen. 

#### Solution

The list of master data cards can be retreived from an REST API and the each click of the master data, the data is fetched using another GET API of the service. 

**The key solution considerations are**

1. Configurability: The list of master data which have to be rendered in the screen is configurable. Also the master data fetch APIs are also configurable. 

2. Separation of concerns: The list of master data is fetched from the config server. The URLs for the fetch APIs is in the Admin portal configurations. 


**Sequence diagram**


![Sequence Diagram](_images/admin-mastercards.jpg)


## Implementation


**admin-login** [README](../../../admin/admin-login/README.md)


