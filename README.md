# DistributedSystem Java

All files here are written in Java and based on Client and Server communication.


All test files are runnable and includes client and server two programs.

## Contents:


`FileTransfer`: 
- `TCP(Synchronous)`: 
  - File transfer, 
  - File IO (read from and write to local file).
  - handshake protocol for file transfer, binary file, file open, file write operations.
  
- `UDP(Asynchronous)`: 
  - String transfer, only transfer text data.
  - Without HandShake Protocol, may result in data loss.
  
- `UDP_File(Asynchronous)`: 
  - File IO (read from and write to local file).
  - Without HandShake Protocol, may result in data loss.

`FileDownload`:
similar to FileTransfer project, but this time it uses Json messages and client asks to download a file
from server.

`ServerClient_JSON`: handshake protocol, Synchronous, Java implementation of Client-Server **text** transfer with JSON.

`Encryption`: Encrypt messages between server and client in TCP synchronous transfer with JSON.

`RMI`: RMI demo for a client and server doing remote math calculations.

`JSON`: Client and Server exchange JSON formatted messages, TCP, synchronous.

`TestFileClient`: File for testing purpose.

`TestFileServer`: File for testing purpose.