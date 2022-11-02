package TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * TCP protocol, using Socket to transfer file.
 * Server Listen, Run this file first.
 *
 * Optional HandShake, Synchronous Protocol
 *
 * Message1: Receive an Long double, indicate File Size first
 * Message2: While loop: receive many small messages, with maximum length @param MAX_BUFFER
 * Message3: between the while loop, server send receipt for previous data, so client can move on to next.
 *
 * Note: client and server keep the file size to be sent to determine if it is finished.
 */
public class Server {

    private static int ServerPort = 2000;
    private static final int MAX_BUFFER = 140;   // large buffer to reduce file loss
    private static final String FilePath = "./TestFileServer/sauron2.jpg";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(ServerPort);
        // blocking here
        Socket s = serverSocket.accept();
        saveFile(new File(FilePath), s);
    }


    public static boolean saveFile(File fileSave, Socket socket) {
        RandomAccessFile file = null;
        DataInputStream in = null;
        DataOutputStream out = null;

        boolean errorOnSave = false;
        try {
            file = new RandomAccessFile(fileSave, "rw");

            file.getChannel().lock(); // concurrency protection. Thread Safe.

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            /** Message 1 **/
            long fileSize = in.readLong();

            byte buffer[] = new byte[MAX_BUFFER];
            int read = 0;

            /** Message 2 **/
            while ((fileSize > 0) && ((read = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1)) {
                file.write(buffer, 0, read);
                fileSize -= read;
                buffer = new byte[MAX_BUFFER];
                /** Message 3 **/
                // send a message back to client to indicate how much data did the server receive.
                out.writeLong(read);
            }

        } catch (FileNotFoundException e1) {
            System.out.println(e1.getMessage());
            return false;
        } catch (IOException e) {
            System.out.println("An error has occurred when saving the file\n\t" + e.getMessage());
            errorOnSave = true;
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    System.out.println(
                            "An error occurred when closing the file " + fileSave.getName() + "\n\t" + e.getMessage());
                    errorOnSave = true;
                }
            }
            if (errorOnSave) {
                if (fileSave.exists()) {
                    fileSave.delete();
                }
            }

        }
        return !errorOnSave;
    }

}