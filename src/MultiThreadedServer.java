package server;

import java.net.*;
import java.io.*;
import java.util.*;

public class MultiThreadedServer implements Runnable {
	
	private Socket connection;
	private String TimeStamp;
	private int ID;
	public static void main(String[] args) {
		int port = 19999;
		int count = 0;
		try{
			ServerSocket socket1 = new ServerSocket(port);
			System.out.println("MultipleSocketServer Initialized");
			while (true) {
				Socket connection = socket1.accept();
				Runnable runnable = new MultiThreadedServer(connection, ++count);
				Thread thread = new Thread(runnable);
				thread.start();
			}
		}
		catch (Exception e) {
		
		}
	}
	
	MultiThreadedServer(Socket s, int i) {
	this.connection = s;
	this.ID = i;
	}
	
	public void run() {
		try {
			BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
			InputStreamReader isr = new InputStreamReader(is);
			int character;
			StringBuffer process = new StringBuffer();
			while((character = isr.read()) != 13) {
				process.append((char)character);
			}
			System.out.println(process);
			//need to wait 10 seconds to pretend that we're processing something
			try {
				Thread.sleep(10000);
			}
			catch (Exception e){}
			TimeStamp = new java.util.Date().toString();
			String returnCode = "MultipleSocketServer repsonded at "+ TimeStamp + (char) 13;
			BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
			osw.write(returnCode);
			osw.flush();
		}
		catch (Exception e) {
			System.out.println(e);
		}
		finally {
			try {
				connection.close();
			}
			catch (IOException e){
				System.out.println("Error while closing" + e);
			}
		}
	}
	
}
