package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Synchronous TCP socket connections with UTF encoding.
 * NOTE:
 * 		Uses UTF rather than binary byte[], byte array. Byte can transfer images, files.
 * 		UTF is text encoded, can only transfer text data.
 *
 * 	BASE 64:
 * 	1. convert text data using ASCII. a=97 for example
 * 	2. convert it into 8 bit binary representation. 01010110
 *	3. separate it into 6 bit group. 010101 10....
 *  4. 6 bit to decimal - > 24 22 ....
 *  5. using BASE64 Chart (just like reversed ASCII) to get Character -> for example, 24=Y ....
 *  6. send that Character string. YTSXA.....
 *
 *
 * 1. Exchange handshake hello message.
 * 2. Client send/ Server receive Json file encoded using UTF.
 * 3. Server send a reply back
 */

public class Server {
	
	// Declare the port number
	private static int port = 3002;
	
	// Identifies the user number connected
	private static int counter = 0;

	public static void main(String[] args)
	{
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		try(ServerSocket server = factory.createServerSocket(port)){
			System.out.println("Waiting for client connection..");
			
			// Wait for connections.
			while(true){
				// block here waiting for connection
				Socket client = server.accept();
				counter++;
				System.out.println("Client "+counter+": Applying for connection!");
				
				
				// Start a new thread for a connection, thread run serveClient(client) function in the background
				Thread t = new Thread(() -> serveClient(client));
				t.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
	private static void serveClient(Socket client)
	{
		try(Socket clientSocket = client)
		{
			
			// The JSON Parser
			JSONParser parser = new JSONParser();
			// Input stream
			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
			// Output Stream
		    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

			/** 1. HandShake Message (exchange for both client and server) **/
		    System.out.println("CLIENT: "+input.readUTF());
		    output.writeUTF("Server: Hi Client "+counter+" !!!");
		    
		    // Receive more data.. Remove While and if, otherwise it is infinity loop.
		    while(true){
		    	if(input.available() > 0){
					/** 2. Receive Json **/
		    		// Attempt to convert read data to JSON
		    		JSONObject command = (JSONObject) parser.parse(input.readUTF());
		    		System.out.println("COMMAND RECEIVED: "+command.toJSONString());
		    		Integer result = parseCommand(command);
		    		JSONObject results = new JSONObject();
					/** 2. Send Json **/
		    		results.put("result", result);
		    		output.writeUTF(results.toJSONString());
		    	}
				else{
					//return;
				}
		    }
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	private static Integer parseCommand(JSONObject command) {
		
		int result = 0;
		
		if(command.containsKey("command_name")){
			System.out.println("IT HAS A COMMAND NAME");
		}
		else{
			//TODO Invalid Message
		}
		
		if(command.get("command_name").equals("Math"))
		{
			Math math = new Math();
			Integer firstInt = Integer.parseInt(command.get("first_integer").toString());
			Integer secondInt = Integer.parseInt(command.get("second_integer").toString());
			
			switch((String) command.get("method_name"))
			{
				case "add":
					result = math.add(firstInt,secondInt);
					break;
				case "multiply":
					result = math.multiply(firstInt,secondInt);
					break;
				case "subtract":
					result = math.subtract(firstInt,secondInt);
					break;
				default:
					try 
					{
						throw new Exception();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		// TODO Auto-generated method stub
		return result;
	}

}
