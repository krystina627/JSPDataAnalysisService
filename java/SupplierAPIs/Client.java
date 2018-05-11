package SupplierAPIs;

import java.io.IOException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class Client{
	public static void main(String[] args){
	try{
		//Create SOAP Connection
		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		
		//Send SOAP Message to SOAP Server
		String url = "http://api.mouser.com/service/searchapi.asmx";
		SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
		//Process the SOAP Response
		printSOAPResponse(soapResponse);
		soapConnection.close();
		}catch(Exception e){
			System.err.println("Error occurred while sending SOAP Request to Server");
		}

	}
//
private static SOAPMessage createSOAPRequest() throws SOAPException, IOException{
	
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
     mouserPartNumber.addTextNode("654-MS3116F8-4S");
     
     
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

//Method used to print the SOAP Response

private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    Source sourceContent = soapResponse.getSOAPPart().getContent();
    System.out.print("\nResponse SOAP Message = ");
    StreamResult result = new StreamResult(System.out);
    transformer.transform(sourceContent, result);
	
}
	
}	

	
	
		
	
	




			
		
				










