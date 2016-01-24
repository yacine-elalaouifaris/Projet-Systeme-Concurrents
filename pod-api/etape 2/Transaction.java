import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Transaction {
	
	static Transaction current ;
	public HashMap<Integer ,SharedObject> initial_states ;
	
	public Transaction(){
		this.initial_states = new HashMap<Integer,SharedObject>();
	}	
	
	public static Transaction getCurrentTransaction() {
		return Transaction.current;
	}
    

	// indique si l'appelant est en mode transactionnel
	public boolean isActive() {
		return(current != null && current.equals(this));
	}
	
	// demarre une transaction (passe en mode transactionnel)
	public void start() {
		current = this ; 
	}
	
	// termine une transaction et passe en mode non transactionnel
	public boolean commit(){
			HashMap<Integer,SharedObject> final_states = new HashMap<Integer,SharedObject>();
			boolean transaction_validee = true ; 
			//On Enregistre les états finaux des objects dans une liste (vérification de la cohérence
			for( SharedObject so : Client.sharedobjects.values()){
				if(initial_states.containsKey(so.getId())){
					final_states.put(so.getId(), so);
					so.unlock();
				}
			}
			//validation de la Transaction
			for(SharedObject so : Client.sharedobjects.values() ){
				if(initial_states.containsKey(so.getId())){
					if(!final_states.get(so.getId()).getObj().equals(so.getObj())){
						transaction_validee  = false ;
					}
				}
			}
			//annulation de la Transaction si elle n'est pas validee
			for(SharedObject so : Client.sharedobjects.values()){
				if(initial_states.containsKey(so.getId())){
					so = initial_states.get(so.getId());		
				}
			}
			//passer en mode non transactionnel 
			current = null ;
			initial_states = new HashMap<Integer,SharedObject>();
			return transaction_validee;
	}
		
	// abandonne et annule une transaction (et passe en mode non transactionnel)
	public void abort(){
		for(SharedObject so : Client.sharedobjects.values()){
			if(initial_states.containsKey(so.getId())){
				so.unlock();
				so = initial_states.get(so.getId());
			}
		}
		//passer en mode non transactionnel 
		current = null ;
		initial_states = new HashMap<Integer,SharedObject>();
	}
}