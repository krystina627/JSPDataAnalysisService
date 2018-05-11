package dataUpload;
/**
 * SaveManualForm 
 * takes in formatted BIM data and writes it out to a BOM structure in the DB
 * Created by Nick Radonic
 * Feb. 25
 * Adapted from Krystina Poling jsp script
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import billofmaterialssearch.*;



/**
 * Servlet implementation class SaveManualForm
 */
@WebServlet(description = "Saves contents of Manual BOM input", urlPatterns = { "/SaveManualForm" })
public class SaveManualForm extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	String BOMName = "";
	String BOMData = "";
	
	String[] component = new String[]{};
	String userName;
	int userID=0;
	// path strings
	final String moveOnToBOMEntry = "BOMmain.jsp";

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveManualForm() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		Set<String> parameterNames = request.getParameterMap().keySet();
		
		this.BOMName = request.getParameter("bom_title");
		this.BOMData = request.getParameter("total_data");
		this.userName = (String)session.getAttribute("userName");
		try {
			this.userID = (Integer)session.getAttribute("userID");
		} catch (Exception e) { userID=0;}
		
		String message = processBOM(userName, userID, BOMName, BOMData);
		// Set response content type
		response.setContentType("text/html");
		
		String destination = moveOnToBOMEntry;
		
		// Set response content type
		response.setContentType("text/html");

		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", destination);
	}

	private String processBOM(String uName, int uID, String BName, String BData){
		// Prototype JSON data: ["1","","",""],["2","","",""],["3","","",""]  = [row, ID, qty, description]
		String message = "";
		// number of splits in input string
		final int PIECES = 3;
		// split at line breaks ],[
		String text = "\\],\\[";
		Pattern pattern = Pattern.compile(text);
		String[] bomData0 = pattern.split(BData);
		
		// take care of first and last elements [  ]
		bomData0[0] = bomData0[0].replaceAll("\\[","");
		bomData0[bomData0.length-1] = bomData0[bomData0.length-1].replaceAll("\\]","");
		
		ArrayList<String[]> brokenOutData = new ArrayList<String[]>();
		// parse lines
		for(String s:bomData0){
			// break each line into 4 pieces
			// line number, part number, qty, description - ignore line number for now
			String[] lineS = s.split(",",4);
			// strip out quotes ----- strip quotes out...
			
			for(int i=0; i<lineS.length; i++){
				lineS[i] = lineS[i].replaceAll("\"","");
			}
			// push the array into the output ArrayList
			brokenOutData.add(lineS);
		}
		try{
			BOM bom = new BOM(uID, BName);
			for(String[] sa : brokenOutData){
				String partNumber = sa[1];
				int quantity = Integer.parseInt(sa[2]);
				String userDescription =  sa[3];
				bom.addComponent(partNumber, userDescription, quantity);
			}
		} catch(Exception e){message = "BOM creation error: "+e.toString();}
		
		
		
		System.out.println("Broken out data: " +brokenOutData.toString());
		return message;
	}
}
