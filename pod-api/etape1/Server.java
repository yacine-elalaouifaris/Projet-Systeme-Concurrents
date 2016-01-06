import java.util.concurrent ; 
public class Server implements Server_itf extends java.rmi.UnicastRemoteObject {
	
	public ConcurrentMap<String><int> sharedobjs_names ; 
	public ConcurrentMap<int><ServerObject> serverobjs ; 
	public AtomicInteger uniqueID = new AtomicInteger(0) ; 
	public int lookup(String name) throws java.rmi.RemoteException {
			return sharedobjs.get(name); 
	}
	public void register(String name, int id) throws java.rmi.RemoteException{
			sharedobjs.put(name,id) ; 		
	}	
	public int create(Object o) throws java.rmi.RemoteException{
			 int id = uniqueID.getAndIncrement(); 
			 ServerObject_itf so = new ServerObject(id,o);
			 serverobjs.put(id , so) ;
			 return id ; 
	}
	public Object lock_read(int id, Client_itf client) throws java.rmi.RemoteException{
				SharedObject so = client.sharedobjects.get(id) ; 
				ServerObject servo = serverobjs.get(id) ; 
				// !!! Synchro a faire // 
				so.lock = ?
	}
	public Object lock_write(int id, Client_itf client) throws java.rmi.RemoteException{
				SharedObject so = client.sharedobjects.get(id) ; 
				ServerObject servo = serverobjs.get(id) ; 
				// !!! Synchro a faire // 
	}
	
	public static void main(){
		Registry registry = LocateRegistry.createRegistry(8080);
		Server_itf serv = new Server(); 
		Naming.rebind("serveur", serv);
	}
	
	
	
}
