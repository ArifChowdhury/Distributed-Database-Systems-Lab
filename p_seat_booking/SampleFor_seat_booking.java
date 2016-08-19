import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;


public class SampleFor_seat_booking {

	public static void seatBooking() {
		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("\tPROCEDURE: get_booked_seats -> returns: list-of-booked-seats.");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

		System.out.println("\n----------<For 'Zootopia(2016)'3D on the '31-JUL-16' at '11: 40 AM'>----------");
		getBookedSeats("Zootopia(2016)", 3, "31-JUL-16", "11: 40 AM");

		System.out.println("\n----------<For 'Zootopia(2016)'3D on the '31-07-16' at '11: 40 AM'>----------");
		getBookedSeats("Zootopia(2016)", 3, "31-07-16", "11: 40 AM"); //<-- Will throw an EXCEPTION

		System.out.println("\n----------<For 'Zootopia(2016)'3D on the '31-JUL-16' at '11:40 AM'>----------");
		getBookedSeats("Zootopia(2016)", 3, "31-JUL-16", "11:40 AM");//<-- Will throw an EXCEPTION

		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("\tPROCEDURE: get_booked_seats_with_prices -> returns: list-of-booked-seats-with-their-price.");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

		System.out.println("\n----------<For #Hall: 01|Date: 31-JUL-16|Time: 11: 40 AM|Movie-Format: 3D>----------");
		getBookedSeatsWithPrices(01, "31-JUL-16", "11: 40 AM", 3);

		System.out.println("\n----------<For #Hall: 01|Date: 31-JUL-2016|Time: 11: 40 AM|Movie-Format: 3D>----------");
		getBookedSeatsWithPrices(01, "31-JUL-2016", "11: 40 AM", 3);//<-- Will throw an EXCEPTION

		System.out.println("\n----------<For #Hall: 01|Date: 31-JUL-16|Time: 11: 40 |Movie-Format: 3D>----------");
		getBookedSeatsWithPrices(01, "31-JUL-16", "11: 40 ", 3);//<-- Will throw an EXCEPTION

		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("\tPROCEDURE: is_hall_full -> returns: NUMBER[01] houseful");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

		System.out.println("On the 31-JUL-16 at 11: 40 AM, is there any available seats left in the Hall-No 6?");
		isHallFull("31-JUL-16", "11: 40 AM", 6);

		System.out.println("On the 31-JUL-16 at 11: 40 AM, is there any available seats left in the Hall-No 1?");
		isHallFull("31-JUL-16", "11: 40 AM", 1);

		System.out.println("On the 31-JUL-16 at 11: 40 am, is there any available seats left in the Hall-No 6?");
		isHallFull("31-JUL-16", "11: 40 am", 6);//<-- Will throw an EXCEPTION

		System.out.println("On the 31-JUL-16 at 21: 40 AM, is there any available seats left in the Hall-No 1?");
		isHallFull("31-JUL-16", "21: 40 AM", 1);//<-- Will throw an EXCEPTION

		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("\tPROCEDURE: is_seat_available -> returns: NUMBER[01] availability");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

		System.out.println("SeatNo: LB34 | HallNo: 5 | Date: 31-JUL-16 | Time: 05: 00 PM");
		isSeatAvailable("31-JUL-16", "05: 00 PM", 5, "LB34");

		System.out.println("SeatNo: LA34 | HallNo: 5 | Date: 31-JUL-16 | Time: 05: 00 PM");
		isSeatAvailable("31-JUL-16", "05: 00 PM", 5, "LA34");
		//All the EXCEPTIONs shown in previous cases are also applicable here too.

		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("\tPROCEDURE: are_seats_available -> returns: NUMBER[01] availability, list_booked_seats");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

		System.out.println("SeatNo: LA01,LA02,LA03,LA04 | HallNo: 6 | Date: 31-JUL-16 | Time: 11: 40 AM");
		areSeatsAvailable("31-JUL-16", "11: 40 AM", 6, "LA01,LA02,LA03,LA04");

		System.out.println("\nSeatNo: LA03,LA04 | HallNo: 6 | Date: 31-JUL-16 | Time: 11: 40 AM");
		areSeatsAvailable("31-JUL-16", "11: 40 AM", 6, "LA03,LA04");
		//All the EXCEPTIONs shown in previous cases are also applicable here too.

		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("\tPROCEDURE: get_ticket_price -> returns: price_of_ticket");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

		System.out.println("\nHall-no: 1 | Format: 3");
		getTicketPrice(1, 3);

		System.out.println("\nHall-no: 1 | Format: 2");
		getTicketPrice(1, 2);

		System.out.println("\nHall-no: 2 | Format: 3");
		getTicketPrice(2, 3);

		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("\tPROCEDURE: book_seats -> returns: newly-entered-PURCHASEID");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

		//Note: Now it intakes movieName instead of movieId
		System.out.println("\n\n>>User: auzchowdhury,auzchowdhury@Gmail.com,02557446802 ||| Tickets: LE36,UB15,LA51 \n>>Date: 04-AUG-2016 ||| Time: 05: 00 PM ||| HallNo: 5 ||| MovieName: 'The Godfather' ||| Format: 2");
		bookSeats("auzchowdhury,auzchowdhury@Gmail.com,02557446802", "LE36,UB15,LA51", "04-AUG-2016", "05: 00 PM", 5, "The Godfather", 2);

		System.out.println("\n\n>>User: auzchowdhury,auzchowdhury@Gmail.com,02557446802 ||| Tickets: LE36,UB15,LA51 \n>>Date: 31-JUL-2016 ||| Time: 11: 30 AM ||| HallNo: 6 ||| MovieName: 'The Godfather' ||| Format: 3");
		bookSeats("auzchowdhury,auzchowdhury@Gmail.com,02557446802", "LE36,UB15,LA51", "31-JUL-2016", "11: 30 AM", 6, "The Godfather", 3);//<-- Will throw an EXCEPTION

	}

	private static void bookSeats(String customerData, String seatsToBook, String showDate, String showTime, int hallNo, String movieName, int movieFormat) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call seat_booking.book_seats( '"+customerData+"',"
					+ " '"+seatsToBook+"',"
					+ " '"+showDate+"',"
					+ " '"+showTime+"',"
					+ " "+hallNo+","
					+ " '"+movieName+"',"
					+ " "+movieFormat+","
					+ " ?)}");
			call.registerOutParameter (1, OracleTypes.INTEGER);
			call.execute ();
			System.out.println("New PurchaseID: "+call.getInt(1));
			call.close();
			conn.close();
		}catch (SQLException  e) {
			printSQLERRM(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	private static void getTicketPrice(int hallNo, int format) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call seat_booking.get_ticket_price( "+hallNo+", "+format+", ?)}");
			call.registerOutParameter (1, OracleTypes.NUMBER);
			call.execute ();
			System.out.println("Price:\t"+call.getInt(1));
			call.close();
			conn.close();
		}catch (SQLException  e) {
			printSQLERRM(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	private static void areSeatsAvailable(String showDate, String showTime, int hallNo,
			String seatNoList) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call seat_booking.are_seats_available( '"+showDate+"', '"+showTime+"', "+hallNo+", '"+seatNoList+"', ?, ?)}");
			call.registerOutParameter (1, OracleTypes.NUMBER);
			call.registerOutParameter (2, OracleTypes.VARCHAR);
			call.execute ();
			System.out.println("The seats you queried for:\n"+seatNoList);
			System.out.println((call.getInt(1) == 1)?"<<Available>>":"<<Not available>>\nSeats that were already booked:\n"+call.getString(2));
			call.close();
			conn.close();
		}catch (SQLException  e) {
			printSQLERRM(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	private static void isSeatAvailable(String showDate, String showTime, int hallNo, String seatNo) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call seat_booking.is_seat_available( '"+showDate+"', '"+showTime+"', "+hallNo+", '"+seatNo+"', ?)}");
			call.registerOutParameter (1, OracleTypes.NUMBER);
			call.execute ();
			System.out.println((call.getInt(1)==1)? "Available." : "Not availabe.");
			call.close();
			conn.close();
		}catch (SQLException  e) {
			printSQLERRM(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	private static void isHallFull(String showDate, String showTime, int hallNo) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call seat_booking.is_hall_full( '"+showDate+"', '"+showTime+"', "+hallNo+", ?)}");
			call.registerOutParameter (1, OracleTypes.NUMBER);
			call.execute ();
			if(call.getInt(1) == 1)
				System.out.println("\tYes");
			else if(call.getInt(1) == 0)
				System.out.println("\tNo");
			call.close();
			conn.close();
		}catch (SQLException  e) {
			printSQLERRM(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	private static void getBookedSeatsWithPrices(int hallNo, String showDate,
			String showTime, int movieFormat) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call seat_booking.get_booked_seats_with_prices( "+hallNo+", '"+showDate+"', '"+showTime+"', "+movieFormat+", ?)}");
			call.registerOutParameter (1, OracleTypes.CURSOR);
			call.execute ();
			ResultSet rs = (ResultSet)call.getObject (1);
			System.out.println("Booked seats are: ");
			while (rs.next()) {
				System.out.println("\t\t#Seat: "+rs.getString("SEATNO")+" | Price: "+rs.getString("PRICE"));
			}
			rs.close();
			call.close();
			conn.close();
		}catch (SQLException  e) {
			printSQLERRM(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	private static void getBookedSeats(String movieName, int foramt, String showDate, String showTime) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call seat_booking.get_booked_seats( '"+movieName+"', "+foramt+", '"+showDate+"', '"+showTime+"', ?)}");
			call.registerOutParameter (1, OracleTypes.CURSOR);
			call.execute ();
			ResultSet rs = (ResultSet)call.getObject (1);
			System.out.println("Booked seats are: ");
			while (rs.next()) {
				System.out.println("\t\t"+rs.getString("SEATNO")+" ");
			}
			rs.close();
			call.close();
			conn.close();
		}catch (SQLException  e) {
			printSQLERRM(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	private static void printSQLERRM(SQLException e) {
		if(e.getErrorCode() == 20001)
			System.out.println("ORA-20001: The date inserted is not in the correct format. The correct format is 'DD-MON-YY' e.g. '30-JUL-16'.");
		else if(e.getErrorCode() == 20004)
			System.out.println("ORA-20004: The time inserted is not in the correct format. The correct format is 'HH: MI AM' e.g. '11: 40 AM'.");
		else if(e.getErrorCode() == 20006)
			System.out.println("ORA-20006: The time is not valid.");
		else if(e.getErrorCode() == 20007)
			System.out.println("ORA-20007: Illegal attempt to reserve seats since one or all of the selected seats are already reserved.");
		else if(e.getErrorCode() == 20009)
			System.out.println("ORA-20009: There is no such schedule for the movie inserted.");
		else
			System.out.println(e.getLocalizedMessage());
	}

	public static void main(String args[]){
		SampleFor_seat_booking.seatBooking();
	}
}
