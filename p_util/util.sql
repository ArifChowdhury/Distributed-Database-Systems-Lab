CREATE OR REPLACE PACKAGE util AS
  PROCEDURE is_time_valid(p_time IN VARCHAR2, p_validity OUT NUMBER);
END util;
/

CREATE OR REPLACE PACKAGE BODY util AS
 PROCEDURE is_time_valid(p_time IN VARCHAR2, p_validity OUT NUMBER) AS
  BEGIN
    IF REGEXP_LIKE(p_time, '((1[012])|(0[1-9])):\s?[0-5][0-9]\s?((AM)|(PM))|((am)|(pm))') THEN
      p_validity := 1;
    ELSE
      p_validity := 0;
    END IF;
  END is_time_valid;
END util;
/
--|| Testing util.is_time_valid ||--
SET SERVEROUTPUT ON;
DECLARE
  l_validity NUMBER;
BEGIN
  util.is_time_valid('21: 40 AM', l_validity);
  IF l_validity = 1 THEN 
    DBMS_OUTPUT.PUT_LINE('Valid');
  ELSIF l_validity = 0 THEN 
    DBMS_OUTPUT.PUT_LINE('Invalid');
  END IF;
END;
/