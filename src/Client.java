
package server;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    
    public void sendImage (String name) throws FileNotFoundException, IOException {
    	//BufferedImage imagefile = ImageIO.read(new File("/home/romain/Images/Mastodon.jpg"));
    	//byte[] imagebyte = this.imageToByte(imagefile);
    	
    	//Use the outputStream of the socket
    	output = this.socketClient.getOutputStream();
    	//Send the image to the server
    	InputStream temp = new FileInputStream("/home/romain/Images/Mastodon.jpg");
    	this.send_as_bytes(temp, output);
    	//Close the temporary inputStream
    	temp.close();
    	
    	/*
        int i;
        input = new FileInputStream (name);
        output = this.socketClient.getOutputStream();
        while ((i = input.read()) > -1) {
            output.write(i);
        }
        input.close();
        output.flush();
        */
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
        //this.stdin.close();
        //this.stdout.flush();
        //this.stdout.close();
		this.socketClient.close();
	}

    public static void main(String arg[]) throws InterruptedException{
        //Creating a SocketClient object
        Client client = new Client ("localhost",9996);
        try {
            //trying to establish connection to the server
            client.connect();
            //if successful, send the image
            client.sendImage("/home/romain/Images/Mastodon.jpg");
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