package billofmaterialssearch;
/*
 * Author: Krystina Poling
 * Data: 2/11/2016
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	public static Connection getConnection() {
		String username = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
		String password = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
		String dbName = System.getenv("OPENSHIFT_APP_NAME");
		String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
		String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return conn;
		

	}
	

}