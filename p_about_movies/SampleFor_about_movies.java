import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;


public class SampleFor_about_movies {
	public static void aboutMovies(){
		System.out.println("<isMoviePlaying(\"The Godfather\", \"31-JUL-16\", \"11: 40 AM\", 6);>");
		isMoviePlaying("The Godfather", "31-JUL-16", "11: 40 AM", 6);

		System.out.println("\n<isMoviePlaying(\"The Conjuring\", \"31-JUL-16\", \"11: 40 AM\", 6);>");
		isMoviePlaying("The Conjuring", "31-JUL-16", "11: 40 AM", 6);

		System.out.println("\n<isMoviePlaying(\"The Godfather\", \"31-08-16\", \"11: 40 AM\", 6);>");
		isMoviePlaying("The Godfather", "31-08-16", "11: 40 AM", 6); //<- will throw EXCEPTION

		System.out.println("\n<isMoviePlaying(\"The Godfather\", \"31-JUL-16\", \"11:40 AM\", 6);>");
		isMoviePlaying("The Godfather", "31-JUL-16", "11:40 AM", 6); //<- will throw EXCEPTION

		System.out.println("\n<isMoviePlaying(\"The Godfather\", \"31-JUL-16\", \"31: 40 AM\", 6);>");
		isMoviePlaying("The Godfather", "31-JUL-16", "31: 40 AM", 6); //<- will throw EXCEPTION
	}

	private static void isMoviePlaying(String movieName, String showDate, String showTime, int hallNo) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ ? = call about_movies.is_movie_playing( '"+movieName+"', '"+showDate+"', '"+showTime+"', "+hallNo+")}");
			call.registerOutParameter (1, OracleTypes.NUMBER);
			call.execute ();

			int result = call.getInt(1);
			System.out.println((result==1)? "Playing" : "Not playing");

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
		else
			System.out.println(e.getLocalizedMessage());
	}

	public static void main (String arga[]){
		SampleFor_about_movies.aboutMovies();
	}
}
