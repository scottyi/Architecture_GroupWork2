
package server;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A Simple Socket client that connects to a socket server
 * @author  Dizier Romain
 *
 */
public class Client {

    private String hostname;
    private int port;
    Socket socketClient;
    InputStream input;
    OutputStream output;

    public Client(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() throws UnknownHostException, IOException{
        System.out.println("Attempting to connect to "+hostname+":"+port);
        socketClient = new Socket(hostname,port);
        System.out.println("Connection Established");
    }
    
    public void sendImage (String file) throws FileNotFoundException, IOException {
        int i;
        input = new BufferedInputStream (new FileInputStream (file));
        output = new BufferedOutputStream(new DataOutputStream(this.socketClient.getOutputStream()));
        while ((i = input.read()) > -1)
            output.write(i);

        this.closeClient();
    }

    public void readResponse() throws IOException{
        String userInput;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

        System.out.println("Response from server:");
        while ((userInput = stdIn.readLine()) != null) {
            System.out.println(userInput);
        }
    }
    
    public void closeClient() throws IOException {
    	this.input.close();
        this.output.close();
		this.socketClient.close();
	}

    public static void main(String arg[]){
        //Creating a SocketClient object
        Client client = new Client ("localhost",9999);
        try {
            //trying to establish connection to the server
            client.connect();
            //if successful, read response from server
            client.sendImage("/home/romain/Images/Mastodon.jpg");
            //close the connection
            client.closeClient();

        }
        catch (FileNotFoundException e) {
            System.err.println("The given file was not found");
        } 
        catch (UnknownHostException e) {
            System.err.println("Host unknown. Cannot establish connection");
        } 
        catch (IOException e) {
            System.err.println("Cannot establish connection. Server may not be up."+e.getMessage());
        }
    }
}