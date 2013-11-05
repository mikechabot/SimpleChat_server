package interwebs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class Client implements Runnable {

	private static Logger log = Logger.getLogger(Client.class);
	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	private Server server;
	private Thread thread;
	private Socket socket;
	private InputStream iStream;
	private OutputStream oStream;
	private Scanner console;
	private PrintWriter toClient;
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
        } 
    } 
    
    public void stop() {
    	running = false;
    	try {
			iStream.close();
			oStream.close();
			console.close();
			toClient.close();
		} catch (IOException e) {
			log.error("\n>> Badness occurred while closing stream: ", e);
		}
    }
    
    public void sendMessage(String message) {
    	toClient.println(message);
    	toClient.flush();
    } 
	
	@Override
	public void run() {
		running = true;
		log.info("Client connected from " + socket.getLocalAddress().getHostName() + " (" +socket.getPort() + "), binding to port " + socket.getLocalPort());
		try {
			iStream = socket.getInputStream();		// Read input stream from client
			console = new Scanner(iStream);			// Read text from input stream
			oStream = socket.getOutputStream(); 	// Initiate output stream back to client
			toClient = new PrintWriter(oStream); 	// Send text back to client using output stream
			sendMessage("\n** Joined Room #" + socket.getLocalPort() + " as User #" + socket.getPort() + " | Room Size: " + server.getClients().size() + " **");
            while (running) {
            	if(console.hasNextLine()) {
                    String message = console.nextLine();  
                    log.info("Room " + socket.getLocalPort() + ", User #" + socket.getPort() + ": " + message);
                    server.broadcast(">> " + sdf.format(new Date()) + " | User #" + socket.getPort() + ": " + message);                    
            	}           
            }            
		} catch (IOException e) {
			log.error("\n>> Badness occurred while reading client socket stream: ", e);
		}		
	}

}
