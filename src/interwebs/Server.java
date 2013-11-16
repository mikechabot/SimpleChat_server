package interwebs;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

public class Server implements Runnable {
	
	private static Logger log = Logger.getLogger(Server.class);
	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	private ServerSocket server;
	private ConcurrentMap<Integer, Client> clients;
	private Client client;
	private Socket socket;
	private Thread thread;
	
	private int port;
	boolean running;
	
	public Server(int port) {
		this.port = port;
		clients = new ConcurrentHashMap<Integer, Client>(0);
	}
	
	public int getPort() {
		return port;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public ConcurrentMap<Integer, Client> getClients() {
		return clients;
	}
	
	public void removeClient(int port) {
		clients.remove(port);
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
			if (!clients.isEmpty()) {
				for(Client temp : clients.values()) {
					temp.stop();
				}
			}
			if (socket != null) {
				socket.close();
			}
			server.close();
			log.info("Server stopped on port " + port);
		} catch (IOException e) {
			log.error("Badness occured while stopping the server: ", e);
		}
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(port);
			running = true;
			log.info("Binding server thread to port " + port);
			while (running) {
				socket = server.accept(); 
				client = new Client(this, socket);				
				client.start();
				clients.put(socket.getPort(), client);
				broadcast(socket.getPort(), ">> " + sdf.format(new Date()) + " | User #" + socket.getPort() + " joined the chat room");
			}
		} catch (IllegalArgumentException e) {
			log.error("Port out of range; try another");
		} catch (BindException e) {
			log.error("Port " + port + " bound by another process");			
		//} catch (SocketException e) {			
            /*
             * Don't need to do anything here. From Java doc:
             *  "Class ServerSocket (close): Closes this socket. Any thread currently 
             *   blocked in accept() will throw a SocketException"
             *                 
             */
		} catch (IOException e) {
			log.error("Badness occured while running the server", e);
		} finally {
			running = false;
		} 
	}
	
	// Send message to all clients, except for the sender
	public void broadcast(int remotePort, String message) {
		if (clients != null && !clients.isEmpty()) {
			for (Client temp : clients.values()) {
				if(temp.getPort() != remotePort) {
					temp.sendMessage(message);
				}			
			}
		}
	}
	
	// Send message to all clients
	public void broadcast(String message) {
		if (clients != null && !clients.isEmpty()) {
			for (Client temp : clients.values()) {
				temp.sendMessage(message);			
			}
		}
	}

}