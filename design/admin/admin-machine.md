# Admin Machine 

#### Background

The admin should be able to manage the machine in the Admin application. 

#### Solution

**The key solution considerations are**

The Admin should be able to view , create and edit the master machine data. Admin should be able to perform filter, search and sort on the machine data.

Key considerations : 

1. Entity Spec: 
	- When the page loads, how the page loads depends upon the configurations. NOTE: This configuration is from the Angular code itself. Any change requires the code change.

2. Data loading:
	- After the Angular component decides the layout and the specification, the data should be loaded in the page. 
	- This is achieved by calling the service URL, 
	"https://github.com/mosip/mosip-docs/wiki/Machine-APIs#post-machinessearch"
	- The same URL will be used after filter search also. But, in this case, no filter parameters are passed as part of the request. Thus, all the records are returned. 

3. Filter:
	- When the page loads initially, the UI have to display the list of filters. The list of filters is retrieved from the configuration in the Angular UI component. 
	- Once the user clicks on "Filter" button, we have to show the filters. And once the user selects the filter, the page should reflect the changes.
	- This is achieved by calling the service URL, 
	"https://github.com/mosip/mosip-docs/wiki/Machine-APIs#post-machinessearch"
	- The following URL is used to populate the filter dropdowns, 
	"https://github.com/mosip/mosip-docs/wiki/Machine-APIs#post-machinesfiltervalues"
	- In the above URL, the filters have to be passed as parameters for which you will get the values for the filter dropdown.
	- Filter values are injected dynamically into the queries based on entity in runtime.
	- Filter values are validated based on each entity which are to mapped to particular entity.
	
4. View:
	- The following API is used with the id as filter,
	https://github.com/mosip/mosip-docs/wiki/Machine-APIs#post-machinessearch
	
5. Edit:
	- The values from the view screen is used in the edit screen. Once saved, the following URL is used for saving the data, 
	https://github.com/mosip/mosip-docs/wiki/Machine-APIs#put-machines
	
6. Create:
	- During the create the following API is used, 
	https://github.com/mosip/mosip-docs/wiki/Machine-APIs#post-machines


**Class diagram**

![Class Diagram](_images/admin-machineclass.jpg)