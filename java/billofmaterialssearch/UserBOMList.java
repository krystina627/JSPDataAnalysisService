/*
	UserBOMList.jsp
	Purpose: Populate Dropdown BOM Selection Menus found on main BOM page.
	Author: Krystina Poling
	Date: Feb. 21, 2016
*/

package billofmaterialssearch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserBOMList {
	public int userID;
	public int bomID;
	PreparedStatement preparedStatement = null;
	ResultSet rs = null;
	Connection conn = DBConnection.getConnection();
	
	public UserBOMList(){
		
	}

	public UserBOMList(int userID) {
		this.userID = userID;
	}

	public int getUserID() {
		return this.userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

public ResultSet getUserBOMList(int userID) throws SQLException, ClassNotFoundException {
	 
	conn = DBConnection.getConnection();
	String selectSQL = "SELECT BOM_Title, BOM_ID FROM BOM WHERE UserID = ?";
	
		preparedStatement = conn.prepareStatement(selectSQL);
		preparedStatement.setInt(1, userID);

		// execute select SQL statement
		rs = preparedStatement.executeQuery();
	
	return rs;
}
public void closeConnection() {
	try {
		rs.close();
	} catch (Exception e) {
	}
	try {
		preparedStatement.close();
	} catch (Exception e) {
	}
	try {
		conn.close();
	} catch (Exception e) {
	}
}
}
