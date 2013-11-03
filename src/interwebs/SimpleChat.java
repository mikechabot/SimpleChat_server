package interwebs;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.regexp.RE;

public class SimpleChat {
	
	private static Map<Integer, Server> servers = new HashMap<Integer, Server>(0);
	
	public SimpleChat() {
		//
	}
	
	public SimpleChat(String command, int port) {
		executeCommand(command, port);
	}
	
	public void start() {
		RE regex = new RE("sc (start|stop) ([0-9]+)$");
		Scanner scanner;
		
		if(servers.size() == 0) {
			printUsage();
		} else if (servers.size() == 1) {
			System.out.println("\nType \"help\" to see SimpleChat usage commands");
		}
		
		while(true) {		
			scanner = new Scanner(System.in);
			String input = scanner.nextLine();
			
			try {
				if (regex.match(input)) {
					executeCommand(regex.getParen(1), Integer.parseInt(regex.getParen(2)));
				} else if (input.equalsIgnoreCase("help")) {
					printUsage();
				} else if (input.equalsIgnoreCase("sc -l")) {
					printActiveServers();
				} else if (input.equalsIgnoreCase("sc -x")) {
					stopServers();
					break;
				} else {
					System.out.println(">> Unknown command. Type \"help\" to display to usage options");
				}
			} catch (NumberFormatException e) {
				System.out.println(">> Invalid port number; try another");
			}
			
		}
		scanner.close();
		System.out.println("\nExiting SimpleChat. Thanks for hosting!");
	}
	
	public void executeCommand(String command, int port) {
		
		// Get existing server or create a new one
		Server server = servers.get(port);
		if (server == null) {
			server = new Server(port);
			servers.put(port, server);
		}
		
		// Issue command to server
		if (command.equals("start")) {
			if (!server.isRunning()) {
				server.start();
			} else {
				System.out.println(">> Server already running on port " + port);
			}
		} else if (command.equals("stop")) {
			if (server.isRunning()) {
				server.stop();
				servers.remove(server.getPort());
				server = null;
			} else {
				System.out.println(">> No server running on port " + port);
			}
		} 
	}
	
	public void printUsage() {
		System.out.println("    Usage: sc [command] [port] [-l] [-x]");
		System.out.println("    Options: ");
		System.out.println("	[command]	Issue a command to start/stop a server");
		System.out.println("	[port]		Port number of the server to be stopped or started");
		System.out.println("	-l		List running servers");
		System.out.println("	-x		Exit SimpleChat");
		System.out.println("    Examples: ");
		System.out.println("	sc start 9090");
		System.out.println("	sc -l");
		System.out.println("    Tips:");
		System.out.println("	Type \"help\" to display to usage options");
		System.out.println("	Use \"sc -x\" when exiting to ensure sockets are closed appropriately.");
		System.out.print("\n");
	}
	
	private void printActiveServers() {
		if(servers.size() > 0) {
			System.out.println("\nRunning Servers");
			System.out.println("#\tPort");
			int i = 1;
			for(Server server : servers.values()) {
				if(server.isRunning()) {
					System.out.println(i + "\t" + server.getPort());
				}
				i++;
			}
		} else {
			System.out.println(">> There are no running servers. Type \"sc start [port]\" to start one.\n");
		}
	}
	
	private void stopServers() {
		for(Server server : servers.values()) {
			if(server != null && server.isRunning()) {
				server.stop();
			}
		}
	}
	
	public static void main(String[] args) {
		
		SimpleChat simpleChat = null;
		if (args != null && args.length > 0) {
			simpleChat = new SimpleChat(args[1], Integer.parseInt(args[2]));
			simpleChat.start();
		} else {
			simpleChat = new SimpleChat();
			simpleChat.start();
		}
	}
	
}
