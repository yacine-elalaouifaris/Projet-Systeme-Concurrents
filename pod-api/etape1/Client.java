import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {
	
	public ConcurrentMap<int><SharedObject>  sharedobjects;
	public ArrayList<SharedObject>    liste_ecriture ; 
	public ArrayList<SharedObject>    liste_lecture ; 
	public ServerObject_itf serveur ;  //reference au serveur de  nommage 
	
	
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
			mutex.lock();
			serveur = (Server) Naming.lookup("//localhost/SharedObjects");
			SharedObject so = null;
			int id = serveur.lookup(name) ;
			mutex.unlock(); 
			if(sharedobjects.containsKey(id){
				so = sharedobjects.get(id);
			}
			else{
				Object o = serveur.serverobjs.get(id).obj;
				so = new SharedObject(id , o):
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
			serveur.register(name , so ); 		
		}catch(Exception e ) {
			e.printStackTrace() ; 
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
			try {
			int id = this.server.create(Object o);
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
			liste_lecture.put(this.sharedobjects.get(id));
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
			liste_ecriture.put(this.sharedobjects.get(id)) ; 
			return serv.lock_write(id , this) ; 
		 }catch(Exception e){
			e.printStackTrace();
		 }
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
			SharedObject so = this.sharedobjects.get(id);
			return so.reduce_lock();
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
				SharedObject so = sharedobjects.get(id) ;
				so.invalidate_reader() ;
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
				SharedObject so = sharedobjects.get(id);
				so.invalidate_writer();
	}
}
