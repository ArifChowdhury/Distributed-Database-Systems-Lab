CREATE OR REPLACE
PACKAGE seat_booking AS
  PROCEDURE get_booked_seats ( p_movie_name IN MOVIES.MOVIENAME%TYPE, 
                p_format IN NOW_SHOWING_MOVIES.FORMAT%TYPE, p_show_date IN VARCHAR2, p_show_time IN VARCHAR2, p_seat_numbers OUT SYS_REFCURSOR);
END seat_booking;
/

CREATE OR REPLACE
PACKAGE BODY seat_booking AS
  
  PROCEDURE get_hall_no (p_movie_name_input IN MOVIES.MOVIENAME%TYPE, 
                p_format_input IN NOW_SHOWING_MOVIES.FORMAT%TYPE, 
                p_show_date_input IN VARCHAR2, p_show_time_input IN VARCHAR2, 
                p_hall_no_ouput OUT CINEMA_HALLS.HALLNO%TYPE)AS
  BEGIN
    SELECT st.HALLNO
    INTO p_hall_no_ouput
    FROM SHOW_TIMES st, MOVIES m
    WHERE  m.MOVIENAME = p_movie_name_input
    AND m.MOVIEID = st.MOVIEID
    AND st.FORMAT = p_format_input
    AND TO_CHAR(st.SHOWDATETIME, 'DD-MON-YY') = p_show_date_input
    AND TO_CHAR(st.SHOWDATETIME, 'HH12: MI AM') = p_show_time_input;
  END get_hall_no;
  
  PROCEDURE get_booked_seats_for_hn_n_t( hall_no IN CINEMA_HALLS.HALLNO%TYPE, show_date IN VARCHAR2,
                             show_time IN VARCHAR2, seat_numbers OUT SYS_REFCURSOR) AS
  BEGIN
    OPEN seat_numbers FOR
    SELECT SEATNO
    FROM BOOKED_SEATS
    WHERE TO_DATE(SHOWDATETIME) = TO_DATE(show_date)
    AND TO_CHAR(SHOWDATETIME, 'HH12: MI AM') = show_time
    AND HALLNO = hall_no
    ORDER BY SEATNO;
  END get_booked_seats_for_hn_n_t;

  PROCEDURE get_booked_seats ( p_movie_name IN MOVIES.MOVIENAME%TYPE, 
                p_format IN NOW_SHOWING_MOVIES.FORMAT%TYPE, p_show_date IN VARCHAR2, p_show_time IN VARCHAR2, p_seat_numbers OUT SYS_REFCURSOR) AS
    l_hall_no CINEMA_HALLS.HALLNO%TYPE;
    ex_invalid_time_format EXCEPTION;
    PRAGMA EXCEPTION_INIT( ex_invalid_time_format, -20001 );
  BEGIN
    IF NOT REGEXP_LIKE (p_show_time, '^\d{2}:\s{1}\d{2}\s{1}[AP]M$') THEN
      RAISE_APPLICATION_ERROR( -20004, 'The time inserted is not in the correct format. The correct format is ''HH: MI AM'' e.g. ''11: 40 AM''.' ); 
    END IF;
    IF NOT REGEXP_LIKE (p_show_date, '^\d{2}-\w{3}-\d{2}$') THEN
      RAISE_APPLICATION_ERROR( -20001, 'The date inserted is not in the correct format. The correct format is ''DD-MON-YY'' e.g. ''30-JUL-16''.' ); 
    END IF;  
    seat_booking.get_hall_no (p_movie_name_input => p_movie_name, 
                            p_format_input => p_format, 
                            p_show_date_input => p_show_date, 
                            p_show_time_input => p_show_time, 
                            p_hall_no_ouput => l_hall_no);
    get_booked_seats_for_hn_n_t(hall_no => l_hall_no, 
                                    show_date => p_show_date,
                                    show_time => p_show_time,
                                    seat_numbers => p_seat_numbers);
  END get_booked_seats;
END seat_booking;
/

--|| Test get_booked_seats||--
SET SERVEROUTPUT ON;
DECLARE
  l_booked_seats SYS_REFCURSOR;
  seat_no VARCHAR2(4);
BEGIN
  seat_booking.get_booked_seats(p_movie_name => 'Zootopia(2016)', 
                                p_format => 3,
                                p_show_date => '31-JUL-16',
                                p_show_time => '11: 40 AM',
                                p_seat_numbers => l_booked_seats);
  LOOP
    FETCH l_booked_seats INTO seat_no;
    EXIT WHEN l_booked_seats%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE('Seat no: '||seat_no);
  END LOOP;
END;
/
