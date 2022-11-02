package UDP_File;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Asynchronous UDP doing File Transfer (not recommended as it is asynchronous and it is no guarantee that it is complete).
 *
 *  1. Read Local File
 *  2. Client Send datagram continuously , larger buffer size results in smaller iterations..
 *  3. Server Receive and write to local.
 */

public class Client {
    private static String ServerIP = "localhost";
    private static int ServerPort = 2000;
    private static final int MAX_BUFFER = 8192;   // large buffer to reduce file loss
    private static final String FilePath = "./TestFileClient/dqw";

    public static void main(String args[]){
        // args[0] is message args[1] is server's hostname
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(); // we don't care which port it binds to
            InetAddress serverAddress = InetAddress.getByName(ServerIP);

            /** Read Local File:**/
            File f = new File(FilePath);
            if (!f.exists()){ System.out.println("File does not exists."); return;}
            FileInputStream in = new FileInputStream(f);

            byte buffer[] = new byte[MAX_BUFFER];
            int read = 0;
            int count = 0;
            // non-blocking, send untill all file read.
            while ((read = in.read(buffer)) != -1) {
                DatagramPacket request = new DatagramPacket(buffer, read, serverAddress, ServerPort);
                System.out.println("Sending data: "+read);
                aSocket.send(request);
                count += 1;
            }

            System.out.println("Client finished sending data, total count: " + count);
        } catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e){
            System.out.println("IO: " + e.getMessage());
        } finally {if(aSocket != null) aSocket.close();}
    }
}