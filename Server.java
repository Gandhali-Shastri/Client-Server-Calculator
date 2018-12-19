/*	* Name:- Gandhali Girish Shastri
 * ID: 1001548562
 * Lab Assignment - 3
 * -----------------------------------------------------
 * 
 * References:	https://stackoverflow.com/questions/35956451/combining-multiple-lines-into-one-single-line
 * 				https://www.geeksforgeeks.org/simple-calculator-using-tcp-java/
 * 				https://stackoverflow.com/questions/32416267/java-client-server-calculator-example
 * 				
 * 
 * */

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.StringTokenizer; 

public class Server implements ActionListener {

	//Declarations and Initializations

	//port number for connection
	int port;
	private static String user = "";
	static int index=0;
	static String opr, op1,op2;
	static double res1;
	static String str=null;
	static String operat;

	static String s=null;
	static ArrayList<String> operands= new ArrayList<String>();
	static ArrayList<String> operator= new ArrayList<String>();
	static String init = "1";

	//queue for queuing threads
	static  Queue<Integer> q = new LinkedList<>();
	static ServerSocket server= null;
	static Socket client=null;
	static ExecutorService pool=null;

	//stores the user names to check whether username exists or not
	static ArrayList<String> users= new ArrayList<String>(3);
	//Only 3 clients can connect at a time
	private static int MAXCLIENTS = 3;
	//Creates different threads for each client
	private static clientThread[] threads = new clientThread[MAXCLIENTS];		

	//GUI variables
	private JButton btnNewButton;
	private static  JTextArea textArea;
	private JFrame frame;
	static String action=null;

	//main method
	public static void main(String[] args) {

		//run method of thread
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//window object to build the GUI
					Server window = new Server();
					//makes frame visible
					window.frame.setVisible(true);
				} catch (Exception e) {
					//System.out.println(e);
				}
			}
		});


		try {
			//Socket for server with port number
			//port where the clients would connect
			server=new ServerSocket(5000);	
		} catch (Exception e) {
			//System.out.println(e);
		}

		int i=0; //counts number of client threads

		while(true) {
			try {
				//creating new threads as clients join
				client=server.accept();	

				//Creates all the clients and stores them in array of 'threads'
				for (i = 0; i <= MAXCLIENTS; i++) {
					if (threads[i] == null) {
						//starts a new thread
						(threads[i] = new clientThread(client, threads)).start();	

						break;
					}
				}

				//if a 4th client tries to connect, the foll code will be executed
				if (i == MAXCLIENTS) {
					PrintStream os = new PrintStream(client.getOutputStream());
					//prints on client side
					os.println("Server too busy. Try later.");
					//prints on server side
					textArea.append("Maximum number of clients have already joined.");
					os.close();
					client.close();		//close client thread
				}
			} catch(Exception e) {
				//System.out.println(e);
			}
		}

	}

	//constructor
	public Server() {

		initialize();	//used to build the GUI
	}

	//Builds the GUI (system generated code)
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 578, 520);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);



		textArea = new JTextArea();
		textArea.setBounds(23, 75, 239, 175);

		JScrollPane scroll = new JScrollPane (textArea);
		scroll.setBounds(10, 74, 381, 369);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		frame.getContentPane().add(scroll);


		//send button
		btnNewButton = new JButton("Send");
		btnNewButton.setBounds(434, 76, 89, 23);
		frame.getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(this);

	}

	public void actionPerformed(ActionEvent ae) {

		//send button
		if(ae.getSource()== btnNewButton) {
			for(clientThread ct: threads) {
				while(ct != null) {
					ct.os.println("send");
				}
			}

		}
	}


	//Client thread class
	public static class clientThread extends Thread {

		private PrintStream os = null;
		private Socket clientSocket = null;
		private clientThread[] threads;
		private int maxClientsCount;
		private String username;
		//		BufferedReader is = new BufferedReader(new InputStreamReader(client.getInputStream()));
		//		PrintStream os = new PrintStream(clientSocket.getOutputStream());


		//constructor.  called when a thread is started
		public clientThread(Socket clientSocket, clientThread[] threads) {
			this.clientSocket = clientSocket; //socket at which client is connected
			this.threads = threads; //current thread
			maxClientsCount = threads.length; //size of the array should be less than 4
		}

		//execution of a particular thread starts here
		public void run() {

			int maxClientsCount = this.maxClientsCount; //current client count
			clientThread[] threads = this.threads; //all active clients
			String oprtn = null;

			try {

				BufferedReader is = new BufferedReader(new InputStreamReader(client.getInputStream()));
				os = new PrintStream(clientSocket.getOutputStream());

				this.username = is.readLine();	//accept username from client
				user = username;

				// this block of code is for accepting username only if it is unique
				while(true) {

					//Checks if username exists
					if(users.contains(this.username)) {
						os.println("re-enter");		//client side handles accepting many usernames until its unique
					}
					else {
						String response= "Registeration successfull.\n" + this.username + " has been connected.\n";

						//prints on server side

						textArea.append(response+"\n");

						//sends to client
						os.println(this.username);	          			

						users.add(this.username);	//stores usernames
						textArea.append("\n Online user: \n");
						for(int i=0;i<users.size();i++) {
							textArea.append(users.get(i) + "\n");
						}

						break;
					}


					this.username=is.readLine();		//accepts username after it is flagged as unique
				}

				os.println(username);
				System.out.println(username);
				String s= is.readLine();
				os.println(s);

				StringBuilder builder = new StringBuilder();

				while(true){
					oprtn = is.readLine();
					if(!(oprtn.isEmpty())) {
						synchronized (oprtn) {
							builder.append(oprtn);
							System.out.println(oprtn);
						}
					}
					else {
						break;
					}
				}
				String cal = builder.toString();
				String servans = calculate(cal);

			} 
			catch(IOException ex){
				System.out.println("Error client disconn: "+ex);
				textArea.append("\n ****************client: " +username + "disconnected. ************** \n");
				users.remove(username);
			} 
			catch(NoSuchElementException ex) {
				System.out.println("Error client disconn: "+ex);
				textArea.append("\n ****************client: " +username + "disconnected. ************** \n");
				users.remove(username);
			}


		}   
	}

	//this is the main func that calculates the operations arithematically
	public static String calculate(String s) {

		if (s.matches("([-+/*]?[0-9]*\\.?[0-9]+[\\/\\+\\-\\*])+([-+/*]?[0-9]*\\.?[0-9]+)")) { //checks for num and signs only
			StringTokenizer stk = new StringTokenizer(s, "+-*/", true);
			String tok = stk.nextToken();

			//checks for 1st char to be oprtor as we will append it with initial value
			if(!("+-/*".contains(tok))) {
				System.out.println("1st char should be operator");
				return null;
			}
			else {
				s = init.concat(s);
				System.out.println(s);
				String logVal=(s+"\n");
				String result = calculate(s);

			}   
		}

		boolean flag=false;

		do{
			//System.out.println(s);

			StringTokenizer st = new StringTokenizer(s, "+-*/", true);

			//this resets the string  after 1 calculation is performed
			if(!operands.isEmpty()) {
				operands.clear();
			}
			if(!operator.isEmpty()) {
				operator.clear();
			}

			//splits operands and operators
			while (st.hasMoreTokens()) {

				String token = st.nextToken();

				if ("+-/*".contains(token)) {	//* true
					//handles negative numbers
					String neg = st.nextToken();	//-

					if(neg.equals("-")) {	//true
						operator.add(token);
						operands.add(neg);//splits and stores neg in opr

					}
					else {
						operator.add(token);
						operands.add(neg);
					}

				} else {

					operands.add(token);	//all the operands are stored here
				}

			}

			//System.out.println(operands);
			//System.out.println(operator);

			//checks for calc acc to PEDMAS

			//Mult or division first
			if(operator.contains("*") || operator.contains("/")){
				for(int i=0; i< operator.size(); i++) {
					String o = operator.get(i);
					if(o.equals("*")){
						index = operator.indexOf("*");
						BigDecimal ans=mult(index);		//mult funct performs mult
						s=replaceM(ans, s,index);		//this stores the product n org string
						break;
					}
					else {
						//division
						if(o.equals("/")){
							index = operator.indexOf("/");	//same as above
							BigDecimal ans=div(index);
							s=replaceM(ans, s,index);
							break;
						}
					}
				}
			}
			//ADDITION SUBTRATION
			else if(operator.contains("+") || operator.contains("-")){
				for(int i=0; i< operator.size(); i++) {
					String o = operator.get(i);
					if(o.equals("+")){
						index = operator.indexOf("+");		//same as above
						BigDecimal ans=add(index);
						s=replaceM(ans, s ,index);
						break;
					}
					else { 
						if(o.equals("-")){
							index = operator.indexOf("-");  	//same as above
							BigDecimal ans=sub(index);
							s=replaceM(ans, s,index);
							break;
						}
					}
				}

			}	

			//code is same as we split above. This part is to check and terminate the prog.
			//it checks if only 1 operand is left by splitting the ans each time
			StringTokenizer st1 = new StringTokenizer(s, "+-*/", true);

			while (st1.hasMoreTokens()) {

				String token = st1.nextToken();

				if ("+-/*".contains(token)) {	

					String neg = st1.nextToken();

					if(neg.equals("-")) {			//same as above
						operator.add(token);
						operands.add(neg);
					}
					else {
						operator.add(token);
						operands.add(neg);
					}
				} else {					//same as above
					operands.add(token);
				}					    
			}

			if(operator.isEmpty())
				flag=true;

		}while(flag==false);

		return s;		
	}

	//replaces ans after calculating. eg-- 1+2*3  ---> 1+6
	public static String replaceM(BigDecimal ans, String s, int index) {
		StringBuilder sb = new StringBuilder(s);
		String resultString=null;
		int start=0, end=0;

		if(s.contains(str)){
			int ind = s.indexOf(str);		//finds the postion

			start = ind;
			end = ind + str.length();

			sb.replace(start, end, ans.toString());		//replces n the org string

			resultString = sb.toString();
			//System.out.println(sb + " new string");

		}

		return resultString;
	}

	//Multiplication function
	public static BigDecimal mult(int index) {
		StringBuilder sb = new StringBuilder();
		int i=index;

		opr= operator.get(i);
		op1= operands.get(i);		//finds the operands and opr
		op2 = operands.get(i+1);

		if(op2.equals("-")) {
			String op3 = operands.get(i+2);
			op2 = op2.concat(op3);
			System.out.println(op2);
		}

		//	System.out.println(op1 + opr + op2);

		sb.append(op1).append(opr).append(op2);
		str = sb.toString();

		//System.out.println(str);

		res1= Double.parseDouble(op1) * Double.parseDouble(op2);	//multiplies
		BigDecimal result;
		result = round(res1,4);
		//	System.out.println(result);

		return result;
	}
	//----------------- same as above func except that this performs division-----------------------
	public static BigDecimal div(int index) {
		StringBuilder sb = new StringBuilder();
		int i=index;

		opr= operator.get(i);
		op1= operands.get(i);
		op2 = operands.get(i+1);

		System.out.println(op1 + opr + op2);
		sb.append(op1).append(opr).append(op2);
		str = sb.toString();

		System.out.println(str);

		res1= Double.parseDouble(op1) / Double.parseDouble(op2);
		BigDecimal result;
		result = round(res1,4);
		System.out.println(result);

		return result;
	}
	//----------------- same as above func except that this performs addn-----------------------
	public static BigDecimal add(int index) {

		int i=index;
		StringBuilder sb = new StringBuilder();
		opr= operator.get(i);
		op1= operands.get(i);
		op2 = operands.get(i+1);

		System.out.println(op1 + opr + op2);
		sb.append(op1).append(opr).append(op2);
		str = sb.toString();

		System.out.println(str);

		res1= Double.parseDouble(op1) + Double.parseDouble(op2);
		BigDecimal result;
		result = round(res1,4);
		System.out.println(result);

		return result;
	}
	//----------------- same as above func except that this performs sub-----------------------
	public static BigDecimal sub(int index) {
		StringBuilder sb = new StringBuilder();
		int i=index;

		opr= operator.get(i);
		op1= operands.get(i);
		op2 = operands.get(i+1);

		System.out.println(op1 + opr + op2);
		sb.append(op1).append(opr).append(op2);
		str = sb.toString();

		System.out.println(str);
		res1= Double.parseDouble(op1) - Double.parseDouble(op2);
		BigDecimal result;
		result = round(res1,4);
		System.out.println(result);

		return result;
	}

	//funct to round upto 4 places.
	public static BigDecimal round(double res1, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Double.toString(res1));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);       
		return bd;
	}

}