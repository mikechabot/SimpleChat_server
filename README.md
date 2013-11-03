SimpleChat_server
=================

Multithreaded server-side chat agent that can start up multiple chat rooms (i.e. listen on multiple ports) and respond to multiple, threaded clients.

## Run a server

<ol>
<li>Download server.jar</li>
<li>Open Command Prompt</li>
<li>Navigate to JAR location</li>
<li>Start a chat server on port 9090: </li>
<code>java -jar server.jar sc start 9090</code>
</ol>

## Usage
<pre>
Usage: sc [command] [port] [-l] [-x]
Options: 
	[command]	Issue a command to start/stop a server
	[port]		Port number of the server to be stopped or started
	-l			List running servers
	-x			Exit SimpleChat
Examples: 
	sc start 9090
	sc -l
Tips:
	Type \"help\" to display to usage options
	Use \"sc -x\" when exiting to ensure sockets are closed appropriately.
</pre>
