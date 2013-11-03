package interwebs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class Client implements Runnable {

	final static Logger log = Logger.getLogger(Client.class);
	
	private Thread thread;
	private Socket socket;
	private InputStream iStream;
	private OutputStream oStream;
	private Scanner scanner;
	private PrintWriter printWriter;
	private boolean running;
	
	public Client(Socket socket) {
		this.socket = socket;
	}
	
	public boolean isRunning() {
		return running;
	}
	
    public void start() { 
    	if (thread == null) { 
    		thread = new Thread(this); 
        	thread.start();
        	running = true;
        	log.info("Client connected from " + socket.getLocalAddress().getHostName());
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
			log.error("Badness closing I/O stream: ", e);
		}
    }
	
	@Override
	public void run() {
		try {
			iStream = socket.getInputStream();
			oStream = socket.getOutputStream(); 
			scanner = new Scanner(iStream);
			printWriter = new PrintWriter(oStream);
			
            while (running) {      
                if (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    log.info("Room " + socket.getLocalPort() + " | Client Said: " + line);
                    printWriter.println("You Said: " + line);
                    printWriter.flush();
                }
            }
			
		} catch (IOException e) {
			log.error("Badness while reading socket stream: ", e);
		}
		
	}

}
