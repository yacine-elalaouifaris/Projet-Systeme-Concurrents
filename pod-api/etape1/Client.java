import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {
	public ConcurrentMap<int><SharedObject>  sharedobjects;
	
	public Client() throws RemoteException {
		super();
		sharedobjects = new ConcurrentMap<int><SharedObject>(); 
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		try {
			Server_itf serv = (Server) Naming.lookup("serveur");
			return new SharedObject(serv.lookup(name),null)  ; 
		 }catch(Exception e){
			e.printStackTrace();
		 }
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
					
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
			try {
			Server_itf serv = (Server) Naming.lookup("serveur");
			int id = serv.create(Object o);
			so = new SharedObject(id , o) ;  
			return so;
		 }catch(Exception e){
			e.printStackTrace();
		 }
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		// Appel a distance de la méthode  lock_read(int id, Client_itf client) du Serveur 
		try {
			Server_itf serv = (Server) Naming.lookup("serveur");
			return serv.lock_read(id , this) ; 
		 }catch(Exception e){
			e.printStackTrace();
		 }
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		// Appel a distance de la méthode  lock_write(int id, Client_itf client) du Serveur 
		try {
			Server_itf serv = (Server) Naming.lookup("serveur");
			return serv.lock_write(id , this) ; 
		 }catch(Exception e){
			e.printStackTrace();
		 }
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
	}
}
