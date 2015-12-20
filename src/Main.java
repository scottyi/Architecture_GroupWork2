package server;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {
	
	public static void main (String[] args){
	
	    // Setting a default port number.
	    int portNumber = 9990;
	    
	    try {
	        // initializing the Socket Server
	        Server socketServer = new Server(portNumber);
	        //socketServer.start();
	    
		    //Creating a SocketClient object
	        Client client = new Client ("localhost",portNumber);
	        //trying to establish connection to the server
	        client.connect();
	        //if successful, read response from server
	        //client.readResponse();

        } 
	    catch (UnknownHostException e) {
            System.err.println("Host unknown. Cannot establish connection");
        } 
	    catch (IOException e) {
            System.err.println("Cannot establish connection. Server may not be up."+e.getMessage());
        }
		
	}

}
