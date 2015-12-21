package server;

import java.net.*;
import java.io.*;

public class MultiThreadedServer implements Runnable {
	
    private int serverPort;
    private ServerSocket serverSocket;
    private boolean isStopped = false;
    private Thread runningThread = null;
    private int number_of_convo = 0;
	
	public MultiThreadedServer(int port) {
		this.serverPort = port;
	}
	
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
        	System.out.println("Cannot open port");
            System.exit(-1);
        }
    }
    
    private synchronized void incrementConvo() {
    	this.number_of_convo++;
    }
    
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } 
        catch (IOException e) {
            System.out.println("Error closing server");
            System.exit(-1);
        }
    }
	
	public void run() {
		synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } 
            catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                System.out.println("Error accepting client connection");
                System.exit(-1);
            }
            new Thread(new Convolutionner(clientSocket, this.number_of_convo)).start();
            incrementConvo();
        }
        System.out.println("Server Stopped.") ;

	}
	
	public static void main(String[] args) {
		int port = 9996;
		MultiThreadedServer serverSocket = new MultiThreadedServer(port);
		new Thread(serverSocket).start();
	}
	
}
