package billofmaterialssearch;

import java.sql.*;


public class BOMtestDriver {

	public static void main(String[] args) 
	{
				
		//New BOM
		BOM testBOM,oldBOM;
		
		//array of vendor ID's
		int[] testArray = new int[3];
		testArray[0] = 2;
		testArray[1] = 4;
		testArray[2] = 5;
		
		//add components to a BOM
		try{
			testBOM = new BOM(1,"New BOM 98765");
			testBOM.addComponent("Part-12345", "A Resistor", 8);
			testBOM.addComponent("BLAH-4k", "4k Resistor", 2);
			testBOM.addComponent("Test-Part-ABC123", "8k Resistor", 15);
			testBOM.addComponent("CAP-asdkbnj", "capacitor", 2);
			testBOM.addComponent("LED-123", "led", 1);
		//verify remove component from BOM
			System.out.println(testBOM.toString());
			testBOM.removeLineItem("CAP-asdkbnj");
			System.out.println("Part removed \n" + testBOM.toString());
		//have the BOM class initiate a vendor search against all existing components
			testBOM.queryVendor(testArray);


		//Pull an old BOM from the database and then send it for a Vendor search
			oldBOM = new BOM(5);
			oldBOM.queryVendor(testArray);
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());	
		}
	}
}