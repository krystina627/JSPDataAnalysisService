package billofmaterialssearch;
/*
 * Author: Terry Dupont
 * Feb 28, 2016
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class Vendor {

	private int BOM_ID,currentQuantity; 
	private  int quantity;
	private int vendorNum = 0;
	private int MOUSERID = 2; 
	private  double	currentPrice = 0.0;
	private ArrayList<Component> components;
	private int[] vendorID;
	private String url, serverURI, manufacturerPartNum, description, vendorPartNum;
	private Component currentComponent = null;
	private DBConnection connection = null;
	private Connection myConnection = null;
	private java.sql.PreparedStatement SQLStatement;
	private boolean status = false;
	
	
	public Vendor(int BOM_ID,int[] vendorID , ArrayList<Component> components){
		this.BOM_ID = BOM_ID;
		this.components = components;
		this.vendorID = vendorID;
		
		getComponents();
	}
	/**
	 * Description - Gets the components and compares the vendors
	 */
	private void getComponents(){
		Iterator componentsIterator = ((ArrayList<Component>) components).iterator();
		
		while(componentsIterator.hasNext()){
			currentComponent = (Component) componentsIterator.next();
			quantity = currentComponent.getQuantityRequested();
			manufacturerPartNum = currentComponent.getPartNum();
			
			//Create SOAP message for all user specified vendors
			for(int i = 0; i < vendorID.length; i++){
				int vendorNum = vendorID[i];
				
				//Check the vendor numbers to determine which vendor to query.
				if(vendorNum == MOUSERID){
					createSOAPConnection("http://api.mouser.com/service/searchapi.asmx", manufacturerPartNum, vendorNum);
					currentComponent.setCost(currentPrice);
					currentComponent.setQuantityAvailable(quantity);
					currentComponent.setPartDescription(description);
					currentComponent.setVendorID(MOUSERID);
					
					if(status == true){
						addDataToDatabase();
					}
				}
				setVariables();
			}	//Add else if statement to add other vendors.
			
		}
	}
	/**
	 * Description - Creates a connection, SOAP message, and gets the response from a SOAP server
	 * @param url - 
	 * @param manufPartNum - String that represents the manufacturer part number to search the vendor
	 * @param vendorID - Integer that represents an unique identifier for the vendor.
	 */
	private void createSOAPConnection(String url, String manufPartNum, int vendorID){
		try{
			//Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			
			//Send SOAP Message to SOAP Server
			this.url = url;
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(manufPartNum, vendorID), url);
			
			//Print SOAP Response for Testing Purposes.
			printSOAPResponse(soapResponse);
			
			//Convert SOAPResponse to a string
			String response =convertSOAPMessage(soapResponse);
		
			//Parse SOAPResponse String
			SOAPParser(response);
			
			soapConnection.close();
	   }catch(Exception e){
		   
		   e.printStackTrace();
	   }
     }
	
	/**
	 * Description - Creates a SOAP request to Mouser
	 * @param manufPartNum
	 * @param vendorID
	 * @return
	 * @throws SOAPException
	 * @throws IOException
	 */
	private SOAPMessage createSOAPRequest(String manufPartNum, int vendorID) throws SOAPException, IOException{
		
		//Mouser's Vendor ID
		if(vendorID == MOUSERID){
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage soapMessage = messageFactory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();

			String serverURI = "http://api.mouser.com/service";

	     // SOAP Envelope
			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.addNamespaceDeclaration("xsi", serverURI);
			envelope.addNamespaceDeclaration("xsd", serverURI);
		

	//***************SOAP Request for user specified part from Mouser************************
	/*		
		<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
		<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n"
		   <soap:Header>
		      <MouserHeader xmlns=http://api.mouser.com/service">
		         <AccountInfo>
		             <PartnerID>"b1ddc5b2-ad8f-44ef-b0c2-6cae6125533c</PartnerID>
		         </AccountInfo>
		       </MouserHeader>
		   </soap:Header>
		   <soap:Body>
		      <SearchByPartNumber xmlns=http://api.mouser.com/service>
		          <mouserPartNumber>654-MS3116F8-4S" + "<mouserPartNumber>
		      </SearchByPartNumber>
		   </soap:Body>
		 <soap:Envelope>
		        
		        
		        */
		
		//SOAP Header
			SOAPHeader soapHeader = envelope.getHeader();
			SOAPElement mouserHeader = soapHeader.addChildElement("MouserHeader", "", "http://api.mouser.com/service");
			SOAPElement acccountInfo = mouserHeader.addChildElement("AccountInfo");
			SOAPElement partnerID = acccountInfo.addChildElement("PartnerID");
			partnerID.addTextNode("b1ddc5b2-ad8f-44ef-b0c2-6cae6125533c");
	     
	     
	     //SOAP Body
			SOAPBody soapBody = envelope.getBody();
			SOAPElement searchPartNumber = soapBody.addChildElement("SearchByPartNumber", "", "http://api.mouser.com/service");
			SOAPElement mouserPartNumber = searchPartNumber.addChildElement("mouserPartNumber");
	    //Testing Purposes
			mouserPartNumber.addTextNode(manufPartNum);
	     
	     
	     //Get MimeHeaders
			MimeHeaders headers = soapMessage.getMimeHeaders();
			headers.addHeader("SOAPAction", "http://api.mouser.com/service/SearchByPartNumber");
	     
			soapMessage.saveChanges();
	     
	     //Print the request message 
			System.out.println("Request SOAP Message = ");
			soapMessage.writeTo(System.out);
			System.out.println();
	     
		
			return soapMessage;
		}
		else{
			//Other SOAP vendors
		}
		return null;
	  }
	
	/**
	 * Used to print out the SOAP Response
	 * @param soapResponse
	 * @throws Exception
	 */

	private  void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    Source sourceContent = soapResponse.getSOAPPart().getContent();
	    System.out.print("\nResponse SOAP Message = ");
	    StreamResult result = new StreamResult(System.out);
	    transformer.transform(sourceContent, result);
		
	}
	/**
	 * Parses the SOAP Response
	 * @param soapResponse - String that represents the SOAP Response 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	
	private  void SOAPParser(String soapResponse) throws ParserConfigurationException, SAXException, IOException{
		
		int numResult, numAvailability;
		String vendorDescription, vendorQuantity, price;
		String[] available;
		NodeList prices;
		double componentPrice = 0.0;
		
	
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new ByteArrayInputStream((soapResponse).getBytes("UTF-8")));
		Document document = db.parse(is);
	
		for(int i = 0; i < document.getChildNodes().getLength(); i++){
			numResult = Integer.parseInt(document.getDocumentElement().getElementsByTagName("NumberOfResult").item(i).getTextContent());
			if(numResult == 0){
				// Mouser does not have the part
			}
			else{		
				vendorQuantity = document.getDocumentElement().getElementsByTagName("Availability").item(i).getTextContent();
				available = vendorQuantity.split("\\s+");
				numAvailability = Integer.parseInt(available[0]);
				
				if(numAvailability >= quantity){
					
					prices = document.getDocumentElement().getElementsByTagName("Price");
					
					//Price is based on the user ordering a quantity of 1.  Will update to include price breaks.
					price = prices.item(i).getTextContent();
					price = price.substring(1);
					componentPrice = Double.parseDouble(price);
					
					
					//Compare current price with the component price from Mouser
					if(currentPrice > componentPrice || currentPrice == 0.0){
						currentPrice = componentPrice;
						//Set Mouser vendor ID
						vendorNum = MOUSERID;
						//Set component's description
						description = document.getDocumentElement().getElementsByTagName("Description").item(i).getTextContent();
						currentQuantity = numAvailability;
						vendorPartNum = document.getDocumentElement().getElementsByTagName("MouserPartNumber").item(i).getTextContent();
						
						status = true;
						
						}
						
					}
					
			      }
						
				}
						
			}	
	         
	/**
	 * Helper Method that converts the SOAP Message to a String
	 * @param soapResponse - SOAP Message that needs to be converted to a string
	 * @return - String that represents the SOAP Message
	 * @throws SOAPException
	 * @throws IOException
	 */
	private String convertSOAPMessage(SOAPMessage soapResponse) throws SOAPException, IOException{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapResponse.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");
		return message;
	}
	
	private void addDataToDatabase(){
	
		Connection myConnection = (Connection) DBConnection.getConnection();
		
		try {
			SQLStatement = myConnection.prepareStatement
					("UPDATE Component SET Price = ?, VendorID = ?, Prt_Description = ?, QuantityAvailable = ? WHERE BOM_ID = ? AND PartNumber = ?");
			
				SQLStatement.setDouble(1, currentPrice);	
				SQLStatement.setInt(2, vendorNum);
				SQLStatement.setString(3, description);
				SQLStatement.setInt(4, currentQuantity);
				SQLStatement.setInt(5, BOM_ID);
				SQLStatement.setString(6, currentComponent.getPartNum());
				
				SQLStatement.executeUpdate();
			
				myConnection.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	  }
	
	private void setVariables(){
		currentPrice = 0.0;
		vendorNum = 0;
		description = "";
		currentQuantity = 0;
		status = false;
	}

  }
