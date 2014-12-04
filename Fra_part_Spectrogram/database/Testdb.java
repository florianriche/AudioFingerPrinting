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
		cassandra.connectToKeyspace("127.0.0.1", "audiofinger");
		AudioDatabase db = new AudioDatabase(cassandra);
		
		//db.createTables(5);
		//db.addFingerprintColumn(5);
		
		byte[] fingerprint = {1,2,3};
		int id_music = 1;
		int id = 2;
		int indexColumn = 1;
		//db.insertOneFingerprint(id, id_music, fingerprint, indexColumn);		
		db.selectIdsmusicFromFingerprint(fingerprint, 1);
			
		cassandra.closeConnection();
	}

}
