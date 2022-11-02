package TCP;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * TCP protocol, using Socket to transfer file.
 *
 * Optional HandShake, Synchronous Protocol
 *
 * Message1: Send an Long double, indicate File Size first
 * Message2: While loop: send many small messages, with maximum length @param MAX_BUFFER
 * Message3: between the while loop, server send receipt for previous data, so client can move on to next.
 *
 * Note: client and server keep the file size to be sent to determine if it is finished.
 */
public class Client {

    private static String ServerIP = "localhost";
    private static int ServerPort = 2000;
    private static final int MAX_BUFFER = 140;   // large buffer to reduce file loss
    private static final String FilePath = "./TestFileClient/sauron.jpg";

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(ServerIP, ServerPort), 9000);
        sendFile(new File(FilePath), socket);
    }


    public static boolean sendFile(File file, Socket socket) {

        boolean errorOnSave = false;
        long length = file.length();

        if (file.exists()) {

            FileInputStream in = null;
            DataOutputStream out = null;
            DataInputStream serverIn = null;
            try {
                in = new FileInputStream(file);
                out = new DataOutputStream(socket.getOutputStream());
                serverIn = new DataInputStream(socket.getInputStream());

                /** Message 1 **/
                out.writeLong(length);
                out.flush();

                byte buffer[] = new byte[MAX_BUFFER];
                int read = 0;

                /** Message 2 **/
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    out.flush();
                    buffer = new byte[MAX_BUFFER];
                    /** Message 3 **/
                    // only move on next message if receive confirm from server.
                    if (read != serverIn.readLong()){
                        System.out.println("Server Received different message.");
                    };

                }

            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                return false;
            } catch (IOException e) {
                System.out.println("An error has occurred when try send file " + file.getName() + " \nSocket: "
                        + socket.getInetAddress() + ":" + socket.getPort() + "\n\t" + e.getMessage());
                errorOnSave = true;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        System.out.println("An error has occurred when closing the InputStream of the file "
                                + file.getName() + "\n\t" + e.getMessage());
                    }
                }

            }
            return !errorOnSave;
        } else {
            System.out.println("File does not Exist");
            return false;
        }
    }

}