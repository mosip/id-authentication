# MasterDataType

**Background**
- This service can be used to fetch masterdata based on type to be diplayed on the tables. 

**Solution**

    - Create a Rest Controller with Request URL "v1/masterdata/{masterdataType}".
    - Create a Rest Service which fetches the masterdata to be diplayed from the type.                 
    - Admin UI component can fetch the entites to be displayed for the tables.

**Sequence Diagram**

![Sequence Diagram](_images/admin-getmasterdatatype.jpg)

        