import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf  {
	
	int id ; 
	Object obj = null ; 
	int lock; //A voir si ça ne serait pas mieux de faire ça avec des énums ^^ 
	//0 : NL : no local lock
	//1 : RLC : read lock cached (not taken)
	//2 : WLC : write lock cached
	//3 : RLT : read lock taken
	//4 : WLT : write lock taken
	//5 : RLT_WLC : read lock taken and write lock cached
	
	// invoked by the user program on the client node
	public SharedObject(int id , Object obj ){
		this.id = id ; 
		this.obj = obj ; 
		this.lock = 0;
	}
	public void lock_read() {
		
			if(lock == 0 ){
				// Pas de verrou :  Propagation au serveur 
				lock = (int) Client.lock_read(this.id) ; 
			}
		
			if(lock == 1 ){ 
			// read lock en cache , pas besoin de propager 
			lock = 3 ; 
			}
			
			if(lock == 2){ 
			// write lock en cache , pas besoin de propager 
			lock = 5 ; 
			}
		
	}

	// invoked by the user program on the client node
	public void lock_write() {
		
			if(lock == 0 || lock == 1 ){
				// NL ou RLC :  Propagation au serveur 
				lock = (int) Client.lock_write(this.id) ; 
			}
		
			if(lock == 2){ 
			// write lock en cache , pas besoin de propager 
			lock = 4 ; 
			}
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
			if(lock == 4 || lock == 5 ) {
				//WLT ou RLT-WLC ---> WLC
				lock = 2 ; 
				 notify() ; 
			}
			if(lock == 3){
				//RLT ---> RLC 
				lock = 1 ;
				notify();
			}
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock()  {

	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
	}

	public synchronized Object invalidate_writer() {
	}

}
