package interwebs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {

	private Server server;
	private Thread thread;
	private Socket socket;
	private InputStream iStream;
	private OutputStream oStream;
	private Scanner scanner;
	private PrintWriter printWriter;
	private boolean running;
	
	public Client(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public int getRemotePort() {
		return socket.getPort();
	}
	
    public void start() { 
    	if (thread == null) { 
    		thread = new Thread(this); 
        	thread.start();
        	running = true;
        	System.out.println(">> " + thread.getName() + " (" + this.getClass().getSimpleName() +") | Client connected from " + socket.getLocalAddress().getHostName() + " (" +socket.getPort() + "), binding to port " + socket.getLocalPort());
        } 
    } 
    
    public void stop() {
    	running = false;
    	try {
			iStream.close();
			oStream.close();
			scanner.close();
	    	printWriter.close();
		} catch (IOException e) {
			System.out.println("\n>> Badness occurred while closing stream: ");
			e.printStackTrace();
		}
    }
    
    public void sendMessage(String message) {
    	printWriter.println(">> Someone said: " + message);
    	printWriter.flush();
    }
	
	@Override
	public void run() {
		try {
			iStream = socket.getInputStream();		// Read input stream from client
			scanner = new Scanner(iStream);			// Read console text from input stream
			oStream = socket.getOutputStream(); 	// Initiate output stream back to client
			printWriter = new PrintWriter(oStream); // Send text back to client using output stream
			
            while (running) {
            	if(scanner.hasNextLine()) {
                    String message = scanner.nextLine();
                    System.out.println(">> " + thread.getName() + " (" + this.getClass().getSimpleName() +") | Room " + socket.getLocalPort() + ", client sent: " + message);                    
                    server.broadcast(socket.getPort(), message);
                    printWriter.println(">> User #" + socket.getPort() + ": " + message);
                    printWriter.flush();
            	}           
            }
            
		} catch (IOException e) {
			System.out.println("\n>> Badness occurred while reading client socket stream: ");
			e.printStackTrace();
		}		
	}

}
