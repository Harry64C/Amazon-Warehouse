CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION updates()
    RETURNS "trigger" AS $auditUpdate$
    BEGIN
        IF (pg_trigger_depth() = 1) THEN
            insert into ProductUpdates (managerID, storeID, productName, updatedOn) values ((SELECT managerID FROM Store s1 WHERE s1.storeID = NEW.storeID), NEW.storeID, NEW.productName,current_timestamp);
        END IF;
        RETURN NEW;
    END;
    $auditUpdate$
LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS auditUpdate ON Product;
CREATE TRIGGER auditUpdate BEFORE Update ON Product 
FOR EACH ROW EXECUTE PROCEDURE updates();

CREATE OR REPLACE FUNCTION orderProduct()
    RETURNS "trigger" AS $updateProduct$
    BEGIN

        UPDATE Product p 
        SET numberOfUnits = p.numberOfUnits - NEW.unitsOrdered 
        WHERE p.storeID = NEW.storeID AND p.productName = NEW.productName;
        RETURN NEW;
    END;
$updateProduct$
LANGUAGE plpgsql VOLATILE;




DROP TRIGGER IF EXISTS updateProduct ON Orders;
CREATE TRIGGER updateProduct AFTER INSERT ON Orders 
FOR EACH ROW EXECUTE PROCEDURE orderProduct();

CREATE OR REPLACE FUNCTION resupply()
    RETURNS "trigger" AS $resup$
    BEGIN

        UPDATE Product p 
        SET numberOfUnits = p.numberOfUnits + NEW.unitsRequested 
        WHERE p.storeID = NEW.storeID AND p.productName = NEW.productName;
        RETURN NEW;
    END;
$resup$
LANGUAGE plpgsql VOLATILE;




DROP TRIGGER IF EXISTS resup ON ProductSupplyRequests;
CREATE TRIGGER resup AFTER INSERT ON ProductSupplyRequests 
FOR EACH ROW EXECUTE PROCEDURE resupply();
