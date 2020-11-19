import socket
import json
import sys

port = 8000
host = 'localhost'


# create client socket using socket stream
myClient = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# connect to server socket
try: 
    myClient.connect((host, port))
    while True:
        dataReceived = myClient.recv(1024)
        message0 = dataReceived.decode()
        message = message0.replace("'",'"')
        print("Data received from peers: {}".format(message))
        
        #parse json
        parsedMsg = json.loads(message)

        print("{}".format(parsedMsg['username']))
        print("{}".format(parsedMsg['message']))
    
        if (parsedMsg['game_active'] == "NO" and parsedMsg['message_type'] == "RESULT"):
            break
finally: 
    myClient.close()


