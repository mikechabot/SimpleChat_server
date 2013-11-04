package interwebs;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

	private static List<Client> clients;
	
	private Thread thread;
	private ServerSocket server;
	private Socket socket;
	private Client client;
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
			if (socket != null) {
				client.stop();
				socket.close();
			}
			server.close();
			clients.remove(client);
			System.out.println(">> Server stopped on port " + port);
		} catch (IOException e) {
			System.out.println("\n>> Badness occured while stopping the server: ");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(port);
			running = true;
			System.out.println(">> Server thread started on port " + port);
			while (running) {
				socket = server.accept();
				client = new Client(this, socket);				
				client.start();
				clients.add(client);
			}
		} catch (IllegalArgumentException e) {
			System.out.println(">> Port out of range; try another");
		} catch (BindException e) {
			System.out.println(">> Port " + port + " bound by another process");			
		} catch (SocketException e) {			
            /*
             * Don't need to do anything here. From Java doc:
             *  "Class ServerSocket (close): Closes this socket. Any thread currently 
             *   blocked in accept() will throw a SocketException"
             *                 
             */
		} catch (IOException e) {
			System.out.println("\n>> Badness occured while running the server\n");
			e.printStackTrace();
		} finally {
			running = false;
		}
	}
	
	public void broadcast(int remotePort, String message) {
		message = ">> User #" + remotePort + ": " + message;
		if (clients != null && !clients.isEmpty()) {
			for (Client temp : clients) {
				if (temp.getRemotePort() != remotePort) {
					temp.sendMessage(message);
				}
			}
		}
	}
	
}