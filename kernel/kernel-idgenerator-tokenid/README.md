## Module kernel-idgenerator-tokenid

[API Documentation](doc/index.html)



** Properties to be added in parent Spring Application environment **

[kernel-idgenerator-tokenid-dev.properties](../../config/kernel-idgenerator-tokenid-dev.properties)



** Database Properties **

Schema : ids

Table : token_id


** Description **

1._Token Id generator is solely used for generating an id which will be used by the TSP as an alternative for the UIN_

2.**ADMIN** _can only configure the length_ 

3._The Token Id should be generated sequentially,cannot not have repeated numbers and cannot contain alphanumeric values_

4._The last digit of the generated token id should have checksum_  


##Sample##










