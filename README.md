# Client-Server-Calculator

An application consisting of a server and three client processes. Each client process will connect to the server over a socket connection and register a user name at the server. The server should be able to handle all three clients concurrently and display the names of the connected clients in real time.
Each client will implement a simple four-function calculator. The calculator handles:
• Addition
• Subtraction
• Multiplication
• Division
• Negative numbers
• Decimals to four digits, rounding up
The calculator does not allow the user to input grouping operations. 
Each client will keep a local copy of a shared value and will execute all operations on that local copy. 
Each calculator accepts an unlimited number of operations prior to executing them and commands should be executed according to algebraic order of operations. Any operation resulting in NaNs should be rejected by the client and that sequence of operations should be cleared.
When instructed by the user via a GUI input, the server will poll clients for their executed sequence of operations. Clients will then upload the sequence of operations (not just the final computed value) they have executed locally. The server will order all operations received from clients according to algebraic order of operations and apply those operations to the value stored on the server.

For example, each client may perform the following sequence of operations on its local value.
Client 1              Client 2            Client 3
Initial Value: 1      Initial Value: 1    Initial Value: 1
+ 1                   / 2                 * 3
- 11                  * 22                / 33
* 111                 - 222               + 333
/ 1111                + 2222              - 3333
= 0.9010              = 2011.0000         = -2999.9091

The server would then execute the following sequence of operations on its copy of the value:
<Initial Value: 1> + 1 - 11 * 111 / 1111 / 2 * 22 - 222 + 2222 * 3 / 33 + 333 - 3333
= -3030.0891.
After the server completes its calculations, it should push the new value to each of the clients. The clients should then overwrite their local copy with the value received from the server.
Clients should keep a persistent log of all instructions entered by the user (e.g., the log should survive a client process being killed and restarted). If a client logs user-input operations, but misses a server poll due to being shut down, it should upload the logged operations during the next poll. No user-input should be uploaded more than once. The log need not store user-input operations that have already been uploaded to the server.
