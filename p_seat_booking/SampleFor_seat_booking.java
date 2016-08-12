import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;


public class SampleFor_seat_booking {

	public static void seatBooking() {
		System.out.println("\n----------<For 'Zootopia(2016)'3D on the '31-JUL-16' at '11: 40 AM'>----------");
		getBookedSeats("Zootopia(2016)", 3, "31-JUL-16", "11: 40 AM");

		System.out.println("\n----------<For 'Zootopia(2016)'3D on the '31-07-16' at '11: 40 AM'>----------");
		getBookedSeats("Zootopia(2016)", 3, "31-07-16", "11: 40 AM"); //<-- Will throw an EXCEPTION

		System.out.println("\n----------<For 'Zootopia(2016)'3D on the '31-JUL-16' at '11:40 AM'>----------");
		getBookedSeats("Zootopia(2016)", 3, "31-JUL-16", "11:40 AM");//<-- Will throw an EXCEPTION
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
	}

	public static void main(String args[]){
		SampleFor_seat_booking.seatBooking();
	}
}
