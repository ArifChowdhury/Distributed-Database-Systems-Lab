CREATE OR REPLACE
PACKAGE edit_data AS
  PROCEDURE insert_row(table_name IN VARCHAR2, input_data IN VARCHAR2, new_row OUT SYS_REFCURSOR);
END edit_data;
/

CREATE OR REPLACE
PACKAGE BODY edit_data AS
  PROCEDURE insert_row(table_name IN VARCHAR2, input_data IN VARCHAR2,
                       new_row OUT SYS_REFCURSOR) AS
    data_array apex_application_global.vc_arr2;
    new_pk NUMBER;
  BEGIN
    data_array := apex_util.string_to_table(input_data, ',');
    IF table_name = 'CUSTOMERS' OR table_name = 'customers' THEN
      INSERT INTO CUSTOMERS(CUSTOMERID, NAME, EMAILID, PHONE)
      VALUES (SEQ_CUSTOMERS_ID.NEXTVAL, data_array(1), data_array(2), data_array(3))
      RETURNING CUSTOMERID INTO new_pk;
    END IF;
    OPEN new_row FOR SELECT * FROM CUSTOMERS WHERE CUSTOMERID = new_pk;
  END insert_row;
END edit_data;
/

--|| Testing edit_data.insert_row ||--
SET SERVEROUTPUT ON;
DECLARE
  l_new_row SYS_REFCURSOR;
  customer_row CUSTOMERS%ROWTYPE;
BEGIN
  edit_data.insert_row(table_name => 'customers',
                      input_data => 'hbjbddv0,kjs0kvcbj@hh.com, 0488542154',
                      new_row => l_new_row);
  LOOP
    FETCH l_new_row INTO customer_row;
    EXIT WHEN l_new_row%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE('ID: '||customer_row.CUSTOMERID || ' Name: ' || customer_row.NAME || ' Email: ' || customer_row.EMAILID || ' Phone: ' || customer_row.PHONE);
  END LOOP;
END;
/
