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

public class Client {
    private static String data = "Good";
    private static String ServerIP = "localhost";
    private static int ServerPort = 2000;
    private static final int MAX_BUFFER = 8192;

    public static void main(String args[]){
        // args[0] is message args[1] is server's hostname
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(); // we don't care which port it binds to

            /** Use Command Line:**/
            //byte[] m = args[0].getBytes(); // get the message as a byte array
            //InetAddress serverAddress = InetAddress.getByName(args[1]); // resolve the server's name
//            DatagramPacket request =
//                    new DatagramPacket(m, args[0].length(), serverAddress, ServerPort);
            // System.out.println("Sending data: "+args[0]);
            

            byte[] m = data.getBytes();
            InetAddress serverAddress = InetAddress.getByName(ServerIP);

            DatagramPacket request = new DatagramPacket(m, data.length(), serverAddress, ServerPort);
            System.out.println("Sending data: "+data);
            aSocket.send(request);

            byte[] buffer = new byte[MAX_BUFFER]; // magic number :-S
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            System.out.println("Client waiting to receive a response");
            /* The following line blocks until the server sends a response.
            * If the server fails to send a response then this line blocks
            * until an exception occurs.
            * */
            aSocket.receive(reply);
            System.out.println("Reply: " + new String(reply.getData()));
        } catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e){
            System.out.println("IO: " + e.getMessage());
        } finally {if(aSocket != null) aSocket.close();}
    }
}