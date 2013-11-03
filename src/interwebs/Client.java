package interwebs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {

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
        	System.out.println(">> " + thread.getName() + " (" + this.getClass().getSimpleName() +") | Client connected from " + socket.getLocalAddress().getHostName());
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
			System.out.println("\n>> Badness occurred while closing I/O stream: ");
			e.printStackTrace();
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
                    System.out.println(">> " + thread.getName() + " (" + this.getClass().getSimpleName() +") | Room " + socket.getLocalPort() + ", client sent: " + line);
                    printWriter.println("You Said: " + line);
                    printWriter.flush();
                }
            }
			
		} catch (IOException e) {
			System.out.println("\n>> Badness occurred while reading socket stream: ");
			e.printStackTrace();
		}		
	}

}
