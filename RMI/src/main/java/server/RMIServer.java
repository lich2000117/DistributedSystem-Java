package server;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import remote.IMath;

/**
 * Creates an instance of the MathRMI class and
 * publishes it in the rmiregistry
 * 
 */
public class RMIServer {
	private static int ServerPort = 2000;
	public static void main(String[] args)  {
		
		try {
			
			//Export the remote math object to the Java RMI runtime, Because MathRMI extends UnicastRemoteObject, this is already done automatically
		    //MathRMI obj = new MathRMI();
			//IMath stub = (IMath) UnicastRemoteObject.exportObject(obj, 0);

			IMath remoteMath = new MathRMI();
			//System.setProperty("java.rmi.server.hostname",self_IP); // set this up if RMI is used in LAN
            Registry registry = LocateRegistry.createRegistry(ServerPort); // open this port for RMI listening.
			//Publish the remote object's stub in the registry under the name "Compute"
            registry.bind("MathCompute", remoteMath);
            
            System.out.println("Math server is ready ... Waiting for Connections");
            
            //use this line to terminate the server:
            //UnicastRemoteObject.unexportObject(remoteMath, false);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
