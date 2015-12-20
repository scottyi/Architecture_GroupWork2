package server;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

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
        
        System.out.println("On commence a recevoir l'image");
        //Receive the image
        input = this.client.getInputStream(); 
        /*output = new FileOutputStream(path + name); 
        int i;
        while ( (i = input.read()) > -1) {
            output.write(i);
        }
        output.flush();
        output.close();
        */
        /*
        //Apply the kernel to the image (edge  detection)
        float[] float_values = { 1, 0, -1, 0, 0, 0, -1, 0, 1 };
		Kernel kernel = new Kernel(3, 3, float_values);
        BufferedImage new_image = applyKernel(kernel, "/home/romain/Bureau/Recu.jpg");
        File outputfile = new File("new_image.jpg");
        ImageIO.write(new_image, "jpg", outputfile);
        */
        //Send the image back to the client
        int j;
        //input = new FileInputStream (path + name);
        
        output = this.client.getOutputStream();
        System.out.println("Debut de l'ecriture");
        this.send_as_bytes(input, output);
        /*
        while ((j = input.read()) > -1) {
            output.write(j);
        }
        */
        System.out.println("Fin de l'ecriture");
        input.close();
        output.flush();
        output.close(); //Dernier ajout
        System.out.println("Flush");
    }
    
	public static BufferedImage applyKernel(Kernel kernel, String path_image) throws IOException {
		BufferedImageOp edge = new ConvolveOp(kernel);
		BufferedImage img = ImageIO.read(new File(path_image));
		BufferedImage new_img = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		edge.filter(img, new_img);
		return new_img;
	
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
    	out.flush();
    }
    
    public void closeServer() throws IOException {
    	this.output.flush();
    	this.output.close();
    	this.input.close();
    	//this.stdout.flush();
    	//this.stdout.close();
    	//this.stdin.close();
		this.serverSocket.close();
		this.client.close();
	}
    
    
    /**
    * Creates a SocketServer object and starts the server.
    *
    * @param args
    */
    public static void main(String[] args) {
        //Setting a default port number.
        int portNumber = 9996;
        
        try {
            //initializing the Socket Server
            Server socketServer = new Server(portNumber);
            //start the server, receive and store the image, then send the new image to the client;
            socketServer.start("/home/romain/Bureau/", "Recu.jpg");
            //close the server
            socketServer.closeServer();
            } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}



