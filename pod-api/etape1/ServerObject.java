public class ServerObject implements  java.io.Serializable , ServerObject_itf {
	int id ; 
	Object obj = null ; 
	enum lock_serveur = { NL , RLT , WLT } ;
	public lock_serveur state ; 
	public ArrayList<Client_itf> clients_verrou ; 
	
	public Object lock_write(Client_itf c){
			Object o = null ; 
			if(clients_verrou.contains(c) ){
				if(lock_serveur == WLT){
					 o = c.invalidate_writer(id);
				}else if(lock_serveur == RLT){
					for(Client_itf cl : clients_verrou ){
						if( cl == c ){
							o = c.invalidate_reader(id) ;
						}
					}
				} 	
			}
			
			return o ; 
	}
	
	public Object lock_read(Client_itf c){
		Object o ; 
		if(clients_verrou.contains(c)){
			for(Client_itf cl : clients_verrou)){
				if(c == cl) c.reduce_lock(id);
			}
		}
		this.state = RLT
	}
	
	public ServerObject(int id , Object obj ) {
			this.lockstate = 0 ;
			 this.id = id ; 
			 this.obj = obj ; 
	}
}
