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
* A simple socket server
* @author Dizier Romain
*/
public class Server {
    
    private ServerSocket serverSocket;
    private Socket client;
    private int port;
    InputStream input;
    OutputStream output;
    float[] float_values = { 1, 0, -1, 0, 0, 0, -1, 0, 1 }; //Kernel for the transformation
    byte[] full_image = new byte[1024];
    
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
        //Store the image
        OutputStream temp_out = new FileOutputStream ("/home/romain/Bureau/Test/Server.jpg");
        this.send_as_bytes(input, temp_out);
        temp_out.close();
        
        //Apply the convolution
        BufferedImage convo = ImageIO.read(new File("/home/romain/Bureau/Test/Server.jpg"));
        byte[] byte_values = this.imageToByte(convo);
        BufferedImage image_convo = this.apply_convo(byte_values);
        File outputfile = new File("/home/romain/Bureau/Test/Convolution.jpg");
        ImageIO.write(image_convo, "jpg", outputfile);
        
        //Send the image back to the client
        InputStream temp_in = new FileInputStream ("/home/romain/Bureau/Test/Convolution.jpg");
        output = this.client.getOutputStream();
        System.out.println("Debut de l'ecriture");
        this.send_as_bytes(temp_in, output);

        System.out.println("Fin de l'ecriture");
        temp_in.close();
        input.close();
        output.flush();
        output.close();
        System.out.println("Flush");
    }
	
	public BufferedImage apply_convo(byte[] data) throws IOException {
		BufferedImageOp edge = new ConvolveOp(new Kernel(3, 3, float_values));
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
		BufferedImage img_modif = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		edge.filter(img, img_modif);
		return img_modif;
	}
	
	public byte[] imageToByte (BufferedImage img) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", bos);
		bos.flush();
		return bos.toByteArray();
	}
	
    public byte[] combine(byte[] a, byte[] b){
        int length = a.length + b.length;
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
	
    public void send_as_bytes (InputStream in, OutputStream out) throws IOException {
    	byte buf[] = new byte[1024]; //Sends data by kB 
    	int n;
    	while((n=in.read(buf))!=-1) {
    		
    		// full_image = this.combine(full_image, buf);
    		out.write(buf,0,n);
    		if(n < 1024){
    			break;
    		}
    	}

    	//BufferedImage temp = this.apply_convo(full_image);
    	//ImageIO.write(temp, "jpg",new File("/home/romain/Bureau/Test/Kernel.jpg"));
		//byte[] new_buf = this.imageToByte(temp);
		
		// out.write(new_buf);
    	out.flush();
    }
    
    public void closeServer() throws IOException {
    	this.output.flush();
    	this.output.close();
    	this.input.close();
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



