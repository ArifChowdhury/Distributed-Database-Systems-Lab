import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;


public class Sample_for_package_get_schedule {

	public static void get_schedule() {
		get_schedule_for_single_movie("The Godfather", "04-08-16");//<-- Should print an error message because the date format is not correct.
		get_schedule_for_single_movie("The Godfather", "04-AUG-16");
		get_schedule_for_date("04-08-16");//<-- Should print an error message because the date format is not correct.
		get_schedule_for_date("04-AUG-16");
	}

	private static void get_schedule_for_date(String date_input) {
		System.out.println("\n########Showing the movies list with schedule for the date: "+date_input+"########\n");
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call P_GET_SCHEDULE.FOR_DATE('"+date_input+"', ?)}");
			call.registerOutParameter (1, OracleTypes.CURSOR);
			call.execute ();
			ResultSet rs = (ResultSet)call.getObject (1);
			System.out.println("Date || Movie name || Hall-no || Showtime || Format\n");
			printResultSet(date_input, rs);
			rs.close();
			call.close();
			conn.close();
		}catch (SQLException e) {
			printSQLERRM(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	private static void printResultSet(String date_input, ResultSet rs)
			throws SQLException {
		while (rs.next()) {
			System.out.println(date_input+" || "+rs.getString("MOVIENAME")+" || "+rs.getString("HALLNO")+" || "+rs.getString("SHOWTIME")+" || "+rs.getString("FORMAT")+"D");
		}
	}

	public static void get_schedule_for_single_movie(String movieName, String date){
		System.out.println("\n########<"+movieName+"> :: <"+date+">########\n");
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call P_GET_SCHEDULE.FOR_MOVIE('"+movieName+"', '"+date+"', ?)}");
			call.registerOutParameter (1, OracleTypes.CURSOR);
			call.execute ();
			ResultSet rs = (ResultSet)call.getObject (1);
			System.out.println("Movie name || Date || Hall-no || Showtime || Format");
			while (rs.next()) {
				System.out.println(movieName+" || "+ date+" || "+rs.getString("HALLNO")+" || "+rs.getString("SHOWTIME")+" || "+rs.getString("FORMAT")+"D");
			}
			rs.close();
			call.close();
			conn.close();

		}catch (SQLException e) {
			printSQLERRM(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	private static void printSQLERRM(SQLException e) {
		if(e.getErrorCode() == 20001)
			System.out.println("ORA-20001: The date inserted is not in the correct format. The correct format is 'DD-MON-YY' e.g. '30-JUL-16'.");
	}

	public static void main(String args[]){
		Sample_for_package_get_schedule.get_schedule();
	}
}
