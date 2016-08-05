CREATE OR REPLACE
PACKAGE p_get_schedule AS
  PROCEDURE for_date(date_input IN VARCHaR2, refc_result_set OUT SYS_REFCURSOR);
  PROCEDURE for_movie(movie_name IN MOVIES.MOVIENAME%TYPE, date_input IN VARCHAR2, refc_result_set OUT SYS_REFCURSOR);
END p_get_schedule;

CREATE OR REPLACE
PACKAGE BODY p_get_schedule AS
  FUNCTION is_valid_date(p_format IN VARCHAR2 ) 
    RETURN BOOLEAN IS
      v_date DATE := NULL;
      ex_invalid_date_format EXCEPTION;
      PRAGMA EXCEPTION_INIT( ex_invalid_date_format, -20001 );
    BEGIN
      if not REGEXP_LIKE (p_format, '^\d{2}-\d{2}-\d{2}$') then
        raise_application_error( -20001, 'The date inserted is not in the correct format. The correct format id ''dd-mm-yy''' );
      END IF;
      select to_date(p_format,'dd-mm-yy') into v_date from dual;
      RETURN TRUE;
    EXCEPTION
      WHEN OTHERS THEN
          DBMS_OUTPUT.PUT_LINE(sqlerrm );
        RETURN FALSE;
  END is_valid_date;

  PROCEDURE for_movie(
      movie_name IN MOVIES.MOVIENAME%TYPE,
      date_input IN VARCHAR2,
      refc_result_set OUT SYS_REFCURSOR)
      AS
  BEGIN
    IF (IS_VALID_DATE(date_input)) THEN
      OPEN refc_result_set FOR 
      SELECT SHOW_TIMES.HALLNO, TO_CHAR(SHOW_TIMES.SHOWDATETIME , 'HH12:MI AM') AS "SHOWTIME", SHOW_TIMES.FORMAT
      FROM MOVIES, SHOW_TIMES
      WHERE MOVIES.MOVIEID = SHOW_TIMES.MOVIEID
      AND TO_CHAR(SHOW_TIMES.SHOWDATETIME, 'DD-MM-YY') = date_input
      AND MOVIES.MOVIENAME LIKE '%'||movie_name||'%'
      ORDER BY SHOW_TIMES.SHOWDATETIME;
    END IF;
  END for_movie;

  PROCEDURE for_date(date_input IN VARCHaR2,
                                refc_result_set OUT SYS_REFCURSOR) AS
  BEGIN
    IF (IS_VALID_DATE(date_input)) THEN
      OPEN refc_result_set FOR
      SELECT SHOW_TIMES.HALLNO, MOVIES.MOVIENAME, SHOW_TIMES.FORMAT, TO_CHAR(SHOW_TIMES.SHOWDATETIME, 'HH12:MM AM') AS "SHOWTIME"
      FROM SHOW_TIMES, MOVIES
      WHERE MOVIES.MOVIEID = SHOW_TIMES.MOVIEID
      AND TO_CHAR(SHOW_TIMES.SHOWDATETIME, 'DD-MM-YY') = date_input
      ORDER BY SHOW_TIMES.SHOWDATETIME ASC;
    END IF;
  END for_date;
END p_get_schedule;
/

--|| Testing the package ||--
----|| Testing the procedure for_movie ||---
SET SERVEROUTPUT ON;
DECLARE
  result_set SYS_REFCURSOR;
  l_hall_no SHOW_TIMES.HALLNO%TYPE;
  l_showtime VARCHAR2(10);
  l_format SHOW_TIMES.FORMAT%TYPE;
BEGIN
  P_GET_SCHEDULE.FOR_MOVIE( movie_name => 'The Godfather',
                          date_input => '04-08-16',  --Change the date with invalid one or in the wrong format and it will be caught!
                          refc_result_set => result_set);
  LOOP                      
    FETCH result_set INTO l_hall_no, l_showtime, l_format;
    DBMS_OUTPUT.PUT_LINE('Hall No: '|| l_hall_no || ' Showtime: ' || l_showtime || ' Format: ' ||l_format||'D');
    EXIT WHEN result_set%NOTFOUND;
  END LOOP;
  EXCEPTION
    WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE(sqlerrm );
END;
/
----|| Testing the procedure for_date ||---
SET SERVEROUTPUT ON;
DECLARE
  l_date_input VARCHAR2(15) := '30-07-16' ;--Change the date with invalid one or in the wrong format and it will be caught!
  l_result_set SYS_REFCURSOR;
  l_hall_no SHOW_TIMES.HALLNO%TYPE;
  l_movie_name MOVIES.MOVIENAME%TYPE;
  l_format VARCHAR2(5);
  l_showtime VARCHAR2(10);
BEGIN
  P_GET_SCHEDULE.FOR_DATE(date_input => l_date_input,
                        refc_result_set => l_result_set);
  LOOP
    FETCH l_result_set INTO l_hall_no, l_movie_name, l_format, l_showtime;
    EXIT WHEN l_result_set%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE('Date: '||l_date_input || ' || Hall no: '|| l_hall_no 
    || ' || Movie name: ' || l_movie_name || ' || Format: ' 
    || l_format || 'D || Showtime: ' || l_showtime);
  END LOOP;
    EXCEPTION
    WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE(sqlerrm );
END;
/
