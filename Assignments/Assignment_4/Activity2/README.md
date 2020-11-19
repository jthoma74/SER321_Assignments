====================
ACTIVITY 2: The game
====================

If you would like to test the game up to 2 points instead of 5 to win, change the code in Peer.java, line: 335

For each peer on the console: 
gradle runPeer --args "Jem 8000" --console=plain -q
gradle runPeer --args "Kay 8001" --console=plain -q
gradle runPeer --args "Moo 8002" --console=plain -q
[etc...]
The program can handle more than 3 peers


When prompted, enter in one line all the host:port combinations you want to listen to, e.g.
localhost:8001 localhost:8002
localhost:8000 localhost:8002
localhost:8000 localhost:8001
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

I would love feedback on why my python code below did not read the int from the argument correctly. My Gradle command ran correctly [I left it in my build.gradle if you want to see] 
I have also tried to pass arguments in directly as "python SocketClient.py localhost 8000" 
It gave me a string to int conversion error.... 

This was the previous version of my code before I hardcoded it in my final submission....

==================================
The python code that did not work:
==================================

import socket
import json
import sys


class SocketClient(object):
    def __init__(self, host, port):
        self.host = host
        self.port = port

    def connect(self):
        try:
            # connect to the server
            serverSock = socket.socket()
            serverSock.connect((self.host, self.port))
            while True:
                dataReceived = serverSock.recv(1024)
                message0 = dataReceived.decode()
                message = message0.replace("'",'"')
                print("Data received from peers: {}".format(message))
                
                #parse json
                parsedMsg = json.loads(message)

                print("{}".format(parsedMsg['username']))
                print("{}".format(parsedMsg['message']))
            
                if (parsedMsg['game_active'] == "NO"):
                    break
        except socket.error:
            print('Failed to create socket')
            sys.exit()          
        finally: 
            serverSock.close()


if __name__ == "__main__":
    if len(sys.argv) != 3:
            raise ValueError("Expected arguments: <host(String)> <port(int)>")
    print(sys.argv)
    _, host, port = sys.argv  # host, port,
    socketClient = SocketClient(host, int(port))
    socketClient.connect()

