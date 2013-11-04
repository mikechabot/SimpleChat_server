SimpleChat_server
=================

Multithreaded server-side chat agent that can start up multiple chat rooms (i.e. listen on multiple ports) and respond to multiple, threaded clients.

## Run a server

<ol>
<li>Download server.jar and server.bat</li>
<li>Double-click server.bat</li>
<li>Review and execute usage commands</li>
</ol>

or

<ol>
<li>Download server.jar</li>
<li>Open Command Prompt</li>
<li>Navigate to JAR location</li>
<code>> cd C:\Users\You\Desktop</code>
<li>Run the JAR without arguments to review usage options</li>
<code>> java -jar server.jar </code>
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
<li>Or start a chat server immediately on a specific port</li>
<code>> java -jar server.jar sc start 9090</code>
<pre>
>> Server thread started on port 9090

Type "help" to see SimpleChat usage commands
</pre>
</ol>


