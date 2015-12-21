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
import java.net.Socket;
import javax.imageio.ImageIO;

/**
* A worker agent that will process a single request from a user
* @author Dizier Romain
*/


public class Convolutionner implements Runnable{
	
	 private Socket clientSocket = null; //The client socket
	 private int nb; // The worker number
	 float[] float_values = { 1, 0, -1, 0, 0, 0, -1, 0, 1 }; //Kernel for the transformation

	 /**
	  * Constructor for the worker 
	  * @param clientSocket : the socket of the client
	  * @param nb_convo : the number representing the worker
	  */
	 public Convolutionner(Socket clientSocket, int nb_convo) {
		 this.clientSocket = clientSocket;
		 this.nb = nb_convo;
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
		 byte buf[] = new byte[1024]; //Sends data by kB 
		 int n;
		 while((n=in.read(buf))!=-1) {
			 out.write(buf,0,n);
			 if(n < 1024){
				 break;
			 }
		 }
		 out.flush();
	 }
    

	 /**
     * Run the worker, listen for the client, receive the image and send it back after applying the kernel
     * @throws IOException
     */
	 public void run() {
		 try {
			 InputStream input  = clientSocket.getInputStream();
			 OutputStream output = clientSocket.getOutputStream();
	        
			 long time = System.currentTimeMillis();
	        
			 //Store the image
			 OutputStream temp_out = new FileOutputStream ("/home/romain/Bureau/Test/Server" + nb + ".jpg");
			 this.send_as_bytes(input, temp_out);
			 temp_out.close();
	        
			 //Apply the convolution
			 BufferedImage convo = ImageIO.read(new File("/home/romain/Bureau/Test/Server" + nb + ".jpg"));
			 byte[] byte_values = this.imageToByte(convo);
			 BufferedImage image_convo = this.apply_convo(byte_values);
			 File outputfile = new File("/home/romain/Bureau/Test/Convolution" + nb + ".jpg");
			 ImageIO.write(image_convo, "jpg", outputfile);
	        
			 //Send the image back to the client
			 InputStream temp_in = new FileInputStream ("/home/romain/Bureau/Test/Convolution" + nb + ".jpg");
			 System.out.println("Debut de l'ecriture");
			 this.send_as_bytes(temp_in, output);

			 //Delete the temporary image
			 File server = new File("/home/romain/Bureau/Test/Server" + nb + ".jpg");
			 server.delete();
			 outputfile.delete();
	        
			 //Close the socket
			 System.out.println("Fin de l'ecriture");
			 temp_in.close();
			 input.close();
			 output.flush();
			 output.close();
			 System.out.println("Flush");
	        
			 System.out.println("Request processed: " + time);
		 } 
		 catch (IOException e) {
			 e.printStackTrace();
		 }
	}

}
