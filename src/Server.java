package server;

import java.io.BufferedWriter;
import java.io.IOException;
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
    
    public Server(int port) {
        this.port = port;
    }
    
    public void start() throws IOException {
        System.out.println("Starting the socket server at port:" + port);
        serverSocket = new ServerSocket(port);
        
        //Listen for clients. Block till one connects
        
        System.out.println("Waiting for clients...");
        client = serverSocket.accept();
        
        //A client has connected to this server. Send welcome message
        sendWelcomeMessage(client);
    }
    
    private void sendWelcomeMessage(Socket client) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        writer.write("Hello. You are connected to a Simple Socket Server. What is your name?");
        writer.flush();
        writer.close();
    }
    
    public void closeServer() throws IOException {
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
            socketServer.start();
            
            } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}



