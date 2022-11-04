package TCP;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
public class Client {

    private static String ServerIP = "localhost";
    private static int ServerPort = 4444;
    private static final int MAX_BUFFER = 8192;   // large buffer to reduce file loss
    static String requestFileName = "./sauron.jpg";
    static String saveFileName = "./sauron3.jpg";

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(ServerIP, ServerPort), 9000);

        File fileSave = new File(saveFileName);
        RandomAccessFile file = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        boolean errorOnSave = false;
        try {
            JSONParser parser = new JSONParser();
            file = new RandomAccessFile(fileSave, "rw");

            file.getChannel().lock(); // concurrency protection. Thread Safe.

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            /**Message 0 : tell server which file is needed**/
            JSONObject newCommand = new JSONObject();
            newCommand.put("command_name", "DOWNLOAD");
            newCommand.put("file_name",requestFileName);
            out.writeUTF(newCommand.toJSONString());
            out.flush();


            /** Message 1 read file length**/
            JSONObject command =  (JSONObject) parser.parse(in.readUTF());
            long fileSize = (long) command.get("total_length");
            System.out.println("Sent download request");
            byte buffer[];
            int read;
            System.out.println(fileSize);
            /** Message 2 **/
            while ((fileSize > 0)) {
                command = (JSONObject) parser.parse(in.readUTF());
                System.out.println(command);
                read = ((Long) command.get("block_len")).intValue();
                String msg = (String) command.get("content");
                buffer = Base64.getDecoder().decode(msg.getBytes());
                file.write(buffer, 0, read);
                fileSize -= read;
                buffer = new byte[MAX_BUFFER];
                /** Message 3 send a message back to client to indicate how much data did the server receive.**/
                newCommand = new JSONObject();
                newCommand.put("SUCCESS", read);
                out.writeUTF(newCommand.toJSONString());
                out.flush();
            }






        } catch (FileNotFoundException e1) {
            System.out.println(e1.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("An error has occurred when saving the file\n\t" + e.getMessage());
            errorOnSave = true;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }



        finally {
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
    }


}