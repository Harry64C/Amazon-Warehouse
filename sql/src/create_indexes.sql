DROP INDEX Users_userID_idx;
DROP INDEX Users_name_idx;
DROP INDEX Orders_customerID_idx;
DROP INDEX Products_storeID_productName_idx;

CREATE INDEX Users_userID_idx
ON Users USING BTREE 
(userID);

CREATE INDEX Users_name_idx
ON Users USING BTREE 
(name);

CREATE INDEX Orders_customerID_idx
ON Orders USING BTREE 
(customerID);

CREATE INDEX Products_storeID_productName_idx
ON Product USING BTREE 
(storeID, productName);


