package application;
import java.io.*;
import java.net.*;

public class ClientProgram {
	final String serverHost = "35.240.252.106";
	String Username=null;
	Controler cont=null;
	int port = 8000;
	Socket socketOfClient = null;
	BufferedWriter os = null;
	BufferedReader is = null;
	public ClientProgram(String username,Controler cont) {
		this.Username= username;
		this.cont = cont;
		try {
			socketOfClient = new Socket(serverHost, port);
			os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
			is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
			} 
		catch (UnknownHostException e) {
			System.err.println("Don't know about host " + serverHost);
			return;
	       } catch (IOException e) {
	    	   System.err.println("Couldn't get I/O for the connection to " + serverHost);
	    	   return;
	       }
		Wait_For_Server();
	}
	public void SendMessage(String mess) {
		try {
			os.write(mess);
			os.newLine();
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String Terminate_connection() {
		 try {
			os.close();
			is.close();
	        socketOfClient.close();
	        return "Username: "+this.Username+" terminate connection !";
		} catch (UnknownHostException e) {
	           System.err.println("Trying to connect to unknown host: " + e);
	        return "Trying to connect to unknown host: " + e;
		} catch (IOException e) {
			e.printStackTrace();
			return "IOException: " + e;
		}
	 }
	public void Wait_For_Server() {
		String responseLine;
		try {
			while ((responseLine = is.readLine()) != null) {
				cont.ReceiveMessage(responseLine);
				if (responseLine.indexOf("OK") != -1) {
						break;
					}
				}
			Terminate_connection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
