package server;

import java.net.*;
import java.io.*;

/**
* A multi threaded server that can receive several simultineous images and send them back modified to the clients
* @author Dizier Romain
*/


public class MultiThreadedServer implements Runnable {
	
    private int serverPort; // The port number
    private ServerSocket serverSocket; // The socket server
    private boolean isStopped = false; // A boolean representing the status of the server (running or not)
    private Thread runningThread = null; // The current running thread
    private int number_of_convo = 0; // The number of thread launched since the server started
	
    /**
     * Constructor for the multi threaded server
     * @param port : an integer representing the port number
     */
	public MultiThreadedServer(int port) {
		this.serverPort = port;
	}
	
	/**
	 * Creates the server socket 
	 */
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
        	System.out.println("Cannot open port");
            System.exit(-1);
        }
    }
    
    /**
     * Increment the counter of thread
     */
    private synchronized void incrementConvo() {
    	this.number_of_convo++;
    }
    
    /**
     * Return whether or not the server is running
     * @return : a boolean representing the status of the server
     */
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    /**
     * Stop the server
     */
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
	
    /**
     * Create a worker agent that will process a request from a client
     */
	public void run() {
		synchronized(this){
            this.runningThread = Thread.currentThread(); // Place this thread as the running one
        }
        openServerSocket();
        while(! isStopped()){ // While the server is running
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept(); // Accept all connections from clients
            } 
            catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                System.out.println("Error accepting client connection");
                System.exit(-1);
            }
            new Thread(new Convolutionner(clientSocket, this.number_of_convo)).start(); // Start a worker agent
            incrementConvo(); // Increase the number of workers
        }
        System.out.println("Server Stopped.") ;

	}
	
	/**
	 * Launch the server
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 9996;
		MultiThreadedServer serverSocket = new MultiThreadedServer(port);
		new Thread(serverSocket).start();
	}
	
}
