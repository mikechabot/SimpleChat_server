package interwebs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class Client implements Runnable {
	
	private static final String GOODBYE_SRVR = "**GOODBYE_FROM_SERVER**";
	private static final String GOODBYE_CLNT = "**GOODBYE_FROM_CLIENT**";
	private static final String PING_CLNT = "**PING_FROM_CLIENT**";
	
	private static Logger log = Logger.getLogger(Client.class);
	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	private Server server;
	private Thread thread;
	private Socket socket;
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
	
	public int getPort() {
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
		console.close();
		toClient.close();
		server.removeClient(getPort());	
    }
    
    public void sendMessage(String message) {
    	toClient.println(message);
    	toClient.flush();
    } 
    
	public void processClientMessage(String message) {
        if(message.equals("/leave")) {
        	sendMessage(">> " + sdf.format(new Date()) + " | You left Room #" + socket.getLocalPort());
        	sendMessage(GOODBYE_SRVR);
        	server.broadcast(socket.getPort(), ">> " + sdf.format(new Date()) + " | User #" + socket.getPort() + " left the room");
        	log.debug("Room " + socket.getLocalPort() + ", User #" + socket.getPort() + ": " + message);
        } else if (message.equals(GOODBYE_CLNT)) {
        	log.info("Room " + socket.getLocalPort() + ", User #" + socket.getPort() + ": " + message);
        	stop();       	
        } else if (message.equals(PING_CLNT)) {
        	// Do nothing, just the client heartbeat
        } else {
            log.info("Room " + socket.getLocalPort() + ", User #" + socket.getPort() + ": " + message);
            server.broadcast(message = ">> " + sdf.format(new Date()) + " | User #" + socket.getPort() + ": " + message);
        }
	}
	
	@Override
	public void run() {
		running = true;
		log.info("Client connected from " + socket.getLocalAddress().getHostName() + " (" +socket.getPort() + "), binding to port " + socket.getLocalPort());
		try {
			console = new Scanner(socket.getInputStream());			// Read text from input stream
			toClient = new PrintWriter(socket.getOutputStream()); 	// Send text back to client using output stream
			sendMessage("\n** Joined Room #" + socket.getLocalPort() + " as User #" + socket.getPort() + " | Room Size: " + server.getClients().size() + " **");
            while (running) {
            	if(console.hasNextLine()) {
            		processClientMessage(console.nextLine());
            	}
            }     
		} catch (IOException e) {
			log.error("\n>> Badness occurred while reading client socket stream: ", e);
		}		
	}

}
