public class ServerObject implements  java.io.Serializable , ServerObject_itf {
	int id ; 
	Object obj = null ; 
	int lockstate; // 0 : no lock 1  : read , 2 : write
	public ArrayList<Client_itf> clients ;
	public Object lock_write(Client_itf c){
		//...
	}
	public Object lock_read(Client_itf c){
		//...
	}
	public ServerObject(int id , Object obj ) {
			this.lockstate = 0 ;
			 this.id = id ; 
			 this.obj = obj ; 
	}
}
