# DistributedSystem Java
 
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


`ServerClient_JSON`: handshake protocol, Synchronous, Java implementation of Client-Server **text** transfer with JSON.


`TestFileClient`: File for testing purpose.

`TestFileServer`: File for testing purpose.