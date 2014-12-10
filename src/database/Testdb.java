package database;

import java.util.ArrayList;
import java.util.Random;

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
		int l = 10;
//		db.createTables(l);
		byte[] testBytes =  new byte[50];
		for(int i = 0 ;i<testBytes.length;i++){
			testBytes[i] = Byte.parseByte(""+i);
		}
		for(byte b: testBytes){
			System.out.print(b);
		}
		
		
		ArrayList<byte[]> decompo = Utils.Decomposition(testBytes,l);
		Random R = new Random();
		int id = R.nextInt(1000);
		
		db.insertFingerprint(id,id, decompo,0);
		db.insertMusics(id, "A beautiful title"+id, "kyle minogue", id);
		
		byte[] signature = db.selectFullSignatureofID(id,l);
		System.out.println("Ecriture de la signature résultante: ");
		for(byte b: signature){
			System.out.print(b);
		}
		
		System.out.println("Test du fonctionnement du retrieval process ");
		
//		String result = db.getMatchingSong(testRetrieal, l, 20);
//		System.out.println(result);
//		
		String result = db.getMatchingSong(testBytes, l, 2);
		System.out.println(result);
//		
//		
		

		//db.createTables(30);
		//db.addFingerprintColumn(5);	
		//db.selectIdsmusicFromFingerprint(fingerprint, 1);
		
		cassandra.closeConnection();
	}
	
}//end of class
