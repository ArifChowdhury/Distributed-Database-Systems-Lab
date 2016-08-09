import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;


public class SampleForPackage_SearchMovies {

	public static void getMovies() {
		System.out.println("\n############ <1972>, release-year ###########\n");
		getMoviesForSearchKeyAndTypeOfKeys("1972", "release-year");
		System.out.println("\n############ <04-MAR-16>, release-date ###########\n");
		getMoviesForSearchKeyAndTypeOfKeys("04-MAR-16", "release-date");
		System.out.println("\n############ <10>, genre ###########\n");
		getMoviesForSearchKeyAndTypeOfKeys("10", "genre");
		System.out.println("\n############ <PG-13>, maturity ###########\n");
		getMoviesForSearchKeyAndTypeOfKeys("PG-13", "maturity");
		System.out.println("\n############ <The father> ###########\n");
		getMoviesForSearchKey("The father");
		System.out.println("\n############ <> ###########\n");
		getMoviesForSearchKey("");
		System.out.println("\n############ <>, maturity ###########\n");
		getMoviesForSearchKeyAndTypeOfKeys("", "maturity");
		System.out.println("\n############ <1899>, release-year ###########\n");
		getMoviesForSearchKeyAndTypeOfKeys("1899", "release-year");
		System.out.println("\n############ <04-MAR-1816>, release-date ###########\n");
		getMoviesForSearchKeyAndTypeOfKeys("04-MAR-1816", "release-date");
		System.out.println("\n############ <04-03-16>, release-date ###########\n");
		getMoviesForSearchKeyAndTypeOfKeys("04-03-16", "release-date");

	}

	private static void getMoviesForSearchKey(String searchKey) {
		try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			call = conn.prepareCall ("{ call search_movies.GET_MOVIES( '"+searchKey+"', ?)}");
			call.registerOutParameter (1, OracleTypes.CURSOR);  
			call.execute ();
			ResultSet rs = (ResultSet)call.getObject (1);
			printResultSet(rs);
			rs.close();
			call.close();
			conn.close();

		}catch (SQLException  e) {
			printErrorMessageForCustomExceptions(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	public static void getMoviesForSearchKeyAndTypeOfKeys(String searchKey, String keyType){

		try{
			//## Another way:
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521:xe", "MOVIE","MOVIE");
			CallableStatement call = null;
			if("release-date".equals(keyType)){
				if(searchKey.matches("\\d{2}-\\w{3}-\\d{2}"))
				call = conn.prepareCall ("{ call search_movies.GET_MOVIES('"+searchKey+"', '"+keyType+"', ?)}");
				else{
					System.out.println("Enter the correct format of 'date'. The format id 'DD-MMM-YY' e.g. '04-MAR-16'");
					return;
				}
			}else if("release-year".equals(keyType)){
				call = conn.prepareCall ("{ call search_movies.GET_MOVIES("+searchKey+", '"+keyType+"', ?)}");
			}else if("genre".equals(keyType)){
				call = conn.prepareCall ("{ call search_movies.GET_MOVIES("+searchKey+", '"+keyType+"', ?)}");
			}else if("maturity".equals(keyType)){
				call = conn.prepareCall ("{ call search_movies.GET_MOVIES( '"+searchKey+"', '"+keyType+"', ?)}");
			}else{
				call = conn.prepareCall ("{ call search_movies.GET_MOVIES( '"+searchKey+"', ?)}");
			}
			call.registerOutParameter (1, OracleTypes.CURSOR);  
			call.execute ();
			ResultSet rs = (ResultSet)call.getObject (1);
			printResultSet(rs);
			rs.close();
			call.close();
			conn.close();

		}catch (SQLException e) {
			printErrorMessageForCustomExceptions(e);
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	private static void printErrorMessageForCustomExceptions(SQLException e) {
		if(e.getErrorCode() == 20002)
			System.out.println("ORA-20002: The search-key can not be null.");
		else if(e.getErrorCode() == 20003)
			System.out.println("ORA-20003: The release-year can not be less than 1900.");
	}

	private static void printResultSet(ResultSet rs) throws SQLException {
		while (rs.next()) {
			System.out.println("\nName: "+rs.getString("movieName") 
					+ "\t\t" + rs.getString("maturity")
					+ "\tGenre: " + rs.getString("genre")
					+ "\nRealease Date: " + rs.getString("releaseDate").replaceAll("00:00:00.0", "")
					+ "\tDuration: " + rs.getString("duration")+" mins"
					+ "\nTrailer ID: " + rs.getString("trailer")
					+ "\t\tPoster: " + rs.getString("image")
					+ "\nSummary: " + rs.getString("summary")
					);

		}
	}

	public static void main(String args[]){
		SampleForPackage_SearchMovies.getMovies();
	}

}
