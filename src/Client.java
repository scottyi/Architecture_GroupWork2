
package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

/**
 * A Simple Socket client that connects to a socket server
 * @author  Dizier Romain
 *
 */
public class Client{

    private String hostname;
    private int port;
    private int difficulty = 0;
    Socket socketClient;
    InputStream input;
    OutputStream output;
    
    public Client(String hostname, int port, int difficulty){
        this.hostname = hostname;
        this.port = port;
        this.difficulty = difficulty;
    }

    public void connect() throws UnknownHostException, IOException{
        System.out.println("Attempting to connect to "+hostname+":"+port);
        socketClient = new Socket(hostname,port);
        System.out.println("Connection Established");
    }
    
    public void sendImage () throws FileNotFoundException, IOException {
    	//Use the outputStream of the socket
    	output = this.socketClient.getOutputStream();
    	
    	//Select the image to send depending on the difficulty
    	if (this.difficulty == 0) {
    		System.out.println("The difficulty was not set properly, please select a value between 1 and 5");
    		System.exit(-1);
    	}
    	else if (this.difficulty > 5 || this.difficulty < 1) {
    		System.out.println("The difficulty was not set properly, please select a value between 1 and 5");
    		System.exit(-1);
    	}
    	else{
    		if (this.difficulty == 1) {
    			//Send the image to the server
    	    	InputStream temp = new FileInputStream("/home/romain/Images/cat.jpg");
    	    	this.send_as_bytes(temp, output);
    	    	//Close the temporary inputStream
    	    	temp.close();
    		}
    		else if (this.difficulty == 2) {
    			//Send the image to the server
    			InputStream temp = new FileInputStream("/home/romain/Images/fish.jpg");
    	    	this.send_as_bytes(temp, output);
    	    	//Close the temporary inputStream
    	    	temp.close();
    		}
    		else if (this.difficulty == 3) {
    			//Send the image to the server
    			InputStream temp = new FileInputStream("/home/romain/Images/Mastodon.jpg");
    	    	this.send_as_bytes(temp, output);
    	    	//Close the temporary inputStream
    	    	temp.close();
    		}
    		else if (this.difficulty == 4) {
    			//Send the image to the server
    			InputStream temp = new FileInputStream("/home/romain/Images/dog.jpg");
    			this.send_as_bytes(temp, output);
    	    	//Close the temporary inputStream
    	    	temp.close();
    		}
    		else {
    			//Send the image to the server
    			InputStream temp = new FileInputStream("/home/romain/Images/bird.jpg");
    	    	this.send_as_bytes(temp, output);
    	    	//Close the temporary inputStream
    	    	temp.close();
    		}
    	}
    }
    
    public byte[] imageToByte (BufferedImage image) throws IOException {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", bas);
        byte[] data = bas.toByteArray();
        return data;
    }

    public void receiveImage(String path, String name) throws IOException {
    	//Use the inputStream of the socket
        input = this.socketClient.getInputStream();
        //Writes the received image
        System.out.println("Response from server ...");
        OutputStream temp = new FileOutputStream(path + name);
        System.out.println("Creation du temp");
        this.send_as_bytes(input, temp);
        System.out.println("... Received !");
        //Close the temporary outputStream
        temp.close();
    }
    
    public void send_as_bytes (InputStream in, OutputStream out) throws IOException {
    	byte buf[] = new byte[1024];
    	int n;
    	while((n=in.read(buf))!=-1) {
    		out.write(buf,0,n);
    		if(n < 1024){
    			break;
    		}
    	}
    	System.out.println("Sorti du while !");
    	out.flush();
    }
    
    public void closeClient() throws IOException {
    	this.input.close();
    	this.output.flush();
        this.output.close();
		this.socketClient.close();
	}

    public static void main(String arg[]) throws InterruptedException{
        //Creating a SocketClient object
        Client client = new Client ("localhost",9996, 3);
        try {
            //trying to establish connection to the server
            client.connect();
            //if successful, send the image
            client.sendImage();
            //receive the transformed image from the server
            client.receiveImage("/home/romain/Bureau/Test/", "Test.jpg");
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