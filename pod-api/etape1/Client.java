import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {
	
	public static ConcurrentMap<Integer,SharedObject>  sharedobjects;
	public static Server_itf serveur ;  //reference au serveur de  nommage 
	public static ReentrantLock mutex ; 
	public static Client_itf  ref ; // référence statique au client (parce qu'on peut pas utiliser "this") 
	public Client() throws RemoteException {
		super();
		mutex = new ReentrantLock(); 
		sharedobjects = new ConcurrentHashMap<Integer,SharedObject>(); ;
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		try {
			ref = new Client();
			serveur = (Server_itf) Naming.lookup("//localhost:8080/SharedObjects");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
        SharedObject so = null ;
		int id ;
        try {
			mutex.lock();
			id = serveur.lookup(name) ;
			mutex.unlock(); 
			if(sharedobjects.containsKey(id)){
				so = sharedobjects.get(id);
			}
			else{
				Object o = serveur.getSObjects().get(id).getObj();
				so = new SharedObject(id , o);
				sharedobjects.put(id,so);
			}
		 }catch(Exception e){
			e.printStackTrace();
		 }
		
		return so ; 
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try{  
			SharedObject so_impl = (SharedObject) so ;
			serveur.register(name , so_impl.getId() ); 		
		}catch(Exception e ) {
			e.printStackTrace() ; 
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		int id = -1;	
			try {
				id = serveur.create(o);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			SharedObject so = new SharedObject(id , o) ;  
			sharedobjects.put(id, so);
			return so;	
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		// Appel a distance de la méthode  lock_read(int id, Client_itf client) du Serveur 
			Object o = null;
			try {
				o = serveur.lock_read(id , ref);
			} catch (RemoteException e) {
				e.printStackTrace();
			} 
			return o;
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		// Appel a distance de la méthode  lock_write(int id, Client_itf client) du Serveur 
		Object o = null ;
		try { 
			o = serveur.lock_write(id , ref) ; 
		 }catch(Exception e){
			e.printStackTrace();
		 }
		 return o ;
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
			SharedObject so = sharedobjects.get(id);
			return so.reduce_lock();
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
				SharedObject so = sharedobjects.get(id) ;
				so.invalidate_reader() ;
				serveur.getSObjects().get(id).clients_lockread.remove(id);
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
				SharedObject so = sharedobjects.get(id);
				serveur.getSObjects().get(id).writer = null;
				return so.invalidate_writer();
	}
}
