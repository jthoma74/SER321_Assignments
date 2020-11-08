The TCP program is the only one I was able to implement.
It uses a Message object with headers and a payload to carry the message packet to and from the server

The program can be run on the server side using:
gradle runServer -Pnumber=4444

On the client side: 
gradle runClient -Phost=localhost -Pnumber=4444

Protocol Header contains:
An Answer Flag to indicate if an answer was correct.
An integer to indicate the State of the system: 
First Qn Sent
Qn Sent
Server Sent
Answer Sent

Payload: 
- string to carry messages to/from the client (That contains questions or answers)
- A byte array that contains the encoded image from the server to the client (where it is parsed)

The program is designed to be robust: through error handling, and the program occurs in correct sequential order:
1. Client initiates a new game 
2. Client is prompted to enter the number of tiles
3. Client sends dimension to Server.
4. Server randomly selects between 3 rebus puzzles and cuts it up into appropriate number of pieces
5. The first question is sent to the client
6. For every question the client answers correctly, the next question is sent together with an image.

