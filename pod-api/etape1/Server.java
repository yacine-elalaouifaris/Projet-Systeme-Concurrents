import java.util.concurrent.* ; 
import java.util.ArrayList ; 
import java.rmi.* ; 


public class Server implements Server_itf extends java.rmi.UnicastRemoteObject {
	
	public ConcurrentMap<String><ServerObject> serverobjs_noms ;  //service de nommage a distance sur le serveur .
	public ArrayList<ServerObject> serverobjs;  //liste des serverobjects du serveur .
	public AtomicInteger uniqueID ;
	public ReentrantLock mutex ;
	
	public Server() throws RemoteException {
			serverobjs = new ArrayList<ServerObject>(); 
			uniqueID = new AtomicInteger(0) ; 
			mutex =  new ReentrantLock() ; 
	}
	public int lookup(String name) throws java.rmi.RemoteException {
			mutex.lock(); 
			ServerObject so =  serverobjs_noms.get(name);
			return so.getId() ; 
			mutex.unlock(); 
	}
	public void register(String name, int id) throws java.rmi.RemoteException{
			mutex.lock(); 
			serverobjs_noms.put(name,id) ; 		
			mutex.unlock();
	}	
	public int create(Object o) throws java.rmi.RemoteException{
			 mutex.lock();
			 int id = uniqueID.getAndIncrement(); 
			 ServerObject_itf so = new ServerObject(id,o);
			 serverobj.add(so);
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

		mutex.lock();
        ServerObject so = serverobjs.get(id);
        mutex.unlock();
		so.lock_write(client);
        return object.obj;
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
