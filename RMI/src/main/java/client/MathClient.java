package client;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import remote.IMath;

/**
 * This class retrieves a reference to the remote object from the RMI registry. It
 * invokes the methods on the remote object as if it was a local object of the type of the 
 * remote interface.
 *
 */
public class MathClient {

	private static int ServerPort = 2000;
	private static String ServerIP = "localhost";

	public static void main(String[] args) {
		
		try {
			//Connect to the rmiregistry that is running on rmi remote server
			Registry registry = LocateRegistry.getRegistry(ServerIP, ServerPort);
           
			//Retrieve the stub using look up
			IMath remoteMath = (IMath) registry.lookup("MathCompute");
           
			//Call methods on the remote object as if it was a local object
			double addResult = remoteMath.add(5.0, 4.0);
			System.out.println("5.0 + 4.0 = " + addResult);

			double subResult = remoteMath.subtract(5.0, 2.0);
			System.out.println("5.0 - 2.0 = " + subResult);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
