import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SharedObject implements Serializable, SharedObject_itf  {
	
	private  int id ; 
	public  Object obj = null ; //accés direct dans Irc.java ?!
	public 	ReentrantLock mutex  ;
	public Condition writelock_returned ;
	public Condition readlock_returned ; 
	public  enum lock {NL,RLC,WLC,RLT,WLT,RLT_WLC} ;
	public lock state ; 
	

	public SharedObject deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (SharedObject) ois.readObject();
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	public SharedObject(int id , Object obj ){
		this.id = id ; 
		this.obj = obj ; 
		this.state = lock.NL;
		mutex = new ReentrantLock();
		writelock_returned = mutex.newCondition();
		readlock_returned = mutex.newCondition() ;
	}
	public void lock_read() {
			Object o = null ; 
			mutex.lock();
			if(this.state == lock.NL ){
				o = Client.lock_read(this.id); 
				this.obj = o ; 
				this.state = lock.RLT ;
			}
		
			if(this.state == lock.RLC ){ 
			// read lock en cache , pas besoin de propager 
			this.state = lock.RLT ; 
			}
			
			if(this.state == lock.WLC ){ 
			// write lock en cache , pas besoin de propager 
			this.state = lock.RLT_WLC ; 
			}
			mutex.unlock();
	}

	// invoked by the user program on the client node
	public void lock_write() {
			Object o = null ; 
			if(Transaction.getCurrentTransaction() != null){
				Transaction.getCurrentTransaction().initial_states.put(this.getId(), this.deepClone());
			}
			mutex.lock();
			if(this.state == lock.NL || this.state == lock.RLC || this.state == lock.RLT ){
				// NL ou RLC ou :  Propagation au serveur 
				o  =  Client.lock_write(this.id) ; 
				this.obj = o ; 
				this.state = lock.RLT;
			}
		
			if(this.state == lock.WLC){ 
			// write lock en cache , pas beasoin de propager 
			this.state = lock.WLT; 
			}
			mutex.unlock();
			
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
			mutex.lock();
			if(this.state == lock.WLT ) {
				//WLT  ---> WLC
				this.state = lock.WLC ; 
				this.writelock_returned.notify() ; 
			}
			if(this.state == lock.RLT_WLC){
				this.state = lock.WLC;
				this.readlock_returned.notify();
			}
			if(this.state == lock.RLT){
				//RLT ---> RLC 
				this.state = lock.RLC ;
				this.writelock_returned.notify();
			}
			mutex.unlock();
	}
	


	// callback invoked remotely by the server
	public synchronized Object reduce_lock()  {
			mutex.lock();
			if(this.state == lock.WLC){
					this.state = lock.RLC ; 
			}
			if(this.state == lock.RLT_WLC){
					this.state = lock.RLT ;
			}
			Object o = this.obj ;
			mutex.unlock();
			return o;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		while(this.state == lock.RLT || this.state == lock.RLT_WLC){
			try {
				readlock_returned.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mutex.lock(); 
			this.state = lock.NL ;
		mutex.unlock();
	}

	public synchronized Object invalidate_writer() {
		    while(this.state== lock.WLT) {
		    	try {
					writelock_returned.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    }
			mutex.lock(); 
			this.state = lock.NL ;
			Object o = this.obj ;
			this.obj= null ;
			mutex.unlock();
		return o ; 
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
