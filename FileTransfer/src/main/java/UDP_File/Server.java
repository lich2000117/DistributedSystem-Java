package UDP_File;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Asynchronous UDP.
 *
 * 1. Read Local File
 * 2. Client Send datagram continuously, based on buffer size, larger buffer size results in smaller iterations.
 * 3. Server Receive and write to local.
 */

public class Server {
    private static final int MAX_BUFFER = 8192; // large buffer to reduce file loss
    private static final int PORT = 2000;
    private static final String FilePath = "./TestFileServer/r";

public static void main(String args[]){
        DatagramSocket aSocket = null;
        try {

            aSocket = new DatagramSocket(PORT); // create socket at agreed port

            // receive datagram continuously from client.
            byte[] buffer = new byte[MAX_BUFFER];
            int count = 0;
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                // block here
                System.out.println("Server waiting to receive another DataGram: " + count);
                aSocket.receive(request);
                // only write file if receive data.
                RandomAccessFile file = new RandomAccessFile(FilePath, "rw");
                file.getChannel().lock(); // concurrency protection. Thread Safe.
                file.write(request.getData(),0,request.getLength());
                file.close(); // close file after receiving data.
                count += 1;
            }

            } catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            } finally {if(aSocket != null) aSocket.close();}
        }
}