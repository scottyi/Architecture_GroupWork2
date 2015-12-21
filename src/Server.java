package server;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;

/**
* A simple socket server that receives image and send them back modified to the client
* @author Dizier Romain
*/

public class Server{
    
    private ServerSocket serverSocket; //The server socket
    private Socket client; //The client's socket
    private int port; //The port to use
    InputStream input; // The input stream
    OutputStream output; //The output stream
    float[] float_values = { 1, 0, -1, 0, 0, 0, -1, 0, 1 }; //Kernel for the transformation
    
    /**
     * Constructor for the server
     * @param port : an integer representing the port number
     */
    public Server(int port) {
        this.port = port;
    }
    
    /**
     * Creates a socketserver, listen for the clients, receive the image and send it back after applying the kernel
     * @param path : a string representing the path of the file to store the temporary image
     * @throws IOException
     */
    public void start(String path) throws IOException {
        System.out.println("Starting the socket server at port:" + port);
        serverSocket = new ServerSocket(port);
        
        //Listen for clients. Block till one connects
        System.out.println("Waiting for clients...");
        client = serverSocket.accept();
        
        System.out.println("On commence a recevoir l'image");
        //Receive the image
        input = this.client.getInputStream();
        //Store the image
        OutputStream temp_out = new FileOutputStream (path + "Server.jpg");
        this.send_as_bytes(input, temp_out);
        temp_out.close();
        
        //Apply the convolution
        BufferedImage convo = ImageIO.read(new File(path + "Server.jpg"));
        byte[] byte_values = this.imageToByte(convo);
        BufferedImage image_convo = this.apply_convo(byte_values);
        File outputfile = new File(path + "Convolution.jpg");
        ImageIO.write(image_convo, "jpg", outputfile);
        
        //Send the image back to the client
        InputStream temp_in = new FileInputStream (path + "Convolution.jpg");
        output = this.client.getOutputStream();
        System.out.println("Debut de l'ecriture");
        this.send_as_bytes(temp_in, output);

        //Delete the temporary image
        File server = new File(path + "Server.jpg");
        server.delete();
        outputfile.delete();
        
        //Close the socket and the streams
        System.out.println("Fin de l'ecriture");
        temp_in.close();
        input.close();
        output.flush();
        output.close();
        System.out.println("Flush");
    }
	
    /**
     * Apply the kernel to the given byte array representing the image
     * @param data : a byte array representing the image that is not null
     * @return : a bufferedimage representing the new image
     * @throws IOException
     */
	public BufferedImage apply_convo(byte[] data) throws IOException {
		BufferedImageOp edge = new ConvolveOp(new Kernel(3, 3, float_values));
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
		BufferedImage img_modif = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		edge.filter(img, img_modif);
		return img_modif;
	}
	
	/**
     * Transform a BufferedImage to a byte array
     * @param image : a bufferedimage that is not null
     * @return : the array of bytes representing the image
     * @throws IOException
     */
	public byte[] imageToByte (BufferedImage img) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", bos);
		bos.flush();
		return bos.toByteArray();
	}
	
	/**
     * Send the object(s) on the given inputstream to the given outputstream (by packets of 1024 bytes)
     * @param in : an inputstream that is not null
     * @param out : an outputstream that is not null
     * @throws IOException
     */
    public void send_as_bytes (InputStream in, OutputStream out) throws IOException {
    	//Creates an array of bytes of size 1024 (= 1 kB)
    	byte buf[] = new byte[1024];
    	int n;
    	//While there is bytes emitted on the inputstream, fill the array with 1024 of them and send it to the server
    	while((n=in.read(buf))!=-1) {
    		out.write(buf,0,n);
    		//If the array contains less than 1024 bytes it means that this is the last packet
    		if(n < 1024){
    			break;
    		}
    	}
    	out.flush();
    }
    
    /**
     * Close all the streams and the socket
     * @throws IOException
     */
    public void closeServer() throws IOException {
    	this.output.flush();
    	this.output.close();
    	this.input.close();
		this.serverSocket.close();
		this.client.close();
	}
    
    
    /**
    * Creates a SocketServer object and starts the server.
    * @param args
    */
    public static void main(String[] args) {
        //Setting a default port number.
        int portNumber = 9996;
        
        try {
            //initializing the Socket Server
            Server socketServer = new Server(portNumber);
            //start the server, receive and store the image, then send the new image to the client;
            socketServer.start("/home/romain/Bureau/Test/");
            //close the server
            socketServer.closeServer();
            } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}



