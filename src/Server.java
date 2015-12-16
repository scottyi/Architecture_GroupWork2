package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
* A simple socket server
* @author Dizier Romain
*/
public class Server {
    
    private ServerSocket serverSocket;
    private Socket client;
    private int port;
    InputStream input;
    OutputStream output;
    
    public Server(int port) {
        this.port = port;
    }
    
    public void start(String path, String name) throws IOException {
        System.out.println("Starting the socket server at port:" + port);
        serverSocket = new ServerSocket(port);
        
        //Listen for clients. Block till one connects
        
        System.out.println("Waiting for clients...");
        client = serverSocket.accept();
        
        //Receive the image
        input = new BufferedInputStream (new DataInputStream(client.getInputStream()));
        output = new BufferedOutputStream (new FileOutputStream(path + name));
        int i;
        while ( (i = input.read()) > -1) {
            output.write(i);
        }

        this.closeServer();
    }
    
    public void closeServer() throws IOException {
    	output.flush();
    	output.close();
    	input.close();
		this.serverSocket.close();
		this.client.close();
	}
    
    /**
    * Creates a SocketServer object and starts the server.
    *
    * @param args
    */
    public static void main(String[] args) {
        // Setting a default port number.
        int portNumber = 9999;
        
        try {
            // initializing the Socket Server
            Server socketServer = new Server(portNumber);
            socketServer.start("/home/romain/Bureau/", "Recu.jpg");
            
            } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}



