package interwebs;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class Server implements Runnable {

	private static Logger log = Logger.getLogger(Server.class);
	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	private ServerSocket server;
	private List<Client> clients;
	private Client client;
	private Socket socket;
	private Thread thread;
	
	private int port;
	boolean running;
	
	public Server(int port) {
		this.port = port;
		clients = new ArrayList<Client>(0);
	}
	
	public int getPort() {
		return port;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public List<Client> getClients() {
		return clients;
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
				for(Client temp : clients) {
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
			log.info("Server thread started on port " + port);
			while (running) {
				socket = server.accept();
				client = new Client(this, socket);				
				client.start();
				clients.add(client);
				broadcast(socket.getPort(), ">> " + sdf.format(new Date()) + " | User #" + socket.getPort() + " joined the chat room");
			}
		} catch (IllegalArgumentException e) {
			log.error("Port out of range; try another");
		} catch (BindException e) {
			log.error("Port " + port + " bound by another process");			
		} catch (SocketException e) {			
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
	
	public void broadcast(int remotePort, String message) {
		if (clients != null && !clients.isEmpty()) {
			for (Client temp : clients) {
				if(temp.getRemotePort() != remotePort) {
					temp.sendMessage(message);
				}			
			}
		}
	}
	
	public void broadcast(String message) {
		if (clients != null && !clients.isEmpty()) {
			for (Client temp : clients) {
				temp.sendMessage(message);			
			}
		}
	}

}