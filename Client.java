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

/* ALL THE NECESSARY COMMENTS ARE MENTIONED. ALL THE FUNCTIONS WORK THE SAME FOR ALL 4 SIGNS(*-+/), SO I HAVE NOT REPEATED THE COMMENTS*/

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.Socket;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.Component;
import javax.swing.ScrollPaneConstants;

public class Client implements ActionListener, Runnable {
	//Declarations

	Thread clientThread;
	Socket skt;
	BufferedReader sin;
	PrintStream sout;
	BufferedReader stdin;

	int index=0;
	String opr, op1,op2;
	double res1;
	String str=null;
	String operat;

	String s=null;
	ArrayList<String> operands= new ArrayList<String>();
	ArrayList<String> operator= new ArrayList<String>();
	String init = "1";

	//GUI declarations
	private JFrame frame;
	private  JTextArea textArea;
	private  JTextArea textArea1;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JButton btnCalculate;
	String username;



	//constructor to initialize
	public Client() {
		//Builds GUI
		initialize();

		try{
			//connects to server at port 5000
			skt= new Socket("127.0.0.1",5000);	

			sin= new BufferedReader(new InputStreamReader(skt.getInputStream())); //input
			sout= new PrintStream(skt.getOutputStream()); //output

			stdin= new BufferedReader(new InputStreamReader(System.in)); //input

			clientThread=new Thread(this);		//start a new thread
			clientThread.start();

		}catch(Exception e) {

		}
	}

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//window obj to create GUI
					Client window = new Client();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	//Builds GUI (system generated)
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 578, 520);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane((Component) null);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 21, 381, 130);
		frame.getContentPane().add(scrollPane);

		textArea1 = new JTextArea();
		scrollPane.setViewportView(textArea1);

		textArea = new JTextArea();
		textArea.setBounds(23, 75, 239, 175);

		JScrollPane scroll = new JScrollPane (textArea);
		scroll.setBounds(10, 170, 381, 273);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		frame.getContentPane().add(scroll);


		//send button
		btnNewButton = new JButton("Send");
		btnNewButton.setBounds(434, 76, 89, 23);
		frame.getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(this);
		//quit button
		btnNewButton_1 = new JButton("Quit");
		btnNewButton_1.setBounds(434, 206, 89, 23);
		frame.getContentPane().add(btnNewButton_1);
		//calculate button
		btnCalculate = new JButton("Calculate");
		btnCalculate.setBounds(434, 122, 89, 23);
		frame.getContentPane().add(btnCalculate);
		btnNewButton_1.addActionListener(this);
		btnCalculate.addActionListener(this);
		btnCalculate.setEnabled(false);

	}

	//Func for buttons: SEND CALC and QUIT
	public void actionPerformed(ActionEvent ae) {

		//send button
		if(ae.getSource()== btnNewButton) {

			sout.println(textArea1.getText());	//gets user input from txtbox
			textArea1.setText("");
		}
		//quit button
		if(ae.getSource()== btnNewButton_1) {
			sout.println("quit");
			textArea.append("Disconnecting");
			System.out.println("Client disconnected");
			System.exit(0);	//exits the prog
		}
		//Calculate button
		if(ae.getSource()== btnCalculate) {

			s = textArea1.getText();	//gets user input from txtbox
			textArea1.setText("");
			StringBuilder sb = new StringBuilder(s);

			//if user enters the input with new line, this part of code mergers it into a single string
			boolean hasNewline = s.contains("\n");
			if(hasNewline) {
				int i = sb.indexOf("\n");
				sb.deleteCharAt(i);
				s = sb.toString();
			}
			String ans = calculate(s);
			textArea.append("\n"+s+"="+ans);	//prints answer to the user

		}
	}

	//execution of thread
	public void run() {

		textArea.append("You need to register yourself.\n");
		textArea.append("Enter username.\n");

		try {

			username=sin.readLine();	//receive username from the server

			//If username exists, user will re-enter username
			while(username.equals("re-enter")){

				textArea.append("Username exists.\nTry registering with a new username.\n");
				username=sin.readLine();	//receive untill not 're-enter'
			}

			textArea.append("Enter operator then operand.\n");
			btnNewButton.setEnabled(false);		//disables  the button
			btnCalculate.setEnabled(true);		//enables

			//this block of code is for the persitent log
			while(true) {
				String f=sin.readLine();	//reads the ip from server to poll
				String sCurrentLine;

				if(f.equals("send")) {
					BufferedReader br = new BufferedReader(new FileReader(username + ".txt"));	//open file
					String line = br.readLine();	//read 1st line
					int count=0;	//count num of #. # indicates start of the operations to upload

					while (line != null) {
						if(line.contains("#")){
							count++;

							StringBuilder builder = new StringBuilder();
							while ((sCurrentLine = br.readLine()) != null){
								builder.append(sCurrentLine);		//appends all the opeartions to send it to the server

							}
							//after appending, send it to the server and adds # at the EOF
							String operat = builder.toString();
							sout.println(operat);
							System.out.println(operat);
							FileWriter fw = new FileWriter(username + ".txt", true);
							fw.write("#");
							fw.close();
						}
						else {//reads next line
							line = br.readLine();
						}
					}
					//this part will be executed when a new log is created ie no partial uploads have done
					if(count==0) {
						StringBuilder builder = new StringBuilder();
						while ((sCurrentLine = br.readLine()) != null){
							builder.append(sCurrentLine);
						}
						operat = builder.toString();
						FileWriter fw = new FileWriter(username + ".txt", true);
						fw.write("#");
						fw.close();
						sout.println(operat);
						System.out.println(operat);
					}

				}
			}


		} catch (IOException e) {}


	}
	//func stores opr in file
	//every user will produce a file with username.txt
	public void writeToFile(String logVal) throws Exception {
		File file = new File(username+".txt");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(logVal);	//write in file
			fw.close();
		}

		// true = append file
		FileWriter fw = new FileWriter(file, true);
		//fw.write("\n");
		fw.write(logVal);	//write in file
		fw.close();

	} 
	//this is the main func that calculates the operations arithematically
	public String calculate(String s) {

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

				try {
					writeToFile(logVal);	//calls the func to write in the file.
				} catch (Exception e) {}
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
	public String replaceM(BigDecimal ans, String s, int index) {
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
	public BigDecimal mult(int index) {
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
	public BigDecimal div(int index) {
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
	public BigDecimal add(int index) {

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
	public BigDecimal sub(int index) {
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
