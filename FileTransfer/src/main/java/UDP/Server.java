package UDP;

import java.net.*;
import java.io.*;

/**
 * Asynchronous UDP.
 *
 * 1. Send message directly without locating any buffers.
 * 2. Set a buffer to receive data, if buffer size too small, may cause data loss
 *
 *  1. Client Send.
 *  2. Server Receive.
 *  3. Server Send.
 *  4. Client Receive.
 */

public class Server {
    private static final int MAX_BUFFER = 8192;
    private static final int PORT = 2000;

public static void main(String args[]){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(PORT); // create socket at agreed port
            byte[] buffer = new byte[MAX_BUFFER];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                System.out.println("Server waiting to receive data");
                // Block here
                aSocket.receive(request);
                System.out.println("Received Data: " + new String(request.getData()));
                DatagramPacket reply = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());
                aSocket.send(reply);
                }
            } catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            } finally {if(aSocket != null) aSocket.close();}
        }
}