import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class DBUpdateAndRollback {
	
	public static void Sample1(String myField, String condition1, String condition2) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/test", "user", "password");
		connection.setAutoCommit(false);
		
		PreparedStatement ps = connection.prepareStatement("UPDATE myTable SET myField = ? WHERE myOtherField1 = ? AND myOtherField2 = ?");
		ps.setString(1, myField);
		ps.setString(2, condition1);
		ps.setString(3, condition2);
		
		// If more than 10 entries change, panic and rollback
		int numChanged = ps.executeUpdate();
		if(numChanged > 10) {
			connection.rollback();
		} else {
			connection.commit();
		}
		
		ps.close();
		connection.close();
	}
	
	public static void Sample2(String myField, String condition1, String condition2) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/test", "user", "password");
		connection.setAutoCommit(false);
		
		Statement st = connection.createStatement();
		
		String sql = "UPDATE myTable SET myField = '" + myField + "' WHERE myOtherField1 = '" + condition1 + "' AND myOtherField2 = '" + condition2 + "'";
		
		int numChanged = st.executeUpdate(sql);
		
		// If more than 10 entries change, panic and rollback
		if(numChanged > 10) {
			connection.rollback();
		} else {
			connection.commit();
		}
		
		st.close();
		connection.close();
	}

}
