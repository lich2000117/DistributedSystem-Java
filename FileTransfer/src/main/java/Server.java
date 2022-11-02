import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP protocol, using Socket to transfer file.
 * Server Listen, Run this file first.
 *
 * Optional HandShake, Synchronous Protocol
 *
 * Message1: Receive an Long double, indicate File Size first
 * Message2: While loop: receive many small messages, with maximum length @param MAX_BUFFER
 */
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(2000);
        Socket s = serverSocket.accept();
        Server T2 = new Server(s);
        T2.saveFile(new File("./dqw2")); // safe file as dqw2

    }

    private Socket socket;
    private static final int MAX_BUFFER = 8192;

    public Server(Socket socket) {
        this.socket = socket;
    }

    public boolean saveFile(File fileSave) {
        RandomAccessFile file = null;
        DataInputStream in = null;

        boolean errorOnSave = false;
        try {
            file = new RandomAccessFile(fileSave, "rw");

            file.getChannel().lock(); // concurrency protection. Thread Safe.

            in = new DataInputStream(this.socket.getInputStream());
            /** Message 1 **/
            long fileSize = in.readLong();

            byte buffer[] = new byte[MAX_BUFFER];
            int read = 0;

            /** Message 2 **/
            while ((fileSize > 0) && ((read = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1)) {
                file.write(buffer, 0, read);
                fileSize -= read;
                buffer = new byte[MAX_BUFFER];
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