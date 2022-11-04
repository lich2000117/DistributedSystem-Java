package TCP;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

/**
 * TCP protocol, using Socket to transfer file with JSON message format
 * Server Listen, Run this file first.
 *
 * Optional HandShake, Synchronous Protocol
 *
 * Message1: Receive JSON message, indicate File Size first
 * Message2: While loop: receive many small JSON messages, with maximum length @param MAX_BUFFER for every file content
 * Message3: between the while loop, server send receipt for previous data, so server can move on to next download.
 *
 * Note: client and server keep the file size to be sent to determine if it is finished.
 */
public class Server {

    private static int ServerPort = 4444;
    private static final int MAX_BUFFER = 8192;   // large buffer to reduce file loss

    public static void main(String[] args) throws IOException, ParseException {
        ServerSocket serverSocket = new ServerSocket(ServerPort);

        // Wait for connections.
        while(true){
            // blocking here
            System.out.println("Server listening");
            Socket socket = serverSocket.accept();
            // Start a new thread for a connection, thread run serveClient(client) function in the background
            Thread t = new Thread(() -> process(socket));
            t.start();
        }


    }

    private static void process(Socket socket) {
        JSONParser parser = new JSONParser();
        String file_name = null;

        DataInputStream socket_in = null;
        DataOutputStream out = null;
        try {
            socket_in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error");
        }

        /** Message 0 Receive Download Request and get file name requried. **/
        try {
                // Attempt to convert read data to JSON
                JSONObject command = (JSONObject) parser.parse(socket_in.readUTF());
                System.out.println("COMMAND RECEIVED: " + command.toJSONString());

                // get file name
                if (command.containsKey("command_name")) {
                    System.out.println("IT HAS A COMMAND NAME");
                } else {
                    System.out.println("INVALID MESSAGE!");
                    return;
                }

                if (command.get("command_name").equals("DOWNLOAD")) {
                    file_name = (String) command.get("file_name");
                    System.out.println("File to be downloaded: " + file_name);
                }

        }
        catch (Exception e) {
            System.out.println("Error2");
        }
        if (file_name == null) return;

        // open local file
        try {
            FileInputStream file_in = new FileInputStream(file_name);
            File file = new File(file_name);
            long length = file.length();

            if (file.exists()) {
                try {

                    /** Message 1, send a message indicating total file length with json**/
                    JSONObject out_json = new JSONObject();
                    out_json.put("total_length", length);
                    out.writeUTF(out_json.toJSONString());
                    out.flush();
                    byte buffer[] = new byte[MAX_BUFFER];
                    int read = 0;
                    System.out.println("Total Length sent");
                    /** Message 2 read files by blocks and send json messages**/
                    while ((read = file_in.read(buffer)) != -1) {
                        out_json = new JSONObject();
                        out_json.put("block_len", read);
                        out_json.put("content", Base64.getEncoder().encodeToString(buffer));
                        out.writeUTF(out_json.toJSONString());

                        out.flush();
                        buffer = new byte[MAX_BUFFER];

                        /** Message 3 only move on next message if receive confirm from server.**/

                        JSONObject msg = (JSONObject) parser.parse(socket_in.readUTF());
                        System.out.println("COMMAND RECEIVED: " + msg.toJSONString());
                        if (msg.containsKey("SUCCESS")) {
                            if (((Long) msg.get("SUCCESS")).intValue() == read) {
                                System.out.println("SUCCESS upload: " + msg.toJSONString());
                                continue;
                            }
                        }

                    }


                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                    return;
                } catch (IOException e) {
                    System.out.println("An error has occurred when try send file " + file.getName() + " \nSocket: "
                            + socket.getInetAddress() + ":" + socket.getPort() + "\n\t" + e.getMessage());
                } finally {
                    if (file_in != null) {
                        try {
                            file_in.close();
                        } catch (IOException e) {
                            System.out.println("An error has occurred when closing the InputStream of the file "
                                    + file.getName() + "\n\t" + e.getMessage());
                        }
                    }

                }


            } else {
                System.out.println("File does not Exist");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Open file error.");
        }

    }

}