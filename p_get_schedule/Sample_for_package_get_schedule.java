import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;


public class Sample_for_package_get_schedule {

	public Sample_for_package_get_schedule() {
		get_schedule_for_single_movie("The Godfather", "04-08-16");
		//get_schedule_for_single_movie("The Godfather", "04-AUG-1"); //<-- Should return error message because the date format is not correct.
		get_schedule_for_date("04-08-16");
	}

	private void get_schedule_for_date(String date) {
		System.out.println("\n########Showing the movies list with schedule for the date: "+date+"########\n");
		
		if(date.matches("\\d{2}-\\d{2}-\\d{2}"))
			// The date should be tested more specifically.
			try{
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
				CallableStatement call = null;
				call = conn.prepareCall ("{ call P_GET_SCHEDULE.FOR_DATE('"+date+"', ?)}");
				call.registerOutParameter (1, OracleTypes.CURSOR);  
				call.execute ();
				ResultSet rs = (ResultSet)call.getObject (1);
				System.out.println("Date || Movie name || Hall-no || Showtime || Format\n");
				while (rs.next()) {
					System.out.println(date+" || "+rs.getString("MOVIENAME")+" || "+rs.getString("HALLNO")+" || "+rs.getString("SHOWTIME")+" || "+rs.getString("FORMAT")+"D");
				}
				rs.close();
				call.close();
				conn.close();
			}catch (SQLException e) {
				System.out.println(e.getLocalizedMessage());
			}
		else {
			System.out.println("Enter the correct format of 'date'. The format id 'DD-MM-YY'");
		}



	}

	public void get_schedule_for_single_movie(String movieName, String date){
		System.out.println("\n########Showing the schedule for "+movieName+" on the date: "+date+"########\n");
		if(date.matches("\\d{2}-\\d{2}-\\d{2}"))
			// The date should be tested more specifically.
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
				System.out.println(e.getLocalizedMessage());
			}
		else {
			System.out.println("Enter the correct format of 'date'. The format id 'DD-MM-YY'");
		}

	}

	public static void main(String args[]){
		Sample_for_package_get_schedule smpl = new Sample_for_package_get_schedule();
	}
}
