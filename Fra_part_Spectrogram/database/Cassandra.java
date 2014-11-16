package database;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

public class Cassandra {
	
	public Cluster cluster;
	public Session session;
	
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	public ResultSet getResultRequest(String req){
		return session.execute(req);
	}
	
	/**
	 * 
	 * @param req
	 */
	public void executeRequest(String req){
		session.execute(req);
	}
	
	/**
	 * 
	 */
	public void closeConnection(){
		cluster.close();
	}
	
	/**
	 * 
	 * @param host
	 * @param database
	 * @return
	 */
	public void connectToKeyspace(String host, String database){
		cluster = Cluster.builder().addContactPoint(host).build();
		session = cluster.connect(database);
	}

}//end of class
