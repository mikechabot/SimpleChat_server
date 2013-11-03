package interwebs;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

public class Server implements Runnable {

	final static Logger log = Logger.getLogger(Server.class);
	
	private Thread thread;
	private ServerSocket server;
	private Socket socket;
	private Client client;
	private int port;
	boolean running;
	
	public Server(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
	
	public boolean isRunning() {
		return running;
	}
	
    public void start() { 
    	if (thread == null) { 
    		thread = new Thread(this);
    		running = true;
    		thread.start();
        	log.info("Server thread started on port " + port);
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
			log.info("Server stopped on port " + port);
		} catch (IOException e) {
			log.error("Badness occured while stopping the server: ", e);
		}
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(port);
			while (running) {
				socket = server.accept();
				client = new Client(socket);				
				client.start();			
			} 
		} catch (BindException e) {
			log.error("Port " + port + " already bound; choose another.");			
		} catch (SocketException e) {			
            /*
             * Don't need to do anything here. From Java doc:
             *  "Class ServerSocket (close): Closes this socket. Any thread currently 
             *   blocked in accept() will throw a SocketException"
             * 
             */
		} catch (IOException e) {
			log.error("Badness occured while running the server: ", e);
		}
	}
}