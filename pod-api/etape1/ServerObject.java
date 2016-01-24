import java.rmi.RemoteException;
import java.util.ArrayList;



public class ServerObject implements  java.io.Serializable , ServerObject_itf {
	private int id ; 
	private Object obj = null ; 
	enum lock_serveur  { NL , RLT , WLT } ;
	public lock_serveur state ; 
	public Client_itf writer ;  
	public ArrayList<Client_itf> clients_lockread ;
	
	public Object lock_write(Client_itf c){
			if(writer != null){
				try {
					this.obj = writer.invalidate_writer(this.id);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}	
			  if(clients_lockread.size()!=0){
				  for(Client_itf cl : clients_lockread){
					   try {
						cl.invalidate_reader(this.id);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				  }		
			  }
			writer = c ;
			this.state = lock_serveur.WLT ;			
			return this.obj ; 
	}
	
	public Object lock_read(Client_itf c){
		Object o= null ; 
		if(this.writer == null){
			try {
				o = this.writer.reduce_lock(this.id);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		this.state = lock_serveur.RLT;
		this.obj = o ; 
		this.clients_lockread.add(c);
		return o ;
	}
	
	public ServerObject(int id , Object obj ) {
			this.state = lock_serveur.NL ;
			 this.id = id ; 
			 this.obj = obj ; 
			 this.clients_lockread  = new ArrayList<Client_itf>();
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public int getId() {
		return id;
	}
}
