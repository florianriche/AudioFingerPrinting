package database;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

/**
 * 
 * @author Paco
 *
 */
public class Cassandra {
	
	public Cluster cluster;
	public Session session;
	
	/**
	 * Get the result of a request
	 * @param req
	 * @return
	 */
	public ResultSet getResultRequest(String req){
		return session.execute(req);
	}
	
	/**
	 * Get the result of a request
	 * @param b
	 * @return
	 */
	public ResultSet getResultRequest(BoundStatement b){
		return session.execute(b);
	}
	
	/**
	 * Execute a Cassandra request
	 * @param req
	 */
	public void executeRequest(String req){
		session.execute(req);
	}
	
	/**
	 * Execute a Cassandra request (bis)
	 * @param b
	 */
	public void executeRequest(BoundStatement b){
		session.execute(b);
	}
	
	/**
	 * Prepare statement for request
	 * @param s
	 * @return
	 */
	public PreparedStatement prepareRequest(String s){
		return session.prepare(s);	
	}
	
	/**
	 * Close a Cassandra session
	 */
	public void closeConnection(){
		cluster.close();
	}
	
	/**
	 * Connect to a Cassandra keyspace
	 * @param host
	 * @param database
	 * @return
	 */
	public void connectToKeyspace(String host, String database){
		cluster = Cluster.builder().addContactPoint(host).build();
		session = cluster.connect(database);
	}

}//end of class
