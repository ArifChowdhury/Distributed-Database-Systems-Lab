import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;


public class SampleFor_edit_data {

	public static void edit_data() {
		instertRow("CUSTOMERS", "Zaman,zaman@mail.com,01545412");
		instertRow("CUSTOMERS", "Zaman,zaman@mail.com,01545412"); //<- Obviously, this will throw an EXCEPTION.
	}

	private static void instertRow(String tableName, String inputData) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call edit_data.insert_row( '"+tableName+"', '"+inputData+"', ?)}");
			call.registerOutParameter (1, OracleTypes.CURSOR);
			call.execute ();
			ResultSet rs = (ResultSet)call.getObject (1);
			while (rs.next()) {
				System.out.println("New Entry:\nID: "+rs.getString("CUSTOMERID")+" Name: "+rs.getString("NAME")+" Email: "+rs.getString("EMAILID")+" Phone: "+rs.getString("PHONE"));
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
		if(e.getErrorCode() == 00001)
			System.out.println("ORA-00001: unique constraint (MOVIE.SYS_C007563) violated");

	}

	public static void main(String args[]){
		SampleFor_edit_data.edit_data();
	}

}
