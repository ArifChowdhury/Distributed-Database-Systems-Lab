--|| Package Specification:
CREATE OR REPLACE PACKAGE search_movies AS
  PROCEDURE get_movies (search_key_input IN VARCHAR2, key_type_input IN VARCHAR2, c_movie_list OUT SYS_REFCURSOR);
  PROCEDURE get_movies (search_key_input IN VARCHAR2, c_movie_list OUT SYS_REFCURSOR);
END search_movies;
/
--|| Body of the Package
CREATE OR REPLACE PACKAGE BODY search_movies AS
  l_statement VARCHAR2(255);
  ex_search_key_can_not_be_null EXCEPTION;
  PRAGMA EXCEPTION_INIT( ex_search_key_can_not_be_null, -20002);
  ex_r_yr_cn_nt_be_lss_thn_1990 EXCEPTION;
  PRAGMA EXCEPTION_INIT( ex_r_yr_cn_nt_be_lss_thn_1990, -20003);
  
  
  PROCEDURE get_movies_helper_show_query(search_key IN OUT VARCHAR2, key_type IN VARCHAR2, movie_list OUT SYS_REFCURSOR) AS
    l_statement VARCHAR2(500);
    temp_search_key NUMBER;
  BEGIN  
    IF search_key IS NULL THEN
        RAISE_APPLICATION_ERROR(-20002, 'Search-key can not be NULL.');
    END IF;
    l_statement := 'SELECT MOVIES.MOVIENAME, MOVIES.RELEASEDATE, 
    MOVIES.DURATION, MOVIES.GENRE, MOVIES.MATURITY, MOVIES.SUMMARY, MOVIES.TRAILER, MOVIES.IMAGE, MOVIES_IN_THEATER.NOW_PLAYING 
    FROM MOVIES, MOVIES_IN_THEATER';
    IF  key_type = 'release-date' THEN 
      IF TO_DATE(search_key) < TO_DATE('01-JAN-1990') THEN
        RAISE_APPLICATION_ERROR(-20003, 'The release-year can not be less than 1900.');
      END IF;
      l_statement := l_statement || ' WHERE MOVIES.RELEASEDATE = ''' || search_key || '''';
    ELSIF key_type = 'maturity' THEN 
      l_statement := l_statement || ' WHERE MOVIES.MATURITY = ''' || search_key || '''';
    ELSIF key_type = 'genre' THEN 
      temp_search_key := TO_NUMBER(search_key, '999');
      l_statement := l_statement || ' WHERE MOVIES.GENRE = ' || search_key;
    ELSIF key_type = 'release-year' THEN
      IF search_key < 1900 THEN
        RAISE_APPLICATION_ERROR(-20003, 'The release-year can not be less than 1900.');
      END IF;
      temp_search_key := TO_NUMBER(search_key, '9999');
      l_statement := l_statement || ' WHERE to_char(RELEASEDATE, ''YYYY'') = ' || search_key;
    END IF;
    l_statement := l_statement || ' AND MOVIES.MOVIEID = MOVIES_IN_THEATER.MOVIEID';
    --DBMS_OUTPUT.PUT_LINE(l_statement);
    OPEN movie_list FOR l_statement;
  END get_movies_helper_show_query;

  
  PROCEDURE get_movies (search_key_input IN VARCHAR2, key_type_input IN VARCHAR2, c_movie_list OUT SYS_REFCURSOR) AS
      l_search_key VARCHAR2(100):= search_key_input;
    BEGIN
      get_movies_helper_show_query( search_key => l_search_key, key_type => key_type_input, movie_list => c_movie_list);
    END get_movies;
    
  PROCEDURE get_movies (search_key_input IN VARCHAR2, c_movie_list OUT SYS_REFCURSOR) AS
      keys_array apex_application_global.vc_arr2;
      l_statement VARCHAR2(500) := 'SELECT DISTINCT m.MOVIENAME, m.RELEASEDATE, m.DURATION, m.GENRE, m.MATURITY, m.SUMMARY, m.TRAILER, m.IMAGE, mit.NOW_PLAYING FROM MOVIES m JOIN MOVIES_IN_THEATER mit ON m.MOVIEID = mit.MOVIEID';
    BEGIN
      IF search_key_input IS NULL THEN
        RAISE_APPLICATION_ERROR(-20002, 'Search-key can not be NULL.');
      END IF;
    
      keys_array := apex_util.string_to_table(search_key_input, ' ');
      FOR i IN 1..keys_array.count
      LOOP
        IF i = 1 THEN
          l_statement := l_statement || ' WHERE' || ' m.MOVIENAME LIKE ''%'|| keys_array(i) ||'%''';
        ELSE
          l_statement := l_statement || ' OR ' || ' m.MOVIENAME LIKE ''%'|| keys_array(i) ||'%''';
        END IF;
      END LOOP;
      OPEN c_movie_list FOR l_statement;
 END get_movies; 
END search_movies;
/

--### Verifying the Package ###--
SET SERVEROUTPUT ON
DECLARE
  l_cursor  SYS_REFCURSOR;
  l_movie_name   MOVIES.MOVIENAME%TYPE;
  l_movie_releasedate   MOVIES.RELEASEDATE%TYPE;
  l_movie_duration   MOVIES.DURATION%TYPE;
  l_movie_genre   MOVIES.GENRE%TYPE;
  l_movie_maturity   MOVIES.MATURITY%TYPE;
  l_movie_summary   MOVIES.SUMMARY%TYPE;
  l_movie_trailer   MOVIES.TRAILER%TYPE;
  l_movie_image   MOVIES.IMAGE%TYPE;
  l_status MOVIES_IN_THEATER.NOW_PLAYING%TYPE;

BEGIN
  
  --search_movies.GET_MOVIES( search_key_input => '1', key_type_input => 'genre', c_movie_list => l_cursor); --> NOT OK
  --search_movies.GET_MOVIES( search_key_input => 'PG-13', key_type_input => 'maturity', c_movie_list => l_cursor); --> NOT OK
  search_movies.GET_MOVIES( search_key_input => '04-MAR-16', key_type_input => 'release-date', c_movie_list => l_cursor); --> NOT OK
  --search_movies.GET_MOVIES( search_key_input => '1972', key_type_input => 'release-year', c_movie_list => l_cursor); --> NOT OK
  search_movies.GET_MOVIES( search_key_input => 'The father', c_movie_list => l_cursor); --> OK
  --|| TESTING the Exception-Handling ||--
  --search_movies.GET_MOVIES( search_key_input => '', c_movie_list => l_cursor);
  --search_movies.GET_MOVIES( search_key_input => '', key_type_input => 'maturity', c_movie_list => l_cursor);
  --search_movies.GET_MOVIES( search_key_input => '04-MAR-1816', key_type_input => 'release-date', c_movie_list => l_cursor);
  --search_movies.GET_MOVIES( search_key_input => 1872, key_type_input => 'release-year', c_movie_list => l_cursor);
  --search_movies.GET_MOVIES( search_key_input => 'god father', c_movie_list => l_cursor);          
  LOOP 
    FETCH l_cursor INTO  l_movie_name, l_movie_releasedate, l_movie_duration, l_movie_genre, l_movie_maturity, l_movie_summary, l_movie_trailer, l_movie_image, l_status;
    EXIT WHEN l_cursor%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE(l_movie_name || '(' || l_movie_releasedate || ') ' || l_movie_maturity);
    DBMS_OUTPUT.PUT_LINE('     Duration: ' || l_movie_duration || ' Genre: ' || l_movie_genre);
    DBMS_OUTPUT.PUT_LINE('     Summary: ' || l_movie_summary);
    DBMS_OUTPUT.PUT_LINE('     Trailer ID: ' || l_movie_trailer);
    DBMS_OUTPUT.PUT_LINE('     Poster: ' || l_movie_image);
    DBMS_OUTPUT.PUT_LINE('     Status: ' || l_status);
  END LOOP;
  CLOSE l_cursor;
END;
/