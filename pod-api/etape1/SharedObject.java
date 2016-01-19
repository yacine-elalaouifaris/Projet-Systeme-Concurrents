import java.io.*;
import java.util.concurrent.*;

public class SharedObject implements Serializable, SharedObject_itf  {
	
	public  int id ; 
	public  Object obj = null ; 
	public 	ReentrantLock mutex = new ReentrantLock() ;
	public  Condition 
	public  enum lock {NL,RLC,WLC,RLT,WLT,RLT_WLC} ;
	public lock state ; 

	public SharedObject(int id , Object obj ){
		this.id = id ; 
		this.obj = obj ; 
		this.state = lock.NL;
	}
	public void lock_read() {
			Object o = null ; 
			if(this.state = lock.NL ){
				mutex.lock();
				o = Client.lock_read(this.id); 
				mutex.unlock();
				this.obj = o ; 
				this.state = lock.RLT ;
			}
		
			if(this.state == lock.RLC ){ 
			// read lock en cache , pas besoin de propager 
	
			this.state = lock.RLT ; 
			
			}
			
			if(this.state = lock.WLC ){ 
			// write lock en cache , pas besoin de propager 
			
			this.state = lock.RLT_WLC ; 
	
			}
		
	}

	// invoked by the user program on the client node
	public void lock_write() {
			Object o = null ; 
			if(this.state == lock.NL || this.state == lock.RLC || this.state = lock.RLT ){
				// NL ou RLC :  Propagation au serveur 
				mutex.lock() ;
				o  =  Client.lock_write(this.id) ; 
				mutex.unlock();
				this.obj = o ; 
				this.state = lock.RLT;
			}
		
			if(this.state == lock.WLC){ 
			// write lock en cache , pas besoin de propager 
			this.state = lock.WLT; 
			}
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
			mutex.lock();
			if(state == lock.WLT || state == lock.WLC ) {
				//WLT ou RLT-WLC ---> WLC
				state = lock.WLC ; 
				 notify() ; 
			}
			if(state == lock.RLT){
				//RLT ---> RLC 
				state = lock.RLC ;
				notify();
			}
			mutex.unlock();
	}
	


	// callback invoked remotely by the server
	public synchronized Object reduce_lock()  {
			mutex.lock()
			if(this.state == lock.WLC){
					this.state = lock.RLC ; 
			}
			if(this.state = lock.RLT_WLC){
					this.state = lock.RLT ;
			}
			return this.Object ; 
			mutex.unlock()
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		mutex.lock(); 
			this.state = lock.NL ;
			Object o = this.obj ;
			this.obj = null ; 
		mutex.unlock();
		return o ; 
		
	}

	public synchronized Object invalidate_writer() {
		mutex.lock(); 
			this.state = lock.NL ;
			Object o = this.obj ;
			this.obj = null ; 
		mutex.unlock();
		return o ; 
		
	}
	

}
