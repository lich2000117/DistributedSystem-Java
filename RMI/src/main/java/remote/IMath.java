package remote;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Remote interface - client create an instance of this type. Server share an instance of this type.
 * All throw RemoteException.
 * All parameters and returns Serializable.
 * Only methods here are available remotely.
 */
public interface IMath extends Remote {

	public double add(double a, double b) throws RemoteException;
	
	public double subtract(double a, double b) throws RemoteException;
	
	public double mul(double a, double b) throws RemoteException;
	
	public double div(double a, double b) throws RemoteException;
	
}
