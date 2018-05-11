package billofmaterialssearch;
/*	BOM class
 * 	v3.0
 * 	Matt Martino, SQL cleanup by Krystina Poling
 * 	Team 5
 *  Feb 27, 2016
 */

import java.util.ArrayList;
import java.sql.*;
import java.text.*;


public class BOM {
	
	String bomTitle, partNum, partDescription;
	int cost, vendorID, bomID, componentID, userID;
	ArrayList<Component> BOMdataLines = new ArrayList<Component>();
	PreparedStatement SQLstatement = null;
	ResultSet rs = null;
	Connection conn = DBConnection.getConnection();
	

	// constructor requires user ID and the BOM name to be included
	public BOM(int uid, String n) throws SQLException 
	{
		this.userID = uid;
		this.bomTitle = n;
		conn = DBConnection.getConnection();
		String insert = "INSERT INTO BOM (UserID, BOM_Title) VALUES (?,?)";
		SQLstatement = conn.prepareStatement(insert);
		SQLstatement.setInt(1, userID);
		SQLstatement.setString(2, bomTitle);

		try {
			SQLstatement.execute();
			bomID = newBOMIDquery();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		// this hook ensures that the jdbc connection is closed
		// when the class is closed
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				closeConnection();
			}
		});
	}

	// this constructor is NOT to create a new BOM in the database, but rather
	// query an existing one
	public BOM(int b_ID) throws SQLException
	{
		this.bomID = b_ID;
		conn = DBConnection.getConnection();
		String query = "SELECT BOM_Title FROM BOM WHERE BOM_ID = ?";
		SQLstatement = conn.prepareStatement(query);
		SQLstatement.setInt(1,bomID);
		
		try 
		{
			ResultSet resSet = SQLstatement.executeQuery();
			resSet.next();
			bomTitle = resSet.getString("BOM_Title");
			
			updateComponentList();
		} 
		catch (SQLException e) 
		{
			System.out.println(e.getMessage());
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				closeConnection();
			}
		});
	}

	
	public int getBOMid() 
	{
		return bomID;
	}
	
	public String getBOMtitle() 
	{
		String temp = new String(bomTitle);
		return temp;
	}

	public void addComponent(String pn, String pd, int q) throws SQLException 
	{
		conn = DBConnection.getConnection();
		Component c = new Component(pn, pd, q);
		BOMdataLines.add(c);
		String query = "INSERT INTO Component (BOM_ID, PartNumber, Quantity, Prt_Description) VALUES (?, ?, ?, ?)";
		SQLstatement = conn.prepareStatement(query);
		SQLstatement.setInt(1, bomID);
		SQLstatement.setString(2, pn);
		SQLstatement.setInt(3, q);
		SQLstatement.setString(4, pd);

		try
		{
			SQLstatement.execute();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void removeLineItem(String partNum) throws SQLException
	{
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

		String query = "UPDATE Component SET DeleteDate = ? WHERE PartNumber = ? AND BOM_ID = ?";

		conn = DBConnection.getConnection();
		SQLstatement = conn.prepareStatement(query);
		SQLstatement.setString(1,timeStamp);
		SQLstatement.setString(2,partNum);
		SQLstatement.setInt(3,bomID);
		
		try 
		{
			SQLstatement.execute();
			updateComponentList();
		} 
		catch (SQLException e) 
		{
			System.out.println(e.getMessage());
		}
	}


	private void updateComponentList() throws SQLException 
	{
		String query = "SELECT C.ComponentID,C.VendorID,C.PartNumber,C.Prt_Description,"
				+ "C.Price,C.Quantity,C.QuantityAvailable,C.CreateDate "
				+ "FROM BOM B,Component C " + "WHERE B.BOM_ID = " + bomID
				+ " AND B.BOM_ID = C.BOM_ID AND C.DeleteDate IS NULL;";
		try 
		{
			SQLstatement = conn.prepareStatement(query);
			ResultSet resSet = SQLstatement.executeQuery();

			BOMdataLines.clear();

			while (resSet.next()) 
			{
				int cid, vid, qr, qa;
				double pr;
				String pn, pd, cd;
				cid = resSet.getInt("ComponentID");
				vid = resSet.getInt("VendorID");
				pn = resSet.getString("PartNumber");
				pd = resSet.getString("Prt_Description");
				pr = resSet.getDouble("Price");
				qr = resSet.getInt("Quantity");
				qa = resSet.getInt("QuantityAvailable");
				cd = resSet.getString("CreateDate");

				Component c = new Component(pn, pd, qr);
				c.setComponentID(cid);
				c.setVendorID(vid);
				c.setCost(pr);
				c.setQuantityAvailable(qa);
				c.setCreateDate(cd);
				BOMdataLines.add(c);
			}

		} 
		catch (SQLException e) 
		{
			System.out.println(e.getMessage());
		}
	}

	// this method allows the newly created BOM to determine its SQL assigned
	// bomID.  It is not for external use
	private int newBOMIDquery() throws SQLException 
	{
		conn = DBConnection.getConnection();
		rs = null;
		try 
		{
			SQLstatement = conn.prepareStatement("SELECT MAX(BOM_ID) FROM BOM");
			rs = SQLstatement.executeQuery();
			rs.next();
			int bID = rs.getInt("MAX(BOM_ID)");
			return bID;
		} 
		catch (SQLException e) 
		{
			System.out.println(e.getMessage());
			return 0;
		}
	}

	public ResultSet getComponentsTable() throws SQLException 
	{
		conn = DBConnection.getConnection();
		rs = null;
		String query = "SELECT * FROM Component WHERE Component.BOM_ID = ? AND Component.DeleteDate IS NULL";

		SQLstatement = conn.prepareStatement(query);
		SQLstatement.setInt(1, bomID);
		rs = SQLstatement.executeQuery();
		return rs;
	}

	public void queryVendor(int[] arrInt)
	{
		Vendor vendQuery = new Vendor(bomID,arrInt,BOMdataLines);
	}
	
	public String toString() 
	{
		String temp = "";
		for (int i = 0; i < BOMdataLines.size(); i++) {
			temp += BOMdataLines.get(i).toString();
			temp += "\n";
		}

		return temp;
	}

	public ArrayList<Component> getComponentsArray()
	{
		return BOMdataLines;
	}
	
	private void closeConnection() 
	{
		try 
		{
			rs.close();
		} 
		catch (Exception e) 
		{
		}
		try 
		{
			SQLstatement.close();
		} 
		catch (Exception e) 
		{
		}
		try 
		{
			conn.close();
		} 
		catch (Exception e) 
		{
		}
	}

}

