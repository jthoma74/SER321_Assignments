
================
ACTIVITY 1
================




================
ACTIVITY 2
================

If you would like to test the game up to 2 points instead of 5 to win, change the code in Peer.java, line: 335

For each peer on the console: 
gradle runPeer --args "Jem 7000" --console=plain -q
gradle runPeer --args "Kay 7001" --console=plain -q
gradle runPeer --args "Moo 7002" --console=plain -q
[etc...]
The program can handle more than 3 peers


When prompted, enter in one line all the host:port combinations you want to listen to, e.g.
localhost:7001 localhost:7002
localhost:7000 localhost:7002
localhost:7000 localhost:7001
[etc...]
The program can handle more than 3 peers


At the end of the game (after someone has won), everyone switches back to chat-mode and is able to restart a game by hitting "start"
If one peer lags on this, just hit "enter" on their console.

===============
PYTHON LISTENER
===============
The python listener is hard-coded to use port 8000. One of the peers must be on that port for the listener to work

To run the listener, go to directory src/main/python, and run directly from cmd line: "python SocketClient.py"

port = 8000
host = 'localhost'