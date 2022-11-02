package TCP;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * TCP protocol, using Socket to transfer file.
 *
 * Optional HandShake, Synchronous Protocol
 *
 * Message1: Send an Long double, indicate File Size first
 * Message2: While loop: send many small messages, with maximum length @param MAX_BUFFER
 */
public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 2000), 9000);
        Client T = new Client(socket);
        T.sendFile(new File("./sauron.jpg"));
    }

    private Socket socket;
    private static final int MAX_BUFFER = 8192;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public boolean sendFile(File file) {

        boolean errorOnSave = false;
        long length = file.length();

        if (file.exists()) {

            FileInputStream in = null;
            DataOutputStream out = null;

            try {
                in = new FileInputStream(file);
                out = new DataOutputStream(this.socket.getOutputStream());
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