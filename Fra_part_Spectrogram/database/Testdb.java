package database;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.InvalidQueryException;

public class Testdb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Cassandra cassandra = new Cassandra();
		cassandra.connectToKeyspace("127.0.0.1", "audiofingerprint");
		AudioDatabase db = new AudioDatabase(cassandra);
		
		//db.createTables(5);
		//db.insertMusics(1, "tructitle", "batman", 148);
		//db.insertOneFingerprint(1, 1, "AAAAAAAADGDHEjGHEJGZ64573", 2);
		//db.insertOneFingerprint(4, 4, "AAAAAAAADGDHEjGHEJGZ64573", 1);
		db.selectIdsmusicFromFingerprint("AAAAAAAADGDHEjGHEJGZ64573",5);
		//db.selectSongFromMusics(1);
		
		cassandra.closeConnection();
	}

}
