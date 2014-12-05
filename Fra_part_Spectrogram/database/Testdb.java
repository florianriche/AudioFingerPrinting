package database;

/**
 * 
 * @author Paco
 *
 */
public class Testdb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Cassandra cassandra = new Cassandra();
		cassandra.connectToKeyspace("127.0.0.1", "audiofingerprint");
		AudioDatabase db = new AudioDatabase(cassandra);
		
		//db.createTables(30);
		//db.addFingerprintColumn(5);	
		//db.selectIdsmusicFromFingerprint(fingerprint, 1);
		
		cassandra.closeConnection();
	}

}
