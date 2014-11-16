package database;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

public class Testdb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Cassandra cassandra = new Cassandra();
		cassandra.connectToKeyspace("127.0.0.1", "demo");
		cassandra.executeRequest("INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Paco', 24, 'Rennes', 'acpo@example.com', 'paco')");
		ResultSet res = cassandra.getResultRequest("select * from users;");
		for (Row row : res) {
			System.out.println(row.getString("lastname")+" "+row.getInt("age")+" "+row.getString("city"));		 
		}
		cassandra.closeConnection();
	}

}
