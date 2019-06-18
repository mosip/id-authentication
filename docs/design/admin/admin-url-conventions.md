# Admin module - URL conventions and standards

#### Background

The Admin module contains different various functionalities. In the UI, the user can navigate and reach the place where he can do the functionalities. The necessary parameters to this page can be passed by HTTP Headers or URL parameters. The user can bookmark this page or send the URL to somebody. The user also can click on the browser back button to go back. When the user revisits these URLs the state of that page should be maintained. Following are the different states which are maintained, 

	1. The page in which the user was on last time
	2. Action - Like it is a view or edit or create of an entity
	3. Filter parameters
	4. Sort
	5. Pagination

#### Solution


**The key solution considerations are**

- Following are the key considerations of the URL conventions. 

The URL contains the following sections, 

1. Base URL:
	- This URL is retrieved from a configuration entry. 
	- This URL will be different for each environment. 

2. Context path:
	- The context path is retrieved from the reverse proxy. 

3. Angular Router path:
	a. Module
		- This part of the path is the Angular router's one. 
		
	b. Actions
		- This is also Angular's router path. 

4. Action parameters:

The syntax would be as follows, 

<protocol>://<domain_name>:<port_number>/<context_path>/<router_module>/<router_action>?<query_parameters>

For example, 

https://mosip.io/admin/devices/list ===> list
https://mosip.io/admin/devices/view?id=UDT52923 ===> view device UDT52923
https://mosip.io/admin/devices/edit?id=UDT52923 ===> edit device UDT52923
https://mosip.io/admin/devices/create ===> create new device


 - The URL sized limit in some browsers can be limited to 2000 characters. Except the filter parameter, the entire length of the URL was assumed to occupy around 250 characters. If the URL exceeds the limitation, the behaviour is unexpected. 


Please refer to the following diagram to understand the construction of the URL, 


**Admin URL**


![Admin URL](_images/admin-url-mappings.jpg)


- There are times we have to pass the parameters as part of HTTP request body or HTTP request headers. During these times bookmarking the URL, sending the URL to others and such things doesn't work. The reason why the HTTP request body or HTTP request headers are preferred is, there is a limitation in the HTTP URL length. All the parameters are passed via the HTTP GET method. 

Samples:


1. Resource
	- The user can go to specific resources where a common template is used. For example, the admin can go to various master data's list screens. 	
	- In each resource, the resource name are fetched by the Routing parameter only. 	
	- Any additional parameters are passed in the query parameters only.
	- For example, https://mosip.io/admin/devices?deviceID=1234
	admin/resources/centers/view or create or edit?parameters

2. Filter parameters
	- The filter parameters applicable for an entity will be enabled from the config server.
	- Then the enabled filter parameters will be passed as part of the URL.
	- https://mosip.io/admin/regcenters?centerType=mobile
	
3. Sort parameters
	- The sort parameters will  be as part of the URL. 
	- The default sort parameters will be taken from the configuration server. 
	- https://mosip.io/admin/regcenters?sort=A:centerType&sort=D:locationCode
	- A:centerType means, "sort by centerType in Ascending order". B:locationCode means, "sort by locationCode in Descending order"
	- If A: or D: is missed, the default sort is applied. 

4. Pagination parameters
	- The pagination parameters will be as part of the URL. 
	- https://mosip.io/admin/regcenters?pagination=s10:f9
	- In s10:f9, fetch records 11 to 19
	- In s10:f70, fetch records 11 to 60. In this case, the max records returned is configured as 50. 
	- 's' stands for skip and 'f' stands for fetch. 
