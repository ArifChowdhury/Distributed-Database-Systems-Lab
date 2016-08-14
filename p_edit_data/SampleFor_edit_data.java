import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;


public class SampleFor_edit_data {

	public static void edit_data() {
		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("\tPROCEDURE: instertRow -> returns: the-inserted-row");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

		System.out.println("\nCUSTOMERS | Zaman,zaman@mail.com,01545412");
		instertRow("CUSTOMERS", "Zaman,zaman@mail.com,01545412");

		System.out.println("\nCUSTOMERS | Zaman,zaman@mail.com,01545412");
		instertRow("CUSTOMERS", "Zaman,zaman@mail.com,01545412"); //<- Obviously, this will throw an EXCEPTION.

		System.out.println("\nCUSTOMERS | sad,asda,ad,dsa");
		instertRow("CUSTOMERS", "sad,asda,ad,dsa"); //<-- Will throw an EXCEPTION.

		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("\tPROCEDURE: get_reflection -> returns: the-reflected-row");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

		System.out.println("\nCUSTOMERS | Zaman,zaman@mail.com,01545412");
		getReflectedRow("CUSTOMERS", "Zaman,zaman@mail.com,01545412");

		System.out.println("\nCUSTOMERS | Arif,Zaman,zaman@mail.com,01545412");
		getReflectedRow("CUSTOMERS", "Arif,Zaman,zaman@mail.com,01545412");

		System.out.println("\nCUSTOMERS | Zaman0,zaman@mail.com0,01546412");
		getReflectedRow("CUSTOMERS", "Zaman0,zaman@mail.com0,01546412");



	}

	private static void getReflectedRow(String tableName, String inputData) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call edit_data.get_reflection( '"+tableName+"', '"+inputData+"', ?)}");
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
		else if(e.getErrorCode() == 20007)
			System.out.println("ORA-20007: There is more or less than enough data for inserting into CUSTOMERS table.");
		else
			System.out.println(e.getMessage());

	}

	public static void main(String args[]){
		SampleFor_edit_data.edit_data();
	}

}
