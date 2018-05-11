package billofmaterialssearch;

public class Component {
	String partNum, manufacturer = "", partDescription = "", createDate = "";
	int componentID, vendorID, quantRequested, quantAvailable, bomID;
	double cost = 0.0;

	public Component(String pn, String pd, int qr) 
	{
		partNum = pn;
		quantRequested = qr;
		partDescription = pd;
	}

	// SQL will auto assign the component ID but this allows the BOM to easily
	// track multiple components
	public void setComponentID(int c) 
	{
		componentID = c;
	}

	public int getComponentID() 
	{
		return componentID;
	}

	public void setVendorID(int v) 
	{
		vendorID = v;
	}

	public int getVendorID() 
	{
		return vendorID;
	}

	// there is no setPartNum because that value is only set at instantiation
	public String getPartNum() 
	{
		String s = new String(partNum);
		return s;
	}

	public void setPartDescription(String pd) 
	{
		partDescription = pd;
	}
	public String getPartDescription() 
	{
		String ud = new String(partDescription);
		return ud;
	}

	public void setCost(double c) 
	{
		cost = c;
	}

	public double getCost() 
	{
		return cost;
	}

	// there is no setPartQuantity because that value is only set at
	// instantiation
	public int getQuantityRequested() 
	{
		return quantRequested;
	}

	public void setQuantityAvailable(int qa) 
	{
		quantAvailable = qa;
	}

	public int getQuantityAvailable() 
	{
		return quantAvailable;
	}

	// the database automatically sets the create date but this method allows
	// for the data to be set for interim use, i.e., duplicating a Component
	public void setCreateDate(String s) 
	{
		createDate = new String(s);
	}

	public String toString() 
	{
		String temp = new String (getVendorID() + ", " + getPartNum() + ", " + getPartDescription() + ", "
				+ getQuantityRequested() + ", " + getQuantityAvailable() + ", " + getQuantityAvailable() + ", "
				+ getCost());
		return temp;
	}

}
