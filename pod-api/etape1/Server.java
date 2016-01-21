import java.util.concurrent.* ;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList ;
import java.util.HashMap;
import java.util.Map;
import java.rmi.* ;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject; 


public class Server  extends UnicastRemoteObject implements Server_itf{
	public Map<String,ServerObject> serverobjs_noms ;  //service de nommage a distance sur le serveur .
	public ArrayList<ServerObject> serverobjs;  //liste des serverobjects du serveur .
	public AtomicInteger uniqueID ;
	public ReentrantLock mutex ;
	
	public Server() throws RemoteException {
			serverobjs_noms = new HashMap<String,ServerObject>();
			serverobjs = new ArrayList<ServerObject>(); 
			uniqueID = new AtomicInteger(0) ; 
			mutex =  new ReentrantLock() ; 
	}
	public int lookup(String name) throws java.rmi.RemoteException {
			mutex.lock(); 
			ServerObject so =  serverobjs_noms.get(name);
			mutex.unlock(); 
			return so.id ; 
	}
	public void register(String name, int id) throws java.rmi.RemoteException{
			mutex.lock(); 
			serverobjs_noms.put(name,serverobjs.get(id)) ; 		
			mutex.unlock();
	}	
	public int create(Object o) throws java.rmi.RemoteException{
		     int id = uniqueID.getAndIncrement(); 
			 mutex.lock();
			 ServerObject so = new ServerObject(id,o);
			 serverobjs.add(so);
			 mutex.unlock();
			 return id ; 
	}
	public Object lock_read(int id, Client_itf client) throws java.rmi.RemoteException{
				mutex.lock(); 
				ServerObject so = serverobjs.get(id); 
				mutex.unlock(); 
				so.lock_read(client); 
				return so.obj ; 
	}
	public Object lock_write(int id, Client_itf client) throws java.rmi.RemoteException{
		ServerObject so =null;
		mutex.lock();
         so = serverobjs.get(id);
        mutex.unlock();
		so.lock_write(client);
        return so.obj;
	}
	
	public static void main(){
		try {
			Registry registry = LocateRegistry.createRegistry(8080);
			Server_itf serv = new Server(); 
			Naming.rebind("//localhost/SharedObjects", serv);
			System.out.println("serveur operationnel : port 8080 ");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
}
