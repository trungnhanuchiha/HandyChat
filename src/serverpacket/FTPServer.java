package serverpacket;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer {	
	public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }
        System.out.println("Listening on port 4444...");

        while(true) {
        	Socket socket = null;

            try {
                socket = serverSocket.accept();
            } catch (IOException ex) {
                System.out.println("Can't accept client connection. ");
            }
            System.out.println("Get connected on port 4444");
            
            threadHoldConnection nthread = new threadHoldConnection(socket);
            nthread.start();
        }
        

        //serverSocket.close();
    }
	
	
	static class threadHoldConnection extends Thread {
		private Socket nsocket;
		
		threadHoldConnection(Socket socket){
			nsocket = socket;
			System.out.println("Creating new socket... ");
		}
		
		@Override
		public void run() {
			System.out.println("Running...");
			
	        InputStream in = null;
	        OutputStream out = null;
			
			try {
	            in = nsocket.getInputStream();
	        } catch (IOException ex) {
	            System.out.println("Can't get socket input stream. ");
	        }

	        //read file name first, 
	        DataInputStream dt = new DataInputStream(in);
	        String filename = null;
			try {
				filename = dt.readUTF();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        long filesize = 0;
			try {
				filesize = dt.readLong();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	        
	        
	        try {
	            out = new FileOutputStream("d:\\receive.html");
	            System.out.println("FILE NAME IS: " + filename + " FILE SIZE IS: " + filesize);
	        } catch (FileNotFoundException ex) {
	            System.out.println("File not found. ");
	        }

	        byte[] bytes = new byte[16*1024];

	        int count;
	        try {
				while ((count = in.read(bytes)) > 0) {
				    out.write(bytes, 0, count);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        

	        try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				nsocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			
		}
	}
} 